/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

/**
 *
 * @author DELL
 */
public class Contact {
    
    private String fName;
    private String lName;
    private String email;
    private String address;
    private String number;
    
    public Contact(){  }
    
    public Contact(String fName, String lName, String email, String address, String number ){
        this.fName  = fName;
        this.lName = lName;
        this.email = email;
        this.address = address;
        this.number = number;
    }
    
    public String getFName(){
        return fName;
    }
    
    public void setFName(String fName){
        this.fName = fName;
    }
    
    public String getLName(){
        return lName;
    }
    
    public void setLName(String lName){
        this.lName = lName;
    }
    
    public String getEmail(){
        return email;
    }
    
    public void setEmail(String email){
        this.email = email;
    }
    
    public String getAddress(){
        return address;
    }
    
    public void setAddress(String address){
        this.address = address;
    }
    
    public String getNumber(){
        return number;
    }
    
    public void setNumber(String number){
        this.number = number;
    }
}
