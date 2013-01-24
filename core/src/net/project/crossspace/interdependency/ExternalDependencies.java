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
package net.project.crossspace.interdependency;

import java.sql.SQLException;

import net.project.base.Identifiable;
import net.project.database.DBBean;

import org.apache.log4j.Logger;

public class ExternalDependencies {

    public static void update(Identifiable object, DBBean db) {  	
        DependencyWorker worker = new DependencyWorker(object, db);

        //Let the worker do its work
        worker.work();
    }
}

class DependencyWorker {
    private Logger logger = Logger.getLogger(DependencyWorker.class);
    //get any imported objects
    private String SQL =
        "select "+
        "  s.import_container_id, " +
        "  s.imported_object_id, "+
        "  s.export_container_id, "+
        "  s.exported_object_id, "+
        "  o.object_type "+
        "from "+
        "  pn_object o, "+
        "  pn_shared s "+
        "where "+
        "  o.object_id = s.exported_object_id "+
        "  and s.exported_object_id = ? ";

    private DBBean db;
    private Identifiable object;
    private String alteredObjectID;
    private String containerID;
    private String objectID;

    DependencyWorker(Identifiable object, DBBean db) {
        this.db = db;
        this.object = object;
        this.alteredObjectID = object.getID();
    }

    public void work() {
        //First, figure out what is currently using this share
        DBBean db1 = new DBBean();
        try {
            db1.prepareStatement(SQL);
            db1.pstmt.setString(1, alteredObjectID);
            db1.executePrepared();

            while (db1.result.next()) {
                String containerType = db1.result.getString("object_type");
                IWorker typeSpecificWorker = WorkDispatcher.getWorkerForObjectType(containerType);

                //Let the container that the copied is in do any work that it
                //needs to.  e.g. Recalculate a schedule
                if (typeSpecificWorker != null) {
                    	containerID = db1.result.getString("import_container_id");
                    	objectID = db1.result.getString("imported_object_id");
                    	if(!alteredObjectID.equals(objectID))
                    	    typeSpecificWorker.execute(db, object, containerID, objectID);

                } else {
                    logger.warn("There is no worker available for object type " + containerType);
                }
            }
        // uncaught expections may cause application server to suspend the thread.
        // So we need to catch everything that might occur.
        } catch (SQLException sqle) {
            logger.error("DependencyWorker.run() Thread:  Error encountered while trying to query for " +
                "dependencies on object id: "+alteredObjectID, sqle);
        } catch (NullPointerException npe) {
            logger.error("DependencyWorker.run() Thread:  NPE while quering import/export dependencies." +
                    "dependencies on object id: "+alteredObjectID, npe);
        } finally {
        	if (db1 != null)
                db1.release();
        }

    }
}

