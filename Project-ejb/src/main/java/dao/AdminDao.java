/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Product;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**

 *
 * @author DELL
 */
@Stateless
public class AdminDao {

    @PersistenceContext(unitName = "un_projectTest")
    private EntityManager em;

    @EJB
    private ProductDao productDao;

    @EJB
    private CartDao cartDao;

    @EJB
    private WishlistDao wishlistDao;

    @EJB
    private AdminMessageDao adminMessageDao;

    public List<Product> findAllProducts() {
        return productDao.findAll();
    }


    public void removeProduct(Long productId, String reason) {
        Product product = em.find(Product.class, productId);
        if (product == null) {
            return;
        }
        String productName = product.getName();
        Long sellerId = product.getSeller() != null ? product.getSeller().getId() : null;

        List<Long> customersWithInCart = cartDao.findCustomerIdsForProduct(productId);
        List<Long> customersWithInWishlist = wishlistDao.findCustomerIdsForProduct(productId);

        cartDao.removeAllForProduct(productId);
        wishlistDao.removeAllForProduct(productId);
        productDao.delete(productId);

        String note = reason == null || reason.trim().isEmpty()
                ? "no reason was provided"
                : reason.trim();

        if (sellerId != null) {
            adminMessageDao.send(sellerId,
                    "Your product \"" + productName + "\" was removed by an administrator. Reason: " + note);
        }
        for (Long customerId : customersWithInCart) {
            adminMessageDao.send(customerId,
                    "\"" + productName + "\" was removed from the store and has also been removed from your cart.");
        }
        for (Long customerId : customersWithInWishlist) {
            adminMessageDao.send(customerId,
                    "\"" + productName + "\" was removed from the store and has also been removed from your wishlist.");
        }
    }
}