package net.project.hibernate.model;

import java.io.Serializable;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/** @author Hibernate CodeGenerator */
public class PnWorkingtimeCalendarEntry implements Serializable {

	/** identifier field */
	private net.project.hibernate.model.PnWorkingtimeCalendarEntryPK comp_id;

	/** persistent field */
	private String isWorkingDay;

	/** persistent field */
	private String isDayOfWeek;

	/** nullable persistent field */
	private Integer dayNumber;

	/** nullable persistent field */
	private Timestamp startDate;

	/** nullable persistent field */
	private Timestamp endDate;

	/** nullable persistent field */
	private Timestamp time1Start;

	/** nullable persistent field */
	private Timestamp time1End;

	/** nullable persistent field */
	private Timestamp time2Start;

	/** nullable persistent field */
	private Timestamp time2End;

	/** nullable persistent field */
	private Timestamp time3Start;

	/** nullable persistent field */
	private Timestamp time3End;

	/** nullable persistent field */
	private Timestamp time4Start;

	/** nullable persistent field */
	private Timestamp time4End;

	/** nullable persistent field */
	private Timestamp time5Start;

	/** nullable persistent field */
	private Timestamp time5End;

	/** persistent field */
	private String recordStatus;

	/** nullable persistent field */
	private net.project.hibernate.model.PnWorkingtimeCalendar pnWorkingtimeCalendar;

	/** full constructor */
	public PnWorkingtimeCalendarEntry(net.project.hibernate.model.PnWorkingtimeCalendarEntryPK comp_id, String isWorkingDay, String isDayOfWeek, Integer dayNumber, Timestamp startDate,
			Timestamp endDate, Timestamp time1Start, Timestamp time1End, Timestamp time2Start, Timestamp time2End, Timestamp time3Start, Timestamp time3End, Timestamp time4Start, Timestamp time4End, Timestamp time5Start,
			Timestamp time5End, String recordStatus, net.project.hibernate.model.PnWorkingtimeCalendar pnWorkingtimeCalendar) {
		this.comp_id = comp_id;
		this.isWorkingDay = isWorkingDay;
		this.isDayOfWeek = isDayOfWeek;
		this.dayNumber = dayNumber;
		this.startDate = startDate;
		this.endDate = endDate;
		this.time1Start = time1Start;
		this.time1End = time1End;
		this.time2Start = time2Start;
		this.time2End = time2End;
		this.time3Start = time3Start;
		this.time3End = time3End;
		this.time4Start = time4Start;
		this.time4End = time4End;
		this.time5Start = time5Start;
		this.time5End = time5End;
		this.recordStatus = recordStatus;
		this.pnWorkingtimeCalendar = pnWorkingtimeCalendar;
	}

	/** default constructor */
	public PnWorkingtimeCalendarEntry() {
	}

	/** minimal constructor */
	public PnWorkingtimeCalendarEntry(net.project.hibernate.model.PnWorkingtimeCalendarEntryPK comp_id, String isWorkingDay, String isDayOfWeek, String recordStatus) {
		this.comp_id = comp_id;
		this.isWorkingDay = isWorkingDay;
		this.isDayOfWeek = isDayOfWeek;
		this.recordStatus = recordStatus;
	}

	public net.project.hibernate.model.PnWorkingtimeCalendarEntryPK getComp_id() {
		return this.comp_id;
	}

	public void setComp_id(net.project.hibernate.model.PnWorkingtimeCalendarEntryPK comp_id) {
		this.comp_id = comp_id;
	}

	public String getIsWorkingDay() {
		return this.isWorkingDay;
	}

	public void setIsWorkingDay(String isWorkingDay) {
		this.isWorkingDay = isWorkingDay;
	}

	public String getIsDayOfWeek() {
		return this.isDayOfWeek;
	}

	public void setIsDayOfWeek(String isDayOfWeek) {
		this.isDayOfWeek = isDayOfWeek;
	}

	public Integer getDayNumber() {
		return this.dayNumber;
	}

	public void setDayNumber(Integer dayNumber) {
		this.dayNumber = dayNumber;
	}

	public Timestamp getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public Timestamp getTime1Start() {
		return this.time1Start;
	}

	public void setTime1Start(Timestamp time1Start) {
		this.time1Start = time1Start;
	}

	public Timestamp getTime1End() {
		return this.time1End;
	}

	public void setTime1End(Timestamp time1End) {
		this.time1End = time1End;
	}

	public Timestamp getTime2Start() {
		return this.time2Start;
	}

	public void setTime2Start(Timestamp time2Start) {
		this.time2Start = time2Start;
	}

	public Timestamp getTime2End() {
		return this.time2End;
	}

	public void setTime2End(Timestamp time2End) {
		this.time2End = time2End;
	}

	public Timestamp getTime3Start() {
		return this.time3Start;
	}

	public void setTime3Start(Timestamp time3Start) {
		this.time3Start = time3Start;
	}

	public Timestamp getTime3End() {
		return this.time3End;
	}

	public void setTime3End(Timestamp time3End) {
		this.time3End = time3End;
	}

	public Timestamp getTime4Start() {
		return this.time4Start;
	}

	public void setTime4Start(Timestamp time4Start) {
		this.time4Start = time4Start;
	}

	public Timestamp getTime4End() {
		return this.time4End;
	}

	public void setTime4End(Timestamp time4End) {
		this.time4End = time4End;
	}

	public Timestamp getTime5Start() {
		return this.time5Start;
	}

	public void setTime5Start(Timestamp time5Start) {
		this.time5Start = time5Start;
	}

	public Timestamp getTime5End() {
		return this.time5End;
	}

	public void setTime5End(Timestamp time5End) {
		this.time5End = time5End;
	}

	public String getRecordStatus() {
		return this.recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public net.project.hibernate.model.PnWorkingtimeCalendar getPnWorkingtimeCalendar() {
		return this.pnWorkingtimeCalendar;
	}

	public void setPnWorkingtimeCalendar(net.project.hibernate.model.PnWorkingtimeCalendar pnWorkingtimeCalendar) {
		this.pnWorkingtimeCalendar = pnWorkingtimeCalendar;
	}

	public String toString() {
		return new ToStringBuilder(this).append("comp_id", getComp_id()).toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof PnWorkingtimeCalendarEntry))
			return false;
		PnWorkingtimeCalendarEntry castOther = (PnWorkingtimeCalendarEntry) other;
		return new EqualsBuilder().append(this.getComp_id(), castOther.getComp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getComp_id()).toHashCode();
	}

}
