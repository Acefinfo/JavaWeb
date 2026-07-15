package entity;

import entity.User;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-07-15T11:17:14")
@StaticMetamodel(Invoice.class)
public class Invoice_ { 

    public static volatile SingularAttribute<Invoice, Double> unitPrice;
    public static volatile SingularAttribute<Invoice, String> sellerUsername;
    public static volatile SingularAttribute<Invoice, Integer> quantity;
    public static volatile SingularAttribute<Invoice, Long> sellerId;
    public static volatile SingularAttribute<Invoice, Long> productId;
    public static volatile SingularAttribute<Invoice, Double> totalPrice;
    public static volatile SingularAttribute<Invoice, Long> id;
    public static volatile SingularAttribute<Invoice, Date> orderDate;
    public static volatile SingularAttribute<Invoice, String> productName;
    public static volatile SingularAttribute<Invoice, User> buyer;

}