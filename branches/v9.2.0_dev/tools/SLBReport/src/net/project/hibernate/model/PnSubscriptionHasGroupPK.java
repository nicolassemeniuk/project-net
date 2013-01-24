package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSubscriptionHasGroupPK implements Serializable {

    /** identifier field */
    private BigDecimal subscriptionId;

    /** identifier field */
    private BigDecimal deliveryGroupId;

    /** full constructor */
    public PnSubscriptionHasGroupPK(BigDecimal subscriptionId, BigDecimal deliveryGroupId) {
        this.subscriptionId = subscriptionId;
        this.deliveryGroupId = deliveryGroupId;
    }

    /** default constructor */
    public PnSubscriptionHasGroupPK() {
    }

    public BigDecimal getSubscriptionId() {
        return this.subscriptionId;
    }

    public void setSubscriptionId(BigDecimal subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public BigDecimal getDeliveryGroupId() {
        return this.deliveryGroupId;
    }

    public void setDeliveryGroupId(BigDecimal deliveryGroupId) {
        this.deliveryGroupId = deliveryGroupId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("subscriptionId", getSubscriptionId())
            .append("deliveryGroupId", getDeliveryGroupId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSubscriptionHasGroupPK) ) return false;
        PnSubscriptionHasGroupPK castOther = (PnSubscriptionHasGroupPK) other;
        return new EqualsBuilder()
            .append(this.getSubscriptionId(), castOther.getSubscriptionId())
            .append(this.getDeliveryGroupId(), castOther.getDeliveryGroupId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSubscriptionId())
            .append(getDeliveryGroupId())
            .toHashCode();
    }

}
