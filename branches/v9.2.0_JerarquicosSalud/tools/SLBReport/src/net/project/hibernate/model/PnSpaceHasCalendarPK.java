package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasCalendarPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal calendarId;

    /** full constructor */
    public PnSpaceHasCalendarPK(BigDecimal spaceId, BigDecimal calendarId) {
        this.spaceId = spaceId;
        this.calendarId = calendarId;
    }

    /** default constructor */
    public PnSpaceHasCalendarPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getCalendarId() {
        return this.calendarId;
    }

    public void setCalendarId(BigDecimal calendarId) {
        this.calendarId = calendarId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("calendarId", getCalendarId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasCalendarPK) ) return false;
        PnSpaceHasCalendarPK castOther = (PnSpaceHasCalendarPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getCalendarId(), castOther.getCalendarId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getCalendarId())
            .toHashCode();
    }

}
