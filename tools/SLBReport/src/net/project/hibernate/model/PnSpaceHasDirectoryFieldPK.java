package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasDirectoryFieldPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal directoryFieldId;

    /** full constructor */
    public PnSpaceHasDirectoryFieldPK(BigDecimal spaceId, BigDecimal directoryFieldId) {
        this.spaceId = spaceId;
        this.directoryFieldId = directoryFieldId;
    }

    /** default constructor */
    public PnSpaceHasDirectoryFieldPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getDirectoryFieldId() {
        return this.directoryFieldId;
    }

    public void setDirectoryFieldId(BigDecimal directoryFieldId) {
        this.directoryFieldId = directoryFieldId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("directoryFieldId", getDirectoryFieldId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasDirectoryFieldPK) ) return false;
        PnSpaceHasDirectoryFieldPK castOther = (PnSpaceHasDirectoryFieldPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getDirectoryFieldId(), castOther.getDirectoryFieldId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getDirectoryFieldId())
            .toHashCode();
    }

}
