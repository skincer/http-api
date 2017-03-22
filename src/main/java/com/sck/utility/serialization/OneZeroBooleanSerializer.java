package com.sck.utility.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by TEKKINCERS on 5/14/2015.
 */
public class OneZeroBooleanSerializer extends JsonSerializer<Boolean> {

    @Override
    public void serialize(Boolean value, JsonGenerator jsonGenerator, SerializerProvider provider)
    throws IOException {

        if(value) {
            jsonGenerator.writeNumber(1);
        }else if(!value) {
            jsonGenerator.writeNumber(0);
        }

    }

}
