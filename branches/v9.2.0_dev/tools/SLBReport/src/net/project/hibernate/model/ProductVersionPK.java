package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class ProductVersionPK implements Serializable {

    /** identifier field */
    private String product;

    /** identifier field */
    private Integer majorVersion;

    /** identifier field */
    private Integer minorVersion;

    /** identifier field */
    private Integer subMinorVersion;

    /** identifier field */
    private Integer buildVersion;

    /** full constructor */
    public ProductVersionPK(String product, Integer majorVersion, Integer minorVersion, Integer subMinorVersion, Integer buildVersion) {
        this.product = product;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.subMinorVersion = subMinorVersion;
        this.buildVersion = buildVersion;
    }

    /** default constructor */
    public ProductVersionPK() {
    }

    public String getProduct() {
        return this.product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getMajorVersion() {
        return this.majorVersion;
    }

    public void setMajorVersion(Integer majorVersion) {
        this.majorVersion = majorVersion;
    }

    public Integer getMinorVersion() {
        return this.minorVersion;
    }

    public void setMinorVersion(Integer minorVersion) {
        this.minorVersion = minorVersion;
    }

    public Integer getSubMinorVersion() {
        return this.subMinorVersion;
    }

    public void setSubMinorVersion(Integer subMinorVersion) {
        this.subMinorVersion = subMinorVersion;
    }

    public Integer getBuildVersion() {
        return this.buildVersion;
    }

    public void setBuildVersion(Integer buildVersion) {
        this.buildVersion = buildVersion;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("product", getProduct())
            .append("majorVersion", getMajorVersion())
            .append("minorVersion", getMinorVersion())
            .append("subMinorVersion", getSubMinorVersion())
            .append("buildVersion", getBuildVersion())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof ProductVersionPK) ) return false;
        ProductVersionPK castOther = (ProductVersionPK) other;
        return new EqualsBuilder()
            .append(this.getProduct(), castOther.getProduct())
            .append(this.getMajorVersion(), castOther.getMajorVersion())
            .append(this.getMinorVersion(), castOther.getMinorVersion())
            .append(this.getSubMinorVersion(), castOther.getSubMinorVersion())
            .append(this.getBuildVersion(), castOther.getBuildVersion())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getProduct())
            .append(getMajorVersion())
            .append(getMinorVersion())
            .append(getSubMinorVersion())
            .append(getBuildVersion())
            .toHashCode();
    }

}
