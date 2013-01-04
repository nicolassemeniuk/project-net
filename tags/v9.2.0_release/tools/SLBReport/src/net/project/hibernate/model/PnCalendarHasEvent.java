package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCalendarHasEvent implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnCalendarHasEventPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnCalendar pnCalendar;

    /** nullable persistent field */
    private net.project.hibernate.model.PnCalendarEvent pnCalendarEvent;

    /** full constructor */
    public PnCalendarHasEvent(net.project.hibernate.model.PnCalendarHasEventPK comp_id, net.project.hibernate.model.PnCalendar pnCalendar, net.project.hibernate.model.PnCalendarEvent pnCalendarEvent) {
        this.comp_id = comp_id;
        this.pnCalendar = pnCalendar;
        this.pnCalendarEvent = pnCalendarEvent;
    }

    /** default constructor */
    public PnCalendarHasEvent() {
    }

    /** minimal constructor */
    public PnCalendarHasEvent(net.project.hibernate.model.PnCalendarHasEventPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnCalendarHasEventPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnCalendarHasEventPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnCalendar getPnCalendar() {
        return this.pnCalendar;
    }

    public void setPnCalendar(net.project.hibernate.model.PnCalendar pnCalendar) {
        this.pnCalendar = pnCalendar;
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
        if ( !(other instanceof PnCalendarHasEvent) ) return false;
        PnCalendarHasEvent castOther = (PnCalendarHasEvent) other;
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
