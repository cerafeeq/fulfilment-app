package com.fulfilment.application.monolith.fulfillment.adapters.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FulfillmentAssociationRequest {

    @NotNull(message = "Store ID is required")
    private Long storeId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Warehouse business unit code is required")
    private String warehouseBusinessUnitCode;
}
