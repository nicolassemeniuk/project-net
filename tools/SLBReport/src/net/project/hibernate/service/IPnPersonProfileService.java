package net.project.hibernate.service;

import java.math.BigDecimal;

import net.project.hibernate.model.PnPersonProfile;

public interface IPnPersonProfileService {
	
	/**
	 * @param personProfileId for PersonProfile we need to select from database
	 * @return PnPersonProfile bean
	 */
	public PnPersonProfile getPersonProfile(BigDecimal personProfileId);
	
	/**
	 * Saves new PersonProfile
	 * @param pnPersonProfile object we want to save
	 * @return primary key for saved PersonProfile
	 */
	public BigDecimal savePersonProfile(PnPersonProfile pnPersonProfile);
	
	/**
	 * Deletes PersonProfile from database
	 * @param pnPersonProfile object we want to delete
	 */
	public void deletePersonProfile(PnPersonProfile pnPersonProfile);
	
	/**
	 * Updates PersonProfile
	 * @param pnPersonProfile object we want to update
	 */
	public void updatePersonProfile(PnPersonProfile pnPersonProfile);

}
