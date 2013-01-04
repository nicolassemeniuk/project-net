package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkingtimeCalendar implements Serializable {

    /** identifier field */
    private BigDecimal calendarId;

    /** persistent field */
    private String isBaseCalendar;

    /** nullable persistent field */
    private String name;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private net.project.hibernate.model.PnWorkingtimeCalendar pnWorkingtimeCalendar;

    /** persistent field */
    private net.project.hibernate.model.PnPlan pnPlan;

    /** persistent field */
    private Set pnWorkingtimeCalendarEntries;

    /** persistent field */
    private Set pnWorkingtimeCalendars;

    /** full constructor */
    public PnWorkingtimeCalendar(BigDecimal calendarId, String isBaseCalendar, String name, String recordStatus, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnWorkingtimeCalendar pnWorkingtimeCalendar, net.project.hibernate.model.PnPlan pnPlan, Set pnWorkingtimeCalendarEntries, Set pnWorkingtimeCalendars) {
        this.calendarId = calendarId;
        this.isBaseCalendar = isBaseCalendar;
        this.name = name;
        this.recordStatus = recordStatus;
        this.pnPerson = pnPerson;
        this.pnWorkingtimeCalendar = pnWorkingtimeCalendar;
        this.pnPlan = pnPlan;
        this.pnWorkingtimeCalendarEntries = pnWorkingtimeCalendarEntries;
        this.pnWorkingtimeCalendars = pnWorkingtimeCalendars;
    }

    /** default constructor */
    public PnWorkingtimeCalendar() {
    }

    /** minimal constructor */
    public PnWorkingtimeCalendar(BigDecimal calendarId, String isBaseCalendar, String recordStatus, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnWorkingtimeCalendar pnWorkingtimeCalendar, net.project.hibernate.model.PnPlan pnPlan, Set pnWorkingtimeCalendarEntries, Set pnWorkingtimeCalendars) {
        this.calendarId = calendarId;
        this.isBaseCalendar = isBaseCalendar;
        this.recordStatus = recordStatus;
        this.pnPerson = pnPerson;
        this.pnWorkingtimeCalendar = pnWorkingtimeCalendar;
        this.pnPlan = pnPlan;
        this.pnWorkingtimeCalendarEntries = pnWorkingtimeCalendarEntries;
        this.pnWorkingtimeCalendars = pnWorkingtimeCalendars;
    }

    public BigDecimal getCalendarId() {
        return this.calendarId;
    }

    public void setCalendarId(BigDecimal calendarId) {
        this.calendarId = calendarId;
    }

    public String getIsBaseCalendar() {
        return this.isBaseCalendar;
    }

    public void setIsBaseCalendar(String isBaseCalendar) {
        this.isBaseCalendar = isBaseCalendar;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnWorkingtimeCalendar getPnWorkingtimeCalendar() {
        return this.pnWorkingtimeCalendar;
    }

    public void setPnWorkingtimeCalendar(net.project.hibernate.model.PnWorkingtimeCalendar pnWorkingtimeCalendar) {
        this.pnWorkingtimeCalendar = pnWorkingtimeCalendar;
    }

    public net.project.hibernate.model.PnPlan getPnPlan() {
        return this.pnPlan;
    }

    public void setPnPlan(net.project.hibernate.model.PnPlan pnPlan) {
        this.pnPlan = pnPlan;
    }

    public Set getPnWorkingtimeCalendarEntries() {
        return this.pnWorkingtimeCalendarEntries;
    }

    public void setPnWorkingtimeCalendarEntries(Set pnWorkingtimeCalendarEntries) {
        this.pnWorkingtimeCalendarEntries = pnWorkingtimeCalendarEntries;
    }

    public Set getPnWorkingtimeCalendars() {
        return this.pnWorkingtimeCalendars;
    }

    public void setPnWorkingtimeCalendars(Set pnWorkingtimeCalendars) {
        this.pnWorkingtimeCalendars = pnWorkingtimeCalendars;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("calendarId", getCalendarId())
            .toString();
    }

}
