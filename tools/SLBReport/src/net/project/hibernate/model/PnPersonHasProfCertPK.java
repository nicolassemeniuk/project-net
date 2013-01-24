package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonHasProfCertPK implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private Integer profCertCode;

    /** full constructor */
    public PnPersonHasProfCertPK(BigDecimal personId, Integer profCertCode) {
        this.personId = personId;
        this.profCertCode = profCertCode;
    }

    /** default constructor */
    public PnPersonHasProfCertPK() {
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public Integer getProfCertCode() {
        return this.profCertCode;
    }

    public void setProfCertCode(Integer profCertCode) {
        this.profCertCode = profCertCode;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .append("profCertCode", getProfCertCode())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonHasProfCertPK) ) return false;
        PnPersonHasProfCertPK castOther = (PnPersonHasProfCertPK) other;
        return new EqualsBuilder()
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getProfCertCode(), castOther.getProfCertCode())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPersonId())
            .append(getProfCertCode())
            .toHashCode();
    }

}
