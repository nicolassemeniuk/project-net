package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnElementPropertyPK implements Serializable {

    /** identifier field */
    private BigDecimal elementId;

    /** identifier field */
    private BigDecimal propertyId;

    /** identifier field */
    private BigDecimal clientTypeId;

    /** full constructor */
    public PnElementPropertyPK(BigDecimal elementId, BigDecimal propertyId, BigDecimal clientTypeId) {
        this.elementId = elementId;
        this.propertyId = propertyId;
        this.clientTypeId = clientTypeId;
    }

    /** default constructor */
    public PnElementPropertyPK() {
    }

    public BigDecimal getElementId() {
        return this.elementId;
    }

    public void setElementId(BigDecimal elementId) {
        this.elementId = elementId;
    }

    public BigDecimal getPropertyId() {
        return this.propertyId;
    }

    public void setPropertyId(BigDecimal propertyId) {
        this.propertyId = propertyId;
    }

    public BigDecimal getClientTypeId() {
        return this.clientTypeId;
    }

    public void setClientTypeId(BigDecimal clientTypeId) {
        this.clientTypeId = clientTypeId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("elementId", getElementId())
            .append("propertyId", getPropertyId())
            .append("clientTypeId", getClientTypeId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnElementPropertyPK) ) return false;
        PnElementPropertyPK castOther = (PnElementPropertyPK) other;
        return new EqualsBuilder()
            .append(this.getElementId(), castOther.getElementId())
            .append(this.getPropertyId(), castOther.getPropertyId())
            .append(this.getClientTypeId(), castOther.getClientTypeId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getElementId())
            .append(getPropertyId())
            .append(getClientTypeId())
            .toHashCode();
    }

}
