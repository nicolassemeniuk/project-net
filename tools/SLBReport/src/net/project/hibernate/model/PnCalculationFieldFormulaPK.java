package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCalculationFieldFormulaPK implements Serializable {

    /** identifier field */
    private BigDecimal classId;

    /** identifier field */
    private BigDecimal fieldId;

    /** identifier field */
    private BigDecimal orderId;

    /** full constructor */
    public PnCalculationFieldFormulaPK(BigDecimal classId, BigDecimal fieldId, BigDecimal orderId) {
        this.classId = classId;
        this.fieldId = fieldId;
        this.orderId = orderId;
    }

    /** default constructor */
    public PnCalculationFieldFormulaPK() {
    }

    public BigDecimal getClassId() {
        return this.classId;
    }

    public void setClassId(BigDecimal classId) {
        this.classId = classId;
    }

    public BigDecimal getFieldId() {
        return this.fieldId;
    }

    public void setFieldId(BigDecimal fieldId) {
        this.fieldId = fieldId;
    }

    public BigDecimal getOrderId() {
        return this.orderId;
    }

    public void setOrderId(BigDecimal orderId) {
        this.orderId = orderId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("classId", getClassId())
            .append("fieldId", getFieldId())
            .append("orderId", getOrderId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnCalculationFieldFormulaPK) ) return false;
        PnCalculationFieldFormulaPK castOther = (PnCalculationFieldFormulaPK) other;
        return new EqualsBuilder()
            .append(this.getClassId(), castOther.getClassId())
            .append(this.getFieldId(), castOther.getFieldId())
            .append(this.getOrderId(), castOther.getOrderId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getClassId())
            .append(getFieldId())
            .append(getOrderId())
            .toHashCode();
    }

}
