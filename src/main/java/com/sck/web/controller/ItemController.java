package com.sck.web.controller;

import com.sck.domain.ItemEntity;
import com.sck.repository.ItemRepository;
import com.sck.web.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by TEKKINCERS on 4/24/2017.
 */
@RestController
@RequestMapping("/rest/item")
public class ItemController extends BaseRestController<ItemEntity, Long> {

    @Autowired
    public ItemController(ItemRepository repository) {
        super(repository);
    }
}
