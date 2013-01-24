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
public class PnEnvelopeVersion implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnEnvelopeVersionPK comp_id;

    /** persistent field */
    private BigDecimal priorityId;

    /** persistent field */
    private BigDecimal createdById;

    /** persistent field */
    private Date createdDatetime;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Clob commentsClob;

    /** nullable persistent field */
    private net.project.hibernate.model.PnWorkflowEnvelope pnWorkflowEnvelope;

    /** persistent field */
    private net.project.hibernate.model.PnWorkflowTransition pnWorkflowTransition;

    /** persistent field */
    private net.project.hibernate.model.PnWorkflowStep pnWorkflowStep;

    /** persistent field */
    private net.project.hibernate.model.PnWorkflowStatus pnWorkflowStatus;

    /** persistent field */
    private Set pnEnvelopeVersionHasObjects;

    /** full constructor */
    public PnEnvelopeVersion(net.project.hibernate.model.PnEnvelopeVersionPK comp_id, BigDecimal priorityId, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus, Clob commentsClob, net.project.hibernate.model.PnWorkflowEnvelope pnWorkflowEnvelope, net.project.hibernate.model.PnWorkflowTransition pnWorkflowTransition, net.project.hibernate.model.PnWorkflowStep pnWorkflowStep, net.project.hibernate.model.PnWorkflowStatus pnWorkflowStatus, Set pnEnvelopeVersionHasObjects) {
        this.comp_id = comp_id;
        this.priorityId = priorityId;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.commentsClob = commentsClob;
        this.pnWorkflowEnvelope = pnWorkflowEnvelope;
        this.pnWorkflowTransition = pnWorkflowTransition;
        this.pnWorkflowStep = pnWorkflowStep;
        this.pnWorkflowStatus = pnWorkflowStatus;
        this.pnEnvelopeVersionHasObjects = pnEnvelopeVersionHasObjects;
    }

    /** default constructor */
    public PnEnvelopeVersion() {
    }

    /** minimal constructor */
    public PnEnvelopeVersion(net.project.hibernate.model.PnEnvelopeVersionPK comp_id, BigDecimal priorityId, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus, net.project.hibernate.model.PnWorkflowTransition pnWorkflowTransition, net.project.hibernate.model.PnWorkflowStep pnWorkflowStep, net.project.hibernate.model.PnWorkflowStatus pnWorkflowStatus, Set pnEnvelopeVersionHasObjects) {
        this.comp_id = comp_id;
        this.priorityId = priorityId;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflowTransition = pnWorkflowTransition;
        this.pnWorkflowStep = pnWorkflowStep;
        this.pnWorkflowStatus = pnWorkflowStatus;
        this.pnEnvelopeVersionHasObjects = pnEnvelopeVersionHasObjects;
    }

    public net.project.hibernate.model.PnEnvelopeVersionPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnEnvelopeVersionPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getPriorityId() {
        return this.priorityId;
    }

    public void setPriorityId(BigDecimal priorityId) {
        this.priorityId = priorityId;
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

    public Clob getCommentsClob() {
        return this.commentsClob;
    }

    public void setCommentsClob(Clob commentsClob) {
        this.commentsClob = commentsClob;
    }

    public net.project.hibernate.model.PnWorkflowEnvelope getPnWorkflowEnvelope() {
        return this.pnWorkflowEnvelope;
    }

    public void setPnWorkflowEnvelope(net.project.hibernate.model.PnWorkflowEnvelope pnWorkflowEnvelope) {
        this.pnWorkflowEnvelope = pnWorkflowEnvelope;
    }

    public net.project.hibernate.model.PnWorkflowTransition getPnWorkflowTransition() {
        return this.pnWorkflowTransition;
    }

    public void setPnWorkflowTransition(net.project.hibernate.model.PnWorkflowTransition pnWorkflowTransition) {
        this.pnWorkflowTransition = pnWorkflowTransition;
    }

    public net.project.hibernate.model.PnWorkflowStep getPnWorkflowStep() {
        return this.pnWorkflowStep;
    }

    public void setPnWorkflowStep(net.project.hibernate.model.PnWorkflowStep pnWorkflowStep) {
        this.pnWorkflowStep = pnWorkflowStep;
    }

    public net.project.hibernate.model.PnWorkflowStatus getPnWorkflowStatus() {
        return this.pnWorkflowStatus;
    }

    public void setPnWorkflowStatus(net.project.hibernate.model.PnWorkflowStatus pnWorkflowStatus) {
        this.pnWorkflowStatus = pnWorkflowStatus;
    }

    public Set getPnEnvelopeVersionHasObjects() {
        return this.pnEnvelopeVersionHasObjects;
    }

    public void setPnEnvelopeVersionHasObjects(Set pnEnvelopeVersionHasObjects) {
        this.pnEnvelopeVersionHasObjects = pnEnvelopeVersionHasObjects;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnEnvelopeVersion) ) return false;
        PnEnvelopeVersion castOther = (PnEnvelopeVersion) other;
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
