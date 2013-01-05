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

import net.project.hibernate.dao.IPnSpaceHasDocSpaceDAO;
import net.project.hibernate.model.PnSpaceHasDocSpace;
import net.project.hibernate.model.PnSpaceHasDocSpacePK;
import net.project.hibernate.service.IPnSpaceHasDocSpaceService;
import net.project.hibernate.service.filters.IPnSpaceHasDocSpaceFilter;

@Service(value="pnSpaceHasDocSpaceService")
public class PnSpaceHasDocSpaceServiceImpl implements IPnSpaceHasDocSpaceService {

	@Autowired
    private IPnSpaceHasDocSpaceDAO pnSpaceHasDocSpaceDAO;

    public void setPnSpaceHasDocSpaceDAO(IPnSpaceHasDocSpaceDAO pnSpaceHasDocSpaceDAO) {
        this.pnSpaceHasDocSpaceDAO = pnSpaceHasDocSpaceDAO;
    }

    public PnSpaceHasDocSpace getSpaceHasDocSpace(PnSpaceHasDocSpacePK pnSpaceHasDocSpaceId) {
        return pnSpaceHasDocSpaceDAO.findByPimaryKey(pnSpaceHasDocSpaceId);
    }

    public PnSpaceHasDocSpacePK saveSpaceHasDocSpace(PnSpaceHasDocSpace pnSpaceHasDocSpace) {
        return pnSpaceHasDocSpaceDAO.create(pnSpaceHasDocSpace);
    }

    public void deleteSpaceHasDocSpace(PnSpaceHasDocSpace pnSpaceHasDocSpace) {
        pnSpaceHasDocSpaceDAO.delete(pnSpaceHasDocSpace);
    }

    public void updateSpaceHasDocSpace(PnSpaceHasDocSpace pnSpaceHasDocSpace) {
        pnSpaceHasDocSpaceDAO.update(pnSpaceHasDocSpace);
    }

    public List<PnSpaceHasDocSpace> findByFilter(IPnSpaceHasDocSpaceFilter filter) {
        List<PnSpaceHasDocSpace> result = new ArrayList();
        List<PnSpaceHasDocSpace> allPnSpaceHasDocSpaces = pnSpaceHasDocSpaceDAO.findAll();
        for (int i = 0, n = allPnSpaceHasDocSpaces.size(); i < n; i++) {
            PnSpaceHasDocSpace currentSpaceHasDocSpace = allPnSpaceHasDocSpaces.get(i);
            boolean needAdd = true;
            if (filter != null)
                needAdd = filter.isSuitable(currentSpaceHasDocSpace);
            if (needAdd)
                result.add(currentSpaceHasDocSpace);
        }
        return result;
    }


}
