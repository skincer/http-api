package com.sck.repository;

import com.sck.domain.CustomerEntity;

import java.util.List;

/**
 * Created by TEKKINCERS on 4/24/2017.
 */
public interface CustomerRepository extends JpaSpecRepository<CustomerEntity, Long> {

    List<CustomerEntity> findByName(String name);
}
