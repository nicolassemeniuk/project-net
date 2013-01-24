package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowStepHasGroup implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnWorkflowStepHasGroupPK comp_id;

    /** persistent field */
    private int isParticipant;

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
    private net.project.hibernate.model.PnGroup pnGroup;

    /** nullable persistent field */
    private net.project.hibernate.model.PnWorkflowStep pnWorkflowStep;

    /** persistent field */
    private Set pnWfRuleAuthHasGroups;

    /** full constructor */
    public PnWorkflowStepHasGroup(net.project.hibernate.model.PnWorkflowStepHasGroupPK comp_id, int isParticipant, BigDecimal createdById, Date createdDatetime, BigDecimal modifiedById, Date modifiedDatetime, Date crc, String recordStatus, net.project.hibernate.model.PnGroup pnGroup, net.project.hibernate.model.PnWorkflowStep pnWorkflowStep, Set pnWfRuleAuthHasGroups) {
        this.comp_id = comp_id;
        this.isParticipant = isParticipant;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.modifiedById = modifiedById;
        this.modifiedDatetime = modifiedDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnGroup = pnGroup;
        this.pnWorkflowStep = pnWorkflowStep;
        this.pnWfRuleAuthHasGroups = pnWfRuleAuthHasGroups;
    }

    /** default constructor */
    public PnWorkflowStepHasGroup() {
    }

    /** minimal constructor */
    public PnWorkflowStepHasGroup(net.project.hibernate.model.PnWorkflowStepHasGroupPK comp_id, int isParticipant, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus, Set pnWfRuleAuthHasGroups) {
        this.comp_id = comp_id;
        this.isParticipant = isParticipant;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWfRuleAuthHasGroups = pnWfRuleAuthHasGroups;
    }

    public net.project.hibernate.model.PnWorkflowStepHasGroupPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnWorkflowStepHasGroupPK comp_id) {
        this.comp_id = comp_id;
    }

    public int getIsParticipant() {
        return this.isParticipant;
    }

    public void setIsParticipant(int isParticipant) {
        this.isParticipant = isParticipant;
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

    public net.project.hibernate.model.PnGroup getPnGroup() {
        return this.pnGroup;
    }

    public void setPnGroup(net.project.hibernate.model.PnGroup pnGroup) {
        this.pnGroup = pnGroup;
    }

    public net.project.hibernate.model.PnWorkflowStep getPnWorkflowStep() {
        return this.pnWorkflowStep;
    }

    public void setPnWorkflowStep(net.project.hibernate.model.PnWorkflowStep pnWorkflowStep) {
        this.pnWorkflowStep = pnWorkflowStep;
    }

    public Set getPnWfRuleAuthHasGroups() {
        return this.pnWfRuleAuthHasGroups;
    }

    public void setPnWfRuleAuthHasGroups(Set pnWfRuleAuthHasGroups) {
        this.pnWfRuleAuthHasGroups = pnWfRuleAuthHasGroups;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnWorkflowStepHasGroup) ) return false;
        PnWorkflowStepHasGroup castOther = (PnWorkflowStepHasGroup) other;
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
