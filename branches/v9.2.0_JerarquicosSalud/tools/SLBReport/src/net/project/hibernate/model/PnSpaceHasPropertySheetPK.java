package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasPropertySheetPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal propertySheetId;

    /** full constructor */
    public PnSpaceHasPropertySheetPK(BigDecimal spaceId, BigDecimal propertySheetId) {
        this.spaceId = spaceId;
        this.propertySheetId = propertySheetId;
    }

    /** default constructor */
    public PnSpaceHasPropertySheetPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getPropertySheetId() {
        return this.propertySheetId;
    }

    public void setPropertySheetId(BigDecimal propertySheetId) {
        this.propertySheetId = propertySheetId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("propertySheetId", getPropertySheetId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasPropertySheetPK) ) return false;
        PnSpaceHasPropertySheetPK castOther = (PnSpaceHasPropertySheetPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getPropertySheetId(), castOther.getPropertySheetId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getPropertySheetId())
            .toHashCode();
    }

}
