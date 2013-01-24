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

import net.project.hibernate.dao.IPnBusinessSpaceDAO;
import net.project.hibernate.model.PnBusiness;
import net.project.hibernate.model.PnBusinessSpace;
import net.project.hibernate.service.IPnBusinessSpaceService;


@Service(value="pnBusinessSpaceService")
public class PnBusinessSpaceServiceImpl implements IPnBusinessSpaceService {
	
	@Autowired
    private IPnBusinessSpaceDAO pnBusinessSpaceDAO;

    public IPnBusinessSpaceDAO getPnBusinessSpaceDAO() {
        return pnBusinessSpaceDAO;
    }

    public void setPnBusinessSpaceDAO(IPnBusinessSpaceDAO pnBusinessSpaceDAO) {
        this.pnBusinessSpaceDAO = pnBusinessSpaceDAO;
    }

    public PnBusinessSpace getBusinessSpace(Integer objectId) {
        return getPnBusinessSpaceDAO().findByPimaryKey(objectId);
    }

    public void saveObject(PnBusinessSpace object) {
        getPnBusinessSpaceDAO().update(object);
    }

    public void updateBusinessSpace(PnBusinessSpace object) {
	pnBusinessSpaceDAO.update(object);
    }
    
    public PnBusiness getBusinessByProjectId(Integer projectId){
    	return pnBusinessSpaceDAO.getBusinessByProjectId(projectId);
    }
    
	public PnBusinessSpace getBusinessSpaceById(Integer id){
		return pnBusinessSpaceDAO.getBusinessSpaceById(id);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnBusinessSpaceService#isRootBusines(java.lang.Integer)
	 */
	public boolean isRootBusines(Integer spaceId) {
		return pnBusinessSpaceDAO.isRootBusines(spaceId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnBusinessSpaceService#getParentBusinessByBusinessId(java.lang.Integer)
	 */
	public PnBusiness getParentBusinessByBusinessId(Integer businessId) {
		return pnBusinessSpaceDAO.getParentBusinessByBusinessId(businessId);
	}
}
