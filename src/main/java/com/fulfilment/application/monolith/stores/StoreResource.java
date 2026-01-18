package com.fulfilment.application.monolith.stores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import org.jboss.logging.Logger;
import jakarta.enterprise.event.Event;

@Path("store")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class StoreResource {
  private static final String STORE_NOT_FOUND = "Store with id of %d does not exist.";
  private static final String STORE_NOT_SET = "Store Name was not set on request.";
  private static final String INVALID_ID = "Id was invalidly set on request.";
  public static final int UNPROCESSABLE_ENTITY = 422;

  @Inject LegacyStoreManagerGateway legacyStoreManagerGateway;

  @Inject Event<StoreCreatedEvent> storeCreatedEvent;

  @Inject Event<StoreUpdatedEvent> storeUpdatedEvent;

  private static final Logger LOGGER = Logger.getLogger(StoreResource.class.getName());

  @GET
  public List<Store> get() {
    return Store.listAll(Sort.by("name"));
  }

  @GET
  @Path("{id}")
  public Store getSingle(Long id) {
    Store entity = Store.findById(id);
    if (entity == null) {
      throw new WebApplicationException(String.format(STORE_NOT_FOUND, id), Response.Status.NOT_FOUND);
    }
    return entity;
  }

  @POST
  @Transactional
  public Response create(@Valid Store store) {
    if (store.id != null) {
      throw new WebApplicationException(INVALID_ID, UNPROCESSABLE_ENTITY);
    }

    store.persist();

    storeCreatedEvent.fire(new StoreCreatedEvent(store));

    return Response.ok(store).status(Response.Status.CREATED).build();
  }

  @PUT
  @Path("{id}")
  @Transactional
  public Store update(Long id, Store updatedStore) {
    if (updatedStore.getName() == null) {
      throw new WebApplicationException(STORE_NOT_SET, UNPROCESSABLE_ENTITY);
    }

    Store entity = Store.findById(id);

    if (entity == null) {
      throw new WebApplicationException(String.format(STORE_NOT_FOUND, id), Response.Status.NOT_FOUND);
    }

    entity.setName(updatedStore.getName());
    entity.setQuantityProductsInStock(updatedStore.getQuantityProductsInStock());

    storeUpdatedEvent.fire(new StoreUpdatedEvent(entity));

    return entity;
  }

  @PATCH
  @Path("{id}")
  @Transactional
  public Store patch(Long id, Store updatedStore) {
    if (updatedStore.getName() == null) {
      throw new WebApplicationException(STORE_NOT_SET, UNPROCESSABLE_ENTITY);
    }

    Store entity = Store.findById(id);

    if (entity == null) {
      throw new WebApplicationException(String.format(STORE_NOT_FOUND, id), Response.Status.NOT_FOUND);
    }

    if (entity.getName() != null) {
      entity.setName(updatedStore.getName());
    }

    if (entity.getQuantityProductsInStock() != 0) {
      entity.setQuantityProductsInStock(updatedStore.getQuantityProductsInStock());
    }

    storeUpdatedEvent.fire(new StoreUpdatedEvent(entity));

    return entity;
  }

  @DELETE
  @Path("{id}")
  @Transactional
  public Response delete(Long id) {
    Store entity = Store.findById(id);
    if (entity == null) {
      throw new WebApplicationException(String.format(STORE_NOT_FOUND, id), Response.Status.NOT_FOUND);
    }
    entity.delete();
    return Response.status(204).build();
  }

  @Provider
  public static class ErrorMapper implements ExceptionMapper<Exception> {

    @Inject ObjectMapper objectMapper;

    @Override
    public Response toResponse(Exception exception) {
      LOGGER.error("Failed to handle request", exception);

      int code = 500;
      if (exception instanceof WebApplicationException) {
        code = ((WebApplicationException) exception).getResponse().getStatus();
      }

      ObjectNode exceptionJson = objectMapper.createObjectNode();
      exceptionJson.put("exceptionType", exception.getClass().getName());
      exceptionJson.put("code", code);

      if (exception.getMessage() != null) {
        exceptionJson.put("error", exception.getMessage());
      }

      return Response.status(code).entity(exceptionJson).build();
    }
  }
}
