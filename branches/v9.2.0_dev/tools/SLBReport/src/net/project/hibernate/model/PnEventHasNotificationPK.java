package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnEventHasNotificationPK implements Serializable {

    /** identifier field */
    private BigDecimal notificationTypeId;

    /** identifier field */
    private BigDecimal eventTypeId;

    /** full constructor */
    public PnEventHasNotificationPK(BigDecimal notificationTypeId, BigDecimal eventTypeId) {
        this.notificationTypeId = notificationTypeId;
        this.eventTypeId = eventTypeId;
    }

    /** default constructor */
    public PnEventHasNotificationPK() {
    }

    public BigDecimal getNotificationTypeId() {
        return this.notificationTypeId;
    }

    public void setNotificationTypeId(BigDecimal notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }

    public BigDecimal getEventTypeId() {
        return this.eventTypeId;
    }

    public void setEventTypeId(BigDecimal eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("notificationTypeId", getNotificationTypeId())
            .append("eventTypeId", getEventTypeId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnEventHasNotificationPK) ) return false;
        PnEventHasNotificationPK castOther = (PnEventHasNotificationPK) other;
        return new EqualsBuilder()
            .append(this.getNotificationTypeId(), castOther.getNotificationTypeId())
            .append(this.getEventTypeId(), castOther.getEventTypeId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getNotificationTypeId())
            .append(getEventTypeId())
            .toHashCode();
    }

}
