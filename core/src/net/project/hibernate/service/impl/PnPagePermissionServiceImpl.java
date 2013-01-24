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

import net.project.hibernate.dao.IPnPagePermissionDAO;
import net.project.hibernate.model.PnPagePermission;
import net.project.hibernate.service.IPnPagePermissionService;

@Service(value="pnPagePermissionService")
public class PnPagePermissionServiceImpl implements IPnPagePermissionService {
	
	@Autowired
    private IPnPagePermissionDAO permissionDAO;

    public IPnPagePermissionDAO getPermissionDAO() {
        return permissionDAO;
    }

    public void setPermissionDAO(IPnPagePermissionDAO permissionDAO) {
        this.permissionDAO = permissionDAO;
    }


    public void deletePermissionByGroupId(Integer groupId) {
        List<PnPagePermission> permissions = permissionDAO.findAll();
        if (permissions != null) {
            for (int i = 0, n = permissions.size(); i < n; i++) {
                PnPagePermission currentPermision = permissions.get(i);
                if (currentPermision.getPnGroup().getGroupId().equals(groupId)) {
                    permissionDAO.delete(currentPermision);
                }
            }
        }

    }

    public void deletePermissionByGroupIdAndSpaceID(Integer groupId, Integer spaceId) {
        List<PnPagePermission> permissions = permissionDAO.findAll();
        if (permissions != null) {
            for (int i = 0, n = permissions.size(); i < n; i++) {
                PnPagePermission currentPermision = permissions.get(i);
                boolean needDelete = ((currentPermision.getPnGroup().getGroupId().equals(groupId)) && (currentPermision.getComp_id().getSpaceId().equals(spaceId)));
                if (needDelete) {
                    permissionDAO.delete(currentPermision);
                }
            }
        }
    }
}
