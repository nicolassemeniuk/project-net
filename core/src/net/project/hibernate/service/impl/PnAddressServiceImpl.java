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

import net.project.hibernate.dao.IPnAddressDAO;
import net.project.hibernate.model.PnAddress;
import net.project.hibernate.service.IPnAddressService;

@Service(value="pnAddressService")
public class PnAddressServiceImpl implements IPnAddressService {
	
	@Autowired
	private IPnAddressDAO pnAddressDAO;

	public void setPnAddressDAO(IPnAddressDAO pnAddressDAO) {
		this.pnAddressDAO = pnAddressDAO;
	}

	public PnAddress getAddress(Integer addressId) {
		return pnAddressDAO.findByPimaryKey(addressId);
	}

	public Integer saveAddress(PnAddress pnAddress) {
		return pnAddressDAO.create(pnAddress);
	}

	public void deleteAddress(PnAddress pnAddress) {
		pnAddressDAO.delete(pnAddress);
	}

	public void updateAddress(PnAddress pnAddress) {
		pnAddressDAO.update(pnAddress);
	}

}
