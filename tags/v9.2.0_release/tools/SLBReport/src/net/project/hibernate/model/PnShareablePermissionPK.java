package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnShareablePermissionPK implements Serializable {

    /** identifier field */
    private BigDecimal objectId;

    /** identifier field */
    private BigDecimal permittedObjectId;

    /** full constructor */
    public PnShareablePermissionPK(BigDecimal objectId, BigDecimal permittedObjectId) {
        this.objectId = objectId;
        this.permittedObjectId = permittedObjectId;
    }

    /** default constructor */
    public PnShareablePermissionPK() {
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public BigDecimal getPermittedObjectId() {
        return this.permittedObjectId;
    }

    public void setPermittedObjectId(BigDecimal permittedObjectId) {
        this.permittedObjectId = permittedObjectId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .append("permittedObjectId", getPermittedObjectId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnShareablePermissionPK) ) return false;
        PnShareablePermissionPK castOther = (PnShareablePermissionPK) other;
        return new EqualsBuilder()
            .append(this.getObjectId(), castOther.getObjectId())
            .append(this.getPermittedObjectId(), castOther.getPermittedObjectId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getObjectId())
            .append(getPermittedObjectId())
            .toHashCode();
    }

}
