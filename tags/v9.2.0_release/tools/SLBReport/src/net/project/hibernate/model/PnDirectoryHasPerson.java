package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDirectoryHasPerson implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnDirectoryHasPersonPK comp_id;

    /** persistent field */
    private int isDefault;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDirectory pnDirectory;

    /** full constructor */
    public PnDirectoryHasPerson(net.project.hibernate.model.PnDirectoryHasPersonPK comp_id, int isDefault, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnDirectory pnDirectory) {
        this.comp_id = comp_id;
        this.isDefault = isDefault;
        this.pnPerson = pnPerson;
        this.pnDirectory = pnDirectory;
    }

    /** default constructor */
    public PnDirectoryHasPerson() {
    }

    /** minimal constructor */
    public PnDirectoryHasPerson(net.project.hibernate.model.PnDirectoryHasPersonPK comp_id, int isDefault) {
        this.comp_id = comp_id;
        this.isDefault = isDefault;
    }

    public net.project.hibernate.model.PnDirectoryHasPersonPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnDirectoryHasPersonPK comp_id) {
        this.comp_id = comp_id;
    }

    public int getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnDirectory getPnDirectory() {
        return this.pnDirectory;
    }

    public void setPnDirectory(net.project.hibernate.model.PnDirectory pnDirectory) {
        this.pnDirectory = pnDirectory;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDirectoryHasPerson) ) return false;
        PnDirectoryHasPerson castOther = (PnDirectoryHasPerson) other;
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
