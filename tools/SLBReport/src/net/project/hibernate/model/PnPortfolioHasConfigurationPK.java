package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPortfolioHasConfigurationPK implements Serializable {

    /** identifier field */
    private BigDecimal portfolioId;

    /** identifier field */
    private BigDecimal configurationId;

    /** full constructor */
    public PnPortfolioHasConfigurationPK(BigDecimal portfolioId, BigDecimal configurationId) {
        this.portfolioId = portfolioId;
        this.configurationId = configurationId;
    }

    /** default constructor */
    public PnPortfolioHasConfigurationPK() {
    }

    public BigDecimal getPortfolioId() {
        return this.portfolioId;
    }

    public void setPortfolioId(BigDecimal portfolioId) {
        this.portfolioId = portfolioId;
    }

    public BigDecimal getConfigurationId() {
        return this.configurationId;
    }

    public void setConfigurationId(BigDecimal configurationId) {
        this.configurationId = configurationId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("portfolioId", getPortfolioId())
            .append("configurationId", getConfigurationId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPortfolioHasConfigurationPK) ) return false;
        PnPortfolioHasConfigurationPK castOther = (PnPortfolioHasConfigurationPK) other;
        return new EqualsBuilder()
            .append(this.getPortfolioId(), castOther.getPortfolioId())
            .append(this.getConfigurationId(), castOther.getConfigurationId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPortfolioId())
            .append(getConfigurationId())
            .toHashCode();
    }

}
