package net.project.hibernate.service;

import java.math.BigDecimal;

import net.project.hibernate.model.PnCalendar;

public interface IPnCalendarService {
	

	/**
	 * @param calendarId for Calendar we need to select from database
	 * @return PnCalendar bean
	 */
	public PnCalendar getCalendar(BigDecimal calendarId);
	
	/**
	 * Saves new Calendar
	 * @param pnCalendar object we want to save
	 * @return primary key for saved Calendar
	 */
	public BigDecimal saveCalendar(PnCalendar pnCalendar);
	
	/**
	 * Deletes Calendar from database
	 * @param pnCalendar object we want to delete
	 */
	public void deleteCalendar(PnCalendar pnCalendar);
	
	/**
	 * Updates Calendar
	 * @param pnCalendar object we want to update
	 */
	public void updateCalendar(PnCalendar pnCalendar);


}
