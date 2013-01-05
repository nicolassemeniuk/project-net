package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzCronTrigger implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.QrtzCronTriggerPK comp_id;

    /** persistent field */
    private String cronExpression;

    /** nullable persistent field */
    private String timeZoneId;

    /** nullable persistent field */
    private net.project.hibernate.model.QrtzTrigger qrtzTrigger;

    /** full constructor */
    public QrtzCronTrigger(net.project.hibernate.model.QrtzCronTriggerPK comp_id, String cronExpression, String timeZoneId, net.project.hibernate.model.QrtzTrigger qrtzTrigger) {
        this.comp_id = comp_id;
        this.cronExpression = cronExpression;
        this.timeZoneId = timeZoneId;
        this.qrtzTrigger = qrtzTrigger;
    }

    /** default constructor */
    public QrtzCronTrigger() {
    }

    /** minimal constructor */
    public QrtzCronTrigger(net.project.hibernate.model.QrtzCronTriggerPK comp_id, String cronExpression) {
        this.comp_id = comp_id;
        this.cronExpression = cronExpression;
    }

    public net.project.hibernate.model.QrtzCronTriggerPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.QrtzCronTriggerPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getCronExpression() {
        return this.cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getTimeZoneId() {
        return this.timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public net.project.hibernate.model.QrtzTrigger getQrtzTrigger() {
        return this.qrtzTrigger;
    }

    public void setQrtzTrigger(net.project.hibernate.model.QrtzTrigger qrtzTrigger) {
        this.qrtzTrigger = qrtzTrigger;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof QrtzCronTrigger) ) return false;
        QrtzCronTrigger castOther = (QrtzCronTrigger) other;
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
