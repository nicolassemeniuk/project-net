package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasClass implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasClassPK comp_id;

    /** persistent field */
    private int isOwner;

    /** nullable persistent field */
    private net.project.hibernate.model.PnClass pnClass;

    /** full constructor */
    public PnSpaceHasClass(net.project.hibernate.model.PnSpaceHasClassPK comp_id, int isOwner, net.project.hibernate.model.PnClass pnClass) {
        this.comp_id = comp_id;
        this.isOwner = isOwner;
        this.pnClass = pnClass;
    }

    /** default constructor */
    public PnSpaceHasClass() {
    }

    /** minimal constructor */
    public PnSpaceHasClass(net.project.hibernate.model.PnSpaceHasClassPK comp_id, int isOwner) {
        this.comp_id = comp_id;
        this.isOwner = isOwner;
    }

    public net.project.hibernate.model.PnSpaceHasClassPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasClassPK comp_id) {
        this.comp_id = comp_id;
    }

    public int getIsOwner() {
        return this.isOwner;
    }

    public void setIsOwner(int isOwner) {
        this.isOwner = isOwner;
    }

    public net.project.hibernate.model.PnClass getPnClass() {
        return this.pnClass;
    }

    public void setPnClass(net.project.hibernate.model.PnClass pnClass) {
        this.pnClass = pnClass;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasClass) ) return false;
        PnSpaceHasClass castOther = (PnSpaceHasClass) other;
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
