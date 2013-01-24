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

import net.project.hibernate.dao.IPnPropertySheetDAO;
import net.project.hibernate.model.PnPropertySheet;
import net.project.hibernate.service.IPnPropertySheetService;

@Service(value="pnPropertySheetService")
public class PnPropertySheetServiceImpl implements IPnPropertySheetService {
	
	@Autowired
	private IPnPropertySheetDAO pnPropertySheetDAO;

	public void setPnPropertySheetDAO(IPnPropertySheetDAO pnPropertySheetDAO) {
		this.pnPropertySheetDAO = pnPropertySheetDAO;
	}

	public PnPropertySheet getPropertySheet(Integer pnPropertySheetId) {
		return pnPropertySheetDAO.findByPimaryKey(pnPropertySheetId);
	}

	public Integer savePropertySheet(PnPropertySheet pnPropertySheet) {
		return pnPropertySheetDAO.create(pnPropertySheet);
	}

	public void deletePropertySheet(PnPropertySheet pnPropertySheet) {
		pnPropertySheetDAO.delete(pnPropertySheet);
	}

	public void updatePropertySheet(PnPropertySheet pnPropertySheet) {
		pnPropertySheetDAO.update(pnPropertySheet);
	}

}
