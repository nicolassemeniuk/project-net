package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnBaselineHasPlan implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnBaselineHasPlanPK comp_id;

    /** persistent field */
    private BigDecimal planVersionId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnBaseline pnBaseline;

    /** full constructor */
    public PnBaselineHasPlan(net.project.hibernate.model.PnBaselineHasPlanPK comp_id, BigDecimal planVersionId, net.project.hibernate.model.PnBaseline pnBaseline) {
        this.comp_id = comp_id;
        this.planVersionId = planVersionId;
        this.pnBaseline = pnBaseline;
    }

    /** default constructor */
    public PnBaselineHasPlan() {
    }

    /** minimal constructor */
    public PnBaselineHasPlan(net.project.hibernate.model.PnBaselineHasPlanPK comp_id, BigDecimal planVersionId) {
        this.comp_id = comp_id;
        this.planVersionId = planVersionId;
    }

    public net.project.hibernate.model.PnBaselineHasPlanPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnBaselineHasPlanPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getPlanVersionId() {
        return this.planVersionId;
    }

    public void setPlanVersionId(BigDecimal planVersionId) {
        this.planVersionId = planVersionId;
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
        if ( !(other instanceof PnBaselineHasPlan) ) return false;
        PnBaselineHasPlan castOther = (PnBaselineHasPlan) other;
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
