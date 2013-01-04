package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDefaultObjectPermissionPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private String objectType;

    /** identifier field */
    private BigDecimal groupId;

    /** full constructor */
    public PnDefaultObjectPermissionPK(BigDecimal spaceId, String objectType, BigDecimal groupId) {
        this.spaceId = spaceId;
        this.objectType = objectType;
        this.groupId = groupId;
    }

    /** default constructor */
    public PnDefaultObjectPermissionPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public BigDecimal getGroupId() {
        return this.groupId;
    }

    public void setGroupId(BigDecimal groupId) {
        this.groupId = groupId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("objectType", getObjectType())
            .append("groupId", getGroupId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDefaultObjectPermissionPK) ) return false;
        PnDefaultObjectPermissionPK castOther = (PnDefaultObjectPermissionPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getObjectType(), castOther.getObjectType())
            .append(this.getGroupId(), castOther.getGroupId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getObjectType())
            .append(getGroupId())
            .toHashCode();
    }

}
