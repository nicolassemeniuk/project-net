package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassFieldPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal classId;

    /** identifier field */
    private BigDecimal fieldId;

    /** full constructor */
    public PnClassFieldPK(BigDecimal spaceId, BigDecimal classId, BigDecimal fieldId) {
        this.spaceId = spaceId;
        this.classId = classId;
        this.fieldId = fieldId;
    }

    /** default constructor */
    public PnClassFieldPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
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

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("classId", getClassId())
            .append("fieldId", getFieldId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnClassFieldPK) ) return false;
        PnClassFieldPK castOther = (PnClassFieldPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getClassId(), castOther.getClassId())
            .append(this.getFieldId(), castOther.getFieldId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getClassId())
            .append(getFieldId())
            .toHashCode();
    }

}
