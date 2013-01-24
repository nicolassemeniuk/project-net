package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasDirectoryPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal directoryId;

    /** full constructor */
    public PnSpaceHasDirectoryPK(BigDecimal spaceId, BigDecimal directoryId) {
        this.spaceId = spaceId;
        this.directoryId = directoryId;
    }

    /** default constructor */
    public PnSpaceHasDirectoryPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getDirectoryId() {
        return this.directoryId;
    }

    public void setDirectoryId(BigDecimal directoryId) {
        this.directoryId = directoryId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("directoryId", getDirectoryId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasDirectoryPK) ) return false;
        PnSpaceHasDirectoryPK castOther = (PnSpaceHasDirectoryPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getDirectoryId(), castOther.getDirectoryId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getDirectoryId())
            .toHashCode();
    }

}
