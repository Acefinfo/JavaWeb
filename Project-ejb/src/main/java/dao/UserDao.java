/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.User;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.NoResultException;

/**
 *
 * @author DELL
 */

@Stateless
public class UserDao {
    
    @PersistenceContext(unitName = "un_projectTest")
    private EntityManager em;
    
    public void create (User user) throws Exception{
        em.persist(user);
    }
    
    public User findByCredentials(String username, String password) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :un AND u.password = :pw", User.class)
                    .setParameter("un", username)
                    .setParameter("pw", password)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        
    }
    
    public User findByUsername(String username){
        try{
            return em.createQuery("SELECT u FROM User u WHERE u.username = :un", User.class)
                    .setParameter("un", username)
                    .getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }
    
    public User findByEmail(String email){
        try{
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class).setParameter("email",email).getSingleResult();
        }catch (NoResultException e){
            return null;
        }
    }
    
    public User findByPasswordHash(String hashedPwd) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.password = :pw", User.class)
                    .setParameter("pw", hashedPwd)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public User findById(Long id) {
        if (id == null) {
            return null;
        }
        return em.find(User.class, id);
    }

    public long countByRole(String role) {
        return em.createQuery("SELECT COUNT(u) FROM User u WHERE LOWER(u.roles) = :role", Long.class)
                .setParameter("role", role.toLowerCase())
                .getSingleResult();
    }

}