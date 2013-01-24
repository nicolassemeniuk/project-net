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

import net.project.hibernate.dao.IPnObjectSpaceDAO;
import net.project.hibernate.model.PnObjectSpace;
import net.project.hibernate.model.PnObjectSpacePK;
import net.project.hibernate.service.IPnObjectSpaceService;
import net.project.hibernate.service.filters.IPnObjectSpaceServiceFilter;

@Service(value="pnObjectSpaceService")
public class PnObjectSpaceServiceImpl implements IPnObjectSpaceService {
	
	@Autowired
    private IPnObjectSpaceDAO spaceDAO;

    public IPnObjectSpaceDAO getSpaceDAO() {
        return spaceDAO;
    }

    public void setSpaceDAO(IPnObjectSpaceDAO spaceDAO) {
        this.spaceDAO = spaceDAO;
    }


    public List<PnObjectSpace> getByFilter(IPnObjectSpaceServiceFilter filter) {
        List result = new ArrayList();
        List<PnObjectSpace> all = getSpaceDAO().findAll();
        for (int i = 0, n = all.size(); i < n; i++) {
            PnObjectSpace currentSpace = all.get(i);
            boolean needAdd = true;
            if (filter != null)
                needAdd = filter.isSuitable(currentSpace);
            if (needAdd)
                result.add(currentSpace);
        }
        return result;
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnObjectSpaceService#save(net.project.hibernate.model.PnObjectSpace)
     */
    public PnObjectSpacePK save(PnObjectSpace pnObjectSpace) {
    	return spaceDAO.create(pnObjectSpace);
    }
}
