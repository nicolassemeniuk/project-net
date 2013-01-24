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

import net.project.hibernate.dao.IPnObjectDAO;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.IPnObjectService;

@Service(value="pnObjectService")
public class PnObjectServiceImpl implements IPnObjectService {


    /**
     * PnObject database access object
     */
	@Autowired
    private IPnObjectDAO pnObjectDAO;

    /**
     * Sets the PnObject data access object
     *
     * @param pnObjectDao
     */
    public void setPnObjectDAO(IPnObjectDAO pnObjectDAO) {
        this.pnObjectDAO = pnObjectDAO;
    }

    /**
     * get PnObject bean by primary key
     */
    public PnObject getObject(Integer primaryKey) {
        return pnObjectDAO.findByPimaryKey(primaryKey);
    }

    public PnObject getObjectWithProjectSpace(Integer primaryKey) {
    	PnObject pnObject = this.getObject(primaryKey);
    	
    	if (pnObject != null) {
    		pnObject.getPnProjectSpace();
    	}
    	
		return pnObject;
	}

	/**
     * create new object
     */
    public Integer saveObject(PnObject pnObject) {
        return pnObjectDAO.create(pnObject);
    }

    public Integer generateNewId() {
        return pnObjectDAO.generateNewId();
    }
    
    /* method added for bypassing lazy initialization exception when retreiving object by object id */
    public PnObject getObjectWithAssignedWikiPage(Integer primaryKey) {
    	PnObject pnObject = this.getObject(primaryKey);
    	
    	if (pnObject != null) {
    		PnWikiPage wikiPage = pnObject.getAssignedWikiPage();
    		
    		if (wikiPage != null) {
    			wikiPage.getEditedBy();
    			wikiPage.getOwnerObjectId();
    		}
    	}
    	
		return pnObject;
    }
    
    public PnObject getObjectByObjectId(Integer objectId){
    	return pnObjectDAO.getObjectByObjectId(objectId);
    }
}
