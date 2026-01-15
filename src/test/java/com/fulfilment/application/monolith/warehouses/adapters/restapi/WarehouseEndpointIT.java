package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.not;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WarehouseEndpointIT {
  private static final String PATH = "warehouses";

  @Test
  @Order(1)
  public void testListAllWarehouses() {
    // List all warehouses - should have 3 from import.sql
    given()
            .when()
            .get(PATH)
            .then()
            .statusCode(200)
            .body(containsString("MWH.001"))   // Separate assertions
            .body(containsString("MWH.012"))
            .body(containsString("MWH.023"))
            .body(containsString("ZWOLLE-001"))
            .body(containsString("AMSTERDAM-001"))
            .body(containsString("TILBURG-001"));
  }

  @Test
  @Order(2)
  public void testGetWarehouseByBusinessUnitCode() {
    // Get a single warehouse by business unit code
    given()
            .when()
            .get(PATH + "/MWH.001")
            .then()
            .statusCode(200)
            .body(containsString("MWH.001"))
            .body(containsString("ZWOLLE-001"))
            .body(containsString("\"capacity\":100"))
            .body(containsString("\"stock\":10"));
  }

  @Test
  @Order(3)
  public void testGetNonExistentWarehouse() {
    // Try to get a warehouse that doesn't exist
    given()
            .when()
            .get(PATH + "/NONEXISTENT-999")
            .then()
            .statusCode(404);
  }

  @Test
  @Order(4)
  public void testCreateNewWarehouse() {
    // Create a new warehouse with a unique business unit code
    String newWarehouse = """
        {
          "businessUnitCode": "NEW-WH-999",
          "location": "EINDHOVEN-001",
          "capacity": 70,
          "stock": 35
        }
        """;

    given()
            .contentType(ContentType.JSON)
            .body(newWarehouse)
            .when()
            .post(PATH)
            .then()
            .statusCode(200)
            .body(containsString("NEW-WH-999"))
            .body(containsString("EINDHOVEN-001"))
            .body(containsString("\"capacity\":70"))
            .body(containsString("\"stock\":35"));

    // Verify the warehouse was created
    given()
            .when()
            .get(PATH)
            .then()
            .statusCode(200)
            .body(containsString("NEW-WH-999"));
  }

  @Test
  @Order(5)
  public void testCreateWarehouseWithDuplicateBusinessUnitCode() {
    // Try to create a warehouse with a duplicate business unit code
    String duplicateWarehouse = """
        {
          "businessUnitCode": "MWH.001",
          "location": "ROTTERDAM-001",
          "capacity": 80,
          "stock": 40
        }
        """;

    given()
            .contentType(ContentType.JSON)
            .body(duplicateWarehouse)
            .when()
            .post(PATH)
            .then()
            .statusCode(400)
            .body(containsString("already exists"));
  }

  @Test
  @Order(6)
  public void testCreateWarehouseWithInvalidLocation() {
    // Try to create a warehouse with an invalid location
    String invalidLocationWarehouse = """
        {
          "businessUnitCode": "TEST-INVALID-001",
          "location": "INVALID-LOCATION",
          "capacity": 50,
          "stock": 25
        }
        """;

    given()
            .contentType(ContentType.JSON)
            .body(invalidLocationWarehouse)
            .when()
            .post(PATH)
            .then()
            .statusCode(400);
  }

  @Test
  @Order(7)
  public void testCreateWarehouseWithStockExceedingCapacity() {
    // Try to create a warehouse where stock exceeds capacity
    String invalidStockWarehouse = """
        {
          "businessUnitCode": "TEST-INVALID-002",
          "location": "AMSTERDAM-001",
          "capacity": 50,
          "stock": 100
        }
        """;

    given()
            .contentType(ContentType.JSON)
            .body(invalidStockWarehouse)
            .when()
            .post(PATH)
            .then()
            .statusCode(400)
            .body(containsString("Stock"));
  }

  @Test
  @Order(8)
  public void testArchiveWarehouse() {
    // Archive a warehouse
    given()
            .when()
            .delete(PATH + "/MWH.023")
            .then()
            .statusCode(204);

    // Verify the warehouse is archived (should not appear in list)
    given()
            .when()
            .get(PATH)
            .then()
            .statusCode(200)
            .body(not(containsString("MWH.023")));

    // Verify we can't retrieve the archived warehouse
    given()
            .when()
            .get(PATH + "/MWH.023")
            .then()
            .statusCode(404);
  }

  @Test
  @Order(9)
  public void testArchiveNonExistentWarehouse() {
    // Try to archive a warehouse that doesn't exist
    given()
            .when()
            .delete(PATH + "/NONEXISTENT-999")
            .then()
            .statusCode(404);
  }

  @Test
  @Order(10)
  public void testArchiveAlreadyArchivedWarehouse() {
    // Try to archive an already archived warehouse (MWH.023 was archived in test 8)
    given()
            .when()
            .delete(PATH + "/MWH.023")
            .then()
            .statusCode(404);  // Should be 404 since archived warehouses are not found
  }

  @Test
  @Order(11)
  public void testReplaceWarehouse() {
    // Replace an existing warehouse
    String replacementWarehouse = """
        {
          "businessUnitCode": "MWH.001",
          "location": "AMSTERDAM-001",
          "capacity": 50,
          "stock": 10
        }
        """;

    given()
            .contentType(ContentType.JSON)
            .body(replacementWarehouse)
            .when()
            .put(PATH + "/MWH.001/replace")
            .then()
            .statusCode(200)
            .body(containsString("MWH.001"))
            .body(containsString("AMSTERDAM-001"))
            .body(containsString("\"capacity\":50"))
            .body(containsString("\"stock\":10"));

    // Verify the replacement
    given()
            .when()
            .get(PATH + "/MWH.001")
            .then()
            .statusCode(200)
            .body(containsString("AMSTERDAM-001"))
            .body(not(containsString("ZWOLLE-001")));
  }

  @Test
  @Order(12)
  public void testReplaceNonExistentWarehouse() {
    // Try to replace a warehouse that doesn't exist
    String replacementWarehouse = """
        {
          "businessUnitCode": "NONEXISTENT-999",
          "location": "TILBURG-001",
          "capacity": 100,
          "stock": 50
        }
        """;

    given()
            .contentType(ContentType.JSON)
            .body(replacementWarehouse)
            .when()
            .put(PATH + "/NONEXISTENT-999/replace")
            .then()
            .statusCode(404);
  }

  @Test
  @Order(13)
  public void testReplaceWarehouseWithInsufficientCapacity() {
    // Try to replace a warehouse with capacity less than current stock
    String replacementWarehouse = """
        {
          "businessUnitCode": "NEW-WH-999",
          "location": "EINDHOVEN-001",
          "capacity": 20,
          "stock": 35
        }
        """;

    given()
            .contentType(ContentType.JSON)
            .body(replacementWarehouse)
            .when()
            .put(PATH + "/NEW-WH-999/replace")
            .then()
            .statusCode(400)
            .body(containsString("capacity"));
  }

  @Test
  @Order(14)
  public void testReplaceWarehouseWithMismatchedStock() {
    // Try to replace a warehouse with stock that doesn't match current stock
    String replacementWarehouse = """
        {
          "businessUnitCode": "NEW-WH-999",
          "location": "TILBURG-001",
          "capacity": 100,
          "stock": 30
        }
        """;

    given()
            .contentType(ContentType.JSON)
            .body(replacementWarehouse)
            .when()
            .put(PATH + "/NEW-WH-999/replace")
            .then()
            .statusCode(400)
            .body(containsString("stock"));
  }
}
