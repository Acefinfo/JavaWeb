/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import dao.ProductDao;
import entity.Product;
import entity.User;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author DELL
 */
@ManagedBean(name = "productBean")
@ViewScoped
public class ProductBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProductDao productDao;

    private Product product = new Product();
    private List<Product> productList;
    private UploadedFile uploadedFile;
    private boolean editMode = false;


    private List<Product> allProducts;
    private String searchKeyword;


    private Long viewProductId;
    private Product selectedProduct;
    private int buyQuantity = 1;
    private List<Product> recommendedProducts;
    
    public List<Product> getAllProducts() {
        if (allProducts == null) {
            allProducts = productDao.findAll();
        }
        return allProducts;
    }

    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }


    public void search() {
        allProducts = productDao.search(searchKeyword);
    }

    public Long getViewProductId() { 
        return viewProductId; 
    }
    public void setViewProductId(Long viewProductId) { 
        this.viewProductId = viewProductId; 
    }

    public void loadSelectedProduct() {
        if (viewProductId != null) {
            selectedProduct = productDao.findById(viewProductId);
        }
    }

    public Product getSelectedProduct() { return selectedProduct; }

    public int getBuyQuantity() { return buyQuantity; }
    public void setBuyQuantity(int buyQuantity) { this.buyQuantity = buyQuantity; }

    private Long getCurrentSellerId() {
        Object sessionUser = FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("currentUser");
        if (sessionUser instanceof User) {
            return ((User) sessionUser).getId();
        }
        return null;
    }

    private void loadProducts() {
        Long sellerId = getCurrentSellerId();
        if (sellerId != null) {
            productList = productDao.findBySellerId(sellerId);
        }
    }

    public List<Product> getProductList() {
        if (productList == null) {
            loadProducts();
        }
        return productList;
    }

    public void openNew() {
        product = new Product();
        uploadedFile = null;
        editMode = false;
    }

    public void editProduct(Product selected) {
        product = selected;
        uploadedFile = null;
        editMode = true;
    }

    public void saveProduct() {
        try {
            Long sellerId = getCurrentSellerId();
            if (sellerId == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Session Expired", "Please log in again."));
                return;
            }

            if (uploadedFile != null && uploadedFile.getContent() != null && uploadedFile.getContent().length > 0) {
                product.setImage(uploadedFile.getContent());
            }

            if (editMode) {
                productDao.update(product, sellerId);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Product updated."));
            } else {
                productDao.create(product, sellerId);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Product added."));
            }

            product = new Product();
            uploadedFile = null;
            editMode = false;
            loadProducts();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not save product."));
        }
    }

    public void deleteProduct(Product selected) {
        try {
            productDao.delete(selected.getId());
            loadProducts();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Deleted", "Product removed."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not delete product."));
        }
    }
    
    public List<Product> getRecommendedProducts() {
        if (recommendedProducts == null) {
            List<Product> all = productDao.findAll();
            recommendedProducts = new java.util.ArrayList<>();
            for (Product p : all) {
                if (selectedProduct == null || !p.getId().equals(selectedProduct.getId())) {
                    recommendedProducts.add(p);
                }
                if (recommendedProducts.size() >= 8) {
                    break;
                }
            }
        }
        return recommendedProducts;
    }

    public Product getProduct() { 
        return product; 
    }
    
    public void setProduct(Product product) { 
        this.product = product; 
    }

    public UploadedFile getUploadedFile() { 
        return uploadedFile; 
    }

    public void setUploadedFile(UploadedFile uploadedFile) { 
        this.uploadedFile = uploadedFile; 
    }

    public boolean isEditMode() { 
        return editMode; 
    }
    
    public void setEditMode(boolean editMode) { 
        this.editMode = editMode; 
    }
}