package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnBrandSupportsLanguagePK implements Serializable {

    /** identifier field */
    private BigDecimal brandId;

    /** identifier field */
    private String languageCode;

    /** full constructor */
    public PnBrandSupportsLanguagePK(BigDecimal brandId, String languageCode) {
        this.brandId = brandId;
        this.languageCode = languageCode;
    }

    /** default constructor */
    public PnBrandSupportsLanguagePK() {
    }

    public BigDecimal getBrandId() {
        return this.brandId;
    }

    public void setBrandId(BigDecimal brandId) {
        this.brandId = brandId;
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("brandId", getBrandId())
            .append("languageCode", getLanguageCode())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnBrandSupportsLanguagePK) ) return false;
        PnBrandSupportsLanguagePK castOther = (PnBrandSupportsLanguagePK) other;
        return new EqualsBuilder()
            .append(this.getBrandId(), castOther.getBrandId())
            .append(this.getLanguageCode(), castOther.getLanguageCode())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBrandId())
            .append(getLanguageCode())
            .toHashCode();
    }

}
