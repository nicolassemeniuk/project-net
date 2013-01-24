package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocContainerHasObject implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnDocContainerHasObjectPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocContainer pnDocContainer;

    /** full constructor */
    public PnDocContainerHasObject(net.project.hibernate.model.PnDocContainerHasObjectPK comp_id, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnDocContainer pnDocContainer) {
        this.comp_id = comp_id;
        this.pnObject = pnObject;
        this.pnDocContainer = pnDocContainer;
    }

    /** default constructor */
    public PnDocContainerHasObject() {
    }

    /** minimal constructor */
    public PnDocContainerHasObject(net.project.hibernate.model.PnDocContainerHasObjectPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnDocContainerHasObjectPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnDocContainerHasObjectPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public net.project.hibernate.model.PnDocContainer getPnDocContainer() {
        return this.pnDocContainer;
    }

    public void setPnDocContainer(net.project.hibernate.model.PnDocContainer pnDocContainer) {
        this.pnDocContainer = pnDocContainer;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocContainerHasObject) ) return false;
        PnDocContainerHasObject castOther = (PnDocContainerHasObject) other;
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
