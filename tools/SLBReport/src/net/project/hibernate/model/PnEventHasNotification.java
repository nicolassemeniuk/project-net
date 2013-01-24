package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnEventHasNotification implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnEventHasNotificationPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnEventType pnEventType;

    /** nullable persistent field */
    private net.project.hibernate.model.PnNotificationType pnNotificationType;

    /** full constructor */
    public PnEventHasNotification(net.project.hibernate.model.PnEventHasNotificationPK comp_id, net.project.hibernate.model.PnEventType pnEventType, net.project.hibernate.model.PnNotificationType pnNotificationType) {
        this.comp_id = comp_id;
        this.pnEventType = pnEventType;
        this.pnNotificationType = pnNotificationType;
    }

    /** default constructor */
    public PnEventHasNotification() {
    }

    /** minimal constructor */
    public PnEventHasNotification(net.project.hibernate.model.PnEventHasNotificationPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnEventHasNotificationPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnEventHasNotificationPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnEventType getPnEventType() {
        return this.pnEventType;
    }

    public void setPnEventType(net.project.hibernate.model.PnEventType pnEventType) {
        this.pnEventType = pnEventType;
    }

    public net.project.hibernate.model.PnNotificationType getPnNotificationType() {
        return this.pnNotificationType;
    }

    public void setPnNotificationType(net.project.hibernate.model.PnNotificationType pnNotificationType) {
        this.pnNotificationType = pnNotificationType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnEventHasNotification) ) return false;
        PnEventHasNotification castOther = (PnEventHasNotification) other;
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
