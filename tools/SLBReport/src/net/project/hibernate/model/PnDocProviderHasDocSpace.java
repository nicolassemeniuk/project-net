package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocProviderHasDocSpace implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnDocProviderHasDocSpacePK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocSpace pnDocSpace;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocProvider pnDocProvider;

    /** full constructor */
    public PnDocProviderHasDocSpace(net.project.hibernate.model.PnDocProviderHasDocSpacePK comp_id, net.project.hibernate.model.PnDocSpace pnDocSpace, net.project.hibernate.model.PnDocProvider pnDocProvider) {
        this.comp_id = comp_id;
        this.pnDocSpace = pnDocSpace;
        this.pnDocProvider = pnDocProvider;
    }

    /** default constructor */
    public PnDocProviderHasDocSpace() {
    }

    /** minimal constructor */
    public PnDocProviderHasDocSpace(net.project.hibernate.model.PnDocProviderHasDocSpacePK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnDocProviderHasDocSpacePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnDocProviderHasDocSpacePK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnDocSpace getPnDocSpace() {
        return this.pnDocSpace;
    }

    public void setPnDocSpace(net.project.hibernate.model.PnDocSpace pnDocSpace) {
        this.pnDocSpace = pnDocSpace;
    }

    public net.project.hibernate.model.PnDocProvider getPnDocProvider() {
        return this.pnDocProvider;
    }

    public void setPnDocProvider(net.project.hibernate.model.PnDocProvider pnDocProvider) {
        this.pnDocProvider = pnDocProvider;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocProviderHasDocSpace) ) return false;
        PnDocProviderHasDocSpace castOther = (PnDocProviderHasDocSpace) other;
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
