package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTaskDependencyVersionPK implements Serializable {

    /** identifier field */
    private BigDecimal taskId;

    /** identifier field */
    private BigDecimal taskVersionId;

    /** identifier field */
    private BigDecimal dependencyId;

    /** identifier field */
    private BigDecimal dependencyTypeId;

    /** full constructor */
    public PnTaskDependencyVersionPK(BigDecimal taskId, BigDecimal taskVersionId, BigDecimal dependencyId, BigDecimal dependencyTypeId) {
        this.taskId = taskId;
        this.taskVersionId = taskVersionId;
        this.dependencyId = dependencyId;
        this.dependencyTypeId = dependencyTypeId;
    }

    /** default constructor */
    public PnTaskDependencyVersionPK() {
    }

    public BigDecimal getTaskId() {
        return this.taskId;
    }

    public void setTaskId(BigDecimal taskId) {
        this.taskId = taskId;
    }

    public BigDecimal getTaskVersionId() {
        return this.taskVersionId;
    }

    public void setTaskVersionId(BigDecimal taskVersionId) {
        this.taskVersionId = taskVersionId;
    }

    public BigDecimal getDependencyId() {
        return this.dependencyId;
    }

    public void setDependencyId(BigDecimal dependencyId) {
        this.dependencyId = dependencyId;
    }

    public BigDecimal getDependencyTypeId() {
        return this.dependencyTypeId;
    }

    public void setDependencyTypeId(BigDecimal dependencyTypeId) {
        this.dependencyTypeId = dependencyTypeId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("taskId", getTaskId())
            .append("taskVersionId", getTaskVersionId())
            .append("dependencyId", getDependencyId())
            .append("dependencyTypeId", getDependencyTypeId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnTaskDependencyVersionPK) ) return false;
        PnTaskDependencyVersionPK castOther = (PnTaskDependencyVersionPK) other;
        return new EqualsBuilder()
            .append(this.getTaskId(), castOther.getTaskId())
            .append(this.getTaskVersionId(), castOther.getTaskVersionId())
            .append(this.getDependencyId(), castOther.getDependencyId())
            .append(this.getDependencyTypeId(), castOther.getDependencyTypeId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getTaskId())
            .append(getTaskVersionId())
            .append(getDependencyId())
            .append(getDependencyTypeId())
            .toHashCode();
    }

}
