package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassListFilterPK implements Serializable {

    /** identifier field */
    private BigDecimal classId;

    /** identifier field */
    private BigDecimal listId;

    /** identifier field */
    private BigDecimal fieldId;

    /** identifier field */
    private BigDecimal valueId;

    /** full constructor */
    public PnClassListFilterPK(BigDecimal classId, BigDecimal listId, BigDecimal fieldId, BigDecimal valueId) {
        this.classId = classId;
        this.listId = listId;
        this.fieldId = fieldId;
        this.valueId = valueId;
    }

    /** default constructor */
    public PnClassListFilterPK() {
    }

    public BigDecimal getClassId() {
        return this.classId;
    }

    public void setClassId(BigDecimal classId) {
        this.classId = classId;
    }

    public BigDecimal getListId() {
        return this.listId;
    }

    public void setListId(BigDecimal listId) {
        this.listId = listId;
    }

    public BigDecimal getFieldId() {
        return this.fieldId;
    }

    public void setFieldId(BigDecimal fieldId) {
        this.fieldId = fieldId;
    }

    public BigDecimal getValueId() {
        return this.valueId;
    }

    public void setValueId(BigDecimal valueId) {
        this.valueId = valueId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("classId", getClassId())
            .append("listId", getListId())
            .append("fieldId", getFieldId())
            .append("valueId", getValueId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnClassListFilterPK) ) return false;
        PnClassListFilterPK castOther = (PnClassListFilterPK) other;
        return new EqualsBuilder()
            .append(this.getClassId(), castOther.getClassId())
            .append(this.getListId(), castOther.getListId())
            .append(this.getFieldId(), castOther.getFieldId())
            .append(this.getValueId(), castOther.getValueId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getClassId())
            .append(getListId())
            .append(getFieldId())
            .append(getValueId())
            .toHashCode();
    }

}
