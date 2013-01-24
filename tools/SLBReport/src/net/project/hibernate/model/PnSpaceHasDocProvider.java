package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasDocProvider implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasDocProviderPK comp_id;

    /** nullable persistent field */
    private Integer isDefault;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocProvider pnDocProvider;

    /** full constructor */
    public PnSpaceHasDocProvider(net.project.hibernate.model.PnSpaceHasDocProviderPK comp_id, Integer isDefault, net.project.hibernate.model.PnDocProvider pnDocProvider) {
        this.comp_id = comp_id;
        this.isDefault = isDefault;
        this.pnDocProvider = pnDocProvider;
    }

    /** default constructor */
    public PnSpaceHasDocProvider() {
    }

    /** minimal constructor */
    public PnSpaceHasDocProvider(net.project.hibernate.model.PnSpaceHasDocProviderPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSpaceHasDocProviderPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasDocProviderPK comp_id) {
        this.comp_id = comp_id;
    }

    public Integer getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
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
        if ( !(other instanceof PnSpaceHasDocProvider) ) return false;
        PnSpaceHasDocProvider castOther = (PnSpaceHasDocProvider) other;
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
