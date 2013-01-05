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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnModulePermissionDAO;
import net.project.hibernate.model.PnModulePermission;
import net.project.hibernate.model.PnModulePermissionPK;
import net.project.hibernate.service.IPnModulePermissionService;
import net.project.hibernate.service.filters.IPnModulePermissionFilter;

@Service(value="pnModulePermissionService")
public class PnModulePermissionServiceImpl implements IPnModulePermissionService {

	@Autowired
    private IPnModulePermissionDAO pnModulePermissionDAO;

    public void setPnModulePermissionDAO(IPnModulePermissionDAO pnModulePermissionDAO) {
        this.pnModulePermissionDAO = pnModulePermissionDAO;
    }

    public PnModulePermission getModulePermission(PnModulePermissionPK pnModulePermissionId) {
        return pnModulePermissionDAO.findByPimaryKey(pnModulePermissionId);
    }

    public PnModulePermissionPK saveModulePermission(PnModulePermission pnModulePermission) {
        return pnModulePermissionDAO.create(pnModulePermission);
    }

    public void deleteModulePermission(PnModulePermission pnModulePermission) {
        pnModulePermissionDAO.delete(pnModulePermission);
    }

    public void updateModulePermission(PnModulePermission pnModulePermission) {
        pnModulePermissionDAO.update(pnModulePermission);
    }
    
    public void deleteModulePermissionsByGroupId(Integer groupId) {
        List<PnModulePermission> permissions = pnModulePermissionDAO.findAll();
        if (permissions != null) {
            for (int i = 0, n = permissions.size(); i < n; i++) {
                PnModulePermission currentPermision = permissions.get(i);
                if (currentPermision.getPnGroup().getGroupId().equals(groupId)) {
                    deleteModulePermission(currentPermision);
                }
            }
        }

    }

    public void deleteModulePermissionsBySpaceAndGroupId(Integer spaceId, Integer groupId) {
        List<PnModulePermission> permissions = pnModulePermissionDAO.findAll();
        if (permissions != null) {
            for (int i = 0, n = permissions.size(); i < n; i++) {
                PnModulePermission currentPermision = permissions.get(i);
                boolean needDelete = ((currentPermision.getPnGroup().getGroupId().equals(groupId)) && (currentPermision.getComp_id().getSpaceId().equals(spaceId)));
                if (needDelete) {
                    deleteModulePermission(currentPermision);
                }
            }
        }
    }

    public List<PnModulePermission> findAll() {
        return pnModulePermissionDAO.findAll();
    }

    @SuppressWarnings("unchecked")
    public List<PnModulePermission> findByFilter(IPnModulePermissionFilter filter) {
        List<PnModulePermission> all = findAll();
        List<PnModulePermission> result = new ArrayList();
        for (int i = 0, n = all.size(); i < n; i++) {
            PnModulePermission currentPermission = all.get(i);
            boolean needAdd = true;
            if (filter != null)
                needAdd = filter.isSuitable(currentPermission);
            if (needAdd)
                result.add(currentPermission);
        }
        return result;
    }
    
    public List<PnModulePermission> getModulePermissionsBySpaceAndModule(Integer spaceId, Integer moduleId) {
	return pnModulePermissionDAO.getModulePermissionsBySpaceAndModule(spaceId, moduleId);
    }

}
