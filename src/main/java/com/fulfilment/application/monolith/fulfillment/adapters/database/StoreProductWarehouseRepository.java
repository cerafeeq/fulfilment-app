package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.models.StoreProductWarehouse;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class StoreProductWarehouseRepository implements PanacheRepository<StoreProductWarehouse> {

    public List<StoreProductWarehouse> findByStoreAndProduct(Long storeId, Long productId) {
        return list("storeId = ?1 and productId = ?2", storeId, productId);
    }

    public long countByStoreAndProduct(Long storeId, Long productId) {
        return count("storeId = ?1 and productId = ?2", storeId, productId);
    }

    public long countByStore(Long storeId) {
        return find("select distinct warehouseBusinessUnitCode from StoreProductWarehouse where storeId = ?1", storeId)
                .count();
    }

    public long countDistinctWarehousesByStore(Long storeId) {
        return getEntityManager()
                .createQuery("SELECT COUNT(DISTINCT spw.warehouseBusinessUnitCode) FROM StoreProductWarehouse spw WHERE spw.storeId = :storeId", Long.class)
                .setParameter("storeId", storeId)
                .getSingleResult();
    }

    public long countProductsByWarehouse(String warehouseBusinessUnitCode) {
        return getEntityManager()
                .createQuery("SELECT COUNT(DISTINCT spw.productId) FROM StoreProductWarehouse spw WHERE spw.warehouseBusinessUnitCode = :warehouseCode", Long.class)
                .setParameter("warehouseCode", warehouseBusinessUnitCode)
                .getSingleResult();
    }

    public boolean exists(Long storeId, Long productId, String warehouseBusinessUnitCode) {
        return count("storeId = ?1 and productId = ?2 and warehouseBusinessUnitCode = ?3",
                storeId, productId, warehouseBusinessUnitCode) > 0;
    }

    public List<StoreProductWarehouse> findByStore(Long storeId) {
        return list("storeId = ?1", storeId);
    }

    public List<StoreProductWarehouse> findByProduct(Long productId) {
        return list("productId = ?1", productId);
    }

    public List<StoreProductWarehouse> findByWarehouse(String warehouseBusinessUnitCode) {
        return list("warehouseBusinessUnitCode = ?1", warehouseBusinessUnitCode);
    }

    public void deleteByStoreAndProductAndWarehouse(Long storeId, Long productId, String warehouseBusinessUnitCode) {
        delete("storeId = ?1 and productId = ?2 and warehouseBusinessUnitCode = ?3",
                storeId, productId, warehouseBusinessUnitCode);
    }
}
