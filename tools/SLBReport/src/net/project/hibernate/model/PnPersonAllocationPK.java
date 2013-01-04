package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonAllocationPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private BigDecimal allocationId;

    /** full constructor */
    public PnPersonAllocationPK(BigDecimal spaceId, BigDecimal personId, BigDecimal allocationId) {
        this.spaceId = spaceId;
        this.personId = personId;
        this.allocationId = allocationId;
    }

    /** default constructor */
    public PnPersonAllocationPK() {
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

    public BigDecimal getAllocationId() {
        return this.allocationId;
    }

    public void setAllocationId(BigDecimal allocationId) {
        this.allocationId = allocationId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("personId", getPersonId())
            .append("allocationId", getAllocationId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonAllocationPK) ) return false;
        PnPersonAllocationPK castOther = (PnPersonAllocationPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getAllocationId(), castOther.getAllocationId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getPersonId())
            .append(getAllocationId())
            .toHashCode();
    }

}
