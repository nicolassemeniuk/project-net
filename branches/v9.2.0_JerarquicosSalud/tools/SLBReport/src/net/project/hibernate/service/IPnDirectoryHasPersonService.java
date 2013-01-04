package net.project.hibernate.service;

import java.math.BigDecimal;

import net.project.hibernate.model.PnDirectoryHasPerson;
import net.project.hibernate.model.PnDirectoryHasPersonPK;

public interface IPnDirectoryHasPersonService {
	
	/**
	 * @param directoryHasPersonPK for PnDirectoryHasPerson we need to select from database
	 * @return PnDirectoryHasPerson bean
	 */
	public PnDirectoryHasPerson getDirectoryHasPerson(PnDirectoryHasPersonPK directoryHasPersonPK);
	
	/**
	 * Saves PnDirectoryHasPerson
	 * @param pnDirectoryHasPerson object we want to save
	 * @return primary key for saved DirectoryHasPerson
	 */
	public PnDirectoryHasPersonPK saveDirectoryHasPerson(PnDirectoryHasPerson pnDirectoryHasPerson);
	
	/**
	 * Deletes PnDirectoryHasPerson from database
	 * @param pnDirectoryHasPerson object we want to delete
	 */
	public void deleteDirectoryHasPerson(PnDirectoryHasPerson pnDirectoryHasPerson);
	
	/**
	 * Updates PnDirectoryHasPerson
	 * @param pnDirectoryHasPerson object we want to update
	 */
	public void updateDirectoryHasPerson(PnDirectoryHasPerson pnDirectoryHasPerson);


}
