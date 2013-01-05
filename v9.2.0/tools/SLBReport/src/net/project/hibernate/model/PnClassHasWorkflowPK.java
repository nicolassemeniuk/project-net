package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassHasWorkflowPK implements Serializable {

    /** identifier field */
    private BigDecimal classId;

    /** identifier field */
    private BigDecimal workflowId;

    /** full constructor */
    public PnClassHasWorkflowPK(BigDecimal classId, BigDecimal workflowId) {
        this.classId = classId;
        this.workflowId = workflowId;
    }

    /** default constructor */
    public PnClassHasWorkflowPK() {
    }

    public BigDecimal getClassId() {
        return this.classId;
    }

    public void setClassId(BigDecimal classId) {
        this.classId = classId;
    }

    public BigDecimal getWorkflowId() {
        return this.workflowId;
    }

    public void setWorkflowId(BigDecimal workflowId) {
        this.workflowId = workflowId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("classId", getClassId())
            .append("workflowId", getWorkflowId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnClassHasWorkflowPK) ) return false;
        PnClassHasWorkflowPK castOther = (PnClassHasWorkflowPK) other;
        return new EqualsBuilder()
            .append(this.getClassId(), castOther.getClassId())
            .append(this.getWorkflowId(), castOther.getWorkflowId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getClassId())
            .append(getWorkflowId())
            .toHashCode();
    }

}
