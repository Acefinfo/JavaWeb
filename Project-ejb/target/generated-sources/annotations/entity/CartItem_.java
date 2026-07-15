package entity;

import entity.Product;
import entity.User;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-07-15T16:02:51")
@StaticMetamodel(CartItem.class)
public class CartItem_ { 

    public static volatile SingularAttribute<CartItem, Product> product;
    public static volatile SingularAttribute<CartItem, Integer> quantity;
    public static volatile SingularAttribute<CartItem, Long> id;
    public static volatile SingularAttribute<CartItem, User> customer;

}