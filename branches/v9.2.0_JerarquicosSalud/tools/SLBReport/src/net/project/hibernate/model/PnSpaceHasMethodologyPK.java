package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasMethodologyPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal methodologyId;

    /** full constructor */
    public PnSpaceHasMethodologyPK(BigDecimal spaceId, BigDecimal methodologyId) {
        this.spaceId = spaceId;
        this.methodologyId = methodologyId;
    }

    /** default constructor */
    public PnSpaceHasMethodologyPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getMethodologyId() {
        return this.methodologyId;
    }

    public void setMethodologyId(BigDecimal methodologyId) {
        this.methodologyId = methodologyId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("methodologyId", getMethodologyId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasMethodologyPK) ) return false;
        PnSpaceHasMethodologyPK castOther = (PnSpaceHasMethodologyPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getMethodologyId(), castOther.getMethodologyId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getMethodologyId())
            .toHashCode();
    }

}
