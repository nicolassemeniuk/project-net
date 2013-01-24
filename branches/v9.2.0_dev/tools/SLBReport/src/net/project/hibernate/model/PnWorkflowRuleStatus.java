package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowRuleStatus implements Serializable {

    /** identifier field */
    private BigDecimal ruleStatusId;

    /** nullable persistent field */
    private String statusName;

    /** nullable persistent field */
    private String statusDescription;

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
    public PnWorkflowRuleStatus(BigDecimal ruleStatusId, String statusName, String statusDescription, BigDecimal createdById, Date createdDatetime, BigDecimal modifiedById, Date modifiedDatetime, Date crc, String recordStatus, Set pnWorkflowRules) {
        this.ruleStatusId = ruleStatusId;
        this.statusName = statusName;
        this.statusDescription = statusDescription;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.modifiedById = modifiedById;
        this.modifiedDatetime = modifiedDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflowRules = pnWorkflowRules;
    }

    /** default constructor */
    public PnWorkflowRuleStatus() {
    }

    /** minimal constructor */
    public PnWorkflowRuleStatus(BigDecimal ruleStatusId, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus, Set pnWorkflowRules) {
        this.ruleStatusId = ruleStatusId;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflowRules = pnWorkflowRules;
    }

    public BigDecimal getRuleStatusId() {
        return this.ruleStatusId;
    }

    public void setRuleStatusId(BigDecimal ruleStatusId) {
        this.ruleStatusId = ruleStatusId;
    }

    public String getStatusName() {
        return this.statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusDescription() {
        return this.statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
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
            .append("ruleStatusId", getRuleStatusId())
            .toString();
    }

}
