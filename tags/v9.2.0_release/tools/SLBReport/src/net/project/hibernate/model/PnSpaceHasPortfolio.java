package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasPortfolio implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasPortfolioPK comp_id;

    /** persistent field */
    private int isDefault;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPortfolio pnPortfolio;

    /** full constructor */
    public PnSpaceHasPortfolio(net.project.hibernate.model.PnSpaceHasPortfolioPK comp_id, int isDefault, net.project.hibernate.model.PnPortfolio pnPortfolio) {
        this.comp_id = comp_id;
        this.isDefault = isDefault;
        this.pnPortfolio = pnPortfolio;
    }

    /** default constructor */
    public PnSpaceHasPortfolio() {
    }

    /** minimal constructor */
    public PnSpaceHasPortfolio(net.project.hibernate.model.PnSpaceHasPortfolioPK comp_id, int isDefault) {
        this.comp_id = comp_id;
        this.isDefault = isDefault;
    }

    public net.project.hibernate.model.PnSpaceHasPortfolioPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasPortfolioPK comp_id) {
        this.comp_id = comp_id;
    }

    public int getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public net.project.hibernate.model.PnPortfolio getPnPortfolio() {
        return this.pnPortfolio;
    }

    public void setPnPortfolio(net.project.hibernate.model.PnPortfolio pnPortfolio) {
        this.pnPortfolio = pnPortfolio;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasPortfolio) ) return false;
        PnSpaceHasPortfolio castOther = (PnSpaceHasPortfolio) other;
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
