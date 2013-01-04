package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWfRuleAuthPK implements Serializable {

    /** identifier field */
    private BigDecimal ruleId;

    /** identifier field */
    private BigDecimal workflowId;

    /** identifier field */
    private BigDecimal transitionId;

    /** full constructor */
    public PnWfRuleAuthPK(BigDecimal ruleId, BigDecimal workflowId, BigDecimal transitionId) {
        this.ruleId = ruleId;
        this.workflowId = workflowId;
        this.transitionId = transitionId;
    }

    /** default constructor */
    public PnWfRuleAuthPK() {
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

    public String toString() {
        return new ToStringBuilder(this)
            .append("ruleId", getRuleId())
            .append("workflowId", getWorkflowId())
            .append("transitionId", getTransitionId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnWfRuleAuthPK) ) return false;
        PnWfRuleAuthPK castOther = (PnWfRuleAuthPK) other;
        return new EqualsBuilder()
            .append(this.getRuleId(), castOther.getRuleId())
            .append(this.getWorkflowId(), castOther.getWorkflowId())
            .append(this.getTransitionId(), castOther.getTransitionId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getRuleId())
            .append(getWorkflowId())
            .append(getTransitionId())
            .toHashCode();
    }

}
