package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnObjectTypeSupportsAction implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnObjectTypeSupportsActionPK comp_id;

    /** nullable persistent field */
    private Integer presentationSeq;

    /** nullable persistent field */
    private net.project.hibernate.model.PnSecurityAction pnSecurityAction;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObjectType pnObjectType;

    /** full constructor */
    public PnObjectTypeSupportsAction(net.project.hibernate.model.PnObjectTypeSupportsActionPK comp_id, Integer presentationSeq, net.project.hibernate.model.PnSecurityAction pnSecurityAction, net.project.hibernate.model.PnObjectType pnObjectType) {
        this.comp_id = comp_id;
        this.presentationSeq = presentationSeq;
        this.pnSecurityAction = pnSecurityAction;
        this.pnObjectType = pnObjectType;
    }

    /** default constructor */
    public PnObjectTypeSupportsAction() {
    }

    /** minimal constructor */
    public PnObjectTypeSupportsAction(net.project.hibernate.model.PnObjectTypeSupportsActionPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnObjectTypeSupportsActionPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnObjectTypeSupportsActionPK comp_id) {
        this.comp_id = comp_id;
    }

    public Integer getPresentationSeq() {
        return this.presentationSeq;
    }

    public void setPresentationSeq(Integer presentationSeq) {
        this.presentationSeq = presentationSeq;
    }

    public net.project.hibernate.model.PnSecurityAction getPnSecurityAction() {
        return this.pnSecurityAction;
    }

    public void setPnSecurityAction(net.project.hibernate.model.PnSecurityAction pnSecurityAction) {
        this.pnSecurityAction = pnSecurityAction;
    }

    public net.project.hibernate.model.PnObjectType getPnObjectType() {
        return this.pnObjectType;
    }

    public void setPnObjectType(net.project.hibernate.model.PnObjectType pnObjectType) {
        this.pnObjectType = pnObjectType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnObjectTypeSupportsAction) ) return false;
        PnObjectTypeSupportsAction castOther = (PnObjectTypeSupportsAction) other;
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
