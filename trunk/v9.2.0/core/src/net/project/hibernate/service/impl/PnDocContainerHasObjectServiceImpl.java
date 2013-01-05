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

import net.project.hibernate.dao.IPnDocContainerHasObjectDAO;
import net.project.hibernate.model.PnDocContainerHasObject;
import net.project.hibernate.service.IPnDocContainerHasObjectService;
import net.project.hibernate.service.filters.IPnDocContainerHasObjectFilter;

/**
 * Created by IntelliJ IDEA.
 * User: Oleg
 * Date: 24.05.2007
 * Time: 23:33:25
 * To change this template use File | Settings | File Templates.
 */

@Service(value="pnDocContainerHasObjectService")
public class PnDocContainerHasObjectServiceImpl implements IPnDocContainerHasObjectService {
	
	@Autowired
    private IPnDocContainerHasObjectDAO dao;

    public IPnDocContainerHasObjectDAO getDao() {
        return dao;
    }

    public void setDao(IPnDocContainerHasObjectDAO dao) {
        this.dao = dao;
    }

    public List<PnDocContainerHasObject> findByFilter(IPnDocContainerHasObjectFilter filter) {
        List<PnDocContainerHasObject> result = new ArrayList();
        List<PnDocContainerHasObject> all = dao.findAll();
        for (int i = 0, n = all.size(); i < n; i++) {
            PnDocContainerHasObject currentPnDocContainerHasObject = all.get(i);
            boolean needAdd = true;
            if (filter != null)
                needAdd = filter.isSuitable(currentPnDocContainerHasObject);
            if (needAdd)
                result.add(currentPnDocContainerHasObject);
        }
        return result;
    }

    public void save(PnDocContainerHasObject object) {
        dao.create(object);
    }

    public void deleteByFilter(IPnDocContainerHasObjectFilter filter) {
        List<PnDocContainerHasObject> result = new ArrayList();
        List<PnDocContainerHasObject> all = dao.findAll();
        for (int i = 0, n = all.size(); i < n; i++) {
            PnDocContainerHasObject currentPnDocContainerHasObject = all.get(i);
            boolean needDelete = true;
            if (filter != null)
                needDelete = filter.isSuitable(currentPnDocContainerHasObject);
            if (needDelete)
                result.add(currentPnDocContainerHasObject);
        }
        for (int i = 0, n = result.size(); i < n; i++) {
            PnDocContainerHasObject currentPnDocContainerHasObject = result.get(i);
            dao.delete(currentPnDocContainerHasObject);
        }
    }
}
