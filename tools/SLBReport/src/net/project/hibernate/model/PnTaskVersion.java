package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTaskVersion implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnTaskVersionPK comp_id;

    /** persistent field */
    private String taskName;

    /** nullable persistent field */
    private String taskDesc;

    /** nullable persistent field */
    private String taskType;

    /** nullable persistent field */
    private BigDecimal duration;

    /** nullable persistent field */
    private BigDecimal work;

    /** nullable persistent field */
    private BigDecimal workUnits;

    /** nullable persistent field */
    private BigDecimal workComplete;

    /** nullable persistent field */
    private Date dateStart;

    /** nullable persistent field */
    private BigDecimal workCompleteUnits;

    /** nullable persistent field */
    private Date dateFinish;

    /** nullable persistent field */
    private Date actualStart;

    /** nullable persistent field */
    private Date actualFinish;

    /** nullable persistent field */
    private Integer priority;

    /** nullable persistent field */
    private Integer percentComplete;

    /** nullable persistent field */
    private Date dateCreated;

    /** nullable persistent field */
    private Date dateModified;

    /** nullable persistent field */
    private BigDecimal modifiedBy;

    /** nullable persistent field */
    private BigDecimal durationUnits;

    /** nullable persistent field */
    private BigDecimal parentTaskId;

    /** nullable persistent field */
    private BigDecimal parentTaskVersionId;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private BigDecimal criticalPath;

    /** nullable persistent field */
    private BigDecimal seq;

    /** nullable persistent field */
    private Integer ignoreTimesForDates;

    /** nullable persistent field */
    private Integer isMilestone;

    /** nullable persistent field */
    private Date earlyStart;

    /** nullable persistent field */
    private Date earlyFinish;

    /** nullable persistent field */
    private Date lateStart;

    /** nullable persistent field */
    private Date lateFinish;

    /** nullable persistent field */
    private BigDecimal workPercentComplete;

    /** nullable persistent field */
    private String constraintType;

    /** nullable persistent field */
    private Date constraintDate;

    /** nullable persistent field */
    private Date deadline;

    /** nullable persistent field */
    private BigDecimal baselineId;

    /** nullable persistent field */
    private BigDecimal planVersionId;

    /** nullable persistent field */
    private BigDecimal legacyFlag;

    /** nullable persistent field */
    private BigDecimal calculationTypeId;

    /** nullable persistent field */
    private BigDecimal unallocatedWorkComplete;

    /** nullable persistent field */
    private BigDecimal unallocatedWorkCompleteUnit;

    /** nullable persistent field */
    private BigDecimal workMs;

    /** nullable persistent field */
    private BigDecimal workCompleteMs;

    /** full constructor */
    public PnTaskVersion(net.project.hibernate.model.PnTaskVersionPK comp_id, String taskName, String taskDesc, String taskType, BigDecimal duration, BigDecimal work, BigDecimal workUnits, BigDecimal workComplete, Date dateStart, BigDecimal workCompleteUnits, Date dateFinish, Date actualStart, Date actualFinish, Integer priority, Integer percentComplete, Date dateCreated, Date dateModified, BigDecimal modifiedBy, BigDecimal durationUnits, BigDecimal parentTaskId, BigDecimal parentTaskVersionId, String recordStatus, BigDecimal criticalPath, BigDecimal seq, Integer ignoreTimesForDates, Integer isMilestone, Date earlyStart, Date earlyFinish, Date lateStart, Date lateFinish, BigDecimal workPercentComplete, String constraintType, Date constraintDate, Date deadline, BigDecimal baselineId, BigDecimal planVersionId, BigDecimal legacyFlag, BigDecimal calculationTypeId, BigDecimal unallocatedWorkComplete, BigDecimal unallocatedWorkCompleteUnit, BigDecimal workMs, BigDecimal workCompleteMs) {
        this.comp_id = comp_id;
        this.taskName = taskName;
        this.taskDesc = taskDesc;
        this.taskType = taskType;
        this.duration = duration;
        this.work = work;
        this.workUnits = workUnits;
        this.workComplete = workComplete;
        this.dateStart = dateStart;
        this.workCompleteUnits = workCompleteUnits;
        this.dateFinish = dateFinish;
        this.actualStart = actualStart;
        this.actualFinish = actualFinish;
        this.priority = priority;
        this.percentComplete = percentComplete;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.modifiedBy = modifiedBy;
        this.durationUnits = durationUnits;
        this.parentTaskId = parentTaskId;
        this.parentTaskVersionId = parentTaskVersionId;
        this.recordStatus = recordStatus;
        this.criticalPath = criticalPath;
        this.seq = seq;
        this.ignoreTimesForDates = ignoreTimesForDates;
        this.isMilestone = isMilestone;
        this.earlyStart = earlyStart;
        this.earlyFinish = earlyFinish;
        this.lateStart = lateStart;
        this.lateFinish = lateFinish;
        this.workPercentComplete = workPercentComplete;
        this.constraintType = constraintType;
        this.constraintDate = constraintDate;
        this.deadline = deadline;
        this.baselineId = baselineId;
        this.planVersionId = planVersionId;
        this.legacyFlag = legacyFlag;
        this.calculationTypeId = calculationTypeId;
        this.unallocatedWorkComplete = unallocatedWorkComplete;
        this.unallocatedWorkCompleteUnit = unallocatedWorkCompleteUnit;
        this.workMs = workMs;
        this.workCompleteMs = workCompleteMs;
    }

    /** default constructor */
    public PnTaskVersion() {
    }

    /** minimal constructor */
    public PnTaskVersion(net.project.hibernate.model.PnTaskVersionPK comp_id, String taskName, String recordStatus) {
        this.comp_id = comp_id;
        this.taskName = taskName;
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnTaskVersionPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnTaskVersionPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDesc() {
        return this.taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getTaskType() {
        return this.taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public BigDecimal getDuration() {
        return this.duration;
    }

    public void setDuration(BigDecimal duration) {
        this.duration = duration;
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

    public Date getDateStart() {
        return this.dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public BigDecimal getWorkCompleteUnits() {
        return this.workCompleteUnits;
    }

    public void setWorkCompleteUnits(BigDecimal workCompleteUnits) {
        this.workCompleteUnits = workCompleteUnits;
    }

    public Date getDateFinish() {
        return this.dateFinish;
    }

    public void setDateFinish(Date dateFinish) {
        this.dateFinish = dateFinish;
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

    public Integer getPriority() {
        return this.priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPercentComplete() {
        return this.percentComplete;
    }

    public void setPercentComplete(Integer percentComplete) {
        this.percentComplete = percentComplete;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return this.dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public BigDecimal getModifiedBy() {
        return this.modifiedBy;
    }

    public void setModifiedBy(BigDecimal modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public BigDecimal getDurationUnits() {
        return this.durationUnits;
    }

    public void setDurationUnits(BigDecimal durationUnits) {
        this.durationUnits = durationUnits;
    }

    public BigDecimal getParentTaskId() {
        return this.parentTaskId;
    }

    public void setParentTaskId(BigDecimal parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public BigDecimal getParentTaskVersionId() {
        return this.parentTaskVersionId;
    }

    public void setParentTaskVersionId(BigDecimal parentTaskVersionId) {
        this.parentTaskVersionId = parentTaskVersionId;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public BigDecimal getCriticalPath() {
        return this.criticalPath;
    }

    public void setCriticalPath(BigDecimal criticalPath) {
        this.criticalPath = criticalPath;
    }

    public BigDecimal getSeq() {
        return this.seq;
    }

    public void setSeq(BigDecimal seq) {
        this.seq = seq;
    }

    public Integer getIgnoreTimesForDates() {
        return this.ignoreTimesForDates;
    }

    public void setIgnoreTimesForDates(Integer ignoreTimesForDates) {
        this.ignoreTimesForDates = ignoreTimesForDates;
    }

    public Integer getIsMilestone() {
        return this.isMilestone;
    }

    public void setIsMilestone(Integer isMilestone) {
        this.isMilestone = isMilestone;
    }

    public Date getEarlyStart() {
        return this.earlyStart;
    }

    public void setEarlyStart(Date earlyStart) {
        this.earlyStart = earlyStart;
    }

    public Date getEarlyFinish() {
        return this.earlyFinish;
    }

    public void setEarlyFinish(Date earlyFinish) {
        this.earlyFinish = earlyFinish;
    }

    public Date getLateStart() {
        return this.lateStart;
    }

    public void setLateStart(Date lateStart) {
        this.lateStart = lateStart;
    }

    public Date getLateFinish() {
        return this.lateFinish;
    }

    public void setLateFinish(Date lateFinish) {
        this.lateFinish = lateFinish;
    }

    public BigDecimal getWorkPercentComplete() {
        return this.workPercentComplete;
    }

    public void setWorkPercentComplete(BigDecimal workPercentComplete) {
        this.workPercentComplete = workPercentComplete;
    }

    public String getConstraintType() {
        return this.constraintType;
    }

    public void setConstraintType(String constraintType) {
        this.constraintType = constraintType;
    }

    public Date getConstraintDate() {
        return this.constraintDate;
    }

    public void setConstraintDate(Date constraintDate) {
        this.constraintDate = constraintDate;
    }

    public Date getDeadline() {
        return this.deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public BigDecimal getBaselineId() {
        return this.baselineId;
    }

    public void setBaselineId(BigDecimal baselineId) {
        this.baselineId = baselineId;
    }

    public BigDecimal getPlanVersionId() {
        return this.planVersionId;
    }

    public void setPlanVersionId(BigDecimal planVersionId) {
        this.planVersionId = planVersionId;
    }

    public BigDecimal getLegacyFlag() {
        return this.legacyFlag;
    }

    public void setLegacyFlag(BigDecimal legacyFlag) {
        this.legacyFlag = legacyFlag;
    }

    public BigDecimal getCalculationTypeId() {
        return this.calculationTypeId;
    }

    public void setCalculationTypeId(BigDecimal calculationTypeId) {
        this.calculationTypeId = calculationTypeId;
    }

    public BigDecimal getUnallocatedWorkComplete() {
        return this.unallocatedWorkComplete;
    }

    public void setUnallocatedWorkComplete(BigDecimal unallocatedWorkComplete) {
        this.unallocatedWorkComplete = unallocatedWorkComplete;
    }

    public BigDecimal getUnallocatedWorkCompleteUnit() {
        return this.unallocatedWorkCompleteUnit;
    }

    public void setUnallocatedWorkCompleteUnit(BigDecimal unallocatedWorkCompleteUnit) {
        this.unallocatedWorkCompleteUnit = unallocatedWorkCompleteUnit;
    }

    public BigDecimal getWorkMs() {
        return this.workMs;
    }

    public void setWorkMs(BigDecimal workMs) {
        this.workMs = workMs;
    }

    public BigDecimal getWorkCompleteMs() {
        return this.workCompleteMs;
    }

    public void setWorkCompleteMs(BigDecimal workCompleteMs) {
        this.workCompleteMs = workCompleteMs;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnTaskVersion) ) return false;
        PnTaskVersion castOther = (PnTaskVersion) other;
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
