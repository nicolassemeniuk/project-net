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

import net.project.hibernate.dao.IPnObjectNameDAO;
import net.project.hibernate.model.PnObjectName;
import net.project.hibernate.service.IPnObjectNameService;

@Service(value="pnObjectNameService")
public class PnObjectNameServiceImpl implements IPnObjectNameService {


    /**
     * PnObjectName database access object
     */
	@Autowired
    private IPnObjectNameDAO pnObjectNameDAO;

    /**
     * Sets the PnObject data access object
     *
     * @param pnObjectDao
     */
    public void setPnObjectNameDAO(IPnObjectNameDAO pnObjectNameDAO) {
        this.pnObjectNameDAO = pnObjectNameDAO;
    }

    public String getNameFofObject(Integer objectId) {
    	return pnObjectNameDAO.getNameFofObject(objectId);
    }
    
    /**
     * Save the PnObject data 
     * @param pnObjectName
     */
    public Integer save(PnObjectName pnObjectName) {
    	return pnObjectNameDAO.create(pnObjectName);
    }
    
    /**
     * Update the PnObject data 
     * @param pnObjectName
     */
    public void update(PnObjectName pnObjectName){
    	pnObjectNameDAO.update(pnObjectName);
    }
    
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnObjectNameService#getObjectNameBySubscriptionId(java.lang.Integer)
	 */
	public String getObjectNameBySubscriptionId(Integer subscriptionId) {
		return pnObjectNameDAO.getObjectNameBySubscriptionId(subscriptionId);
	}
}
