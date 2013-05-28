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

import net.project.hibernate.dao.IPnSpaceHasSpaceDAO;
import net.project.hibernate.model.PnSpaceHasSpace;
import net.project.hibernate.model.PnSpaceHasSpacePK;
import net.project.space.SpaceRelationship;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PnSpaceHasSpaceDAOImpl extends AbstractHibernateAnnotatedDAO<PnSpaceHasSpace, PnSpaceHasSpacePK> implements IPnSpaceHasSpaceDAO {

	private static Logger log = Logger.getLogger(PnSpaceHasSpaceDAOImpl.class);

	public PnSpaceHasSpaceDAOImpl() {
		super(PnSpaceHasSpace.class);
	}

	@Override
	public PnSpaceHasSpace getRelatedFinancialSpace(Integer spaceID) {
		PnSpaceHasSpace spaceHasSpace = new PnSpaceHasSpace();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			spaceHasSpace = (PnSpaceHasSpace) session.createCriteria(PnSpaceHasSpace.class).add(Restrictions.eq("comp_id.parentSpaceId", spaceID))
					.add(Restrictions.eq("relationshipParentToChild", SpaceRelationship.FINANCIAL.getNameParentToChild())).uniqueResult();
			session.close();
		} catch (HibernateException e) {
			log.error(e.getMessage());
		}
		return spaceHasSpace;
	}

}
