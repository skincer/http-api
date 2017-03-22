package com.sck.domain;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by TEKKINCERS on 5/27/2015.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Transient
    public Map<String, String> getForeignKeys() {
        Map<String, String> annotationMap = new HashMap<>();
        for(Field field : this.getClass().getDeclaredFields()) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            if(joinColumn != null) {
                annotationMap.put(joinColumn.name(), field.getType().getName());
            }
        }
        return annotationMap;
    }

    @CreatedBy
    @Column(name = "createdBy", nullable = false, length = 100, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "modifiedBy", length = 100)
    private String modifiedBy;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
