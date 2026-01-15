package com.fulfilment.application.monolith.location;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LocationGatewayTest {

  @Test
  public void testWhenResolveExistingLocationShouldReturn() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when
    Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");

    // then
    assertNotNull(location);
    assertEquals("ZWOLLE-001", location.getIdentification());
    assertEquals(1, location.getMaxNumberOfWarehouses());
    assertEquals(40, location.getMaxCapacity());
  }

  @Test
  public void testWhenResolveNonExistingLocationShouldReturnNull() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when
    Location location = locationGateway.resolveByIdentifier("NONEXISTENT-001");

    // then
    assertNull(location);
  }

  @Test
  public void testWhenResolveAllLocationsFromStaticList() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when & then
    assertNotNull(locationGateway.resolveByIdentifier("ZWOLLE-001"));
    assertNotNull(locationGateway.resolveByIdentifier("ZWOLLE-002"));
    assertNotNull(locationGateway.resolveByIdentifier("AMSTERDAM-001"));
    assertNotNull(locationGateway.resolveByIdentifier("AMSTERDAM-002"));
    assertNotNull(locationGateway.resolveByIdentifier("TILBURG-001"));
    assertNotNull(locationGateway.resolveByIdentifier("HELMOND-001"));
    assertNotNull(locationGateway.resolveByIdentifier("EINDHOVEN-001"));
    assertNotNull(locationGateway.resolveByIdentifier("VETSBY-001"));
  }

  @Test
  public void testLocationPropertiesAreCorrect() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when
    Location amsterdam001 = locationGateway.resolveByIdentifier("AMSTERDAM-001");

    // then
    assertNotNull(amsterdam001);
    assertEquals("AMSTERDAM-001", amsterdam001.getIdentification());
    assertEquals(5, amsterdam001.getMaxNumberOfWarehouses());
    assertEquals(100, amsterdam001.getMaxCapacity());
  }

  @Test
  public void testWhenResolveWithNullIdentifierShouldReturnNull() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when
    Location location = locationGateway.resolveByIdentifier(null);

    // then
    assertNull(location);
  }

  @Test
  public void testWhenResolveWithEmptyIdentifierShouldReturnNull() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when
    Location location = locationGateway.resolveByIdentifier("");

    // then
    assertNull(location);
  }
}
