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
package net.project.space;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import net.project.database.DBBean;
import net.project.security.SessionManager;

/**
 * Find spaces by name.
 *
 * @author Matthew Flower
 */
public class SpaceSearch {
    /** Base SQL to construct a query with. */
    private static final String SQL =
        "select "+
        "  o.object_id, "+
        "  oname.name "+
        "from "+
        "  pn_object o, "+
        "  pn_object_name oname, "+
        "  pn_space_has_person shp "+
        "where "+
        "  o.object_id = oname.object_id "+
        "  and o.record_status = 'A' "+
        "  and shp.space_id = o.object_id "+
        "  and shp.person_id = ? " +
        "  and o.object_type <> 'person' ";

    /**
     * Search for spaces that match a keyword.
     *
     * @param keyword a <code>String</code> containing a keyword we are
     * searching for.
     * @return a <code>Collection</code> of SpaceSearch.SpaceMatch object.
     */
    public Collection keywordSearch(String keyword) {
        Collection spaces = new ArrayList();

        //Construct the search string
        String whereClause = " and lower(oname.name) like '%"+keyword.toLowerCase()+"%' ";

        DBBean db = new DBBean();
        try {
            db.prepareStatement(SQL+whereClause);
            db.pstmt.setString(1, SessionManager.getUser().getID());
            db.executePrepared();

            while (db.result.next()) {
                spaces.add(new SpaceMatch(
                    db.result.getString(1),
                    db.result.getString(2)
                ));
            }
        } catch (SQLException sqle) {
        } finally {
            db.release();
        }

        return spaces;
    }

    /**
     * Represents a space that has matched a user's search criteria.
     *
     * @author Matthew Flower
     */
    public class SpaceMatch {
        private String id;
        private String name;

        /**
         * Creates a new instance of SpaceMatch.
         *
         * @param id a <code>String</code> containing the primary key of the
         * space.
         * @param name a <code>String</code> containing the human readable
         * display name of the space.
         */
        public SpaceMatch(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getID() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
