/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import dao.AdminDao;
import entity.Product;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author DELL
 */
@Path("admin")
@RequestScoped
public class AdminResource {

    @EJB
    private AdminDao adminDao;

    @GET
    @Path("products")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getAllProducts() {
        return adminDao.findAllProducts();
    }

    @DELETE
    @Path("products/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeProduct(@PathParam("id") Long id, @QueryParam("reason") String reason) {
        adminDao.removeProduct(id, reason);
        Map<String, String> m = new HashMap<String, String>();
        m.put("message", "Product removed and affected users notified.");
        return Response.ok(m).build();
    }
}
