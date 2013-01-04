package net.project.hibernate.model;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzJobDetail implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.QrtzJobDetailPK comp_id;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private String jobClassName;

    /** persistent field */
    private String isDurable;

    /** persistent field */
    private String isVolatile;

    /** persistent field */
    private String isStateful;

    /** persistent field */
    private String requestsRecovery;

    /** nullable persistent field */
    private Blob jobData;

    /** persistent field */
    private Set qrtzJobListeners;

    /** persistent field */
    private Set qrtzTriggers;

    /** full constructor */
    public QrtzJobDetail(net.project.hibernate.model.QrtzJobDetailPK comp_id, String description, String jobClassName, String isDurable, String isVolatile, String isStateful, String requestsRecovery, Blob jobData, Set qrtzJobListeners, Set qrtzTriggers) {
        this.comp_id = comp_id;
        this.description = description;
        this.jobClassName = jobClassName;
        this.isDurable = isDurable;
        this.isVolatile = isVolatile;
        this.isStateful = isStateful;
        this.requestsRecovery = requestsRecovery;
        this.jobData = jobData;
        this.qrtzJobListeners = qrtzJobListeners;
        this.qrtzTriggers = qrtzTriggers;
    }

    /** default constructor */
    public QrtzJobDetail() {
    }

    /** minimal constructor */
    public QrtzJobDetail(net.project.hibernate.model.QrtzJobDetailPK comp_id, String jobClassName, String isDurable, String isVolatile, String isStateful, String requestsRecovery, Set qrtzJobListeners, Set qrtzTriggers) {
        this.comp_id = comp_id;
        this.jobClassName = jobClassName;
        this.isDurable = isDurable;
        this.isVolatile = isVolatile;
        this.isStateful = isStateful;
        this.requestsRecovery = requestsRecovery;
        this.qrtzJobListeners = qrtzJobListeners;
        this.qrtzTriggers = qrtzTriggers;
    }

    public net.project.hibernate.model.QrtzJobDetailPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.QrtzJobDetailPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJobClassName() {
        return this.jobClassName;
    }

    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
    }

    public String getIsDurable() {
        return this.isDurable;
    }

    public void setIsDurable(String isDurable) {
        this.isDurable = isDurable;
    }

    public String getIsVolatile() {
        return this.isVolatile;
    }

    public void setIsVolatile(String isVolatile) {
        this.isVolatile = isVolatile;
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

    public Blob getJobData() {
        return this.jobData;
    }

    public void setJobData(Blob jobData) {
        this.jobData = jobData;
    }

    public Set getQrtzJobListeners() {
        return this.qrtzJobListeners;
    }

    public void setQrtzJobListeners(Set qrtzJobListeners) {
        this.qrtzJobListeners = qrtzJobListeners;
    }

    public Set getQrtzTriggers() {
        return this.qrtzTriggers;
    }

    public void setQrtzTriggers(Set qrtzTriggers) {
        this.qrtzTriggers = qrtzTriggers;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof QrtzJobDetail) ) return false;
        QrtzJobDetail castOther = (QrtzJobDetail) other;
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
