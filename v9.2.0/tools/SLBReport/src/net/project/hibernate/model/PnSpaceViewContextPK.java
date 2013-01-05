package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceViewContextPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal viewId;

    /** full constructor */
    public PnSpaceViewContextPK(BigDecimal spaceId, BigDecimal viewId) {
        this.spaceId = spaceId;
        this.viewId = viewId;
    }

    /** default constructor */
    public PnSpaceViewContextPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getViewId() {
        return this.viewId;
    }

    public void setViewId(BigDecimal viewId) {
        this.viewId = viewId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("viewId", getViewId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceViewContextPK) ) return false;
        PnSpaceViewContextPK castOther = (PnSpaceViewContextPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getViewId(), castOther.getViewId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getViewId())
            .toHashCode();
    }

}
