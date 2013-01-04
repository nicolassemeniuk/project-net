package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonHasAlternateEmailPK implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private String email;

    /** full constructor */
    public PnPersonHasAlternateEmailPK(BigDecimal personId, String email) {
        this.personId = personId;
        this.email = email;
    }

    /** default constructor */
    public PnPersonHasAlternateEmailPK() {
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .append("email", getEmail())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonHasAlternateEmailPK) ) return false;
        PnPersonHasAlternateEmailPK castOther = (PnPersonHasAlternateEmailPK) other;
        return new EqualsBuilder()
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getEmail(), castOther.getEmail())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPersonId())
            .append(getEmail())
            .toHashCode();
    }

}
