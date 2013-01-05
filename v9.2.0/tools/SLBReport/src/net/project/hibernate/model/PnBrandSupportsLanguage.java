package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnBrandSupportsLanguage implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnBrandSupportsLanguagePK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnLanguage pnLanguage;

    /** full constructor */
    public PnBrandSupportsLanguage(net.project.hibernate.model.PnBrandSupportsLanguagePK comp_id, net.project.hibernate.model.PnLanguage pnLanguage) {
        this.comp_id = comp_id;
        this.pnLanguage = pnLanguage;
    }

    /** default constructor */
    public PnBrandSupportsLanguage() {
    }

    /** minimal constructor */
    public PnBrandSupportsLanguage(net.project.hibernate.model.PnBrandSupportsLanguagePK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnBrandSupportsLanguagePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnBrandSupportsLanguagePK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnLanguage getPnLanguage() {
        return this.pnLanguage;
    }

    public void setPnLanguage(net.project.hibernate.model.PnLanguage pnLanguage) {
        this.pnLanguage = pnLanguage;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnBrandSupportsLanguage) ) return false;
        PnBrandSupportsLanguage castOther = (PnBrandSupportsLanguage) other;
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
