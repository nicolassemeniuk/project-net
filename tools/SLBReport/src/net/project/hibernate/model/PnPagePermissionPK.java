package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPagePermissionPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private String page;

    /** identifier field */
    private BigDecimal groupId;

    /** full constructor */
    public PnPagePermissionPK(BigDecimal spaceId, String page, BigDecimal groupId) {
        this.spaceId = spaceId;
        this.page = page;
        this.groupId = groupId;
    }

    /** default constructor */
    public PnPagePermissionPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public String getPage() {
        return this.page;
    }

    public void setPage(String page) {
        this.page = page;
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
            .append("page", getPage())
            .append("groupId", getGroupId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPagePermissionPK) ) return false;
        PnPagePermissionPK castOther = (PnPagePermissionPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getPage(), castOther.getPage())
            .append(this.getGroupId(), castOther.getGroupId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getPage())
            .append(getGroupId())
            .toHashCode();
    }

}
