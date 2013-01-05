package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzJobListener implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.QrtzJobListenerPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.QrtzJobDetail qrtzJobDetail;

    /** full constructor */
    public QrtzJobListener(net.project.hibernate.model.QrtzJobListenerPK comp_id, net.project.hibernate.model.QrtzJobDetail qrtzJobDetail) {
        this.comp_id = comp_id;
        this.qrtzJobDetail = qrtzJobDetail;
    }

    /** default constructor */
    public QrtzJobListener() {
    }

    /** minimal constructor */
    public QrtzJobListener(net.project.hibernate.model.QrtzJobListenerPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.QrtzJobListenerPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.QrtzJobListenerPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.QrtzJobDetail getQrtzJobDetail() {
        return this.qrtzJobDetail;
    }

    public void setQrtzJobDetail(net.project.hibernate.model.QrtzJobDetail qrtzJobDetail) {
        this.qrtzJobDetail = qrtzJobDetail;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof QrtzJobListener) ) return false;
        QrtzJobListener castOther = (QrtzJobListener) other;
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
