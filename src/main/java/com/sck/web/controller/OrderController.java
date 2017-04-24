package com.sck.web.controller;

import com.sck.domain.OrderEntity;
import com.sck.repository.OrderRepository;
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
@RequestMapping("/rest/order")
public class OrderController extends BaseRestController<OrderEntity, Long> {

    @Autowired
    public OrderController(OrderRepository repository) {
        super(repository);
    }

    @RequestMapping("/{id}/item")
    public ResponseEntity<?> getOrderItems(@PathVariable Long id) {

        return Optional.ofNullable(repository.findOne(id).getItems())
                .map(items -> new ResponseEntity<>(items, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
