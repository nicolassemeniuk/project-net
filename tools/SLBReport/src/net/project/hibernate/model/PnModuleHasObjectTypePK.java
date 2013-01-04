package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnModuleHasObjectTypePK implements Serializable {

    /** identifier field */
    private BigDecimal moduleId;

    /** identifier field */
    private String objectType;

    /** full constructor */
    public PnModuleHasObjectTypePK(BigDecimal moduleId, String objectType) {
        this.moduleId = moduleId;
        this.objectType = objectType;
    }

    /** default constructor */
    public PnModuleHasObjectTypePK() {
    }

    public BigDecimal getModuleId() {
        return this.moduleId;
    }

    public void setModuleId(BigDecimal moduleId) {
        this.moduleId = moduleId;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("moduleId", getModuleId())
            .append("objectType", getObjectType())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnModuleHasObjectTypePK) ) return false;
        PnModuleHasObjectTypePK castOther = (PnModuleHasObjectTypePK) other;
        return new EqualsBuilder()
            .append(this.getModuleId(), castOther.getModuleId())
            .append(this.getObjectType(), castOther.getObjectType())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getModuleId())
            .append(getObjectType())
            .toHashCode();
    }

}
