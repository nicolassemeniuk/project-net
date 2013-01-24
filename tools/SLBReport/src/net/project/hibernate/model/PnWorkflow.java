package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflow implements Serializable {

    /** identifier field */
    private BigDecimal workflowId;

    /** nullable persistent field */
    private String workflowName;

    /** nullable persistent field */
    private String workflowDescription;

    /** persistent field */
    private BigDecimal createdById;

    /** nullable persistent field */
    private String notes;

    /** persistent field */
    private Date createdDatetime;

    /** nullable persistent field */
    private BigDecimal modifiedById;

    /** persistent field */
    private BigDecimal ownerId;

    /** nullable persistent field */
    private Date modifiedDatetime;

    /** persistent field */
    private int isPublished;

    /** persistent field */
    private int isGeneric;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private net.project.hibernate.model.PnWorkflowStrictness pnWorkflowStrictness;

    /** persistent field */
    private Set pnWorkflowEnvelopes;

    /** persistent field */
    private Set pnWorkflowTransitions;

    /** persistent field */
    private Set pnWorkflowHasObjectTypes;

    /** persistent field */
    private Set pnSpaceHasWorkflows;

    /** persistent field */
    private Set pnWorkflowSteps;

    /** full constructor */
    public PnWorkflow(BigDecimal workflowId, String workflowName, String workflowDescription, BigDecimal createdById, String notes, Date createdDatetime, BigDecimal modifiedById, BigDecimal ownerId, Date modifiedDatetime, int isPublished, int isGeneric, Date crc, String recordStatus, net.project.hibernate.model.PnWorkflowStrictness pnWorkflowStrictness, Set pnWorkflowEnvelopes, Set pnWorkflowTransitions, Set pnWorkflowHasObjectTypes, Set pnSpaceHasWorkflows, Set pnWorkflowSteps) {
        this.workflowId = workflowId;
        this.workflowName = workflowName;
        this.workflowDescription = workflowDescription;
        this.createdById = createdById;
        this.notes = notes;
        this.createdDatetime = createdDatetime;
        this.modifiedById = modifiedById;
        this.ownerId = ownerId;
        this.modifiedDatetime = modifiedDatetime;
        this.isPublished = isPublished;
        this.isGeneric = isGeneric;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflowStrictness = pnWorkflowStrictness;
        this.pnWorkflowEnvelopes = pnWorkflowEnvelopes;
        this.pnWorkflowTransitions = pnWorkflowTransitions;
        this.pnWorkflowHasObjectTypes = pnWorkflowHasObjectTypes;
        this.pnSpaceHasWorkflows = pnSpaceHasWorkflows;
        this.pnWorkflowSteps = pnWorkflowSteps;
    }

    /** default constructor */
    public PnWorkflow() {
    }

    /** minimal constructor */
    public PnWorkflow(BigDecimal workflowId, BigDecimal createdById, Date createdDatetime, BigDecimal ownerId, int isPublished, int isGeneric, Date crc, String recordStatus, net.project.hibernate.model.PnWorkflowStrictness pnWorkflowStrictness, Set pnWorkflowEnvelopes, Set pnWorkflowTransitions, Set pnWorkflowHasObjectTypes, Set pnSpaceHasWorkflows, Set pnWorkflowSteps) {
        this.workflowId = workflowId;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.ownerId = ownerId;
        this.isPublished = isPublished;
        this.isGeneric = isGeneric;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflowStrictness = pnWorkflowStrictness;
        this.pnWorkflowEnvelopes = pnWorkflowEnvelopes;
        this.pnWorkflowTransitions = pnWorkflowTransitions;
        this.pnWorkflowHasObjectTypes = pnWorkflowHasObjectTypes;
        this.pnSpaceHasWorkflows = pnSpaceHasWorkflows;
        this.pnWorkflowSteps = pnWorkflowSteps;
    }

    public BigDecimal getWorkflowId() {
        return this.workflowId;
    }

    public void setWorkflowId(BigDecimal workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkflowName() {
        return this.workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getWorkflowDescription() {
        return this.workflowDescription;
    }

    public void setWorkflowDescription(String workflowDescription) {
        this.workflowDescription = workflowDescription;
    }

    public BigDecimal getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(BigDecimal createdById) {
        this.createdById = createdById;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public BigDecimal getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(BigDecimal ownerId) {
        this.ownerId = ownerId;
    }

    public Date getModifiedDatetime() {
        return this.modifiedDatetime;
    }

    public void setModifiedDatetime(Date modifiedDatetime) {
        this.modifiedDatetime = modifiedDatetime;
    }

    public int getIsPublished() {
        return this.isPublished;
    }

    public void setIsPublished(int isPublished) {
        this.isPublished = isPublished;
    }

    public int getIsGeneric() {
        return this.isGeneric;
    }

    public void setIsGeneric(int isGeneric) {
        this.isGeneric = isGeneric;
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

    public net.project.hibernate.model.PnWorkflowStrictness getPnWorkflowStrictness() {
        return this.pnWorkflowStrictness;
    }

    public void setPnWorkflowStrictness(net.project.hibernate.model.PnWorkflowStrictness pnWorkflowStrictness) {
        this.pnWorkflowStrictness = pnWorkflowStrictness;
    }

    public Set getPnWorkflowEnvelopes() {
        return this.pnWorkflowEnvelopes;
    }

    public void setPnWorkflowEnvelopes(Set pnWorkflowEnvelopes) {
        this.pnWorkflowEnvelopes = pnWorkflowEnvelopes;
    }

    public Set getPnWorkflowTransitions() {
        return this.pnWorkflowTransitions;
    }

    public void setPnWorkflowTransitions(Set pnWorkflowTransitions) {
        this.pnWorkflowTransitions = pnWorkflowTransitions;
    }

    public Set getPnWorkflowHasObjectTypes() {
        return this.pnWorkflowHasObjectTypes;
    }

    public void setPnWorkflowHasObjectTypes(Set pnWorkflowHasObjectTypes) {
        this.pnWorkflowHasObjectTypes = pnWorkflowHasObjectTypes;
    }

    public Set getPnSpaceHasWorkflows() {
        return this.pnSpaceHasWorkflows;
    }

    public void setPnSpaceHasWorkflows(Set pnSpaceHasWorkflows) {
        this.pnSpaceHasWorkflows = pnSpaceHasWorkflows;
    }

    public Set getPnWorkflowSteps() {
        return this.pnWorkflowSteps;
    }

    public void setPnWorkflowSteps(Set pnWorkflowSteps) {
        this.pnWorkflowSteps = pnWorkflowSteps;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("workflowId", getWorkflowId())
            .toString();
    }

}
