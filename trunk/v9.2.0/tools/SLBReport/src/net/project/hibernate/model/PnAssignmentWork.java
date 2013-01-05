package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** 
 *        Contains history of all work that users have logged against a task.
 *     
*/
public class PnAssignmentWork implements Serializable {

    /** identifier field */
    private BigDecimal assignmentWorkId;

    /** persistent field */
    private BigDecimal objectId;

    /** persistent field */
    private BigDecimal personId;

    /** nullable persistent field */
    private Date workStart;

    /** nullable persistent field */
    private Date workEnd;

    /** nullable persistent field */
    private BigDecimal work;

    /** nullable persistent field */
    private BigDecimal workUnits;

    /** nullable persistent field */
    private BigDecimal workRemaining;

    /** nullable persistent field */
    private BigDecimal workRemainingUnits;

    /** nullable persistent field */
    private BigDecimal percentComplete;

    /** nullable persistent field */
    private Date logDate;

    /** nullable persistent field */
    private BigDecimal modifiedBy;

    /** nullable persistent field */
    private BigDecimal scheduledWork;

    /** nullable persistent field */
    private BigDecimal scheduledWorkUnits;

    /** full constructor */
    public PnAssignmentWork(BigDecimal assignmentWorkId, BigDecimal objectId, BigDecimal personId, Date workStart, Date workEnd, BigDecimal work, BigDecimal workUnits, BigDecimal workRemaining, BigDecimal workRemainingUnits, BigDecimal percentComplete, Date logDate, BigDecimal modifiedBy, BigDecimal scheduledWork, BigDecimal scheduledWorkUnits) {
        this.assignmentWorkId = assignmentWorkId;
        this.objectId = objectId;
        this.personId = personId;
        this.workStart = workStart;
        this.workEnd = workEnd;
        this.work = work;
        this.workUnits = workUnits;
        this.workRemaining = workRemaining;
        this.workRemainingUnits = workRemainingUnits;
        this.percentComplete = percentComplete;
        this.logDate = logDate;
        this.modifiedBy = modifiedBy;
        this.scheduledWork = scheduledWork;
        this.scheduledWorkUnits = scheduledWorkUnits;
    }

    /** default constructor */
    public PnAssignmentWork() {
    }

    /** minimal constructor */
    public PnAssignmentWork(BigDecimal assignmentWorkId, BigDecimal objectId, BigDecimal personId) {
        this.assignmentWorkId = assignmentWorkId;
        this.objectId = objectId;
        this.personId = personId;
    }

    public BigDecimal getAssignmentWorkId() {
        return this.assignmentWorkId;
    }

    public void setAssignmentWorkId(BigDecimal assignmentWorkId) {
        this.assignmentWorkId = assignmentWorkId;
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public Date getWorkStart() {
        return this.workStart;
    }

    public void setWorkStart(Date workStart) {
        this.workStart = workStart;
    }

    public Date getWorkEnd() {
        return this.workEnd;
    }

    public void setWorkEnd(Date workEnd) {
        this.workEnd = workEnd;
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

    public BigDecimal getWorkRemaining() {
        return this.workRemaining;
    }

    public void setWorkRemaining(BigDecimal workRemaining) {
        this.workRemaining = workRemaining;
    }

    public BigDecimal getWorkRemainingUnits() {
        return this.workRemainingUnits;
    }

    public void setWorkRemainingUnits(BigDecimal workRemainingUnits) {
        this.workRemainingUnits = workRemainingUnits;
    }

    public BigDecimal getPercentComplete() {
        return this.percentComplete;
    }

    public void setPercentComplete(BigDecimal percentComplete) {
        this.percentComplete = percentComplete;
    }

    public Date getLogDate() {
        return this.logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public BigDecimal getModifiedBy() {
        return this.modifiedBy;
    }

    public void setModifiedBy(BigDecimal modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public BigDecimal getScheduledWork() {
        return this.scheduledWork;
    }

    public void setScheduledWork(BigDecimal scheduledWork) {
        this.scheduledWork = scheduledWork;
    }

    public BigDecimal getScheduledWorkUnits() {
        return this.scheduledWorkUnits;
    }

    public void setScheduledWorkUnits(BigDecimal scheduledWorkUnits) {
        this.scheduledWorkUnits = scheduledWorkUnits;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("assignmentWorkId", getAssignmentWorkId())
            .toString();
    }

}
