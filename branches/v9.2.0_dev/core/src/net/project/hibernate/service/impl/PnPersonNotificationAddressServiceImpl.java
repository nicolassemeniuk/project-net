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

import net.project.hibernate.dao.IPnPersonNotificationAddressDAO;
import net.project.hibernate.model.PnPersonNotificationAddress;
import net.project.hibernate.service.IPnPersonNotificationAddressService;

@Service(value="pnPersonNotificationAddressService")
public class PnPersonNotificationAddressServiceImpl implements IPnPersonNotificationAddressService {

	@Autowired
	private IPnPersonNotificationAddressDAO pnPersonNotificationAddressDAO;

	public void setPnPersonNotificationAddressDAO(IPnPersonNotificationAddressDAO pnPersonNotificationAddressDAO) {
		this.pnPersonNotificationAddressDAO = pnPersonNotificationAddressDAO;
	}

	public PnPersonNotificationAddress getPersonNotificationAddress(Integer personNotificationAddressId) {
		return pnPersonNotificationAddressDAO.findByPimaryKey(personNotificationAddressId);
	}

	public Integer savePersonNotificationAddress(PnPersonNotificationAddress pnPersonNotificationAddress) {
		return pnPersonNotificationAddressDAO.create(pnPersonNotificationAddress);
	}

	public void deletePersonNotificationAddress(PnPersonNotificationAddress pnPersonNotificationAddress) {
		pnPersonNotificationAddressDAO.delete(pnPersonNotificationAddress);

	}

	public void updatePersonNotificationAddress(PnPersonNotificationAddress pnPersonNotificationAddress) {
		pnPersonNotificationAddressDAO.update(pnPersonNotificationAddress);
	}

}
