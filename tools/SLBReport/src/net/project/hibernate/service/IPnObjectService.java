package net.project.hibernate.service;

import java.math.BigDecimal;

import net.project.hibernate.model.PnObject;

public interface IPnObjectService {
	
	/** 
	 * @param primaryKey unique is id for PnObject bean 
	 * @return PnObject bean
	 */
	public PnObject getObject(BigDecimal primaryKey);
	
	/**
	 * Stores new PnObject on database
	 * @param pnObject is PnObject bean that we need to save in database
	 * @return saved object id, pnObject have sequence so we don't need to set primary key value
	 */
	public BigDecimal saveObject(PnObject pnObject);

}
