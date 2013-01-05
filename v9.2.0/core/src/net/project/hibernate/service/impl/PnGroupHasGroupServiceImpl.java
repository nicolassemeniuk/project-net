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

import net.project.hibernate.dao.IPnGroupHasGroupDAO;
import net.project.hibernate.model.PnGroupHasGroup;
import net.project.hibernate.service.IPnGroupHasGroupService;

@Service(value="pnGroupHasGroupService")
public class PnGroupHasGroupServiceImpl implements IPnGroupHasGroupService {
	
	@Autowired
    private IPnGroupHasGroupDAO pnGroupHasGroupDAO;

    public IPnGroupHasGroupDAO getPnGroupHasGroupDAO() {
        return pnGroupHasGroupDAO;
    }

    public void setPnGroupHasGroupDAO(IPnGroupHasGroupDAO pnGroupHasGroupDAO) {
        this.pnGroupHasGroupDAO = pnGroupHasGroupDAO;
    }

    public void deleteByGroupId(Integer groupId) {
        List<PnGroupHasGroup> permissions = pnGroupHasGroupDAO.findAll();
        if (permissions != null) {
            for (int i = 0, n = permissions.size(); i < n; i++) {
                PnGroupHasGroup currentPermision = permissions.get(i);
                boolean needDelete = ((currentPermision.getPnGroupByGroupId().getGroupId().equals(groupId)) || (currentPermision.getPnGroupByMemberGroupId().getGroupId().equals(groupId)));
                if (needDelete) {
                    getPnGroupHasGroupDAO().delete(currentPermision);
                }
            }
        }

    }
}
