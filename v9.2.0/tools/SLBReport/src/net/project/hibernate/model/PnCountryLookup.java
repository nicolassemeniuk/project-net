package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCountryLookup implements Serializable {

    /** identifier field */
    private String countryCode;

    /** persistent field */
    private String countryName;

    /** persistent field */
    private Set pnAddresses;

    /** persistent field */
    private Set pnSpaceHasWeathers;

    /** persistent field */
    private Set pnStateLookups;

    /** full constructor */
    public PnCountryLookup(String countryCode, String countryName, Set pnAddresses, Set pnSpaceHasWeathers, Set pnStateLookups) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.pnAddresses = pnAddresses;
        this.pnSpaceHasWeathers = pnSpaceHasWeathers;
        this.pnStateLookups = pnStateLookups;
    }

    /** default constructor */
    public PnCountryLookup() {
    }
    
    /** default constructor */
    public PnCountryLookup(String countryCode) {
    	 this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Set getPnAddresses() {
        return this.pnAddresses;
    }

    public void setPnAddresses(Set pnAddresses) {
        this.pnAddresses = pnAddresses;
    }

    public Set getPnSpaceHasWeathers() {
        return this.pnSpaceHasWeathers;
    }

    public void setPnSpaceHasWeathers(Set pnSpaceHasWeathers) {
        this.pnSpaceHasWeathers = pnSpaceHasWeathers;
    }

    public Set getPnStateLookups() {
        return this.pnStateLookups;
    }

    public void setPnStateLookups(Set pnStateLookups) {
        this.pnStateLookups = pnStateLookups;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("countryCode", getCountryCode())
            .toString();
    }

}
