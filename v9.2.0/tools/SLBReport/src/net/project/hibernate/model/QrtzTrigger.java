package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzTrigger implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.QrtzTriggerPK comp_id;

    /** persistent field */
    private String isVolatile;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private Long nextFireTime;

    /** nullable persistent field */
    private Long prevFireTime;

    /** persistent field */
    private String triggerState;

    /** persistent field */
    private String triggerType;

    /** persistent field */
    private long startTime;

    /** nullable persistent field */
    private Long endTime;

    /** nullable persistent field */
    private String calendarName;

    /** nullable persistent field */
    private Integer misfireInstr;

    /** nullable persistent field */
    private net.project.hibernate.model.QrtzBlobTrigger qrtzBlobTrigger;

    /** nullable persistent field */
    private net.project.hibernate.model.QrtzSimpleTrigger qrtzSimpleTrigger;

    /** nullable persistent field */
    private net.project.hibernate.model.QrtzCronTrigger qrtzCronTrigger;

    /** persistent field */
    private net.project.hibernate.model.QrtzJobDetail qrtzJobDetail;

    /** persistent field */
    private Set qrtzTriggerListeners;

    /** full constructor */
    public QrtzTrigger(net.project.hibernate.model.QrtzTriggerPK comp_id, String isVolatile, String description, Long nextFireTime, Long prevFireTime, String triggerState, String triggerType, long startTime, Long endTime, String calendarName, Integer misfireInstr, net.project.hibernate.model.QrtzBlobTrigger qrtzBlobTrigger, net.project.hibernate.model.QrtzSimpleTrigger qrtzSimpleTrigger, net.project.hibernate.model.QrtzCronTrigger qrtzCronTrigger, net.project.hibernate.model.QrtzJobDetail qrtzJobDetail, Set qrtzTriggerListeners) {
        this.comp_id = comp_id;
        this.isVolatile = isVolatile;
        this.description = description;
        this.nextFireTime = nextFireTime;
        this.prevFireTime = prevFireTime;
        this.triggerState = triggerState;
        this.triggerType = triggerType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.calendarName = calendarName;
        this.misfireInstr = misfireInstr;
        this.qrtzBlobTrigger = qrtzBlobTrigger;
        this.qrtzSimpleTrigger = qrtzSimpleTrigger;
        this.qrtzCronTrigger = qrtzCronTrigger;
        this.qrtzJobDetail = qrtzJobDetail;
        this.qrtzTriggerListeners = qrtzTriggerListeners;
    }

    /** default constructor */
    public QrtzTrigger() {
    }

    /** minimal constructor */
    public QrtzTrigger(net.project.hibernate.model.QrtzTriggerPK comp_id, String isVolatile, String triggerState, String triggerType, long startTime, net.project.hibernate.model.QrtzJobDetail qrtzJobDetail, Set qrtzTriggerListeners) {
        this.comp_id = comp_id;
        this.isVolatile = isVolatile;
        this.triggerState = triggerState;
        this.triggerType = triggerType;
        this.startTime = startTime;
        this.qrtzJobDetail = qrtzJobDetail;
        this.qrtzTriggerListeners = qrtzTriggerListeners;
    }

    public net.project.hibernate.model.QrtzTriggerPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.QrtzTriggerPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getIsVolatile() {
        return this.isVolatile;
    }

    public void setIsVolatile(String isVolatile) {
        this.isVolatile = isVolatile;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getNextFireTime() {
        return this.nextFireTime;
    }

    public void setNextFireTime(Long nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Long getPrevFireTime() {
        return this.prevFireTime;
    }

    public void setPrevFireTime(Long prevFireTime) {
        this.prevFireTime = prevFireTime;
    }

    public String getTriggerState() {
        return this.triggerState;
    }

    public void setTriggerState(String triggerState) {
        this.triggerState = triggerState;
    }

    public String getTriggerType() {
        return this.triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getCalendarName() {
        return this.calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public Integer getMisfireInstr() {
        return this.misfireInstr;
    }

    public void setMisfireInstr(Integer misfireInstr) {
        this.misfireInstr = misfireInstr;
    }

    public net.project.hibernate.model.QrtzBlobTrigger getQrtzBlobTrigger() {
        return this.qrtzBlobTrigger;
    }

    public void setQrtzBlobTrigger(net.project.hibernate.model.QrtzBlobTrigger qrtzBlobTrigger) {
        this.qrtzBlobTrigger = qrtzBlobTrigger;
    }

    public net.project.hibernate.model.QrtzSimpleTrigger getQrtzSimpleTrigger() {
        return this.qrtzSimpleTrigger;
    }

    public void setQrtzSimpleTrigger(net.project.hibernate.model.QrtzSimpleTrigger qrtzSimpleTrigger) {
        this.qrtzSimpleTrigger = qrtzSimpleTrigger;
    }

    public net.project.hibernate.model.QrtzCronTrigger getQrtzCronTrigger() {
        return this.qrtzCronTrigger;
    }

    public void setQrtzCronTrigger(net.project.hibernate.model.QrtzCronTrigger qrtzCronTrigger) {
        this.qrtzCronTrigger = qrtzCronTrigger;
    }

    public net.project.hibernate.model.QrtzJobDetail getQrtzJobDetail() {
        return this.qrtzJobDetail;
    }

    public void setQrtzJobDetail(net.project.hibernate.model.QrtzJobDetail qrtzJobDetail) {
        this.qrtzJobDetail = qrtzJobDetail;
    }

    public Set getQrtzTriggerListeners() {
        return this.qrtzTriggerListeners;
    }

    public void setQrtzTriggerListeners(Set qrtzTriggerListeners) {
        this.qrtzTriggerListeners = qrtzTriggerListeners;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof QrtzTrigger) ) return false;
        QrtzTrigger castOther = (QrtzTrigger) other;
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
