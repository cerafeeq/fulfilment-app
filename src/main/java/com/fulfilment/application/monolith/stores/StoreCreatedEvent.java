package com.fulfilment.application.monolith.stores;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StoreCreatedEvent {
    private final Store store;

}
