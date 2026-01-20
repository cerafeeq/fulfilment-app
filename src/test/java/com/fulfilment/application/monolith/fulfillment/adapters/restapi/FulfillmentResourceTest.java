package com.fulfilment.application.monolith.fulfillment.adapters.restapi;

import com.fulfilment.application.monolith.fulfillment.adapters.database.StoreProductWarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.fulfillment.domain.models.StoreProductWarehouse;
import com.fulfilment.application.monolith.warehouses.domain.services.WarehouseValidationService;
import com.fulfilment.application.monolith.fulfillment.adapters.restapi.dto.FulfillmentAssociationRequest;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
public class FulfillmentResourceTest {

    @InjectMock
    StoreProductWarehouseRepository repository;

    @InjectMock
    WarehouseValidationService validationService;

    @BeforeEach
    void setup() {
        reset(repository, validationService);
    }

    @Test
    void testListAll() {
        when(repository.listAll()).thenReturn(List.of(
                new StoreProductWarehouse(1L, 10L, "WH-001"),
                new StoreProductWarehouse(2L, 11L, "WH-002")
        ));

        given()
                .when().get("/fulfillment")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].storeId", equalTo(1))
                .body("[1].storeId", equalTo(2));
    }

    @Test
    void testListAllWhenEmpty() {
        when(repository.listAll()).thenReturn(List.of());

        given()
                .when().get("/fulfillment")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void testGetByStore() {
        when(repository.findByStore(1L)).thenReturn(List.of(
                new StoreProductWarehouse(1L, 10L, "WH-001"),
                new StoreProductWarehouse(1L, 11L, "WH-002")
        ));

        given()
                .pathParam("storeId", 1)
                .when().get("/fulfillment/store/{storeId}")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("storeId", everyItem(equalTo(1)));
    }

    @Test
    void testGetByProduct() {
        when(repository.findByProduct(10L)).thenReturn(List.of(
                new StoreProductWarehouse(1L, 10L, "WH-001"),
                new StoreProductWarehouse(2L, 10L, "WH-002")
        ));

        given()
                .pathParam("productId", 10)
                .when().get("/fulfillment/product/{productId}")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("productId", everyItem(equalTo(10)));
    }

    @Test
    void testGetByWarehouse() {
        when(repository.findByWarehouse("WH-001")).thenReturn(List.of(
                new StoreProductWarehouse(1L, 10L, "WH-001"),
                new StoreProductWarehouse(2L, 11L, "WH-001")
        ));

        given()
                .pathParam("warehouseBusinessUnitCode", "WH-001")
                .when().get("/fulfillment/warehouse/{warehouseBusinessUnitCode}")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("warehouseBusinessUnitCode", everyItem(equalTo("WH-001")));
    }

    @Test
    void testCreateAssociationSuccess() {
        doNothing().when(validationService)
                .validateFulfillmentAssociation(anyLong(), anyLong(), anyString());

        doNothing().when(repository).persist(any(StoreProductWarehouse.class));

        FulfillmentAssociationRequest request = new FulfillmentAssociationRequest();
        request.setStoreId(1L);
        request.setProductId(10L);
        request.setWarehouseBusinessUnitCode("WH-001");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/fulfillment")
                .then()
                .statusCode(201)
                .body("storeId", equalTo(1))
                .body("productId", equalTo(10))
                .body("warehouseBusinessUnitCode", equalTo("WH-001"))
                .body("createdAt", notNullValue());

        verify(validationService, times(1))
                .validateFulfillmentAssociation(1L, 10L, "WH-001");
    }

    @Test
    void testCreateAssociationValidationFailure() {
        doThrow(new WarehouseValidationException("Warehouse does not exist"))
                .when(validationService)
                .validateFulfillmentAssociation(anyLong(), anyLong(), anyString());

        FulfillmentAssociationRequest request = new FulfillmentAssociationRequest();
        request.setStoreId(1L);
        request.setProductId(10L);
        request.setWarehouseBusinessUnitCode("WH-001");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/fulfillment")
                .then()
                .statusCode(400)
                .body("error", containsString("Warehouse does not exist"));
    }

    @Test
    void testCreateAssociationWithNullFields() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"productId\":10}")
                .when().post("/fulfillment")
                .then()
                .statusCode(400);
    }

    private void assertFalse(boolean condition) {
        org.junit.jupiter.api.Assertions.assertFalse(condition);
    }

    private void assertTrue(boolean condition) {
        org.junit.jupiter.api.Assertions.assertTrue(condition);
    }
}
