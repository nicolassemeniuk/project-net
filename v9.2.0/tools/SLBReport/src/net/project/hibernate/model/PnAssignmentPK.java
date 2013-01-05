package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnAssignmentPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private BigDecimal objectId;

    /** full constructor */
    public PnAssignmentPK(BigDecimal spaceId, BigDecimal personId, BigDecimal objectId) {
        this.spaceId = spaceId;
        this.personId = personId;
        this.objectId = objectId;
    }

    /** default constructor */
    public PnAssignmentPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("personId", getPersonId())
            .append("objectId", getObjectId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnAssignmentPK) ) return false;
        PnAssignmentPK castOther = (PnAssignmentPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getObjectId(), castOther.getObjectId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getPersonId())
            .append(getObjectId())
            .toHashCode();
    }

}
