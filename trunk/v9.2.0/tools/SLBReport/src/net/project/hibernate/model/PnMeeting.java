package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnMeeting implements Serializable {

    /** identifier field */
    private BigDecimal meetingId;

    /** persistent field */
    private int nextAgendaItemSeq;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private net.project.hibernate.model.PnCalendarEvent pnCalendarEvent;

    /** persistent field */
    private Set pnAgendaItems;

    /** full constructor */
    public PnMeeting(BigDecimal meetingId, int nextAgendaItemSeq, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnCalendarEvent pnCalendarEvent, Set pnAgendaItems) {
        this.meetingId = meetingId;
        this.nextAgendaItemSeq = nextAgendaItemSeq;
        this.pnObject = pnObject;
        this.pnPerson = pnPerson;
        this.pnCalendarEvent = pnCalendarEvent;
        this.pnAgendaItems = pnAgendaItems;
    }

    /** default constructor */
    public PnMeeting() {
    }

    /** minimal constructor */
    public PnMeeting(BigDecimal meetingId, int nextAgendaItemSeq, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnCalendarEvent pnCalendarEvent, Set pnAgendaItems) {
        this.meetingId = meetingId;
        this.nextAgendaItemSeq = nextAgendaItemSeq;
        this.pnPerson = pnPerson;
        this.pnCalendarEvent = pnCalendarEvent;
        this.pnAgendaItems = pnAgendaItems;
    }

    public BigDecimal getMeetingId() {
        return this.meetingId;
    }

    public void setMeetingId(BigDecimal meetingId) {
        this.meetingId = meetingId;
    }

    public int getNextAgendaItemSeq() {
        return this.nextAgendaItemSeq;
    }

    public void setNextAgendaItemSeq(int nextAgendaItemSeq) {
        this.nextAgendaItemSeq = nextAgendaItemSeq;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
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

    public Set getPnAgendaItems() {
        return this.pnAgendaItems;
    }

    public void setPnAgendaItems(Set pnAgendaItems) {
        this.pnAgendaItems = pnAgendaItems;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("meetingId", getMeetingId())
            .toString();
    }

}
