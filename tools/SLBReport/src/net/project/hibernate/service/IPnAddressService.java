package net.project.hibernate.service;

import java.math.BigDecimal;

import net.project.hibernate.model.PnAddress;

public interface IPnAddressService {
	
	/**
	 * @param addressId for Address we need to select from database
	 * @return PnAddress bean
	 */
	public PnAddress getAddress(BigDecimal addressId);
	
	/**
	 * Saves new Address
	 * @param pnAddress object we want to save
	 * @return primary key for saved Address
	 */
	public BigDecimal saveAddress(PnAddress pnAddress);
	
	/**
	 * Deletes Address from database
	 * @param pnAddress object we want to delete
	 */
	public void deleteAddress(PnAddress pnAddress);
	
	/**
	 * Updates Address
	 * @param pnAddress object we want to update
	 */
	public void updateAddress(PnAddress pnAddress);

}
