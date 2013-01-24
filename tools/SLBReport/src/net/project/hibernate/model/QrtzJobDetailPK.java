package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzJobDetailPK implements Serializable {

    /** identifier field */
    private String jobName;

    /** identifier field */
    private String jobGroup;

    /** full constructor */
    public QrtzJobDetailPK(String jobName, String jobGroup) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
    }

    /** default constructor */
    public QrtzJobDetailPK() {
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

    public String toString() {
        return new ToStringBuilder(this)
            .append("jobName", getJobName())
            .append("jobGroup", getJobGroup())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof QrtzJobDetailPK) ) return false;
        QrtzJobDetailPK castOther = (QrtzJobDetailPK) other;
        return new EqualsBuilder()
            .append(this.getJobName(), castOther.getJobName())
            .append(this.getJobGroup(), castOther.getJobGroup())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getJobName())
            .append(getJobGroup())
            .toHashCode();
    }

}
