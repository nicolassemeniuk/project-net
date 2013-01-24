package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnEnvelopeHistoryAction implements Serializable {

    /** identifier field */
    private BigDecimal historyActionId;

    /** nullable persistent field */
    private String actionName;

    /** nullable persistent field */
    private String actionDescription;

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
    private Set pnEnvelopeHistories;

    /** full constructor */
    public PnEnvelopeHistoryAction(BigDecimal historyActionId, String actionName, String actionDescription, BigDecimal createdById, Date createdDatetime, BigDecimal modifiedById, Date modifiedDatetime, Date crc, String recordStatus, Set pnEnvelopeHistories) {
        this.historyActionId = historyActionId;
        this.actionName = actionName;
        this.actionDescription = actionDescription;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.modifiedById = modifiedById;
        this.modifiedDatetime = modifiedDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnEnvelopeHistories = pnEnvelopeHistories;
    }

    /** default constructor */
    public PnEnvelopeHistoryAction() {
    }

    /** minimal constructor */
    public PnEnvelopeHistoryAction(BigDecimal historyActionId, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus, Set pnEnvelopeHistories) {
        this.historyActionId = historyActionId;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnEnvelopeHistories = pnEnvelopeHistories;
    }

    public BigDecimal getHistoryActionId() {
        return this.historyActionId;
    }

    public void setHistoryActionId(BigDecimal historyActionId) {
        this.historyActionId = historyActionId;
    }

    public String getActionName() {
        return this.actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionDescription() {
        return this.actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
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

    public Set getPnEnvelopeHistories() {
        return this.pnEnvelopeHistories;
    }

    public void setPnEnvelopeHistories(Set pnEnvelopeHistories) {
        this.pnEnvelopeHistories = pnEnvelopeHistories;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("historyActionId", getHistoryActionId())
            .toString();
    }

}
