package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzSimpleTrigger implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.QrtzSimpleTriggerPK comp_id;

    /** persistent field */
    private int repeatCount;

    /** persistent field */
    private long repeatInterval;

    /** persistent field */
    private int timesTriggered;

    /** nullable persistent field */
    private net.project.hibernate.model.QrtzTrigger qrtzTrigger;

    /** full constructor */
    public QrtzSimpleTrigger(net.project.hibernate.model.QrtzSimpleTriggerPK comp_id, int repeatCount, long repeatInterval, int timesTriggered, net.project.hibernate.model.QrtzTrigger qrtzTrigger) {
        this.comp_id = comp_id;
        this.repeatCount = repeatCount;
        this.repeatInterval = repeatInterval;
        this.timesTriggered = timesTriggered;
        this.qrtzTrigger = qrtzTrigger;
    }

    /** default constructor */
    public QrtzSimpleTrigger() {
    }

    /** minimal constructor */
    public QrtzSimpleTrigger(net.project.hibernate.model.QrtzSimpleTriggerPK comp_id, int repeatCount, long repeatInterval, int timesTriggered) {
        this.comp_id = comp_id;
        this.repeatCount = repeatCount;
        this.repeatInterval = repeatInterval;
        this.timesTriggered = timesTriggered;
    }

    public net.project.hibernate.model.QrtzSimpleTriggerPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.QrtzSimpleTriggerPK comp_id) {
        this.comp_id = comp_id;
    }

    public int getRepeatCount() {
        return this.repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public long getRepeatInterval() {
        return this.repeatInterval;
    }

    public void setRepeatInterval(long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public int getTimesTriggered() {
        return this.timesTriggered;
    }

    public void setTimesTriggered(int timesTriggered) {
        this.timesTriggered = timesTriggered;
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
        if ( !(other instanceof QrtzSimpleTrigger) ) return false;
        QrtzSimpleTrigger castOther = (QrtzSimpleTrigger) other;
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
