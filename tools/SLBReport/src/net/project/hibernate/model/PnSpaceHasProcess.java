package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasProcess implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasProcessPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnProcess pnProcess;

    /** full constructor */
    public PnSpaceHasProcess(net.project.hibernate.model.PnSpaceHasProcessPK comp_id, net.project.hibernate.model.PnProcess pnProcess) {
        this.comp_id = comp_id;
        this.pnProcess = pnProcess;
    }

    /** default constructor */
    public PnSpaceHasProcess() {
    }

    /** minimal constructor */
    public PnSpaceHasProcess(net.project.hibernate.model.PnSpaceHasProcessPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSpaceHasProcessPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasProcessPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnProcess getPnProcess() {
        return this.pnProcess;
    }

    public void setPnProcess(net.project.hibernate.model.PnProcess pnProcess) {
        this.pnProcess = pnProcess;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasProcess) ) return false;
        PnSpaceHasProcess castOther = (PnSpaceHasProcess) other;
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
