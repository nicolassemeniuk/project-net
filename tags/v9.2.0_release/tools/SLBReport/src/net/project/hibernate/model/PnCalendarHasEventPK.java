package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCalendarHasEventPK implements Serializable {

    /** identifier field */
    private BigDecimal calendarId;

    /** identifier field */
    private BigDecimal calendarEventId;

    /** full constructor */
    public PnCalendarHasEventPK(BigDecimal calendarId, BigDecimal calendarEventId) {
        this.calendarId = calendarId;
        this.calendarEventId = calendarEventId;
    }

    /** default constructor */
    public PnCalendarHasEventPK() {
    }

    public BigDecimal getCalendarId() {
        return this.calendarId;
    }

    public void setCalendarId(BigDecimal calendarId) {
        this.calendarId = calendarId;
    }

    public BigDecimal getCalendarEventId() {
        return this.calendarEventId;
    }

    public void setCalendarEventId(BigDecimal calendarEventId) {
        this.calendarEventId = calendarEventId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("calendarId", getCalendarId())
            .append("calendarEventId", getCalendarEventId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnCalendarHasEventPK) ) return false;
        PnCalendarHasEventPK castOther = (PnCalendarHasEventPK) other;
        return new EqualsBuilder()
            .append(this.getCalendarId(), castOther.getCalendarId())
            .append(this.getCalendarEventId(), castOther.getCalendarEventId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCalendarId())
            .append(getCalendarEventId())
            .toHashCode();
    }

}
