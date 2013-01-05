package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnBaselineHasTask implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnBaselineHasTaskPK comp_id;

    /** persistent field */
    private BigDecimal taskVersionId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnBaseline pnBaseline;

    /** full constructor */
    public PnBaselineHasTask(net.project.hibernate.model.PnBaselineHasTaskPK comp_id, BigDecimal taskVersionId, net.project.hibernate.model.PnBaseline pnBaseline) {
        this.comp_id = comp_id;
        this.taskVersionId = taskVersionId;
        this.pnBaseline = pnBaseline;
    }

    /** default constructor */
    public PnBaselineHasTask() {
    }

    /** minimal constructor */
    public PnBaselineHasTask(net.project.hibernate.model.PnBaselineHasTaskPK comp_id, BigDecimal taskVersionId) {
        this.comp_id = comp_id;
        this.taskVersionId = taskVersionId;
    }

    public net.project.hibernate.model.PnBaselineHasTaskPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnBaselineHasTaskPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getTaskVersionId() {
        return this.taskVersionId;
    }

    public void setTaskVersionId(BigDecimal taskVersionId) {
        this.taskVersionId = taskVersionId;
    }

    public net.project.hibernate.model.PnBaseline getPnBaseline() {
        return this.pnBaseline;
    }

    public void setPnBaseline(net.project.hibernate.model.PnBaseline pnBaseline) {
        this.pnBaseline = pnBaseline;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnBaselineHasTask) ) return false;
        PnBaselineHasTask castOther = (PnBaselineHasTask) other;
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
