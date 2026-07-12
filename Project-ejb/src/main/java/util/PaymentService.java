/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import dao.CartDao;
import dao.InvoiceDao;
import dao.PendingPaymentDao;
import dao.ProductDao;
import entity.CartItem;
import entity.Invoice;
import entity.PendingPayment;
import entity.Product;
import entity.User;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author DELL
 */

@Stateless
public class PaymentService {
    
    @EJB
    private ProductDao productDao;

    @EJB
    private InvoiceDao invoiceDao;

    @EJB
    private CartDao cartDao;

    @EJB
    private PendingPaymentDao pendingPaymentDao;
    
     public PendingPayment initiateSingleProductPayment(User buyer, Long productId, int quantity) {
        Product product = productDao.findById(productId);
        if (product == null) {
            throw new IllegalStateException("This product is no longle avaliable. ");
        }
        if (quantity <= 0){
            quantity = 1;
        }
        if (product.getStock() == null || product.getStock() < quantity){
            throw new IllegalStateException("Not enough stock left for that quantity. ");
        }
        
        double amount = product.getPrice()*quantity;
        
        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setTransactionUuid(UUID.randomUUID().toString());
        pendingPayment.setBuyer(buyer);
        pendingPayment.setType("SINGLE");
        pendingPayment.setItemsSnapshot(productId+ ":" + quantity);
        pendingPayment.setAmount(amount);
        pendingPayment.setStatus("PENDING");
        pendingPayment.setCreatedDate(new Date());
        
        pendingPaymentDao.create(pendingPayment);
        return pendingPayment;
    }
     
     public PendingPayment initiateCartPayment(User buyer){
         List <CartItem> items = cartDao.findByCustomerId(buyer.getId());
         if (items == null || items.isEmpty()){
             throw new IllegalStateException("There is nothing in cart to buy");
         }
         
         StringBuilder snapshot = new StringBuilder();
         double total = 0;
         
         for (CartItem item: items) {
             Product product = productDao.findById(item.getProduct().getId());
             
             if (product == null || product.getStock() == null || product.getStock() < item.getQuantity()){
                 throw new IllegalStateException("Not enough stock for" + (product != null ? product.getName() : "an item") + ".");
             }
             if (snapshot.length() > 0) 
                 snapshot.append(";");
             
             snapshot.append(product.getId()).append(":").append(item.getQuantity());
             total += product.getPrice() * item.getQuantity();
         }
         
         PendingPayment pendingPayment = new PendingPayment();
         pendingPayment.setTransactionUuid(UUID.randomUUID().toString());
         pendingPayment.setBuyer(buyer);
         pendingPayment.setType("CART");
        pendingPayment.setItemsSnapshot(snapshot.toString());
        pendingPayment.setAmount(total);
        pendingPayment.setStatus("PENDING");
        pendingPayment.setCreatedDate(new Date());

        pendingPaymentDao.create(pendingPayment);
        return pendingPayment;
     }
     
     public String finalizePayment (String transactionUuid, boolean paymentConfirmedSuccess){
         PendingPayment pendingPayment = pendingPaymentDao.findByUuid(transactionUuid);
         if (pendingPayment == null){
             return "NOT_FOUND";
         }
         if (!"PENDING".equals(pendingPayment.getStatus())){
             return pendingPayment.getStatus();
         }
         
         if (!paymentConfirmedSuccess) {
            pendingPayment.setStatus("FAILED");
            pendingPaymentDao.update(pendingPayment);
            return "FAILED";
        }
         
         String[] entries  = pendingPayment.getItemsSnapshot().split(";");
         for (String entry: entries) {
             String[] parts = entry.split(":");
             Long productId = Long.parseLong(parts[0]);
            int quantity = Integer.parseInt(parts[1]);
            
            Product product = productDao.findById(productId);
            if (product == null || !productDao.decrementStock(productId, quantity)) {
                continue; 
            }
            
            Invoice invoice = new Invoice();
            invoice.setBuyer(pendingPayment.getBuyer());
            invoice.setProductId(product.getId());
            invoice.setProductName(product.getName());
            invoice.setUnitPrice(product.getPrice());
            invoice.setQuantity(quantity);
            invoice.setTotalPrice(product.getPrice() * quantity);
            if (product.getSeller() != null) {
                invoice.setSellerId(product.getSeller().getId());
                invoice.setSellerUsername(product.getSeller().getUsername());
            }
            invoiceDao.create(invoice);
         } 
         
         if ("CART".equals(pendingPayment.getType())){
             List<CartItem> items = cartDao.findByCustomerId(pendingPayment.getBuyer().getId());
             
             if (items != null) {
                 for(CartItem item : items){
                     cartDao.remove(item.getId());
                 }
             }
         }
         
         pendingPayment.setStatus("COMPLETE");
         pendingPaymentDao.update(pendingPayment);
         return "COMPLETE";
        
         
     }
     
    
}
