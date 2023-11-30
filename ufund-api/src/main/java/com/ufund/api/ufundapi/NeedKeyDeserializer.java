package com.ufund.api.ufundapi;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

/**
 * Key deserializer for the Need class. Used when Need is used as the key for
 * a Map, such as in basketQuantity in the Basket class
 * 
 * @author Elijah Sanders (ejs8021)
 */
public class NeedKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        if(key == null) {
            return null;
        }
        return new Need("", 0.0, 0, new ArrayList<String>());
    }
    
}
