package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasPlan implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasPlanPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPlan pnPlan;

    /** full constructor */
    public PnSpaceHasPlan(net.project.hibernate.model.PnSpaceHasPlanPK comp_id, net.project.hibernate.model.PnPlan pnPlan) {
        this.comp_id = comp_id;
        this.pnPlan = pnPlan;
    }

    /** default constructor */
    public PnSpaceHasPlan() {
    }

    /** minimal constructor */
    public PnSpaceHasPlan(net.project.hibernate.model.PnSpaceHasPlanPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSpaceHasPlanPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasPlanPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnPlan getPnPlan() {
        return this.pnPlan;
    }

    public void setPnPlan(net.project.hibernate.model.PnPlan pnPlan) {
        this.pnPlan = pnPlan;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasPlan) ) return false;
        PnSpaceHasPlan castOther = (PnSpaceHasPlan) other;
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
