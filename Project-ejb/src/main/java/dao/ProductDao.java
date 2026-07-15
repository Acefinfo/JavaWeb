/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Product;
import entity.User;
import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author DELL
 */

@Stateless
public class ProductDao implements Serializable {
        private static final long serialVersionUID = 1L;

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

    public List<Product> findAll() {
        return em.createQuery("SELECT p FROM Product p ORDER BY p.id DESC", Product.class)
                .getResultList();
    }


    public List<Product> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String like = "%" + keyword.trim().toLowerCase() + "%";
        return em.createQuery(
                "SELECT p FROM Product p WHERE LOWER(p.name) LIKE :kw OR LOWER(p.description) LIKE :kw ORDER BY p.id DESC",
                Product.class)
                .setParameter("kw", like)
                .getResultList();
    }

    public boolean decrementStock(Long productId, int quantity) {
        Product product = em.find(Product.class, productId);
        if (product == null || product.getStock() == null || product.getStock() < quantity) {
            return false;
        }
        product.setStock(product.getStock() - quantity);
        em.merge(product);
        return true;
    }
}