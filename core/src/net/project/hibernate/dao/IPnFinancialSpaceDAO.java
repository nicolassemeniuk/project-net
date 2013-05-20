package net.project.hibernate.dao;

import net.project.hibernate.model.PnFinancialSpace;

public interface IPnFinancialSpaceDAO extends IDAO<PnFinancialSpace, Integer> {
	
	public PnFinancialSpace getFinancialSpaceById(Integer financialSpaceId);

}
