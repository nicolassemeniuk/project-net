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

import net.project.hibernate.dao.IPnSpaceHasSpaceDAO;
import net.project.hibernate.model.PnSpaceHasSpace;
import net.project.hibernate.service.IPnSpaceHasSpaceService;
import net.project.hibernate.service.filters.IPnSpaceHasSpaceFilter;

/**
 * Created by IntelliJ IDEA.
 * User: Oleg
 * Date: 19.05.2007
 * Time: 15:59:53
 * To change this template use File | Settings | File Templates.
 */
@Service(value="pnSpaceHasSpaceService")
public class PnSpaceHasSpaceServiceImpl implements IPnSpaceHasSpaceService {
	
	@Autowired
    private IPnSpaceHasSpaceDAO pnSpaceHasSpaceDAO;

    public IPnSpaceHasSpaceDAO getPnSpaceHasSpaceDAO() {
        return pnSpaceHasSpaceDAO;
    }

    public void setPnSpaceHasSpaceDAO(IPnSpaceHasSpaceDAO pnSpaceHasSpaceDAO) {
        this.pnSpaceHasSpaceDAO = pnSpaceHasSpaceDAO;
    }


    public List<PnSpaceHasSpace> findByFilter(IPnSpaceHasSpaceFilter filter) {
        List<PnSpaceHasSpace> all = getPnSpaceHasSpaceDAO().findAll();
        List<PnSpaceHasSpace> result = new ArrayList();
        for (int i = 0, n = all.size(); i < n; i++) {
            PnSpaceHasSpace currentSpaceHasSpace = all.get(i);
            boolean needAdd = true;
            if (filter != null)
                needAdd = filter.isSuitable(currentSpaceHasSpace);
            if (needAdd)
                result.add(currentSpaceHasSpace);
        }
        return result;
    }

	@Override
	public PnSpaceHasSpace getFinancialRelatedSpace(Integer spaceID) {
		return pnSpaceHasSpaceDAO.getRelatedFinancialSpace(spaceID);
	}

	@Override
	public Integer getParentSpaceID(Integer spaceID) {
		return pnSpaceHasSpaceDAO.getParentSpaceID(spaceID);
	}
}
