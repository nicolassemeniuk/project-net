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

import net.project.hibernate.dao.IPnDocVersionDAO;
import net.project.hibernate.model.PnDocVersion;
import net.project.hibernate.service.IPnDocVersionService;
import net.project.hibernate.service.filters.IPnDocVersionFilter;

@Service(value="pnDocVersionService")
public class PnDocVersionServiceImpl implements IPnDocVersionService {
	
	@Autowired
    private IPnDocVersionDAO dao;

    public IPnDocVersionDAO getDao() {
        return dao;
    }

    public void setDao(IPnDocVersionDAO dao) {
        this.dao = dao;
    }

    public List<PnDocVersion> findByFilter(IPnDocVersionFilter filter) {
        List<PnDocVersion> result = new ArrayList();
        List<PnDocVersion> all = dao.findAll();
        for (int i = 0, n = all.size(); i < n; i++) {
            PnDocVersion docVersion = all.get(i);
            boolean needadd = true;
            if (filter != null)
                needadd = filter.isSuitable(docVersion);
            if (needadd)
                result.add(docVersion);
        }
        return result;
    }

    public void update(PnDocVersion object) {
        dao.update(object);
    }

    public PnDocVersion findByPk(Integer pk) {
        return dao.findByPimaryKey(pk);
    }

    public void save(PnDocVersion object) {
        dao.create(object);
    }
}
