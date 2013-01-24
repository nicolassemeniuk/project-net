package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnUserDefaultCredentialPK implements Serializable {

    /** identifier field */
    private BigDecimal userId;

    /** identifier field */
    private BigDecimal domainId;

    /** full constructor */
    public PnUserDefaultCredentialPK(BigDecimal userId, BigDecimal domainId) {
        this.userId = userId;
        this.domainId = domainId;
    }

    /** default constructor */
    public PnUserDefaultCredentialPK() {
    }

    public BigDecimal getUserId() {
        return this.userId;
    }

    public void setUserId(BigDecimal userId) {
        this.userId = userId;
    }

    public BigDecimal getDomainId() {
        return this.domainId;
    }

    public void setDomainId(BigDecimal domainId) {
        this.domainId = domainId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("userId", getUserId())
            .append("domainId", getDomainId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnUserDefaultCredentialPK) ) return false;
        PnUserDefaultCredentialPK castOther = (PnUserDefaultCredentialPK) other;
        return new EqualsBuilder()
            .append(this.getUserId(), castOther.getUserId())
            .append(this.getDomainId(), castOther.getDomainId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getUserId())
            .append(getDomainId())
            .toHashCode();
    }

}
