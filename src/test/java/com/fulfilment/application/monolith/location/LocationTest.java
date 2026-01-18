package com.fulfilment.application.monolith.location;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LocationTest {

    @Test
    void testConstructor() {
        // Given
        String identification = "ZWOLLE-001";
        int maxWarehouses = 5;
        int maxCapacity = 1000;

        // When
        Location location = new Location(identification, maxWarehouses, maxCapacity);

        // Then
        assertEquals(identification, location.getIdentification());
        assertEquals(maxWarehouses, location.getMaxNumberOfWarehouses());
        assertEquals(maxCapacity, location.getMaxCapacity());
    }

    @Test
    void testGettersAndSetters() {
        // Given
        Location location = new Location("ZWOLLE-001", 5, 1000);

        // When
        location.setIdentification("AMSTERDAM-001");
        location.setMaxNumberOfWarehouses(10);
        location.setMaxCapacity(2000);

        // Then
        assertEquals("AMSTERDAM-001", location.getIdentification());
        assertEquals(10, location.getMaxNumberOfWarehouses());
        assertEquals(2000, location.getMaxCapacity());
    }

    @Test
    void testEquals_SameObject() {
        // Given
        Location location = new Location("ZWOLLE-001", 5, 1000);

        // Then
        assertEquals(location, location);
    }

    @Test
    void testEquals_EqualObjects() {
        // Given
        Location location1 = new Location("ZWOLLE-001", 5, 1000);
        Location location2 = new Location("ZWOLLE-001", 5, 1000);

        // Then
        assertEquals(location1, location2);
    }

    @Test
    void testEquals_DifferentIdentification() {
        // Given
        Location location1 = new Location("ZWOLLE-001", 5, 1000);
        Location location2 = new Location("AMSTERDAM-001", 5, 1000);

        // Then
        assertNotEquals(location1, location2);
    }

    @Test
    void testEquals_DifferentMaxWarehouses() {
        // Given
        Location location1 = new Location("ZWOLLE-001", 5, 1000);
        Location location2 = new Location("ZWOLLE-001", 10, 1000);

        // Then
        assertNotEquals(location1, location2);
    }

    @Test
    void testEquals_DifferentMaxCapacity() {
        // Given
        Location location1 = new Location("ZWOLLE-001", 5, 1000);
        Location location2 = new Location("ZWOLLE-001", 5, 2000);

        // Then
        assertNotEquals(location1, location2);
    }

    @Test
    void testEquals_Null() {
        // Given
        Location location = new Location("ZWOLLE-001", 5, 1000);

        // Then
        assertNotEquals(null, location);
    }

    @Test
    void testEquals_DifferentClass() {
        // Given
        Location location = new Location("ZWOLLE-001", 5, 1000);
        String notALocation = "Not a location";

        // Then
        assertNotEquals(location, notALocation);
    }

    @Test
    void testHashCode_EqualObjects() {
        // Given
        Location location1 = new Location("ZWOLLE-001", 5, 1000);
        Location location2 = new Location("ZWOLLE-001", 5, 1000);

        // Then
        assertEquals(location1.hashCode(), location2.hashCode());
    }

    @Test
    void testHashCode_DifferentObjects() {
        // Given
        Location location1 = new Location("ZWOLLE-001", 5, 1000);
        Location location2 = new Location("AMSTERDAM-001", 5, 1000);

        // Then
        assertNotEquals(location1.hashCode(), location2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        Location location = new Location("ZWOLLE-001", 5, 1000);

        // When
        String result = location.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("ZWOLLE-001"));
        assertTrue(result.contains("5"));
        assertTrue(result.contains("1000"));
    }
}