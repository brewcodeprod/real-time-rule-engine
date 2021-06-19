import com.app.engine.service.DQValidate;
import com.test.importproducts.ImportProduct;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.internal.command.CommandFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

public class Main {

    //@Value("${kie.containerId}")
    private static String containerId = "ImportProducts_1.0.1-LATEST";
    //@Value("${kie.server.user}")
    private static String user = "kieserver";
    //@Value("${kie.server.password}")
    private static String password = "kieserver1!";
    //@Value("${kie.server.url}")
    private static String url = "http://localhost:8180/kie-server/services/rest/server";


    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;
    private static String CLASS_NAME = "ImportProduct";

    public static void main(String[] args) {
        List<ImportProduct> prods = new ArrayList<>();
        prods.add(new ImportProduct("1", "Grocery - Milk", "OK", 25.0));
        prods.add(new ImportProduct("2", "Fashion - Trouser", "NOT_OK", 1300.0));
        prods.add(new ImportProduct("3", "Grocery - Wheat", "OK", 425.0));
        prods.add(new ImportProduct("4", "Grocery - Dairy Milk Chocolate", "OK", 100.0));

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(url, user, password, 60000);
        config.setMarshallingFormat(MarshallingFormat.JSON);
        RuleServicesClient client = KieServicesFactory.newKieServicesClient(config).getServicesClient(RuleServicesClient.class);

        List<Command<?>> cmds = new ArrayList<>();
        KieCommands commands = KieServices.Factory.get().getCommands();
        cmds.add(commands.newInsert(prods, CLASS_NAME));
        cmds.add(commands.newFireAllRules());

        BatchExecutionCommand myCommands = CommandFactory.newBatchExecution(cmds);
        ServiceResponse<ExecutionResults> response = client.executeCommandsWithResults(containerId, myCommands);

        if (response.getType() == ServiceResponse.ResponseType.SUCCESS) {
            LOGGER.info("Commands executed with success! Response: ");
            LOGGER.info("{}", response.getResult());
            List<ImportProduct> prodUpdated = (List<ImportProduct>) response.getResult().getValue(CLASS_NAME);
            //sale.setDiscount(saleUpdated.getDiscount());
            LOGGER.info("Response is: {}", response.getMsg());
            LOGGER.info("Output is: {}", prodUpdated.toString());
        } else {
            LOGGER.error("Error executing rules. Message: {}", response.getMsg());
        }

        //KieServices kieServices = KieServices.Factory.get();
        //ReleaseId releaseId = (ReleaseId) kieServices.newReleaseId( "com.test", "ImportProducts", "1.0.1-LATEST" );

    }
}
