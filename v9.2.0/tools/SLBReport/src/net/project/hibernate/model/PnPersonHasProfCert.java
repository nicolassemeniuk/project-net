package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonHasProfCert implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPersonHasProfCertPK comp_id;

    /** nullable persistent field */
    private String otherProfCert;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** nullable persistent field */
    private net.project.hibernate.model.PnProfCertLookup pnProfCertLookup;

    /** full constructor */
    public PnPersonHasProfCert(net.project.hibernate.model.PnPersonHasProfCertPK comp_id, String otherProfCert, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnProfCertLookup pnProfCertLookup) {
        this.comp_id = comp_id;
        this.otherProfCert = otherProfCert;
        this.pnPerson = pnPerson;
        this.pnProfCertLookup = pnProfCertLookup;
    }

    /** default constructor */
    public PnPersonHasProfCert() {
    }

    /** minimal constructor */
    public PnPersonHasProfCert(net.project.hibernate.model.PnPersonHasProfCertPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnPersonHasProfCertPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPersonHasProfCertPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getOtherProfCert() {
        return this.otherProfCert;
    }

    public void setOtherProfCert(String otherProfCert) {
        this.otherProfCert = otherProfCert;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnProfCertLookup getPnProfCertLookup() {
        return this.pnProfCertLookup;
    }

    public void setPnProfCertLookup(net.project.hibernate.model.PnProfCertLookup pnProfCertLookup) {
        this.pnProfCertLookup = pnProfCertLookup;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonHasProfCert) ) return false;
        PnPersonHasProfCert castOther = (PnPersonHasProfCert) other;
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
