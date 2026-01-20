package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.adapters.database.StoreProductWarehouseRepository;
import com.fulfilment.application.monolith.fulfillment.domain.models.StoreProductWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.services.WarehouseValidationService;
import com.fulfilment.application.monolith.fulfillment.adapters.restapi.dto.FulfillmentAssociationRequest;
import com.fulfilment.application.monolith.fulfillment.adapters.restapi.dto.FulfillmentAssociationResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class FulfillmentUseCase {

    @Inject
    StoreProductWarehouseRepository fulfillmentRepository;

    @Inject
    WarehouseValidationService validationService;

    public List<FulfillmentAssociationResponse> listAll() {
        return fulfillmentRepository.listAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<FulfillmentAssociationResponse> getByStore(Long storeId) {
        return fulfillmentRepository.findByStore(storeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<FulfillmentAssociationResponse> getByProduct(Long productId) {
        return fulfillmentRepository.findByProduct(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<FulfillmentAssociationResponse> getByWarehouse(String warehouseCode) {
        return fulfillmentRepository.findByWarehouse(warehouseCode)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FulfillmentAssociationResponse createAssociation(
            FulfillmentAssociationRequest request) {

        validationService.validateFulfillmentAssociation(
                request.getStoreId(),
                request.getProductId(),
                request.getWarehouseBusinessUnitCode()
        );

        StoreProductWarehouse association =
                new StoreProductWarehouse(
                        request.getStoreId(),
                        request.getProductId(),
                        request.getWarehouseBusinessUnitCode());

        association.setCreatedAt(LocalDateTime.now());
        fulfillmentRepository.persist(association);

        return toResponse(association);
    }

    public void deleteAssociation(
            Long storeId, Long productId, String warehouseBusinessUnitCode) {

        if (!fulfillmentRepository.exists(
                storeId, productId, warehouseBusinessUnitCode)) {
            throw new WebApplicationException(
                    "Association not found",
                    Response.Status.NOT_FOUND);
        }

        fulfillmentRepository.deleteByStoreAndProductAndWarehouse(
                storeId, productId, warehouseBusinessUnitCode);
    }

    private FulfillmentAssociationResponse toResponse(StoreProductWarehouse association) {
        FulfillmentAssociationResponse response =
                new FulfillmentAssociationResponse();
        response.setId(association.id);
        response.setStoreId(association.getStoreId());
        response.setProductId(association.getProductId());
        response.setWarehouseBusinessUnitCode(
                association.getWarehouseBusinessUnitCode());
        response.setCreatedAt(association.getCreatedAt());
        return response;
    }
}

