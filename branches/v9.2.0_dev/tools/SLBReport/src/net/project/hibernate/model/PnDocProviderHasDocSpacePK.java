package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocProviderHasDocSpacePK implements Serializable {

    /** identifier field */
    private BigDecimal docProviderId;

    /** identifier field */
    private BigDecimal docSpaceId;

    /** full constructor */
    public PnDocProviderHasDocSpacePK(BigDecimal docProviderId, BigDecimal docSpaceId) {
        this.docProviderId = docProviderId;
        this.docSpaceId = docSpaceId;
    }

    /** default constructor */
    public PnDocProviderHasDocSpacePK() {
    }

    public BigDecimal getDocProviderId() {
        return this.docProviderId;
    }

    public void setDocProviderId(BigDecimal docProviderId) {
        this.docProviderId = docProviderId;
    }

    public BigDecimal getDocSpaceId() {
        return this.docSpaceId;
    }

    public void setDocSpaceId(BigDecimal docSpaceId) {
        this.docSpaceId = docSpaceId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docProviderId", getDocProviderId())
            .append("docSpaceId", getDocSpaceId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocProviderHasDocSpacePK) ) return false;
        PnDocProviderHasDocSpacePK castOther = (PnDocProviderHasDocSpacePK) other;
        return new EqualsBuilder()
            .append(this.getDocProviderId(), castOther.getDocProviderId())
            .append(this.getDocSpaceId(), castOther.getDocSpaceId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDocProviderId())
            .append(getDocSpaceId())
            .toHashCode();
    }

}
