package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassTypeElement implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnClassTypeElementPK comp_id;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnElement pnElement;

    /** nullable persistent field */
    private net.project.hibernate.model.PnClassType pnClassType;

    /** full constructor */
    public PnClassTypeElement(net.project.hibernate.model.PnClassTypeElementPK comp_id, String recordStatus, net.project.hibernate.model.PnElement pnElement, net.project.hibernate.model.PnClassType pnClassType) {
        this.comp_id = comp_id;
        this.recordStatus = recordStatus;
        this.pnElement = pnElement;
        this.pnClassType = pnClassType;
    }

    /** default constructor */
    public PnClassTypeElement() {
    }

    /** minimal constructor */
    public PnClassTypeElement(net.project.hibernate.model.PnClassTypeElementPK comp_id, String recordStatus) {
        this.comp_id = comp_id;
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnClassTypeElementPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnClassTypeElementPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnElement getPnElement() {
        return this.pnElement;
    }

    public void setPnElement(net.project.hibernate.model.PnElement pnElement) {
        this.pnElement = pnElement;
    }

    public net.project.hibernate.model.PnClassType getPnClassType() {
        return this.pnClassType;
    }

    public void setPnClassType(net.project.hibernate.model.PnClassType pnClassType) {
        this.pnClassType = pnClassType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnClassTypeElement) ) return false;
        PnClassTypeElement castOther = (PnClassTypeElement) other;
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
