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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.project.base.finder.EmptyFinderFilter;
import net.project.database.DatabaseUtils;
import net.project.schedule.TaskFinder;

public class PhaseFilter extends EmptyFinderFilter {
    List phaseID;

    public PhaseFilter(String id) {
        super(id);
    }

    /**
     * Get the where clause for an empty finder filter, which should always be
     * empty.
     *
     * @return a <code>String</code> value which should always be empty.
     */
    public String getWhereClause() {
        String sql = "";

        if (phaseID == null || phaseID.size()==0) {
            sql = "(1=1)";
        } else if (phaseID.size() == 1 && phaseID.get(0).equals("null")) {
            sql = "("+TaskFinder.PHASE_ID_COLUMN.getColumnName()+" is null)";
        } else if (phaseID.size() == 1) {
            sql = "("+TaskFinder.PHASE_ID_COLUMN.getColumnName()+" = "+phaseID.get(0)+")";
        } else {
            sql = "("+TaskFinder.PHASE_ID_COLUMN.getColumnName()+" in ("+DatabaseUtils.collectionToCSV(phaseID)+"))";
        }

        return sql;
    }


    public List getPhaseID() {
        return phaseID;
    }

    public void setPhaseID(List phaseID) {
        this.phaseID = phaseID;
    }

    public void setPhaseID(String[] phaseID) {
        this.phaseID = Arrays.asList(phaseID);
    }

    public void setPhaseID(String phaseID) {
        ArrayList phaseIDList = new ArrayList();
        phaseIDList.add(phaseID);

        this.phaseID = phaseIDList;

    }
}
