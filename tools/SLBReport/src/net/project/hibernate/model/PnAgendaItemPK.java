package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnAgendaItemPK implements Serializable {

    /** identifier field */
    private BigDecimal meetingId;

    /** identifier field */
    private BigDecimal agendaItemId;

    /** full constructor */
    public PnAgendaItemPK(BigDecimal meetingId, BigDecimal agendaItemId) {
        this.meetingId = meetingId;
        this.agendaItemId = agendaItemId;
    }

    /** default constructor */
    public PnAgendaItemPK() {
    }

    public BigDecimal getMeetingId() {
        return this.meetingId;
    }

    public void setMeetingId(BigDecimal meetingId) {
        this.meetingId = meetingId;
    }

    public BigDecimal getAgendaItemId() {
        return this.agendaItemId;
    }

    public void setAgendaItemId(BigDecimal agendaItemId) {
        this.agendaItemId = agendaItemId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("meetingId", getMeetingId())
            .append("agendaItemId", getAgendaItemId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnAgendaItemPK) ) return false;
        PnAgendaItemPK castOther = (PnAgendaItemPK) other;
        return new EqualsBuilder()
            .append(this.getMeetingId(), castOther.getMeetingId())
            .append(this.getAgendaItemId(), castOther.getAgendaItemId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getMeetingId())
            .append(getAgendaItemId())
            .toHashCode();
    }

}
