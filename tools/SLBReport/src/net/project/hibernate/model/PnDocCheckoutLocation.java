package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocCheckoutLocation implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnDocCheckoutLocationPK comp_id;

    /** nullable persistent field */
    private String absoluteFilename;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocument pnDocument;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** full constructor */
    public PnDocCheckoutLocation(net.project.hibernate.model.PnDocCheckoutLocationPK comp_id, String absoluteFilename, net.project.hibernate.model.PnDocument pnDocument, net.project.hibernate.model.PnPerson pnPerson) {
        this.comp_id = comp_id;
        this.absoluteFilename = absoluteFilename;
        this.pnDocument = pnDocument;
        this.pnPerson = pnPerson;
    }

    /** default constructor */
    public PnDocCheckoutLocation() {
    }

    /** minimal constructor */
    public PnDocCheckoutLocation(net.project.hibernate.model.PnDocCheckoutLocationPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnDocCheckoutLocationPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnDocCheckoutLocationPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getAbsoluteFilename() {
        return this.absoluteFilename;
    }

    public void setAbsoluteFilename(String absoluteFilename) {
        this.absoluteFilename = absoluteFilename;
    }

    public net.project.hibernate.model.PnDocument getPnDocument() {
        return this.pnDocument;
    }

    public void setPnDocument(net.project.hibernate.model.PnDocument pnDocument) {
        this.pnDocument = pnDocument;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocCheckoutLocation) ) return false;
        PnDocCheckoutLocation castOther = (PnDocCheckoutLocation) other;
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
