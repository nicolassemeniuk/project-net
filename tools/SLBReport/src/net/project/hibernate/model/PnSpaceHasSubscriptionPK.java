package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasSubscriptionPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal subscriptionId;

    /** full constructor */
    public PnSpaceHasSubscriptionPK(BigDecimal spaceId, BigDecimal subscriptionId) {
        this.spaceId = spaceId;
        this.subscriptionId = subscriptionId;
    }

    /** default constructor */
    public PnSpaceHasSubscriptionPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getSubscriptionId() {
        return this.subscriptionId;
    }

    public void setSubscriptionId(BigDecimal subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("subscriptionId", getSubscriptionId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasSubscriptionPK) ) return false;
        PnSpaceHasSubscriptionPK castOther = (PnSpaceHasSubscriptionPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getSubscriptionId(), castOther.getSubscriptionId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getSubscriptionId())
            .toHashCode();
    }

}
