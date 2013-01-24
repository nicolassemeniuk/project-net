package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnObjectInCategoryPK implements Serializable {

    /** identifier field */
    private BigDecimal categoryId;

    /** identifier field */
    private BigDecimal objectId;

    /** full constructor */
    public PnObjectInCategoryPK(BigDecimal categoryId, BigDecimal objectId) {
        this.categoryId = categoryId;
        this.objectId = objectId;
    }

    /** default constructor */
    public PnObjectInCategoryPK() {
    }

    public BigDecimal getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(BigDecimal categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("categoryId", getCategoryId())
            .append("objectId", getObjectId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnObjectInCategoryPK) ) return false;
        PnObjectInCategoryPK castOther = (PnObjectInCategoryPK) other;
        return new EqualsBuilder()
            .append(this.getCategoryId(), castOther.getCategoryId())
            .append(this.getObjectId(), castOther.getObjectId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCategoryId())
            .append(getObjectId())
            .toHashCode();
    }

}
