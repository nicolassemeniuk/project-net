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
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

/**
 * Provides methods to determine whether tokens have changed for a particular
 * context and language.
 * <p>
 * To flag that tokens have changed for a context: <code><pre>
 *     TokenChangeMangaer.flagTokenChanged(contextID, language);
 * </pre></code>
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class TokenChangeManager {

    //
    // Static members
    //

    /**
     * Flags that a token changed for a specified context and language.
     * The date and time is recorded as the current date and time.
     * @param contextID the context ID
     * @param language the language
     * @throws PersistenceException if there is a problem updating the database
     * @throws NullPointerException if the contextID is null or the language is null
     */
    public static void flagTokenChanged(String contextID, String language) throws PersistenceException {

        if (contextID == null || language == null) {
            throw new NullPointerException("Context ID and language are required");
        }

        StringBuffer deleteQuery = new StringBuffer();
        deleteQuery.append("delete from pn_property_change where context_id = ? and language = ? ");

        StringBuffer insertQuery = new StringBuffer();
        insertQuery.append("insert into pn_property_change ")
                .append("(context_id, language, last_updated_datetime) ")
                .append("values (?, ?, ?) ");

        DBBean db = new DBBean();

        try {

            db.setAutoCommit(false);

            // Delete row
            int index = 0;
            db.prepareStatement(deleteQuery.toString());
            db.pstmt.setString(++index, contextID);
            db.pstmt.setString(++index, language);
            db.executePrepared();

            // Insert row
            index = 0;
            db.prepareStatement(insertQuery.toString());
            db.pstmt.setString(++index, contextID);
            db.pstmt.setString(++index, language);
            db.pstmt.setTimestamp(++index, new Timestamp(new Date().getTime()));
            db.executePrepared();

            db.commit();

        } catch (SQLException e) {
            try {
                db.rollback();
            } catch (SQLException e2) {
                // Throw original error
            }
            throw new PersistenceException("Error loading token change data: " + e, e);

        } finally {
            db.release();

        }

    }

    /**
     * Returns a loaded TokenChangeManager.
     * This returns a new loaded instantiation.  It is loaded once to avoid
     * excessive database accesses when determining the last updated time
     * of more than one context and language.
     * @return the loaded TokenChangeManager
     * @throws PersistenceException if there is a problem loading
     */
    static TokenChangeManager getLoadedInstance() throws PersistenceException {
        TokenChangeManager manager = new TokenChangeManager();
        manager.load();
        return manager;
    }

    //
    // Instance members
    //

    /**
     * The map of context to dates.
     */
    private Map contextLastUpdateMap = Collections.EMPTY_MAP;

    /**
     * Private instantiation to prevent usage.
     * @see TokenChangeManager#getLoadedInstance
     */
    private TokenChangeManager() {
        // Do nothing
    }

    /**
     * Indicates whether any properties for the specified context have changed
     * since a certain date.
     * If the context is not found (that is, properties have _never_ been
     * updated for the context) then this method returns "false".
     * @param context the context to check
     * @param sinceDate the date to compare to
     * @return true if the current updated date for the specified context is
     * later than the specified sinceDate; false otherwise
     */
    boolean hasChanged(Context context, Date sinceDate) {

        boolean hasChanged = false;

        Date lastUpdatedDate = (Date) this.contextLastUpdateMap.get(context);
        if (lastUpdatedDate == null) {
            // No context found matching
            // It suggests that there are properties loaded for a context
            // that we don't know about
            // This could happen if a "last updated" record was never created,
            // which means the properties have never been modified for the
            // context
            // In this case the properties for the passed-in context cannot
            // have changed

            // Do nothing (hasChanged = false)

        } else if (lastUpdatedDate.getTime() > sinceDate.getTime()) {
            hasChanged = true;

        }

        return hasChanged;
    }

    /**
     * Loads all context update information for all contexts which have been
     * updated.
     * @throws PersistenceException if there is a problem loading
     */
    private void load() throws PersistenceException {

        StringBuffer query = new StringBuffer();
        query.append("select pc.context_id, pc.language, pc.last_updated_datetime ")
                .append("from pn_property_change pc ");

        DBBean db = new DBBean();

        Map contexts = new HashMap();

        try {

            db.executeQuery(query.toString());
            while (db.result.next()) {

                String contextID = db.result.getString("context_id");
                String language = db.result.getString("language");
                Date lastUpdatedDate = db.result.getTimestamp("last_updated_datetime");

                contexts.put(new Context(contextID, language), lastUpdatedDate);
            }

            // Since we loaded all successfully, replace the current map
            this.contextLastUpdateMap = contexts;

        } catch (SQLException e) {
            throw new PersistenceException("Error loading token change data: " + e, e);

        } finally {
            db.release();

        }

    }

}