package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCalEventHasAttendee implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnCalEventHasAttendeePK comp_id;

    /** persistent field */
    private BigDecimal statusId;

    /** nullable persistent field */
    private String attendeeComment;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** nullable persistent field */
    private net.project.hibernate.model.PnCalendarEvent pnCalendarEvent;

    /** full constructor */
    public PnCalEventHasAttendee(net.project.hibernate.model.PnCalEventHasAttendeePK comp_id, BigDecimal statusId, String attendeeComment, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnCalendarEvent pnCalendarEvent) {
        this.comp_id = comp_id;
        this.statusId = statusId;
        this.attendeeComment = attendeeComment;
        this.pnPerson = pnPerson;
        this.pnCalendarEvent = pnCalendarEvent;
    }

    /** default constructor */
    public PnCalEventHasAttendee() {
    }

    /** minimal constructor */
    public PnCalEventHasAttendee(net.project.hibernate.model.PnCalEventHasAttendeePK comp_id, BigDecimal statusId) {
        this.comp_id = comp_id;
        this.statusId = statusId;
    }

    public net.project.hibernate.model.PnCalEventHasAttendeePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnCalEventHasAttendeePK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getStatusId() {
        return this.statusId;
    }

    public void setStatusId(BigDecimal statusId) {
        this.statusId = statusId;
    }

    public String getAttendeeComment() {
        return this.attendeeComment;
    }

    public void setAttendeeComment(String attendeeComment) {
        this.attendeeComment = attendeeComment;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnCalendarEvent getPnCalendarEvent() {
        return this.pnCalendarEvent;
    }

    public void setPnCalendarEvent(net.project.hibernate.model.PnCalendarEvent pnCalendarEvent) {
        this.pnCalendarEvent = pnCalendarEvent;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnCalEventHasAttendee) ) return false;
        PnCalEventHasAttendee castOther = (PnCalEventHasAttendee) other;
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
