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

import net.project.hibernate.dao.IPnAddressDAO;
import net.project.hibernate.model.PnAddress;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnAddressDAOImpl extends AbstractHibernateAnnotatedDAO<PnAddress, Integer> implements IPnAddressDAO {

	public PnAddressDAOImpl(){
		super(PnAddress.class);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.impl.AbstractHibernateAnnotatedDAO#findByPimaryKey(java.io.Serializable)
	 */
	@Override
	public PnAddress findByPimaryKey(Integer key) {
		PnAddress pnAddress = super.findByPimaryKey(key);
		if(pnAddress != null){
			Hibernate.initialize(pnAddress);
		}
		return pnAddress;
	}
	
}
