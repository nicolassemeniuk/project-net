package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnBaselineHasTaskPK implements Serializable {

    /** identifier field */
    private BigDecimal baselineId;

    /** identifier field */
    private BigDecimal taskId;

    /** full constructor */
    public PnBaselineHasTaskPK(BigDecimal baselineId, BigDecimal taskId) {
        this.baselineId = baselineId;
        this.taskId = taskId;
    }

    /** default constructor */
    public PnBaselineHasTaskPK() {
    }

    public BigDecimal getBaselineId() {
        return this.baselineId;
    }

    public void setBaselineId(BigDecimal baselineId) {
        this.baselineId = baselineId;
    }

    public BigDecimal getTaskId() {
        return this.taskId;
    }

    public void setTaskId(BigDecimal taskId) {
        this.taskId = taskId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("baselineId", getBaselineId())
            .append("taskId", getTaskId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnBaselineHasTaskPK) ) return false;
        PnBaselineHasTaskPK castOther = (PnBaselineHasTaskPK) other;
        return new EqualsBuilder()
            .append(this.getBaselineId(), castOther.getBaselineId())
            .append(this.getTaskId(), castOther.getTaskId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBaselineId())
            .append(getTaskId())
            .toHashCode();
    }

}
