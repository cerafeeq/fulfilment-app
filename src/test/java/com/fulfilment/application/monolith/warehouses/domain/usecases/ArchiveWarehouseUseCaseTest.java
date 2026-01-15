package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@QuarkusTest
class ArchiveWarehouseUseCaseTest {

    @Inject
    ArchiveWarehouseUseCase archiveWarehouseUseCase;

    @InjectMock
    WarehouseStore warehouseStore;

    @Test
    void archiveMarksWarehouseAsArchivedAndUpdatesStore() {
        // Given
        Warehouse warehouse = new Warehouse();
        warehouse.setLocation("ZWOLLE-001");
        warehouse.archived = false;

        // When
        archiveWarehouseUseCase.archive(warehouse);

        // Then
        ArgumentCaptor<Warehouse> captor = ArgumentCaptor.forClass(Warehouse.class);
        verify(warehouseStore, times(1)).update(captor.capture());

        Warehouse updated = captor.getValue();
        assertTrue(updated.archived, "Warehouse should be marked as archived");
    }
}
