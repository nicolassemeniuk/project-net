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
package net.project.portfolio.view;

import java.util.Collection;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

/**
 * Defines a context to which a view applies.
 * This allows views to be segregated by context.
 */
public interface IViewContext {

    /**
     * Returns an empty view of the correct type for this context.
     * @return an empty
     */
    public IView makeView();

    /**
     * Returns the views for this context.
     * @return the list of views
     * @throws PersistenceException if there is a problem making the list
     */
    public IViewList makeViewList() throws PersistenceException;

    /**
     * Inserts a view in this context.
     * The view has already been inserted.
     * @param db the DBBean in which to perform the transaction
     * @param view the view to insert
     * @throws PersistenceException if there is a problem inserting
     */
    public void insert(DBBean db, IView view) throws PersistenceException;

    /**
     * Updates a view in this context.
     * @param db the DBBean in which to perform the transaction
     * @param view the view to update
     * @throws PersistenceException if there is a problem updating
     */
    public void update(DBBean db, IView view) throws PersistenceException;

    /**
     * Removes a view from this context.
     * It can be assumed that the view will be soft-deleted.
     * @param db the DBBean in which to perform the transaction
     * @param view the view to remove
     * @throws PersistenceException if there is a problem removing
     */
    public void remove(DBBean db, IView view) throws PersistenceException;

    /**
     * Execute the load statement to fetch active views for this context, populating
     * the db result set.
     * @param db the DBBean who's result set to populate; a connection may
     * or may not have already been opened.  No statement has been prepared.
     * After calling the result set <code>db.result</code> should be set to
     * the result of the query and <code>next()</code> should not have been called.
     * @param baseSQLColumns the <code>SELECT</code> part containing the columns
     * to be selected
     * @throws PersistenceException if there is a problem loading
     */
    public void executeLoadActive(DBBean db, String baseSQLColumns) throws PersistenceException;

    /**
     * Returns the default view ID for the specified default scenario
     * for this context.
     * @param scenario the default scenario for this context
     * @return the default view ID or null if there is none
     */
    public String getDefaultViewID(DefaultScenario scenario);

    /**
     * Returns the loaded default view settings for this context.
     * These are the default view IDs for the default view scenarios.
     * If a scenario does not have a default view, then the setting will have
     * an empty view ID.
     * @return the collection where each element is a <code>DefaultViewSetting</code>
     * @throws PersistenceException if there is a problem loading
     */
    public Collection getDefaultViewSettings() throws PersistenceException;

    /**
     * Stores the specified <code>DefaultViewSetting</code>s in persistent store.
     * @param defaultViewSettings the collection of <code>DefaultViewSetting</code>s
     * to store
     * @throws PersistenceException if there is a problem storing
     */
    public void storeDefaultViewSettings(Collection defaultViewSettings) throws PersistenceException;

}
