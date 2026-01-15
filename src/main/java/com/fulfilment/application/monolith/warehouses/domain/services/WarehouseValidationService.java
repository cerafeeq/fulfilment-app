package com.fulfilment.application.monolith.warehouses.domain.services;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class WarehouseValidationService {

    @Inject private WarehouseRepository warehouseRepository;
    @Inject private LocationResolver locationResolver;

    public void validateBusinessUnitCodeUniqueness(String businessUnitCode) {
        if (warehouseRepository.existsByBusinessUnitCode(businessUnitCode)) {
            throw new WarehouseValidationException(
                    "Warehouse with business unit code '" + businessUnitCode + "' already exists");
        }
    }

    public Location validateLocation(String locationIdentifier) {
        Location location = locationResolver.resolveByIdentifier(locationIdentifier);
        if (location == null) {
            throw new WarehouseValidationException(
                    "Invalid location: '" + locationIdentifier + "' does not exist");
        }
        return location;
    }

    public void validateWarehouseCreationFeasibility(String locationIdentifier) {
        Location location = validateLocation(locationIdentifier);
        int existingWarehousesAtLocation = warehouseRepository.countByLocation(locationIdentifier);

        if (existingWarehousesAtLocation >= location.getMaxNumberOfWarehouses()) {
            throw new WarehouseValidationException(
                    "Cannot create warehouse at location '"
                            + locationIdentifier
                            + "'. Maximum number of warehouses ("
                            + location.getMaxNumberOfWarehouses()
                            + ") already reached");
        }
    }

    public void validateCapacityAndStock(Integer capacity, Integer stock, String locationIdentifier) {
        Location location = validateLocation(locationIdentifier);

        if (capacity > location.getMaxCapacity()) {
            throw new WarehouseValidationException(
                    "Warehouse capacity ("
                            + capacity
                            + ") exceeds maximum capacity for location ("
                            + location.getMaxCapacity()
                            + ")");
        }

        if (stock > capacity) {
            throw new WarehouseValidationException(
                    "Stock (" + stock + ") cannot exceed warehouse capacity (" + capacity + ")");
        }
    }

    public void validateReplacementCapacity(Integer newCapacity, Integer existingStock) {
        if (newCapacity < existingStock) {
            throw new WarehouseValidationException(
                    "New warehouse capacity ("
                            + newCapacity
                            + ") cannot accommodate existing stock ("
                            + existingStock
                            + ")");
        }
    }

    public void validateStockMatching(Integer newStock, Integer existingStock) {
        if (!newStock.equals(existingStock)) {
            throw new WarehouseValidationException(
                    "New warehouse stock ("
                            + newStock
                            + ") must match existing warehouse stock ("
                            + existingStock
                            + ")");
        }
    }
}
