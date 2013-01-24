package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCategoryHasCategoryPK implements Serializable {

    /** identifier field */
    private BigDecimal parentCategoryId;

    /** identifier field */
    private BigDecimal childCategoryId;

    /** full constructor */
    public PnCategoryHasCategoryPK(BigDecimal parentCategoryId, BigDecimal childCategoryId) {
        this.parentCategoryId = parentCategoryId;
        this.childCategoryId = childCategoryId;
    }

    /** default constructor */
    public PnCategoryHasCategoryPK() {
    }

    public BigDecimal getParentCategoryId() {
        return this.parentCategoryId;
    }

    public void setParentCategoryId(BigDecimal parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public BigDecimal getChildCategoryId() {
        return this.childCategoryId;
    }

    public void setChildCategoryId(BigDecimal childCategoryId) {
        this.childCategoryId = childCategoryId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("parentCategoryId", getParentCategoryId())
            .append("childCategoryId", getChildCategoryId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnCategoryHasCategoryPK) ) return false;
        PnCategoryHasCategoryPK castOther = (PnCategoryHasCategoryPK) other;
        return new EqualsBuilder()
            .append(this.getParentCategoryId(), castOther.getParentCategoryId())
            .append(this.getChildCategoryId(), castOther.getChildCategoryId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getParentCategoryId())
            .append(getChildCategoryId())
            .toHashCode();
    }

}
