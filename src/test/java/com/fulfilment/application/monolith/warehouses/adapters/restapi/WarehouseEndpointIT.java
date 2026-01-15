package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

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
}
