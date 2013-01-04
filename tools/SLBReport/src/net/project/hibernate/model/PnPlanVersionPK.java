package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPlanVersionPK implements Serializable {

    /** identifier field */
    private BigDecimal planId;

    /** identifier field */
    private BigDecimal planVersionId;

    /** full constructor */
    public PnPlanVersionPK(BigDecimal planId, BigDecimal planVersionId) {
        this.planId = planId;
        this.planVersionId = planVersionId;
    }

    /** default constructor */
    public PnPlanVersionPK() {
    }

    public BigDecimal getPlanId() {
        return this.planId;
    }

    public void setPlanId(BigDecimal planId) {
        this.planId = planId;
    }

    public BigDecimal getPlanVersionId() {
        return this.planVersionId;
    }

    public void setPlanVersionId(BigDecimal planVersionId) {
        this.planVersionId = planVersionId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("planId", getPlanId())
            .append("planVersionId", getPlanVersionId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPlanVersionPK) ) return false;
        PnPlanVersionPK castOther = (PnPlanVersionPK) other;
        return new EqualsBuilder()
            .append(this.getPlanId(), castOther.getPlanId())
            .append(this.getPlanVersionId(), castOther.getPlanVersionId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPlanId())
            .append(getPlanVersionId())
            .toHashCode();
    }

}
