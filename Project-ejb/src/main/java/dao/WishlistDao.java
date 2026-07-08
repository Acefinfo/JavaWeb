/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Product;
import entity.User;
import entity.WishlistItem;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;


/**
 *
 * @author DELL
 */

@Stateless
public class WishlistDao {
    
    @PersistenceContext(unitName = "un_projectTest")
    private EntityManager em;
    
    public void addToWishList(Long customerId, Long productId){
        WishlistItem existing = findByCustomerAndProduct(customerId, productId);
        
        if (existing != null){
            return;
        }
        
        User customerRef = em.getReference(User.class, customerId);
        Product productRef = em.getReference(Product.class, productId);
 
        WishlistItem item = new WishlistItem();
        item.setCustomer(customerRef);
        item.setProduct(productRef);
        em.persist(item);
    }
    
    public void remove(Long wishlistItemId) {
        WishlistItem item = em.find(WishlistItem.class, wishlistItemId);
        if (item != null) {
            em.remove(item);
        }
    }
    
    public void removeAllForProduct(Long productId){
        em.createQuery("DELETE FROM WishlistItem w WHERE w.product.id = :pid")
                .setParameter("pid", productId)
                .executeUpdate();
    }
    
    public WishlistItem findByCustomerAndProduct(Long customerId, Long productId) {
        try {
            return em.createQuery(
                    "SELECT w FROM WishlistItem w WHERE w.customer.id = :cid AND w.product.id = :pid", WishlistItem.class)
                    .setParameter("cid", customerId)
                    .setParameter("pid", productId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
   
    public List<WishlistItem> findByCustomerId(Long customerId) {
        return em.createQuery(
                "SELECT w FROM WishlistItem w WHERE w.customer.id = :cid ORDER BY w.id DESC", WishlistItem.class)
                .setParameter("cid", customerId)
                .getResultList();
    }
    
    public List<Long> findCustomerIdsForProduct(Long productId) {
        return em.createQuery(
                "SELECT DISTINCT w.customer.id FROM WishlistItem w WHERE w.product.id = :pid", Long.class)
                .setParameter("pid", productId)
                .getResultList();
    }
    
    
    
    
    
}
