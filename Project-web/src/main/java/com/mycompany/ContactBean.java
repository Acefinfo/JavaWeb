/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author DELL
 */

@ManagedBean(name = "contactBean")
@SessionScoped
public class ContactBean implements Serializable {
    
    private Contact contactObj = new Contact();
    private List<Contact> contactList = new ArrayList<>();
    
    public void save(){
        
        contactList.add(0 , new Contact(
                contactObj.getFName(),
                contactObj.getLName(),
                contactObj.getEmail(),
                contactObj.getAddress(),
                contactObj.getNumber()
        ));
        contactObj = new Contact();
    }
    
    public Contact getContact(){
        return contactObj;
    }
    
    public void setContact(Contact contactObj){
        this.contactObj = contactObj;
    }
    
    public List<Contact> getContactList(){
        return contactList;
    }
    
    public void setContactList(List<Contact> contactList) {
    this.contactList = contactList;
    }
}
