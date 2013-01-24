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

import net.project.hibernate.dao.IPnSpaceHasModuleDAO;
import net.project.hibernate.model.PnSpaceHasModule;
import net.project.hibernate.model.PnSpaceHasModulePK;
import net.project.hibernate.service.IPnSpaceHasModuleService;

@Service(value="pnSpaceHasModuleService")
public class PnSpaceHasModuleServiceImpl implements IPnSpaceHasModuleService {

	@Autowired
    private IPnSpaceHasModuleDAO pnSpaceHasModuleDAO;

    public void setPnSpaceHasModuleDAO(IPnSpaceHasModuleDAO pnSpaceHasModuleDAO) {
        this.pnSpaceHasModuleDAO = pnSpaceHasModuleDAO;
    }

    public PnSpaceHasModule getSpaceHasModule(PnSpaceHasModulePK pnSpaceHasModulePK) {
        return pnSpaceHasModuleDAO.findByPimaryKey(pnSpaceHasModulePK);
    }

    public PnSpaceHasModulePK saveSpaceHasModule(PnSpaceHasModule pnSpaceHasModule) {
        return pnSpaceHasModuleDAO.create(pnSpaceHasModule);
    }

    public void deleteSpaceHasModule(PnSpaceHasModule pnSpaceHasModule) {
        pnSpaceHasModuleDAO.delete(pnSpaceHasModule);
    }

    public void updateSpaceHasModule(PnSpaceHasModule pnSpaceHasModule) {
        pnSpaceHasModuleDAO.update(pnSpaceHasModule);
    }

    public List<PnSpaceHasModule> findAll() {
        return pnSpaceHasModuleDAO.findAll();
    }

    public List<PnSpaceHasModule> findBySpaceId(Integer spaceId) {
        List<PnSpaceHasModule> result = new ArrayList();
        List<PnSpaceHasModule> all = findAll();
        for (int i = 0, n = all.size(); i < n; i++) {
            PnSpaceHasModule spaceHasModule = all.get(i);
            if (spaceHasModule.getComp_id().getSpaceId().equals(spaceId)) {
                result.add(spaceHasModule);
            }
        }
        return result;
    }
}