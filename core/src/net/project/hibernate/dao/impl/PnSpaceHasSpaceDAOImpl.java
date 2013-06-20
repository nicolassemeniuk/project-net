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

import net.project.hibernate.dao.IPnSpaceHasSpaceDAO;
import net.project.hibernate.model.PnSpaceHasSpace;
import net.project.hibernate.model.PnSpaceHasSpacePK;
import net.project.space.SpaceRelationship;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Property;
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
	public PnSpaceHasSpace getFinancialRelatedToBusinessSpace(Integer businessSpaceID) {
		PnSpaceHasSpace spaceHasSpace = new PnSpaceHasSpace();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnSpaceHasSpace.class);
			criteria.add(Restrictions.eq("comp_id.parentSpaceId", businessSpaceID));
			criteria.add(Restrictions.eq("relationshipParentToChild", SpaceRelationship.FINANCIAL.getNameParentToChild()));
			criteria.add(Restrictions.ne("recordStatus", "D"));
			spaceHasSpace = (PnSpaceHasSpace) criteria.uniqueResult();
			session.close();
		} catch (HibernateException e) {
			log.error(e.getMessage());
		}
		return spaceHasSpace;
	}

	@Override
	public PnSpaceHasSpace getBusinessRelatedToFinancialSpace(Integer financialSpaceID) {
		PnSpaceHasSpace spaceHasSpace = new PnSpaceHasSpace();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnSpaceHasSpace.class);
			criteria.add(Restrictions.eq("comp_id.childSpaceId", financialSpaceID));
			criteria.add(Restrictions.ne("recordStatus", "D"));
			criteria.add(Restrictions.eq("relationshipParentToChild", SpaceRelationship.FINANCIAL.getNameParentToChild()));
			spaceHasSpace = (PnSpaceHasSpace) criteria.uniqueResult();
			session.close();
		} catch (HibernateException e) {
			log.error(e.getMessage());
		}
		return spaceHasSpace;
	}

	@Override
	public Integer getParentSpaceID(Integer spaceID) {
		Integer parentSpaceID = 0;
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnSpaceHasSpace.class);
			criteria.add(Restrictions.eq("comp_id.childSpaceId", spaceID));
			criteria.add(Restrictions.ne("recordStatus", "D"));
			criteria.add(Restrictions.eq("relationshipChildToParent", SpaceRelationship.SUBSPACE.getNameChildToParent()));
			criteria.setProjection(Property.forName("comp_id.parentSpaceId"));
			parentSpaceID = (Integer) criteria.uniqueResult();
			session.close();
		} catch (HibernateException e) {
			log.error(e.getMessage());
		}

		return parentSpaceID;
	}

	@Override
	public PnSpaceHasSpace getFinancialParentSpaceRelationship(Integer financialChildSpaceID) {
		PnSpaceHasSpace spaceHasSpace = new PnSpaceHasSpace();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnSpaceHasSpace.class);
			criteria.add(Restrictions.eq("comp_id.childSpaceId", financialChildSpaceID));
			criteria.add(Restrictions.ne("recordStatus", "D"));
			criteria.add(Restrictions.eq("relationshipParentToChild", SpaceRelationship.SUBSPACE.getNameParentToChild()));
			spaceHasSpace = (PnSpaceHasSpace) criteria.uniqueResult();
			session.close();
		} catch (HibernateException e) {
			log.error(e.getMessage());
		}
		return spaceHasSpace;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<PnSpaceHasSpace> getFinancialChildsSpaceRelationships(Integer financialParentSpaceID) {
		ArrayList<PnSpaceHasSpace> spaceHasSpace = new ArrayList<PnSpaceHasSpace>();
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();
			Criteria criteria = session.createCriteria(PnSpaceHasSpace.class);
			criteria.add(Restrictions.eq("comp_id.parentSpaceId", financialParentSpaceID));
			criteria.add(Restrictions.ne("recordStatus", "D"));
			criteria.add(Restrictions.eq("relationshipParentToChild", SpaceRelationship.SUBSPACE.getNameParentToChild()));
			spaceHasSpace = (ArrayList<PnSpaceHasSpace>)criteria.list();
			session.close();
		} catch (HibernateException e) {
			log.error(e.getMessage());
		}
		return spaceHasSpace;
	}
}
