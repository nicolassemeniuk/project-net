package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWorkflowStrictness implements Serializable {

    /** identifier field */
    private BigDecimal strictnessId;

    /** nullable persistent field */
    private String strictnessName;

    /** nullable persistent field */
    private String strictnessDescription;

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
    private Set pnWorkflowEnvelopes;

    /** persistent field */
    private Set pnWorkflows;

    /** full constructor */
    public PnWorkflowStrictness(BigDecimal strictnessId, String strictnessName, String strictnessDescription, BigDecimal createdById, Date createdDatetime, BigDecimal modifiedById, Date modifiedDatetime, Date crc, String recordStatus, Set pnWorkflowEnvelopes, Set pnWorkflows) {
        this.strictnessId = strictnessId;
        this.strictnessName = strictnessName;
        this.strictnessDescription = strictnessDescription;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.modifiedById = modifiedById;
        this.modifiedDatetime = modifiedDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflowEnvelopes = pnWorkflowEnvelopes;
        this.pnWorkflows = pnWorkflows;
    }

    /** default constructor */
    public PnWorkflowStrictness() {
    }

    /** minimal constructor */
    public PnWorkflowStrictness(BigDecimal strictnessId, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus, Set pnWorkflowEnvelopes, Set pnWorkflows) {
        this.strictnessId = strictnessId;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflowEnvelopes = pnWorkflowEnvelopes;
        this.pnWorkflows = pnWorkflows;
    }

    public BigDecimal getStrictnessId() {
        return this.strictnessId;
    }

    public void setStrictnessId(BigDecimal strictnessId) {
        this.strictnessId = strictnessId;
    }

    public String getStrictnessName() {
        return this.strictnessName;
    }

    public void setStrictnessName(String strictnessName) {
        this.strictnessName = strictnessName;
    }

    public String getStrictnessDescription() {
        return this.strictnessDescription;
    }

    public void setStrictnessDescription(String strictnessDescription) {
        this.strictnessDescription = strictnessDescription;
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

    public Set getPnWorkflowEnvelopes() {
        return this.pnWorkflowEnvelopes;
    }

    public void setPnWorkflowEnvelopes(Set pnWorkflowEnvelopes) {
        this.pnWorkflowEnvelopes = pnWorkflowEnvelopes;
    }

    public Set getPnWorkflows() {
        return this.pnWorkflows;
    }

    public void setPnWorkflows(Set pnWorkflows) {
        this.pnWorkflows = pnWorkflows;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("strictnessId", getStrictnessId())
            .toString();
    }

}
