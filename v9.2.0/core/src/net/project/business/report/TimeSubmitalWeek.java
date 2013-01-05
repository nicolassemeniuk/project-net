package net.project.business.report;

import java.util.Date;

/**
 *
 */
public class TimeSubmitalWeek {

	private Date dateValue;
	
	private String work;
	
	private boolean endOfweek;;
	
	private String dateString;
	
	private String weeklyTotal;
	
	private Integer weekDays;
	
	private String dateRangeString;
	
	public TimeSubmitalWeek() {
	}

	/**
	 * @return the dateValue
	 */
	public Date getDateValue() {
		return dateValue;
	}

	/**
	 * @param dateValue the dateValue to set
	 */
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	/**
	 * @return the work
	 */
	public String getWork() {
		return work;
	}

	/**
	 * @param work the work to set
	 */
	public void setWork(String work) {
		this.work = work;
	}

	/**
	 * @return the dateString
	 */
	public String getDateString() {
		return dateString;
	}

	/**
	 * @param dateString the dateString to set
	 */
	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	/**
	 * @return the weeklyTotal
	 */
	public String getWeeklyTotal() {
		return weeklyTotal;
	}

	/**
	 * @param weeklyTotal the weeklyTotal to set
	 */
	public void setWeeklyTotal(String weeklyTotal) {
		this.weeklyTotal = weeklyTotal;
	}

	/**
	 * @return the weekDays
	 */
	public Integer getWeekDays() {
		return weekDays;
	}

	/**
	 * @param weekDays the weekDays to set
	 */
	public void setWeekDays(Integer weekDays) {
		this.weekDays = weekDays;
	}

	/**
	 * @return the dateRangeString
	 */
	public String getDateRangeString() {
		return dateRangeString;
	}

	/**
	 * @param dateRangeString the dateRangeString to set
	 */
	public void setDateRangeString(String dateRangeString) {
		this.dateRangeString = dateRangeString;
	}

	/**
	 * @return the endOfweek
	 */
	public boolean isEndOfweek() {
		return endOfweek;
	}

	/**
	 * @param endOfweek the endOfweek to set
	 */
	public void setEndOfweek(boolean endOfweek) {
		this.endOfweek = endOfweek;
	}

}
