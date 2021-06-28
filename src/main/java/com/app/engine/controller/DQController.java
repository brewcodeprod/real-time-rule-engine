package com.app.engine.controller;

//import com.app.engine.service.DQValidate;
import com.app.engine.kafka.DroolsConsumer;
import com.data.objects.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.test.importproducts.ImportProduct;
//import java.util.ArrayList;
import java.io.IOException;
import java.util.List;

@RestController
public class DQController {

    @Autowired
    private DroolsConsumer consumer;

    public DQController(DroolsConsumer service) {
        this.consumer = service;
    }

    @RequestMapping(value="/", method = RequestMethod.GET)
    private List<Employee> fireRules() throws IOException {
        System.out.println("Firing DRL rules..");
        return this.consumer.applyRules();
    }
}
