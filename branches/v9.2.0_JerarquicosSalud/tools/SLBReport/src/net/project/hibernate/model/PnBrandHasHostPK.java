package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnBrandHasHostPK implements Serializable {

    /** identifier field */
    private BigDecimal brandId;

    /** identifier field */
    private String hostName;

    /** full constructor */
    public PnBrandHasHostPK(BigDecimal brandId, String hostName) {
        this.brandId = brandId;
        this.hostName = hostName;
    }

    /** default constructor */
    public PnBrandHasHostPK() {
    }

    public BigDecimal getBrandId() {
        return this.brandId;
    }

    public void setBrandId(BigDecimal brandId) {
        this.brandId = brandId;
    }

    public String getHostName() {
        return this.hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("brandId", getBrandId())
            .append("hostName", getHostName())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnBrandHasHostPK) ) return false;
        PnBrandHasHostPK castOther = (PnBrandHasHostPK) other;
        return new EqualsBuilder()
            .append(this.getBrandId(), castOther.getBrandId())
            .append(this.getHostName(), castOther.getHostName())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getBrandId())
            .append(getHostName())
            .toHashCode();
    }

}
