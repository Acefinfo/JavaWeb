/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import dao.AdminMessageDao;
import entity.AdminMessage;
import entity.User;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

/**
 *
 * @author DELL
 */

@ManagedBean(name = "inboxBean")
@ViewScoped
public class InboxBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    private AdminMessageDao adminMessageDao;
    
    private List<AdminMessage> messages;
    
    private Long getCurrentUserId() {
        Object sessionUser = FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("currentUser");
        if (sessionUser instanceof User) {
            return ((User) sessionUser).getId();
        }
        return null;
    }
    
    private void loadMessages(){
        Long userId = getCurrentUserId();
        if (userId != null){
            messages = adminMessageDao.findByRecipientId(userId);
        }
    }
    
    public List <AdminMessage> getMessages(){
        if (messages == null){
            loadMessages();
        }
        return messages;
    }
    
    public long getUnreadCount(){
        Long userId = getCurrentUserId();
        if (userId == null){
            return 0;
        }
        return adminMessageDao.countUnread(userId);
    }
    
    public void markRead(AdminMessage message){
        adminMessageDao.markRead(message.getId());
    }
    
    public void markAllRead(){
        Long userId = getCurrentUserId();
        if (userId != null){
            adminMessageDao.markAllRead(userId);
            loadMessages();
        }
    }
    
}
