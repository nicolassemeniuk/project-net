package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassFieldProperty implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnClassFieldPropertyPK comp_id;

    /** nullable persistent field */
    private String propertyType;

    /** nullable persistent field */
    private String value;

    /** nullable persistent field */
    private net.project.hibernate.model.PnClientType pnClientType;

    /** full constructor */
    public PnClassFieldProperty(net.project.hibernate.model.PnClassFieldPropertyPK comp_id, String propertyType, String value, net.project.hibernate.model.PnClientType pnClientType) {
        this.comp_id = comp_id;
        this.propertyType = propertyType;
        this.value = value;
        this.pnClientType = pnClientType;
    }

    /** default constructor */
    public PnClassFieldProperty() {
    }

    /** minimal constructor */
    public PnClassFieldProperty(net.project.hibernate.model.PnClassFieldPropertyPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnClassFieldPropertyPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnClassFieldPropertyPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getPropertyType() {
        return this.propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
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
        if ( !(other instanceof PnClassFieldProperty) ) return false;
        PnClassFieldProperty castOther = (PnClassFieldProperty) other;
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
