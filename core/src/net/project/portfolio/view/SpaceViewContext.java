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
|   $Revision: 20917 $
|       $Date: 2010-06-04 04:36:25 -0300 (vie, 04 jun 2010) $
|     $Author: ritesh $
|
+----------------------------------------------------------------------*/
package net.project.portfolio.view;

import java.sql.SQLException;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.space.Space;

/**
 * Provides a ViewContext based on a space.
 */
public abstract class SpaceViewContext implements IViewContext {

    /**
     * The space for this view context.
     */
    private Space space = null;

    /**
     * The current user.
     */
    private User user = null;

    /**
     * Specifies the space for which this view context applies.
     * @param space the space
     */
    public void setSpace(Space space) {
        this.space = space;
    }

    /**
     * Returns the space for which this view context applies.
     * @return the space
     */
    public Space getSpace() {
        return this.space;
    }

    /**
     * Sets the current user.
     * @param user the current user
     * @see #getCurrentUser
     */
    public void setCurrentUser(User user) {
        this.user = user;
    }

    /**
     * Returns the current user.
     * @return the current user
     * @see #getCurrentUser
     */
    public User getCurrentUser() {
        return this.user;
    }


    /**
     * Inserts a view in this space view context.
     * Performs no commit or rollback.
     * @param db the DBBean in which to perform the transaction
     * @param view the view being inserted
     * @throws PersistenceException if there is a problem inserting
     * @throws NullPointerException if the view is null
     * @throws IllegalStateException if a current space is not available
     */
    public void insert(DBBean db, IView view) throws PersistenceException {

        if (view == null) {
            throw new NullPointerException("view is required");
        }

        if (getSpace() == null) {
            throw new IllegalStateException("Space must be specified");
        }

        StringBuffer query = new StringBuffer();
        query.append("insert into pn_space_view_context ");
        query.append("(space_id, view_id) ");
        query.append("values (?, ?) ");

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, getSpace().getID());
            db.pstmt.setString(++index, view.getID());
            db.executePrepared();

        } catch (SQLException sqle) {
            throw new PersistenceException("Space View Context insert operation failed: " + sqle, sqle);

        }

    }

    /**
     * Updates a view in this space view context.
     * Performs no commit or rollback.
     * @param db the DBBean in which to perform the transaction
     * @param view the view being updated
     * @throws PersistenceException if there is a problem updating
     * @throws NullPointerException if the view is null
     * @throws IllegalStateException if a current space is not available
     */
    public void update(DBBean db, IView view) throws PersistenceException {

        if (view == null) {
            throw new NullPointerException("view is required");
        }

        if (getSpace() == null) {
            throw new IllegalStateException("Space must be specified");
        }

        // Nothing to do in space view context when updating
    }

    /**
     * Removes a view from this space context.
     * @param db the DBBean in which to perform the transaction
     * @param view the view to remove
     * @throws PersistenceException if there is a problem removing
     * @throws NullPointerException if the view is null
     * @throws IllegalStateException if a current space is not available
     */
    public void remove(DBBean db, IView view) throws PersistenceException {

        if (view == null) {
            throw new NullPointerException("view is required");
        }

        if (getSpace() == null) {
            throw new IllegalStateException("Space must be specified");
        }

        // Nothing to do in space view context when removing
        // The view will be, or has already been, soft-deleted

    }

    /**
     * Execute the load statement to fetch views for the current space.
     * These fetched views contains views created by current user and those created by other users 
     * but made shared with the business where current user is a member
     * It also contains views which are made shared to all users in entire application
     * displaying shared to all user views can be enabled and disabled by appadmin.  
     * No commit, rollback or release is performed.
     * @param db the DBBean who's result set to populate
     * @param baseSQLColumns the <code>SELECT</code> part containing the columns
     * to be selected
     * @throws PersistenceException if there is a problem loading
     */
    public void executeLoadActive(DBBean db, String baseSQLColumns) throws PersistenceException {

    	boolean viewGloballyVisible = PropertyProvider.getBoolean("prm.global.view.visibility.isenabled");
        try {

            StringBuffer query = new StringBuffer();
            query.append(baseSQLColumns);
            query.append("from pn_space_view_context svc, pn_view v Left outer join pn_business_has_view bhv on v.view_id = bhv.view_id ");
            query.append("where(");
            query.append("(v.view_id = bhv.view_id  and bhv.business_id in(select b.business_id from pn_business_space_view b, pn_space_has_person shp where shp.space_id = b.business_id and (shp.person_id = ? or b.includes_everyone = ?)))");
            query.append("or (svc.space_id = ? and v.view_id = svc.view_id )");
            if(viewGloballyVisible)
                query.append("or v.is_visible_to_all = ?");
            query.append(")");
            query.append("and v.record_status = ? order by upper(v.name) asc");
 
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, getSpace().getID());
            db.pstmt.setBoolean(++index, true);
            db.pstmt.setString(++index, getSpace().getID());
            if(viewGloballyVisible)
            	db.pstmt.setBoolean(++index, true);
            db.pstmt.setString(++index, "A");
            db.executePrepared();

        } catch (SQLException sqle) {
            throw new PersistenceException("Space view context load operation failed: " + sqle, sqle);

        }

    }

}
