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

 package net.project.schedule.gantt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.schedule.Schedule;
import net.project.security.SessionManager;
import net.project.util.DateUtils;

/**
 * A typed enumeration of TimeScale classes.
 * 
 * @author Matthew Flower
 * @since Version 7.7
 */
public class TimeScale {
    /** This list contains all of the possible types for this typed enumeration. */
    private static Map types = new HashMap();
    public static final TimeScale HOURLY = new TimeScale("0", "", 480);
    public static final TimeScale DAILY = new TimeScale("1", "", 15) {
        public long getOffset(Schedule schedule) {
            PnCalendar cal = new PnCalendar(SessionManager.getUser());
            Date ganttStartDate = getGanttStartDate(schedule);

            Date weekStart = cal.startOfWeek(ganttStartDate);
            return DateUtils.daysBetween(cal, weekStart, ganttStartDate, true);
        }
    };
    public static final TimeScale WEEKLY = new TimeScale("2", "", 7){
        public long getOffset(Schedule schedule) {
            PnCalendar cal = new PnCalendar(SessionManager.getUser());
            Date ganttStartDate = getGanttStartDate(schedule);
            
            Date monthStart = cal.startOfMonth(ganttStartDate);
            return DateUtils.daysBetween(cal, monthStart, ganttStartDate, true);
        }
    };
    public static final TimeScale MONTHLY = new TimeScale("3", "", 5.28f){
        public long getOffset(Schedule schedule) {
            PnCalendar cal = new PnCalendar(SessionManager.getUser());
            Date ganttStartDate = getGanttStartDate(schedule);
            
            Date monthStart = cal.startOfMonth(ganttStartDate);
            return DateUtils.daysBetween(cal, monthStart, ganttStartDate, true);
        }
    };
    public static final TimeScale QUARTERLY = new TimeScale("4", "", 1.133f) {
        public long getOffset(Schedule schedule) {
            PnCalendar cal = new PnCalendar(SessionManager.getUser());
            Date ganttStartDate = getGanttStartDate(schedule);
            
            Date quarterStart = DateUtils.startOfQuarter(ganttStartDate);
            return DateUtils.daysBetween(cal, quarterStart, ganttStartDate, true);
        }
    };
    public static final TimeScale HALF_YEARLY = new TimeScale("5", "", 0.6575f){
        public long getOffset(Schedule schedule) {
            PnCalendar cal = new PnCalendar(SessionManager.getUser());
            Date ganttStartDate = getGanttStartDate(schedule);
            
            Date halfStart = DateUtils.startOfHalf(ganttStartDate);
            return DateUtils.daysBetween(cal, halfStart, ganttStartDate, true);
        }
    };
    public static final TimeScale YEARLY_WITH_QUARTER_MARKS = new TimeScale("6", "", 0.27f){
        public long getOffset(Schedule schedule) {
            PnCalendar cal = new PnCalendar(SessionManager.getUser());
            Date ganttStartDate = getGanttStartDate(schedule);
            
            Date yearStart = DateUtils.startOfYear(ganttStartDate);
            return DateUtils.daysBetween(cal, yearStart, ganttStartDate, true);
        }
    };
    public static final TimeScale YEARLY_WITH_HALF_MARKS = new TimeScale("7", "", 0.136f){
        public long getOffset(Schedule schedule) {
            PnCalendar cal = new PnCalendar(SessionManager.getUser());
            Date ganttStartDate = getGanttStartDate(schedule);
            
            Date yearStart = DateUtils.startOfYear(ganttStartDate);
            return DateUtils.daysBetween(cal, yearStart, ganttStartDate, true);
        }
    };

    /**
     * Get the TimeScale that corresponds to a given ID.
     * 
     * @param id a <code>String</code> value which is the primary key of a
     * <code>TimeScale</code> we want to find.
     * @return a <code>TimeScale</code> corresponding to the supplied ID, or the
     *         DEFAULT <code>TimeScale</code> if one cannot be found.
     */
    public static TimeScale getForID(String id) {
        return (TimeScale)types.get(id);
    }

    //**************************************************************************
    // Implementation code
    //**************************************************************************
    /** A Unique identifier for this TimeScale */
    private String id;
    /** A token used to find a human-readable name for this TimeScale */
    private String displayToken;
    private float pixelsPerDay;

    /**
     * Private constructor which creates a new TimeScale instance.
     * 
     * @param id a <code>String</code> value which is a unique identifier for
     * this typed enumeration.
     * @param displayToken a <code>String</code> value which contains a token to
     * look up the display name for this type.
     */
    private TimeScale(String id, String displayToken, float pixelsPerDay) {
        this.id = id;
        this.displayToken = displayToken;
        this.pixelsPerDay = pixelsPerDay;
        types.put(id, this);
    }

    /**
     * Get the unique identifier for this type enumeration.
     * 
     * @return a <code>String</code> value containing the unique id for this
     *         type.
     */
    public String getID() {
        return id;
    }

    /**
     * Return a human-readable display name for this TimeScale.
     * 
     * @return a <code>String</code> containing a human-readable version of this
     *         TimeScale.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }

    /**
     * Returns the offset of start of the schedule from the start date of the period
     * 
     * @param schedule the <code>Schedule</code> to pass
     * @return the offset from start of the period
     */
    public long getOffset(Schedule schedule) {
        return 0;
    }

    public float getPixelsPerDay() {
        return this.pixelsPerDay;
    }

    /**
     * Returns the start date of the schedule, from where Gantt Chart
     * is to start.
     * 
     * @param schedule
     * @return the start date
     */
    protected Date getGanttStartDate(Schedule schedule) {
        Date ganttStartDate = schedule.getScheduleStartDate();
        if (ganttStartDate == null) {
            ganttStartDate = new Date();
        }
        if (schedule.getEarliestTaskStartTimeMS() != -1) {
            ganttStartDate = DateUtils.min(ganttStartDate, new Date(schedule.getEarliestTaskStartTimeMS()));
        }
        return ganttStartDate;
    }
}
