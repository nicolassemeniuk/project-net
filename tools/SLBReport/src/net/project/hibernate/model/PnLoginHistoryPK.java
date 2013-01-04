package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnLoginHistoryPK implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private Date loginDate;

    /** identifier field */
    private BigDecimal loginConcurrency;

    /** full constructor */
    public PnLoginHistoryPK(BigDecimal personId, Date loginDate, BigDecimal loginConcurrency) {
        this.personId = personId;
        this.loginDate = loginDate;
        this.loginConcurrency = loginConcurrency;
    }

    /** default constructor */
    public PnLoginHistoryPK() {
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public Date getLoginDate() {
        return this.loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public BigDecimal getLoginConcurrency() {
        return this.loginConcurrency;
    }

    public void setLoginConcurrency(BigDecimal loginConcurrency) {
        this.loginConcurrency = loginConcurrency;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .append("loginDate", getLoginDate())
            .append("loginConcurrency", getLoginConcurrency())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnLoginHistoryPK) ) return false;
        PnLoginHistoryPK castOther = (PnLoginHistoryPK) other;
        return new EqualsBuilder()
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getLoginDate(), castOther.getLoginDate())
            .append(this.getLoginConcurrency(), castOther.getLoginConcurrency())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPersonId())
            .append(getLoginDate())
            .append(getLoginConcurrency())
            .toHashCode();
    }

}
