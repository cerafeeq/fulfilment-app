package com.fulfilment.application.monolith.stores;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Cacheable
@Getter
@Setter
public class Store extends PanacheEntity {

  @Column(length = 40, unique = true)
  @NotNull
  private String name;

  @Min(0)
  private int quantityProductsInStock;

  public Store() {}

  public Store(String name) {
    this.name = name;
  }
}
