package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasModule implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasModulePK comp_id;

    /** persistent field */
    private int isActive;

    /** nullable persistent field */
    private net.project.hibernate.model.PnModule pnModule;

    /** persistent field */
    private Set pnModulePermissions;

    /** full constructor */
    public PnSpaceHasModule(net.project.hibernate.model.PnSpaceHasModulePK comp_id, int isActive, net.project.hibernate.model.PnModule pnModule, Set pnModulePermissions) {
        this.comp_id = comp_id;
        this.isActive = isActive;
        this.pnModule = pnModule;
        this.pnModulePermissions = pnModulePermissions;
    }

    /** default constructor */
    public PnSpaceHasModule() {
    }

    /** minimal constructor */
    public PnSpaceHasModule(net.project.hibernate.model.PnSpaceHasModulePK comp_id, int isActive, Set pnModulePermissions) {
        this.comp_id = comp_id;
        this.isActive = isActive;
        this.pnModulePermissions = pnModulePermissions;
    }
    
    public PnSpaceHasModule(net.project.hibernate.model.PnSpaceHasModulePK comp_id, int isActive) {
        this.comp_id = comp_id;
        this.isActive = isActive;
    } 

    public net.project.hibernate.model.PnSpaceHasModulePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasModulePK comp_id) {
        this.comp_id = comp_id;
    }

    public int getIsActive() {
        return this.isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public net.project.hibernate.model.PnModule getPnModule() {
        return this.pnModule;
    }

    public void setPnModule(net.project.hibernate.model.PnModule pnModule) {
        this.pnModule = pnModule;
    }

    public Set getPnModulePermissions() {
        return this.pnModulePermissions;
    }

    public void setPnModulePermissions(Set pnModulePermissions) {
        this.pnModulePermissions = pnModulePermissions;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasModule) ) return false;
        PnSpaceHasModule castOther = (PnSpaceHasModule) other;
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
