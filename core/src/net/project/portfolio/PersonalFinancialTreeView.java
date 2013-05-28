package net.project.portfolio;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.project.persistence.PersistenceException;

public class PersonalFinancialTreeView extends PortfolioTreeView {
	
	PersonalFinancialTreeView(Portfolio portfolio) throws PersistenceException {
        super(portfolio);
    }

	@Override
	protected Collection getRelatedSpaceCollection(Set membershipSpaceIDCollection) throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map loadAllSpacesMap(Collection additionalSpaceIDCollection) throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void constructTree(Map allSpacesMap, Collection membershipSpaceIDCollection, Map childParentMap) {
		// TODO Auto-generated method stub

	}

}
