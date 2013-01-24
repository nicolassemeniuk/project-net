package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPhaseHasDeliverable implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPhaseHasDeliverablePK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDeliverable pnDeliverable;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPhase pnPhase;

    /** full constructor */
    public PnPhaseHasDeliverable(net.project.hibernate.model.PnPhaseHasDeliverablePK comp_id, net.project.hibernate.model.PnDeliverable pnDeliverable, net.project.hibernate.model.PnPhase pnPhase) {
        this.comp_id = comp_id;
        this.pnDeliverable = pnDeliverable;
        this.pnPhase = pnPhase;
    }

    /** default constructor */
    public PnPhaseHasDeliverable() {
    }

    /** minimal constructor */
    public PnPhaseHasDeliverable(net.project.hibernate.model.PnPhaseHasDeliverablePK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnPhaseHasDeliverablePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPhaseHasDeliverablePK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnDeliverable getPnDeliverable() {
        return this.pnDeliverable;
    }

    public void setPnDeliverable(net.project.hibernate.model.PnDeliverable pnDeliverable) {
        this.pnDeliverable = pnDeliverable;
    }

    public net.project.hibernate.model.PnPhase getPnPhase() {
        return this.pnPhase;
    }

    public void setPnPhase(net.project.hibernate.model.PnPhase pnPhase) {
        this.pnPhase = pnPhase;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPhaseHasDeliverable) ) return false;
        PnPhaseHasDeliverable castOther = (PnPhaseHasDeliverable) other;
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
