package net.project.hibernate.service;

import net.project.hibernate.model.PnSpaceHasPerson;
import net.project.hibernate.model.PnSpaceHasPersonPK;

public interface IPnSpaceHasPersonService {
	
	/**
	 * @param pnSpaceHasPersonId 
	 * @return PnSpaceHasPerson bean
	 */
	public PnSpaceHasPerson getSpaceHasPerson(PnSpaceHasPersonPK pnSpaceHasPersonId);
	
	/**
	 * Saves new PnSpaceHasPerson
	 * @param PnSpaceHasPerson object we want to save
	 * @return primary key for saved space and person
	 */
	public PnSpaceHasPersonPK saveSpaceHasPerson(PnSpaceHasPerson pnSpaceHasPerson);
	
	/**
	 * Deletes PnSpaceHasPerson from database
	 * @param PnSpaceHasPerson object we want to delete
	 */
	public void deleteSpaceHasPerson(PnSpaceHasPerson pnSpaceHasPerson);
	
	/**
	 * Updates PnSpaceHasPerson
	 * @param PnSpaceHasPerson object we want to update
	 */
	public void updateSpaceHasPerson(PnSpaceHasPerson pnSpaceHasPerson);

}
