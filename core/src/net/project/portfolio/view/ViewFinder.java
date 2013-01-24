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
|   $Revision: 20896 $
|       $Date: 2010-06-02 10:54:07 -0300 (mié, 02 jun 2010) $
|     $Author: ritesh $
|
+----------------------------------------------------------------------*/
package net.project.portfolio.view;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.project.base.finder.Finder;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

/**
 * Provides mechanisms for finding and populating <code>IView</code>s.
 */
class ViewFinder extends Finder {

    /**
     * The columns used in SQL statements required for populating an IView.
     */
    private static final String BASE_SQL_COLUMNS =
            "select distinct v.view_id, v.name, v.description, " +
            "v.created_by_id, v.created_datetime, v.modified_by_id, v.modified_datetime, " +
            "v.record_status, v.ingredients_id, v.is_view_shared, v.is_visible_to_all ";

    /**
     * The base SQL statement for loading views.
     */
    private static final String BASE_SQL_STATEMENT =
            BASE_SQL_COLUMNS +
            "from pn_view v " +
            "where (1=1) ";

    /**
     * Returns the base SQL statement used for loading views.
     * @return the base SQL statement, including a where clause; modifications
     * to this may only append additional clauses to the where clause
     */
    protected String getBaseSQLStatement() {
        return BASE_SQL_STATEMENT;
    }

    /**
     * Finds a view with the specified id and populates the sepcified view object.
     * @param viewID the id of the view to find
     * @param view the view to populate
     * @throws PersistenceException if there is a problem finding or populating
     * the view
     */
    void findByID(String viewID, IView view) throws PersistenceException {
        addWhereClause("v.view_id = " + viewID);
        addOrderByClause("upper(v.name) asc");
        DBBean db = new DBBean();

        try {

            db.prepareStatement(getSQLStatement());
            db.executePrepared();

            if (db.result.next()) {
                populateObjectForResultSetRow(db.result, view);
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("find by ID operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Finds all views for the specified context.
     * @param context the context from which to load views.
     * @return a list where each element is a <code>IView</code> of the type
     * specified by the {@link IViewContext#makeView} method.
     * @throws PersistenceException if there is a problem loading
     */
    List findByContext(IViewContext context) throws PersistenceException {
    	addOrderByClause("upper(v.name) asc");
        List viewList = new ArrayList();
        DBBean db = new DBBean();

        try {

            // Ask the context to execute the statement to load the views
            // We only pass it the columns since it will join against other tables
            context.executeLoadActive(db, BASE_SQL_COLUMNS);

            while (db.result.next()) {
                IView view = context.makeView();
                populateObjectForResultSetRow(db.result, view);
                viewList.add(view);
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("find by context operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

        return viewList;
    }

    /**
     * Not supported by this finder.
     * Views cannot be created automatically here.
     * Use {@link #populateObjectForResultSetRow} instead.
     * @param databaseResults the <code>ResultSet</code> that provides the data
     * necessary to populate the view.
     * @return a populated object
     * @throws SQLException if an error occurs populating the object
     * @throws UnsupportedOperationException always
     */
    protected Object createObjectForResultSetRow(ResultSet databaseResults) throws SQLException {
        throw new UnsupportedOperationException("Create Object not supported");
    }

    /**
     * Populates the specified view with data from the result set.
     * @param result the <code>ResultSet</code> that provides the data
     * necessary to populate the view.
     * @param view the <code>IView</code> to populate with
     * data from the ResultSet.  The ResultSet is assumed to be on a current row.
     * @throws java.sql.SQLException if an error occurs populating the view
     */
    private void populateObjectForResultSetRow(ResultSet result, IView view) throws SQLException {
        view.setID(result.getString("view_id"));
        view.setName(result.getString("name"));
        view.setDescription(result.getString("description"));
        view.setCreatedByID(result.getString("created_by_id"));
        view.setCreatedDate(result.getTimestamp("created_datetime"));
        view.setModifiedByID(result.getString("modified_by_id"));
        view.setModifiedDate(result.getTimestamp("modified_datetime"));
        view.setRecordStatus(result.getString("record_status"));
        view.setFinderIngredientsID(result.getString("ingredients_id"));
        view.setViewShared(result.getBoolean("is_view_shared"));
        view.setVisibleToAll(result.getBoolean("is_visible_to_all"));
    }
}
