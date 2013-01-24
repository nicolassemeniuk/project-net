package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTaskVersionPK implements Serializable {

    /** identifier field */
    private BigDecimal taskId;

    /** identifier field */
    private BigDecimal taskVersionId;

    /** full constructor */
    public PnTaskVersionPK(BigDecimal taskId, BigDecimal taskVersionId) {
        this.taskId = taskId;
        this.taskVersionId = taskVersionId;
    }

    /** default constructor */
    public PnTaskVersionPK() {
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

    public String toString() {
        return new ToStringBuilder(this)
            .append("taskId", getTaskId())
            .append("taskVersionId", getTaskVersionId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnTaskVersionPK) ) return false;
        PnTaskVersionPK castOther = (PnTaskVersionPK) other;
        return new EqualsBuilder()
            .append(this.getTaskId(), castOther.getTaskId())
            .append(this.getTaskVersionId(), castOther.getTaskVersionId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getTaskId())
            .append(getTaskVersionId())
            .toHashCode();
    }

}
