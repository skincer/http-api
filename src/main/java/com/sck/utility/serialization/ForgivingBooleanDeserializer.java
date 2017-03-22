package com.sck.utility.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Created by TEKKINCERS on 5/14/2015.
 */
public class ForgivingBooleanDeserializer extends JsonDeserializer<Boolean> {

    protected static final String NO = "no";
    protected static final String YES = "yes";
    protected static final String ONE = "1";
    protected static final String ZERO = "0";
    protected static final String TRUE = "true";
    protected static final String FALSE = "false";


    @Override
    public Boolean deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonToken currentToken = jp.getCurrentToken();

        if (currentToken.equals(JsonToken.VALUE_STRING)) {

            String strVal = jp.getText().trim();

            if (YES.equalsIgnoreCase(strVal)) {
                return Boolean.TRUE;
            } else if (NO.equalsIgnoreCase(strVal)) {
                return Boolean.FALSE;
            } else if (ONE.equalsIgnoreCase(strVal)) {
                return Boolean.TRUE;
            } else if (ZERO.equalsIgnoreCase(strVal)) {
                return Boolean.FALSE;
            } else if (TRUE.equalsIgnoreCase(strVal)) {
                return Boolean.TRUE;
            } else if (FALSE.equalsIgnoreCase(strVal)) {
                return Boolean.FALSE;
            }

            throw ctxt.weirdStringException(strVal, Boolean.class,
                    "That is not a supported string value for Boolean!");

        }else if(currentToken.isBoolean()) {

            return jp.getBooleanValue();

        }else if(currentToken.isNumeric()) {

            Integer val = jp.getIntValue();

            if(val == 1) {
                return true;
            }else if(val == 0) {
                return false;
            }

            throw ctxt.weirdNumberException(val, Boolean.class,
                    "That is not a supported number for Boolean!");

        } else if (currentToken.equals(JsonToken.VALUE_NULL)) {

            return getNullValue();

        }

        throw ctxt.mappingException(Boolean.class);
    }

    @Override
    public Boolean getNullValue() {
        return Boolean.FALSE;
    }

}
