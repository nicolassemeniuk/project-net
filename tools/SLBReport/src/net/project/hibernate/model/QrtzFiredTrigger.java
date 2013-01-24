package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzFiredTrigger implements Serializable {

    /** identifier field */
    private String entryId;

    /** persistent field */
    private String triggerName;

    /** persistent field */
    private String triggerGroup;

    /** persistent field */
    private String isVolatile;

    /** persistent field */
    private String instanceName;

    /** persistent field */
    private long firedTime;

    /** persistent field */
    private String state;

    /** nullable persistent field */
    private String jobName;

    /** nullable persistent field */
    private String jobGroup;

    /** nullable persistent field */
    private String isStateful;

    /** nullable persistent field */
    private String requestsRecovery;

    /** full constructor */
    public QrtzFiredTrigger(String entryId, String triggerName, String triggerGroup, String isVolatile, String instanceName, long firedTime, String state, String jobName, String jobGroup, String isStateful, String requestsRecovery) {
        this.entryId = entryId;
        this.triggerName = triggerName;
        this.triggerGroup = triggerGroup;
        this.isVolatile = isVolatile;
        this.instanceName = instanceName;
        this.firedTime = firedTime;
        this.state = state;
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.isStateful = isStateful;
        this.requestsRecovery = requestsRecovery;
    }

    /** default constructor */
    public QrtzFiredTrigger() {
    }

    /** minimal constructor */
    public QrtzFiredTrigger(String entryId, String triggerName, String triggerGroup, String isVolatile, String instanceName, long firedTime, String state) {
        this.entryId = entryId;
        this.triggerName = triggerName;
        this.triggerGroup = triggerGroup;
        this.isVolatile = isVolatile;
        this.instanceName = instanceName;
        this.firedTime = firedTime;
        this.state = state;
    }

    public String getEntryId() {
        return this.entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getTriggerName() {
        return this.triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerGroup() {
        return this.triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    public String getIsVolatile() {
        return this.isVolatile;
    }

    public void setIsVolatile(String isVolatile) {
        this.isVolatile = isVolatile;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public long getFiredTime() {
        return this.firedTime;
    }

    public void setFiredTime(long firedTime) {
        this.firedTime = firedTime;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getJobName() {
        return this.jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return this.jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getIsStateful() {
        return this.isStateful;
    }

    public void setIsStateful(String isStateful) {
        this.isStateful = isStateful;
    }

    public String getRequestsRecovery() {
        return this.requestsRecovery;
    }

    public void setRequestsRecovery(String requestsRecovery) {
        this.requestsRecovery = requestsRecovery;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("entryId", getEntryId())
            .toString();
    }

}
