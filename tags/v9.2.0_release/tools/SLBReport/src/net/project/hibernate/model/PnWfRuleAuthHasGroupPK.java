package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWfRuleAuthHasGroupPK implements Serializable {

    /** identifier field */
    private BigDecimal ruleId;

    /** identifier field */
    private BigDecimal workflowId;

    /** identifier field */
    private BigDecimal transitionId;

    /** identifier field */
    private BigDecimal groupId;

    /** identifier field */
    private BigDecimal stepId;

    /** full constructor */
    public PnWfRuleAuthHasGroupPK(BigDecimal ruleId, BigDecimal workflowId, BigDecimal transitionId, BigDecimal groupId, BigDecimal stepId) {
        this.ruleId = ruleId;
        this.workflowId = workflowId;
        this.transitionId = transitionId;
        this.groupId = groupId;
        this.stepId = stepId;
    }

    /** default constructor */
    public PnWfRuleAuthHasGroupPK() {
    }

    public BigDecimal getRuleId() {
        return this.ruleId;
    }

    public void setRuleId(BigDecimal ruleId) {
        this.ruleId = ruleId;
    }

    public BigDecimal getWorkflowId() {
        return this.workflowId;
    }

    public void setWorkflowId(BigDecimal workflowId) {
        this.workflowId = workflowId;
    }

    public BigDecimal getTransitionId() {
        return this.transitionId;
    }

    public void setTransitionId(BigDecimal transitionId) {
        this.transitionId = transitionId;
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

    public String toString() {
        return new ToStringBuilder(this)
            .append("ruleId", getRuleId())
            .append("workflowId", getWorkflowId())
            .append("transitionId", getTransitionId())
            .append("groupId", getGroupId())
            .append("stepId", getStepId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnWfRuleAuthHasGroupPK) ) return false;
        PnWfRuleAuthHasGroupPK castOther = (PnWfRuleAuthHasGroupPK) other;
        return new EqualsBuilder()
            .append(this.getRuleId(), castOther.getRuleId())
            .append(this.getWorkflowId(), castOther.getWorkflowId())
            .append(this.getTransitionId(), castOther.getTransitionId())
            .append(this.getGroupId(), castOther.getGroupId())
            .append(this.getStepId(), castOther.getStepId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getRuleId())
            .append(getWorkflowId())
            .append(getTransitionId())
            .append(getGroupId())
            .append(getStepId())
            .toHashCode();
    }

}
