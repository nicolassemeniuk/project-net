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

 package net.project.schedule;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

public class CyclicDependencyDetectorV2 {
    
    public boolean hasCycle(ScheduleEntry entry, Schedule schedule) throws PersistenceException {
        boolean hasCycle = false;
        DBBean db = new DBBean();
        try {
            db.prepareCall("{? = call SCHEDULE.test_cyclic(?,?)}");
            //sjmittal: reverting the changes of changeset 15957 as check for implied predecessors 
            //is done inside the stored proc
//            TaskDependenciesFetcher tdf = new TaskDependenciesFetcher(schedule.getEntryMap());
//            Collection predecessors = tdf.getPredecessors(entry);
            //for predecessors
            for (Iterator it = entry.getPredecessors().getInternalList().iterator(); it.hasNext();) {
                TaskDependency dependency = (TaskDependency) it.next();

                db.cstmt.registerOutParameter(1, Types.NUMERIC);
                db.cstmt.setString(2, entry.getID());
                db.cstmt.setString(3, dependency.getDependencyID());

                db.executeCallable();

                if (db.cstmt.getInt(1) > 0) {
                    hasCycle = true;
                    break;
                }
            }
            
            //for parent task
            if(entry.getParentTaskID() != null) {
                db.cstmt.registerOutParameter(1, Types.NUMERIC);
                db.cstmt.setString(2, entry.getParentTaskID());
                db.cstmt.setString(3, entry.getID());
    
                db.executeCallable();
    
                if (db.cstmt.getInt(1) > 0) {
                    hasCycle = true;
                }
            }

        } catch (SQLException sqle) {
            throw new PersistenceException(sqle);
        } finally {
            db.release();
        }

        return hasCycle;
    }

    public List getCyclicDependencies(ScheduleEntry entry, Schedule schedule) throws PersistenceException {
        List cyclicDependencies = new ArrayList();
        DBBean db = new DBBean();
        try {
            db.prepareCall("{? = call SCHEDULE.test_cyclic(?,?)}");
            //sjmittal: reverting the changes of changeset 15957 as check for implied predecessors 
            //is done inside the stored proc
//            TaskDependenciesFetcher tdf = new TaskDependenciesFetcher(schedule.getEntryMap());
//            Collection predecessors = tdf.getPredecessors(entry);
            //for predecessors
            for (Iterator it = entry.getPredecessors().getInternalList().iterator(); it.hasNext();) {
                TaskDependency dependency = (TaskDependency) it.next();

                db.cstmt.registerOutParameter(1, Types.NUMERIC);
                db.cstmt.setString(2, entry.getID());
                db.cstmt.setString(3, dependency.getDependencyID());

                db.executeCallable();

                if (db.cstmt.getInt(1) > 0) {
                    cyclicDependencies.add(dependency);
                }
            }

        } catch (SQLException sqle) {
            throw new PersistenceException(sqle);
        } finally {
            db.release();
        }

        return cyclicDependencies;
    }
}
