package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowRule implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnWorkflowRulePK comp_id;

    /** nullable persistent field */
    private String ruleName;

    /** nullable persistent field */
    private String ruleDescription;

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

    /** nullable persistent field */
    private net.project.hibernate.model.PnWfRuleAuth pnWfRuleAuth;

    /** nullable persistent field */
    private net.project.hibernate.model.PnWorkflowTransition pnWorkflowTransition;

    /** persistent field */
    private net.project.hibernate.model.PnWorkflowRuleType pnWorkflowRuleType;

    /** persistent field */
    private net.project.hibernate.model.PnWorkflowRuleStatus pnWorkflowRuleStatus;

    /** full constructor */
    public PnWorkflowRule(net.project.hibernate.model.PnWorkflowRulePK comp_id, String ruleName, String ruleDescription, String notes, BigDecimal createdById, Date createdDatetime, BigDecimal modifiedById, Date modifiedDatetime, Date crc, String recordStatus, net.project.hibernate.model.PnWfRuleAuth pnWfRuleAuth, net.project.hibernate.model.PnWorkflowTransition pnWorkflowTransition, net.project.hibernate.model.PnWorkflowRuleType pnWorkflowRuleType, net.project.hibernate.model.PnWorkflowRuleStatus pnWorkflowRuleStatus) {
        this.comp_id = comp_id;
        this.ruleName = ruleName;
        this.ruleDescription = ruleDescription;
        this.notes = notes;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.modifiedById = modifiedById;
        this.modifiedDatetime = modifiedDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWfRuleAuth = pnWfRuleAuth;
        this.pnWorkflowTransition = pnWorkflowTransition;
        this.pnWorkflowRuleType = pnWorkflowRuleType;
        this.pnWorkflowRuleStatus = pnWorkflowRuleStatus;
    }

    /** default constructor */
    public PnWorkflowRule() {
    }

    /** minimal constructor */
    public PnWorkflowRule(net.project.hibernate.model.PnWorkflowRulePK comp_id, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus, net.project.hibernate.model.PnWorkflowRuleType pnWorkflowRuleType, net.project.hibernate.model.PnWorkflowRuleStatus pnWorkflowRuleStatus) {
        this.comp_id = comp_id;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflowRuleType = pnWorkflowRuleType;
        this.pnWorkflowRuleStatus = pnWorkflowRuleStatus;
    }

    public net.project.hibernate.model.PnWorkflowRulePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnWorkflowRulePK comp_id) {
        this.comp_id = comp_id;
    }

    public String getRuleName() {
        return this.ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleDescription() {
        return this.ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
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

    public net.project.hibernate.model.PnWfRuleAuth getPnWfRuleAuth() {
        return this.pnWfRuleAuth;
    }

    public void setPnWfRuleAuth(net.project.hibernate.model.PnWfRuleAuth pnWfRuleAuth) {
        this.pnWfRuleAuth = pnWfRuleAuth;
    }

    public net.project.hibernate.model.PnWorkflowTransition getPnWorkflowTransition() {
        return this.pnWorkflowTransition;
    }

    public void setPnWorkflowTransition(net.project.hibernate.model.PnWorkflowTransition pnWorkflowTransition) {
        this.pnWorkflowTransition = pnWorkflowTransition;
    }

    public net.project.hibernate.model.PnWorkflowRuleType getPnWorkflowRuleType() {
        return this.pnWorkflowRuleType;
    }

    public void setPnWorkflowRuleType(net.project.hibernate.model.PnWorkflowRuleType pnWorkflowRuleType) {
        this.pnWorkflowRuleType = pnWorkflowRuleType;
    }

    public net.project.hibernate.model.PnWorkflowRuleStatus getPnWorkflowRuleStatus() {
        return this.pnWorkflowRuleStatus;
    }

    public void setPnWorkflowRuleStatus(net.project.hibernate.model.PnWorkflowRuleStatus pnWorkflowRuleStatus) {
        this.pnWorkflowRuleStatus = pnWorkflowRuleStatus;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnWorkflowRule) ) return false;
        PnWorkflowRule castOther = (PnWorkflowRule) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
