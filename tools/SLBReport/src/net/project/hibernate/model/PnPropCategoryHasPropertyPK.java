package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPropCategoryHasPropertyPK implements Serializable {

    /** identifier field */
    private BigDecimal categoryId;

    /** identifier field */
    private String property;

    /** full constructor */
    public PnPropCategoryHasPropertyPK(BigDecimal categoryId, String property) {
        this.categoryId = categoryId;
        this.property = property;
    }

    /** default constructor */
    public PnPropCategoryHasPropertyPK() {
    }

    public BigDecimal getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(BigDecimal categoryId) {
        this.categoryId = categoryId;
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("categoryId", getCategoryId())
            .append("property", getProperty())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPropCategoryHasPropertyPK) ) return false;
        PnPropCategoryHasPropertyPK castOther = (PnPropCategoryHasPropertyPK) other;
        return new EqualsBuilder()
            .append(this.getCategoryId(), castOther.getCategoryId())
            .append(this.getProperty(), castOther.getProperty())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCategoryId())
            .append(getProperty())
            .toHashCode();
    }

}
