package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnElementProperty implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnElementPropertyPK comp_id;

    /** persistent field */
    private String property;

    /** nullable persistent field */
    private String propertyType;

    /** nullable persistent field */
    private String defaultValue;

    /** nullable persistent field */
    private String maxValue;

    /** nullable persistent field */
    private String propertyLabel;

    /** nullable persistent field */
    private String minValue;

    /** persistent field */
    private int isUserChangable;

    /** nullable persistent field */
    private net.project.hibernate.model.PnElement pnElement;

    /** nullable persistent field */
    private net.project.hibernate.model.PnClientType pnClientType;

    /** full constructor */
    public PnElementProperty(net.project.hibernate.model.PnElementPropertyPK comp_id, String property, String propertyType, String defaultValue, String maxValue, String propertyLabel, String minValue, int isUserChangable, net.project.hibernate.model.PnElement pnElement, net.project.hibernate.model.PnClientType pnClientType) {
        this.comp_id = comp_id;
        this.property = property;
        this.propertyType = propertyType;
        this.defaultValue = defaultValue;
        this.maxValue = maxValue;
        this.propertyLabel = propertyLabel;
        this.minValue = minValue;
        this.isUserChangable = isUserChangable;
        this.pnElement = pnElement;
        this.pnClientType = pnClientType;
    }

    /** default constructor */
    public PnElementProperty() {
    }

    /** minimal constructor */
    public PnElementProperty(net.project.hibernate.model.PnElementPropertyPK comp_id, String property, int isUserChangable) {
        this.comp_id = comp_id;
        this.property = property;
        this.isUserChangable = isUserChangable;
    }

    public net.project.hibernate.model.PnElementPropertyPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnElementPropertyPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getPropertyType() {
        return this.propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getMaxValue() {
        return this.maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getPropertyLabel() {
        return this.propertyLabel;
    }

    public void setPropertyLabel(String propertyLabel) {
        this.propertyLabel = propertyLabel;
    }

    public String getMinValue() {
        return this.minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public int getIsUserChangable() {
        return this.isUserChangable;
    }

    public void setIsUserChangable(int isUserChangable) {
        this.isUserChangable = isUserChangable;
    }

    public net.project.hibernate.model.PnElement getPnElement() {
        return this.pnElement;
    }

    public void setPnElement(net.project.hibernate.model.PnElement pnElement) {
        this.pnElement = pnElement;
    }

    public net.project.hibernate.model.PnClientType getPnClientType() {
        return this.pnClientType;
    }

    public void setPnClientType(net.project.hibernate.model.PnClientType pnClientType) {
        this.pnClientType = pnClientType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnElementProperty) ) return false;
        PnElementProperty castOther = (PnElementProperty) other;
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
