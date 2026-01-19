package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.products.ProductResource;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {
  private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseRepository.class);

  @Override
  public List<Warehouse> getAll() {
    return find("archivedAt is null")  // Only return non-archived warehouses
            .stream()
            .map(DbWarehouse::toWarehouse)
            .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void create(Warehouse warehouse) {
    DbWarehouse dbWarehouse = DbWarehouse.fromWarehouse(warehouse);
    this.persist(dbWarehouse);
    LOGGER.info("Warehouse {} created successfully", warehouse);
    // Update the warehouse with the generated ID
    warehouse.id = dbWarehouse.id;
  }

  @Override
  @Transactional
  public void update(Warehouse warehouse) {
    DbWarehouse dbWarehouse = this.find("id", warehouse.id).firstResult();
    if (dbWarehouse == null) {
      throw new IllegalArgumentException("Warehouse with id " + warehouse.id + " not found");
    }

    dbWarehouse.businessUnitCode = warehouse.businessUnitCode;
    dbWarehouse.location = warehouse.location;
    dbWarehouse.capacity = warehouse.capacity;
    dbWarehouse.stock = warehouse.stock;
    dbWarehouse.createdAt = warehouse.createdAt;
    dbWarehouse.archivedAt = warehouse.archivedAt;

    this.persist(dbWarehouse);
    LOGGER.info("Warehouse {} updated successfully", warehouse);
  }

  @Override
  @Transactional
  public void remove(Warehouse warehouse) {
    DbWarehouse dbWarehouse = this.find("id", warehouse.id).firstResult();
    if (dbWarehouse != null) {
      this.delete(dbWarehouse);
    }
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse dbWarehouse = find("businessUnitCode", buCode).firstResult();
    return dbWarehouse != null ? dbWarehouse.toWarehouse() : null;
  }

  public Warehouse findActiveByBusinessUnitCode(String buCode) {
    DbWarehouse dbWarehouse = find("businessUnitCode = ?1 and archivedAt is null", buCode).firstResult();
    return dbWarehouse != null ? dbWarehouse.toWarehouse() : null;
  }

  // Additional helper methods needed for validation
  public boolean existsByBusinessUnitCode(String businessUnitCode) {
    return count("businessUnitCode = ?1 and archivedAt is null", businessUnitCode) > 0;
  }

  public int countByLocation(String location) {
    return (int) count("location = ?1 and archivedAt is null", location);
  }

  public Warehouse findById(String id) {
    DbWarehouse dbWarehouse = this.find("id", id).firstResult();
    return dbWarehouse != null ? dbWarehouse.toWarehouse() : null;
  }

  @Transactional
  public void save(Warehouse warehouse) {
    if (warehouse.id == null) {
      create(warehouse);
    } else {
      update(warehouse);
    }
  }
}
