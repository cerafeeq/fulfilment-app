package com.fulfilment.application.monolith.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import org.jboss.logging.Logger;

@Path("product")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class ProductResource {
  private static final String PRODUCT_NOT_FOUND = "Product with id of %d does not exist.";
  private static final String INVALID_ID = "Id was invalidly set on request.";
  private static final String PRODUCT_NOT_SET = "Product Name was not set on request.";
  public static final int UNPROCESSABLE_ENTITY = 422;

  @Inject ProductRepository productRepository;

  private static final Logger LOGGER = Logger.getLogger(ProductResource.class.getName());

  @GET
  public List<Product> get() {
    return productRepository.listAll(Sort.by("name"));
  }

  @GET
  @Path("{id}")
  public Product getSingle(@PathParam("id") Long id) {
    Product entity = productRepository.findById(id);
    if (entity == null) {
      throw new WebApplicationException(String.format(PRODUCT_NOT_FOUND, id), Response.Status.NOT_FOUND);
    }
    return entity;
  }

  @POST
  @Transactional
  public Response create(@Valid Product product) {
    if (product.id != null) {
      throw new WebApplicationException(INVALID_ID, UNPROCESSABLE_ENTITY);
    }

    productRepository.persist(product);
    return Response.ok(product).status(201).build();
  }

  @PUT
  @Path("{id}")
  @Transactional
  public Product update(@PathParam("id") Long id, Product product) {
    if (product.name == null) {
      throw new WebApplicationException(PRODUCT_NOT_SET, UNPROCESSABLE_ENTITY);
    }

    Product entity = productRepository.findById(id);

    if (entity == null) {
      throw new WebApplicationException(String.format(PRODUCT_NOT_FOUND, id), Response.Status.NOT_FOUND);
    }

    entity.name = product.name;
    entity.description = product.description;
    entity.price = product.price;
    entity.stock = product.stock;

    productRepository.persist(entity);

    return entity;
  }

  @DELETE
  @Path("{id}")
  @Transactional
  public Response delete(@PathParam("id") Long id) {
    Product entity = productRepository.findById(id);
    if (entity == null) {
      throw new WebApplicationException(String.format(PRODUCT_NOT_FOUND, id), Response.Status.NOT_FOUND);
    }
    productRepository.delete(entity);
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
