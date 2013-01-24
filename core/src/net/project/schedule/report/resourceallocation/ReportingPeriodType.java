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
package net.project.schedule.report.resourceallocation;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.security.SessionManager;
import net.project.util.DateUtils;

/**
 * A typed enumeration of ReportingPeriodType classes.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public abstract class ReportingPeriodType {
    /** This list contains all of the possible types for this typed enumeration. */
    private static List types = new ArrayList();
    public static final ReportingPeriodType DAILY_REPORT = new ReportingPeriodType(
        "10",
        "prm.schedule.report.resourceallocation.daygrouping.name",
        "select " +
        "  cal.cal_date as cal_date, " +
        "  cal.cal_date as allocation_start_date, " +
        "  cal.cal_date+0.99999 as allocation_finish_date, " +
        "  t.task_id, " +
        "  t.task_name, " +
        "  a.work, " +
        "  t.duration, " +
        "  t.date_start, " +
        "  t.date_finish, " +
        "  p.person_id as resource_id, " +
        "  p.display_name, " +
        "  a.percent_allocated " +
        "from " +
        "  pn_assignment a, " +
        "  pn_task t, " +
        "  pn_person p, " +
        "  (select  " +
        "     @@@startdate@@@+pv.x cal_date " +
        "   from  " +
        "      pn_pivot pv  " +
        "   where  " +
        "      pv.x <= (select least(@@@finishdate@@@-@@@startdate@@@,9999) from dual)) cal " +
        "where " +
        "  a.object_id = t.task_id " +
        "  and a.person_id = p.person_id " +
        "  and a.record_status = 'A' " +
        "  and t.record_status = 'A' " +
        "  and a.start_date < cal.cal_date+1 " +
        "  and a.end_date >= cal.cal_date "
    ) {
        protected Date fixDate(Date originalDate) {
            PnCalendar cal = new PnCalendar(SessionManager.getUser());
            return cal.startOfDay(originalDate);
        }
    };
    public static final ReportingPeriodType WEEKLY_REPORT = new ReportingPeriodType(
        "20",
        "prm.schedule.report.resourceallocation.weekgrouping.name",
        "select " +
        "  cal.cal_date as cal_date, " +
        "  greatest(cal.cal_date, t.date_start) as allocation_start_date, " +
        "  least(cal.cal_date+6, t.date_finish) as allocation_finish_date, " +
        "  t.task_id, " +
        "  t.task_name, " +
        "  a.work, " +
        "  t.duration, " +
        "  t.date_start, " +
        "  t.date_finish, " +
        "  p.person_id as resource_id, " +
        "  p.display_name, " +
        "  a.percent_allocated " +
        "from " +
        "  pn_assignment a, " +
        "  pn_task t, " +
        "  pn_person p, " +
        "  (select " +
        "     @@@startdate@@@+(pv.x*7) cal_date " +
        "   from " +
        "      pn_pivot pv " +
        "   where " +
        "      pv.x <= (select least(@@@finishdate@@@-@@@startdate@@@,9999) from dual)) cal " +
        "where " +
        "  a.object_id = t.task_id " +
        "  and a.person_id = p.person_id " +
        "  and a.record_status = 'A' " +
        "  and t.record_status = 'A' " +
        "  and a.start_date < cal.cal_date+7 " +
        "  and a.end_date >= cal.cal_date "
    ) {
        protected Date fixDate(Date originalDate) {
            PnCalendar cal = new PnCalendar(SessionManager.getUser());
            return cal.startOfWeek(originalDate);
        }
    };
    public static final ReportingPeriodType MONTHLY_REPORT = new ReportingPeriodType(
        "30",
        "prm.schedule.report.resourceallocation.monthgrouping.name",
        "select " +
        "  cal.cal_date as cal_date, " +
        "  greatest(cal.cal_date, t.date_start) as allocation_start_date, " +
        "  least(add_months(cal.cal_date, 1)-1, t.date_finish) as allocation_finish_date, " +
        "  t.task_id, " +
        "  t.task_name, " +
        "  a.work, " +
        "  t.duration, " +
        "  t.date_start, " +
        "  t.date_finish, " +
        "  p.person_id as resource_id, " +
        "  p.display_name, " +
        "  a.percent_allocated " +
        "from " +
        "  pn_assignment a, " +
        "  pn_task t, " +
        "  pn_person p, " +
        "  (select " +
        "     add_months(@@@startdate@@@,pv.x) cal_date " +
        "   from " +
        "      pn_pivot pv " +
        "   where " +
        "      pv.x <= (select least(months_between(@@@finishdate@@@, @@@startdate@@@), 999) from dual)) cal " +
        "where " +
        "  a.object_id = t.task_id " +
        "  and a.person_id = p.person_id " +
        "  and a.record_status = 'A' " +
        "  and t.record_status = 'A' " +
        "  and a.start_date <= add_months(cal.cal_date, 1) " +
        "  and a.end_date >= cal.cal_date "
    ) {
        protected Date fixDate(Date originalDate) {
            PnCalendar cal = new PnCalendar(SessionManager.getUser());
            return cal.startOfMonth(originalDate);
        }
    };
    public static final ReportingPeriodType DEFAULT = WEEKLY_REPORT;

    /**
     * Get the ReportingPeriodType that corresponds to a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of a
     * <code>ReportingPeriodType</code> we want to find.
     * @return a <code>ReportingPeriodType</code> corresponding to the supplied ID, or
     * the DEFAULT <code>ReportingPeriodType</code> if one cannot be found.
     */
    public static ReportingPeriodType getForID(String id) {
        ReportingPeriodType toReturn = DEFAULT;

        for (Iterator it = types.iterator(); it.hasNext();) {
            ReportingPeriodType type = (ReportingPeriodType)it.next();
            if (type.getID().equals(id)) {
                toReturn = type;
                break;
            }
        }

        return toReturn;
    }

    //**************************************************************************
    // Implementation code
    //**************************************************************************
    /** A Unique identifier for this ReportingPeriodType */
    private String id;
    /** A token used to find a human-readable name for this ReportingPeriodType */
    private String displayToken;
    /** Base SQL statement which will be used to construct the SQL statement. */
    private String baseSQLStatement;

    /**
     * Private constructor which creates a new ReportingPeriodType instance.
     *
     * @param id a <code>String</code> value which is a unique identifier for
     * this typed enumeration.
     * @param displayToken a <code>String</code> value which contains a token to
     * look up the display name for this type.
     */
    private ReportingPeriodType(String id, String displayToken, String baseSQLStatement) {
        this.id = id;
        this.displayToken = displayToken;
        this.baseSQLStatement = baseSQLStatement;
        types.add(this);
    }

    /**
     * Start date for a reporting period should be at the beginning of the time
     * unit associated with that reporting period.  For example, for a week, the
     * start date should be at the beginning of the week.  (Which is locale
     * specific.)  For a month, the start of the reporting period should be on
     * the first day of the month.
     *
     * @param originalDate a <code>Date</code> object indicating the earliest
     * "Allocation Date" found in the resource allocation report.
     * @return a <code>Date</code> which indicates the correct date to start
     * summarizing allocation data.
     */
    protected abstract Date fixDate(Date originalDate);

    public String getSQLStatement(Date startDate, Date finishDate) {
        String sqlStatement = baseSQLStatement;

        //if there isn't a start date, it will cause the sql statement to fail.
        //Set it to today instead.
        if (startDate == null) {
            startDate = new Date();
        }
        if (finishDate == null) {
            finishDate = new Date();
        }

        startDate = fixDate(startDate);
        sqlStatement = sqlStatement.replaceAll("@@@startdate@@@",
            DateUtils.getDatabaseDateString(startDate));
        sqlStatement = sqlStatement.replaceAll("@@@finishdate@@@",
            DateUtils.getDatabaseDateString(finishDate));
        return sqlStatement;
    }

    /**
     * Get the unique identifier for this type enumeration.
     *
     * @return a <code>String</code> value containing the unique id for this
     * type.
     */
    public String getID() {
        return id;
    }

    /**
     * Return a human-readable display name for this ReportingPeriodType.
     *
     * @return a <code>String</code> containing a human-readable version of this
     * ReportingPeriodType.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }
}
