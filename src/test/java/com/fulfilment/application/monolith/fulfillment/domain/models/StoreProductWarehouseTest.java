package com.fulfilment.application.monolith.fulfillment.domain.models;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StoreProductWarehouseTest {

    private StoreProductWarehouse association;

    @BeforeEach
    void setUp() {
        association = new StoreProductWarehouse();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(association);
        assertNull(association.getStoreId());
        assertNull(association.getProductId());
        assertNull(association.getWarehouseBusinessUnitCode());
        assertNull(association.getCreatedAt());
    }

    @Test
    void testParameterizedConstructor() {
        Long storeId = 1L;
        Long productId = 10L;
        String warehouseCode = "WH-001";

        StoreProductWarehouse newAssociation = new StoreProductWarehouse(storeId, productId, warehouseCode);

        assertEquals(storeId, newAssociation.getStoreId());
        assertEquals(productId, newAssociation.getProductId());
        assertEquals(warehouseCode, newAssociation.getWarehouseBusinessUnitCode());
        assertNull(newAssociation.getCreatedAt()); // Not set until @PrePersist
    }

    @Test
    void testSettersAndGetters() {
        Long storeId = 5L;
        Long productId = 20L;
        String warehouseCode = "WH-CENTRAL";
        LocalDateTime now = LocalDateTime.now();

        association.setStoreId(storeId);
        association.setProductId(productId);
        association.setWarehouseBusinessUnitCode(warehouseCode);
        association.setCreatedAt(now);

        assertEquals(storeId, association.getStoreId());
        assertEquals(productId, association.getProductId());
        assertEquals(warehouseCode, association.getWarehouseBusinessUnitCode());
        assertEquals(now, association.getCreatedAt());
    }

    @Test
    void testOnCreateSetsTimestamp() {
        // Simulate @PrePersist callback
        association.onCreate();

        assertNotNull(association.getCreatedAt());
        assertTrue(association.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(association.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void testMultipleOnCreateCalls() {
        association.onCreate();
        LocalDateTime firstTimestamp = association.getCreatedAt();

        // Wait a tiny bit
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            fail("Test interrupted");
        }

        association.onCreate();
        LocalDateTime secondTimestamp = association.getCreatedAt();

        // Each call should set a new timestamp
        assertTrue(secondTimestamp.isAfter(firstTimestamp) || secondTimestamp.isEqual(firstTimestamp));
    }

    @Test
    void testEqualsAndHashCode() {
        StoreProductWarehouse association1 = new StoreProductWarehouse(1L, 10L, "WH-001");

        // Test reflexive property - object equals itself
        assertEquals(association1, association1);

        // Test hashCode consistency
        int hash1 = association1.hashCode();
        int hash2 = association1.hashCode();
        assertEquals(hash1, hash2);

        // Set an id
        association1.id = 1L;

        // Object still equals itself
        assertEquals(association1, association1);

        // Create a different instance
        StoreProductWarehouse association2 = new StoreProductWarehouse(1L, 10L, "WH-001");
        association2.id = 2L;

        // Different ids means not equal
        assertNotEquals(association1, association2);
    }

    @Test
    void testNullValues() {
        association.setStoreId(null);
        association.setProductId(null);
        association.setWarehouseBusinessUnitCode(null);

        assertNull(association.getStoreId());
        assertNull(association.getProductId());
        assertNull(association.getWarehouseBusinessUnitCode());
    }
}
