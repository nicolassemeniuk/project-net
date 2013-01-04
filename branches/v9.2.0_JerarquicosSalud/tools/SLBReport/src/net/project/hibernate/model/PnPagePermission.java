package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPagePermission implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPagePermissionPK comp_id;

    /** persistent field */
    private long actions;

    /** nullable persistent field */
    private net.project.hibernate.model.PnGroup pnGroup;

    /** full constructor */
    public PnPagePermission(net.project.hibernate.model.PnPagePermissionPK comp_id, long actions, net.project.hibernate.model.PnGroup pnGroup) {
        this.comp_id = comp_id;
        this.actions = actions;
        this.pnGroup = pnGroup;
    }

    /** default constructor */
    public PnPagePermission() {
    }

    /** minimal constructor */
    public PnPagePermission(net.project.hibernate.model.PnPagePermissionPK comp_id, long actions) {
        this.comp_id = comp_id;
        this.actions = actions;
    }

    public net.project.hibernate.model.PnPagePermissionPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPagePermissionPK comp_id) {
        this.comp_id = comp_id;
    }

    public long getActions() {
        return this.actions;
    }

    public void setActions(long actions) {
        this.actions = actions;
    }

    public net.project.hibernate.model.PnGroup getPnGroup() {
        return this.pnGroup;
    }

    public void setPnGroup(net.project.hibernate.model.PnGroup pnGroup) {
        this.pnGroup = pnGroup;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPagePermission) ) return false;
        PnPagePermission castOther = (PnPagePermission) other;
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
