package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTaskBaselinePK implements Serializable {

    /** identifier field */
    private BigDecimal taskId;

    /** identifier field */
    private BigDecimal baselineId;

    /** full constructor */
    public PnTaskBaselinePK(BigDecimal taskId, BigDecimal baselineId) {
        this.taskId = taskId;
        this.baselineId = baselineId;
    }

    /** default constructor */
    public PnTaskBaselinePK() {
    }

    public BigDecimal getTaskId() {
        return this.taskId;
    }

    public void setTaskId(BigDecimal taskId) {
        this.taskId = taskId;
    }

    public BigDecimal getBaselineId() {
        return this.baselineId;
    }

    public void setBaselineId(BigDecimal baselineId) {
        this.baselineId = baselineId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("taskId", getTaskId())
            .append("baselineId", getBaselineId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnTaskBaselinePK) ) return false;
        PnTaskBaselinePK castOther = (PnTaskBaselinePK) other;
        return new EqualsBuilder()
            .append(this.getTaskId(), castOther.getTaskId())
            .append(this.getBaselineId(), castOther.getBaselineId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getTaskId())
            .append(getBaselineId())
            .toHashCode();
    }

}
