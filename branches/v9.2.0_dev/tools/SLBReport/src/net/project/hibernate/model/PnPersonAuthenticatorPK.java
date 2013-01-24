package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonAuthenticatorPK implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private BigDecimal authenticatorId;

    /** full constructor */
    public PnPersonAuthenticatorPK(BigDecimal personId, BigDecimal authenticatorId) {
        this.personId = personId;
        this.authenticatorId = authenticatorId;
    }

    /** default constructor */
    public PnPersonAuthenticatorPK() {
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public BigDecimal getAuthenticatorId() {
        return this.authenticatorId;
    }

    public void setAuthenticatorId(BigDecimal authenticatorId) {
        this.authenticatorId = authenticatorId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .append("authenticatorId", getAuthenticatorId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonAuthenticatorPK) ) return false;
        PnPersonAuthenticatorPK castOther = (PnPersonAuthenticatorPK) other;
        return new EqualsBuilder()
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getAuthenticatorId(), castOther.getAuthenticatorId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPersonId())
            .append(getAuthenticatorId())
            .toHashCode();
    }

}
