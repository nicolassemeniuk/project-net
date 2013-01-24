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
package net.project.resource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;

/**
 * This class maps property names to property values for a given
 * {@link net.project.resource.PersonProperty}.  This class has no ability to
 * store or remove properties, use {@link net.project.resource.PersonProperty}
 * for that.
 *
 * This object doesn't support property names that have multiple values.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class PersonalPropertyMap {

    /** Map of property names to property values. */
    private Map properties = new HashMap();

    /**
     * Loads all of the current person's properties for the current user
     * and global scope.
     * <p>
     * <b>Note:</b> This is a convenience for XSL files to load global scoped properties
     * without requiring knowledge of the current user.
     * </p>
     * @param context the context to load for
     */
    public PersonalPropertyMap(String context) throws PersistenceException {
        this(new PersonPropertyGlobalScope(SessionManager.getUser()), context);
    }

    /**
     * Loads all of the current person's property given a context to load.
     *
     * @param scope the scope from which to load the properties
     * @param context a <code>String</code> containing the context that we wish
     * to load properties for.  Examine the pn_person_properties table to figure
     * @throws NullPointerException if either scope or context are null
     */
    public PersonalPropertyMap(IPersonPropertyScope scope, String context) throws PersistenceException {

        if (scope == null || context == null) {
            throw new NullPointerException("scope and context are required");
        }

        DBBean db = new DBBean();
        try {
            db.prepareStatement(
                "SELECT property, value " +
                "FROM pn_person_properties " +
                "WHERE space_id = ? " +
                "and person_id = ? " +
                "and context = ? "
            );

            db.pstmt.setString(1, scope.getSpaceID());
            db.pstmt.setString(2, scope.getUserID());
            db.pstmt.setString(3, context);
            db.executePrepared();

            while (db.result.next()) {
                properties.put(db.result.getString("property"), db.result.getString("value"));
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("Error loading properties: " + sqle, sqle);

        } finally {
            db.release();
        }
    }

    /**
     * Get a property from the collection.
     *
     * @param name a <code>String</code> containing the property name to fetch.
     * @return a <code>String</code> containing the first property value that
     * matched the property name.
     */
    public String getProperty(String name) {
        String value = (String)properties.get(name);

        return (value == null ? "" : value);
    }

    /**
     * Determines if a property is loaded that has the given property name.
     *
     * @param name a <code>String</code> containing the name we want to look for.
     * @return a <code>boolean</code> indicating if the property was found
     * internally.
     */
    public boolean propertyExists(String name) {
        return properties.containsKey(name);
    }
}
