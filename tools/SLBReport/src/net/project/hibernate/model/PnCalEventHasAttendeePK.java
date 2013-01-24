package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCalEventHasAttendeePK implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private BigDecimal calendarEventId;

    /** full constructor */
    public PnCalEventHasAttendeePK(BigDecimal personId, BigDecimal calendarEventId) {
        this.personId = personId;
        this.calendarEventId = calendarEventId;
    }

    /** default constructor */
    public PnCalEventHasAttendeePK() {
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public BigDecimal getCalendarEventId() {
        return this.calendarEventId;
    }

    public void setCalendarEventId(BigDecimal calendarEventId) {
        this.calendarEventId = calendarEventId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .append("calendarEventId", getCalendarEventId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnCalEventHasAttendeePK) ) return false;
        PnCalEventHasAttendeePK castOther = (PnCalEventHasAttendeePK) other;
        return new EqualsBuilder()
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getCalendarEventId(), castOther.getCalendarEventId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPersonId())
            .append(getCalendarEventId())
            .toHashCode();
    }

}
