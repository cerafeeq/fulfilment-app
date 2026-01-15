package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.services.WarehouseValidationService;
import com.warehouse.api.WarehousesResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehousesResource {

  @Inject private WarehouseRepository warehouseRepository;
  @Inject private WarehouseValidationService validationService;

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @Override
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    try {
      // Validate business unit code uniqueness
      validationService.validateBusinessUnitCodeUniqueness(data.getBusinessUnitCode());

      // Validate location
      validationService.validateLocation(data.getLocation());

      // Validate warehouse creation feasibility
      validationService.validateWarehouseCreationFeasibility(data.getLocation());

      // Validate capacity and stock
      validationService.validateCapacityAndStock(
              data.getCapacity(), data.getStock(), data.getLocation());

      // Create warehouse entity
      var warehouse = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
      //warehouse.id = UUID.randomUUID().toString();
      warehouse.businessUnitCode = data.getBusinessUnitCode();
      warehouse.location = data.getLocation();
      warehouse.capacity = data.getCapacity();
      warehouse.stock = data.getStock();
      warehouse.archived = false;

      warehouseRepository.create(warehouse);

      return toWarehouseResponse(warehouse);
    } catch (WarehouseValidationException e) {
      throw  new WebApplicationException(e.getMessage(), Response.Status.BAD_REQUEST);
    }
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    var warehouse = warehouseRepository.findActiveByBusinessUnitCode(id);

    if (warehouse == null || warehouse.archivedAt != null) {
      throw new WebApplicationException(
              "Warehouse with business unit code '" + id + "' not found",
              Response.Status.NOT_FOUND);
    }

    return toWarehouseResponse(warehouse);
  }

  @Override
  @Transactional
  public void archiveAWarehouseUnitByID(String id) {
    var warehouse = warehouseRepository.findByBusinessUnitCode(id);

    if (warehouse == null || warehouse.archivedAt != null) {
      throw new WebApplicationException(
              "Warehouse with business unit code '" + id + "' not found",
              Response.Status.NOT_FOUND);
    }

    warehouse.archivedAt = LocalDateTime.now();
    warehouseRepository.update(warehouse);
  }

  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(String businessUnitCode, @NotNull Warehouse data) {
    try {
      // Find existing warehouse by business unit code
      var existingWarehouse = warehouseRepository.findByBusinessUnitCode(businessUnitCode);

      if (existingWarehouse == null || existingWarehouse.archivedAt != null) {
        throw new WebApplicationException(
                "Active warehouse with business unit code '" + businessUnitCode + "' not found",
                Response.Status.NOT_FOUND);  // This will return 404
      }

      // Validate location
      validationService.validateLocation(data.getLocation());

      // Validate warehouse creation feasibility at new location
      validationService.validateWarehouseCreationFeasibility(data.getLocation());

      // Validate capacity can accommodate existing stock
      validationService.validateReplacementCapacity(data.getCapacity(), existingWarehouse.stock);

      // Validate stock matching
      validationService.validateStockMatching(data.getStock(), existingWarehouse.stock);

      // Validate capacity and stock for the new location
      validationService.validateCapacityAndStock(
              data.getCapacity(), data.getStock(), data.getLocation());

      // Archive the existing warehouse
      existingWarehouse.archivedAt = LocalDateTime.now();
      warehouseRepository.update(existingWarehouse);

      // Create new warehouse
      var newWarehouse = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
      newWarehouse.businessUnitCode = businessUnitCode;
      newWarehouse.location = data.getLocation();
      newWarehouse.capacity = data.getCapacity();
      newWarehouse.stock = data.getStock();
      newWarehouse.createdAt = LocalDateTime.now();
      newWarehouse.archivedAt = null;

      warehouseRepository.save(newWarehouse);

      return toWarehouseResponse(newWarehouse);

    } catch (WarehouseValidationException | WarehouseNotFoundException e) {
      throw new WebApplicationException(e.getMessage(), Response.Status.BAD_REQUEST);
    }
  }

  private Warehouse toWarehouseResponse(com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);

    return response;
  }
}
