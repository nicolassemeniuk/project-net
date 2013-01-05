package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCategory implements Serializable {

    /** identifier field */
    private BigDecimal categoryId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** full constructor */
    public PnCategory(BigDecimal categoryId, String name, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }

    /** default constructor */
    public PnCategory() {
    }

    /** minimal constructor */
    public PnCategory(BigDecimal categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public BigDecimal getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(BigDecimal categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("categoryId", getCategoryId())
            .toString();
    }

}
