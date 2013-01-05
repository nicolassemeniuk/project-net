package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowStepHasGroupPK implements Serializable {

    /** identifier field */
    private BigDecimal groupId;

    /** identifier field */
    private BigDecimal stepId;

    /** identifier field */
    private BigDecimal workflowId;

    /** full constructor */
    public PnWorkflowStepHasGroupPK(BigDecimal groupId, BigDecimal stepId, BigDecimal workflowId) {
        this.groupId = groupId;
        this.stepId = stepId;
        this.workflowId = workflowId;
    }

    /** default constructor */
    public PnWorkflowStepHasGroupPK() {
    }

    public BigDecimal getGroupId() {
        return this.groupId;
    }

    public void setGroupId(BigDecimal groupId) {
        this.groupId = groupId;
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
            .append("groupId", getGroupId())
            .append("stepId", getStepId())
            .append("workflowId", getWorkflowId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnWorkflowStepHasGroupPK) ) return false;
        PnWorkflowStepHasGroupPK castOther = (PnWorkflowStepHasGroupPK) other;
        return new EqualsBuilder()
            .append(this.getGroupId(), castOther.getGroupId())
            .append(this.getStepId(), castOther.getStepId())
            .append(this.getWorkflowId(), castOther.getWorkflowId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getGroupId())
            .append(getStepId())
            .append(getWorkflowId())
            .toHashCode();
    }

}
