package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public CreateWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void create(Warehouse warehouse) {
    if (warehouse.getLocation() == null) {
      throw new IllegalArgumentException("Warehouse location is required");
    }

    // Validate business rules (example)
    if (warehouse.getCapacity() != null && warehouse.getCapacity() <= 0) {
      throw new IllegalArgumentException("Warehouse capacity must be positive");
    }

    // Set default values if needed
    if (warehouse.isArchived()) {
      warehouse.setArchivedAt(null);
    }

    // if all went well, create the warehouse
    warehouseStore.create(warehouse);
  }
}
