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

import net.project.hibernate.dao.IPnDocSpaceHasContainerDAO;
import net.project.hibernate.model.PnDocSpaceHasContainer;
import net.project.hibernate.model.PnDocSpaceHasContainerPK;
import net.project.hibernate.service.IPnDocSpaceHasContainerService;
import net.project.hibernate.service.filters.IPnDocSpaceHasContainerFilter;

@Service(value="pnDocSpaceHasContainerService")
public class PnDocSpaceHasContainerServiceImpl implements IPnDocSpaceHasContainerService {

	@Autowired
    private IPnDocSpaceHasContainerDAO pnDocSpaceHasContainerDAO;

    public void setPnDocSpaceHasContainerDAO(IPnDocSpaceHasContainerDAO pnDocSpaceHasContainerDAO) {
        this.pnDocSpaceHasContainerDAO = pnDocSpaceHasContainerDAO;
    }

    public PnDocSpaceHasContainer getDocSpaceHasContainer(PnDocSpaceHasContainerPK pnDocSpaceHasContainerId) {
        return pnDocSpaceHasContainerDAO.findByPimaryKey(pnDocSpaceHasContainerId);
    }

    public PnDocSpaceHasContainerPK saveDocSpaceHasContainer(PnDocSpaceHasContainer pnDocSpaceHasContainer) {
        return pnDocSpaceHasContainerDAO.create(pnDocSpaceHasContainer);
    }

    public void deleteDocSpaceHasContainer(PnDocSpaceHasContainer pnDocSpaceHasContainer) {
        pnDocSpaceHasContainerDAO.delete(pnDocSpaceHasContainer);
    }

    public void updateDocSpaceHasContainer(PnDocSpaceHasContainer pnDocSpaceHasContainer) {
        pnDocSpaceHasContainerDAO.update(pnDocSpaceHasContainer);
    }

    public List<PnDocSpaceHasContainer> findByFilter(IPnDocSpaceHasContainerFilter filter) {
        List<PnDocSpaceHasContainer> result = new ArrayList();
        List<PnDocSpaceHasContainer> allPnDocSpaceHasContainers = pnDocSpaceHasContainerDAO.findAll();
        for (int i = 0, n = allPnDocSpaceHasContainers.size(); i < n; i++) {
            PnDocSpaceHasContainer currentPnDocSpaceHasContainer = allPnDocSpaceHasContainers.get(i);
            boolean needAdd = true;
            if (filter != null)
                needAdd = filter.isSuitable(currentPnDocSpaceHasContainer);
            if (needAdd)
                result.add(currentPnDocSpaceHasContainer);
        }
        return result;
    }


}
