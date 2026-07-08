/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Invoice;
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
public class InvoiceDao {
    
    @PersistenceContext(unitName = "un_projectTest")
    private EntityManager em;
    
    public void create(Invoice invoice) {
        if (invoice.getOrderDate() == null) {
            invoice.setOrderDate(new Date());
        }
        em.persist(invoice);
    }
 
    public List<Invoice> findByBuyerId(Long buyerId) {
        return em.createQuery( "SELECT i FROM Invoice i WHERE i.buyer.id = :bid ORDER BY i.orderDate DESC", Invoice.class)
                .setParameter("bid", buyerId)
                .getResultList();
    }
 
    public List<Invoice> findBySellerId(Long sellerId) {
        return em.createQuery("SELECT i FROM Invoice i WHERE i.sellerId = :sid ORDER BY i.orderDate DESC", Invoice.class)
                .setParameter("sid", sellerId)
                .getResultList();
    }
 
    public List<Invoice> findAll() {
        return em.createQuery( "SELECT i FROM Invoice i ORDER BY i.orderDate DESC", Invoice.class)
                .getResultList();
    }
    
    
    
}
