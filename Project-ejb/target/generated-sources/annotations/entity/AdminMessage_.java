package entity;

import entity.User;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-07-08T15:13:26")
@StaticMetamodel(AdminMessage.class)
public class AdminMessage_ { 

    public static volatile SingularAttribute<AdminMessage, Date> sentDate;
    public static volatile SingularAttribute<AdminMessage, Boolean> read;
    public static volatile SingularAttribute<AdminMessage, User> recipient;
    public static volatile SingularAttribute<AdminMessage, Long> id;
    public static volatile SingularAttribute<AdminMessage, String> message;

}