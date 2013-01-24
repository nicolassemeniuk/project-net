package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnIndustryHasCategoryPK implements Serializable {

    /** identifier field */
    private BigDecimal industryId;

    /** identifier field */
    private BigDecimal categoryId;

    /** full constructor */
    public PnIndustryHasCategoryPK(BigDecimal industryId, BigDecimal categoryId) {
        this.industryId = industryId;
        this.categoryId = categoryId;
    }

    /** default constructor */
    public PnIndustryHasCategoryPK() {
    }

    public BigDecimal getIndustryId() {
        return this.industryId;
    }

    public void setIndustryId(BigDecimal industryId) {
        this.industryId = industryId;
    }

    public BigDecimal getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(BigDecimal categoryId) {
        this.categoryId = categoryId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("industryId", getIndustryId())
            .append("categoryId", getCategoryId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnIndustryHasCategoryPK) ) return false;
        PnIndustryHasCategoryPK castOther = (PnIndustryHasCategoryPK) other;
        return new EqualsBuilder()
            .append(this.getIndustryId(), castOther.getIndustryId())
            .append(this.getCategoryId(), castOther.getCategoryId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getIndustryId())
            .append(getCategoryId())
            .toHashCode();
    }

}
