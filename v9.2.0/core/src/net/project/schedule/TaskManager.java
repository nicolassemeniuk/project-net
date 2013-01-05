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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.schedule;

import java.util.Iterator;
import java.util.Map;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

public class TaskManager implements java.io.Serializable {

    private DBBean db = null;

    public TaskManager() {
        this.db = new DBBean();
    }

    /**
     * Simple structure for maintaining a task and its predecessor task
     * The task id is the PRM task id, the importParentTaskID is the import file-specific
     * predecessor id.
     */
    public static class ImportTask {
        String taskID = null;
        String importParentTaskID = null;

        public ImportTask(String taskID, String importParentTaskID) {
            this.taskID = taskID;
            this.importParentTaskID = importParentTaskID;
        }
    }

    /**
     * Updated tasks in database setting their parent task ids to real value
     * The map is assumed to be of the form<br>
     * <code>importTaskID = ImportTask{taskID, importParentTaskID}</code>
     */
    public void resolveImportHierarchy(Map tasks) throws PersistenceException {
        Iterator it = null;
        ImportTask task = null;
        ImportTask parentTask = null;
        String realParentTaskID = null;

        String updateQuery = "update pn_task set parent_task_id = ? where task_id = ?";

        try {

            db.openConnection();
            db.connection.setAutoCommit(false);
            db.prepareStatement(updateQuery);

            it = tasks.keySet().iterator();
            while (it.hasNext()) {
                task = (ImportTask)tasks.get(it.next());

                // If task has a parent id from the import file
                // update parent task id with the real parent task id
                if (task.importParentTaskID != null) {
                    parentTask = (ImportTask)tasks.get(task.importParentTaskID);
                    // If a parent task is missing, this can only be due to
                    // erroneous predecessor information in the import file
                    // We will skip updating the task with no parent info.
                    if (parentTask != null) {
                        realParentTaskID = parentTask.taskID;
                        db.pstmt.setInt(1, Integer.parseInt(realParentTaskID));
                        db.pstmt.setInt(2, Integer.parseInt(task.taskID));
                        db.pstmt.addBatch();
                    }
                }

            } //end while

            db.pstmt.executeBatch();
            db.connection.commit();

        } catch (java.sql.SQLException sqle) {
        	Logger.getLogger(TaskManager.class).error("TaskManager.resolveImportHierarchy threw an SQL exception: " + sqle);
            throw new PersistenceException("Task Manager import partially failed.", sqle);

        } finally {
            if (db.connection != null) {
                try {
                    db.connection.rollback();
                } catch (java.sql.SQLException sqle) {
                }
            }
            db.release();
        }

    }

}
