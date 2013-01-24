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

import net.project.hibernate.dao.IPnObjectLinkDAO;
import net.project.hibernate.model.PnObjectLink;
import net.project.hibernate.model.PnObjectLinkPK;
import net.project.hibernate.service.IPnObjectLinkService;
import net.project.hibernate.service.filters.IPnObjectLinkFilter;

@Service(value="pnObjectLinkService")
public class PnObjectLinkServiceImpl implements IPnObjectLinkService {
	
	@Autowired
    private IPnObjectLinkDAO pnObjectLinkDAO;

    

    public void setPnObjectLinkDAO(IPnObjectLinkDAO pnObjectLinkDAO) {
		this.pnObjectLinkDAO = pnObjectLinkDAO;
	}



	public List<PnObjectLink> findByFilter(IPnObjectLinkFilter filter) {
        List<PnObjectLink> result = new ArrayList<PnObjectLink>();
        List<PnObjectLink> all = pnObjectLinkDAO.findAll();
        for (int i = 0, n = all.size(); i < n; i++) {
            PnObjectLink currentLink = all.get(i);
            boolean needAdd = true;
            if (filter != null)
                needAdd = filter.isSuitable(currentLink);
            if (needAdd)
                result.add(currentLink);
        }
        return result;
    }


	public List<PnObjectLink> getObjectsById(Integer parentId, Integer contextId) {
		return pnObjectLinkDAO.getObjectsById(parentId, contextId);
	}



	public PnObjectLinkPK saveObjectLink(PnObjectLink pnObjectLink) {
		return pnObjectLinkDAO.create(pnObjectLink);
	}



	public void saveOrUpdateObjectLink(PnObjectLink pnObjectLink) {
		pnObjectLinkDAO.createOrUpdate(pnObjectLink);
	}
	
	public List<PnObjectLink> getObjectLinksByParent(Integer fromObjectId) {
		return pnObjectLinkDAO.getObjectLinksByParent(fromObjectId);
	}
	
	public List<PnObjectLink> getObjectLinksByChild(Integer toObjectId){
		return pnObjectLinkDAO.getObjectLinksByChild(toObjectId);
	}
	
}
