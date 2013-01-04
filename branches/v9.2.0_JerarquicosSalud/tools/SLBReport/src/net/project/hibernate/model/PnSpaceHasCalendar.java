package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasCalendar implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasCalendarPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnCalendar pnCalendar;

    /** full constructor */
    public PnSpaceHasCalendar(net.project.hibernate.model.PnSpaceHasCalendarPK comp_id, net.project.hibernate.model.PnCalendar pnCalendar) {
        this.comp_id = comp_id;
        this.pnCalendar = pnCalendar;
    }

    /** default constructor */
    public PnSpaceHasCalendar() {
    }

    /** minimal constructor */
    public PnSpaceHasCalendar(net.project.hibernate.model.PnSpaceHasCalendarPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSpaceHasCalendarPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasCalendarPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnCalendar getPnCalendar() {
        return this.pnCalendar;
    }

    public void setPnCalendar(net.project.hibernate.model.PnCalendar pnCalendar) {
        this.pnCalendar = pnCalendar;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasCalendar) ) return false;
        PnSpaceHasCalendar castOther = (PnSpaceHasCalendar) other;
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
