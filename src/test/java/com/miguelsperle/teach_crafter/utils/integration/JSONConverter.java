package com.miguelsperle.teach_crafter.utils.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JSONConverter {
    public static String objectToJSON(Object object) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            return objectMapper.writeValueAsString(object);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
