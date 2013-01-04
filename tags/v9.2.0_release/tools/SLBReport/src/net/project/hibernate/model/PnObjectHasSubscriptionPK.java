package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnObjectHasSubscriptionPK implements Serializable {

    /** identifier field */
    private BigDecimal subscriptionId;

    /** identifier field */
    private BigDecimal objectId;

    /** full constructor */
    public PnObjectHasSubscriptionPK(BigDecimal subscriptionId, BigDecimal objectId) {
        this.subscriptionId = subscriptionId;
        this.objectId = objectId;
    }

    /** default constructor */
    public PnObjectHasSubscriptionPK() {
    }

    public BigDecimal getSubscriptionId() {
        return this.subscriptionId;
    }

    public void setSubscriptionId(BigDecimal subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("subscriptionId", getSubscriptionId())
            .append("objectId", getObjectId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnObjectHasSubscriptionPK) ) return false;
        PnObjectHasSubscriptionPK castOther = (PnObjectHasSubscriptionPK) other;
        return new EqualsBuilder()
            .append(this.getSubscriptionId(), castOther.getSubscriptionId())
            .append(this.getObjectId(), castOther.getObjectId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSubscriptionId())
            .append(getObjectId())
            .toHashCode();
    }

}
