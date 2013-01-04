package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassFieldPropertyPK implements Serializable {

    /** identifier field */
    private BigDecimal classId;

    /** identifier field */
    private BigDecimal fieldId;

    /** identifier field */
    private String property;

    /** identifier field */
    private BigDecimal clientTypeId;

    /** full constructor */
    public PnClassFieldPropertyPK(BigDecimal classId, BigDecimal fieldId, String property, BigDecimal clientTypeId) {
        this.classId = classId;
        this.fieldId = fieldId;
        this.property = property;
        this.clientTypeId = clientTypeId;
    }

    /** default constructor */
    public PnClassFieldPropertyPK() {
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

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public BigDecimal getClientTypeId() {
        return this.clientTypeId;
    }

    public void setClientTypeId(BigDecimal clientTypeId) {
        this.clientTypeId = clientTypeId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("classId", getClassId())
            .append("fieldId", getFieldId())
            .append("property", getProperty())
            .append("clientTypeId", getClientTypeId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnClassFieldPropertyPK) ) return false;
        PnClassFieldPropertyPK castOther = (PnClassFieldPropertyPK) other;
        return new EqualsBuilder()
            .append(this.getClassId(), castOther.getClassId())
            .append(this.getFieldId(), castOther.getFieldId())
            .append(this.getProperty(), castOther.getProperty())
            .append(this.getClientTypeId(), castOther.getClientTypeId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getClassId())
            .append(getFieldId())
            .append(getProperty())
            .append(getClientTypeId())
            .toHashCode();
    }

}
