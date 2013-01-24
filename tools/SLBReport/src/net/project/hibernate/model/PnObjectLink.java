package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnObjectLink implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnObjectLinkPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObjectByToObjectId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObjectByFromObjectId;

    /** full constructor */
    public PnObjectLink(net.project.hibernate.model.PnObjectLinkPK comp_id, net.project.hibernate.model.PnObject pnObjectByToObjectId, net.project.hibernate.model.PnObject pnObjectByFromObjectId) {
        this.comp_id = comp_id;
        this.pnObjectByToObjectId = pnObjectByToObjectId;
        this.pnObjectByFromObjectId = pnObjectByFromObjectId;
    }

    /** default constructor */
    public PnObjectLink() {
    }

    /** minimal constructor */
    public PnObjectLink(net.project.hibernate.model.PnObjectLinkPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnObjectLinkPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnObjectLinkPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnObject getPnObjectByToObjectId() {
        return this.pnObjectByToObjectId;
    }

    public void setPnObjectByToObjectId(net.project.hibernate.model.PnObject pnObjectByToObjectId) {
        this.pnObjectByToObjectId = pnObjectByToObjectId;
    }

    public net.project.hibernate.model.PnObject getPnObjectByFromObjectId() {
        return this.pnObjectByFromObjectId;
    }

    public void setPnObjectByFromObjectId(net.project.hibernate.model.PnObject pnObjectByFromObjectId) {
        this.pnObjectByFromObjectId = pnObjectByFromObjectId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnObjectLink) ) return false;
        PnObjectLink castOther = (PnObjectLink) other;
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
