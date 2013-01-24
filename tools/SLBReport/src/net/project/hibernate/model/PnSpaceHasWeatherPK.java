package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasWeatherPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private String city;

    /** identifier field */
    private String stateCode;

    /** identifier field */
    private String countryCode;

    /** full constructor */
    public PnSpaceHasWeatherPK(BigDecimal spaceId, String city, String stateCode, String countryCode) {
        this.spaceId = spaceId;
        this.city = city;
        this.stateCode = stateCode;
        this.countryCode = countryCode;
    }

    /** default constructor */
    public PnSpaceHasWeatherPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateCode() {
        return this.stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("city", getCity())
            .append("stateCode", getStateCode())
            .append("countryCode", getCountryCode())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasWeatherPK) ) return false;
        PnSpaceHasWeatherPK castOther = (PnSpaceHasWeatherPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getCity(), castOther.getCity())
            .append(this.getStateCode(), castOther.getStateCode())
            .append(this.getCountryCode(), castOther.getCountryCode())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getCity())
            .append(getStateCode())
            .append(getCountryCode())
            .toHashCode();
    }

}
