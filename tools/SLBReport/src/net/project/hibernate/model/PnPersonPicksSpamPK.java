package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonPicksSpamPK implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private Integer spamTypeCode;

    /** full constructor */
    public PnPersonPicksSpamPK(BigDecimal personId, Integer spamTypeCode) {
        this.personId = personId;
        this.spamTypeCode = spamTypeCode;
    }

    /** default constructor */
    public PnPersonPicksSpamPK() {
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public Integer getSpamTypeCode() {
        return this.spamTypeCode;
    }

    public void setSpamTypeCode(Integer spamTypeCode) {
        this.spamTypeCode = spamTypeCode;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .append("spamTypeCode", getSpamTypeCode())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonPicksSpamPK) ) return false;
        PnPersonPicksSpamPK castOther = (PnPersonPicksSpamPK) other;
        return new EqualsBuilder()
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getSpamTypeCode(), castOther.getSpamTypeCode())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPersonId())
            .append(getSpamTypeCode())
            .toHashCode();
    }

}
