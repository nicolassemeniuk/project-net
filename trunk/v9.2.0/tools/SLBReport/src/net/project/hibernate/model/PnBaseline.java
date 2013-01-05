package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnBaseline implements Serializable {

    /** identifier field */
    private BigDecimal baselineId;

    /** persistent field */
    private BigDecimal objectId;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private Integer isDefaultForObject;

    /** nullable persistent field */
    private Date dateCreated;

    /** nullable persistent field */
    private Date dateModified;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private Set pnBaselineHasTasks;

    /** persistent field */
    private Set pnBaselineHasPlans;

    /** full constructor */
    public PnBaseline(BigDecimal baselineId, BigDecimal objectId, String name, String description, Integer isDefaultForObject, Date dateCreated, Date dateModified, String recordStatus, Set pnBaselineHasTasks, Set pnBaselineHasPlans) {
        this.baselineId = baselineId;
        this.objectId = objectId;
        this.name = name;
        this.description = description;
        this.isDefaultForObject = isDefaultForObject;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.recordStatus = recordStatus;
        this.pnBaselineHasTasks = pnBaselineHasTasks;
        this.pnBaselineHasPlans = pnBaselineHasPlans;
    }

    /** default constructor */
    public PnBaseline() {
    }

    /** minimal constructor */
    public PnBaseline(BigDecimal baselineId, BigDecimal objectId, String recordStatus, Set pnBaselineHasTasks, Set pnBaselineHasPlans) {
        this.baselineId = baselineId;
        this.objectId = objectId;
        this.recordStatus = recordStatus;
        this.pnBaselineHasTasks = pnBaselineHasTasks;
        this.pnBaselineHasPlans = pnBaselineHasPlans;
    }

    public BigDecimal getBaselineId() {
        return this.baselineId;
    }

    public void setBaselineId(BigDecimal baselineId) {
        this.baselineId = baselineId;
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
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

    public Integer getIsDefaultForObject() {
        return this.isDefaultForObject;
    }

    public void setIsDefaultForObject(Integer isDefaultForObject) {
        this.isDefaultForObject = isDefaultForObject;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return this.dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Set getPnBaselineHasTasks() {
        return this.pnBaselineHasTasks;
    }

    public void setPnBaselineHasTasks(Set pnBaselineHasTasks) {
        this.pnBaselineHasTasks = pnBaselineHasTasks;
    }

    public Set getPnBaselineHasPlans() {
        return this.pnBaselineHasPlans;
    }

    public void setPnBaselineHasPlans(Set pnBaselineHasPlans) {
        this.pnBaselineHasPlans = pnBaselineHasPlans;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("baselineId", getBaselineId())
            .toString();
    }

}
