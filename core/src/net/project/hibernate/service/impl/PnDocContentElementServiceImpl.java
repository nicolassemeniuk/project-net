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

import net.project.hibernate.dao.IPnDocContentElementDAO;
import net.project.hibernate.model.PnDocContentElement;
import net.project.hibernate.service.IPnDocContentElementService;
import net.project.hibernate.service.filters.IPnDocContentElementFilter;


/**
 * Created by IntelliJ IDEA.
 * User: Oleg
 * Date: 31.05.2007
 * Time: 23:17:42
 * To change this template use File | Settings | File Templates.
 */

@Service(value="pnDocContentElementService")
public class PnDocContentElementServiceImpl implements IPnDocContentElementService {
    public IPnDocContentElementDAO getDao() {
        return dao;
    }

    public void setDao(IPnDocContentElementDAO dao) {
        this.dao = dao;
    }

    @Autowired
    private IPnDocContentElementDAO dao;

    public void saveObject(PnDocContentElement object) {
        dao.create(object);
    }

    public List<PnDocContentElement> findByFilter(IPnDocContentElementFilter filter) {
        List<PnDocContentElement> result = new ArrayList();
        List<PnDocContentElement> all = dao.findAll();
        for (int i = 0, n = all.size(); i < n; i++) {
            PnDocContentElement contentElement = all.get(i);
            boolean needAdd = true;
            if (filter != null)
                needAdd = filter.isSuitable(contentElement);
            if (needAdd)
                result.add(contentElement);
        }
        return result;
    }

    public PnDocContentElement findByPK(Integer pk) {
        return dao.findByPimaryKey(pk);
    }
}
