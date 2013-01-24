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
package net.project.api.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskFinder;

/**
 * Provides a selection of 300 schedule entries for a person.
 * <p>
 * 15 tasks are selected from each of 20 projects that a
 * person belongs to.
 * </p>
 * @author Tim Morrow
 * @since Version 7.6.4
 */
public class ScheduleEntrySelector {

    /** Number of projects per person. */
    private static final int NUM_PROJECTS = 20;

    /** Number of tasks from those projects. */
    private static final int NUM_TASKS = 15;

    private final Random random = new Random();

    /** The current person. */
    private final String personID;

    /** A cache in which to place loaded schedules. */
    private final ScheduleCache scheduleCache;

    /** A cache in which to place loaded schedule entries. */
    private final ScheduleEntryCache scheduleEntryCache;

    /** ScheduleEntryUpdate items representing tasks to update. */
    private final Collection updateScheduleEntries = new ArrayList();

    public ScheduleEntrySelector(String personID, ScheduleCache scheduleCache, ScheduleEntryCache scheduleEntryCache) {
        this.personID = personID;
        this.scheduleCache = scheduleCache;
        this.scheduleEntryCache = scheduleEntryCache;
    }

    /**
     * Returns the ID of the person selecting the schedule entries.
     * @return the person ID
     */
    public String getPersonID() {
        return this.personID;
    }

    /**
     * Returns an unmodifiable collection of the schedule entries to
     * update.
     * <p>
     * Call {@link #load} first.
     * </p>
     * @return a collection where each element is a <code>ScheduleEntryUpdate</code>
     * or an empty collection if none were loaded
     */
    public Collection getScheduleEntryUpdates() {
        return Collections.unmodifiableCollection(this.updateScheduleEntries);
    }

    /**
     * Loads 300 tasks to update.
     * Adds to the scheduleEntryCache.
     * @throws PersistenceException if there is a problem loading
     */
    public void load() throws PersistenceException {

        Collection updateTasks = new ArrayList();

        for (Iterator it = selectProjectSchedules(personID, NUM_PROJECTS).iterator(); it.hasNext();) {
            ProjectSchedule nextProjectSchedule = (ProjectSchedule) it.next();

            // Now grab 15 tasks for this space
            for (Iterator it2 = getRandomTasks(nextProjectSchedule, NUM_TASKS).iterator(); it2.hasNext(); ) {
                ScheduleEntry nextScheduleEntry = (ScheduleEntry) it2.next();

                // Create a new Update item
                ScheduleEntryUpdate update = new ScheduleEntryUpdate(nextProjectSchedule, nextScheduleEntry);
                updateTasks.add(update);

                // Add the task to the scheduleEntryCache
                this.scheduleEntryCache.add(nextScheduleEntry);
            }

        }

        this.updateScheduleEntries.addAll(updateTasks);
    }

    /**
     * Returns random tasks from the specified project schedule.
     * @param projectSchedule the project schedule from which to load tasks
     * @param numTasks the number of tasks to return
     * @return a collection where each element is a <code>ScheduleEntry</code>
     * @throws PersistenceException if there is a problem loading
     */
    private Collection getRandomTasks(ProjectSchedule projectSchedule, int numTasks)
            throws PersistenceException {

        // Load all the tasks for the space
        List allTasks = new TaskFinder().findBySpaceID(projectSchedule.getSpaceID());

        Collection selectedTasks = new ArrayList();

        for (int i = 0; i < numTasks; i++) {
            int index = random.nextInt(allTasks.size());
            selectedTasks.add(allTasks.remove(index));
        }

        return selectedTasks;
    }

    /**
     * Selects a somewhat random sampling of the specified number of projects that
     * the specified person is a member of.
     * <p>
     * Selects a 2% sample from all the spaces that a person is a member of
     * then limits the results.
     * </p>
     * <p>
     * Updates the schedule cache with loaded schedules.
     * </p>
     * @param personID the ID of the person for whom to select projects
     * @param numProjects the number of projects to select
     * @return a collection of ProjectSchedules
     * @throws PersistenceException if there is a problem loading or
     * less than the specified number of projects were found
     */
    private Collection selectProjectSchedules(String personID, int numProjects) throws PersistenceException {

        final String query =
                "select proj.space_id, shplan.plan_id " +
                "  from (select space_id " +
                "          from pn_space_has_person sample (2.5) " +
                "         where person_id = ? and space_id != person_id) proj, " +
                "       pn_space_has_plan shplan " +
                " where shplan.space_id = proj.space_id and rownum <= ? ";

        Collection projectSchedules = new ArrayList();

        DBBean db = new DBBean();

        try {
            int index = 0;
            db.prepareStatement(query);
            db.pstmt.setString(++index, personID);
            db.pstmt.setInt(++index, numProjects);
            db.executePrepared();
            while (db.result.next()) {
                ProjectSchedule projectSchedule = new ProjectSchedule(db.result.getString("space_id"), db.result.getString("plan_id"));

                // Now load and cache the schedule
                Schedule schedule = new Schedule();
                schedule.setID(projectSchedule.getScheduleID());
                schedule.load();

                // Force loading of workingtime calendars
                schedule.getWorkingTimeCalendarProvider();

                // Save the project schedule and cache the schedule
                projectSchedules.add(projectSchedule);
                this.scheduleCache.add(schedule);
            }

        } catch (SQLException e) {
            throw new PersistenceException("Error selecting a sample of projectSchedules", e);

        } finally {
            db.release();
        }

        if (projectSchedules.size() < numProjects) {
            throw new PersistenceException("Unable to select " + numProjects + " projectSchedules; found only " + projectSchedules.size());
        }

        return projectSchedules;
    }

    /**
     * Represents a project/schedule combination.
     */
    private static class ProjectSchedule {
        private final String spaceID;
        private final String scheduleID;
        private ProjectSchedule(String spaceID, String scheduleID) {
            this.spaceID = spaceID;
            this.scheduleID = scheduleID;
        }

        private String getSpaceID() {
            return this.spaceID;
        }

        private String getScheduleID() {
            return this.scheduleID;
        }
    }

    /**
     * Represents a single ScheduleEntry to update.
     */
    public static class ScheduleEntryUpdate {

        private final ProjectSchedule projectSchedule;
        private final ScheduleEntry scheduleEntry;

        private ScheduleEntryUpdate(ProjectSchedule projectSchedule, ScheduleEntry scheduleEntry) {
            this.projectSchedule = projectSchedule;
            this.scheduleEntry = scheduleEntry;
        }

        public String getSpaceID() {
            return this.projectSchedule.getSpaceID();
        }

        public String getScheduleID() {
            return this.projectSchedule.getScheduleID();
        }

        public String getScheduleEntryID() {
            return scheduleEntry.getID();
        }


    }
}
