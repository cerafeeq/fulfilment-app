package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

import java.util.logging.Logger;

@ApplicationScoped
public class StoreLegacySystemSyncService {

    @Inject
    LegacyStoreManagerGateway legacyStoreManagerGateway;

    private static final Logger LOGGER = Logger.getLogger(StoreLegacySystemSyncService.class.getName());

    public void onStoreCreated(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoreCreatedEvent event) {
        try {
            legacyStoreManagerGateway.createStoreOnLegacySystem(event.getStore());
        } catch (Exception e) {
            LOGGER.severe("Failed to sync created store to legacy system: " + e.getMessage());
            // Consider implementing compensation logic or retry mechanism here
        }
    }

    public void onStoreUpdated(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoreUpdatedEvent event) {
        try {
            legacyStoreManagerGateway.updateStoreOnLegacySystem(event.getStore());
        } catch (Exception e) {
            LOGGER.severe("Failed to sync updated store to legacy system: " + e.getMessage());
            // Consider implementing compensation logic or retry mechanism here
        }
    }
}
