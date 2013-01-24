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
package net.project.base.finder;

import java.sql.SQLException;
import java.util.EventListener;
import java.util.List;

import net.project.database.DBBean;

/**
 * This class allows a user to register callback methods for a finder so that
 * user code can be notified when a finder event occurs.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public interface FinderListener extends EventListener {
    /**
     * This method is called prior to doing anything in the getSQLStatement()
     * method.  No sorters or filters have been gathered, nor has the
     * <code>getBaseSQLStatement</code> been called.
     *
     * This method was created to allow Finders to add "group by" statements
     * that are required for the sql statement to function properly, although
     * it can be used for much more.
     *
     * @param f a <code>Finder</code> that is about to call its own
     * {@link net.project.base.finder.Finder#getSQLStatement} method.
     */
    public void preConstruct(Finder f);

    /**
     * This method is called just before running <code>executePrepared</code> on
     * the sql statement constructed by calling <code>getSQLStatement</code>.
     * This allows a user to set parameters that are required to execute a
     * prepared statement.
     *
     * @param db a {@link net.project.database.DBBean} that is just about to
     * call {@link net.project.database.DBBean#executePrepared}.
     * @throws SQLException if an error occurs while modifying the
     * <code>DBBean</code>.
     */
    public void preExecute(DBBean db) throws SQLException;

    /**
     * This method is called after the SQL Statement has been execute and after
     * all of the objects have been loaded.
     *
     * @param db a <code>DBBean</code> which was used to load the objects.
     * @param db a <code>Object</code> which was the only one loaded.  This
     * method will only be called if the Finder specifically decided to load a
     * single object which they had created prior to calling the finder.
     */
    public void postExecute(DBBean db, Object object) throws SQLException;

    /**
     * This method is called after the SQL Statement has been execute and after
     * all of the objects have been loaded.
     *
     * @param db a <code>DBBean</code> which was used to load the objects.
     * @param list a <code>List</code> of objects loaded during population
     */
    public void postExecute(DBBean db, List list) throws SQLException;
}
