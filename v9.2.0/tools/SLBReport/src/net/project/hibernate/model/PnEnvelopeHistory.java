package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnEnvelopeHistory implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnEnvelopeHistoryPK comp_id;

    /** persistent field */
    private BigDecimal actionById;

    /** persistent field */
    private Date actionDatetime;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnWorkflowEnvelope pnWorkflowEnvelope;

    /** persistent field */
    private net.project.hibernate.model.PnEnvelopeHistoryAction pnEnvelopeHistoryAction;

    /** persistent field */
    private net.project.hibernate.model.PnEnvelopeHistoryClob pnEnvelopeHistoryClob;

    /** full constructor */
    public PnEnvelopeHistory(net.project.hibernate.model.PnEnvelopeHistoryPK comp_id, BigDecimal actionById, Date actionDatetime, Date crc, String recordStatus, net.project.hibernate.model.PnWorkflowEnvelope pnWorkflowEnvelope, net.project.hibernate.model.PnEnvelopeHistoryAction pnEnvelopeHistoryAction, net.project.hibernate.model.PnEnvelopeHistoryClob pnEnvelopeHistoryClob) {
        this.comp_id = comp_id;
        this.actionById = actionById;
        this.actionDatetime = actionDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflowEnvelope = pnWorkflowEnvelope;
        this.pnEnvelopeHistoryAction = pnEnvelopeHistoryAction;
        this.pnEnvelopeHistoryClob = pnEnvelopeHistoryClob;
    }

    /** default constructor */
    public PnEnvelopeHistory() {
    }

    /** minimal constructor */
    public PnEnvelopeHistory(net.project.hibernate.model.PnEnvelopeHistoryPK comp_id, BigDecimal actionById, Date actionDatetime, Date crc, String recordStatus, net.project.hibernate.model.PnEnvelopeHistoryAction pnEnvelopeHistoryAction, net.project.hibernate.model.PnEnvelopeHistoryClob pnEnvelopeHistoryClob) {
        this.comp_id = comp_id;
        this.actionById = actionById;
        this.actionDatetime = actionDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnEnvelopeHistoryAction = pnEnvelopeHistoryAction;
        this.pnEnvelopeHistoryClob = pnEnvelopeHistoryClob;
    }

    public net.project.hibernate.model.PnEnvelopeHistoryPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnEnvelopeHistoryPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getActionById() {
        return this.actionById;
    }

    public void setActionById(BigDecimal actionById) {
        this.actionById = actionById;
    }

    public Date getActionDatetime() {
        return this.actionDatetime;
    }

    public void setActionDatetime(Date actionDatetime) {
        this.actionDatetime = actionDatetime;
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

    public net.project.hibernate.model.PnWorkflowEnvelope getPnWorkflowEnvelope() {
        return this.pnWorkflowEnvelope;
    }

    public void setPnWorkflowEnvelope(net.project.hibernate.model.PnWorkflowEnvelope pnWorkflowEnvelope) {
        this.pnWorkflowEnvelope = pnWorkflowEnvelope;
    }

    public net.project.hibernate.model.PnEnvelopeHistoryAction getPnEnvelopeHistoryAction() {
        return this.pnEnvelopeHistoryAction;
    }

    public void setPnEnvelopeHistoryAction(net.project.hibernate.model.PnEnvelopeHistoryAction pnEnvelopeHistoryAction) {
        this.pnEnvelopeHistoryAction = pnEnvelopeHistoryAction;
    }

    public net.project.hibernate.model.PnEnvelopeHistoryClob getPnEnvelopeHistoryClob() {
        return this.pnEnvelopeHistoryClob;
    }

    public void setPnEnvelopeHistoryClob(net.project.hibernate.model.PnEnvelopeHistoryClob pnEnvelopeHistoryClob) {
        this.pnEnvelopeHistoryClob = pnEnvelopeHistoryClob;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnEnvelopeHistory) ) return false;
        PnEnvelopeHistory castOther = (PnEnvelopeHistory) other;
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
