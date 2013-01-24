package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasSubscription implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasSubscriptionPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnSubscription pnSubscription;

    /** full constructor */
    public PnSpaceHasSubscription(net.project.hibernate.model.PnSpaceHasSubscriptionPK comp_id, net.project.hibernate.model.PnSubscription pnSubscription) {
        this.comp_id = comp_id;
        this.pnSubscription = pnSubscription;
    }

    /** default constructor */
    public PnSpaceHasSubscription() {
    }

    /** minimal constructor */
    public PnSpaceHasSubscription(net.project.hibernate.model.PnSpaceHasSubscriptionPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSpaceHasSubscriptionPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasSubscriptionPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSubscription getPnSubscription() {
        return this.pnSubscription;
    }

    public void setPnSubscription(net.project.hibernate.model.PnSubscription pnSubscription) {
        this.pnSubscription = pnSubscription;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasSubscription) ) return false;
        PnSpaceHasSubscription castOther = (PnSpaceHasSubscription) other;
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
