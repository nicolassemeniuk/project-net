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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

/**
 * Provides mechanisms for loading adefault view settings from persistent store
 * and storing settings to persistent store.
 *
 * @since Version 7.4
 * @author Tim Morrow
 */
public class DefaultViewProvider {

    /**
     * Returns the default view settings for the specified context ID.
     * This loads the settings from the database and creates <code>DefaultViewSetting</code>s
     * based on the specified scenarios.
     *
     * @param contextID the context from which to load settings; this is
     * an arbitrary designator that paritions settings for different view
     * contexts
     * @param defaultScenarioCollection the collection where each element
     * is a <code>DefaultScenario</code>
     * @return the collection of view settings; this collection will contain
     * the same number of entries as the default scenarios; some settings
     * may have empty view IDs
     * @throws NullPointerException if contextID is null or defaultScenarioCollection is null
     */
    public Collection getDefaultViewSettings(String contextID, Collection defaultScenarioCollection)
            throws PersistenceException {

        if (contextID == null || defaultScenarioCollection == null) {
            throw new NullPointerException("contextID or defaultScenarioCollection is required");
        }

        // First load all the default views from the database
        StringBuffer query = new StringBuffer();
        query.append("select scenario_id, view_id from pn_view_default_setting d ");
        query.append("where context_id = ? ");

        Map defaultViewMap = new HashMap();
        DBBean db = new DBBean();

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, contextID);
            db.executePrepared();

            while (db.result.next()) {
                defaultViewMap.put(db.result.getString("scenario_id"), db.result.getString("view_id"));
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("Error loading default view settings: " + sqle, sqle);

        } finally {
            db.release();

        }

        // Now construct the settings based on the scenarios
        // For each scenaio, we look-up to see if we loaded a default view ID
        // for that scenario
        List defaultViews = new ArrayList();

        for (Iterator it = defaultScenarioCollection.iterator(); it.hasNext(); ) {
            DefaultScenario nextScenario = (DefaultScenario) it.next();

            // We don't actually care if the viewID is null (that is, we have
            // no default for the scenario).  The setting allows null view IDs
            // It simply means that there is no default view ID for the scenario
            String viewID = (String) defaultViewMap.get(nextScenario.getID());
            DefaultViewSetting defaultViewSetting = new DefaultViewSetting(nextScenario, viewID);
            defaultViews.add(defaultViewSetting);
        }

        return defaultViews;
    }

    /**
     * Stores the specified default view settings in persistent store for
     * the specified context ID.
     * Only settings which have a value are stored.
     *
     * @param contextID the context against which to store the settings
     * @param defaultViewSettings the settings to store, each element is a
     * <code>DefaultViewSetting</code>
     * @throws PersistenceException if there is a problem storing
     * @throws NullPointerException if contextID is null or defaultViewSettings is null
     */
    public void storeDefaultViewSettings(String contextID, Collection defaultViewSettings)
            throws PersistenceException {

        if (contextID == null || defaultViewSettings == null) {
            throw new NullPointerException("contextID or defaultViewSettings is required");
        }

        StringBuffer deleteQuery = new StringBuffer();
        deleteQuery.append("delete from pn_view_default_setting ");
        deleteQuery.append("where context_id = ? ");

        StringBuffer insertQuery = new StringBuffer();
        insertQuery.append("insert into pn_view_default_setting ");
        insertQuery.append("(context_id, scenario_id, view_id) ");
        insertQuery.append("values (?, ?, ?) ");

        DBBean db = new DBBean();

        try {
            db.setAutoCommit(false);

            // First delete all settings for the current context
            int index = 0;
            db.prepareStatement(deleteQuery.toString());
            db.pstmt.setString(++index, contextID);
            db.executePrepared();

            // Next insert the settings that have values
            // We take care to ensure we only perform the insert query
            // if we had at least one setting with a value
            boolean isExecuteInsertRequired = false;
            db.prepareStatement(insertQuery.toString());

            for (Iterator it = defaultViewSettings.iterator(); it.hasNext(); ) {
                DefaultViewSetting nextSetting = (DefaultViewSetting) it.next();

                if (nextSetting.hasValue()) {
                    index = 0;
                    db.pstmt.setString(++index, contextID);
                    db.pstmt.setString(++index, nextSetting.getScenario().getID());
                    db.pstmt.setString(++index, nextSetting.getDefaultID());
                    db.pstmt.addBatch();
                    isExecuteInsertRequired = true;
                }
            }
            if (isExecuteInsertRequired) {
                db.executePreparedBatch();
            }

            db.commit();

        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException sqle2) {
                // Simply release
            }
            throw new PersistenceException("Error storing default view settings:" + sqle, sqle);

        } finally {
            db.release();
        }

    }

}
