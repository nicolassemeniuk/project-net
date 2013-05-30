package net.project.hibernate.dao;

import java.util.Collection;

import net.project.financial.PnFinancialSpaceList;
import net.project.hibernate.model.PnFinancialSpace;

public interface IPnFinancialSpaceDAO extends IDAO<PnFinancialSpace, Integer> {
	
	public PnFinancialSpace getFinancialSpaceById(Integer financialSpaceId);

	public PnFinancialSpaceList getFinancialSpacesByIds(Collection additionalSpaceIDCollection);

}
