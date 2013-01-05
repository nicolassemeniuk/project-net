package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPortfolioHasSpacePK implements Serializable {

    /** identifier field */
    private BigDecimal portfolioId;

    /** identifier field */
    private BigDecimal spaceId;

    /** full constructor */
    public PnPortfolioHasSpacePK(BigDecimal portfolioId, BigDecimal spaceId) {
        this.portfolioId = portfolioId;
        this.spaceId = spaceId;
    }

    /** default constructor */
    public PnPortfolioHasSpacePK() {
    }

    public BigDecimal getPortfolioId() {
        return this.portfolioId;
    }

    public void setPortfolioId(BigDecimal portfolioId) {
        this.portfolioId = portfolioId;
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("portfolioId", getPortfolioId())
            .append("spaceId", getSpaceId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPortfolioHasSpacePK) ) return false;
        PnPortfolioHasSpacePK castOther = (PnPortfolioHasSpacePK) other;
        return new EqualsBuilder()
            .append(this.getPortfolioId(), castOther.getPortfolioId())
            .append(this.getSpaceId(), castOther.getSpaceId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPortfolioId())
            .append(getSpaceId())
            .toHashCode();
    }

}
