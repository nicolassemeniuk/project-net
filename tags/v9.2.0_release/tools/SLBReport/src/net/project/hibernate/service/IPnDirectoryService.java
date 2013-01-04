package net.project.hibernate.service;

import java.math.BigDecimal;
import java.util.List;

import net.project.hibernate.model.PnDirectory;

public interface IPnDirectoryService {
	
	/**
	 * @param directoryId for Directory we need to select from database
	 * @return PnDirectory bean
	 */
	public PnDirectory getDirectory(BigDecimal directoryId);
	
	/**
	 * Saves new Directory
	 * @param pnDirectory object we want to save
	 * @return primary key for saved Directory
	 */
	public BigDecimal saveDirectory(PnDirectory pnDirectory);
	
	/**
	 * Deletes Directory from database
	 * @param pnDirectory object we want to delete
	 */
	public void deleteDirectory(PnDirectory pnDirectory);
	
	/**
	 * Updates Directory
	 * @param pnDirectory object we want to update
	 */
	public void updateDirectory(PnDirectory pnDirectory);
	
	/**
	 * Get list of default directories
	 * @return list of default directories
	 */
	public List<PnDirectory> getDefaultDirectory();

}
