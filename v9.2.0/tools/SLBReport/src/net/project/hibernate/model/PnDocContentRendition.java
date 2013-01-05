package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocContentRendition implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnDocContentRenditionPK comp_id;

    /** nullable persistent field */
    private String renderedAs;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocContentElement pnDocContentElement;

    /** full constructor */
    public PnDocContentRendition(net.project.hibernate.model.PnDocContentRenditionPK comp_id, String renderedAs, String recordStatus, net.project.hibernate.model.PnDocContentElement pnDocContentElement) {
        this.comp_id = comp_id;
        this.renderedAs = renderedAs;
        this.recordStatus = recordStatus;
        this.pnDocContentElement = pnDocContentElement;
    }

    /** default constructor */
    public PnDocContentRendition() {
    }

    /** minimal constructor */
    public PnDocContentRendition(net.project.hibernate.model.PnDocContentRenditionPK comp_id, String recordStatus) {
        this.comp_id = comp_id;
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnDocContentRenditionPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnDocContentRenditionPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getRenderedAs() {
        return this.renderedAs;
    }

    public void setRenderedAs(String renderedAs) {
        this.renderedAs = renderedAs;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnDocContentElement getPnDocContentElement() {
        return this.pnDocContentElement;
    }

    public void setPnDocContentElement(net.project.hibernate.model.PnDocContentElement pnDocContentElement) {
        this.pnDocContentElement = pnDocContentElement;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocContentRendition) ) return false;
        PnDocContentRendition castOther = (PnDocContentRendition) other;
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
