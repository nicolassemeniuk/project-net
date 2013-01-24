package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnObjectTypeSubscriptionPK implements Serializable {

    /** identifier field */
    private BigDecimal subscriptionId;

    /** identifier field */
    private String objectType;

    /** full constructor */
    public PnObjectTypeSubscriptionPK(BigDecimal subscriptionId, String objectType) {
        this.subscriptionId = subscriptionId;
        this.objectType = objectType;
    }

    /** default constructor */
    public PnObjectTypeSubscriptionPK() {
    }

    public BigDecimal getSubscriptionId() {
        return this.subscriptionId;
    }

    public void setSubscriptionId(BigDecimal subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("subscriptionId", getSubscriptionId())
            .append("objectType", getObjectType())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnObjectTypeSubscriptionPK) ) return false;
        PnObjectTypeSubscriptionPK castOther = (PnObjectTypeSubscriptionPK) other;
        return new EqualsBuilder()
            .append(this.getSubscriptionId(), castOther.getSubscriptionId())
            .append(this.getObjectType(), castOther.getObjectType())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSubscriptionId())
            .append(getObjectType())
            .toHashCode();
    }

}
