/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import dao.CartDao;
import dao.InvoiceDao;
import dao.ProductDao;
import entity.CartItem;
import entity.Invoice;
import entity.Product;
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
@ManagedBean(name = "purchaseBean")
@ViewScoped
public class PurchaseBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private ProductDao productDao;

    @EJB
    private InvoiceDao invoiceDao;

    @EJB
    private CartDao cartDao;

    private User getCurrentUser() {
        Object sessionUser = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentUser");
        return (sessionUser instanceof User) ? (User) sessionUser : null;
    }
    
    private boolean requireCustomer(){
        User current = getCurrentUser();
        if (current == null){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please log in", "Log in as customer to buy items."));
            return false;
        }
        if (current.getRole() == null || !current.getRole().equalsIgnoreCase("customer")){
             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Not allowed", "Only customer accounts can make purchases."));
            return false;
        }
        return true;
    }
    
    private Invoice buildInvoice(User buyer, Product product, int quantity){
        
        Invoice invoice = new Invoice();
        invoice.setBuyer(buyer);
        invoice.setProductId(product.getId());
        invoice.setProductName(product.getName());
        invoice.setUnitPrice(product.getPrice());
        invoice.setQuantity(quantity);
        invoice.setTotalPrice(product.getPrice() * quantity);
        
        if (product.getSeller() != null){
            invoice.setSellerId(product.getSeller().getId());
            invoice.setSellerUsername(product.getSeller().getUsername());
        }
        return invoice;
    }
    
    public void buyNow(Long productId, int quantity){
        if (!requireCustomer()){
            return;
        }
        if (quantity <= 0){
            quantity = 1;
        }
        
        Product product = productDao.findById(productId);
        if (product == null){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unavailable", "This product is no longer available."));
            return;
        }
        if (!productDao.decrementStock(productId, quantity)){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Out of stock", "Not enough stock left for that quantity."));
            return;
        }
        
        Invoice invoice = buildInvoice(getCurrentUser(), product, quantity);
        invoiceDao.create(invoice);
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Purchase complete", "You bought " + quantity + " x " + product.getName() + "."));
    }
    
   public void checkoutCart() {
        if (!requireCustomer()) {
            return;
        }
        User buyer = getCurrentUser();
        List<CartItem> items = cartDao.findByCustomerId(buyer.getId());
        if (items == null || items.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Empty cart", "There is nothing in your cart to buy."));
            return;
        }

        int purchasedCount = 0;
        StringBuilder skipped = new StringBuilder();

        for (CartItem item : items) {
            Product product = productDao.findById(item.getProduct().getId());
            if (product == null || !productDao.decrementStock(product.getId(), item.getQuantity())) {
                if (skipped.length() > 0) {
                    skipped.append(", ");
                }
                skipped.append(product != null ? product.getName() : "an item");
                continue;
            }
            Invoice invoice = buildInvoice(buyer, product, item.getQuantity());
            invoiceDao.create(invoice);
            cartDao.remove(item.getId());
            purchasedCount++;
        }

        if (purchasedCount > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Purchase complete", "Bought " + purchasedCount + " item(s)."));
        }
        if (skipped.length() > 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Some items skipped", "Not enough stock for: " + skipped + "."));
        }
    }
}