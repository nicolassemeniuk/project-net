package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassDomainValuePK implements Serializable {

    /** identifier field */
    private BigDecimal domainId;

    /** identifier field */
    private BigDecimal domainValueId;

    /** full constructor */
    public PnClassDomainValuePK(BigDecimal domainId, BigDecimal domainValueId) {
        this.domainId = domainId;
        this.domainValueId = domainValueId;
    }

    /** default constructor */
    public PnClassDomainValuePK() {
    }

    public BigDecimal getDomainId() {
        return this.domainId;
    }

    public void setDomainId(BigDecimal domainId) {
        this.domainId = domainId;
    }

    public BigDecimal getDomainValueId() {
        return this.domainValueId;
    }

    public void setDomainValueId(BigDecimal domainValueId) {
        this.domainValueId = domainValueId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("domainId", getDomainId())
            .append("domainValueId", getDomainValueId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnClassDomainValuePK) ) return false;
        PnClassDomainValuePK castOther = (PnClassDomainValuePK) other;
        return new EqualsBuilder()
            .append(this.getDomainId(), castOther.getDomainId())
            .append(this.getDomainValueId(), castOther.getDomainValueId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDomainId())
            .append(getDomainValueId())
            .toHashCode();
    }

}
