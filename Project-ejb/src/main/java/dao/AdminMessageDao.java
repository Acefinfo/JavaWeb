/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.AdminMessage;
import entity.User;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author DELL
 */

@Stateless
public class AdminMessageDao {
    
    @PersistenceContext(unitName = "un_projectTest")
    private EntityManager em;
 
    public void send(Long recipientId, String message) {
        User recipientRef = em.getReference(User.class, recipientId);
        AdminMessage msg = new AdminMessage();
        
        msg.setRecipient(recipientRef);
        msg.setMessage(message);
        msg.setSentDate(new Date());
        msg.setRead(false);
        em.persist(msg);
    }
 
    public List<AdminMessage> findByRecipientId(Long recipientId) {
        return em.createQuery(
                "SELECT m FROM AdminMessage m WHERE m.recipient.id = :rid ORDER BY m.sentDate DESC", AdminMessage.class)
                .setParameter("rid", recipientId)
                .getResultList();
    }
 
    public long countUnread(Long recipientId) {
        return em.createQuery(
                "SELECT COUNT(m) FROM AdminMessage m WHERE m.recipient.id = :rid AND m.read = false", Long.class)
                .setParameter("rid", recipientId)
                .getSingleResult();
    }
 
    public void markRead(Long messageId) {
        AdminMessage msg = em.find(AdminMessage.class, messageId);
        if (msg != null) {
            msg.setRead(true);
            em.merge(msg);
        }
    }
 
    public void markAllRead(Long recipientId) {
        em.createQuery("UPDATE AdminMessage m SET m.read = true WHERE m.recipient.id = :rid")
                .setParameter("rid", recipientId)
                .executeUpdate();
    }
    
}
