package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasNew implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasNewPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnNew pnNew;

    /** full constructor */
    public PnSpaceHasNew(net.project.hibernate.model.PnSpaceHasNewPK comp_id, net.project.hibernate.model.PnNew pnNew) {
        this.comp_id = comp_id;
        this.pnNew = pnNew;
    }

    /** default constructor */
    public PnSpaceHasNew() {
    }

    /** minimal constructor */
    public PnSpaceHasNew(net.project.hibernate.model.PnSpaceHasNewPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSpaceHasNewPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasNewPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnNew getPnNew() {
        return this.pnNew;
    }

    public void setPnNew(net.project.hibernate.model.PnNew pnNew) {
        this.pnNew = pnNew;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasNew) ) return false;
        PnSpaceHasNew castOther = (PnSpaceHasNew) other;
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
