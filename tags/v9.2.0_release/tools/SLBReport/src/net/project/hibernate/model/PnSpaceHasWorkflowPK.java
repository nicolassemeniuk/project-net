package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasWorkflowPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal workflowId;

    /** full constructor */
    public PnSpaceHasWorkflowPK(BigDecimal spaceId, BigDecimal workflowId) {
        this.spaceId = spaceId;
        this.workflowId = workflowId;
    }

    /** default constructor */
    public PnSpaceHasWorkflowPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getWorkflowId() {
        return this.workflowId;
    }

    public void setWorkflowId(BigDecimal workflowId) {
        this.workflowId = workflowId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("workflowId", getWorkflowId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasWorkflowPK) ) return false;
        PnSpaceHasWorkflowPK castOther = (PnSpaceHasWorkflowPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getWorkflowId(), castOther.getWorkflowId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getWorkflowId())
            .toHashCode();
    }

}
