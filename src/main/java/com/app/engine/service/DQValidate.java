package com.app.engine.service;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.stream.Collectors;

        import com.test.importproducts.ImportProduct;
        import org.kie.api.KieServices;
        import org.kie.api.command.BatchExecutionCommand;
        import org.kie.api.command.Command;
        import org.kie.api.command.KieCommands;
        import org.kie.api.runtime.ClassObjectFilter;
        import org.kie.api.runtime.ExecutionResults;
        import org.kie.api.runtime.ObjectFilter;
        import org.kie.internal.command.CommandFactory;
        import org.kie.server.api.marshalling.MarshallingFormat;
        import org.kie.server.api.model.ServiceResponse;
        import org.kie.server.client.KieServicesConfiguration;
        import org.kie.server.client.KieServicesFactory;
        import org.kie.server.client.RuleServicesClient;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.stereotype.Service;

@Service
public class DQValidate {

    private static final String PRODUCT_IDENTIFIER = "ImportProduct";
    private static final Logger LOGGER = LoggerFactory.getLogger(DQValidate.class);
    private static String CLASS_NAME = "ImportProduct";
    private Class resultClass;

    @Value("${kie.containerId}")
    private String containerId;
    @Value("${kie.server.user}")
    private String user;
    @Value("${kie.server.password}")
    private String password;
    @Value("${kie.server.url}")
    private String url;
    @Value("${kie.server.session}")
    private String mySession;

    public void setResultClass(Class resultClass) {
        this.resultClass = resultClass;
    }

    public void applyRules(List<ImportProduct> facts) {
        //String outIdentifier = "out";
        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(url, user, password, 60000);
        config.setMarshallingFormat(MarshallingFormat.JSON);

        RuleServicesClient client = KieServicesFactory.newKieServicesClient(config).getServicesClient(RuleServicesClient.class);
        //BatchExecutionCommand batchExecutionCommand = batchCommand(facts);
        Command<?> batchExecutionCommand = prepareCommands(facts, mySession, CLASS_NAME);
        ServiceResponse<ExecutionResults> response = client.executeCommandsWithResults(containerId, batchExecutionCommand);
        LOGGER.info("{}", response.getMsg());

        if (response.getType() == ServiceResponse.ResponseType.SUCCESS) {
            LOGGER.info("Commands executed with success! Response: ");
            LOGGER.info("{}", response.getResult());
            List<ImportProduct> prodUpdated = (List<ImportProduct>) response.getResult().getValue(PRODUCT_IDENTIFIER);
            //sale.setDiscount(saleUpdated.getDiscount());
            //LOGGER.info("{}", saleUpdated);
            LOGGER.info("Response is: {}", response.getMsg());
            LOGGER.info("{}", prodUpdated);
        } else {
            LOGGER.error("Error executing rules. Message: {}", response.getMsg());
        }
    }

    /*private BatchExecutionCommand batchCommand(List<ImportProduct> product) {
        List<Command<?>> cmds = buildCommands(product);

        BatchExecutionCommand batchExecutionCommand = CommandFactory.newBatchExecution(cmds, mySession);
        return batchExecutionCommand;
    }

    private List<Command<?>> buildCommands(List<ImportProduct> product) {
        List<Command<?>> cmds = new ArrayList<Command<?>>();
        KieCommands commands = KieServices.Factory.get().getCommands();
        cmds.add(commands.newInsert(product, PRODUCT_IDENTIFIER));
        cmds.add(commands.newFireAllRules());
        ObjectFilter factsFilter = new ClassObjectFilter(resultClass);
        System.out.println("Check: "+cmds.toString());
        return cmds;
    }*/

    private Command<?> prepareCommands(List<ImportProduct> facts, String sessionName, String outIdentifier) {
        KieCommands commandsFactory = KieServices.Factory.get().getCommands();
        //System.out.println("commandsFactory: "+commandsFactory.toString());
        List<Command> commands = facts.stream().map(commandsFactory::newInsert).collect(Collectors.toList());
        commands.add(commandsFactory.newFireAllRules());
        ObjectFilter factsFilter = new ClassObjectFilter(ImportProduct.class);
        //System.out.println("Check1: "+commandsFactory.newGetObjects(factsFilter, CLASS_NAME));
        //System.out.println("Check2: "+commands.toString());
        commands.add(commandsFactory.newGetObjects(factsFilter, CLASS_NAME));
        return commandsFactory.newBatchExecution(commands, sessionName);
    }
}
