package net.project.hibernate.service;

import net.project.hibernate.model.PnSpaceHasCalendar;
import net.project.hibernate.model.PnSpaceHasCalendarPK;

public interface IPnSpaceHasCalendarService {
	
	/**
	 * @param pnGroupHasPersonId 
	 * @return PnGroupHasPerson bean
	 */
	public PnSpaceHasCalendar getSpaceHasCalendar(PnSpaceHasCalendarPK pnSpaceHasCalendarId);
	
	/**
	 * Saves new PnSpaceHasCalendar
	 * @param PnSpaceHasCalendar object we want to save
	 * @return primary key for saved space and Portfolio
	 */
	public PnSpaceHasCalendarPK saveSpaceHasCalendar(PnSpaceHasCalendar pnSpaceHasCalendar);
	
	/**
	 * Deletes PnSpaceHasCalendar from database
	 * @param PnSpaceHasCalendar object we want to delete
	 */
	public void deleteSpaceHasCalendar(PnSpaceHasCalendar pnSpaceHasCalendar);
	
	/**
	 * Updates PnSpaceHasCalendar
	 * @param PnSpaceHasCalendar object we want to update
	 */
	public void updateSpaceHasCalendar(PnSpaceHasCalendar pnSpaceHasCalendar);


}
