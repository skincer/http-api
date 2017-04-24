package com.sck.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by TEKKINCERS on 4/24/2017.
 */
@Entity
@Table(name = "`order`", schema = "sck_test")
public class OrderEntity extends BaseEntity implements RestInitializable<Long> {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "ordered_at")
    private LocalDateTime orderedAt;
    @Column(name = "`customer_id`", insertable = false, updatable = false)
    private Long customerId;

    public OrderEntity() {
    }

    public OrderEntity(LocalDateTime orderedAt, CustomerEntity customer) {
        this.orderedAt = orderedAt;
        this.customer = customer;
    }

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "`customer_id`")
    private CustomerEntity customer;
    public CustomerEntity getCustomer() {
        return customer;
    }
    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ItemEntity> items;
    public Set<ItemEntity> getItems() {
        return items;
    }
    public void setItems(Set<ItemEntity> items) {
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
