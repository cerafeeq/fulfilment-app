package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.adapters.database.StoreProductWarehouseRepository;
import com.fulfilment.application.monolith.fulfillment.adapters.restapi.dto.FulfillmentAssociationRequest;
import com.fulfilment.application.monolith.fulfillment.adapters.restapi.dto.FulfillmentAssociationResponse;
import com.fulfilment.application.monolith.fulfillment.domain.models.StoreProductWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.services.WarehouseValidationService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class FulfillmentUseCaseTest {

    @Inject
    FulfillmentUseCase useCase;

    @InjectMock
    StoreProductWarehouseRepository fulfillmentRepository;

    @InjectMock
    WarehouseValidationService validationService;

    @Test
    void listAll_returnsMappedResponses() {
        StoreProductWarehouse entity =
                new StoreProductWarehouse(1L, 10L, "WH-001");
        entity.id = 100L;
        entity.setCreatedAt(LocalDateTime.now());

        when(fulfillmentRepository.listAll())
                .thenReturn(List.of(entity));

        List<FulfillmentAssociationResponse> result =
                useCase.listAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getStoreId());
        assertEquals(10L, result.get(0).getProductId());
        assertEquals("WH-001",
                result.get(0).getWarehouseBusinessUnitCode());
        assertNotNull(result.get(0).getCreatedAt());
    }

    @Test
    void getByStore_returnsAssociationsForStore() {
        when(fulfillmentRepository.findByStore(1L))
                .thenReturn(List.of(
                        new StoreProductWarehouse(1L, 10L, "WH-001")
                ));

        List<FulfillmentAssociationResponse> result =
                useCase.getByStore(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getStoreId());
    }

    @Test
    void getByProduct_returnsAssociationsForProduct() {
        when(fulfillmentRepository.findByProduct(10L))
                .thenReturn(List.of(
                        new StoreProductWarehouse(1L, 10L, "WH-001")
                ));

        List<FulfillmentAssociationResponse> result =
                useCase.getByProduct(10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getProductId());
    }

    @Test
    void getByWarehouse_returnsAssociationsForWarehouse() {
        when(fulfillmentRepository.findByWarehouse("WH-001"))
                .thenReturn(List.of(
                        new StoreProductWarehouse(1L, 10L, "WH-001")
                ));

        List<FulfillmentAssociationResponse> result =
                useCase.getByWarehouse("WH-001");

        assertEquals(1, result.size());
        assertEquals("WH-001",
                result.get(0).getWarehouseBusinessUnitCode());
    }

    @Test
    void createAssociation_validRequest_persistsAndReturnsResponse() {
        FulfillmentAssociationRequest request = new FulfillmentAssociationRequest();
        request.setStoreId(1L);
        request.setProductId(10L);
        request.setWarehouseBusinessUnitCode("WH-001");

        doNothing().when(validationService)
                .validateFulfillmentAssociation(1L, 10L, "WH-001");

        FulfillmentAssociationResponse response =
                useCase.createAssociation(request);

        verify(validationService)
                .validateFulfillmentAssociation(1L, 10L, "WH-001");

        verify(fulfillmentRepository)
                .persist(any(StoreProductWarehouse.class));

        assertEquals(1L, response.getStoreId());
        assertEquals(10L, response.getProductId());
        assertEquals("WH-001",
                response.getWarehouseBusinessUnitCode());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void deleteAssociation_existingAssociation_deletesSuccessfully() {
        when(fulfillmentRepository.exists(1L, 10L, "WH-001"))
                .thenReturn(true);

        useCase.deleteAssociation(1L, 10L, "WH-001");

        verify(fulfillmentRepository)
                .deleteByStoreAndProductAndWarehouse(
                        1L, 10L, "WH-001");
    }

    @Test
    void deleteAssociation_missingAssociation_throwsNotFound() {
        when(fulfillmentRepository.exists(1L, 10L, "WH-001"))
                .thenReturn(false);

        WebApplicationException exception =
                assertThrows(WebApplicationException.class,
                        () -> useCase.deleteAssociation(
                                1L, 10L, "WH-001"));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),
                exception.getResponse().getStatus());
    }
}

