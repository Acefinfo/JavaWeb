/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import dao.ProductDao;
import entity.Product;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * REST Web Service
 *
 * @author DELL
 */
@Path("products")
@RequestScoped
public class ProductResource {

    @EJB
    private ProductDao productDao;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getall(){
        return productDao.findAll();
    }
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") Long id){
        Product p = productDao.findById(id);
        if (p == null){
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\": \"Product not found\"}").build();
            
        }
        return Response.ok(p).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Product product, @QueryParam("sellerId") Long sellerId){
        productDao.create(product, sellerId);
        return Response.status(Response.Status.CREATED).entity(product).build();
    }
    
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, Product product,@QueryParam("sellerId") Long sellerId){
        product.setId(id);
        productDao.update(product, sellerId);
        return Response.ok().build();
    }
    
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        productDao.delete(id);
        return Response.noContent().build();
    }
    
    
    
    
}

