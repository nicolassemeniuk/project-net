package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzTriggerListener implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.QrtzTriggerListenerPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.QrtzTrigger qrtzTrigger;

    /** full constructor */
    public QrtzTriggerListener(net.project.hibernate.model.QrtzTriggerListenerPK comp_id, net.project.hibernate.model.QrtzTrigger qrtzTrigger) {
        this.comp_id = comp_id;
        this.qrtzTrigger = qrtzTrigger;
    }

    /** default constructor */
    public QrtzTriggerListener() {
    }

    /** minimal constructor */
    public QrtzTriggerListener(net.project.hibernate.model.QrtzTriggerListenerPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.QrtzTriggerListenerPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.QrtzTriggerListenerPK comp_id) {
        this.comp_id = comp_id;
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
        if ( !(other instanceof QrtzTriggerListener) ) return false;
        QrtzTriggerListener castOther = (QrtzTriggerListener) other;
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
