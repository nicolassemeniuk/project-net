package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasClassList implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasClassListPK comp_id;

    /** persistent field */
    private int isDefault;

    /** nullable persistent field */
    private net.project.hibernate.model.PnClassList pnClassList;

    /** full constructor */
    public PnSpaceHasClassList(net.project.hibernate.model.PnSpaceHasClassListPK comp_id, int isDefault, net.project.hibernate.model.PnClassList pnClassList) {
        this.comp_id = comp_id;
        this.isDefault = isDefault;
        this.pnClassList = pnClassList;
    }

    /** default constructor */
    public PnSpaceHasClassList() {
    }

    /** minimal constructor */
    public PnSpaceHasClassList(net.project.hibernate.model.PnSpaceHasClassListPK comp_id, int isDefault) {
        this.comp_id = comp_id;
        this.isDefault = isDefault;
    }

    public net.project.hibernate.model.PnSpaceHasClassListPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasClassListPK comp_id) {
        this.comp_id = comp_id;
    }

    public int getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public net.project.hibernate.model.PnClassList getPnClassList() {
        return this.pnClassList;
    }

    public void setPnClassList(net.project.hibernate.model.PnClassList pnClassList) {
        this.pnClassList = pnClassList;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasClassList) ) return false;
        PnSpaceHasClassList castOther = (PnSpaceHasClassList) other;
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
