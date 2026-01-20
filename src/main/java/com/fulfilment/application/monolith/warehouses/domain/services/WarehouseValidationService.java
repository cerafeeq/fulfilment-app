package com.fulfilment.application.monolith.warehouses.domain.services;

import com.fulfilment.application.monolith.fulfillment.adapters.database.StoreProductWarehouseRepository;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class WarehouseValidationService {
    private static final int MAX_WAREHOUSES_PER_PRODUCT_PER_STORE = 2;
    private static final int MAX_WAREHOUSES_PER_STORE = 3;
    private static final int MAX_PRODUCTS_PER_WAREHOUSE = 5;

    @Inject private WarehouseRepository warehouseRepository;
    @Inject private LocationResolver locationResolver;
    @Inject private StoreProductWarehouseRepository fulfillmentRepository;

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

    public void validateFulfillmentAssociation(Long storeId, Long productId, String warehouseBusinessUnitCode) {
        validateWarehouseExistsAndActive(warehouseBusinessUnitCode);
        validateAssociationDoesNotExist(storeId, productId, warehouseBusinessUnitCode);
        validateWarehousesPerProductPerStore(storeId, productId);
        validateWarehousesPerStore(storeId, warehouseBusinessUnitCode);
        validateProductsPerWarehouse(warehouseBusinessUnitCode, productId);
    }

    private void validateWarehouseExistsAndActive(String warehouseBusinessUnitCode) {
        var warehouse = warehouseRepository.findActiveByBusinessUnitCode(warehouseBusinessUnitCode);
        if (warehouse == null || warehouse.archivedAt != null) {
            throw new WarehouseValidationException(
                    "Warehouse with business unit code '" + warehouseBusinessUnitCode + "' does not exist or is archived");
        }
    }

    private void validateAssociationDoesNotExist(Long storeId, Long productId, String warehouseBusinessUnitCode) {
        if (fulfillmentRepository.exists(storeId, productId, warehouseBusinessUnitCode)) {
            throw new WarehouseValidationException(
                    "Association already exists for Store " + storeId + ", Product " + productId +
                            ", and Warehouse " + warehouseBusinessUnitCode);
        }
    }

    private void validateWarehousesPerProductPerStore(Long storeId, Long productId) {
        // Constraint 1: Each Product can be fulfilled by a maximum of 2 different Warehouses per Store
        long warehousesForProductInStore = fulfillmentRepository.countByStoreAndProduct(storeId, productId);
        if (warehousesForProductInStore >= MAX_WAREHOUSES_PER_PRODUCT_PER_STORE) {
            throw new WarehouseValidationException(
                    "Product " + productId + " in Store " + storeId + " already has the maximum of "
                            + MAX_WAREHOUSES_PER_PRODUCT_PER_STORE + " warehouses");
        }
    }

    private void validateWarehousesPerStore(Long storeId, String warehouseBusinessUnitCode) {
        // Constraint 2: Each Store can be fulfilled by a maximum of 3 different Warehouses
        long distinctWarehousesForStore = fulfillmentRepository.countDistinctWarehousesByStore(storeId);
        boolean warehouseAlreadyAssociatedWithStore = fulfillmentRepository
                .findByStore(storeId)
                .stream()
                .anyMatch(spw -> spw.getWarehouseBusinessUnitCode().equals(warehouseBusinessUnitCode));

        if (!warehouseAlreadyAssociatedWithStore && distinctWarehousesForStore >= MAX_WAREHOUSES_PER_STORE) {
            throw new WarehouseValidationException(
                    "Store " + storeId + " already has the maximum of " + MAX_WAREHOUSES_PER_STORE + " different warehouses");
        }
    }

    private void validateProductsPerWarehouse(String warehouseBusinessUnitCode, Long productId) {
        // Constraint 3: Each Warehouse can store maximally 5 types of Products
        long productsInWarehouse = fulfillmentRepository.countProductsByWarehouse(warehouseBusinessUnitCode);
        boolean productAlreadyInWarehouse = fulfillmentRepository
                .findByWarehouse(warehouseBusinessUnitCode)
                .stream()
                .anyMatch(spw -> spw.getProductId().equals(productId));

        if (!productAlreadyInWarehouse && productsInWarehouse >= MAX_PRODUCTS_PER_WAREHOUSE) {
            throw new WarehouseValidationException(
                    "Warehouse " + warehouseBusinessUnitCode + " already stores the maximum of "
                            + MAX_PRODUCTS_PER_WAREHOUSE + " different products");
        }
    }
}
