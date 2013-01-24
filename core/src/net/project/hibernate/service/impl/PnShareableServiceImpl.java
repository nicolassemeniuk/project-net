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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnShareableDAO;
import net.project.hibernate.model.PnShareable;
import net.project.hibernate.service.IPnShareableService;

/**
 * @author Vlad Melanchyk
 *
 */
@Service(value="pnShareableService")
public class PnShareableServiceImpl implements IPnShareableService {
    
	@Autowired
    private IPnShareableDAO pnShareableDAO;

    public PnShareable getShareable(Integer objectId) {
	return pnShareableDAO.findByPimaryKey(objectId);
    }
    
    public void updateShareable(PnShareable pnShareable) {
        pnShareableDAO.update(pnShareable);
    }
    
    public Integer saveShareable(PnShareable pnShareable) {
        return pnShareableDAO.create(pnShareable);
    }

    public final void setPnShareableDAO(IPnShareableDAO pnShareableDAO) {
        this.pnShareableDAO = pnShareableDAO;
    }

}
