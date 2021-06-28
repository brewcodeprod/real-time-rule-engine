package com.app.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.app.engine.kafka", "com.app.engine.controller"})
    public class DroolsApp {

    public static void main(String[] args) {
        SpringApplication.run(DroolsApp.class, args);
    }

        /**
         private static final String CONTAINER_ID = "my-deploy";
         private static final String USER = "kieserver";
         private static final String PASSWORD = "kieserver1!";
         private static final String URL = "http://localhost:8090/rest/server";



         @Override
         public void run(String... args) throws Exception {
         KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(URL, USER, PASSWORD, 60000);
         config.setMarshallingFormat(MarshallingFormat.JSON);

         KieServicesClient client = KieServicesFactory.newKieServicesClient(config);
         KieContainerResource kContainer = new KieContainerResource();
         kContainer.setContainerId(CONTAINER_ID);
         kContainer.setReleaseId(new ReleaseId("com.github.mnadeem", "drools-discount-kjar", "0.0.1-SNAPSHOT"));

         ServiceResponse<KieContainerResource> resp = client.createContainer(CONTAINER_ID, kContainer);
         KieContainerStatus status = resp.getResult().getStatus();
         if (!KieContainerStatus.STARTED.equals(status)) {
         throw new IllegalStateException();
         }

         }**/

    }

