/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import dao.PendingPaymentDao;
import entity.PendingPayment;
import entity.User;
import java.io.IOException;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import util.EsewaUtil;

/**
 *
 * @author DELL
 */

@ManagedBean(name = "esewaCheckoutBean")
@ViewScoped
public class EsewaCheckoutBean {
    
    private static final long serialVersionUID = 1L;

    @EJB
    private PendingPaymentDao pendingPaymentDao;

    private String tid;
    private PendingPayment pendingPayment;

    public void loadPayment() {
        User current = getCurrentUser();
        pendingPayment = (tid == null) ? null : pendingPaymentDao.findByUuid(tid);

        boolean valid = pendingPayment != null
                && current != null
                && pendingPayment.getBuyer().getId().equals(current.getId())
                && "PENDING".equals(pendingPayment.getStatus());

        if (!valid) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("home.xhtml");
                FacesContext.getCurrentInstance().responseComplete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private User getCurrentUser() {
        Object sessionUser = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentUser");
        return (sessionUser instanceof User) ? (User) sessionUser : null;
    }

    public String getTid() { return tid; }
    public void setTid(String tid) { this.tid = tid; }

    public String getFormattedAmount() {
        return EsewaUtil.formatAmount(pendingPayment.getAmount());
    }

    public String getTransactionUuid() {
        return pendingPayment.getTransactionUuid();
    }

    public String getProductCode() {
        return EsewaUtil.MERCHANT_CODE;
    }

    public String getFormUrl() {
        return EsewaUtil.FORM_URL;
    }

    public String getSignature() {
        String message = EsewaUtil.buildInitiationMessage(getFormattedAmount(), getTransactionUuid(), getProductCode());
        return EsewaUtil.generateSignature(EsewaUtil.SECRET_KEY, message);
    }

    public String getSuccessUrl() { return baseUrl() + "/esewa/success"; }
    public String getFailureUrl() { return baseUrl() + "/esewa/failure"; }

    private String baseUrl() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}