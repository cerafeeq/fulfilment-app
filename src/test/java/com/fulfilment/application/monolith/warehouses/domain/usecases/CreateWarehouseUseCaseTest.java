package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.inject.Inject;
import java.time.LocalDateTime;

@QuarkusTest
public class CreateWarehouseUseCaseTest {

    @InjectMock
    private WarehouseStore warehouseStore;

    @Inject
    private CreateWarehouseUseCase createWarehouseUseCase;

    @Test
    public void testCreateWarehouseSuccessfully() {
        // Given
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("MWH.001");
        warehouse.setLocation("ZWOLLE-001");
        warehouse.setCapacity(100);
        warehouse.setStock(50);
        warehouse.setCreatedAt(LocalDateTime.now());

        // When
        createWarehouseUseCase.create(warehouse);

        // Then
        verify(warehouseStore, times(1)).create(warehouse);
    }

    @Test
    public void testCreateWarehouseWithNullLocation() {
        // Given
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("MWH.001");
        warehouse.setLocation(null); // Missing location
        warehouse.setCapacity(100);
        warehouse.setStock(50);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createWarehouseUseCase.create(warehouse)
        );

        assertEquals("Warehouse location is required", exception.getMessage());
        verify(warehouseStore, never()).create(any());
    }

    @Test
    public void testCreateWarehouseWithNegativeCapacity() {
        // Given
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("MWH.001");
        warehouse.setLocation("ZWOLLE-001");
        warehouse.setCapacity(-10); // Negative capacity
        warehouse.setStock(0);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createWarehouseUseCase.create(warehouse)
        );

        assertEquals("Warehouse capacity must be positive", exception.getMessage());
        verify(warehouseStore, never()).create(any());
    }

    @Test
    public void testCreateWarehouseWithZeroCapacity() {
        // Given
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("MWH.001");
        warehouse.setLocation("ZWOLLE-001");
        warehouse.setCapacity(0); // Zero capacity
        warehouse.setStock(0);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createWarehouseUseCase.create(warehouse)
        );

        assertEquals("Warehouse capacity must be positive", exception.getMessage());
        verify(warehouseStore, never()).create(any());
    }

    @Test
    public void testCreateWarehouseWithNullCapacity() {
        // Given
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("MWH.001");
        warehouse.setLocation("ZWOLLE-001");
        warehouse.setCapacity(null); // Null capacity should be allowed
        warehouse.setStock(0);

        // When
        createWarehouseUseCase.create(warehouse);

        // Then
        verify(warehouseStore, times(1)).create(warehouse);
    }

    @Test
    public void testCreateWarehouseResetsArchivedFlag() {
        // Given
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("MWH.001");
        warehouse.setLocation("ZWOLLE-001");
        warehouse.setCapacity(100);
        warehouse.setStock(50);
        warehouse.setArchivedAt(LocalDateTime.now()); // Set as archived

        // When
        createWarehouseUseCase.create(warehouse);

        // Then
        ArgumentCaptor<Warehouse> warehouseCaptor = ArgumentCaptor.forClass(Warehouse.class);
        verify(warehouseStore, times(1)).create(warehouseCaptor.capture());

        Warehouse capturedWarehouse = warehouseCaptor.getValue();
        assertFalse(capturedWarehouse.isArchived(), "Warehouse should not be archived after creation");
    }

    @Test
    public void testCreateWarehouseWithAllValidFields() {
        // Given
        Warehouse warehouse = new Warehouse();
        warehouse.setId("1");
        warehouse.setBusinessUnitCode("MWH.001");
        warehouse.setLocation("AMSTERDAM-001");
        warehouse.setCapacity(200);
        warehouse.setStock(100);
        warehouse.setCreatedAt(LocalDateTime.now());
        warehouse.setArchivedAt(null);

        // When
        createWarehouseUseCase.create(warehouse);

        // Then
        verify(warehouseStore, times(1)).create(warehouse);
    }

    @Test
    public void testCreateWarehouseWithMinimalFields() {
        // Given - Only required fields
        Warehouse warehouse = new Warehouse();
        warehouse.setLocation("TILBURG-001");
        warehouse.setCapacity(50);

        // When
        createWarehouseUseCase.create(warehouse);

        // Then
        verify(warehouseStore, times(1)).create(warehouse);
    }

    @Test
    public void testCreateWarehouseStoreThrowsException() {
        // Given
        Warehouse warehouse = new Warehouse();
        warehouse.setLocation("ZWOLLE-001");
        warehouse.setCapacity(100);

        doThrow(new RuntimeException("Database connection failed"))
                .when(warehouseStore).create(any(Warehouse.class));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> createWarehouseUseCase.create(warehouse)
        );

        assertEquals("Database connection failed", exception.getMessage());
        verify(warehouseStore, times(1)).create(warehouse);
    }

    @Test
    public void testCreateWarehouseWithLargeCapacity() {
        // Given
        Warehouse warehouse = new Warehouse();
        warehouse.setLocation("ZWOLLE-001");
        warehouse.setCapacity(1000000); // Very large capacity
        warehouse.setStock(0);

        // When
        createWarehouseUseCase.create(warehouse);

        // Then
        verify(warehouseStore, times(1)).create(warehouse);
    }

    /*@Test
    public void testCreateWarehouseEnsuresArchivedFlagIsSetToFalse() {
        // Given - warehouse that is marked as archived
        Warehouse warehouse = new Warehouse();
        warehouse.setLocation("EINDHOVEN-001");
        warehouse.setCapacity(75);
        warehouse.setArchivedAt(LocalDateTime.now());

        // When
        createWarehouseUseCase.create(warehouse);

        // Then
        verify(warehouseStore).create(any(Warehouse.class));

        // The warehouse object itself should have archivedAt set to null after create is called
        assertNull(warehouse.getArchivedAt(),
                "ArchivedAt should be null for newly created warehouse");
    }*/

    @Test
    public void testCreateWarehouseDoesNotModifyNonArchivedWarehouse() {
        // Given - warehouse that is not archived
        Warehouse warehouse = new Warehouse();
        warehouse.setLocation("ROTTERDAM-001");
        warehouse.setCapacity(150);
        warehouse.setArchivedAt(null);

        // When
        createWarehouseUseCase.create(warehouse);

        // Then
        verify(warehouseStore, times(1)).create(warehouse);
        assertNull(warehouse.getArchivedAt(), "ArchivedAt should remain null");
    }
}
