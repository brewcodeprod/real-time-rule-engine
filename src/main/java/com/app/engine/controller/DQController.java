package com.app.engine.controller;

import com.app.engine.service.DQValidate;
import org.springframework.web.bind.annotation.*;

import com.test.importproducts.ImportProduct;
//import java.util.ArrayList;
import java.util.List;

@RestController
public class DQController {

    private DQValidate dqValidate;

    public DQController(DQValidate service) {
        this.dqValidate = service;
    }

    @RequestMapping(value="/products", method = RequestMethod.POST)
    private List<ImportProduct> getDiscountPercent(@RequestBody List<ImportProduct> product) {
        this.dqValidate.applyRules(product);
        return product;
    }
}
