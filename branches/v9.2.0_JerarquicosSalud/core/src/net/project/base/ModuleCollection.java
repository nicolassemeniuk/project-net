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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Collection of <code>Module</code>s.
 */
public class ModuleCollection
        extends ArrayList {

    /**
     * Creates an empty ModuleCollection.
     */
    public ModuleCollection() {
        super();
    }


    /**
     * Loads all Modules into this collection.
     */
    public void load() throws PersistenceException {
        DBBean db = new DBBean();
        Module module = null;
        StringBuffer query = new StringBuffer();

        // Clear all existing entries from this collection
        super.clear();

        query.append("select m.module_id, m.name, m.description, m.default_permission_actions ");
        query.append("from pn_module m ");
       
        try {
            db.prepareStatement(query.toString());
            db.executePrepared();

            while (db.result.next()) {
                module = new Module();
                module.setId(db.result.getString("module_id"));
                module.setName(db.result.getString("name"));
                module.setDescription(PropertyProvider.get(db.result.getString("description")));
                add(module);
            }
        
        } catch (SQLException sqle) {
            Logger.getLogger(ModuleCollection.class).error("ModuleCollection.load() threw an SQLException: " + sqle);
            throw new PersistenceException("Module collection load operation failed", sqle);

        } finally {
            db.release();

        }

    }


    /**
     * Returns the Module with the specified ID.
     * Assumes this collection is loaded.
     * @param moduleID the id of the module to get from this collection
     * @return the Module or null if there is no Module with that ID
     */
    public Module getModule(String moduleID) {
        Module module = null;
        boolean isNotFound = true;

        Iterator it = iterator();
        while (it.hasNext() & isNotFound) {
            module = (Module) it.next();
            
            if (module.getId().equals(moduleID)) {
                isNotFound = false;
            }
        
        }

        return module;
    }

}
