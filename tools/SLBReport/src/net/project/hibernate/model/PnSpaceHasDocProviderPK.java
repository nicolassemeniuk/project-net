package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasDocProviderPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal docProviderId;

    /** full constructor */
    public PnSpaceHasDocProviderPK(BigDecimal spaceId, BigDecimal docProviderId) {
        this.spaceId = spaceId;
        this.docProviderId = docProviderId;
    }

    /** default constructor */
    public PnSpaceHasDocProviderPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getDocProviderId() {
        return this.docProviderId;
    }

    public void setDocProviderId(BigDecimal docProviderId) {
        this.docProviderId = docProviderId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("docProviderId", getDocProviderId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasDocProviderPK) ) return false;
        PnSpaceHasDocProviderPK castOther = (PnSpaceHasDocProviderPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getDocProviderId(), castOther.getDocProviderId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getDocProviderId())
            .toHashCode();
    }

}
