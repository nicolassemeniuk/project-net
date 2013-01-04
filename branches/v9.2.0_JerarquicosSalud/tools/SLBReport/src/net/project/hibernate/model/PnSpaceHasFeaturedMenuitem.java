package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasFeaturedMenuitem implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasFeaturedMenuitemPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObjectBySpaceId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObjectByObjectId;

    /** full constructor */
    public PnSpaceHasFeaturedMenuitem(net.project.hibernate.model.PnSpaceHasFeaturedMenuitemPK comp_id, net.project.hibernate.model.PnObject pnObjectBySpaceId, net.project.hibernate.model.PnObject pnObjectByObjectId) {
        this.comp_id = comp_id;
        this.pnObjectBySpaceId = pnObjectBySpaceId;
        this.pnObjectByObjectId = pnObjectByObjectId;
    }

    /** default constructor */
    public PnSpaceHasFeaturedMenuitem() {
    }

    /** minimal constructor */
    public PnSpaceHasFeaturedMenuitem(net.project.hibernate.model.PnSpaceHasFeaturedMenuitemPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSpaceHasFeaturedMenuitemPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasFeaturedMenuitemPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnObject getPnObjectBySpaceId() {
        return this.pnObjectBySpaceId;
    }

    public void setPnObjectBySpaceId(net.project.hibernate.model.PnObject pnObjectBySpaceId) {
        this.pnObjectBySpaceId = pnObjectBySpaceId;
    }

    public net.project.hibernate.model.PnObject getPnObjectByObjectId() {
        return this.pnObjectByObjectId;
    }

    public void setPnObjectByObjectId(net.project.hibernate.model.PnObject pnObjectByObjectId) {
        this.pnObjectByObjectId = pnObjectByObjectId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasFeaturedMenuitem) ) return false;
        PnSpaceHasFeaturedMenuitem castOther = (PnSpaceHasFeaturedMenuitem) other;
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
