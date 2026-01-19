package com.fulfilment.application.monolith.warehouses.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FulfillmentAssociationResponse {

    private Long id;
    private Long storeId;
    private Long productId;
    private String warehouseBusinessUnitCode;
    private LocalDateTime createdAt;
}
