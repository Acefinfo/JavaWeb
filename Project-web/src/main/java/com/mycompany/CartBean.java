/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import dao.CartDao;
import entity.CartItem;
import entity.User;
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

@ManagedBean(name = "cartBean")
@ViewScoped
public class CartBean implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @EJB
    private CartDao cartDao;

    private List<CartItem> cartItems;
    
    private Long getCurrentCustomerId() {
        Object sessionUser = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentUser");
        if (sessionUser instanceof User) {
            return ((User) sessionUser).getId();
        }
        return null;
    }
    
    private void loadCart() {
        Long customerId = getCurrentCustomerId();
        if (customerId != null) {
            cartItems = cartDao.findByCustomerId(customerId);
        }
    }
    
    public List<CartItem> getCartItems() {
        loadCart();
        return cartItems;
    }
    
    public double getCartTotal(){
        double total = 0.0;
        for (CartItem item: getCartItems()){
            total += item.getLineTotal();
        }
        return total;
    }
    
    public int getCartCount(){
        return getCartItems() == null ? 0 : getCartItems().size();
    }
    
    public void addToCart(Long productId){
        Long customerId = getCurrentCustomerId();
        
        if (customerId == null){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please log in", "Log in as customer to add items in your cart"));
            return;
        }
        
        cartDao.addToCart(customerId, productId, 1);
        loadCart();
    }
    
    public void updateQuantity(CartItem item, int quantity) {
        cartDao.updateQuantity(item.getId(), quantity);
        loadCart();
    }
    
    public void increment(CartItem item){
        updateQuantity(item, item.getQuantity()+1);
    }
    
    public void decrement(CartItem item){
        updateQuantity(item, item.getQuantity() -1);
    }
    
    public void remove (CartItem item){
        cartDao.remove(item.getId());
        loadCart();
        
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Removed", "Item removed from your cart."));
    }
    
    
}