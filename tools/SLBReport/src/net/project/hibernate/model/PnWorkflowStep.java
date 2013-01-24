package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowStep implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnWorkflowStepPK comp_id;

    /** nullable persistent field */
    private String stepName;

    /** nullable persistent field */
    private String stepDescription;

    /** persistent field */
    private int isFinalStep;

    /** persistent field */
    private int isInitialStep;

    /** nullable persistent field */
    private BigDecimal entryStatusId;

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
    private Clob notesClob;

    /** nullable persistent field */
    private Integer stepSequence;

    /** nullable persistent field */
    private net.project.hibernate.model.PnWorkflow pnWorkflow;

    /** persistent field */
    private net.project.hibernate.model.PnSubscription pnSubscription;

    /** persistent field */
    private Set pnWorkflowStepHasGroups;

    /** persistent field */
    private Set pnWorkflowTransitionsByEndStepIdAndWorkflowId;

    /** persistent field */
    private Set pnWorkflowTransitionsByBeginStepIdAndWorkflowId;

    /** persistent field */
    private Set pnEnvelopeVersions;

    /** full constructor */
    public PnWorkflowStep(net.project.hibernate.model.PnWorkflowStepPK comp_id, String stepName, String stepDescription, int isFinalStep, int isInitialStep, BigDecimal entryStatusId, BigDecimal createdById, Date createdDatetime, BigDecimal modifiedById, Date modifiedDatetime, Date crc, String recordStatus, Clob notesClob, Integer stepSequence, net.project.hibernate.model.PnWorkflow pnWorkflow, net.project.hibernate.model.PnSubscription pnSubscription, Set pnWorkflowStepHasGroups, Set pnWorkflowTransitionsByEndStepIdAndWorkflowId, Set pnWorkflowTransitionsByBeginStepIdAndWorkflowId, Set pnEnvelopeVersions) {
        this.comp_id = comp_id;
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.isFinalStep = isFinalStep;
        this.isInitialStep = isInitialStep;
        this.entryStatusId = entryStatusId;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.modifiedById = modifiedById;
        this.modifiedDatetime = modifiedDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.notesClob = notesClob;
        this.stepSequence = stepSequence;
        this.pnWorkflow = pnWorkflow;
        this.pnSubscription = pnSubscription;
        this.pnWorkflowStepHasGroups = pnWorkflowStepHasGroups;
        this.pnWorkflowTransitionsByEndStepIdAndWorkflowId = pnWorkflowTransitionsByEndStepIdAndWorkflowId;
        this.pnWorkflowTransitionsByBeginStepIdAndWorkflowId = pnWorkflowTransitionsByBeginStepIdAndWorkflowId;
        this.pnEnvelopeVersions = pnEnvelopeVersions;
    }

    /** default constructor */
    public PnWorkflowStep() {
    }

    /** minimal constructor */
    public PnWorkflowStep(net.project.hibernate.model.PnWorkflowStepPK comp_id, int isFinalStep, int isInitialStep, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus, net.project.hibernate.model.PnSubscription pnSubscription, Set pnWorkflowStepHasGroups, Set pnWorkflowTransitionsByEndStepIdAndWorkflowId, Set pnWorkflowTransitionsByBeginStepIdAndWorkflowId, Set pnEnvelopeVersions) {
        this.comp_id = comp_id;
        this.isFinalStep = isFinalStep;
        this.isInitialStep = isInitialStep;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnSubscription = pnSubscription;
        this.pnWorkflowStepHasGroups = pnWorkflowStepHasGroups;
        this.pnWorkflowTransitionsByEndStepIdAndWorkflowId = pnWorkflowTransitionsByEndStepIdAndWorkflowId;
        this.pnWorkflowTransitionsByBeginStepIdAndWorkflowId = pnWorkflowTransitionsByBeginStepIdAndWorkflowId;
        this.pnEnvelopeVersions = pnEnvelopeVersions;
    }

    public net.project.hibernate.model.PnWorkflowStepPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnWorkflowStepPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getStepName() {
        return this.stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepDescription() {
        return this.stepDescription;
    }

    public void setStepDescription(String stepDescription) {
        this.stepDescription = stepDescription;
    }

    public int getIsFinalStep() {
        return this.isFinalStep;
    }

    public void setIsFinalStep(int isFinalStep) {
        this.isFinalStep = isFinalStep;
    }

    public int getIsInitialStep() {
        return this.isInitialStep;
    }

    public void setIsInitialStep(int isInitialStep) {
        this.isInitialStep = isInitialStep;
    }

    public BigDecimal getEntryStatusId() {
        return this.entryStatusId;
    }

    public void setEntryStatusId(BigDecimal entryStatusId) {
        this.entryStatusId = entryStatusId;
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

    public Clob getNotesClob() {
        return this.notesClob;
    }

    public void setNotesClob(Clob notesClob) {
        this.notesClob = notesClob;
    }

    public Integer getStepSequence() {
        return this.stepSequence;
    }

    public void setStepSequence(Integer stepSequence) {
        this.stepSequence = stepSequence;
    }

    public net.project.hibernate.model.PnWorkflow getPnWorkflow() {
        return this.pnWorkflow;
    }

    public void setPnWorkflow(net.project.hibernate.model.PnWorkflow pnWorkflow) {
        this.pnWorkflow = pnWorkflow;
    }

    public net.project.hibernate.model.PnSubscription getPnSubscription() {
        return this.pnSubscription;
    }

    public void setPnSubscription(net.project.hibernate.model.PnSubscription pnSubscription) {
        this.pnSubscription = pnSubscription;
    }

    public Set getPnWorkflowStepHasGroups() {
        return this.pnWorkflowStepHasGroups;
    }

    public void setPnWorkflowStepHasGroups(Set pnWorkflowStepHasGroups) {
        this.pnWorkflowStepHasGroups = pnWorkflowStepHasGroups;
    }

    public Set getPnWorkflowTransitionsByEndStepIdAndWorkflowId() {
        return this.pnWorkflowTransitionsByEndStepIdAndWorkflowId;
    }

    public void setPnWorkflowTransitionsByEndStepIdAndWorkflowId(Set pnWorkflowTransitionsByEndStepIdAndWorkflowId) {
        this.pnWorkflowTransitionsByEndStepIdAndWorkflowId = pnWorkflowTransitionsByEndStepIdAndWorkflowId;
    }

    public Set getPnWorkflowTransitionsByBeginStepIdAndWorkflowId() {
        return this.pnWorkflowTransitionsByBeginStepIdAndWorkflowId;
    }

    public void setPnWorkflowTransitionsByBeginStepIdAndWorkflowId(Set pnWorkflowTransitionsByBeginStepIdAndWorkflowId) {
        this.pnWorkflowTransitionsByBeginStepIdAndWorkflowId = pnWorkflowTransitionsByBeginStepIdAndWorkflowId;
    }

    public Set getPnEnvelopeVersions() {
        return this.pnEnvelopeVersions;
    }

    public void setPnEnvelopeVersions(Set pnEnvelopeVersions) {
        this.pnEnvelopeVersions = pnEnvelopeVersions;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnWorkflowStep) ) return false;
        PnWorkflowStep castOther = (PnWorkflowStep) other;
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
