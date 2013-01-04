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

import net.project.hibernate.dao.IPnDocContainerDAO;
import net.project.hibernate.model.PnDocContainer;
import net.project.hibernate.service.IPnDocContainerService;
import net.project.hibernate.service.filters.IPnDocContainerFilter;

@Service(value="pnDocContainerService")
public class PnDocContainerServiceImpl implements IPnDocContainerService {

	@Autowired
    private IPnDocContainerDAO pnDocContainerDAO;

    public void setPnDocContainerDAO(IPnDocContainerDAO pnDocContainerDAO) {
        this.pnDocContainerDAO = pnDocContainerDAO;
    }

    public PnDocContainer getDocContainer(Integer docContainerId) {
        return pnDocContainerDAO.findByPimaryKey(docContainerId);
    }

    public Integer saveDocContainer(PnDocContainer pnDocContainer) {
        return pnDocContainerDAO.create(pnDocContainer);
    }

    public void deleteDocContainer(PnDocContainer pnDocContainer) {
        pnDocContainerDAO.delete(pnDocContainer);
    }

    public void updateDocContainer(PnDocContainer pnDocContainer) {
        pnDocContainerDAO.update(pnDocContainer);
    }

    public List<PnDocContainer> findByFilter(IPnDocContainerFilter filter) {
        List<PnDocContainer> result = new ArrayList();
        List<PnDocContainer> all = pnDocContainerDAO.findAll();
        for (int i = 0, n = all.size(); i < n; i++) {
            PnDocContainer container = all.get(i);
            boolean needAdd = true;
            if (filter != null)
                needAdd = filter.isSuitable(container);
            if (needAdd)
                result.add(container);
        }
        return result;
    }
    
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnDocContainerService#getDocContainerWithRecordStatus(java.lang.Integer)
	 */
	public PnDocContainer getDocContainerWithRecordStatus(Integer docContainerId) {
		return pnDocContainerDAO.getDocContainerWithRecordStatus(docContainerId);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnDocContainerService#getDocContainerWithIsHidden(java.lang.Integer)
	 */
	public PnDocContainer getDocContainerWithIsHidden(Integer documentId) {
		return pnDocContainerDAO.getDocContainerWithIsHidden(documentId);
	}
}
