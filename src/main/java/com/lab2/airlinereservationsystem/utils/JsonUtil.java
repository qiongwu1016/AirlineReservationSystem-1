package com.lab2.airlinereservationsystem.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lab2.airlinereservationsystem.common.exception.ErrorExceptionWrapper;

import java.io.IOException;

public class JsonUtil {

    private JsonUtil() {
        throw new IllegalStateException("JsonUtil class");
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    public static <T>T parseObject(String json,Class<T> tClass){
        try {
            return OBJECT_MAPPER.readValue(json,tClass);
        } catch (IOException e) {
            throw new ErrorExceptionWrapper(e.getMessage());
        }
    }

    public static String toJsonString(Object object){
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            throw new ErrorExceptionWrapper(e.getMessage());
        }
    }


}
