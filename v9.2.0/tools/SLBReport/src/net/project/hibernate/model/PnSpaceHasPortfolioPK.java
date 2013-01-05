package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasPortfolioPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal portfolioId;

    /** full constructor */
    public PnSpaceHasPortfolioPK(BigDecimal spaceId, BigDecimal portfolioId) {
        this.spaceId = spaceId;
        this.portfolioId = portfolioId;
    }

    /** default constructor */
    public PnSpaceHasPortfolioPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getPortfolioId() {
        return this.portfolioId;
    }

    public void setPortfolioId(BigDecimal portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("portfolioId", getPortfolioId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasPortfolioPK) ) return false;
        PnSpaceHasPortfolioPK castOther = (PnSpaceHasPortfolioPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getPortfolioId(), castOther.getPortfolioId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getPortfolioId())
            .toHashCode();
    }

}
