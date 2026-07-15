package entity;

import entity.User;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2026-07-15T11:17:14")
@StaticMetamodel(PendingPayment.class)
public class PendingPayment_ { 

    public static volatile SingularAttribute<PendingPayment, String> transactionUuid;
    public static volatile SingularAttribute<PendingPayment, Double> amount;
    public static volatile SingularAttribute<PendingPayment, Date> createdDate;
    public static volatile SingularAttribute<PendingPayment, Long> id;
    public static volatile SingularAttribute<PendingPayment, String> itemsSnapshot;
    public static volatile SingularAttribute<PendingPayment, String> type;
    public static volatile SingularAttribute<PendingPayment, User> buyer;
    public static volatile SingularAttribute<PendingPayment, String> status;

}