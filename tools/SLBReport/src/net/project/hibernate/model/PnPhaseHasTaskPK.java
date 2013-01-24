package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPhaseHasTaskPK implements Serializable {

    /** identifier field */
    private BigDecimal phaseId;

    /** identifier field */
    private BigDecimal taskId;

    /** full constructor */
    public PnPhaseHasTaskPK(BigDecimal phaseId, BigDecimal taskId) {
        this.phaseId = phaseId;
        this.taskId = taskId;
    }

    /** default constructor */
    public PnPhaseHasTaskPK() {
    }

    public BigDecimal getPhaseId() {
        return this.phaseId;
    }

    public void setPhaseId(BigDecimal phaseId) {
        this.phaseId = phaseId;
    }

    public BigDecimal getTaskId() {
        return this.taskId;
    }

    public void setTaskId(BigDecimal taskId) {
        this.taskId = taskId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("phaseId", getPhaseId())
            .append("taskId", getTaskId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPhaseHasTaskPK) ) return false;
        PnPhaseHasTaskPK castOther = (PnPhaseHasTaskPK) other;
        return new EqualsBuilder()
            .append(this.getPhaseId(), castOther.getPhaseId())
            .append(this.getTaskId(), castOther.getTaskId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPhaseId())
            .append(getTaskId())
            .toHashCode();
    }

}
