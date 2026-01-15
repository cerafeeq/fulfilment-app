package com.fulfilment.application.monolith.warehouses.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Warehouse {

  // Primary Key
  public String id;

  // unique identifier
  public String businessUnitCode;

  public String location;

  public Integer capacity;

  public Integer stock;

  public boolean archived;

  public LocalDateTime createdAt;

  public LocalDateTime archivedAt;
}
