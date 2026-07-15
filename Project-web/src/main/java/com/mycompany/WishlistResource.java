/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import dao.WishlistDao;
import entity.WishlistItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.DELETE;
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
@Path("wishlist")
@RequestScoped
public class WishlistResource {

   @EJB
    private WishlistDao wishlistDao;

    @GET
    @Path("{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<WishlistItem> getWishlist(@PathParam("customerId") Long customerId) {
        return wishlistDao.findByCustomerId(customerId);
    }

    @POST
    @Path("{customerId}/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(@PathParam("customerId") Long customerId, @QueryParam("productId") Long productId) {
        if (productId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(msg("productId is required.")).build();
        }
        wishlistDao.addToWishList(customerId, productId);
        return Response.ok(wishlistDao.findByCustomerId(customerId)).build();
    }

    @DELETE
    @Path("item/{wishlistItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response remove(@PathParam("wishlistItemId") Long wishlistItemId) {
        wishlistDao.remove(wishlistItemId);
        return Response.ok(msg("Wishlist item removed.")).build();
    }

    private Map<String, String> msg(String text) {
        Map<String, String> m = new HashMap<String, String>();
        m.put("message", text);
        return m;
    }
}