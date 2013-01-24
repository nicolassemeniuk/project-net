package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTaskCommentPK implements Serializable {

    /** identifier field */
    private BigDecimal taskId;

    /** identifier field */
    private BigDecimal baselineId;

    /** identifier field */
    private BigDecimal seq;

    /** full constructor */
    public PnTaskCommentPK(BigDecimal taskId, BigDecimal baselineId, BigDecimal seq) {
        this.taskId = taskId;
        this.baselineId = baselineId;
        this.seq = seq;
    }

    /** default constructor */
    public PnTaskCommentPK() {
    }

    public BigDecimal getTaskId() {
        return this.taskId;
    }

    public void setTaskId(BigDecimal taskId) {
        this.taskId = taskId;
    }

    public BigDecimal getBaselineId() {
        return this.baselineId;
    }

    public void setBaselineId(BigDecimal baselineId) {
        this.baselineId = baselineId;
    }

    public BigDecimal getSeq() {
        return this.seq;
    }

    public void setSeq(BigDecimal seq) {
        this.seq = seq;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("taskId", getTaskId())
            .append("baselineId", getBaselineId())
            .append("seq", getSeq())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnTaskCommentPK) ) return false;
        PnTaskCommentPK castOther = (PnTaskCommentPK) other;
        return new EqualsBuilder()
            .append(this.getTaskId(), castOther.getTaskId())
            .append(this.getBaselineId(), castOther.getBaselineId())
            .append(this.getSeq(), castOther.getSeq())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getTaskId())
            .append(getBaselineId())
            .append(getSeq())
            .toHashCode();
    }

}
