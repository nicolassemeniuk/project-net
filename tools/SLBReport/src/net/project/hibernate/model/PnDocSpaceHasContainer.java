package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocSpaceHasContainer implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnDocSpaceHasContainerPK comp_id;

    /** persistent field */
    private int isRoot;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocContainer pnDocContainer;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocSpace pnDocSpace;

    /** full constructor */
    public PnDocSpaceHasContainer(net.project.hibernate.model.PnDocSpaceHasContainerPK comp_id, int isRoot, net.project.hibernate.model.PnDocContainer pnDocContainer, net.project.hibernate.model.PnDocSpace pnDocSpace) {
        this.comp_id = comp_id;
        this.isRoot = isRoot;
        this.pnDocContainer = pnDocContainer;
        this.pnDocSpace = pnDocSpace;
    }

    /** default constructor */
    public PnDocSpaceHasContainer() {
    }

    /** minimal constructor */
    public PnDocSpaceHasContainer(net.project.hibernate.model.PnDocSpaceHasContainerPK comp_id, int isRoot) {
        this.comp_id = comp_id;
        this.isRoot = isRoot;
    }

    public net.project.hibernate.model.PnDocSpaceHasContainerPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnDocSpaceHasContainerPK comp_id) {
        this.comp_id = comp_id;
    }

    public int getIsRoot() {
        return this.isRoot;
    }

    public void setIsRoot(int isRoot) {
        this.isRoot = isRoot;
    }

    public net.project.hibernate.model.PnDocContainer getPnDocContainer() {
        return this.pnDocContainer;
    }

    public void setPnDocContainer(net.project.hibernate.model.PnDocContainer pnDocContainer) {
        this.pnDocContainer = pnDocContainer;
    }

    public net.project.hibernate.model.PnDocSpace getPnDocSpace() {
        return this.pnDocSpace;
    }

    public void setPnDocSpace(net.project.hibernate.model.PnDocSpace pnDocSpace) {
        this.pnDocSpace = pnDocSpace;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocSpaceHasContainer) ) return false;
        PnDocSpaceHasContainer castOther = (PnDocSpaceHasContainer) other;
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
