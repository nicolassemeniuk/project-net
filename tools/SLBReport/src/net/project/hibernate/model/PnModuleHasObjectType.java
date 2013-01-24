package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnModuleHasObjectType implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnModuleHasObjectTypePK comp_id;

    /** persistent field */
    private int isOwner;

    /** nullable persistent field */
    private net.project.hibernate.model.PnModule pnModule;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObjectType pnObjectType;

    /** full constructor */
    public PnModuleHasObjectType(net.project.hibernate.model.PnModuleHasObjectTypePK comp_id, int isOwner, net.project.hibernate.model.PnModule pnModule, net.project.hibernate.model.PnObjectType pnObjectType) {
        this.comp_id = comp_id;
        this.isOwner = isOwner;
        this.pnModule = pnModule;
        this.pnObjectType = pnObjectType;
    }

    /** default constructor */
    public PnModuleHasObjectType() {
    }

    /** minimal constructor */
    public PnModuleHasObjectType(net.project.hibernate.model.PnModuleHasObjectTypePK comp_id, int isOwner) {
        this.comp_id = comp_id;
        this.isOwner = isOwner;
    }

    public net.project.hibernate.model.PnModuleHasObjectTypePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnModuleHasObjectTypePK comp_id) {
        this.comp_id = comp_id;
    }

    public int getIsOwner() {
        return this.isOwner;
    }

    public void setIsOwner(int isOwner) {
        this.isOwner = isOwner;
    }

    public net.project.hibernate.model.PnModule getPnModule() {
        return this.pnModule;
    }

    public void setPnModule(net.project.hibernate.model.PnModule pnModule) {
        this.pnModule = pnModule;
    }

    public net.project.hibernate.model.PnObjectType getPnObjectType() {
        return this.pnObjectType;
    }

    public void setPnObjectType(net.project.hibernate.model.PnObjectType pnObjectType) {
        this.pnObjectType = pnObjectType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnModuleHasObjectType) ) return false;
        PnModuleHasObjectType castOther = (PnModuleHasObjectType) other;
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
