package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPortfolioHasConfiguration implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPortfolioHasConfigurationPK comp_id;

    /** persistent field */
    private int isPrivate;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPortfolio pnPortfolio;

    /** nullable persistent field */
    private net.project.hibernate.model.PnConfigurationSpace pnConfigurationSpace;

    /** full constructor */
    public PnPortfolioHasConfiguration(net.project.hibernate.model.PnPortfolioHasConfigurationPK comp_id, int isPrivate, net.project.hibernate.model.PnPortfolio pnPortfolio, net.project.hibernate.model.PnConfigurationSpace pnConfigurationSpace) {
        this.comp_id = comp_id;
        this.isPrivate = isPrivate;
        this.pnPortfolio = pnPortfolio;
        this.pnConfigurationSpace = pnConfigurationSpace;
    }

    /** default constructor */
    public PnPortfolioHasConfiguration() {
    }

    /** minimal constructor */
    public PnPortfolioHasConfiguration(net.project.hibernate.model.PnPortfolioHasConfigurationPK comp_id, int isPrivate) {
        this.comp_id = comp_id;
        this.isPrivate = isPrivate;
    }

    public net.project.hibernate.model.PnPortfolioHasConfigurationPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPortfolioHasConfigurationPK comp_id) {
        this.comp_id = comp_id;
    }

    public int getIsPrivate() {
        return this.isPrivate;
    }

    public void setIsPrivate(int isPrivate) {
        this.isPrivate = isPrivate;
    }

    public net.project.hibernate.model.PnPortfolio getPnPortfolio() {
        return this.pnPortfolio;
    }

    public void setPnPortfolio(net.project.hibernate.model.PnPortfolio pnPortfolio) {
        this.pnPortfolio = pnPortfolio;
    }

    public net.project.hibernate.model.PnConfigurationSpace getPnConfigurationSpace() {
        return this.pnConfigurationSpace;
    }

    public void setPnConfigurationSpace(net.project.hibernate.model.PnConfigurationSpace pnConfigurationSpace) {
        this.pnConfigurationSpace = pnConfigurationSpace;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPortfolioHasConfiguration) ) return false;
        PnPortfolioHasConfiguration castOther = (PnPortfolioHasConfiguration) other;
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
