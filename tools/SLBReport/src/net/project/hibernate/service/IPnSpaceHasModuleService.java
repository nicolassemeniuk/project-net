package net.project.hibernate.service;

import net.project.hibernate.model.PnSpaceHasModule;
import net.project.hibernate.model.PnSpaceHasModulePK;

public interface IPnSpaceHasModuleService {
	
	
	/**
	 * @param pnSpaceHasModulePK primary key for PNSpaceHasModule we need to select from database
	 * @return PnSpaceHasModule bean
	 */
	public PnSpaceHasModule getSpaceHasModule(PnSpaceHasModulePK pnSpaceHasModulePK);
	
	/**
	 * Saves new PnSpaceHasModule
	 * @param pnSpaceHasModule object we want to save
	 * @return primary key for saved PnSpaceHasModule
	 */
	public PnSpaceHasModulePK saveSpaceHasModule(PnSpaceHasModule pnSpaceHasModule);
	
	/**
	 * Deletes nnSpaceHasModule object from database
	 * @param pnSpaceHasModule object we want to delete
	 */
	public void deleteSpaceHasModule(PnSpaceHasModule pnSpaceHasModule);
	
	/**
	 * Updates PnSpaceHasModule
	 * @param pnSpaceHasModule object we want to update
	 */
	public void updateSpaceHasModule(PnSpaceHasModule pnSpaceHasModule);
	

}
