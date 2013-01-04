package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnIndustryClassification implements Serializable {

    /** identifier field */
    private BigDecimal industryId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** full constructor */
    public PnIndustryClassification(BigDecimal industryId, String name, String description) {
        this.industryId = industryId;
        this.name = name;
        this.description = description;
    }

    /** default constructor */
    public PnIndustryClassification() {
    }

    /** minimal constructor */
    public PnIndustryClassification(BigDecimal industryId, String name) {
        this.industryId = industryId;
        this.name = name;
    }

    public BigDecimal getIndustryId() {
        return this.industryId;
    }

    public void setIndustryId(BigDecimal industryId) {
        this.industryId = industryId;
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
            .append("industryId", getIndustryId())
            .toString();
    }

}
