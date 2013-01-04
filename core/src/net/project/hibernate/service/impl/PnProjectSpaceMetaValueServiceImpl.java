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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnProjectSpaceMetaValueDAO;
import net.project.hibernate.model.PnProjectSpaceMetaCombo;
import net.project.hibernate.model.PnProjectSpaceMetaValue;
import net.project.hibernate.model.PnProjectSpaceMetaValuePK;
import net.project.hibernate.service.IPnProjectSpaceMetaValueService;

@Service(value = "pnProjectSpaceMetaValueService")
public class PnProjectSpaceMetaValueServiceImpl implements IPnProjectSpaceMetaValueService {
	
	@Autowired
    private IPnProjectSpaceMetaValueDAO pnProjectSpaceMetaValueDAO;

    public void setPnProjectSpaceMetaValueDAO(IPnProjectSpaceMetaValueDAO pnProjectSpaceMetaValueDAO) {
        this.pnProjectSpaceMetaValueDAO = pnProjectSpaceMetaValueDAO;
    }

    public PnProjectSpaceMetaValue getProjectSpaceMetaValue(PnProjectSpaceMetaValuePK pnProjectSpaceMetaValuePK) {
        return pnProjectSpaceMetaValueDAO.findByPimaryKey(pnProjectSpaceMetaValuePK);
    }

    public PnProjectSpaceMetaValuePK saveProjectSpaceMetaValue(PnProjectSpaceMetaValue pnProjectSpaceMetaValue) {
        return pnProjectSpaceMetaValueDAO.create(pnProjectSpaceMetaValue);
    }

    public void deleteProjectSpaceMetaValue(PnProjectSpaceMetaValue pnProjectSpaceMetaValue) {
        pnProjectSpaceMetaValueDAO.delete(pnProjectSpaceMetaValue);
    }

    public void updateProjectSpaceMetaValue(PnProjectSpaceMetaValue pnProjectSpaceMetaValue) {
        pnProjectSpaceMetaValueDAO.update(pnProjectSpaceMetaValue);
    }

    public List<PnProjectSpaceMetaValue> getMetaValuesByProjectId(final Integer projectId) {
        return pnProjectSpaceMetaValueDAO.getMetaValuesByProjectId(projectId);
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.IPnProjectSpaceMetaValueService#getMetaValueByProjectAndPropertyId(java.lang.Integer, java.lang.Integer)
     */
    public PnProjectSpaceMetaValue getMetaValueByProjectAndPropertyId(Integer projectId, Integer propertyId) {
    	return pnProjectSpaceMetaValueDAO.getMetaValueByProjectAndPropertyId(projectId, propertyId);
    }
    
    public List<PnProjectSpaceMetaCombo> getValuesOptionListForProperty(Integer propertyId) {
    	return pnProjectSpaceMetaValueDAO.getValuesOptionListForProperty(propertyId);
    }
}
