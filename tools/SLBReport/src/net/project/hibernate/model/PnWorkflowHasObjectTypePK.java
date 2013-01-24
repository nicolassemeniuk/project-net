package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowHasObjectTypePK implements Serializable {

    /** identifier field */
    private BigDecimal workflowId;

    /** identifier field */
    private String objectType;

    /** full constructor */
    public PnWorkflowHasObjectTypePK(BigDecimal workflowId, String objectType) {
        this.workflowId = workflowId;
        this.objectType = objectType;
    }

    /** default constructor */
    public PnWorkflowHasObjectTypePK() {
    }

    public BigDecimal getWorkflowId() {
        return this.workflowId;
    }

    public void setWorkflowId(BigDecimal workflowId) {
        this.workflowId = workflowId;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("workflowId", getWorkflowId())
            .append("objectType", getObjectType())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnWorkflowHasObjectTypePK) ) return false;
        PnWorkflowHasObjectTypePK castOther = (PnWorkflowHasObjectTypePK) other;
        return new EqualsBuilder()
            .append(this.getWorkflowId(), castOther.getWorkflowId())
            .append(this.getObjectType(), castOther.getObjectType())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getWorkflowId())
            .append(getObjectType())
            .toHashCode();
    }

}
