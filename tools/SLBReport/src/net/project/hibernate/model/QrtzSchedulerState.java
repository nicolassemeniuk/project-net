package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzSchedulerState implements Serializable {

    /** identifier field */
    private String instanceName;

    /** persistent field */
    private long lastCheckinTime;

    /** persistent field */
    private long checkinInterval;

    /** nullable persistent field */
    private String recoverer;

    /** full constructor */
    public QrtzSchedulerState(String instanceName, long lastCheckinTime, long checkinInterval, String recoverer) {
        this.instanceName = instanceName;
        this.lastCheckinTime = lastCheckinTime;
        this.checkinInterval = checkinInterval;
        this.recoverer = recoverer;
    }

    /** default constructor */
    public QrtzSchedulerState() {
    }

    /** minimal constructor */
    public QrtzSchedulerState(String instanceName, long lastCheckinTime, long checkinInterval) {
        this.instanceName = instanceName;
        this.lastCheckinTime = lastCheckinTime;
        this.checkinInterval = checkinInterval;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public long getLastCheckinTime() {
        return this.lastCheckinTime;
    }

    public void setLastCheckinTime(long lastCheckinTime) {
        this.lastCheckinTime = lastCheckinTime;
    }

    public long getCheckinInterval() {
        return this.checkinInterval;
    }

    public void setCheckinInterval(long checkinInterval) {
        this.checkinInterval = checkinInterval;
    }

    public String getRecoverer() {
        return this.recoverer;
    }

    public void setRecoverer(String recoverer) {
        this.recoverer = recoverer;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("instanceName", getInstanceName())
            .toString();
    }

}
