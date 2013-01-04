package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnObjectTypeSupportsActionPK implements Serializable {

    /** identifier field */
    private String objectType;

    /** identifier field */
    private BigDecimal actionId;

    /** full constructor */
    public PnObjectTypeSupportsActionPK(String objectType, BigDecimal actionId) {
        this.objectType = objectType;
        this.actionId = actionId;
    }

    /** default constructor */
    public PnObjectTypeSupportsActionPK() {
    }

    public String getObjectType() {
        return this.objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public BigDecimal getActionId() {
        return this.actionId;
    }

    public void setActionId(BigDecimal actionId) {
        this.actionId = actionId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectType", getObjectType())
            .append("actionId", getActionId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnObjectTypeSupportsActionPK) ) return false;
        PnObjectTypeSupportsActionPK castOther = (PnObjectTypeSupportsActionPK) other;
        return new EqualsBuilder()
            .append(this.getObjectType(), castOther.getObjectType())
            .append(this.getActionId(), castOther.getActionId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getObjectType())
            .append(getActionId())
            .toHashCode();
    }

}
