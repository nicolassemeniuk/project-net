package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasWeather implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasWeatherPK comp_id;

    /** persistent field */
    private String name;

    /** persistent field */
    private int isPrimary;

    /** nullable persistent field */
    private net.project.hibernate.model.PnStateLookup pnStateLookup;

    /** nullable persistent field */
    private net.project.hibernate.model.PnCountryLookup pnCountryLookup;

    /** persistent field */
    private net.project.hibernate.model.PnZipcodeFeedLookup pnZipcodeFeedLookup;

    /** full constructor */
    public PnSpaceHasWeather(net.project.hibernate.model.PnSpaceHasWeatherPK comp_id, String name, int isPrimary, net.project.hibernate.model.PnStateLookup pnStateLookup, net.project.hibernate.model.PnCountryLookup pnCountryLookup, net.project.hibernate.model.PnZipcodeFeedLookup pnZipcodeFeedLookup) {
        this.comp_id = comp_id;
        this.name = name;
        this.isPrimary = isPrimary;
        this.pnStateLookup = pnStateLookup;
        this.pnCountryLookup = pnCountryLookup;
        this.pnZipcodeFeedLookup = pnZipcodeFeedLookup;
    }

    /** default constructor */
    public PnSpaceHasWeather() {
    }

    /** minimal constructor */
    public PnSpaceHasWeather(net.project.hibernate.model.PnSpaceHasWeatherPK comp_id, String name, int isPrimary, net.project.hibernate.model.PnZipcodeFeedLookup pnZipcodeFeedLookup) {
        this.comp_id = comp_id;
        this.name = name;
        this.isPrimary = isPrimary;
        this.pnZipcodeFeedLookup = pnZipcodeFeedLookup;
    }

    public net.project.hibernate.model.PnSpaceHasWeatherPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasWeatherPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsPrimary() {
        return this.isPrimary;
    }

    public void setIsPrimary(int isPrimary) {
        this.isPrimary = isPrimary;
    }

    public net.project.hibernate.model.PnStateLookup getPnStateLookup() {
        return this.pnStateLookup;
    }

    public void setPnStateLookup(net.project.hibernate.model.PnStateLookup pnStateLookup) {
        this.pnStateLookup = pnStateLookup;
    }

    public net.project.hibernate.model.PnCountryLookup getPnCountryLookup() {
        return this.pnCountryLookup;
    }

    public void setPnCountryLookup(net.project.hibernate.model.PnCountryLookup pnCountryLookup) {
        this.pnCountryLookup = pnCountryLookup;
    }

    public net.project.hibernate.model.PnZipcodeFeedLookup getPnZipcodeFeedLookup() {
        return this.pnZipcodeFeedLookup;
    }

    public void setPnZipcodeFeedLookup(net.project.hibernate.model.PnZipcodeFeedLookup pnZipcodeFeedLookup) {
        this.pnZipcodeFeedLookup = pnZipcodeFeedLookup;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasWeather) ) return false;
        PnSpaceHasWeather castOther = (PnSpaceHasWeather) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
