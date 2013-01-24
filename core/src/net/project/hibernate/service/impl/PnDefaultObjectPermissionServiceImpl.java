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

import net.project.hibernate.dao.IPnDefaultObjectPermissionDAO;
import net.project.hibernate.model.PnDefaultObjectPermission;
import net.project.hibernate.model.PnDefaultObjectPermissionPK;
import net.project.hibernate.service.IPnDefaultObjectPermissionService;
import net.project.hibernate.service.filters.IPnDefaultObjectPermissionFilter;

@Service(value="pnDefaultObjectPermissionService")
public class PnDefaultObjectPermissionServiceImpl implements IPnDefaultObjectPermissionService {

	@Autowired
    public IPnDefaultObjectPermissionDAO pnDefaultObjectPermissionDAO;

    public void setPnDefaultObjectPermissionDAO(IPnDefaultObjectPermissionDAO pnDefaultObjectPermissionDAO) {
        this.pnDefaultObjectPermissionDAO = pnDefaultObjectPermissionDAO;
    }

    public PnDefaultObjectPermission getDefaultObjectPermission(PnDefaultObjectPermissionPK pnDefaultObjectPermissionId) {
        return pnDefaultObjectPermissionDAO.findByPimaryKey(pnDefaultObjectPermissionId);
    }

    public PnDefaultObjectPermissionPK saveDefaultObjectPermission(PnDefaultObjectPermission pnDefaultObjectPermission) {
        return pnDefaultObjectPermissionDAO.create(pnDefaultObjectPermission);
    }

    public void deleteDefaultObjectPermission(PnDefaultObjectPermission pnDefaultObjectPermission) {
        pnDefaultObjectPermissionDAO.delete(pnDefaultObjectPermission);
    }

    public void updateDefaultObjectPermission(PnDefaultObjectPermission pnDefaultObjectPermission) {
        pnDefaultObjectPermissionDAO.update(pnDefaultObjectPermission);
    }

    public List<PnDefaultObjectPermission> getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup(
	    Integer spaceId, String objectType) {
        return pnDefaultObjectPermissionDAO.getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup(spaceId, objectType);
    }

    public List<PnDefaultObjectPermission> getDefaultObjectPermissionsBySpaceAndObjectTypeForSystemNonPrincipalGroup(
	    Integer spaceId, String objectType) {
	return pnDefaultObjectPermissionDAO.getDefaultObjectPermissionsBySpaceAndObjectTypeForSystemNonPrincipalGroup(spaceId, objectType);
    }

    public void deleteDefaultObjectPermisionsByGroupId(Integer groupId) {
        List<PnDefaultObjectPermission> permissions = pnDefaultObjectPermissionDAO.findAll();
        if (permissions != null) {
            for (int i = 0, n = permissions.size(); i < n; i++) {
                PnDefaultObjectPermission currentPermision = permissions.get(i);
                if (currentPermision.getPnGroup().getGroupId().equals(groupId)) {
                    deleteDefaultObjectPermission(currentPermision);
                }
            }
        }
    }

    public void deleteDefaultObjectPermisionsByGroupIdAndSpaceID(Integer groupId, Integer spaceId) {
        List<PnDefaultObjectPermission> permissions = pnDefaultObjectPermissionDAO.findAll();
        if (permissions != null) {
            for (int i = 0, n = permissions.size(); i < n; i++) {
                PnDefaultObjectPermission currentPermision = permissions.get(i);
                boolean needDelete = ((currentPermision.getPnGroup().getGroupId().equals(groupId)) && (currentPermision.getComp_id().getSpaceId().equals(spaceId)));
                if (needDelete) {
                    deleteDefaultObjectPermission(currentPermision);
                }
            }
        }

    }

    public List<PnDefaultObjectPermission> getAll() {
        return pnDefaultObjectPermissionDAO.findAll();
    }

    @SuppressWarnings("unchecked")
    public List<PnDefaultObjectPermission> getByFilter(IPnDefaultObjectPermissionFilter filter) {
        List<PnDefaultObjectPermission> all = getAll();
        List result = new ArrayList();
        for (int i = 0, n = all.size(); i < n; i++) {
            PnDefaultObjectPermission currentPnDefaultObjectPermission = all.get(i);
            boolean needAdd = true;
            if (filter != null)
                needAdd = filter.isSuitable(currentPnDefaultObjectPermission);
            if (needAdd)
                result.add(currentPnDefaultObjectPermission);
        }
        return result;
    }

}
