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

import net.project.hibernate.dao.IPnDocumentDAO;
import net.project.hibernate.model.PnDocument;
import net.project.hibernate.service.IPnDocumentService;
import net.project.hibernate.service.filters.IPnDocumentFilter;

@Service(value="pnDocumentService")
public class PnDocumentServiceImpl implements IPnDocumentService {
	
	@Autowired
    private IPnDocumentDAO dao;

    public IPnDocumentDAO getDao() {
        return dao;
    }

    public void setDao(IPnDocumentDAO dao) {
        this.dao = dao;
    }

    public List<PnDocument> findByFilter(IPnDocumentFilter filter) {
        List<PnDocument> result = new ArrayList();
        List<PnDocument> all = dao.findAll();
        for (int i = 0, n = all.size(); i < n; i++) {
            PnDocument pnDocument = all.get(i);
            boolean needAdd = true;
            if (filter != null)
                needAdd = filter.isSuitable(pnDocument);
            if (needAdd)
                result.add(pnDocument);
        }
        return result;
    }

    public PnDocument findByPk(Integer pk) {
        return dao.findByPimaryKey(pk);
    }

    public void update(PnDocument object) {
        dao.update(object);
    }

    public void save(PnDocument object) {
        dao.create(object);
    }
        
    public PnDocument getDocumentDetailsById(Integer docId) {
    	return dao.getDocumentDetailsById(docId);
    }
}
