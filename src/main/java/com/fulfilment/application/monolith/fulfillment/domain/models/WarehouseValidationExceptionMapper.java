package com.fulfilment.application.monolith.fulfillment.domain.models;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
@ApplicationScoped
public class WarehouseValidationExceptionMapper
        implements ExceptionMapper<WarehouseValidationException> {

    @Override
    public Response toResponse(WarehouseValidationException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of(
                        "error", exception.getMessage(),
                        "type", "VALIDATION_ERROR"
                ))
                .build();
    }
}
