package com.fulfilment.application.monolith.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ProductResourceErrorMapperTest {

    private ProductResource.ErrorMapper errorMapper;

    @Inject
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        errorMapper = new ProductResource.ErrorMapper();
        try {
            var field = ProductResource.ErrorMapper.class.getDeclaredField("objectMapper");
            field.setAccessible(true);
            field.set(errorMapper, objectMapper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject ObjectMapper", e);
        }
    }

    @Test
    void testToResponse_WithWebApplicationException_Returns404() {
        // Given
        String errorMessage = "Product not found";
        WebApplicationException exception = new WebApplicationException(errorMessage, 404);

        // When
        Response response = errorMapper.toResponse(exception);

        // Then
        assertEquals(404, response.getStatus());

        ObjectNode entity = (ObjectNode) response.getEntity();
        assertEquals(WebApplicationException.class.getName(), entity.get("exceptionType").asText());
        assertEquals(404, entity.get("code").asInt());
        assertEquals(errorMessage, entity.get("error").asText());
    }

    @Test
    void testToResponse_WithWebApplicationException_Returns422() {
        // Given
        String errorMessage = "Invalid request";
        WebApplicationException exception = new WebApplicationException(errorMessage, 422);

        // When
        Response response = errorMapper.toResponse(exception);

        // Then
        assertEquals(422, response.getStatus());

        ObjectNode entity = (ObjectNode) response.getEntity();
        assertEquals(WebApplicationException.class.getName(), entity.get("exceptionType").asText());
        assertEquals(422, entity.get("code").asInt());
        assertEquals(errorMessage, entity.get("error").asText());
    }

    @Test
    void testToResponse_WithGenericException_Returns500() {
        // Given
        String errorMessage = "Internal server error";
        Exception exception = new RuntimeException(errorMessage);

        // When
        Response response = errorMapper.toResponse(exception);

        // Then
        assertEquals(500, response.getStatus());

        ObjectNode entity = (ObjectNode) response.getEntity();
        assertEquals(RuntimeException.class.getName(), entity.get("exceptionType").asText());
        assertEquals(500, entity.get("code").asInt());
        assertEquals(errorMessage, entity.get("error").asText());
    }

    @Test
    void testToResponse_WithNullPointerException_Returns500() {
        // Given
        Exception exception = new NullPointerException("Null pointer encountered");

        // When
        Response response = errorMapper.toResponse(exception);

        // Then
        assertEquals(500, response.getStatus());

        ObjectNode entity = (ObjectNode) response.getEntity();
        assertEquals(NullPointerException.class.getName(), entity.get("exceptionType").asText());
        assertEquals(500, entity.get("code").asInt());
        assertEquals("Null pointer encountered", entity.get("error").asText());
    }

    @Test
    void testToResponse_WithExceptionWithoutMessage_DoesNotIncludeErrorField() {
        // Given
        Exception exception = new RuntimeException();

        // When
        Response response = errorMapper.toResponse(exception);

        // Then
        assertEquals(500, response.getStatus());

        ObjectNode entity = (ObjectNode) response.getEntity();
        assertEquals(RuntimeException.class.getName(), entity.get("exceptionType").asText());
        assertEquals(500, entity.get("code").asInt());
        assertNull(entity.get("error"));
    }

    @Test
    void testToResponse_ResponseEntityIsObjectNode() {
        // Given
        Exception exception = new RuntimeException("Test error");

        // When
        Response response = errorMapper.toResponse(exception);

        // Then
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof ObjectNode);
    }

    @Test
    void testToResponse_ExceptionTypeIsCorrect() {
        // Given
        Exception exception = new IllegalStateException("Invalid state");

        // When
        Response response = errorMapper.toResponse(exception);

        // Then
        ObjectNode entity = (ObjectNode) response.getEntity();
        assertEquals(IllegalStateException.class.getName(), entity.get("exceptionType").asText());
    }
}
