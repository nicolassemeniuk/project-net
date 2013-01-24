package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowRuleType implements Serializable {

    /** identifier field */
    private BigDecimal ruleTypeId;

    /** persistent field */
    private String tableName;

    /** nullable persistent field */
    private String ruleTypeName;

    /** nullable persistent field */
    private String ruleTypeDescription;

    /** nullable persistent field */
    private String notes;

    /** persistent field */
    private BigDecimal createdById;

    /** persistent field */
    private Date createdDatetime;

    /** nullable persistent field */
    private BigDecimal modifiedById;

    /** nullable persistent field */
    private Date modifiedDatetime;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private Set pnWorkflowRules;

    /** full constructor */
    public PnWorkflowRuleType(BigDecimal ruleTypeId, String tableName, String ruleTypeName, String ruleTypeDescription, String notes, BigDecimal createdById, Date createdDatetime, BigDecimal modifiedById, Date modifiedDatetime, Date crc, String recordStatus, Set pnWorkflowRules) {
        this.ruleTypeId = ruleTypeId;
        this.tableName = tableName;
        this.ruleTypeName = ruleTypeName;
        this.ruleTypeDescription = ruleTypeDescription;
        this.notes = notes;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.modifiedById = modifiedById;
        this.modifiedDatetime = modifiedDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflowRules = pnWorkflowRules;
    }

    /** default constructor */
    public PnWorkflowRuleType() {
    }

    /** minimal constructor */
    public PnWorkflowRuleType(BigDecimal ruleTypeId, String tableName, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus, Set pnWorkflowRules) {
        this.ruleTypeId = ruleTypeId;
        this.tableName = tableName;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflowRules = pnWorkflowRules;
    }

    public BigDecimal getRuleTypeId() {
        return this.ruleTypeId;
    }

    public void setRuleTypeId(BigDecimal ruleTypeId) {
        this.ruleTypeId = ruleTypeId;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRuleTypeName() {
        return this.ruleTypeName;
    }

    public void setRuleTypeName(String ruleTypeName) {
        this.ruleTypeName = ruleTypeName;
    }

    public String getRuleTypeDescription() {
        return this.ruleTypeDescription;
    }

    public void setRuleTypeDescription(String ruleTypeDescription) {
        this.ruleTypeDescription = ruleTypeDescription;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(BigDecimal createdById) {
        this.createdById = createdById;
    }

    public Date getCreatedDatetime() {
        return this.createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public BigDecimal getModifiedById() {
        return this.modifiedById;
    }

    public void setModifiedById(BigDecimal modifiedById) {
        this.modifiedById = modifiedById;
    }

    public Date getModifiedDatetime() {
        return this.modifiedDatetime;
    }

    public void setModifiedDatetime(Date modifiedDatetime) {
        this.modifiedDatetime = modifiedDatetime;
    }

    public Date getCrc() {
        return this.crc;
    }

    public void setCrc(Date crc) {
        this.crc = crc;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Set getPnWorkflowRules() {
        return this.pnWorkflowRules;
    }

    public void setPnWorkflowRules(Set pnWorkflowRules) {
        this.pnWorkflowRules = pnWorkflowRules;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ruleTypeId", getRuleTypeId())
            .toString();
    }

}
