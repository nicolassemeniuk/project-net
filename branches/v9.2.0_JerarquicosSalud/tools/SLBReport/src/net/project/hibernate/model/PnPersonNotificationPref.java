package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonNotificationPref implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** nullable persistent field */
    private BigDecimal dailyTime;

    /** nullable persistent field */
    private BigDecimal weeklyDay;

    /** nullable persistent field */
    private BigDecimal weeklyTime;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** full constructor */
    public PnPersonNotificationPref(BigDecimal personId, BigDecimal dailyTime, BigDecimal weeklyDay, BigDecimal weeklyTime, net.project.hibernate.model.PnPerson pnPerson) {
        this.personId = personId;
        this.dailyTime = dailyTime;
        this.weeklyDay = weeklyDay;
        this.weeklyTime = weeklyTime;
        this.pnPerson = pnPerson;
    }

    /** default constructor */
    public PnPersonNotificationPref() {
    }

    /** minimal constructor */
    public PnPersonNotificationPref(BigDecimal personId) {
        this.personId = personId;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public BigDecimal getDailyTime() {
        return this.dailyTime;
    }

    public void setDailyTime(BigDecimal dailyTime) {
        this.dailyTime = dailyTime;
    }

    public BigDecimal getWeeklyDay() {
        return this.weeklyDay;
    }

    public void setWeeklyDay(BigDecimal weeklyDay) {
        this.weeklyDay = weeklyDay;
    }

    public BigDecimal getWeeklyTime() {
        return this.weeklyTime;
    }

    public void setWeeklyTime(BigDecimal weeklyTime) {
        this.weeklyTime = weeklyTime;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .toString();
    }

}
