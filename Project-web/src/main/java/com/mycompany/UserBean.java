/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import entity.User;
import dao.UserDao;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;


/**
 *
 * @author DELL
 */
@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean implements Serializable  {
    private static final long serialVersionUID = 1L;
    
    private User user = new User();
    private String loginUsername;
    private String loginPassword;
    private String loginRole;
    
    @EJB
    private UserDao userDAO;
    
//    public String register(){
//        try{
//            userDAO.create(user);
//            FacesContext.getCurrentInstance().addMessage(null, 
//                new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration Successful", "You can now log in."));
//       user = new User();
//       return "login.xhtml?faces-redirect=true";
//        }catch (Exception e){
//            FacesContext.getCurrentInstance().addMessage(null, 
//                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration Failed", "Username or email may already exist."));
//            return null;
//            
//        }
//    }
//    
//    public String login(){
//        User userExist = userDAO.findByCredentials(loginUsername, loginPassword);
//        
//        if (userExist != null){
//            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentUser", userExist);
//            return "dashboard.xhtml?faces-redirect=true";
//        }else{
//            FacesContext.getCurrentInstance().addMessage(null, 
//                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Authentication Error", "Invalid Username or Password."));
//            return null;
//        }
//        
//    }
//    
//    public String logout(){
//        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
//        return "login.xhtml?faces-redirect=true";
//    }
    
    
    private String hashPassword(String password){
        
        if (password == null || password.isEmpty()){
            return "";
        }
        byte[] hashBytes = null;
        StringBuilder hexString = new StringBuilder();
        
        try{
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            hashBytes = digest.digest(password.getBytes());
                    
        }catch (java.security.NoSuchAlgorithmException e){
           throw new RuntimeException("Encryption setup failed: " + e.getMessage(), e);
        }
        
        if (hashBytes != null){
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1){
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        }
            
        return hexString.toString();
        
    }
    
    
    public String register(){
        
        try{
            
            String currentRole = user.getRole();
            if (currentRole == null || currentRole.trim().isEmpty()){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Please select an account type."));
                return null;
            }
            
            String currentEmail  = user.getEmail();
            if (currentEmail == null || !currentEmail.toLowerCase().endsWith("@gmail.com")){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Email registry restricted to valid @gmail.com accounts."));
                return null;
            }
            
            String plainPassword = user.getPassword();
            if (plainPassword == null || plainPassword.length() < 8 || plainPassword.length() > 12) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Password length configuration limits: 8-12 characters long."));
                return null;
            }
            
             User duplicateUserCheck = userDAO.findByUsername(user.getUsername());
            if (duplicateUserCheck != null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "This specific username profile has already been used."));
                return null;
            }
            
            User duplicateEmailCheck = userDAO.findByEmail(user.getEmail());
            if (duplicateEmailCheck != null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "This email identity has already registered. Proceed to login."));
                return null;
            }
            
            String temporaryHashedString = hashPassword(plainPassword);
         
            user.setPassword(temporaryHashedString);
            userDAO.create(user);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration Successful", "You can now log in."));
            user = new User();
            return "login.xhtml?faces-redirect=true";
        } catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "System Interruption", "Registration failure exception thrown during save."));
        return null;
        }
    }
    
    public String login(){
    
        if (loginRole == null || loginRole.trim().isEmpty()){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Please select the account type you want to log in as."));
            return null;
        }

        User userExist = userDAO.findByUsername(loginUsername);
        if(userExist != null){
            String loginPagePassword = hashPassword(loginPassword);

            if (userExist.getPassword().equals(loginPagePassword)){

                if (userExist.getRole() == null || !userExist.getRole().equalsIgnoreCase(loginRole)){
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Authentication Error", "This account is not registered as a " + loginRole + "."));
                    return null;
                }

                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentUser", userExist);

                switch (userExist.getRole().toLowerCase()){
                    case "seller":
                        return "seller-dashboard.xhtml?faces-redirect=true";
                    case "admin":
                        return "admin-dashboard.xhtml?faces-redirect=true";
                    case "customer":
                        return "customer-dashboard.xhtml?faces-redirect=true";
                    default:
                        return "dashboard.xhtml?faces-redirect=true";
                }
            }
        }    
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Authentication Error", "Invalid Username or Password."));
        return null;

     }
    
    
     public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "login.xhtml?faces-redirect=true";
    }
    
    
    
   public User getUser() { 
       return user; 
   }
   
   public void setUser(User user) { this.user = user; }
   
   public String getLoginUsername() {
       return loginUsername; }
   
   public void setLoginUsername(String loginUsername) {
       this.loginUsername = loginUsername;
   }
   public String getLoginPassword() { 
       return loginPassword; 
   }
   
   public void setLoginPassword(String loginPassword) { 
       this.loginPassword = loginPassword;
   }
   
   public String getLoginRole() {
        return loginRole;
   }
   
   public void setLoginRole(String loginRole) {
        this.loginRole = loginRole;
   }
    
    
}
