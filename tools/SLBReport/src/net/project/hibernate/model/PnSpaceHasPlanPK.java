package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasPlanPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal planId;

    /** full constructor */
    public PnSpaceHasPlanPK(BigDecimal spaceId, BigDecimal planId) {
        this.spaceId = spaceId;
        this.planId = planId;
    }

    /** default constructor */
    public PnSpaceHasPlanPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getPlanId() {
        return this.planId;
    }

    public void setPlanId(BigDecimal planId) {
        this.planId = planId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("planId", getPlanId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasPlanPK) ) return false;
        PnSpaceHasPlanPK castOther = (PnSpaceHasPlanPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getPlanId(), castOther.getPlanId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getPlanId())
            .toHashCode();
    }

}
