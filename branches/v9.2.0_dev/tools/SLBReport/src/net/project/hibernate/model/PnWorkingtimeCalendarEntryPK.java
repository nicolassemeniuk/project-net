package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkingtimeCalendarEntryPK implements Serializable {

    /** identifier field */
    private BigDecimal calendarId;

    /** identifier field */
    private BigDecimal entryId;

    /** full constructor */
    public PnWorkingtimeCalendarEntryPK(BigDecimal calendarId, BigDecimal entryId) {
        this.calendarId = calendarId;
        this.entryId = entryId;
    }

    /** default constructor */
    public PnWorkingtimeCalendarEntryPK() {
    }

    public BigDecimal getCalendarId() {
        return this.calendarId;
    }

    public void setCalendarId(BigDecimal calendarId) {
        this.calendarId = calendarId;
    }

    public BigDecimal getEntryId() {
        return this.entryId;
    }

    public void setEntryId(BigDecimal entryId) {
        this.entryId = entryId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("calendarId", getCalendarId())
            .append("entryId", getEntryId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnWorkingtimeCalendarEntryPK) ) return false;
        PnWorkingtimeCalendarEntryPK castOther = (PnWorkingtimeCalendarEntryPK) other;
        return new EqualsBuilder()
            .append(this.getCalendarId(), castOther.getCalendarId())
            .append(this.getEntryId(), castOther.getEntryId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCalendarId())
            .append(getEntryId())
            .toHashCode();
    }

}
