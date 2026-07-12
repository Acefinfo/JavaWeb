/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import entity.PendingPayment;
import entity.User;
import java.io.IOException;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import util.PaymentService;

/**
 *
 * @author DELL
 */
@ManagedBean(name = "paymentBean")
@RequestScoped
public class PaymentBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private PaymentService paymentService;

    private User getCurrentUser() {
        Object sessionUser = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentUser");
        return (sessionUser instanceof User) ? (User) sessionUser : null;
    }

    private boolean requireCustomer() {
        User current = getCurrentUser();
        if (current == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please log in", "Log in as customer to buy items."));
            return false;
        }
        if (current.getRole() == null || !current.getRole().equalsIgnoreCase("customer")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Not allowed", "Only customer accounts can make purchases."));
            return false;
        }
        return true;
    }

    public void payProductWithEsewa(Long productId, int quantity) {
        if (!requireCustomer()) return;
        try {
            PendingPayment pendingPayment = paymentService.initiateSingleProductPayment(
                    getCurrentUser(), productId, quantity <= 0 ? 1 : quantity);
            redirectToEsewaCheckout(pendingPayment.getTransactionUuid());
        } catch (IllegalStateException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot proceed", e.getMessage()));
        }
    }

    public void payCartWithEsewa() {
        if (!requireCustomer()) return;
        try {
            PendingPayment pendingPayment = paymentService.initiateCartPayment(getCurrentUser());
            redirectToEsewaCheckout(pendingPayment.getTransactionUuid());
        } catch (IllegalStateException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot proceed", e.getMessage()));
        }
    }

    private void redirectToEsewaCheckout(String transactionUuid) {
        try {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("esewa-checkout.xhtml?tid=" + transactionUuid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}