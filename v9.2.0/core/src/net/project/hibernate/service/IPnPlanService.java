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

import net.project.hibernate.model.PnPlan;

public interface IPnPlanService {

    /**
     * Saves new {@link PnPlan}.
     * @param pnPlan <code>PnPlan</code> object we want to save
     * @return primary key of created <code>PnPlan</code>
     */
    public Integer savePlan(PnPlan pnPlan);
    
    /**
     * Returns {@link PnPlan} with given Id.
     * @param planId
     * @return <code>PnPlan</code> bean
     */
    public PnPlan getPlan(Integer planId);
    
    /**
     * Updates {@link PnPlan}.
     * @param plan
     */
    public void updatePlan(PnPlan plan);
    
}
