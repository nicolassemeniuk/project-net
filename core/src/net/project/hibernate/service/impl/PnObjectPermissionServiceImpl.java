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

import net.project.hibernate.dao.IPnObjectPermissionDAO;
import net.project.hibernate.model.PnObjectPermission;
import net.project.hibernate.model.PnObjectPermissionPK;
import net.project.hibernate.service.IPnObjectPermissionService;

@Service(value="pnObjectPermissionService")
public class PnObjectPermissionServiceImpl implements IPnObjectPermissionService {

	@Autowired
	private IPnObjectPermissionDAO pnObjectPermissionDAO;

	public void setPnObjectPermissionDAO(IPnObjectPermissionDAO pnObjectPermissionDAO) {
		this.pnObjectPermissionDAO = pnObjectPermissionDAO;
	}

	public PnObjectPermission getObjectPermission(PnObjectPermissionPK pnObjectPermissionId) {
		return pnObjectPermissionDAO.findByPimaryKey(pnObjectPermissionId);
	}

	public PnObjectPermissionPK saveObjectPermission(PnObjectPermission pnObjectPermission) {
		return pnObjectPermissionDAO.create(pnObjectPermission);
	}

	public void deleteObjectPermission(PnObjectPermission pnObjectPermission) {
		pnObjectPermissionDAO.delete(pnObjectPermission);
	}

	public void updateObjectPermission(PnObjectPermission pnObjectPermission) {
		pnObjectPermissionDAO.update(pnObjectPermission);
	}

	public void deleteObjectPermissionsForGroup(Integer groupId) {
		List<PnObjectPermission> permissions = pnObjectPermissionDAO.getObjectPermissionsForGroup(groupId);
		for (PnObjectPermission pnObjectPermission : permissions) {
		    deleteObjectPermission(pnObjectPermission);
		}
	}

	public List<PnObjectPermission> getAll() {
		return pnObjectPermissionDAO.findAll();
	}

	public void deleteObjectPermissionsForObject(Integer objectId) {
		List<PnObjectPermission> permissions = pnObjectPermissionDAO.getObjectPermissionsForObject(objectId);
		for (PnObjectPermission pnObjectPermission : permissions) {
		    deleteObjectPermission(pnObjectPermission);
		}
	}

	public List<PnObjectPermission> getObjectPermissionsForGroup(Integer groupId) {
		return pnObjectPermissionDAO.getObjectPermissionsForGroup(groupId);
	}
	
	public List<PnObjectPermission> getObjectPermissionsForObject (Integer objectId) {
	    return pnObjectPermissionDAO.getObjectPermissionsForObject(objectId);
	}

}
