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
/**
 * 
 */
package net.project.hibernate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnUserDAO;
import net.project.hibernate.service.IPnUserService;

/**
 *
 */
@Service(value="pnUserService")
public class PnUserServiceImpl implements IPnUserService {
	
	/**
	 * PnUser data access object
	 */
	@Autowired
	private IPnUserDAO pnUserDAO;
	
	/**
	 * @param pnUserDAO the pnUserDAO to set
	 */
	public void setPnUserDAO(IPnUserDAO pnUserDAO) {
		this.pnUserDAO = pnUserDAO;
	}	

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnUserService#isOnline(java.lang.Integer)
	 */
	public Boolean isOnline(Integer userId) {
		return pnUserDAO.isOnline(userId);
	}

	/**
	 * returns number of all active users in the system 
	 */
	public Integer getUsersCount() {
		return pnUserDAO.getUsersCount();
	}	
	
	

}
