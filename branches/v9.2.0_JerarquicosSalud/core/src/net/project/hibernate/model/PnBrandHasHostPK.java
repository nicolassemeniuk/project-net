package net.project.hibernate.model;
// Generated Jun 13, 2009 11:41:49 PM by Hibernate Tools 3.2.4.GA


import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnBrandHasHostPK generated by hbm2java
 */
@Embeddable
public class PnBrandHasHostPK  implements java.io.Serializable {

    /** identifier field */
    private Integer brandId;

    /** identifier field */
    private String hostName;

    public PnBrandHasHostPK() {
    }

    public PnBrandHasHostPK(Integer brandId, String hostName) {
       this.brandId = brandId;
       this.hostName = hostName;
    }
   


    @Column(name="BRAND_ID", nullable=false, length=20)
    public Integer getBrandId() {
        return this.brandId;
    }
    
    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }


    @Column(name="HOST_NAME", nullable=false, length=120)
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


