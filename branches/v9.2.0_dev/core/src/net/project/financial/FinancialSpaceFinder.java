package net.project.financial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.project.hibernate.model.PnFinancialSpace;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.User;

public class FinancialSpaceFinder {

	public Collection findByUser(User user) {
		
		List<Integer> spaceIDs = ServiceFactory.getInstance().getPnSpaceHasPersonService().getSpacesFromPerson(Integer.valueOf(user.getID()));
		PnFinancialSpaceList spaces = ServiceFactory.getInstance().getPnFinancialSpaceService().getFinancialSpacesByIds(spaceIDs);
		ArrayList<FinancialSpace> financialSpaces = new ArrayList<FinancialSpace>();
		for(PnFinancialSpace space : spaces){
			FinancialSpace financialSpace = new FinancialSpace(space);
			financialSpaces.add(financialSpace);
		}
		return financialSpaces;
	}
	
	

}
