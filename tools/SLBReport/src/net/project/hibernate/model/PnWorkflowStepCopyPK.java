package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowStepCopyPK implements Serializable {

    /** identifier field */
    private BigDecimal stepId;

    /** identifier field */
    private BigDecimal toWorkflowId;

    /** identifier field */
    private BigDecimal toStepId;

    /** full constructor */
    public PnWorkflowStepCopyPK(BigDecimal stepId, BigDecimal toWorkflowId, BigDecimal toStepId) {
        this.stepId = stepId;
        this.toWorkflowId = toWorkflowId;
        this.toStepId = toStepId;
    }

    /** default constructor */
    public PnWorkflowStepCopyPK() {
    }

    public BigDecimal getStepId() {
        return this.stepId;
    }

    public void setStepId(BigDecimal stepId) {
        this.stepId = stepId;
    }

    public BigDecimal getToWorkflowId() {
        return this.toWorkflowId;
    }

    public void setToWorkflowId(BigDecimal toWorkflowId) {
        this.toWorkflowId = toWorkflowId;
    }

    public BigDecimal getToStepId() {
        return this.toStepId;
    }

    public void setToStepId(BigDecimal toStepId) {
        this.toStepId = toStepId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("stepId", getStepId())
            .append("toWorkflowId", getToWorkflowId())
            .append("toStepId", getToStepId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnWorkflowStepCopyPK) ) return false;
        PnWorkflowStepCopyPK castOther = (PnWorkflowStepCopyPK) other;
        return new EqualsBuilder()
            .append(this.getStepId(), castOther.getStepId())
            .append(this.getToWorkflowId(), castOther.getToWorkflowId())
            .append(this.getToStepId(), castOther.getToStepId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getStepId())
            .append(getToWorkflowId())
            .append(getToStepId())
            .toHashCode();
    }

}
