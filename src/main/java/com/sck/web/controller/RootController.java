package com.sck.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by TEKKINCERS on 3/22/2017.
 */
@RestController
@RequestMapping("/")
public class RootController {

    @RequestMapping(
            value = "ping",
            method = RequestMethod.GET
    )
    public ResponseEntity<String> ping() {

        return new ResponseEntity<>("pong", HttpStatus.OK);
    }
}
