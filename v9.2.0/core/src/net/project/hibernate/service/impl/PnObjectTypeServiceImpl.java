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

import net.project.hibernate.dao.IPnObjectTypeDAO;
import net.project.hibernate.model.PnObjectType;
import net.project.hibernate.service.IPnObjectTypeService;

@Service(value="pnObjectTypeService")
public class PnObjectTypeServiceImpl implements IPnObjectTypeService {

	@Autowired
	private IPnObjectTypeDAO pnObjectTypeDAO;

	public void setPnObjectTypeDAO(IPnObjectTypeDAO pnObjectTypeDAO) {
		this.pnObjectTypeDAO = pnObjectTypeDAO;
	}

	public PnObjectType getObjectType(String pnObjectTypeId) {
		return pnObjectTypeDAO.findByPimaryKey(pnObjectTypeId);
	}

	public String saveObjectType(PnObjectType pnObjectType) {
		return pnObjectTypeDAO.create(pnObjectType);
	}

	public void deleteObjectType(PnObjectType pnObjectType) {
		pnObjectTypeDAO.delete(pnObjectType);
	}

	public void updateObjectType(PnObjectType pnObjectType) {
		pnObjectTypeDAO.update(pnObjectType);
	}

	public List<PnObjectType> findObjectTypes() {
		return pnObjectTypeDAO.findObjectTypes();
	}

    public List<PnObjectType> findAll() {
        return pnObjectTypeDAO.findAll();
    }

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnObjectTypeService#getObjectTypeByObjectId(java.lang.Integer)
	 */
	public PnObjectType getObjectTypeByObjectId(Integer objectId) {
		return pnObjectTypeDAO.getObjectTypeByObjectId(objectId);
	}

}
