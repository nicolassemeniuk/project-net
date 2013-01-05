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
package net.project.hibernate.service.impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnPortfolioDAO;
import net.project.hibernate.model.PnPortfolio;
import net.project.hibernate.service.IPnPortfolioService;

@Service(value="pnPortfolioService")
public class PnPortfolioServiceImpl implements IPnPortfolioService {
	
	/**
	 * PnPortfolio data access object
	 */
	@Autowired
	private IPnPortfolioDAO pnPortfolioDAO;

	/**
	 * sets PnPortfolio data acces object
	 * @param pnPortfolioDAO
	 */
	public void setPnPortfolioDAO(IPnPortfolioDAO pnPortfolioDAO) {
		this.pnPortfolioDAO = pnPortfolioDAO;
	}

	/**
	 * gets PnPortfolio object for given id
	 */
	public PnPortfolio getPortfolio(Integer PortfolioId) {
		return pnPortfolioDAO.findByPimaryKey(PortfolioId);
	}

	/**
	 * saves pnPortfolio object
	 */
	public Integer savePortfolio(PnPortfolio pnPortfolio) {
		return pnPortfolioDAO.create(pnPortfolio);
	}

	/**
	 * deletes pnPortfolio object
	 */
	public void deletePortfolio(PnPortfolio pnPortfolio) {
		pnPortfolioDAO.delete(pnPortfolio);
	}

	/**
	 * updates pnPortfolio
	 */	
	public void updatePortfolio(PnPortfolio pnPortfolio) {
		pnPortfolioDAO.update(pnPortfolio);
	}

	public PnPortfolio getPortfolioForSpace(Integer spaceId) {
	    return pnPortfolioDAO.getPortfolioForSpace(spaceId);
	}

}
