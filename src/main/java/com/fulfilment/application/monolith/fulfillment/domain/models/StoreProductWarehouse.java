package com.fulfilment.application.monolith.fulfillment.domain.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "store_product_warehouse",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"store_id", "product_id", "warehouse_business_unit_code"},
                        name = "uk_store_product_warehouse"
                )
        },
        indexes = {
                @Index(name = "idx_store_product", columnList = "store_id, product_id"),
                @Index(name = "idx_store_warehouse", columnList = "store_id, warehouse_business_unit_code"),
                @Index(name = "idx_warehouse_product", columnList = "warehouse_business_unit_code, product_id")
        }
)
@Cacheable
@Getter
@Setter
public class StoreProductWarehouse extends PanacheEntity {

    @NotNull
    @Column(name = "store_id")
    private Long storeId;

    @NotNull
    @Column(name = "product_id")
    private Long productId;

    @NotNull
    @Column(name = "warehouse_business_unit_code", length = 50)
    private String warehouseBusinessUnitCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public StoreProductWarehouse() {}

    public StoreProductWarehouse(Long storeId, Long productId, String warehouseBusinessUnitCode) {
        this.storeId = storeId;
        this.productId = productId;
        this.warehouseBusinessUnitCode = warehouseBusinessUnitCode;
    }
}
