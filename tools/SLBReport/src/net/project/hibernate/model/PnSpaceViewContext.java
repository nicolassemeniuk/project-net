package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceViewContext implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceViewContextPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnView pnView;

    /** full constructor */
    public PnSpaceViewContext(net.project.hibernate.model.PnSpaceViewContextPK comp_id, net.project.hibernate.model.PnView pnView) {
        this.comp_id = comp_id;
        this.pnView = pnView;
    }

    /** default constructor */
    public PnSpaceViewContext() {
    }

    /** minimal constructor */
    public PnSpaceViewContext(net.project.hibernate.model.PnSpaceViewContextPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSpaceViewContextPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceViewContextPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnView getPnView() {
        return this.pnView;
    }

    public void setPnView(net.project.hibernate.model.PnView pnView) {
        this.pnView = pnView;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceViewContext) ) return false;
        PnSpaceViewContext castOther = (PnSpaceViewContext) other;
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
