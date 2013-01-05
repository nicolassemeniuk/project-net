package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPlanHasTaskPK implements Serializable {

    /** identifier field */
    private BigDecimal planId;

    /** identifier field */
    private BigDecimal taskId;

    /** full constructor */
    public PnPlanHasTaskPK(BigDecimal planId, BigDecimal taskId) {
        this.planId = planId;
        this.taskId = taskId;
    }

    /** default constructor */
    public PnPlanHasTaskPK() {
    }

    public BigDecimal getPlanId() {
        return this.planId;
    }

    public void setPlanId(BigDecimal planId) {
        this.planId = planId;
    }

    public BigDecimal getTaskId() {
        return this.taskId;
    }

    public void setTaskId(BigDecimal taskId) {
        this.taskId = taskId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("planId", getPlanId())
            .append("taskId", getTaskId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPlanHasTaskPK) ) return false;
        PnPlanHasTaskPK castOther = (PnPlanHasTaskPK) other;
        return new EqualsBuilder()
            .append(this.getPlanId(), castOther.getPlanId())
            .append(this.getTaskId(), castOther.getTaskId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPlanId())
            .append(getTaskId())
            .toHashCode();
    }

}
