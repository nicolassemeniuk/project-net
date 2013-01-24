package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowStepCopy implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnWorkflowStepCopyPK comp_id;

    /** persistent field */
    private BigDecimal workflowId;

    /** full constructor */
    public PnWorkflowStepCopy(net.project.hibernate.model.PnWorkflowStepCopyPK comp_id, BigDecimal workflowId) {
        this.comp_id = comp_id;
        this.workflowId = workflowId;
    }

    /** default constructor */
    public PnWorkflowStepCopy() {
    }

    public net.project.hibernate.model.PnWorkflowStepCopyPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnWorkflowStepCopyPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getWorkflowId() {
        return this.workflowId;
    }

    public void setWorkflowId(BigDecimal workflowId) {
        this.workflowId = workflowId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnWorkflowStepCopy) ) return false;
        PnWorkflowStepCopy castOther = (PnWorkflowStepCopy) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
