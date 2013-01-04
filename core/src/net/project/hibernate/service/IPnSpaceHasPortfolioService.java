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
package net.project.hibernate.service;

import net.project.hibernate.model.PnSpaceHasPortfolio;
import net.project.hibernate.model.PnSpaceHasPortfolioPK;

public interface IPnSpaceHasPortfolioService {
	
	/**
	 * @param pnSpaceHasPortfolioId 
	 * @return PnSpaceHasPortfolio bean
	 */
	public PnSpaceHasPortfolio getSpaceHasPortfolio(PnSpaceHasPortfolioPK pnSpaceHasPortfolioId);
	
	/**
	 * Saves new PnSpaceHasPortfolio
	 * @param PnSpaceHasPortfolio object we want to save
	 * @return primary key for saved space and Portfolio
	 */
	public PnSpaceHasPortfolioPK saveSpaceHasPortfolio(PnSpaceHasPortfolio pnSpaceHasPortfolio);
	
	/**
	 * Deletes PnSpaceHasPortfolio from database
	 * @param PnSpaceHasPortfolio object we want to delete
	 */
	public void deleteSpaceHasPortfolio(PnSpaceHasPortfolio pnSpaceHasPortfolio);
	
	/**
	 * Updates PnSpaceHasPortfolio
	 * @param PnSpaceHasPortfolio object we want to update
	 */
	public void updateSpaceHasPortfolio(PnSpaceHasPortfolio pnSpaceHasPortfolio);

}
