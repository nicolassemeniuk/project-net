package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTaskHistoryPK implements Serializable {

    /** identifier field */
    private BigDecimal taskId;

    /** identifier field */
    private BigDecimal taskHistoryId;

    /** full constructor */
    public PnTaskHistoryPK(BigDecimal taskId, BigDecimal taskHistoryId) {
        this.taskId = taskId;
        this.taskHistoryId = taskHistoryId;
    }

    /** default constructor */
    public PnTaskHistoryPK() {
    }

    public BigDecimal getTaskId() {
        return this.taskId;
    }

    public void setTaskId(BigDecimal taskId) {
        this.taskId = taskId;
    }

    public BigDecimal getTaskHistoryId() {
        return this.taskHistoryId;
    }

    public void setTaskHistoryId(BigDecimal taskHistoryId) {
        this.taskHistoryId = taskHistoryId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("taskId", getTaskId())
            .append("taskHistoryId", getTaskHistoryId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnTaskHistoryPK) ) return false;
        PnTaskHistoryPK castOther = (PnTaskHistoryPK) other;
        return new EqualsBuilder()
            .append(this.getTaskId(), castOther.getTaskId())
            .append(this.getTaskHistoryId(), castOther.getTaskHistoryId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getTaskId())
            .append(getTaskHistoryId())
            .toHashCode();
    }

}
