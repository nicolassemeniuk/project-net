package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowStepPK implements Serializable {

    /** identifier field */
    private BigDecimal stepId;

    /** identifier field */
    private BigDecimal workflowId;

    /** full constructor */
    public PnWorkflowStepPK(BigDecimal stepId, BigDecimal workflowId) {
        this.stepId = stepId;
        this.workflowId = workflowId;
    }

    /** default constructor */
    public PnWorkflowStepPK() {
    }

    public BigDecimal getStepId() {
        return this.stepId;
    }

    public void setStepId(BigDecimal stepId) {
        this.stepId = stepId;
    }

    public BigDecimal getWorkflowId() {
        return this.workflowId;
    }

    public void setWorkflowId(BigDecimal workflowId) {
        this.workflowId = workflowId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("stepId", getStepId())
            .append("workflowId", getWorkflowId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnWorkflowStepPK) ) return false;
        PnWorkflowStepPK castOther = (PnWorkflowStepPK) other;
        return new EqualsBuilder()
            .append(this.getStepId(), castOther.getStepId())
            .append(this.getWorkflowId(), castOther.getWorkflowId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getStepId())
            .append(getWorkflowId())
            .toHashCode();
    }

}
