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

import net.project.hibernate.dao.IPnModuleDAO;
import net.project.hibernate.model.PnModule;
import net.project.hibernate.service.IPnModuleService;

@Service(value="pnModuleService")
public class PnModuleServiceImpl implements IPnModuleService {

	@Autowired
    private IPnModuleDAO pnModuleDAO;

    public void setPnModuleDAO(IPnModuleDAO pnModuleDAO) {
        this.pnModuleDAO = pnModuleDAO;
    }

    public PnModule getModule(Integer moduleId) {
        return pnModuleDAO.findByPimaryKey(moduleId);
    }

    public Integer saveModule(PnModule pnModule) {
        return pnModuleDAO.create(pnModule);
    }

    public void deleteModule(PnModule pnModule) {
        pnModuleDAO.delete(pnModule);

    }

    public void updateModule(PnModule pnModule) {
        pnModuleDAO.update(pnModule);

    }

    public List<PnModule> getModuleIds() {
        return pnModuleDAO.getModuleIds();
    }

    public List<PnModule> getModuleDefaultPermissions(Integer spaceId) {
        return pnModuleDAO.getModuleDefaultPermissions(spaceId);
    }

    public List<PnModule> getAll() {
        return pnModuleDAO.findAll();
    }
    
    public List<PnModule> getModulesForSpace(Integer spaceId) {
        return pnModuleDAO.getModulesForSpace(spaceId);
    }
    
}
