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
package net.project.base.property;

import java.sql.SQLException;

import net.project.database.DBBean;

import org.apache.log4j.Logger;

class PropertyCategory extends java.util.ArrayList {

    private String id = null;
    private String name = null;
    private String description = null;

    protected PropertyCategory() {
        // do nothing
    }


    void setID(String id) {
        this.id = id;
    }

    String getID() {
        return this.id;
    }

    void setName(String name) {
        this.name = name;
    }

    protected String getName() {
        return this.name;
    }

    void setDescription(String description) {
        this.description = description;
    }

    protected String getDescription() {
        return this.description;
    }


    protected void load() {

        String qstrLoadCategory = "select property from pn_prop_category_has_property where category_id = " + getID();

        DBBean db = new DBBean();

        try {

            db.executeQuery(qstrLoadCategory);

            while (db.result.next())
                this.add(db.result.getString("property"));
        } catch (SQLException sqle) {
        	Logger.getLogger(PropertyCategory.class).debug("PropertyCategory.load() threw an SQLException: " + sqle);
        } finally {
            db.release();
        }

    }

    void addEntry(String entry) {

        if (!this.contains(entry))
            this.add(entry);

    }

}
