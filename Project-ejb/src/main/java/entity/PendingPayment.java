/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author DELL
 */
@Entity
public class PendingPayment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "transaction_uuid", nullable = false, unique = true)
    private String transactionUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @Column(name = "purchase_type", nullable = false)
    private String type; // "SINGLE" or "CART"


    @Column(name = "items_snapshot", nullable = false, length = 1000)
    private String itemsSnapshot;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "status", nullable = false)
    private String status; 

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false)
    private Date createdDate = new Date();

    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getTransactionUuid() { 
        return transactionUuid; 
    }
    public void setTransactionUuid(String transactionUuid) { 
        this.transactionUuid = transactionUuid; 
    }

    public User getBuyer() { 
        return buyer; 
    }
    public void setBuyer(User buyer) { 
        this.buyer = buyer; 
    }

    public String getType() { 
        return type; 
    }
    public void setType(String type) { 
        this.type = type; 
    }

    public String getItemsSnapshot() { 
        return itemsSnapshot; 
    }
    public void setItemsSnapshot(String itemsSnapshot) { 
        this.itemsSnapshot = itemsSnapshot; 
    }

    public Double getAmount() { 
        return amount; 
    }
    public void setAmount(Double amount) { 
        this.amount = amount; 
    }

    public String getStatus() { 
        return status; 
    }
    public void setStatus(String status) { 
        this.status = status; 
    }

    public Date getCreatedDate() { 
        return createdDate; 
    }
    public void setCreatedDate(Date createdDate) { 
        this.createdDate = createdDate; 
    }
}