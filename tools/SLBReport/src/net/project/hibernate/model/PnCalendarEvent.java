package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCalendarEvent implements Serializable {

    /** identifier field */
    private BigDecimal calendarEventId;

    /** persistent field */
    private String eventName;

    /** persistent field */
    private BigDecimal eventTypeId;

    /** persistent field */
    private BigDecimal frequencyTypeId;

    /** nullable persistent field */
    private Date startDate;

    /** nullable persistent field */
    private Date endDate;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Clob eventDescClob;

    /** nullable persistent field */
    private Clob eventPurposeClob;

    /** persistent field */
    private net.project.hibernate.model.PnFacility pnFacility;

    /** persistent field */
    private Set pnMeetings;

    /** persistent field */
    private Set pnCalEventHasAttendees;

    /** persistent field */
    private Set pnCalendarHasEvents;

    /** full constructor */
    public PnCalendarEvent(BigDecimal calendarEventId, String eventName, BigDecimal eventTypeId, BigDecimal frequencyTypeId, Date startDate, Date endDate, String recordStatus, Clob eventDescClob, Clob eventPurposeClob, net.project.hibernate.model.PnFacility pnFacility, Set pnMeetings, Set pnCalEventHasAttendees, Set pnCalendarHasEvents) {
        this.calendarEventId = calendarEventId;
        this.eventName = eventName;
        this.eventTypeId = eventTypeId;
        this.frequencyTypeId = frequencyTypeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.recordStatus = recordStatus;
        this.eventDescClob = eventDescClob;
        this.eventPurposeClob = eventPurposeClob;
        this.pnFacility = pnFacility;
        this.pnMeetings = pnMeetings;
        this.pnCalEventHasAttendees = pnCalEventHasAttendees;
        this.pnCalendarHasEvents = pnCalendarHasEvents;
    }

    /** default constructor */
    public PnCalendarEvent() {
    }

    /** minimal constructor */
    public PnCalendarEvent(BigDecimal calendarEventId, String eventName, BigDecimal eventTypeId, BigDecimal frequencyTypeId, String recordStatus, net.project.hibernate.model.PnFacility pnFacility, Set pnMeetings, Set pnCalEventHasAttendees, Set pnCalendarHasEvents) {
        this.calendarEventId = calendarEventId;
        this.eventName = eventName;
        this.eventTypeId = eventTypeId;
        this.frequencyTypeId = frequencyTypeId;
        this.recordStatus = recordStatus;
        this.pnFacility = pnFacility;
        this.pnMeetings = pnMeetings;
        this.pnCalEventHasAttendees = pnCalEventHasAttendees;
        this.pnCalendarHasEvents = pnCalendarHasEvents;
    }

    public BigDecimal getCalendarEventId() {
        return this.calendarEventId;
    }

    public void setCalendarEventId(BigDecimal calendarEventId) {
        this.calendarEventId = calendarEventId;
    }

    public String getEventName() {
        return this.eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public BigDecimal getEventTypeId() {
        return this.eventTypeId;
    }

    public void setEventTypeId(BigDecimal eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public BigDecimal getFrequencyTypeId() {
        return this.frequencyTypeId;
    }

    public void setFrequencyTypeId(BigDecimal frequencyTypeId) {
        this.frequencyTypeId = frequencyTypeId;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Clob getEventDescClob() {
        return this.eventDescClob;
    }

    public void setEventDescClob(Clob eventDescClob) {
        this.eventDescClob = eventDescClob;
    }

    public Clob getEventPurposeClob() {
        return this.eventPurposeClob;
    }

    public void setEventPurposeClob(Clob eventPurposeClob) {
        this.eventPurposeClob = eventPurposeClob;
    }

    public net.project.hibernate.model.PnFacility getPnFacility() {
        return this.pnFacility;
    }

    public void setPnFacility(net.project.hibernate.model.PnFacility pnFacility) {
        this.pnFacility = pnFacility;
    }

    public Set getPnMeetings() {
        return this.pnMeetings;
    }

    public void setPnMeetings(Set pnMeetings) {
        this.pnMeetings = pnMeetings;
    }

    public Set getPnCalEventHasAttendees() {
        return this.pnCalEventHasAttendees;
    }

    public void setPnCalEventHasAttendees(Set pnCalEventHasAttendees) {
        this.pnCalEventHasAttendees = pnCalEventHasAttendees;
    }

    public Set getPnCalendarHasEvents() {
        return this.pnCalendarHasEvents;
    }

    public void setPnCalendarHasEvents(Set pnCalendarHasEvents) {
        this.pnCalendarHasEvents = pnCalendarHasEvents;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("calendarEventId", getCalendarEventId())
            .toString();
    }

}
