/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import dao.ProductDao;
import entity.Product;
import entity.User;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
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

    public void downloadProductImage(Product p){
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
        
        try{
            byte[] image = p.getImage();
            if (image == null || image.length == 0){
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No image", "This product has no image"));
                return;
            }
            
            String ext = "jpg";
            String contentType = "image/jpeg";
            if (image.length> 4 && (image[0] & 0xFF) == 0x89 && image[1] == 0x50 && image[2] == 0x4E && image[3] == 0x47 ){
                ext ="png";
                contentType = "image/png";
            }
            
            response.reset();
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + sanitizeFileName(p.getName()) + "." + ext + "\"");
            response.setContentLength(image.length);
            
            OutputStream out = response.getOutputStream();
            out.write(image);
            out.flush();
            
        } catch (Exception e){
            
        }
        fc.responseComplete();
    }
    
    public void downloadProductsExcel(){
        List<Product> products = getProductList();
        List<String[]> rows = new java.util.ArrayList<String[]>();
        
        int sn = 1;
        if (products != null){
            for (Product p : products){
                rows.add(new String[]{
                    String.valueOf(sn++),
                    p.getName(),
                    p.getDescription(),
                    String.valueOf(p.getPrice()),
                    String.valueOf(p.getStock())
                });
            }
        }
        util.ExcelExportUtil.export("My Products", new String[]{"SN", "Name", "Description", "Price", "Stock"},rows, "my-products.xlsx");
    }
    
    private String sanitizeFileName(String name){
        if (name == null || name.trim().isEmpty()){
            return "reoduct";
        }
        return name.replaceAll("[^a-zA-Z0-9-_]", "_");
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