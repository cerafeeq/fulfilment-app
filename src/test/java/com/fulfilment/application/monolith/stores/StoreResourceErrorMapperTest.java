package com.fulfilment.application.monolith.stores;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StoreResourceErrorMapperTest {

    private StoreResource.ErrorMapper errorMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        errorMapper = new StoreResource.ErrorMapper();
        objectMapper = new ObjectMapper();
        // Inject the ObjectMapper into the ErrorMapper
        errorMapper.objectMapper = objectMapper;
    }

    @Test
    public void testMapWebApplicationException() throws Exception {
        // Given
        String errorMessage = "Store with id of 123 does not exist.";
        WebApplicationException exception = new WebApplicationException(errorMessage, 404);

        // When
        Response response = errorMapper.toResponse(exception);

        // Then
        assertEquals(404, response.getStatus());

        String jsonResponse = response.getEntity().toString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);

        assertEquals("jakarta.ws.rs.WebApplicationException", jsonNode.get("exceptionType").asText());
        assertEquals(404, jsonNode.get("code").asInt());
        assertEquals(errorMessage, jsonNode.get("error").asText());
    }
}
