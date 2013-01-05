package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnMethodologyInIndustryPK implements Serializable {

    /** identifier field */
    private BigDecimal industryId;

    /** identifier field */
    private BigDecimal methodologyId;

    /** full constructor */
    public PnMethodologyInIndustryPK(BigDecimal industryId, BigDecimal methodologyId) {
        this.industryId = industryId;
        this.methodologyId = methodologyId;
    }

    /** default constructor */
    public PnMethodologyInIndustryPK() {
    }

    public BigDecimal getIndustryId() {
        return this.industryId;
    }

    public void setIndustryId(BigDecimal industryId) {
        this.industryId = industryId;
    }

    public BigDecimal getMethodologyId() {
        return this.methodologyId;
    }

    public void setMethodologyId(BigDecimal methodologyId) {
        this.methodologyId = methodologyId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("industryId", getIndustryId())
            .append("methodologyId", getMethodologyId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnMethodologyInIndustryPK) ) return false;
        PnMethodologyInIndustryPK castOther = (PnMethodologyInIndustryPK) other;
        return new EqualsBuilder()
            .append(this.getIndustryId(), castOther.getIndustryId())
            .append(this.getMethodologyId(), castOther.getMethodologyId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getIndustryId())
            .append(getMethodologyId())
            .toHashCode();
    }

}
