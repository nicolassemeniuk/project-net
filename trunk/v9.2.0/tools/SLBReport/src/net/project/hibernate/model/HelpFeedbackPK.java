package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class HelpFeedbackPK implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private Date timestamp;

    /** full constructor */
    public HelpFeedbackPK(BigDecimal personId, Date timestamp) {
        this.personId = personId;
        this.timestamp = timestamp;
    }

    /** default constructor */
    public HelpFeedbackPK() {
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .append("timestamp", getTimestamp())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof HelpFeedbackPK) ) return false;
        HelpFeedbackPK castOther = (HelpFeedbackPK) other;
        return new EqualsBuilder()
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getTimestamp(), castOther.getTimestamp())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPersonId())
            .append(getTimestamp())
            .toHashCode();
    }

}
