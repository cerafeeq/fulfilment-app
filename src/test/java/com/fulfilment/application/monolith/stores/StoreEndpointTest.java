package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoreEndpointTest {

    private static final String PATH = "/store";

    @Test
    @Order(1)
    public void testGetAllStores() {
        given()
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body("$", hasSize(3))
                .body("[0].name", equalTo("Gothenburg Store"))
                .body("[1].name", equalTo("Malmo Store"))
                .body("[2].name", equalTo("Stockholm Store"));
    }

    @Test
    @Order(2)
    public void testGetSingleStore() {
        given()
                .when()
                .get(PATH + "/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Stockholm Store"))
                .body("quantityProductsInStock", equalTo(100));
    }

    @Test
    @Order(3)
    public void testGetNonExistentStore() {
        given()
                .when()
                .get(PATH + "/999")
                .then()
                .statusCode(404)
                .body("error", containsString("Store with id of 999 does not exist"));
    }

    @Test
    @Order(4)
    public void testCreateStore() {
        String newStore = """
                {
                    "name": "Uppsala Store",
                    "quantityProductsInStock": 120
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(newStore)
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .body("name", equalTo("Uppsala Store"))
                .body("quantityProductsInStock", equalTo(120))
                .body("id", notNullValue());

        // Verify the store was created
        given()
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body("$", hasSize(4));
    }

    @Test
    @Order(5)
    public void testCreateStoreWithInvalidId() {
        String storeWithId = """
                {
                    "id": 100,
                    "name": "Invalid Store",
                    "quantityProductsInStock": 50
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(storeWithId)
                .when()
                .post(PATH)
                .then()
                .statusCode(422)
                .body("error", containsString("Id was invalidly set on request"));

        // Verify the store was NOT created
        given()
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body("$", hasSize(4)); // Still 4 from previous test
    }

    @Test
    @Order(6)
    public void testUpdateStore() {
        String updatedStore = """
                {
                    "name": "Updated Stockholm Store",
                    "quantityProductsInStock": 200
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedStore)
                .when()
                .put(PATH + "/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Stockholm Store"))
                .body("quantityProductsInStock", equalTo(200));
    }

    @Test
    @Order(7)
    public void testUpdateNonExistentStore() {
        String updatedStore = """
                {
                    "name": "Non-existent Store",
                    "quantityProductsInStock": 100
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedStore)
                .when()
                .put(PATH + "/999")
                .then()
                .statusCode(404)
                .body("error", containsString("Store with id of 999 does not exist"));
    }

    @Test
    @Order(8)
    public void testUpdateStoreWithoutName() {
        String storeWithoutName = """
                {
                    "quantityProductsInStock": 150
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(storeWithoutName)
                .when()
                .put(PATH + "/1")
                .then()
                .statusCode(422)
                .body("error", containsString("Store Name was not set on request"));
    }

    @Test
    @Order(9)
    public void testUpdateStoreWithInvalidJson() {
        given()
                .contentType(ContentType.JSON)
                .body("invalid json")
                .when()
                .put(PATH + "/1")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(10)
    public void testPatchStore() {
        String patchData = """
                {
                    "name": "Patched Stockholm Store",
                    "quantityProductsInStock": 250
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(patchData)
                .when()
                .patch(PATH + "/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Patched Stockholm Store"))
                .body("quantityProductsInStock", equalTo(250));
    }

    @Test
    @Order(11)
    public void testPatchNonExistentStore() {
        String patchData = """
                {
                    "name": "Patched Store"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(patchData)
                .when()
                .patch(PATH + "/999")
                .then()
                .statusCode(404)
                .body("error", containsString("Store with id of 999 does not exist"));
    }

    @Test
    @Order(12)
    public void testDeleteStore() {
        // First create a store to delete
        String newStore = """
                {
                    "name": "Store to Delete",
                    "quantityProductsInStock": 50
                }
                """;

        int storeId = given()
                .contentType(ContentType.JSON)
                .body(newStore)
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Delete the store
        given()
                .when()
                .delete(PATH + "/" + storeId)
                .then()
                .statusCode(204);
        // Verify it's deleted
        given()
                .when()
                .get(PATH + "/" + storeId)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(13)
    public void testDeleteNonExistentStore() {
        given()
                .when()
                .delete(PATH + "/999")
                .then()
                .statusCode(404)
                .body("error", containsString("Store with id of 999 does not exist"));
    }

    @Test
    @Order(14)
    public void testFullCrudWorkflow() {
        // Create
        String newStore = """
                {
                    "name": "CRUD Test Store",
                    "quantityProductsInStock": 100
                }
                """;

        int storeId = given()
                .contentType(ContentType.JSON)
                .body(newStore)
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .body("name", equalTo("CRUD Test Store"))
                .extract()
                .path("id");
        // Read
        given()
                .when()
                .get(PATH + "/" + storeId)
                .then()
                .statusCode(200)
                .body("name", equalTo("CRUD Test Store"))
                .body("quantityProductsInStock", equalTo(100));

        // Update
        String updatedStore = """
                {
                    "name": "Updated CRUD Test Store",
                    "quantityProductsInStock": 150
                }
                """;
        given()
                .contentType(ContentType.JSON)
                .body(updatedStore)
                .when()
                .put(PATH + "/" + storeId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated CRUD Test Store"))
                .body("quantityProductsInStock", equalTo(150));

        // Delete
        given()
                .when()
                .delete(PATH + "/" + storeId)
                .then()
                .statusCode(204);

        // Verify deletion
        given()
                .when()
                .get(PATH + "/" + storeId)
                .then()
                .statusCode(404);
    }
}
