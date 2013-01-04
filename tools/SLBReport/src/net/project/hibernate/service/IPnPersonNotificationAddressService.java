package net.project.hibernate.service;

import java.math.BigDecimal;

import net.project.hibernate.model.PnPersonNotificationAddress;

public interface IPnPersonNotificationAddressService {
	
	/**
	 * @param personNotificationAddressId for PnPersonNotificationAddress we need to select from database
	 * @return PnPersonNotificationAddress bean
	 */
	public PnPersonNotificationAddress getPersonNotificationAddress(BigDecimal personNotificationAddressId);
	
	/**
	 * Saves new PersonNotificationAddress
	 * @param pnPersonNotificationAddress object we want to save
	 * @return primary key for saved PnPersonNotificationAddress
	 */
	public BigDecimal savePersonNotificationAddress(PnPersonNotificationAddress pnPersonNotificationAddress);
	
	/**
	 * Deletes PnPersonNotificationAddress from database
	 * @param pnPersonNotificationAddress object we want to delete
	 */
	public void deletePersonNotificationAddress(PnPersonNotificationAddress pnPersonNotificationAddress);
	
	/**
	 * Updates PnPersonNotificationAddress
	 * @param pnPersonNotificationAddress object we want to update
	 */
	public void updatePersonNotificationAddress(PnPersonNotificationAddress pnPersonNotificationAddress);

}
