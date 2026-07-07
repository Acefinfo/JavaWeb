/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Product;
import entity.User;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author DELL
 */

@Stateless
public class ProductDao {
    
    @PersistenceContext(unitName = "un_projectTest")
    private EntityManager em;
    
    
    public void create(Product product, Long sellerId) {
        User sellerRef = em.getReference(User.class, sellerId);
        product.setSeller(sellerRef);
        em.persist(product);
    }
    
    
    public void update(Product product, Long sellerId) {
        User sellerRef = em.getReference(User.class, sellerId);
        product.setSeller(sellerRef);
        em.merge(product);
    }
    
    public void delete(Long id) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            em.remove(product);
        }
    }

    public Product findById(Long id) {
        if (id == null) {
            return null;
        }
        return em.find(Product.class, id);
    }

    public List<Product> findBySellerId(Long sellerId) {
        return em.createQuery(
                "SELECT p FROM Product p WHERE p.seller.id = :sid ORDER BY p.id DESC", Product.class)
                .setParameter("sid", sellerId)
                .getResultList();
    }
}
    
    
   