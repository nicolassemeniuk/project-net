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

import net.project.hibernate.dao.IPnSpaceHasGroupDAO;
import net.project.hibernate.model.PnSpaceHasGroup;
import net.project.hibernate.model.PnSpaceHasGroupPK;
import net.project.hibernate.service.IPnSpaceHasGroupService;
import net.project.hibernate.service.filters.IPnSpaceHasGroupServiceFilter;

@Service(value="pnSpaceHasGroupService")
public class PnSpaceHasGroupServiceImpl implements IPnSpaceHasGroupService {

	@Autowired
    public IPnSpaceHasGroupDAO pnSpaceHasGroupDAO;

    public void setPnSpaceHasGroupDAO(IPnSpaceHasGroupDAO pnSpaceHasGroupDAO) {
        this.pnSpaceHasGroupDAO = pnSpaceHasGroupDAO;
    }

    public PnSpaceHasGroup getSpaceHasGroup(PnSpaceHasGroupPK pnSpaceHasGroupId) {
        return pnSpaceHasGroupDAO.findByPimaryKey(pnSpaceHasGroupId);
    }

    public PnSpaceHasGroupPK saveSpaceHasGroup(PnSpaceHasGroup pnSpaceHasGroup) {
        return pnSpaceHasGroupDAO.create(pnSpaceHasGroup);
    }

    public void deleteSpaceHasGroup(PnSpaceHasGroup pnSpaceHasGroup) {
        pnSpaceHasGroupDAO.delete(pnSpaceHasGroup);
    }

    public void updateSpaceHasGroup(PnSpaceHasGroup pnSpaceHasGroup) {
        pnSpaceHasGroupDAO.update(pnSpaceHasGroup);
    }

    public void deleteByGroupId(Integer groupId) {
        List<PnSpaceHasGroup> permissions = pnSpaceHasGroupDAO.findAll();
        if (permissions != null) {
            for (int i = 0, n = permissions.size(); i < n; i++) {
                PnSpaceHasGroup currentPermision = permissions.get(i);
                if (currentPermision.getPnGroup().getGroupId().equals(groupId)) {
                    deleteSpaceHasGroup(currentPermision);
                }
            }
        }

    }

    public void deleteByGroupIdAndOwner(Integer groupId, long isOwner) {
        List<PnSpaceHasGroup> permissions = pnSpaceHasGroupDAO.findAll();
        if (permissions != null) {
            for (int i = 0, n = permissions.size(); i < n; i++) {
                PnSpaceHasGroup currentPermision = permissions.get(i);
                boolean needDelete = ((currentPermision.getPnGroup().getGroupId().equals(groupId)) && (currentPermision.getIsOwner() == isOwner));
                if (needDelete) {
                    deleteSpaceHasGroup(currentPermision);
                }
            }
        }
    }

    public List<PnSpaceHasGroup> getAll() {
        return pnSpaceHasGroupDAO.findAll();
    }

    public List<PnSpaceHasGroup> getByFilter(IPnSpaceHasGroupServiceFilter filter) {
        List<PnSpaceHasGroup> result = new ArrayList();
        List<PnSpaceHasGroup> all = getAll();
        for (int i = 0, n = all.size(); i < n; i++) {
            PnSpaceHasGroup currentSpaceHasGroup = all.get(i);
            if (filter.isSuitable(currentSpaceHasGroup))
                result.add(currentSpaceHasGroup);
        }
        return result;
    }

}
