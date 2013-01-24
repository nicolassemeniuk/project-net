package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassTypeElementPK implements Serializable {

    /** identifier field */
    private BigDecimal classTypeId;

    /** identifier field */
    private BigDecimal elementId;

    /** full constructor */
    public PnClassTypeElementPK(BigDecimal classTypeId, BigDecimal elementId) {
        this.classTypeId = classTypeId;
        this.elementId = elementId;
    }

    /** default constructor */
    public PnClassTypeElementPK() {
    }

    public BigDecimal getClassTypeId() {
        return this.classTypeId;
    }

    public void setClassTypeId(BigDecimal classTypeId) {
        this.classTypeId = classTypeId;
    }

    public BigDecimal getElementId() {
        return this.elementId;
    }

    public void setElementId(BigDecimal elementId) {
        this.elementId = elementId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("classTypeId", getClassTypeId())
            .append("elementId", getElementId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnClassTypeElementPK) ) return false;
        PnClassTypeElementPK castOther = (PnClassTypeElementPK) other;
        return new EqualsBuilder()
            .append(this.getClassTypeId(), castOther.getClassTypeId())
            .append(this.getElementId(), castOther.getElementId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getClassTypeId())
            .append(getElementId())
            .toHashCode();
    }

}
