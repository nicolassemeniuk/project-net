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


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnDocProviderDAO;
import net.project.hibernate.model.PnDocProvider;
import net.project.hibernate.service.IPnDocProviderService;

@Service(value="pnDocProviderService")
public class PnDocProviderServiceImpl implements IPnDocProviderService {

	@Autowired
	private IPnDocProviderDAO pnDocProviderDAO;

	public void setPnDocProviderDAO(IPnDocProviderDAO pnDocProviderDAO) {
		this.pnDocProviderDAO = pnDocProviderDAO;
	}

	public PnDocProvider getDocProvider(Integer docProviderId) {
		return pnDocProviderDAO.findByPimaryKey(docProviderId);
	}

	public Integer saveDocProvider(PnDocProvider pnDocProvider) {
		return pnDocProviderDAO.create(pnDocProvider);
	}

	public void deleteDocProvider(PnDocProvider pnDocProvider) {
		pnDocProviderDAO.delete(pnDocProvider);
	}

	public void updateDocProvider(PnDocProvider pnDocProvider) {
		pnDocProviderDAO.update(pnDocProvider);
	}

	public List<PnDocProvider> getDocProviderIds() {
		return pnDocProviderDAO.getDocProviderIds();
	}

}
