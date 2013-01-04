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
package net.project.calendar.workingtime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.project.base.RecordStatus;
import net.project.base.finder.Finder;
import net.project.base.finder.FinderListener;
import net.project.base.finder.FinderListenerAdapter;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.util.CollectionUtils;

/**
 * Provides a Finder for loading <code>WorkingTimeCalendarDefinition</code>s.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class WorkingTimeCalendarFinder extends Finder {

    /** The base SQL statement for seleting working time calendar definitions. */
    private static final String BASE_SQL_STATEMENT =
            "select c.calendar_id, c.plan_id, c.is_base_calendar, c.name, " +
            "c.parent_calendar_id, c.resource_person_id, " +
            "c.record_status, p.display_name as resource_display_name " +
            "from pn_workingtime_calendar c, pn_person p " +
            "where p.person_id(+) = c.resource_person_id and c.record_status = 'A'";

    protected String getBaseSQLStatement() {
        return BASE_SQL_STATEMENT;
    }

    protected Object createObjectForResultSetRow(ResultSet databaseResults) throws SQLException {
        WorkingTimeCalendarDefinition calendarDef = new WorkingTimeCalendarDefinition();
        return createObjectForResultSetRow(databaseResults, calendarDef);
    }

    protected Object createObjectForResultSetRow(ResultSet databaseResults, Object object) throws SQLException {
        WorkingTimeCalendarDefinition calendarDef = (WorkingTimeCalendarDefinition) object;
        calendarDef.setID(databaseResults.getString("calendar_id"));
        calendarDef.setPlanID(databaseResults.getString("plan_id"));
        calendarDef.setBaseCalendar(databaseResults.getBoolean("is_base_calendar"));
        calendarDef.setName(databaseResults.getString("name"));
        calendarDef.setParentCalendarID(databaseResults.getString("parent_calendar_id"));
        calendarDef.setResourcePersonID(databaseResults.getString("resource_person_id"));
        calendarDef.setRecordStatus(RecordStatus.findByID(databaseResults.getString("record_status")));
        calendarDef.setResourceDisplayName(databaseResults.getString("resource_display_name"));
        return calendarDef;
    }

    /**
     * Populates a working time calendar definition by finding the one with
     * the specified ID.
     * @param calendarID the ID of the calendar definition to get
     * @param calendarDef the calendar definition to populate; its entries will be added also
     * @throws PersistenceException if there is a problem loading or no
     * calendar definition with that ID was found
     * @throws NullPointerException if calendarID is null
     */
    void findByID(final String calendarID, WorkingTimeCalendarDefinition calendarDef) throws PersistenceException {

        if (calendarID == null) {
            throw new NullPointerException("calendarID is required");
        }

        addWhereClause("c.calendar_id = ? ");

        // Create a listener to bind parameters
        FinderListener listener = new FinderListenerAdapter() {
            public void preExecute(DBBean db) throws SQLException {
                int index = 0;
                db.pstmt.setString(++index, calendarID);
            }
        };
        addFinderListener(listener);

        // Load the definition
        if (!this.loadFromDB(calendarDef)) {
            throw new PersistenceException("Unable to find working time calendar with ID: " + calendarID);
        }

        // Now we have populated the definition's properties, we load
        // the entries and add them to the calendar definition
        for (Iterator it = new WorkingTimeCalendarEntryFinder().findByCalendarID(calendarID).iterator(); it.hasNext();) {
            WorkingTimeCalendarEntry nextEntry = (WorkingTimeCalendarEntry) it.next();
            calendarDef.addEntry(nextEntry);
        }

    }

    /**
     * Finds active working time calendars for the specified planID.
     * <p>
     * Calendar Definitions are loaded along with all entries. Note, however, that
     * resource calendar parent calendars are _not_ resolved.
     * </p>
     * @param planID the plan from which to locate working time calendars
     * @return a collection where each element is a <code>WorkingTimeCalendarDefinition</code>;
     * may be empty if no working time calendars were found
     * @throws PersistenceException if there is a problem loading
     * @throws NullPointerException if planID is null
     */
    public Collection findByPlanID(final String planID) throws PersistenceException {

        if (planID == null) {
            throw new NullPointerException("planID is required");
        }

        addWhereClause("c.plan_id = ? ");
//        addWhereClause("c.record_status = ? ");

        // Create a listener to bind parameters
        FinderListener listener = new FinderListenerAdapter() {
            public void preExecute(DBBean db) throws SQLException {
                int index = 0;
                db.pstmt.setString(++index, planID);
//                db.pstmt.setString(++index, RecordStatus.ACTIVE.getID());
            }
        };
        addFinderListener(listener);

        // Load them; we store them in a map for easy addition of entries
        Map calendarDefinitionMap = new HashMap();
        for (Iterator it = this.loadFromDB().iterator(); it.hasNext();) {
            WorkingTimeCalendarDefinition nextcalendarDefinition = (WorkingTimeCalendarDefinition) it.next();
            calendarDefinitionMap.put(nextcalendarDefinition.getID(), nextcalendarDefinition);
        }

        // Now load entries; we update the working time calendar definitions
        for (Iterator it = new WorkingTimeCalendarEntryFinder().findByPlanID(planID).iterator(); it.hasNext();) {
            WorkingTimeCalendarEntry nextEntry = (WorkingTimeCalendarEntry) it.next();
            WorkingTimeCalendarDefinition calendarDef = (WorkingTimeCalendarDefinition) calendarDefinitionMap.get(nextEntry.getCalendarID());
            calendarDef.addEntry(nextEntry);
        }

        // Return the updated calendar definitions
        return calendarDefinitionMap.values();
    }

    public Collection findByUserID(final String userID) throws PersistenceException {
//        addWhereClause(
//            "c.calendar_id in (select "+
//            "  calendar_id "+
//            "from "+
//            "  pn_space_has_person shpr, "+
//            "  pn_space_has_plan shp, "+
//            "  pn_workingtime_calendar wtc "+
//            "where "+
//            "  shpr.space_id = shp.space_id "+
//            "  and shp.plan_id = wtc.plan_id "+
//            "  and ((wtc.resource_person_id = shpr.person_id and wtc.resource_person_id = " + userID + ") " +
//            "       or (wtc.resource_person_id is null and shpr.person_id = " + userID + "))) "
//        );
        
        if (userID == null) {
            throw new NullPointerException("userID is required");
        }
        
        addWhereClause("(c.resource_person_id = ? AND c.plan_id IS NULL)");
        
        // Create a listener to bind parameters
        FinderListener listener = new FinderListenerAdapter() {
            public void preExecute(DBBean db) throws SQLException {
                int index = 0;
                db.pstmt.setString(++index, userID);
            }
        };
        addFinderListener(listener);        

        //Load all of the working time calendars for this user
        List calendars = loadFromDB();

        //Put the calendars in a map
        Map calendarMap = CollectionUtils.listToMap(calendars, new CollectionUtils.MapKeyLocator() {
            public Object getKey(Object listObject) { return ((WorkingTimeCalendarDefinition)listObject).getID(); }
        });

        //Add the non-base calendars to their base calendar and create a list of their ID's
        Collection calendarIDList = new LinkedList();
        for (Iterator it = calendars.iterator(); it.hasNext();) {
            WorkingTimeCalendarDefinition calendarDefinition = (WorkingTimeCalendarDefinition) it.next();
            if (!calendarDefinition.isBaseCalendar()) {
                calendarDefinition.setParentCalendarDefinition(calendarMap);
            }

            calendarIDList.add(calendarDefinition.getID());
        }

        // Now load entries; we update the working time calendar definitions
        for (Iterator it = new WorkingTimeCalendarEntryFinder().findByCalendarIDs(calendarIDList).iterator(); it.hasNext();) {
            WorkingTimeCalendarEntry nextEntry = (WorkingTimeCalendarEntry) it.next();
            WorkingTimeCalendarDefinition calendarDef = (WorkingTimeCalendarDefinition)calendarMap.get(nextEntry.getCalendarID());
            calendarDef.addEntry(nextEntry);
        }

        return calendars;
    }

}
