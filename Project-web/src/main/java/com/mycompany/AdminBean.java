/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import dao.AdminDao;
import entity.Product;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author DELL
 */
@ManagedBean(name = "adminBean")
@ViewScoped
public class AdminBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private AdminDao adminDao;

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    private List<Product> allProducts;
    private String removalReason;

    private String newAdminUsername;
    private String newAdminEmail;
    private String newAdminPassword;

    
    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    private void loadProducts() {
        allProducts = adminDao.findAllProducts();
    }

    public List <Product> getAllProducts(){
        if (allProducts == null){
            loadProducts();
        }
        return allProducts;
    }
    
    public int getProductCount() {
        return getAllProducts() == null ? 0 : getAllProducts().size();
    }
    
    public String getRemovalReason() {
        return removalReason;
    }
    
    public void setRemovalReason(String removalReason) {
        this.removalReason = removalReason;
    }
    
    public void removeProduct(Product product) {
        adminDao.removeProduct(product.getId(), removalReason);
        removalReason = null;
        loadProducts();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Removed", "Product removed, and the seller (and any affected customers) have been notified."));
    }
    
    public String getNewAdminUsername(){
        return newAdminUsername;
    }
    
    public void setNewAdminUsername(String newAdminUsername){
        this.newAdminUsername = newAdminUsername;
    }
    
    
    public String getNewAdminEmail() { 
        return newAdminEmail; 
    }
    public void setNewAdminEmail(String newAdminEmail) { 
        this.newAdminEmail = newAdminEmail; 
    }

    public String getNewAdminPassword() { 
        return newAdminPassword; 
    }
    public void setNewAdminPassword(String newAdminPassword) { 
        this.newAdminPassword = newAdminPassword; 
    }

    public void createAdmin() {
        userBean.createAdminAccount(newAdminUsername, newAdminEmail, newAdminPassword);
        newAdminUsername = null;
        newAdminEmail = null;
        newAdminPassword = null;
    }
    
}
