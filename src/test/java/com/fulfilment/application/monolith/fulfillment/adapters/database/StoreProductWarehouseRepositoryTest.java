package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.models.StoreProductWarehouse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class StoreProductWarehouseRepositoryTest {

    @Inject
    StoreProductWarehouseRepository repository;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up before each test
        repository.deleteAll();
    }

    @AfterEach
    @Transactional
    void tearDown() {
        // Clean up after each test
        repository.deleteAll();
    }

    @Test
    @Transactional
    void testPersistAndFind() {
        StoreProductWarehouse association = new StoreProductWarehouse(1L, 10L, "WH-001");
        repository.persist(association);

        assertNotNull(association.id);

        StoreProductWarehouse found = repository.findById(association.id);
        assertNotNull(found);
        assertEquals(1L, found.getStoreId());
        assertEquals(10L, found.getProductId());
        assertEquals("WH-001", found.getWarehouseBusinessUnitCode());
    }

    @Test
    @Transactional
    void testFindByStore() {
        repository.persist(new StoreProductWarehouse(1L, 10L, "WH-001"));
        repository.persist(new StoreProductWarehouse(1L, 11L, "WH-002"));
        repository.persist(new StoreProductWarehouse(2L, 12L, "WH-003"));

        List<StoreProductWarehouse> associations = repository.findByStore(1L);

        assertEquals(2, associations.size());
        assertTrue(associations.stream().allMatch(a -> a.getStoreId().equals(1L)));
    }

    @Test
    @Transactional
    void testFindByProduct() {
        repository.persist(new StoreProductWarehouse(1L, 10L, "WH-001"));
        repository.persist(new StoreProductWarehouse(2L, 10L, "WH-002"));
        repository.persist(new StoreProductWarehouse(3L, 11L, "WH-003"));

        List<StoreProductWarehouse> associations = repository.findByProduct(10L);

        assertEquals(2, associations.size());
        assertTrue(associations.stream().allMatch(a -> a.getProductId().equals(10L)));
    }

    @Test
    @Transactional
    void testFindByWarehouse() {
        repository.persist(new StoreProductWarehouse(1L, 10L, "WH-001"));
        repository.persist(new StoreProductWarehouse(2L, 11L, "WH-001"));
        repository.persist(new StoreProductWarehouse(3L, 12L, "WH-002"));

        List<StoreProductWarehouse> associations = repository.findByWarehouse("WH-001");

        assertEquals(2, associations.size());
        assertTrue(associations.stream().allMatch(a -> a.getWarehouseBusinessUnitCode().equals("WH-001")));
    }

    @Test
    @Transactional
    void testFindByStoreAndProduct() {
        repository.persist(new StoreProductWarehouse(1L, 10L, "WH-001"));
        repository.persist(new StoreProductWarehouse(1L, 10L, "WH-002"));
        repository.persist(new StoreProductWarehouse(1L, 11L, "WH-003"));

        List<StoreProductWarehouse> associations = repository.findByStoreAndProduct(1L, 10L);

        assertEquals(2, associations.size());
        assertTrue(associations.stream().allMatch(a ->
                a.getStoreId().equals(1L) && a.getProductId().equals(10L)));
    }

    @Test
    @Transactional
    void testCountByStoreAndProduct() {
        repository.persist(new StoreProductWarehouse(1L, 10L, "WH-001"));
        repository.persist(new StoreProductWarehouse(1L, 10L, "WH-002"));
        repository.persist(new StoreProductWarehouse(1L, 11L, "WH-003"));

        long count = repository.countByStoreAndProduct(1L, 10L);

        assertEquals(2, count);
    }

    @Test
    @Transactional
    void testCountDistinctWarehousesByStore() {
        repository.persist(new StoreProductWarehouse(1L, 10L, "WH-001"));
        repository.persist(new StoreProductWarehouse(1L, 11L, "WH-001")); // Same warehouse
        repository.persist(new StoreProductWarehouse(1L, 12L, "WH-002"));
        repository.persist(new StoreProductWarehouse(1L, 13L, "WH-003"));

        long count = repository.countDistinctWarehousesByStore(1L);

        assertEquals(3, count);
    }

    @Test
    @Transactional
    void testCountProductsByWarehouse() {
        repository.persist(new StoreProductWarehouse(1L, 10L, "WH-001"));
        repository.persist(new StoreProductWarehouse(2L, 10L, "WH-001")); // Same product
        repository.persist(new StoreProductWarehouse(3L, 11L, "WH-001"));
        repository.persist(new StoreProductWarehouse(4L, 12L, "WH-001"));

        long count = repository.countProductsByWarehouse("WH-001");

        assertEquals(3, count);
    }

    @Test
    @Transactional
    void testExists() {
        repository.persist(new StoreProductWarehouse(1L, 10L, "WH-001"));

        assertTrue(repository.exists(1L, 10L, "WH-001"));
        assertFalse(repository.exists(1L, 10L, "WH-002"));
        assertFalse(repository.exists(2L, 10L, "WH-001"));
        assertFalse(repository.exists(1L, 11L, "WH-001"));
    }

    @Test
    @Transactional
    void testDeleteByStoreAndProductAndWarehouse() {
        StoreProductWarehouse association = new StoreProductWarehouse(1L, 10L, "WH-001");
        repository.persist(association);
        repository.persist(new StoreProductWarehouse(1L, 11L, "WH-002"));

        assertTrue(repository.exists(1L, 10L, "WH-001"));

        repository.deleteByStoreAndProductAndWarehouse(1L, 10L, "WH-001");

        assertFalse(repository.exists(1L, 10L, "WH-001"));
        assertTrue(repository.exists(1L, 11L, "WH-002")); // Other associations remain
    }

    @Test
    @Transactional
    void testListAll() {
        repository.persist(new StoreProductWarehouse(1L, 10L, "WH-001"));
        repository.persist(new StoreProductWarehouse(2L, 11L, "WH-002"));
        repository.persist(new StoreProductWarehouse(3L, 12L, "WH-003"));

        List<StoreProductWarehouse> all = repository.listAll();

        assertEquals(3, all.size());
    }

    @Test
    @Transactional
    void testFindByStoreReturnsEmptyListWhenNoResults() {
        List<StoreProductWarehouse> associations = repository.findByStore(999L);

        assertNotNull(associations);
        assertTrue(associations.isEmpty());
    }

    @Test
    @Transactional
    void testCountReturnsZeroWhenNoResults() {
        long count = repository.countByStoreAndProduct(999L, 999L);

        assertEquals(0, count);
    }
}
