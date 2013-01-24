package net.project.hibernate.model;

import java.io.Serializable;
import java.sql.Clob;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** 
 *        For brand name terminology lookups for 
 *     
*/
public class PnProperty implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPropertyPK comp_id;

    /** persistent field */
    private String propertyType;

    /** nullable persistent field */
    private String propertyValue;

    /** nullable persistent field */
    private String recordStatus;

    /** persistent field */
    private int isSystemProperty;

    /** persistent field */
    private int isTranslatableProperty;

    /** nullable persistent field */
    private Clob propertyValueClob;

    /** nullable persistent field */
    private net.project.hibernate.model.PnLanguage pnLanguage;

    /** full constructor */
    public PnProperty(net.project.hibernate.model.PnPropertyPK comp_id, String propertyType, String propertyValue, String recordStatus, int isSystemProperty, int isTranslatableProperty, Clob propertyValueClob, net.project.hibernate.model.PnLanguage pnLanguage) {
        this.comp_id = comp_id;
        this.propertyType = propertyType;
        this.propertyValue = propertyValue;
        this.recordStatus = recordStatus;
        this.isSystemProperty = isSystemProperty;
        this.isTranslatableProperty = isTranslatableProperty;
        this.propertyValueClob = propertyValueClob;
        this.pnLanguage = pnLanguage;
    }

    /** default constructor */
    public PnProperty() {
    }

    /** minimal constructor */
    public PnProperty(net.project.hibernate.model.PnPropertyPK comp_id, String propertyType, int isSystemProperty, int isTranslatableProperty) {
        this.comp_id = comp_id;
        this.propertyType = propertyType;
        this.isSystemProperty = isSystemProperty;
        this.isTranslatableProperty = isTranslatableProperty;
    }

    public net.project.hibernate.model.PnPropertyPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPropertyPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getPropertyType() {
        return this.propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPropertyValue() {
        return this.propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public int getIsSystemProperty() {
        return this.isSystemProperty;
    }

    public void setIsSystemProperty(int isSystemProperty) {
        this.isSystemProperty = isSystemProperty;
    }

    public int getIsTranslatableProperty() {
        return this.isTranslatableProperty;
    }

    public void setIsTranslatableProperty(int isTranslatableProperty) {
        this.isTranslatableProperty = isTranslatableProperty;
    }

    public Clob getPropertyValueClob() {
        return this.propertyValueClob;
    }

    public void setPropertyValueClob(Clob propertyValueClob) {
        this.propertyValueClob = propertyValueClob;
    }

    public net.project.hibernate.model.PnLanguage getPnLanguage() {
        return this.pnLanguage;
    }

    public void setPnLanguage(net.project.hibernate.model.PnLanguage pnLanguage) {
        this.pnLanguage = pnLanguage;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnProperty) ) return false;
        PnProperty castOther = (PnProperty) other;
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
