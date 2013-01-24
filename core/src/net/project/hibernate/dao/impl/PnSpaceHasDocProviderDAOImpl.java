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

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.dao.IPnSpaceHasDocProviderDAO;
import net.project.hibernate.model.PnSpaceHasDocProvider;
import net.project.hibernate.model.PnSpaceHasDocProviderPK;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Vlad Melanchyk
 * 
 */
@Transactional 
@Repository 
public class PnSpaceHasDocProviderDAOImpl extends
	AbstractHibernateAnnotatedDAO<PnSpaceHasDocProvider, PnSpaceHasDocProviderPK>
	implements IPnSpaceHasDocProviderDAO {
    
    private final static Logger LOG = Logger
	    .getLogger(PnSpaceHasDocProviderDAOImpl.class);

    public PnSpaceHasDocProviderDAOImpl() {
	super(PnSpaceHasDocProvider.class);
    }

    public PnSpaceHasDocProvider findDefaultSpaceHasDocProviderBySpace(Integer spaceId) {
	if (LOG.isDebugEnabled()) {
	    LOG.debug("ENTRY OK: findDefaultSpaceHasDocProviderForSpace");
	}
	PnSpaceHasDocProvider result = null;
        try {
            List spaceDocProviders = new ArrayList();
            String sql = "from PnSpaceHasDocProvider where comp_id.spaceId=:spaceId and isDefault = 1";
            spaceDocProviders = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql).
            		setInteger("spaceId", spaceId).list();
        if (spaceDocProviders.size() > 0){
            result = (PnSpaceHasDocProvider)spaceDocProviders.get(0);                     
        }  
    } catch (Exception e) {
	if (LOG.isDebugEnabled()) {
	    LOG.debug("EXIT FAIL: findDefaultSpaceHasDocProviderForSpace");
	}
        e.printStackTrace();
    }
    if (LOG.isDebugEnabled()) {
	LOG.debug("EXIT OK: findDefaultSpaceHasDocProviderForSpace");
    }
    return result;
    
    }

}
