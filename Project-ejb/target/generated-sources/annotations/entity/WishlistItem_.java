package entity;

import entity.Product;
import entity.User;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-07-15T11:17:14")
@StaticMetamodel(WishlistItem.class)
public class WishlistItem_ { 

    public static volatile SingularAttribute<WishlistItem, Product> product;
    public static volatile SingularAttribute<WishlistItem, Long> id;
    public static volatile SingularAttribute<WishlistItem, User> customer;

}