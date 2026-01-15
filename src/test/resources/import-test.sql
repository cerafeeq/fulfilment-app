-- Test data for stores
INSERT INTO store(id, name, quantityProductsInStock) VALUES (1, 'Stockholm Store', 100);
INSERT INTO store(id, name, quantityProductsInStock) VALUES (2, 'Gothenburg Store', 150);
INSERT INTO store(id, name, quantityProductsInStock) VALUES (3, 'Malmo Store', 75);
ALTER SEQUENCE store_seq RESTART WITH 4;

-- Test data for products
INSERT INTO product(id, name, stock) VALUES (1, 'TONSTAD', 100);
INSERT INTO product(id, name, stock) VALUES (2, 'KALLAX', 150);
INSERT INTO product(id, name, stock) VALUES (3, 'BESTÅ', 75);
ALTER SEQUENCE product_seq RESTART WITH 4;

-- Test data for warehouses
INSERT INTO warehouse(id, businessUnitCode, location, capacity, stock, createdAt, archivedAt)
VALUES ('1', 'MWH.001', 'ZWOLLE-001', 100, 10, '2024-01-01', null);

INSERT INTO warehouse(id, businessUnitCode, location, capacity, stock, createdAt, archivedAt)
VALUES ('2', 'MWH.012', 'AMSTERDAM-001', 100, 50, '2024-01-01', null);

INSERT INTO warehouse(id, businessUnitCode, location, capacity, stock, createdAt, archivedAt)
VALUES ('3', 'MWH.023', 'TILBURG-001', 40, 30, '2024-01-01', null);