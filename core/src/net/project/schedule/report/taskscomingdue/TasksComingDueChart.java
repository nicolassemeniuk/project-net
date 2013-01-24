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

 package net.project.schedule.report.taskscomingdue;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.chart.ChartingException;
import net.project.chart.LineChart;
import net.project.chart.MissingChartDataException;
import net.project.persistence.PersistenceException;
import net.project.portfolio.chart.NoDataFoundException;
import net.project.report.ReportData;
import net.project.schedule.ScheduleEntry;
import net.project.security.SessionManager;
import net.project.util.DateUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

import org.jfree.chart.JFreeChart;

/**
 * Class which creates a filled line graph for the late task report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class TasksComingDueChart extends LineChart {
    /** Data that required to construct the chart. */
    private TasksComingDueData data;
    /**
     * Token pointing to: "{0} parameter was not found.  This is a required
     * field."
     */
    private String MISSING_PARAMETER_ERROR_TOKEN = "prm.report.errors.missingparametererror.message";
    /**
     * Token pointing to: "Unable to create chart due to unexpected persistence
     * exception."
     */
    private String UNEXPECTED_PERSISTENCE_EXCEPTION_TOKEN = "prm.global.chart.errors.unexpectedpersistenceerror.message";
    /**
     * Label for the vertical axis.  ("Total Number of Tasks Due")
     */
    //private String VERTICAL_AXIS_LABEL = "Tasks Due";
    private String VERTICAL_AXIS_LABEL = "prm.schedule.report.taskscomingdue.chart.verticalaxis.name";
    /**
     * Label for the horizontal axis.  ("Number of Days from today")
     */
    private String HORIZONTAL_AXIS_LABEL = "prm.schedule.report.taskscomingdue.chart.horizontalaxis.name";

    public TasksComingDueChart() {
        setShowLegend(false);
        setValueTitle(PropertyProvider.get(VERTICAL_AXIS_LABEL));
        setValueFont(new Font("Arial", Font.BOLD, 10));
        setValueLabelsAsIntegersOnly(true);
    }

    /**
     * Populate the internal variables necessary to create the report.
     *
     * @param request the source for the internal data.
     * @throws MissingChartDataException if one or more required parameters are
     * missing from the form post.
     */
    public void populateParameters(HttpServletRequest request) throws MissingChartDataException {
        String id = request.getParameter("objectID");

        if ((id == null) || (id.trim().length() == 0)) {
            throw new MissingChartDataException(
                PropertyProvider.get(MISSING_PARAMETER_ERROR_TOKEN, "objectID"));
        }

        //Construct a data object based on data based to this object
        data = new TasksComingDueData();
        data.setSpaceID(id);
        data.updateParametersFromRequest(request);

        //Set the proposed width and height if the information is available.
        this.width = (request.getParameter("width") != null ? Integer.parseInt(request.getParameter("width")) : -1);
        this.height = (request.getParameter("height") != null ? Integer.parseInt(request.getParameter("height")) : -1);
    }

    /**
     * Populate the internal data structures needed to produce this chart using
     * a data object that has already been populated.
     *
     * @param data a <code>ReportData</code> object (which really needs to be a
     * <code>TasksComingDueData</code> object that will provide the necessary
     * information to create this chart.
     */
    public void populateParameters(ReportData data) {
        this.data = (TasksComingDueData)data;
    }

    protected void setupChart(JFreeChart chart) throws Exception {
        try {
            data.load();
        } catch (PersistenceException pe) {
            throw new ChartingException(PropertyProvider.get(UNEXPECTED_PERSISTENCE_EXCEPTION_TOKEN), pe);
        }

        if (data.getDetailedData().size() == 0) {
            throw new NoDataFoundException();
        }

        //todonow -- fix this
//        lineChart.setStackedOn(true);
//        lineChart.setLabelAngle("rangeAxisLabelAngle", 270);
//        //lineChart.setLabelAngle("sampleAxisLabelAngle", 90);

        //Configure the data (and the axes)
        //Get a calendar object for the current user so we can figure out when
        //the beginning of the day is.
        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        Date todaysDate = cal.startOfDay(new Date());

        //Get a hashmap to store the number of values for any given day
        HashMap valueCounts = new HashMap();

        //Fill the hashmap will all of the values
        for (Iterator it = data.getDetailedData().iterator(); it.hasNext();) {
            ScheduleEntry task = (ScheduleEntry)it.next();

            Long daysBetween = new Long(DateUtils.daysBetween(cal, todaysDate, cal.startOfDay(task.getEndTime())));
            Integer frequency = (Integer)valueCounts.get(daysBetween);
            if (frequency == null) {
                valueCounts.put(daysBetween, new Integer(1));
            } else {
                valueCounts.put(daysBetween, new Integer(frequency.intValue() + 1));
            }
        }

        ArrayList keys = new ArrayList(valueCounts.keySet());
        Collections.sort(keys);

        for (Iterator it = keys.iterator(); it.hasNext();) {
            Long key = (Long)it.next();
            Integer value = (Integer)valueCounts.get(key);

            TimeQuantity daysAway = new TimeQuantity(key, TimeQuantityUnit.DAY);
            dataset.setValue(value, "Days", daysAway.toShortString(0,Integer.MAX_VALUE));
        }
    }

    protected BufferedImage getExceptionImage(Exception e) {
        if (e instanceof NoDataFoundException) {
            return getNoRecordsFoundImage();
        } else {
            return super.getExceptionImage(e);
        }
    }

}
