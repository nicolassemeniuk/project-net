package net.project.hibernate.service;

import java.math.BigDecimal;

import net.project.hibernate.model.PnPerson;

public interface IPnPersonService {
	
	/**
	 * @param personId for person we need to select from database
	 * @return PnPerson bean
	 */
	public PnPerson getPerson(BigDecimal personId);
	
	/**
	 * Saves new person
	 * @param pnPerson object we want to save
	 * @return primary key for saved person
	 */
	public BigDecimal savePerson(PnPerson pnPerson);
	
	/**
	 * Deletes person from database
	 * @param pnPerson object we want to delete
	 */
	public void deletePerson(PnPerson pnPerson);
	
	/**
	 * Updates person
	 * @param pnPerson object we want to update
	 */
	public void updatePerson(PnPerson pnPerson);

}
