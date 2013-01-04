package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnModulePermissionPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal groupId;

    /** identifier field */
    private BigDecimal moduleId;

    /** full constructor */
    public PnModulePermissionPK(BigDecimal spaceId, BigDecimal groupId, BigDecimal moduleId) {
        this.spaceId = spaceId;
        this.groupId = groupId;
        this.moduleId = moduleId;
    }

    /** default constructor */
    public PnModulePermissionPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getGroupId() {
        return this.groupId;
    }

    public void setGroupId(BigDecimal groupId) {
        this.groupId = groupId;
    }

    public BigDecimal getModuleId() {
        return this.moduleId;
    }

    public void setModuleId(BigDecimal moduleId) {
        this.moduleId = moduleId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("groupId", getGroupId())
            .append("moduleId", getModuleId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnModulePermissionPK) ) return false;
        PnModulePermissionPK castOther = (PnModulePermissionPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getGroupId(), castOther.getGroupId())
            .append(this.getModuleId(), castOther.getModuleId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getGroupId())
            .append(getModuleId())
            .toHashCode();
    }

}
