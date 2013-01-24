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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnDocBySpaceViewDAO;
import net.project.hibernate.dao.IPnPortfolioViewDAO;
import net.project.hibernate.model.PnDocBySpaceView;
import net.project.hibernate.service.IPnDocBySpaceViewService;
import net.project.hibernate.service.filters.IPnDocBySpaceViewFilter;

@Service(value="pnDocBySpaceViewService")
public class PnDocBySpaceViewServiceImpl implements IPnDocBySpaceViewService {

	@Autowired
	private IPnDocBySpaceViewDAO pnDocBySpaceViewDAO;
	
	/**
	 * Method to set pnDocBySpaceViewDAO
	 * 
	 * @param pnDocBySpaceViewDAO 
	 */
	public void setPnDocBySpaceViewDAO(IPnDocBySpaceViewDAO pnDocBySpaceViewDAO) {
		this.pnDocBySpaceViewDAO = pnDocBySpaceViewDAO;
	}


	public List<PnDocBySpaceView> findByFilter(IPnDocBySpaceViewFilter filter) {
        List<PnDocBySpaceView> result = new ArrayList();
        List<PnDocBySpaceView> all = pnDocBySpaceViewDAO.findAll();
        for (int i = 0, n = all.size(); i < n; i++) {
            PnDocBySpaceView currentPnDocBySpaceView = all.get(i);
            boolean needAdd = true;
            if (filter != null)
                needAdd = filter.isSuitable(currentPnDocBySpaceView);
            if (needAdd)
                result.add(currentPnDocBySpaceView);
        }
        return result;
    }

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnDocBySpaceViewService#getPnDocBySpaceView(java.lang.Integer)
	 */
	public PnDocBySpaceView getPnDocBySpaceView(Integer docId) {
		return pnDocBySpaceViewDAO.getPnDocBySpaceView(docId);
	}
}
