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
package net.project.hibernate.dao.impl;

import java.util.Date;

import net.project.hibernate.dao.IPnTimesheetDAO;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnTimesheet;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.ISecurityService;
import net.project.hibernate.service.ServiceFactory;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnTimesheetDAOImpl extends AbstractHibernateAnnotatedDAO<PnTimesheet, Integer> implements IPnTimesheetDAO {
    public PnTimesheetDAOImpl() {
        super(PnTimesheet.class);
    }

    public Integer create(PnTimesheet pnObject) {
        IPnObjectService objectService = ServiceFactory.getInstance().getPnObjectService();
        // creates address object
        Integer objectId = null;
        if (pnObject.getObject_id() == null) {
            Integer personId = new Integer(pnObject.getPerson().getPersonId());
            PnObject timesheetObject = new PnObject(PnTimesheet.OBJECT_TYPE, personId.intValue(), new Date(), pnObject.getRecordStatus());
            objectId = objectService.saveObject(timesheetObject);
            pnObject.setObject_id(objectId.intValue());
            // get Security service
            ISecurityService securityService = ServiceFactory.getInstance().getSecurityService();
            securityService.createSecurityPermissions(objectId, PnTimesheet.OBJECT_TYPE, personId, personId);
            getHibernateTemplate().saveOrUpdate(pnObject);
        } else {
            getHibernateTemplate().saveOrUpdate(pnObject);
            objectId = new Integer(pnObject.getObject_id());
        }
        return objectId.intValue();
    }

}
