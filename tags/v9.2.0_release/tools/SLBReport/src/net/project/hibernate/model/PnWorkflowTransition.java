package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowTransition implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnWorkflowTransitionPK comp_id;

    /** nullable persistent field */
    private String transitionVerb;

    /** nullable persistent field */
    private String transitionDescription;

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
    private net.project.hibernate.model.PnWorkflow pnWorkflow;

    /** persistent field */
    private net.project.hibernate.model.PnWorkflowStep pnWorkflowStepByEndStepIdAndWorkflowId;

    /** persistent field */
    private net.project.hibernate.model.PnWorkflowStep pnWorkflowStepByBeginStepIdAndWorkflowId;

    /** persistent field */
    private Set pnWorkflowRules;

    /** persistent field */
    private Set pnEnvelopeVersions;

    /** full constructor */
    public PnWorkflowTransition(net.project.hibernate.model.PnWorkflowTransitionPK comp_id, String transitionVerb, String transitionDescription, BigDecimal createdById, Date createdDatetime, BigDecimal modifiedById, Date modifiedDatetime, Date crc, String recordStatus, net.project.hibernate.model.PnWorkflow pnWorkflow, net.project.hibernate.model.PnWorkflowStep pnWorkflowStepByEndStepIdAndWorkflowId, net.project.hibernate.model.PnWorkflowStep pnWorkflowStepByBeginStepIdAndWorkflowId, Set pnWorkflowRules, Set pnEnvelopeVersions) {
        this.comp_id = comp_id;
        this.transitionVerb = transitionVerb;
        this.transitionDescription = transitionDescription;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.modifiedById = modifiedById;
        this.modifiedDatetime = modifiedDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflow = pnWorkflow;
        this.pnWorkflowStepByEndStepIdAndWorkflowId = pnWorkflowStepByEndStepIdAndWorkflowId;
        this.pnWorkflowStepByBeginStepIdAndWorkflowId = pnWorkflowStepByBeginStepIdAndWorkflowId;
        this.pnWorkflowRules = pnWorkflowRules;
        this.pnEnvelopeVersions = pnEnvelopeVersions;
    }

    /** default constructor */
    public PnWorkflowTransition() {
    }

    /** minimal constructor */
    public PnWorkflowTransition(net.project.hibernate.model.PnWorkflowTransitionPK comp_id, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus, net.project.hibernate.model.PnWorkflowStep pnWorkflowStepByEndStepIdAndWorkflowId, net.project.hibernate.model.PnWorkflowStep pnWorkflowStepByBeginStepIdAndWorkflowId, Set pnWorkflowRules, Set pnEnvelopeVersions) {
        this.comp_id = comp_id;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflowStepByEndStepIdAndWorkflowId = pnWorkflowStepByEndStepIdAndWorkflowId;
        this.pnWorkflowStepByBeginStepIdAndWorkflowId = pnWorkflowStepByBeginStepIdAndWorkflowId;
        this.pnWorkflowRules = pnWorkflowRules;
        this.pnEnvelopeVersions = pnEnvelopeVersions;
    }

    public net.project.hibernate.model.PnWorkflowTransitionPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnWorkflowTransitionPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getTransitionVerb() {
        return this.transitionVerb;
    }

    public void setTransitionVerb(String transitionVerb) {
        this.transitionVerb = transitionVerb;
    }

    public String getTransitionDescription() {
        return this.transitionDescription;
    }

    public void setTransitionDescription(String transitionDescription) {
        this.transitionDescription = transitionDescription;
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

    public net.project.hibernate.model.PnWorkflowStep getPnWorkflowStepByEndStepIdAndWorkflowId() {
        return this.pnWorkflowStepByEndStepIdAndWorkflowId;
    }

    public void setPnWorkflowStepByEndStepIdAndWorkflowId(net.project.hibernate.model.PnWorkflowStep pnWorkflowStepByEndStepIdAndWorkflowId) {
        this.pnWorkflowStepByEndStepIdAndWorkflowId = pnWorkflowStepByEndStepIdAndWorkflowId;
    }

    public net.project.hibernate.model.PnWorkflowStep getPnWorkflowStepByBeginStepIdAndWorkflowId() {
        return this.pnWorkflowStepByBeginStepIdAndWorkflowId;
    }

    public void setPnWorkflowStepByBeginStepIdAndWorkflowId(net.project.hibernate.model.PnWorkflowStep pnWorkflowStepByBeginStepIdAndWorkflowId) {
        this.pnWorkflowStepByBeginStepIdAndWorkflowId = pnWorkflowStepByBeginStepIdAndWorkflowId;
    }

    public Set getPnWorkflowRules() {
        return this.pnWorkflowRules;
    }

    public void setPnWorkflowRules(Set pnWorkflowRules) {
        this.pnWorkflowRules = pnWorkflowRules;
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
        if ( !(other instanceof PnWorkflowTransition) ) return false;
        PnWorkflowTransition castOther = (PnWorkflowTransition) other;
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
