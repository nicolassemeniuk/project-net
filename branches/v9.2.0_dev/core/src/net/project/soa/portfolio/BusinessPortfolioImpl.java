/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
package net.project.soa.portfolio;

import net.project.business.BusinessSpace;
import net.project.portfolio.BusinessPortfolio;
import net.project.portfolio.BusinessPortfolioBean;
import net.project.security.SessionManager;

public class BusinessPortfolioImpl extends BusinessPortfolioBean implements
		IBusinessPortfolio {

	/* (non-Javadoc)
	 * @see net.project.soa.portfolio.IBusinessPortfolio#getBusinessSpaces(java.lang.String)
	 */
	public BusinessSpace[] getBusinessSpaces() throws Exception {
	    BusinessPortfolio businessPortfolio = new BusinessPortfolio(); 
		businessPortfolio.clear();
	    businessPortfolio.setUser(SessionManager.getUser());
	    businessPortfolio.load();
	    Object[] arr = businessPortfolio.toArray();
	    BusinessSpace[] businessSpaces = new BusinessSpace[arr.length];
	    for(int i=0;i<arr.length;i++){
	    	businessSpaces[i] = (BusinessSpace)arr[i];
	    }
	    return businessSpaces;
	}

}
