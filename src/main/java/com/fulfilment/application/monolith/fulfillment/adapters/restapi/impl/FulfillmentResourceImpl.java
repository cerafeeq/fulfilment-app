package com.fulfilment.application.monolith.fulfillment.adapters.restapi.impl;

import com.fulfilment.application.monolith.fulfillment.adapters.restapi.FulfillmentResource;
import com.fulfilment.application.monolith.fulfillment.domain.usecases.FulfillmentUseCase;
import com.fulfilment.application.monolith.fulfillment.adapters.restapi.dto.FulfillmentAssociationRequest;
import com.fulfilment.application.monolith.fulfillment.adapters.restapi.dto.FulfillmentAssociationResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class FulfillmentResourceImpl implements FulfillmentResource {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(FulfillmentResourceImpl.class);

    @Inject
    FulfillmentUseCase fulfillmentUseCase;

    @Override
    public List<FulfillmentAssociationResponse> listAll() {
        return fulfillmentUseCase.listAll();
    }

    @Override
    public List<FulfillmentAssociationResponse> getByStore(Long storeId) {
        return fulfillmentUseCase.getByStore(storeId);
    }

    @Override
    public List<FulfillmentAssociationResponse> getByProduct(Long productId) {
        return fulfillmentUseCase.getByProduct(productId);
    }

    @Override
    public List<FulfillmentAssociationResponse> getByWarehouse(
            String warehouseBusinessUnitCode) {
        return fulfillmentUseCase.getByWarehouse(warehouseBusinessUnitCode);
    }

    @Override
    @Transactional
    public Response createAssociation(FulfillmentAssociationRequest request) {
        FulfillmentAssociationResponse response =
                fulfillmentUseCase.createAssociation(request);

        LOGGER.info("Fulfillment association created successfully");
        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @Override
    @Transactional
    public Response deleteAssociation(
            Long storeId, Long productId, String warehouseBusinessUnitCode) {

        fulfillmentUseCase.deleteAssociation(
                storeId, productId, warehouseBusinessUnitCode);

        LOGGER.info("Deleted fulfillment association");
        return Response.noContent().build();
    }
}

