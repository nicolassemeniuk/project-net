package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonAuthenticator implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPersonAuthenticatorPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** nullable persistent field */
    private net.project.hibernate.model.PnAuthenticator pnAuthenticator;

    /** full constructor */
    public PnPersonAuthenticator(net.project.hibernate.model.PnPersonAuthenticatorPK comp_id, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnAuthenticator pnAuthenticator) {
        this.comp_id = comp_id;
        this.pnPerson = pnPerson;
        this.pnAuthenticator = pnAuthenticator;
    }

    /** default constructor */
    public PnPersonAuthenticator() {
    }

    /** minimal constructor */
    public PnPersonAuthenticator(net.project.hibernate.model.PnPersonAuthenticatorPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnPersonAuthenticatorPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPersonAuthenticatorPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnAuthenticator getPnAuthenticator() {
        return this.pnAuthenticator;
    }

    public void setPnAuthenticator(net.project.hibernate.model.PnAuthenticator pnAuthenticator) {
        this.pnAuthenticator = pnAuthenticator;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonAuthenticator) ) return false;
        PnPersonAuthenticator castOther = (PnPersonAuthenticator) other;
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
