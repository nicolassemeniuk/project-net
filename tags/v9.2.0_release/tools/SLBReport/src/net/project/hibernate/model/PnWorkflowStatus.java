package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowStatus implements Serializable {

    /** identifier field */
    private BigDecimal statusId;

    /** nullable persistent field */
    private String statusName;

    /** nullable persistent field */
    private String statusDescription;

    /** persistent field */
    private int isInactive;

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
    private Set pnEnvelopeVersions;

    /** full constructor */
    public PnWorkflowStatus(BigDecimal statusId, String statusName, String statusDescription, int isInactive, BigDecimal createdById, Date createdDatetime, BigDecimal modifiedById, Date modifiedDatetime, Date crc, String recordStatus, Set pnEnvelopeVersions) {
        this.statusId = statusId;
        this.statusName = statusName;
        this.statusDescription = statusDescription;
        this.isInactive = isInactive;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.modifiedById = modifiedById;
        this.modifiedDatetime = modifiedDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnEnvelopeVersions = pnEnvelopeVersions;
    }

    /** default constructor */
    public PnWorkflowStatus() {
    }

    /** minimal constructor */
    public PnWorkflowStatus(BigDecimal statusId, int isInactive, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus, Set pnEnvelopeVersions) {
        this.statusId = statusId;
        this.isInactive = isInactive;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnEnvelopeVersions = pnEnvelopeVersions;
    }

    public BigDecimal getStatusId() {
        return this.statusId;
    }

    public void setStatusId(BigDecimal statusId) {
        this.statusId = statusId;
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

    public int getIsInactive() {
        return this.isInactive;
    }

    public void setIsInactive(int isInactive) {
        this.isInactive = isInactive;
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

    public Set getPnEnvelopeVersions() {
        return this.pnEnvelopeVersions;
    }

    public void setPnEnvelopeVersions(Set pnEnvelopeVersions) {
        this.pnEnvelopeVersions = pnEnvelopeVersions;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("statusId", getStatusId())
            .toString();
    }

}
