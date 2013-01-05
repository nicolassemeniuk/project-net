package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasSpacePK implements Serializable {

    /** identifier field */
    private BigDecimal parentSpaceId;

    /** identifier field */
    private BigDecimal childSpaceId;

    /** full constructor */
    public PnSpaceHasSpacePK(BigDecimal parentSpaceId, BigDecimal childSpaceId) {
        this.parentSpaceId = parentSpaceId;
        this.childSpaceId = childSpaceId;
    }

    /** default constructor */
    public PnSpaceHasSpacePK() {
    }

    public BigDecimal getParentSpaceId() {
        return this.parentSpaceId;
    }

    public void setParentSpaceId(BigDecimal parentSpaceId) {
        this.parentSpaceId = parentSpaceId;
    }

    public BigDecimal getChildSpaceId() {
        return this.childSpaceId;
    }

    public void setChildSpaceId(BigDecimal childSpaceId) {
        this.childSpaceId = childSpaceId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("parentSpaceId", getParentSpaceId())
            .append("childSpaceId", getChildSpaceId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasSpacePK) ) return false;
        PnSpaceHasSpacePK castOther = (PnSpaceHasSpacePK) other;
        return new EqualsBuilder()
            .append(this.getParentSpaceId(), castOther.getParentSpaceId())
            .append(this.getChildSpaceId(), castOther.getChildSpaceId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getParentSpaceId())
            .append(getChildSpaceId())
            .toHashCode();
    }

}
