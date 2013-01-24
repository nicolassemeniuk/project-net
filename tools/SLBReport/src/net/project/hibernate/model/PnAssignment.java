package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnAssignment implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnAssignmentPK comp_id;

    /** persistent field */
    private int statusId;

    /** nullable persistent field */
    private BigDecimal percentAllocated;

    /** nullable persistent field */
    private String role;

    /** persistent field */
    private int isPrimaryOwner;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Date startDate;

    /** nullable persistent field */
    private Date endDate;

    /** nullable persistent field */
    private BigDecimal work;

    /** nullable persistent field */
    private BigDecimal workUnits;

    /** nullable persistent field */
    private BigDecimal workComplete;

    /** nullable persistent field */
    private BigDecimal workCompleteUnits;

    /** nullable persistent field */
    private Date dateCreated;

    /** nullable persistent field */
    private Date modifiedDate;

    /** nullable persistent field */
    private BigDecimal modifiedBy;

    /** nullable persistent field */
    private BigDecimal isComplete;

    /** nullable persistent field */
    private BigDecimal percentComplete;

    /** nullable persistent field */
    private Date actualStart;

    /** nullable persistent field */
    private Date actualFinish;

    /** nullable persistent field */
    private Date estimatedFinish;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** nullable persistent field */
    private net.project.hibernate.model.PnSpaceHasPerson pnSpaceHasPerson;

    /** full constructor */
    public PnAssignment(net.project.hibernate.model.PnAssignmentPK comp_id, int statusId, BigDecimal percentAllocated, String role, int isPrimaryOwner, String recordStatus, Date startDate, Date endDate, BigDecimal work, BigDecimal workUnits, BigDecimal workComplete, BigDecimal workCompleteUnits, Date dateCreated, Date modifiedDate, BigDecimal modifiedBy, BigDecimal isComplete, BigDecimal percentComplete, Date actualStart, Date actualFinish, Date estimatedFinish, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnSpaceHasPerson pnSpaceHasPerson) {
        this.comp_id = comp_id;
        this.statusId = statusId;
        this.percentAllocated = percentAllocated;
        this.role = role;
        this.isPrimaryOwner = isPrimaryOwner;
        this.recordStatus = recordStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.work = work;
        this.workUnits = workUnits;
        this.workComplete = workComplete;
        this.workCompleteUnits = workCompleteUnits;
        this.dateCreated = dateCreated;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
        this.isComplete = isComplete;
        this.percentComplete = percentComplete;
        this.actualStart = actualStart;
        this.actualFinish = actualFinish;
        this.estimatedFinish = estimatedFinish;
        this.pnObject = pnObject;
        this.pnSpaceHasPerson = pnSpaceHasPerson;
    }

    /** default constructor */
    public PnAssignment() {
    }

    /** minimal constructor */
    public PnAssignment(net.project.hibernate.model.PnAssignmentPK comp_id, int statusId, int isPrimaryOwner, String recordStatus) {
        this.comp_id = comp_id;
        this.statusId = statusId;
        this.isPrimaryOwner = isPrimaryOwner;
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnAssignmentPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnAssignmentPK comp_id) {
        this.comp_id = comp_id;
    }

    public int getStatusId() {
        return this.statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public BigDecimal getPercentAllocated() {
        return this.percentAllocated;
    }

    public void setPercentAllocated(BigDecimal percentAllocated) {
        this.percentAllocated = percentAllocated;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getIsPrimaryOwner() {
        return this.isPrimaryOwner;
    }

    public void setIsPrimaryOwner(int isPrimaryOwner) {
        this.isPrimaryOwner = isPrimaryOwner;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getWork() {
        return this.work;
    }

    public void setWork(BigDecimal work) {
        this.work = work;
    }

    public BigDecimal getWorkUnits() {
        return this.workUnits;
    }

    public void setWorkUnits(BigDecimal workUnits) {
        this.workUnits = workUnits;
    }

    public BigDecimal getWorkComplete() {
        return this.workComplete;
    }

    public void setWorkComplete(BigDecimal workComplete) {
        this.workComplete = workComplete;
    }

    public BigDecimal getWorkCompleteUnits() {
        return this.workCompleteUnits;
    }

    public void setWorkCompleteUnits(BigDecimal workCompleteUnits) {
        this.workCompleteUnits = workCompleteUnits;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public BigDecimal getModifiedBy() {
        return this.modifiedBy;
    }

    public void setModifiedBy(BigDecimal modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public BigDecimal getIsComplete() {
        return this.isComplete;
    }

    public void setIsComplete(BigDecimal isComplete) {
        this.isComplete = isComplete;
    }

    public BigDecimal getPercentComplete() {
        return this.percentComplete;
    }

    public void setPercentComplete(BigDecimal percentComplete) {
        this.percentComplete = percentComplete;
    }

    public Date getActualStart() {
        return this.actualStart;
    }

    public void setActualStart(Date actualStart) {
        this.actualStart = actualStart;
    }

    public Date getActualFinish() {
        return this.actualFinish;
    }

    public void setActualFinish(Date actualFinish) {
        this.actualFinish = actualFinish;
    }

    public Date getEstimatedFinish() {
        return this.estimatedFinish;
    }

    public void setEstimatedFinish(Date estimatedFinish) {
        this.estimatedFinish = estimatedFinish;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public net.project.hibernate.model.PnSpaceHasPerson getPnSpaceHasPerson() {
        return this.pnSpaceHasPerson;
    }

    public void setPnSpaceHasPerson(net.project.hibernate.model.PnSpaceHasPerson pnSpaceHasPerson) {
        this.pnSpaceHasPerson = pnSpaceHasPerson;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnAssignment) ) return false;
        PnAssignment castOther = (PnAssignment) other;
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
