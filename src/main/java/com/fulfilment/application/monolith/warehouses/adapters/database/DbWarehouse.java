package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "warehouse")
@Cacheable
public class DbWarehouse extends PanacheEntityBase {

  @Id
  @Column(name = "id")
  public String id;

  @Column(name = "businessUnitCode")
  public String businessUnitCode;

  @Column(name = "location")
  public String location;

  @Column(name = "capacity")
  public Integer capacity;

  @Column(name = "stock")
  public Integer stock;

  @Column(name = "createdAt")
  public LocalDateTime createdAt;

  @Column(name = "archivedAt")
  public LocalDateTime archivedAt;

  public DbWarehouse() {}

  public static DbWarehouse fromWarehouse(Warehouse warehouse) {
    DbWarehouse dbWarehouse = new DbWarehouse();
    dbWarehouse.id = UUID.randomUUID().toString();
    dbWarehouse.businessUnitCode = warehouse.businessUnitCode;
    dbWarehouse.location = warehouse.location;
    dbWarehouse.capacity = warehouse.capacity;
    dbWarehouse.stock = warehouse.stock;
    dbWarehouse.createdAt = warehouse.createdAt;
    dbWarehouse.archivedAt = warehouse.archivedAt;
    return dbWarehouse;
  }

  public Warehouse toWarehouse() {
    var warehouse = new Warehouse();
    warehouse.businessUnitCode = this.businessUnitCode;
    warehouse.location = this.location;
    warehouse.capacity = this.capacity;
    warehouse.stock = this.stock;
    warehouse.createdAt = this.createdAt;
    warehouse.archivedAt = this.archivedAt;
    return warehouse;
  }
}
