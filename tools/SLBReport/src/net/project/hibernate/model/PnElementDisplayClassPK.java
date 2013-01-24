package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnElementDisplayClassPK implements Serializable {

    /** identifier field */
    private BigDecimal elementId;

    /** identifier field */
    private BigDecimal classId;

    /** full constructor */
    public PnElementDisplayClassPK(BigDecimal elementId, BigDecimal classId) {
        this.elementId = elementId;
        this.classId = classId;
    }

    /** default constructor */
    public PnElementDisplayClassPK() {
    }

    public BigDecimal getElementId() {
        return this.elementId;
    }

    public void setElementId(BigDecimal elementId) {
        this.elementId = elementId;
    }

    public BigDecimal getClassId() {
        return this.classId;
    }

    public void setClassId(BigDecimal classId) {
        this.classId = classId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("elementId", getElementId())
            .append("classId", getClassId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnElementDisplayClassPK) ) return false;
        PnElementDisplayClassPK castOther = (PnElementDisplayClassPK) other;
        return new EqualsBuilder()
            .append(this.getElementId(), castOther.getElementId())
            .append(this.getClassId(), castOther.getClassId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getElementId())
            .append(getClassId())
            .toHashCode();
    }

}
