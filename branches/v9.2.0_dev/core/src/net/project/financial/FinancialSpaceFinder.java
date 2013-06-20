package net.project.financial;

import java.util.ArrayList;
import java.util.Collection;

import net.project.hibernate.model.PnFinancialSpace;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.User;

public class FinancialSpaceFinder {

	/**
	 * Finds all financial spaces for a certain user.
	 * @param user the user from wich we want to obtain the financial spaces.
	 * @return a collection of financial spaces.
	 */
	@SuppressWarnings("rawtypes")
	public Collection findByUser(User user) {
		
		ArrayList<Integer> spaceIDs = ServiceFactory.getInstance().getPnSpaceHasPersonService().getSpacesFromPerson(Integer.valueOf(user.getID()));
		PnFinancialSpaceList spaces = ServiceFactory.getInstance().getPnFinancialSpaceService().getFinancialSpacesByIds(spaceIDs);
		ArrayList<FinancialSpace> financialSpaces = new ArrayList<FinancialSpace>();
		for(PnFinancialSpace space : spaces){
			FinancialSpace financialSpace = new FinancialSpace(space);
			financialSpaces.add(financialSpace);
		}
		return financialSpaces;
	}
	
	

}
