package net.project.hibernate.service;

import net.project.hibernate.model.PnGroupHasPerson;
import net.project.hibernate.model.PnGroupHasPersonPK;

public interface IPnGroupHasPersonService {
	

	/**
	 * @param pnGroupHasPersonId 
	 * @return PnGroupHasPerson bean
	 */
	public PnGroupHasPerson getGroupHasPerson(PnGroupHasPersonPK pnGroupHasPersonId);
	
	/**
	 * Saves new PnGroupHasPerson
	 * @param PnGroupHasPerson object we want to save
	 * @return primary key for saved PnGroupHasPerson bean
	 */
	public PnGroupHasPersonPK saveGroupHasPerson(PnGroupHasPerson pnGroupHasPerson);
	
	/**
	 * Deletes PnGroupHasPerson from database
	 * @param PnGroupHasPerson object we want to delete
	 */
	public void deleteGroupHasPerson(PnGroupHasPerson pnGroupHasPerson);
	
	/**
	 * Updates PnGroupHasPerson
	 * @param PnGroupHasPerson object we want to update
	 */
	public void updateGroupHasPerson(PnGroupHasPerson pnGroupHasPerson);


}
