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

import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.model.PnPersonCosts;
import net.project.resource.ResourceRate;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author avinash
 *
 */
@Transactional
@Repository
public class PnPersonCostsDAO {
	
	public void savePersonCosts(BigInteger personId, int spaceId, List<ResourceRate> rates, Session hbSession) {
		ResourceRate rate = null;
		Iterator iter = rates.iterator();
		while(iter.hasNext()) {
			rate = (ResourceRate)iter.next();
			PnPersonCosts personCosts = new PnPersonCosts();
			personCosts.setCostPerUse(rate.getCostPerUse());
			personCosts.setOvertimeRate(rate.getOvertimeRate());
			personCosts.setOvertimeRateFormat(rate.getOvertimeRateFormat());
			personCosts.setPersonId(personId);
			Date from = null;
			if(rate.getRatesFrom() != null)
				from = rate.getRatesFrom().toGregorianCalendar().getTime();
			
			Date till = null;
			if(rate.getRatesTo() != null)
				till = rate.getRatesTo().toGregorianCalendar().getTime();
			personCosts.setRatesFrom(from);
			personCosts.setRatesTo(till);
			personCosts.setRateTable(rate.getRateTable());
			personCosts.setSpaceId(new BigInteger("" + spaceId));
			personCosts.setStandardRate(rate.getStandardRate());
			personCosts.setStandardRateFormat(rate.getStandardRateFormat());
			hbSession.saveOrUpdate(personCosts);
		}
	}
}
