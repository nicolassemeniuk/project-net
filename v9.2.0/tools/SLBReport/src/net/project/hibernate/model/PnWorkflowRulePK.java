package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowRulePK implements Serializable {

    /** identifier field */
    private BigDecimal ruleId;

    /** identifier field */
    private BigDecimal transitionId;

    /** identifier field */
    private BigDecimal workflowId;

    /** full constructor */
    public PnWorkflowRulePK(BigDecimal ruleId, BigDecimal transitionId, BigDecimal workflowId) {
        this.ruleId = ruleId;
        this.transitionId = transitionId;
        this.workflowId = workflowId;
    }

    /** default constructor */
    public PnWorkflowRulePK() {
    }

    public BigDecimal getRuleId() {
        return this.ruleId;
    }

    public void setRuleId(BigDecimal ruleId) {
        this.ruleId = ruleId;
    }

    public BigDecimal getTransitionId() {
        return this.transitionId;
    }

    public void setTransitionId(BigDecimal transitionId) {
        this.transitionId = transitionId;
    }

    public BigDecimal getWorkflowId() {
        return this.workflowId;
    }

    public void setWorkflowId(BigDecimal workflowId) {
        this.workflowId = workflowId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ruleId", getRuleId())
            .append("transitionId", getTransitionId())
            .append("workflowId", getWorkflowId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnWorkflowRulePK) ) return false;
        PnWorkflowRulePK castOther = (PnWorkflowRulePK) other;
        return new EqualsBuilder()
            .append(this.getRuleId(), castOther.getRuleId())
            .append(this.getTransitionId(), castOther.getTransitionId())
            .append(this.getWorkflowId(), castOther.getWorkflowId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getRuleId())
            .append(getTransitionId())
            .append(getWorkflowId())
            .toHashCode();
    }

}
