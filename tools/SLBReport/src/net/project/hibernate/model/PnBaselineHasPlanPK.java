package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnBaselineHasPlanPK implements Serializable {

    /** identifier field */
    private BigDecimal baselineId;

    /** identifier field */
    private BigDecimal planId;

    /** full constructor */
    public PnBaselineHasPlanPK(BigDecimal baselineId, BigDecimal planId) {
        this.baselineId = baselineId;
        this.planId = planId;
    }

    /** default constructor */
    public PnBaselineHasPlanPK() {
    }

    public BigDecimal getBaselineId() {
        return this.baselineId;
    }

    public void setBaselineId(BigDecimal baselineId) {
        this.baselineId = baselineId;
    }

    public BigDecimal getPlanId() {
        return this.planId;
    }

    public void setPlanId(BigDecimal planId) {
        this.planId = planId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("baselineId", getBaselineId())
            .append("planId", getPlanId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnBaselineHasPlanPK) ) return false;
        PnBaselineHasPlanPK castOther = (PnBaselineHasPlanPK) other;
        return new EqualsBuilder()
            .append(this.getBaselineId(), castOther.getBaselineId())
            .append(this.getPlanId(), castOther.getPlanId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBaselineId())
            .append(getPlanId())
            .toHashCode();
    }

}
