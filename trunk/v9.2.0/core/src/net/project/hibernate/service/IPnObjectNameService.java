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
package net.project.hibernate.service;



import net.project.hibernate.model.PnObjectName;
import net.project.hibernate.model.PnWeblogEntry;

public interface IPnObjectNameService {
	
	/**
	 * Get Objects name by given objectId
	 * @param objectId
	 * @return
	 */
    public String getNameFofObject(Integer objectId);
    
    /**
     * Save the PnobjectName
     * @param pnObjectName
     * @return
     */
    public Integer save(PnObjectName pnObjectName);
    
    /**
     * Update the PnobjectName
     * @param pnWeblogEntry
     */
    public void update(PnObjectName pnObjectName);

	/**
	 * Get Objects name by given subscriptionId
	 * @param subscriptionId
	 * @return object name
	 */
	public String getObjectNameBySubscriptionId(Integer subscriptionId);

}
