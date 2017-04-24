package com.sck.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.JoinColumn;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by TEKKINCERS on 5/27/2015.
 */
public abstract class BaseEntity {

    @JsonIgnore
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
}
