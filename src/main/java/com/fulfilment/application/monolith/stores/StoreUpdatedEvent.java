package com.fulfilment.application.monolith.stores;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StoreUpdatedEvent {
    private final Store store;

}
