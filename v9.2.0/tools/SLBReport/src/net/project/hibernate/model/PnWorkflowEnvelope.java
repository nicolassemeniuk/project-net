package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowEnvelope implements Serializable {

    /** identifier field */
    private BigDecimal envelopeId;

    /** persistent field */
    private BigDecimal currentVersionId;

    /** nullable persistent field */
    private String envelopeName;

    /** nullable persistent field */
    private String envelopeDescription;

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
    private net.project.hibernate.model.PnWorkflow pnWorkflow;

    /** persistent field */
    private net.project.hibernate.model.PnWorkflowStrictness pnWorkflowStrictness;

    /** persistent field */
    private Set pnEnvelopeHasObjects;

    /** persistent field */
    private Set pnEnvelopeHistories;

    /** persistent field */
    private Set pnEnvelopeVersions;

    /** full constructor */
    public PnWorkflowEnvelope(BigDecimal envelopeId, BigDecimal currentVersionId, String envelopeName, String envelopeDescription, BigDecimal createdById, Date createdDatetime, BigDecimal modifiedById, Date modifiedDatetime, Date crc, String recordStatus, net.project.hibernate.model.PnWorkflow pnWorkflow, net.project.hibernate.model.PnWorkflowStrictness pnWorkflowStrictness, Set pnEnvelopeHasObjects, Set pnEnvelopeHistories, Set pnEnvelopeVersions) {
        this.envelopeId = envelopeId;
        this.currentVersionId = currentVersionId;
        this.envelopeName = envelopeName;
        this.envelopeDescription = envelopeDescription;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.modifiedById = modifiedById;
        this.modifiedDatetime = modifiedDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflow = pnWorkflow;
        this.pnWorkflowStrictness = pnWorkflowStrictness;
        this.pnEnvelopeHasObjects = pnEnvelopeHasObjects;
        this.pnEnvelopeHistories = pnEnvelopeHistories;
        this.pnEnvelopeVersions = pnEnvelopeVersions;
    }

    /** default constructor */
    public PnWorkflowEnvelope() {
    }

    /** minimal constructor */
    public PnWorkflowEnvelope(BigDecimal envelopeId, BigDecimal currentVersionId, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus, net.project.hibernate.model.PnWorkflow pnWorkflow, net.project.hibernate.model.PnWorkflowStrictness pnWorkflowStrictness, Set pnEnvelopeHasObjects, Set pnEnvelopeHistories, Set pnEnvelopeVersions) {
        this.envelopeId = envelopeId;
        this.currentVersionId = currentVersionId;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflow = pnWorkflow;
        this.pnWorkflowStrictness = pnWorkflowStrictness;
        this.pnEnvelopeHasObjects = pnEnvelopeHasObjects;
        this.pnEnvelopeHistories = pnEnvelopeHistories;
        this.pnEnvelopeVersions = pnEnvelopeVersions;
    }

    public BigDecimal getEnvelopeId() {
        return this.envelopeId;
    }

    public void setEnvelopeId(BigDecimal envelopeId) {
        this.envelopeId = envelopeId;
    }

    public BigDecimal getCurrentVersionId() {
        return this.currentVersionId;
    }

    public void setCurrentVersionId(BigDecimal currentVersionId) {
        this.currentVersionId = currentVersionId;
    }

    public String getEnvelopeName() {
        return this.envelopeName;
    }

    public void setEnvelopeName(String envelopeName) {
        this.envelopeName = envelopeName;
    }

    public String getEnvelopeDescription() {
        return this.envelopeDescription;
    }

    public void setEnvelopeDescription(String envelopeDescription) {
        this.envelopeDescription = envelopeDescription;
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

    public net.project.hibernate.model.PnWorkflow getPnWorkflow() {
        return this.pnWorkflow;
    }

    public void setPnWorkflow(net.project.hibernate.model.PnWorkflow pnWorkflow) {
        this.pnWorkflow = pnWorkflow;
    }

    public net.project.hibernate.model.PnWorkflowStrictness getPnWorkflowStrictness() {
        return this.pnWorkflowStrictness;
    }

    public void setPnWorkflowStrictness(net.project.hibernate.model.PnWorkflowStrictness pnWorkflowStrictness) {
        this.pnWorkflowStrictness = pnWorkflowStrictness;
    }

    public Set getPnEnvelopeHasObjects() {
        return this.pnEnvelopeHasObjects;
    }

    public void setPnEnvelopeHasObjects(Set pnEnvelopeHasObjects) {
        this.pnEnvelopeHasObjects = pnEnvelopeHasObjects;
    }

    public Set getPnEnvelopeHistories() {
        return this.pnEnvelopeHistories;
    }

    public void setPnEnvelopeHistories(Set pnEnvelopeHistories) {
        this.pnEnvelopeHistories = pnEnvelopeHistories;
    }

    public Set getPnEnvelopeVersions() {
        return this.pnEnvelopeVersions;
    }

    public void setPnEnvelopeVersions(Set pnEnvelopeVersions) {
        this.pnEnvelopeVersions = pnEnvelopeVersions;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("envelopeId", getEnvelopeId())
            .toString();
    }

}
