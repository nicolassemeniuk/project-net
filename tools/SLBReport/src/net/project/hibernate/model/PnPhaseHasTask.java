package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPhaseHasTask implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPhaseHasTaskPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPhase pnPhase;

    /** nullable persistent field */
    private net.project.hibernate.model.PnTask pnTask;

    /** full constructor */
    public PnPhaseHasTask(net.project.hibernate.model.PnPhaseHasTaskPK comp_id, net.project.hibernate.model.PnPhase pnPhase, net.project.hibernate.model.PnTask pnTask) {
        this.comp_id = comp_id;
        this.pnPhase = pnPhase;
        this.pnTask = pnTask;
    }

    /** default constructor */
    public PnPhaseHasTask() {
    }

    /** minimal constructor */
    public PnPhaseHasTask(net.project.hibernate.model.PnPhaseHasTaskPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnPhaseHasTaskPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPhaseHasTaskPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnPhase getPnPhase() {
        return this.pnPhase;
    }

    public void setPnPhase(net.project.hibernate.model.PnPhase pnPhase) {
        this.pnPhase = pnPhase;
    }

    public net.project.hibernate.model.PnTask getPnTask() {
        return this.pnTask;
    }

    public void setPnTask(net.project.hibernate.model.PnTask pnTask) {
        this.pnTask = pnTask;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPhaseHasTask) ) return false;
        PnPhaseHasTask castOther = (PnPhaseHasTask) other;
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
