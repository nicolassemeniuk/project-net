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

import net.project.hibernate.dao.IPnGroupDAO;
import net.project.hibernate.model.PnGroup;
import net.project.hibernate.service.IPnGroupService;

@Service(value="pnGroupService")
public class PnGroupServiceImpl implements IPnGroupService {

	@Autowired
	private IPnGroupDAO pnGroupDAO;

	public void setPnGroupDAO(IPnGroupDAO pnGroupDAO) {
		this.pnGroupDAO = pnGroupDAO;
	}

	public PnGroup getGroup(Integer groupId) {
		return pnGroupDAO.findByPimaryKey(groupId);
	}

	public Integer saveGroup(PnGroup pnGroup) {
		return pnGroupDAO.create(pnGroup);
	}

	public void deleteGroup(PnGroup pnGroup) {
		pnGroupDAO.delete(pnGroup);
	}

	public void updateGroup(PnGroup pnGroup) {
		pnGroupDAO.update(pnGroup);
	}

	public List<PnGroup> getGroupForSpaceAndGroupType(Integer spaceId, Integer groupTypeId) {
		return pnGroupDAO.getGroupForSpaceAndGroupType(spaceId, groupTypeId);
	}

	public PnGroup getPrincipalGroupForSpaceAndPerson(Integer spaceId, Integer personId) {
	    	return pnGroupDAO.getPrincipalGroupForSpaceAndPerson(spaceId, personId);
	}

}
