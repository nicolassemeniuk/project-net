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

import net.project.hibernate.model.PnClass;
import net.project.hibernate.dao.IPnClassDAO;
import net.project.hibernate.service.IPnClassService;

@Service(value="pnClassService")
public class PnClassServiceImpl implements IPnClassService {
	
	@Autowired
	private IPnClassDAO pnClassDAO;
	
	/**
	 * @param pnClassDAO the pnClassDAO to set
	 */
	public void setPnClassDAO(IPnClassDAO pnClassDAO) {
		this.pnClassDAO = pnClassDAO;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnClassService#getFormNamesForSpace(java.lang.String)
	 */
	public List<PnClass> getFormNamesForSpace(String spaceId){
		return pnClassDAO.getFormNamesForSpace(spaceId);
	}
	
	/**
	 * Get Form name and recordStatus by form id
	 * @param classId
	 * @return PnClass
	 */
	public PnClass getFormWithRecordStatus(Integer classId) {
		return pnClassDAO.getFormWithRecordStatus(classId);
	}
}
