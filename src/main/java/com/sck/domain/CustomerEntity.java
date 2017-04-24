package com.sck.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by TEKKINCERS on 4/24/2017.
 */
@Entity
@Table(name = "`customer`", schema = "sck_test")
public class CustomerEntity extends BaseEntity implements RestInitializable<Long> {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    private String name;

    public CustomerEntity() {
    }

    public CustomerEntity(String name) {
        this.name = name;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OrderEntity> orders;
    public Set<OrderEntity> getOrders() {
        return orders;
    }
    public void setOrders(Set<OrderEntity> orders) {
        this.orders = orders;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
