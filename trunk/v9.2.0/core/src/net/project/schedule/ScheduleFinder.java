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

 package net.project.schedule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import net.project.base.PnetRuntimeException;
import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.Finder;
import net.project.base.finder.FinderListener;
import net.project.base.finder.FinderListenerAdapter;
import net.project.base.finder.WhereClauseFilter;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.persistence.PersistenceException;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.CollectionUtils;

import org.apache.log4j.Logger;

/**
 * Object oriented approach to a SQL statement that loads schedules from the
 * database.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class ScheduleFinder extends Finder {
    private Logger logger = Logger.getLogger(ScheduleFinder.class);

    //Entry types to load
    public static final int LOAD_NO_ENTRIES = 0;
    public static final int LOAD_TASKS = 1;
    public static final int LOAD_MILESTONES = 2;
    public static final int LOAD_SUMMARY_TASKS = 4;
    public static final int LOAD_ALL = 8;


    /**
     * Selects schedule properties from the PN_PLAN table.
     * The column order must be identical to {@link #BASELINE_SQL}.
     */
    private static final String REGULAR_SQL =
        "select " +
        "  p.plan_id, " +
        "  p.plan_name, " +
        "  p.plan_desc, " +
        "  p.date_start, " +
        "  p.date_end, " +
        "  p.autocalculate_task_endpoints, " +
        "  p.overallocation_warning, " +
        "  p.default_calendar_id, " +
        "  p.timezone_id, " +
        "  p.baseline_start, " +
        "  p.baseline_end, " +
        "  p.modified_date, " +
        "  p.modified_by, " +
        "  mname.name, " +
        "  p.baseline_id, " +
        "  p.default_task_calc_type_id, " +
        "  shp.space_id," +
        "  p.earliest_start_date, " +
        "  p.earliest_finish_date, " +
        "  p.latest_start_date, " +
        "  p.latest_finish_date, " +
        "  p.constraint_type_id, " +
        "  p.constraint_date, " +
        "  SYSDATE, " + 
        "  p.inline_editing_warning, " +
        "  p.un_assigned_workcapture, "+ 
        "  p.hours_per_day, "+
        "  p.hours_per_week, "+
        "  p.days_per_month, "+
        "  p.resource_calendar " +
        "from " +
        "  pn_plan p," +
        "  pn_object_name mname, " +
        "  pn_space_has_plan shp " +
        "where " +
        "  p.modified_by = mname.object_id(+) " +
        "  and p.plan_id = shp.plan_id ";

    /**
     * Selects schedule properties from the PN_PLAN_VERSION table.
     * The column order must be identical to {@link #REGULAR_SQL}.
     */
    private static final String BASELINE_SQL =
        "select " +
        "  p.plan_id, " +
        "  p.plan_name, " +
        "  p.plan_desc, " +
        "  p.date_start, " +
        "  p.date_end, " +
        "  p.autocalculate_task_endpoints, " +
        "  p.overallocation_warning, " +
        "  p.default_calendar_id, " +
        "  p.timezone_id, " +
        "  p.baseline_start, " +
        "  p.baseline_end, " +
        "  p.modified_date, " +
        "  p.modified_by," +
        "  mname.name, " +
        "  p.baseline_id," +
        "  p.default_task_calc_type_id, " +
        "  shp.space_id, " +
        "  p.earliest_start_date, " +
        "  p.earliest_finish_date, " +
        "  p.latest_start_date, " +
        "  p.latest_finish_date, " +
        "  p.constraint_type_id, " +
        "  p.constraint_date, " +
        "  SYSDATE, " + 
        "  p.inline_editing_warning, " +
        "  p.un_assigned_workcapture, " + 
        "  p.hours_per_day, " +
        "  p.hours_per_week, " +
        "  p.days_per_month, " +
        "  p.resource_calendar, " +
        "  p.plan_version_id " +
        "from " +
        "  pn_plan_version p," +
        "  pn_object_name mname, " +
        "  pn_space_has_plan shp " +
        "where" +
        "  p.modified_by = mname.object_id " +
        "  and p.plan_id = shp.plan_id ";

    private static final int PLAN_ID_COL = 1;
    private static final int PLAN_NAME_COL = 2;
    private static final int PLAN_DESC_COL = 3;
    private static final int DATE_START_COL = 4;
    private static final int DATE_END_COL = 5;
    private static final int AUTOCALCULATE_COL = 6;
    //private static final int OVERALLOCATION_COL = 7;
    private static final int DEFAULT_CALENDAR_ID_COL = 8;
    private static final int TIMEZONE_ID_COL = 9;
    private static final int BASELINE_START_COL = 10;
    private static final int BASELINE_END_COL = 11;
    private static final int MODIFIED_DATE_COL = 12;
    private static final int MODIFIED_BY_COL = 13;
    private static final int MODIFIED_BY_NAME_COL = 14;
    private static final int BASELINE_ID_COL = 15;
    private static final int DEFAULT_TASK_CALCULATION_TYPE_ID_COL = 16;
    private static final int SPACE_ID_COL = 17;
    private static final int EARLIEST_START_DATE_COL = 18;
    private static final int EARLIEST_FINISH_DATE_COL = 19;
    private static final int LATEST_START_DATE_COL = 20;
    private static final int LATEST_FINISH_DATE_COL = 21;
    private static final int CONSTRAINT_TYPE_ID = 22;
    private static final int CONSTRAINT_DATE = 23;

    private static final int DATE_LOADED_COL = 24;
    
    private static final int EDITING_WARNING_COL = 25;
    private static final int UN_ASSIGNED_WORKCAPTURE_COL = 26;
    private static final int HOURS_PER_DAY_COL = 27;
    private static final int HOURS_PER_WEEK_COL = 28;
    private static final int DAYS_PER_MONTH_COL = 29;
    private static final int RESOURCE_CALENDAR_COL = 30;
    
    private static final int PLAN_VERSION_ID_COL = 31;

    public static final ColumnDefinition BASELINE_ID_COLDEF = new ColumnDefinition("nvl(p.baseline_id, -1)", "");
    public static final ColumnDefinition MODIFIED_DATE_COLDEF = new ColumnDefinition("p.modified_date", "");

    private boolean loadVersions = false;
    private HashSet baselinesToLoad = new HashSet();
    private HashMap scheduleToBaselineMap = new HashMap();
    private Map spaceCache;

    private int typesToLoad = LOAD_ALL;
    private boolean loadTaskAssignments = true;
    private boolean loadTaskDependencies = true;

    private FinderListener listener = new FinderListenerAdapter() {
        /**
         * This method is called after the SQL Statement has been execute and after
         * all of the objects have been loaded.
         *
         * @param db a <code>DBBean</code> which was used to load the objects.
         * @param list a <code>List</code> of objects loaded during population
         */
        public void postExecute(DBBean db, List list) throws SQLException {
            if (loadVersions) {
                try {
                    BaselineFinder bf = new BaselineFinder();

                    WhereClauseFilter baselinesForPlans = new WhereClauseFilter(
                        "  b.object_id = (" + DatabaseUtils.collectionToCSV(new LinkedList(baselinesToLoad)) +
                        ")"
                    );

                    bf.addFinderFilter(baselinesForPlans);
                    List baselines = bf.findAll();
                    Map baselineMap = CollectionUtils.listToMap(baselines,
                        new CollectionUtils.MapKeyLocator() {
                            public Object getKey(Object listObject) {
                                return ((Baseline)listObject).getID();
                            }

                        }
                    );

                    //Iterate through the plans and set the baselines
                    for (Iterator it = list.iterator(); it.hasNext();) {
                        ScheduleVersion scheduleVersion = (ScheduleVersion) it.next();
                        String baselineID = (String)scheduleToBaselineMap.get(scheduleVersion.getVersionID());
                        scheduleVersion.setBaseline((Baseline)baselineMap.get(baselineID));
                    }
                } catch (PersistenceException e) {
                    //Unable to load the baselines
                    Logger.getLogger(ScheduleFinder.class).debug("Unable to load " + "baselines for plans.", e);
                }
            }

            for (Iterator it = list.iterator(); it.hasNext();) {
                Schedule schedule = (Schedule) it.next();

                //Find all objects this object is shared as.
                List sharedObjectIDs = new ArrayList();
                db.prepareStatement("select imported_object_id from pn_shared where exported_object_id = ?");
                db.pstmt.setString(1, schedule.getID());
                db.executePrepared();

                while (db.result.next()) {
                    sharedObjectIDs.add(db.result.getString(1));
                }

                PredecessorList predecessorList = new PredecessorList();
                TaskDependencyFinder tdf = new TaskDependencyFinder();
                for (Iterator it2 = sharedObjectIDs.iterator(); it2.hasNext();) {
                    String sharedObjectID = (String) it2.next();
                    predecessorList.addAll(tdf.findByTaskID(db, sharedObjectID));
                }

                schedule.setPredecessorList(predecessorList);
            }
        }
    };

    public ScheduleFinder() {
        addFinderListener(listener);
    }

    public void setTypesToLoad(int typesToLoad) {
        this.typesToLoad = typesToLoad;
    }

    public void setLoadTaskAssignments(boolean loadTaskAssignments) {
        this.loadTaskAssignments = loadTaskAssignments;
    }

    public void setLoadTaskDependencies(boolean loadTaskDependencies) {
        this.loadTaskDependencies = loadTaskDependencies;
    }


    /**
     * This method can help the loading speed of schedule finder by removing the
     * need to load space objects.  If the space object is in the cache, it
     * won't have to be loaded.
     *
     * @param spaceCache a <code>Map</code> which maps space ID to space
     * objects.
     */
    public void setSpaceCache(Map spaceCache) {
        this.spaceCache = Collections.unmodifiableMap(spaceCache);
    }

    public List findAll() throws PersistenceException {
        loadVersions = false;
        return loadFromDB();
    }

    public List findVersions(String planID) throws PersistenceException {
        loadVersions = true;
        addWhereClause("p.plan_id = "+planID);
        return loadFromDB();
    }

    public Schedule findByPlanID(String id) throws PersistenceException {
        addWhereClause("p.plan_id = "+id);
        List schedules = loadFromDB();

        Schedule scheduleToReturn = null;
        if (schedules.size() == 1) {
            scheduleToReturn = (Schedule)schedules.get(0);
        } else if (schedules.size() > 1) {
            throw new IllegalStateException("There should only be one schedule " +
                "per plan id!");
        }

        return scheduleToReturn;
    }

    public List findByPlanIDList(List planIDList) throws PersistenceException {
        addWhereClause("p.plan_id in (" + DatabaseUtils.collectionToCSV(planIDList) +
            ")");
        return loadFromDB();
    }


    public List findBySpaceID(String spaceID) throws PersistenceException {
        addWhereClause("shp.space_id = "+spaceID);
        return loadFromDB();
    }

    /**
     * This allows us to make a space cache when we only have one space.  It
     * makes it easier for the calling function.  It doesn't have to go to the
     * trouble to create a map and to add the space to that map.
     *
     * @param space a <code>Space</code> that will appear in the space cache.
     */
    public void createSpaceCacheFromSpace(Space space) {
        this.spaceCache = new HashMap();
        spaceCache.put(space.getID(), space);
    }

    public List findBySpace(Space space) throws PersistenceException {
        createSpaceCacheFromSpace(space);
        return findBySpaceID(space.getID());
    }

    public void refreshPropertiesFromDB(Schedule schedule) throws PersistenceException {
        this.loadFromDB(schedule);
    }

    /**
     * Get the SQL statement which without any additional where clauses, group
     * by, or order by statements. <p> The SQL statement will include a
     * <code>SELECT</code> part, a <code>FROM</code> part and the
     * <code>WHERE</code> keyword.  It will include any conditional expressions
     * required to perform joins. All additional conditions will be appended
     * with an <code>AND</code> operator. </p>
     *
     * @return a <code>String</code> value containing the default sql statement
     *         without any additional adornments.
     */
    protected String getBaseSQLStatement() {
        return (loadVersions ? BASELINE_SQL : REGULAR_SQL);
    }

    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     *
     * @param results a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.  The ResultSet is assumed to be
     * on a current row.
     * @return a <code>Object</code> subclass specific to your finder that has
     *         been populated with data.
     * @throws java.sql.SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet results) throws SQLException {
        Schedule schedule;

        if (loadVersions) {
            schedule = new ScheduleVersion();
            String baselineID = results.getString(BASELINE_ID_COL);
            String planVersionID = results.getString(PLAN_VERSION_ID_COL);

            ((ScheduleVersion)schedule).setVersionID(planVersionID);
            scheduleToBaselineMap.put(planVersionID, baselineID);
            baselinesToLoad.add(baselineID);
        } else {
            schedule = new Schedule();
        }

        schedule.setID(results.getString(PLAN_ID_COL));
        schedule.setName(results.getString(PLAN_NAME_COL));
        schedule.setDescription(results.getString(PLAN_DESC_COL));
        schedule.setScheduleStartDate(DatabaseUtils.getTimestamp(results, DATE_START_COL));
        schedule.setScheduleEndDate(DatabaseUtils.getTimestamp(results, DATE_END_COL));
        schedule.setAutocalculateTaskEndpoints(results.getBoolean(AUTOCALCULATE_COL));
        schedule.setDefaultCalendarID(results.getString(DEFAULT_CALENDAR_ID_COL));

        //If there isn't a timezone stored in the database, use the current user's timezone.
        if (results.getString(TIMEZONE_ID_COL) != null) {
            schedule.setTimeZone(TimeZone.getTimeZone(results.getString(TIMEZONE_ID_COL)));
        } else {
            schedule.setTimeZone(SessionManager.getUser().getTimeZone());
        }

        schedule.setBaselineID(results.getString(BASELINE_ID_COL));
        schedule.setBaselineStart(DatabaseUtils.getTimestamp(results, BASELINE_START_COL));
        schedule.setBaselineEnd(DatabaseUtils.getTimestamp(results, BASELINE_END_COL));
        schedule.setLastModified(DatabaseUtils.getTimestamp(results, MODIFIED_DATE_COL));
        schedule.setLastModifiedByID(results.getString(MODIFIED_BY_COL));
        schedule.setLastModifiedDisplayName(results.getString(MODIFIED_BY_NAME_COL));
        schedule.setDefaultTaskCalculationType(TaskCalculationType.forID(results.getString(DEFAULT_TASK_CALCULATION_TYPE_ID_COL)));
        schedule.setEarliestStartDate(DatabaseUtils.getTimestamp(results, EARLIEST_START_DATE_COL));
        schedule.setEarliestFinishDate(DatabaseUtils.getTimestamp(results, EARLIEST_FINISH_DATE_COL));
        schedule.setLatestStartDate(DatabaseUtils.getTimestamp(results, LATEST_START_DATE_COL));
        schedule.setLatestFinishDate(DatabaseUtils.getTimestamp(results, LATEST_FINISH_DATE_COL));
        schedule.setStartConstraint(TaskConstraintType.getForID(results.getString(CONSTRAINT_TYPE_ID)));
        schedule.setStartConstraintDate(DatabaseUtils.getTimestamp(results, CONSTRAINT_DATE));
        schedule.setLoadTime(DatabaseUtils.getTimestamp(results, DATE_LOADED_COL));
        schedule.setEditingWarning(results.getBoolean(EDITING_WARNING_COL));
        schedule.setUnAssignedWorkcapture(results.getBoolean(UN_ASSIGNED_WORKCAPTURE_COL));
        schedule.setHoursPerDay(results.getBigDecimal(HOURS_PER_DAY_COL));
        schedule.setHoursPerWeek(results.getBigDecimal(HOURS_PER_WEEK_COL));
        schedule.setDaysPerMonth(results.getBigDecimal(DAYS_PER_MONTH_COL));
        schedule.setResourceCalendar(results.getString(RESOURCE_CALENDAR_COL));

        String spaceID = results.getString(SPACE_ID_COL);
        if (spaceCache != null) {
            Space space = (Space)spaceCache.get(spaceID);
            if (space != null) {
                schedule.setSpace(space);
            }
        }

        if (schedule.getSpace() == null) {
            try {
                schedule.setSpace(SpaceFactory.constructSpaceFromID(spaceID));
            } catch (PersistenceException e) {
                logger.error("Unable to construct space from spaceID", e);
            }
        }

        //Load entries, as necessary
        if (typesToLoad != LOAD_NO_ENTRIES) {
            List taskTypes = new LinkedList();
            if ((typesToLoad | LOAD_TASKS) == LOAD_TASKS)
                taskTypes.add(TaskType.TASK);
            if ((typesToLoad | LOAD_MILESTONES) == LOAD_MILESTONES)
                taskTypes.add(TaskType.MILESTONE);
            if ((typesToLoad | LOAD_SUMMARY_TASKS) == LOAD_SUMMARY_TASKS) {
                taskTypes.add(TaskType.SUMMARY);
            }
            TaskType[] typesArray = new TaskType[taskTypes.size()];

            try {
                schedule.loadEntries((TaskType[])taskTypes.toArray(typesArray), loadTaskDependencies, loadTaskAssignments);
            } catch (PersistenceException e) {
                logger.error("Unable to load entries for schedule.", e);
                throw new PnetRuntimeException("Unable to load entries for schedule.", e);
            }
        }

        return schedule;
    }
}
