package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSubHasNotifyTypePK implements Serializable {

    /** identifier field */
    private BigDecimal notificationTypeId;

    /** identifier field */
    private BigDecimal subscriptionId;

    /** full constructor */
    public PnSubHasNotifyTypePK(BigDecimal notificationTypeId, BigDecimal subscriptionId) {
        this.notificationTypeId = notificationTypeId;
        this.subscriptionId = subscriptionId;
    }

    /** default constructor */
    public PnSubHasNotifyTypePK() {
    }

    public BigDecimal getNotificationTypeId() {
        return this.notificationTypeId;
    }

    public void setNotificationTypeId(BigDecimal notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }

    public BigDecimal getSubscriptionId() {
        return this.subscriptionId;
    }

    public void setSubscriptionId(BigDecimal subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("notificationTypeId", getNotificationTypeId())
            .append("subscriptionId", getSubscriptionId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSubHasNotifyTypePK) ) return false;
        PnSubHasNotifyTypePK castOther = (PnSubHasNotifyTypePK) other;
        return new EqualsBuilder()
            .append(this.getNotificationTypeId(), castOther.getNotificationTypeId())
            .append(this.getSubscriptionId(), castOther.getSubscriptionId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getNotificationTypeId())
            .append(getSubscriptionId())
            .toHashCode();
    }

}
