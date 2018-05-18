package com.tcc.distributedtransaction.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JsonUtils {

    private static ObjectMapper objectMapper = InnerObjectMapper.objectMapper;

    private JsonUtils() {

    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static String toJson(Object targetObj) {
        String jsonStr = null;
        try {
            jsonStr = getObjectMapper().writeValueAsString(targetObj);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return jsonStr;
    }

    @SuppressWarnings("unchecked")
    public static Object toObject(String jsonStr, Class clazz) {
        Object targetObj = null;
        try {
            targetObj = getObjectMapper().readValue(jsonStr, clazz);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return targetObj;
    }

    private static class InnerObjectMapper {
        public static final ObjectMapper objectMapper = new ObjectMapper();
    }
}
