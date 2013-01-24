package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzJobListenerPK implements Serializable {

    /** identifier field */
    private String jobName;

    /** identifier field */
    private String jobGroup;

    /** identifier field */
    private String jobListener;

    /** full constructor */
    public QrtzJobListenerPK(String jobName, String jobGroup, String jobListener) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.jobListener = jobListener;
    }

    /** default constructor */
    public QrtzJobListenerPK() {
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

    public String getJobListener() {
        return this.jobListener;
    }

    public void setJobListener(String jobListener) {
        this.jobListener = jobListener;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("jobName", getJobName())
            .append("jobGroup", getJobGroup())
            .append("jobListener", getJobListener())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof QrtzJobListenerPK) ) return false;
        QrtzJobListenerPK castOther = (QrtzJobListenerPK) other;
        return new EqualsBuilder()
            .append(this.getJobName(), castOther.getJobName())
            .append(this.getJobGroup(), castOther.getJobGroup())
            .append(this.getJobListener(), castOther.getJobListener())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getJobName())
            .append(getJobGroup())
            .append(getJobListener())
            .toHashCode();
    }

}
