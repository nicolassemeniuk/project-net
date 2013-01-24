package net.project.hibernate.model;

import java.io.Serializable;
import java.sql.Blob;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzCalendar implements Serializable {

    /** identifier field */
    private String calendarName;

    /** persistent field */
    private Blob calendar;

    /** full constructor */
    public QrtzCalendar(String calendarName, Blob calendar) {
        this.calendarName = calendarName;
        this.calendar = calendar;
    }

    /** default constructor */
    public QrtzCalendar() {
    }

    public String getCalendarName() {
        return this.calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public Blob getCalendar() {
        return this.calendar;
    }

    public void setCalendar(Blob calendar) {
        this.calendar = calendar;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("calendarName", getCalendarName())
            .toString();
    }

}
