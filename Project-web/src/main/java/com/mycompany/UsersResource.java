/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import dao.UserDao;
import entity.User;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.PasswordUtil;

/**
 * REST Web Service
 *
 * @author DELL
 */
@Path("users")
@RequestScoped
public class UsersResource {
    
    @EJB 
    private UserDao userDao;
    
    
    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(User user) {
        try {
            if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(msg("Username, email and password are required.")).build();
            }
            if (userDao.findByUsername(user.getUsername()) != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(msg("Username already taken.")).build();
            }
            if (userDao.findByEmail(user.getEmail()) != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(msg("Email already registered.")).build();
            }
            if (user.getRole() == null || user.getRole().trim().isEmpty()) {
                user.setRole("customer");
            }
            user.setPassword(PasswordUtil.sha256Hex(user.getPassword()));
            userDao.create(user);
            return Response.status(Response.Status.CREATED).entity(user).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(msg("Registration failed: " + e.getMessage())).build();
        }
    }
    
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        if (username == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg("Username and password are required.")).build();
        }
        String hashed = PasswordUtil.sha256Hex(password);
        User user = userDao.findByCredentials(username, hashed);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(msg("Invalid username or password.")).build();
        }
        return Response.ok(user).build();
    }
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") Long id) {
        User user = userDao.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(msg("User not found.")).build();
        }
        return Response.ok(user).build();
    }

    @GET
    @Path("username/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByUsername(@PathParam("username") String username) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(msg("User not found.")).build();
        }
        return Response.ok(user).build();
    }
    
    
    private Map<String, String> msg(String text) {
        Map<String, String> m = new HashMap<String, String>();
        m.put("message", text);
        return m;
    }

}
