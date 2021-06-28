package com.app.engine.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import com.data.objects.Employee;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.drools.core.io.impl.UrlResource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
//@Component
public class DroolsConsumer {

    private static final String PRODUCT_IDENTIFIER = "Employee";
    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsConsumer.class);
    private static String CLASS_NAME = "Employee";
    //private Class resultClass;
    //@Autowired
    /*private Produce p;

    public DroolsConsumer(Produce p) {
        this.p = p;
    }*/

    @Value("${kie.containerId}")
    private String containerId;
    @Value("${wb.server.user}")
    private String user;
    @Value("${wb.server.password}")
    private String password;
    @Value("${kie.server.url}")
    private String url;
    @Value("${kie.server.session}")
    private String mySession;

    private Command<ExecutionResults> prepareCommands(List<Employee> facts, String outIdentifier) {
        KieCommands commandsFactory = KieServices.Factory.get().getCommands();
        List<Command> commands = facts.stream().map(commandsFactory::newInsert).collect(Collectors.toList());
        commands.add(commandsFactory.newFireAllRules());
        ObjectFilter factsFilter = new ClassObjectFilter(Employee.class);
        //System.out.println("Check1: "+commandsFactory.newGetObjects(factsFilter, CLASS_NAME));
        //System.out.println("Check2: "+commands.toString());
        commands.add(commandsFactory.newGetObjects(factsFilter, CLASS_NAME));
        return commandsFactory.newBatchExecution(commands);
    }

    //@PostConstruct
    public List<Employee> applyRules() throws IOException {
        List<Employee> updatedProducts = new ArrayList<>();
        KieServices ks = KieServices.Factory.get();
        System.out.println("KieServices: "+ks.toString());
        KieRepository kr = ks.getRepository();
        System.out.println("KieRepository: "+kr.toString());
        UrlResource urlResource = (UrlResource) ks.getResources()
                .newUrlResource("http://localhost:8080/business-central/maven2/com/data/objects/hr-department/1.0.0-SNAPSHOT/hr-department-1.0.0-SNAPSHOT.jar");
        urlResource.setUsername(user);
        urlResource.setPassword(password);
        urlResource.setBasicAuthentication("enabled");
        try {
            InputStream is = urlResource.getInputStream();
            //System.out.println("InputStream: "+is.toString());
            KieModule kModule = kr.addKieModule(ks.getResources()
                    .newInputStreamResource(is));
            KieContainer kContainer = ks.newKieContainer(kModule.getReleaseId());
            StatelessKieSession kSession = kContainer.newStatelessKieSession();
            
            // it will remove duplicate object, It will check duplicate using equals method
            List<Employee> uniqueProducts = consumeMessage().stream().distinct().collect(Collectors.toList());
            System.out.println("Facts: "+uniqueProducts);
            ExecutionResults results = null;
            //ExecutionResults results = kSession.execute(prepareCommands(uniqueProducts, CLASS_NAME));
            for(Employee p : uniqueProducts) {
                results = kSession.execute(prepareCommands(uniqueProducts, CLASS_NAME));
                updatedProducts = (List<Employee>) results.getValue(CLASS_NAME);
                System.out.println("Results value: "+results.getValue(CLASS_NAME));
                results.getIdentifiers();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return updatedProducts;
    }

    public List<Employee> consumeMessage() {
            //p.produceMessage();
            Properties properties = new Properties();
            properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            properties.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group");
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ProductDeserializer.class);
            properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
            //max.poll.interval.ms: 3600000
            Consumer<String, Employee> consumer = new KafkaConsumer<>(properties);
            //subscribe to topic
            consumer.subscribe(Collections.singleton("Employee"));
            //poll the record from the topic
            //while (true) {

            /*ConsumerRecords<String, Employee> records = consumer.poll(Duration.ofSeconds(5));
            List<Employee> products = new ArrayList<>();
            for (ConsumerRecord<String, Employee> record : records) {
                System.out.println("Check products: "+record.value());
                products.add(record.value());
            }
            consumer.commitAsync();
            return products;*/
        List<Employee> products = new ArrayList<>();
        boolean stop = false;
        int pollTimeout = 1000;
        while (!stop) {
            // Request unread messages from the topic.
            ConsumerRecords<String, Employee> consumerRecords = consumer.poll(pollTimeout);
            Iterator<ConsumerRecord<String, Employee>> iterator = consumerRecords.iterator();
            if (iterator.hasNext()) {
                while (iterator.hasNext()) {
                    ConsumerRecord<String, Employee> record = iterator.next();
                    // Iterate through returned records, extract the value
                    // of each message, and print the value to standard output.
                    products.add(record.value());
                    System.out.println((" Consumed Record: " + record.toString()));
                }
            } else {
                stop = true;
            }
        }
        consumer.close();
        return products;
        }
}
