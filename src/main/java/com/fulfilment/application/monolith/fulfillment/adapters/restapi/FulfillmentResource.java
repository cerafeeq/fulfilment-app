package com.fulfilment.application.monolith.fulfillment.adapters.restapi;

import com.fulfilment.application.monolith.fulfillment.adapters.restapi.dto.FulfillmentAssociationRequest;
import com.fulfilment.application.monolith.fulfillment.adapters.restapi.dto.FulfillmentAssociationResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/fulfillment")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface FulfillmentResource {

    @GET
    List<FulfillmentAssociationResponse> listAll();

    @GET
    @Path("/store/{storeId}")
    List<FulfillmentAssociationResponse> getByStore(@PathParam("storeId") Long storeId);

    @GET
    @Path("/product/{productId}")
    List<FulfillmentAssociationResponse> getByProduct(@PathParam("productId") Long productId);

    @GET
    @Path("/warehouse/{warehouseBusinessUnitCode}")
    List<FulfillmentAssociationResponse> getByWarehouse(
            @PathParam("warehouseBusinessUnitCode") String warehouseBusinessUnitCode);

    @POST
    Response createAssociation(@Valid FulfillmentAssociationRequest request);

    @DELETE
    @Path("/store/{storeId}/product/{productId}/warehouse/{warehouseBusinessUnitCode}")
    Response deleteAssociation(
            @PathParam("storeId") Long storeId,
            @PathParam("productId") Long productId,
            @PathParam("warehouseBusinessUnitCode") String warehouseBusinessUnitCode);
}

