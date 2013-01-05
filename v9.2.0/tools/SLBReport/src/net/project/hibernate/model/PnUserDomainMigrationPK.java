package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnUserDomainMigrationPK implements Serializable {

    /** identifier field */
    private BigDecimal userId;

    /** identifier field */
    private BigDecimal domainMigrationId;

    /** full constructor */
    public PnUserDomainMigrationPK(BigDecimal userId, BigDecimal domainMigrationId) {
        this.userId = userId;
        this.domainMigrationId = domainMigrationId;
    }

    /** default constructor */
    public PnUserDomainMigrationPK() {
    }

    public BigDecimal getUserId() {
        return this.userId;
    }

    public void setUserId(BigDecimal userId) {
        this.userId = userId;
    }

    public BigDecimal getDomainMigrationId() {
        return this.domainMigrationId;
    }

    public void setDomainMigrationId(BigDecimal domainMigrationId) {
        this.domainMigrationId = domainMigrationId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("userId", getUserId())
            .append("domainMigrationId", getDomainMigrationId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnUserDomainMigrationPK) ) return false;
        PnUserDomainMigrationPK castOther = (PnUserDomainMigrationPK) other;
        return new EqualsBuilder()
            .append(this.getUserId(), castOther.getUserId())
            .append(this.getDomainMigrationId(), castOther.getDomainMigrationId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getUserId())
            .append(getDomainMigrationId())
            .toHashCode();
    }

}
