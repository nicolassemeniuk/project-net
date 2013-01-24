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

import net.project.hibernate.dao.IPnDocVersionHasContentDAO;
import net.project.hibernate.model.PnDocVersionHasContent;
import net.project.hibernate.service.IPnDocVersionHasContentService;
import net.project.hibernate.service.filters.IPnDocVersionHasContentFilter;

@Service(value="pnDocVersionHasContentService")
public class PnDocVersionHasContentServiceImpl implements IPnDocVersionHasContentService {
	
	@Autowired
    private IPnDocVersionHasContentDAO dao;

    public IPnDocVersionHasContentDAO getDao() {
        return dao;
    }

    public void setDao(IPnDocVersionHasContentDAO dao) {
        this.dao = dao;
    }

    public void saveObvect(PnDocVersionHasContent object) {
        dao.create(object);
    }

    public List<PnDocVersionHasContent> findByFilter(IPnDocVersionHasContentFilter filter) {
        List<PnDocVersionHasContent> result = new ArrayList();
        List<PnDocVersionHasContent> all = dao.findAll();
        for (int i = 0, n = all.size(); i < n; i++) {
            PnDocVersionHasContent currentPnDocVersionHasContent = all.get(i);
            boolean needAdd = true;
            if (filter != null)
                needAdd = filter.isSuitable(currentPnDocVersionHasContent);
            if (needAdd)
                result.add(currentPnDocVersionHasContent);
        }
        return result;
    }
}
