package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPortfolioHasSpace implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPortfolioHasSpacePK comp_id;

    /** nullable persistent field */
    private Integer isPrivate;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPortfolio pnPortfolio;

    /** full constructor */
    public PnPortfolioHasSpace(net.project.hibernate.model.PnPortfolioHasSpacePK comp_id, Integer isPrivate, net.project.hibernate.model.PnPortfolio pnPortfolio) {
        this.comp_id = comp_id;
        this.isPrivate = isPrivate;
        this.pnPortfolio = pnPortfolio;
    }

    /** default constructor */
    public PnPortfolioHasSpace() {
    }

    /** minimal constructor */
    public PnPortfolioHasSpace(net.project.hibernate.model.PnPortfolioHasSpacePK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnPortfolioHasSpacePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPortfolioHasSpacePK comp_id) {
        this.comp_id = comp_id;
    }

    public Integer getIsPrivate() {
        return this.isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
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
        if ( !(other instanceof PnPortfolioHasSpace) ) return false;
        PnPortfolioHasSpace castOther = (PnPortfolioHasSpace) other;
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
