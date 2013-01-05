package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTaskDependencyPK implements Serializable {

    /** identifier field */
    private BigDecimal taskId;

    /** identifier field */
    private BigDecimal dependencyId;

    /** identifier field */
    private BigDecimal dependencyTypeId;

    /** full constructor */
    public PnTaskDependencyPK(BigDecimal taskId, BigDecimal dependencyId, BigDecimal dependencyTypeId) {
        this.taskId = taskId;
        this.dependencyId = dependencyId;
        this.dependencyTypeId = dependencyTypeId;
    }

    /** default constructor */
    public PnTaskDependencyPK() {
    }

    public BigDecimal getTaskId() {
        return this.taskId;
    }

    public void setTaskId(BigDecimal taskId) {
        this.taskId = taskId;
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
            .append("dependencyId", getDependencyId())
            .append("dependencyTypeId", getDependencyTypeId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnTaskDependencyPK) ) return false;
        PnTaskDependencyPK castOther = (PnTaskDependencyPK) other;
        return new EqualsBuilder()
            .append(this.getTaskId(), castOther.getTaskId())
            .append(this.getDependencyId(), castOther.getDependencyId())
            .append(this.getDependencyTypeId(), castOther.getDependencyTypeId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getTaskId())
            .append(getDependencyId())
            .append(getDependencyTypeId())
            .toHashCode();
    }

}
