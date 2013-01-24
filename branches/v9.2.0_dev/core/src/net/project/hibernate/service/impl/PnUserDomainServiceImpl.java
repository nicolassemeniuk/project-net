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

import net.project.hibernate.dao.IPnUserDomainDAO;
import net.project.hibernate.model.PnUserDomain;
import net.project.hibernate.service.IPnUserDomainService;

@Service(value="pnUserDomainService")
public class PnUserDomainServiceImpl implements IPnUserDomainService {
	
	@Autowired
	private IPnUserDomainDAO pnUserDomainDAO;

	public void setPnUserDomainDAO(IPnUserDomainDAO pnUserDomainDAO) {
		this.pnUserDomainDAO = pnUserDomainDAO;
	}

	public PnUserDomain getUserDomain(Integer userDomainId) {
		return pnUserDomainDAO.getUserDomain(userDomainId);
	}

	public Integer saveUserDomain(PnUserDomain pnUserDomain) {
		return pnUserDomainDAO.create(pnUserDomain);
	}

	public void deleteUserDomain(PnUserDomain pnUserDomain) {
		pnUserDomainDAO.delete(pnUserDomain);
	}

	public void updateUserDomain(PnUserDomain pnUserDomain) {
		pnUserDomainDAO.update(pnUserDomain);
	}

}
