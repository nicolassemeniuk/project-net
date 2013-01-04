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

import java.util.Date;
import java.util.Set;

import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnObjectType;
import net.project.hibernate.service.IBaseService;
import net.project.hibernate.service.IPnObjectService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="pnBaseService")
public class BaseServiceImpl implements IBaseService {

	private final static Logger LOG = Logger.getLogger(BaseServiceImpl.class);

	@Autowired
	private IPnObjectService pnObjectService;

	/**
	 * @param pnObjectService The pnObjectService to set.
	 */
	public void setPnObjectService(IPnObjectService pnObjectService) {
		this.pnObjectService = pnObjectService;
	}

	public PnObjectType getObjectType(Integer pnObjectId) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("ENTRY OK: getObjectType");
		}
		PnObjectType result = null;
		try {
			PnObject object = pnObjectService.getObject(pnObjectId);
			if (object != null)
				result = object.getPnObjectType();
		} catch (Exception e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("EXIT FAIL: getObjectType");
			}
			e.printStackTrace();
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("EXIT OK: getObjectType");
		}
		return result;
	}

	public Set getDocContainersForObject(Integer pnObjectId) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("ENTRY OK: getDocContainersForObject");
		}
		Set result = null;
		try {
			PnObject object = pnObjectService.getObject(pnObjectId);
			if (object != null)
				result = object.getPnDocContainerHasObjects();
		} catch (Exception e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("EXIT FAIL: getDocContainersForObject");
			}
			e.printStackTrace();
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("EXIT OK: getDocContainersForObject");
		}
		return result;
	}

	// Vlad fixed that method
	public Integer createObject(String objectType, Integer creatorPersonId, String recordStatus) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("ENTRY OK: createObject");
		}
		Integer result = null;
		try {
			// Get new object Id for the sequence generator
			Integer objectId = pnObjectService.generateNewId();
			// Create new PnObject
			PnObject object = new PnObject(objectType, creatorPersonId, new Date(System.currentTimeMillis()), recordStatus);
			object.setObjectId(objectId);
			result = pnObjectService.saveObject(object);
		} catch (Exception e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("EXIT FAIL: createObject");
			}
			e.printStackTrace();
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("EXIT OK: createObject");
		}
		return result;
	}

}
