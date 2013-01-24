package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTaskBaseline implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnTaskBaselinePK comp_id;

    /** nullable persistent field */
    private String taskName;

    /** persistent field */
    private BigDecimal createdBy;

    /** nullable persistent field */
    private Date baselineSetDate;

    /** nullable persistent field */
    private String taskType;

    /** nullable persistent field */
    private Long taskDuration;

    /** nullable persistent field */
    private Integer priority;

    /** nullable persistent field */
    private BigDecimal taskWork;

    /** nullable persistent field */
    private BigDecimal statusId;

    /** nullable persistent field */
    private String workUnits;

    /** nullable persistent field */
    private BigDecimal taskWorkComplete;

    /** nullable persistent field */
    private Date dateStart;

    /** nullable persistent field */
    private BigDecimal durationUnits;

    /** nullable persistent field */
    private Date dateFinish;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private BigDecimal workCompleteUnits;

    /** nullable persistent field */
    private BigDecimal parentTaskId;

    /** nullable persistent field */
    private Integer criticalPath;

    /** nullable persistent field */
    private net.project.hibernate.model.PnTask pnTask;

    /** full constructor */
    public PnTaskBaseline(net.project.hibernate.model.PnTaskBaselinePK comp_id, String taskName, BigDecimal createdBy, Date baselineSetDate, String taskType, Long taskDuration, Integer priority, BigDecimal taskWork, BigDecimal statusId, String workUnits, BigDecimal taskWorkComplete, Date dateStart, BigDecimal durationUnits, Date dateFinish, String recordStatus, BigDecimal workCompleteUnits, BigDecimal parentTaskId, Integer criticalPath, net.project.hibernate.model.PnTask pnTask) {
        this.comp_id = comp_id;
        this.taskName = taskName;
        this.createdBy = createdBy;
        this.baselineSetDate = baselineSetDate;
        this.taskType = taskType;
        this.taskDuration = taskDuration;
        this.priority = priority;
        this.taskWork = taskWork;
        this.statusId = statusId;
        this.workUnits = workUnits;
        this.taskWorkComplete = taskWorkComplete;
        this.dateStart = dateStart;
        this.durationUnits = durationUnits;
        this.dateFinish = dateFinish;
        this.recordStatus = recordStatus;
        this.workCompleteUnits = workCompleteUnits;
        this.parentTaskId = parentTaskId;
        this.criticalPath = criticalPath;
        this.pnTask = pnTask;
    }

    /** default constructor */
    public PnTaskBaseline() {
    }

    /** minimal constructor */
    public PnTaskBaseline(net.project.hibernate.model.PnTaskBaselinePK comp_id, BigDecimal createdBy, String recordStatus) {
        this.comp_id = comp_id;
        this.createdBy = createdBy;
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnTaskBaselinePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnTaskBaselinePK comp_id) {
        this.comp_id = comp_id;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public BigDecimal getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(BigDecimal createdBy) {
        this.createdBy = createdBy;
    }

    public Date getBaselineSetDate() {
        return this.baselineSetDate;
    }

    public void setBaselineSetDate(Date baselineSetDate) {
        this.baselineSetDate = baselineSetDate;
    }

    public String getTaskType() {
        return this.taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Long getTaskDuration() {
        return this.taskDuration;
    }

    public void setTaskDuration(Long taskDuration) {
        this.taskDuration = taskDuration;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public BigDecimal getTaskWork() {
        return this.taskWork;
    }

    public void setTaskWork(BigDecimal taskWork) {
        this.taskWork = taskWork;
    }

    public BigDecimal getStatusId() {
        return this.statusId;
    }

    public void setStatusId(BigDecimal statusId) {
        this.statusId = statusId;
    }

    public String getWorkUnits() {
        return this.workUnits;
    }

    public void setWorkUnits(String workUnits) {
        this.workUnits = workUnits;
    }

    public BigDecimal getTaskWorkComplete() {
        return this.taskWorkComplete;
    }

    public void setTaskWorkComplete(BigDecimal taskWorkComplete) {
        this.taskWorkComplete = taskWorkComplete;
    }

    public Date getDateStart() {
        return this.dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public BigDecimal getDurationUnits() {
        return this.durationUnits;
    }

    public void setDurationUnits(BigDecimal durationUnits) {
        this.durationUnits = durationUnits;
    }

    public Date getDateFinish() {
        return this.dateFinish;
    }

    public void setDateFinish(Date dateFinish) {
        this.dateFinish = dateFinish;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public BigDecimal getWorkCompleteUnits() {
        return this.workCompleteUnits;
    }

    public void setWorkCompleteUnits(BigDecimal workCompleteUnits) {
        this.workCompleteUnits = workCompleteUnits;
    }

    public BigDecimal getParentTaskId() {
        return this.parentTaskId;
    }

    public void setParentTaskId(BigDecimal parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public Integer getCriticalPath() {
        return this.criticalPath;
    }

    public void setCriticalPath(Integer criticalPath) {
        this.criticalPath = criticalPath;
    }

    public net.project.hibernate.model.PnTask getPnTask() {
        return this.pnTask;
    }

    public void setPnTask(net.project.hibernate.model.PnTask pnTask) {
        this.pnTask = pnTask;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnTaskBaseline) ) return false;
        PnTaskBaseline castOther = (PnTaskBaseline) other;
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
