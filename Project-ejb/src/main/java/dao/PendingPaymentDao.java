/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.PendingPayment;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author DELL
 */
@Stateless
public class PendingPaymentDao {
    
    @PersistenceContext(unitName = "un_projectTest")
    private EntityManager em;
    
    
    public void create(PendingPayment pendingPayment){
        em.persist(pendingPayment);
    }
    
    public void update(PendingPayment pendingPayment){
        em.merge(pendingPayment);
    } 
    
    public PendingPayment findByUuid(String transactionUuid){
        try{
            return em.createQuery("SELECT p FROM PendingPayment p WHERE p.transactionUuid = :uuid",PendingPayment.class)
                    .setParameter("uuid", transactionUuid)
                    .getSingleResult();
            
        }catch (NoResultException e){
            return null;
        }
        
    }
    
    
}
