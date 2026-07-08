/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.CartItem;
import entity.Product;
import entity.User;
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
public class CartDao {
    
    @PersistenceContext(unitName = "un_projectTest")
    private EntityManager em;
    
    
   public void addToCart(Long customerId, Long productId, int quantity) {
        CartItem existing = findByCustomerAndProduct(customerId, productId);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            em.merge(existing);
            return;
        }
        User customerRef = em.getReference(User.class, customerId);
        Product productRef = em.getReference(Product.class, productId);
 
        CartItem item = new CartItem();
        item.setCustomer(customerRef);
        item.setProduct(productRef);
        item.setQuantity(quantity);
        em.persist(item);
    }
 
    public void updateQuantity(Long cartItemId, int quantity) {
        CartItem item = em.find(CartItem.class, cartItemId);
        if (item != null) {
            if (quantity <= 0) {
                em.remove(item);
            } else {
                item.setQuantity(quantity);
                em.merge(item);
            }
        }
    }
 
    public void remove(Long cartItemId) {
        CartItem item = em.find(CartItem.class, cartItemId);
        if (item != null) {
            em.remove(item);
        }
    }
 
    public void clearForCustomer(Long customerId) {
        em.createQuery("DELETE FROM CartItem c WHERE c.customer.id = :cid")
                .setParameter("cid", customerId)
                .executeUpdate();
    }
 
    public void removeAllForProduct(Long productId) {
        em.createQuery("DELETE FROM CartItem c WHERE c.product.id = :pid")
                .setParameter("pid", productId)
                .executeUpdate();
    }
 
    public CartItem findByCustomerAndProduct(Long customerId, Long productId) {
        try {
            return em.createQuery(
                    "SELECT c FROM CartItem c WHERE c.customer.id = :cid AND c.product.id = :pid", CartItem.class)
                    .setParameter("cid", customerId)
                    .setParameter("pid", productId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
 
    public List<CartItem> findByCustomerId(Long customerId) {
        return em.createQuery(
                "SELECT c FROM CartItem c WHERE c.customer.id = :cid ORDER BY c.id DESC", CartItem.class)
                .setParameter("cid", customerId)
                .getResultList();
    }
 

    public List<Long> findCustomerIdsForProduct(Long productId) {
        return em.createQuery(
                "SELECT DISTINCT c.customer.id FROM CartItem c WHERE c.product.id = :pid", Long.class)
                .setParameter("pid", productId)
                .getResultList();
    }
}