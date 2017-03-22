package com.sck.domain;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by TEKKINCERS on 7/8/2015.
 */
public interface RestInitializable<ID extends Serializable> {

    ID getId();

    void setId(ID id);

    @Transient
    Map<String, String> getForeignKeys();
}
