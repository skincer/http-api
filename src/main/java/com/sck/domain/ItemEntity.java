package com.sck.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

/**
 * Created by TEKKINCERS on 4/24/2017.
 */
@Entity
@Table(name = "`item`", schema = "sck_test")
public class ItemEntity extends BaseEntity implements RestInitializable<Long> {

    @Id
    @GeneratedValue
    private Long id;
    private String code;

    @Column(name = "`order_id`", insertable = false, updatable = false)
    private Long orderId;

    public ItemEntity() {
    }

    public ItemEntity(String code) {
        this.code = code;
    }

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "`order_id`")
    private OrderEntity order;
    public OrderEntity getOrder() {
        return order;
    }
    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
