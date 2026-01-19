package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fulfilment.application.monolith.warehouses.adapters.database.StoreProductWarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.StoreProductWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.services.WarehouseValidationService;
import com.fulfilment.application.monolith.warehouses.dto.FulfillmentAssociationRequest;
import com.fulfilment.application.monolith.warehouses.dto.FulfillmentAssociationResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.logging.Logger;
import jakarta.ws.rs.core.Response;
import jakarta.validation.Valid;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;
import java.util.stream.Collectors;

@Path("fulfillment")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FulfillmentResource {

    private static final Logger LOGGER = Logger.getLogger(FulfillmentResource.class.getName());

    @Inject
    private StoreProductWarehouseRepository fulfillmentRepository;

    @Inject
    private WarehouseValidationService validationService;

    @GET
    public List<FulfillmentAssociationResponse> listAll() {
        return fulfillmentRepository.listAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GET
    @Path("store/{storeId}")
    public List<FulfillmentAssociationResponse> getByStore(@PathParam("storeId") Long storeId) {
        return fulfillmentRepository.findByStore(storeId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GET
    @Path("product/{productId}")
    public List<FulfillmentAssociationResponse> getByProduct(@PathParam("productId") Long productId) {
        return fulfillmentRepository.findByProduct(productId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GET
    @Path("warehouse/{warehouseBusinessUnitCode}")
    public List<FulfillmentAssociationResponse> getByWarehouse(
            @PathParam("warehouseBusinessUnitCode") String warehouseBusinessUnitCode) {
        return fulfillmentRepository.findByWarehouse(warehouseBusinessUnitCode)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @POST
    @Transactional
    public Response createAssociation(@Valid FulfillmentAssociationRequest request) {
        try {
            // Use the consolidated validation service
            validationService.validateFulfillmentAssociation(
                    request.getStoreId(),
                    request.getProductId(),
                    request.getWarehouseBusinessUnitCode()
            );

            StoreProductWarehouse association = new StoreProductWarehouse(
                    request.getStoreId(),
                    request.getProductId(),
                    request.getWarehouseBusinessUnitCode()
            );

            association.setCreatedAt(LocalDateTime.now());

            fulfillmentRepository.persist(association);

            return Response.ok(toResponse(association))
                    .status(Response.Status.CREATED)
                    .build();

        } catch (WarehouseValidationException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    @DELETE
    @Path("store/{storeId}/product/{productId}/warehouse/{warehouseBusinessUnitCode}")
    @Transactional
    public Response deleteAssociation(
            @PathParam("storeId") Long storeId,
            @PathParam("productId") Long productId,
            @PathParam("warehouseBusinessUnitCode") String warehouseBusinessUnitCode) {

        if (!fulfillmentRepository.exists(storeId, productId, warehouseBusinessUnitCode)) {
            throw new WebApplicationException(
                    "Association not found for Store " + storeId + ", Product " + productId +
                            ", and Warehouse " + warehouseBusinessUnitCode,
                    Response.Status.NOT_FOUND
            );
        }

        fulfillmentRepository.deleteByStoreAndProductAndWarehouse(storeId, productId, warehouseBusinessUnitCode);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    private FulfillmentAssociationResponse toResponse(StoreProductWarehouse association) {
        FulfillmentAssociationResponse response = new FulfillmentAssociationResponse();
        response.setId(association.id);
        response.setStoreId(association.getStoreId());
        response.setProductId(association.getProductId());
        response.setWarehouseBusinessUnitCode(association.getWarehouseBusinessUnitCode());
        response.setCreatedAt(association.getCreatedAt());
        return response;
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Inject
        ObjectMapper objectMapper;

        @Override
        public Response toResponse(Exception exception) {
            LOGGER.severe("Failed to handle request: " + exception.getMessage());

            int code = 500;
            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            ObjectNode exceptionJson = objectMapper.createObjectNode();
            exceptionJson.put("exceptionType", exception.getClass().getName());
            exceptionJson.put("code", code);

            if (exception.getMessage() != null) {
                exceptionJson.put("error", exception.getMessage());
            }

            return Response.status(code).entity(exceptionJson).build();
        }
    }
}
