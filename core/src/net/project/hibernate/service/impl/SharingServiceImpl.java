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

import java.math.BigDecimal;

import net.project.hibernate.model.PnShareable;
import net.project.hibernate.service.IPnShareableService;
import net.project.hibernate.service.ISharingService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Vlad Melanchyk
 *
 */
@Service(value="sharingService")
public class SharingServiceImpl implements ISharingService {

	@Autowired
	private IPnShareableService sherableService;
    
	public IPnShareableService getSherableService() {
		return sherableService;
	}

	public void setSherableService(IPnShareableService sherableService) {
		this.sherableService = sherableService;
	}

	private final static Logger LOG = Logger.getLogger(SharingServiceImpl.class);
    
    public void storeShare(Integer objectId, Integer permissionType,
	    Integer containerId, Integer spaceId, Integer propagateToChildren,
	    Integer allowableActions) {
	
	if (LOG.isDebugEnabled()) {
	    LOG.debug("ENTRY OK: storeShare");
	}
	
	try {
	    PnShareable shareExists =  sherableService.getShareable(objectId);
	    if (shareExists != null) {
		shareExists.setPermissionType(permissionType);
		shareExists.setContainerId(containerId);
		shareExists.setSpaceId(spaceId);
		shareExists.setPropagateToChildren(new BigDecimal(propagateToChildren));
		shareExists.setAllowableActions(allowableActions);
		sherableService.updateShareable(shareExists);
	    } else {
		shareExists = new PnShareable();
		shareExists.setObjectId(objectId);
		shareExists.setPermissionType(permissionType);
		shareExists.setContainerId(containerId);
		shareExists.setSpaceId(spaceId);
		shareExists.setPropagateToChildren(new BigDecimal(propagateToChildren));
		shareExists.setAllowableActions(allowableActions);
		sherableService.saveShareable(shareExists);
	    }
	} catch (Exception e) {
	    if (LOG.isDebugEnabled()) {
		LOG.debug("EXIT FAIL: storeShare");
	    }
	    e.printStackTrace();
	}
	
	if (LOG.isDebugEnabled()) {
	    LOG.debug("EXIT OK: storeShare");
	}

    }

}
