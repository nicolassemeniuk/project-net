package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnBusinessCategory implements Serializable {

    /** identifier field */
    private BigDecimal businessCategoryId;

    /** persistent field */
    private String categoryName;

    /** persistent field */
    private String categoryDescription;

    /** full constructor */
    public PnBusinessCategory(BigDecimal businessCategoryId, String categoryName, String categoryDescription) {
        this.businessCategoryId = businessCategoryId;
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
    }

    /** default constructor */
    public PnBusinessCategory() {
    }

    public BigDecimal getBusinessCategoryId() {
        return this.businessCategoryId;
    }

    public void setBusinessCategoryId(BigDecimal businessCategoryId) {
        this.businessCategoryId = businessCategoryId;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return this.categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("businessCategoryId", getBusinessCategoryId())
            .toString();
    }

}
