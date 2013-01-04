package net.project.hibernate.model;

import java.io.Serializable;
import java.sql.Blob;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzBlobTrigger implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.QrtzBlobTriggerPK comp_id;

    /** nullable persistent field */
    private Blob blobData;

    /** nullable persistent field */
    private net.project.hibernate.model.QrtzTrigger qrtzTrigger;

    /** full constructor */
    public QrtzBlobTrigger(net.project.hibernate.model.QrtzBlobTriggerPK comp_id, Blob blobData, net.project.hibernate.model.QrtzTrigger qrtzTrigger) {
        this.comp_id = comp_id;
        this.blobData = blobData;
        this.qrtzTrigger = qrtzTrigger;
    }

    /** default constructor */
    public QrtzBlobTrigger() {
    }

    /** minimal constructor */
    public QrtzBlobTrigger(net.project.hibernate.model.QrtzBlobTriggerPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.QrtzBlobTriggerPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.QrtzBlobTriggerPK comp_id) {
        this.comp_id = comp_id;
    }

    public Blob getBlobData() {
        return this.blobData;
    }

    public void setBlobData(Blob blobData) {
        this.blobData = blobData;
    }

    public net.project.hibernate.model.QrtzTrigger getQrtzTrigger() {
        return this.qrtzTrigger;
    }

    public void setQrtzTrigger(net.project.hibernate.model.QrtzTrigger qrtzTrigger) {
        this.qrtzTrigger = qrtzTrigger;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof QrtzBlobTrigger) ) return false;
        QrtzBlobTrigger castOther = (QrtzBlobTrigger) other;
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
