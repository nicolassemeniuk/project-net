package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonHasStateReg implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPersonHasStateRegPK comp_id;

    /** nullable persistent field */
    private String otherRegState;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** nullable persistent field */
    private net.project.hibernate.model.PnStateLookup pnStateLookup;

    /** full constructor */
    public PnPersonHasStateReg(net.project.hibernate.model.PnPersonHasStateRegPK comp_id, String otherRegState, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnStateLookup pnStateLookup) {
        this.comp_id = comp_id;
        this.otherRegState = otherRegState;
        this.pnPerson = pnPerson;
        this.pnStateLookup = pnStateLookup;
    }

    /** default constructor */
    public PnPersonHasStateReg() {
    }

    /** minimal constructor */
    public PnPersonHasStateReg(net.project.hibernate.model.PnPersonHasStateRegPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnPersonHasStateRegPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPersonHasStateRegPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getOtherRegState() {
        return this.otherRegState;
    }

    public void setOtherRegState(String otherRegState) {
        this.otherRegState = otherRegState;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnStateLookup getPnStateLookup() {
        return this.pnStateLookup;
    }

    public void setPnStateLookup(net.project.hibernate.model.PnStateLookup pnStateLookup) {
        this.pnStateLookup = pnStateLookup;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonHasStateReg) ) return false;
        PnPersonHasStateReg castOther = (PnPersonHasStateReg) other;
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
