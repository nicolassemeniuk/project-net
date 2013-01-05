package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCalendar implements Serializable {

    /** identifier field */
    private BigDecimal calendarId;

    /** nullable persistent field */
    private String calendarName;

    /** nullable persistent field */
    private String calendarDescription;

    /** persistent field */
    private int isBaseCalendar;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private Set pnSpaceHasCalendars;

    /** persistent field */
    private Set pnCalendarHasEvents;

    /** full constructor */
    public PnCalendar(BigDecimal calendarId, String calendarName, String calendarDescription, int isBaseCalendar, String recordStatus, net.project.hibernate.model.PnObject pnObject, Set pnSpaceHasCalendars, Set pnCalendarHasEvents) {
        this.calendarId = calendarId;
        this.calendarName = calendarName;
        this.calendarDescription = calendarDescription;
        this.isBaseCalendar = isBaseCalendar;
        this.recordStatus = recordStatus;
        this.pnObject = pnObject;
        this.pnSpaceHasCalendars = pnSpaceHasCalendars;
        this.pnCalendarHasEvents = pnCalendarHasEvents;
    }

    /** default constructor */
    public PnCalendar() {
    }

    public PnCalendar(BigDecimal calendarId, int isBaseCalendar, String calendarName, String calendarDescription,  String recordStatus) {
        this.calendarId = calendarId;
        this.calendarName = calendarName;
        this.calendarDescription = calendarDescription;
        this.isBaseCalendar = isBaseCalendar;
        this.recordStatus = recordStatus;
    }  
    
    /** minimal constructor */
    public PnCalendar(BigDecimal calendarId, int isBaseCalendar, String recordStatus, Set pnSpaceHasCalendars, Set pnCalendarHasEvents) {
        this.calendarId = calendarId;
        this.isBaseCalendar = isBaseCalendar;
        this.recordStatus = recordStatus;
        this.pnSpaceHasCalendars = pnSpaceHasCalendars;
        this.pnCalendarHasEvents = pnCalendarHasEvents;
    }

    public BigDecimal getCalendarId() {
        return this.calendarId;
    }

    public void setCalendarId(BigDecimal calendarId) {
        this.calendarId = calendarId;
    }

    public String getCalendarName() {
        return this.calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public String getCalendarDescription() {
        return this.calendarDescription;
    }

    public void setCalendarDescription(String calendarDescription) {
        this.calendarDescription = calendarDescription;
    }

    public int getIsBaseCalendar() {
        return this.isBaseCalendar;
    }

    public void setIsBaseCalendar(int isBaseCalendar) {
        this.isBaseCalendar = isBaseCalendar;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public Set getPnSpaceHasCalendars() {
        return this.pnSpaceHasCalendars;
    }

    public void setPnSpaceHasCalendars(Set pnSpaceHasCalendars) {
        this.pnSpaceHasCalendars = pnSpaceHasCalendars;
    }

    public Set getPnCalendarHasEvents() {
        return this.pnCalendarHasEvents;
    }

    public void setPnCalendarHasEvents(Set pnCalendarHasEvents) {
        this.pnCalendarHasEvents = pnCalendarHasEvents;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("calendarId", getCalendarId())
            .toString();
    }

}
