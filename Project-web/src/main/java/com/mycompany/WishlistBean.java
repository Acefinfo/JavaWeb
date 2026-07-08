/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import dao.CartDao;
import dao.WishlistDao;
import entity.User;
import entity.WishlistItem;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author DELL
 */
@ManagedBean(name = "wishlistBean")
@ViewScoped
public class WishlistBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private WishlistDao wishlistDao;

    @EJB
    private CartDao cartDao;

    private List<WishlistItem> wishlistItems;

    private Long getCurrentCustomerId() {
        Object sessionUser = FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("currentUser");
        if (sessionUser instanceof User) {
            return ((User) sessionUser).getId();
        }
        return null;
    }
    
    private void loadWishlist() {
        Long customerId = getCurrentCustomerId();
        if (customerId != null) {
            wishlistItems = wishlistDao.findByCustomerId(customerId);
        }
    }
    
    public List<WishlistItem> getWishlistItems(){
        if(wishlistItems == null){
            loadWishlist();
        }
        return wishlistItems;
    }
    
    public int getWishlistCount(){
        return getWishlistItems() == null ? 0 : getWishlistItems().size();
    }
    
    public void addToWishlist(Long productId){
        Long customerId = getCurrentCustomerId();
        if(customerId == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Please log in", "Log in as customer to use your wishlist."));
            return;
        }
        
        wishlistDao.addToWishList(customerId, productId);
        loadWishlist();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Added", "Item added to your wishlist."));
    }
    
    public void remove(WishlistItem item) {
        wishlistDao.remove(item.getId());
        loadWishlist();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Removed", "Item removed from your wishlist."));
    }
    
    public void moveToCart(WishlistItem item){
        Long customerId = getCurrentCustomerId();
        if (customerId == null){
            return;
        }
        cartDao.addToCart(customerId, item.getProduct().getId(), 1);
        wishlistDao.remove(item.getId());
        loadWishlist();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Moved", "Item moved to your cart."));
    }
    
}
