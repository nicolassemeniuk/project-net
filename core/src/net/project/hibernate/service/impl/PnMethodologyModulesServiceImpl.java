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

import net.project.hibernate.dao.IPnMethodologyModulesDAO;
import net.project.hibernate.dao.IPnModuleDAO;
import net.project.hibernate.model.PnMethodologyModules;
import net.project.hibernate.model.PnMethodologyModulesPK;
import net.project.hibernate.model.PnModule;
import net.project.hibernate.service.IPnMethodologyModulesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "pnMethodologyModulesService")
public class PnMethodologyModulesServiceImpl implements IPnMethodologyModulesService {
	@Autowired
	private IPnMethodologyModulesDAO pnMethodologyModulesDAO;
	@Autowired
	private IPnModuleDAO pnModuleDAO;

	/**
	 * @param pnMethodologyModulesDAO the pnMethodologyModulesDAO to set
	 */
	public void setPnMethodologyModulesDAO(IPnMethodologyModulesDAO pnMethodologyModulesDAO) {
		this.pnMethodologyModulesDAO = pnMethodologyModulesDAO;
	}
	
	/**
	 * @param pnModuleDAO the pnModuleDAO to set
	 */
	public void setPnModuleDAO(IPnModuleDAO pnModuleDAO) {
		this.pnModuleDAO = pnModuleDAO;
	}

	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnMethodologyModulesService#findByMethodology(java.lang.Integer)
	 */
	public List<PnMethodologyModules> findByMethodology(Integer methodologyId) {
        return pnMethodologyModulesDAO.getByMethodology(methodologyId);
	}


	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnMethodologyModulesService#findMethodologyModules(java.lang.Integer)
	 */

	public List<PnModule> findMethodologyModules(Integer methodologyId) {
		List<PnModule> methodologyModules = new ArrayList<PnModule>();
		List<PnMethodologyModules> mms = pnMethodologyModulesDAO.getByMethodology(methodologyId);
		
		for(PnMethodologyModules mm : mms) {
			PnModule module = pnModuleDAO.findByPimaryKey( mm.getComp_id().getModuleId() );
			methodologyModules.add(module);
		}
		
		return methodologyModules;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnMethodologyModulesService#saveOrUpdateMethodologyModules(java.lang.Integer, int[])
	 */
	public void saveOrUpdateMethodologyModules(Integer methodologyId, int[] moduleIds) {
		// clear previous values for specified methodology
		pnMethodologyModulesDAO.delete(methodologyId);
		
		for(int moduleId : moduleIds) {
			PnMethodologyModulesPK record = new PnMethodologyModulesPK(methodologyId, Integer.valueOf(moduleId));
			PnMethodologyModulesPK result = pnMethodologyModulesDAO.create(new PnMethodologyModules(record));
		}
	}

}
