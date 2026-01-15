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
public class ReplaceWarehouseUseCaseTest {

    @Inject
    ReplaceWarehouseUseCase replaceWarehouseUseCase;

    @InjectMock
    WarehouseStore warehouseStore;

    @Test
    void replaceSucceedsWhenIdAndLocationArePresent() {
        // Given
        Warehouse warehouse = new Warehouse();
        warehouse.setId("WH-001");
        warehouse.setLocation("AMSTERDAM-001");

        // When
        replaceWarehouseUseCase.replace(warehouse);

        // Then
        verify(warehouseStore, times(1)).update(warehouse);
    }

    @Test
    void replaceFailsWhenIdIsNull() {
        // Given
        Warehouse warehouse = new Warehouse();
        warehouse.setLocation("AMSTERDAM-001");

        // When
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> replaceWarehouseUseCase.replace(warehouse)
        );

        // Then
        assertEquals("Warehouse ID is required for replacement", ex.getMessage());
        verify(warehouseStore, never()).update(any());
    }

    @Test
    void replaceFailsWhenLocationIsNull() {
        // Given
        Warehouse warehouse = new Warehouse();
        warehouse.setId("WH-001");
        warehouse.setLocation(null);

        // When
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> replaceWarehouseUseCase.replace(warehouse)
        );

        // Then
        assertEquals("Warehouse location is required", ex.getMessage());
        verify(warehouseStore, never()).update(any());
    }
}
