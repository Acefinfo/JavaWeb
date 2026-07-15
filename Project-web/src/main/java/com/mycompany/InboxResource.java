/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import dao.AdminMessageDao;
import entity.AdminMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
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
@Path("inbox")
@RequestScoped
public class InboxResource {

    @EJB
    private AdminMessageDao adminMessageDao;

    @GET
    @Path("{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AdminMessage> getMessages(@PathParam("userId") Long userId) {
        return adminMessageDao.findByRecipientId(userId);
    }

    @GET
    @Path("{userId}/unread-count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unreadCount(@PathParam("userId") Long userId) {
        Map<String, Long> m = new HashMap<String, Long>();
        m.put("unread", adminMessageDao.countUnread(userId));
        return Response.ok(m).build();
    }

    @PUT
    @Path("message/{messageId}/read")
    @Produces(MediaType.APPLICATION_JSON)
    public Response markRead(@PathParam("messageId") Long messageId) {
        adminMessageDao.markRead(messageId);
        return Response.ok(msg("Message marked as read.")).build();
    }

    @PUT
    @Path("{userId}/read-all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response markAllRead(@PathParam("userId") Long userId) {
        adminMessageDao.markAllRead(userId);
        return Response.ok(msg("All messages marked as read.")).build();
    }

    @POST
    @Path("send")
    @Produces(MediaType.APPLICATION_JSON)
    public Response send(@QueryParam("recipientId") Long recipientId, @QueryParam("message") String message) {
        adminMessageDao.send(recipientId, message);
        return Response.ok(msg("Message sent.")).build();
    }

    private Map<String, String> msg(String text) {
        Map<String, String> m = new HashMap<String, String>();
        m.put("message", text);
        return m;
    }
}