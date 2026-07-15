/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import dao.CartDao;
import entity.CartItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author DELL
 */
@Path("cart")
@RequestScoped
public class CartResource {

    @EJB
    private CartDao cartDao;

    @GET
    @Path("{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CartItem> getCart(@PathParam("customerId") Long customerId) {
        return cartDao.findByCustomerId(customerId);
    }

    @POST
    @Path("{customerId}/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(@PathParam("customerId") Long customerId,
                         @QueryParam("productId") Long productId,
                         @QueryParam("quantity") @DefaultValue("1") int quantity) {
        if (productId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(msg("productId is required.")).build();
        }
        cartDao.addToCart(customerId, productId, quantity);
        return Response.ok(cartDao.findByCustomerId(customerId)).build();
    }

    @PUT
    @Path("item/{cartItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateQuantity(@PathParam("cartItemId") Long cartItemId,
                                    @QueryParam("quantity") int quantity) {
        cartDao.updateQuantity(cartItemId, quantity);
        return Response.ok(msg("Cart item updated.")).build();
    }

    @DELETE
    @Path("item/{cartItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response remove(@PathParam("cartItemId") Long cartItemId) {
        cartDao.remove(cartItemId);
        return Response.ok(msg("Cart item removed.")).build();
    }

    @DELETE
    @Path("{customerId}/clear")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clear(@PathParam("customerId") Long customerId) {
        cartDao.clearForCustomer(customerId);
        return Response.ok(msg("Cart cleared.")).build();
    }

    private Map<String, String> msg(String text) {
        Map<String, String> m = new HashMap<String, String>();
        m.put("message", text);
        return m;
    }
}