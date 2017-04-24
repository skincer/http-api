package com.sck.web.controller;

import com.sck.domain.CustomerEntity;
import com.sck.repository.CustomerRepository;
import com.sck.web.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Created by TEKKINCERS on 4/24/2017.
 */
@RestController
@RequestMapping("/rest/customer")
public class CustomerController extends BaseRestController<CustomerEntity, Long> {

    @Autowired
    public CustomerController(CustomerRepository repository) {
        super(repository);
    }

    @RequestMapping("/{id}/order")
    public ResponseEntity<?> getCustomerOrders(@PathVariable Long id) {

        return Optional.ofNullable(repository.findOne(id).getOrders())
                .map(orders -> new ResponseEntity<>(orders, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
