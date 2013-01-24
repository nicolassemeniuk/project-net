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
package net.project.schedule.report.latetaskreport;

import java.awt.image.BufferedImage;

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.chart.ChartColor;
import net.project.chart.ChartingException;
import net.project.chart.MissingChartDataException;
import net.project.chart.PieChart;
import net.project.persistence.PersistenceException;
import net.project.portfolio.chart.NoDataFoundException;
import net.project.report.ReportData;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;

/**
 * Chart that appears on the front of the latetaskreport.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class LateTaskChart extends PieChart {
    /** Human-readable string for the "Overdue Tasks" legend entry. */
    private String OVERDUE_TASKS = PropertyProvider.get("prm.schedule.report.chart.overduetasks.name");
    /** Human-readable string for the "Overdue Milestones" legend entry. */
    private String OVERDUE_MILESTONES = PropertyProvider.get("prm.schedule.report.chart.overduemilestones.name");
    /** Human-readable string for the "Other Tasks" legend entry. */
    private String OTHER_TASKS = PropertyProvider.get("prm.schedule.report.chart.othertasks.name");
    /** Data required to create the late task chart. */
    private LateTaskReportData data;
    /**
     * Token pointing to: "{0} parameter was not found.  This is a required
     * field."
     */
    private String MISSING_PARAMETER_ERROR_TOKEN = "prm.report.errors.missingparametererror.message";
    /**
     * Token pointing to: "Unexpected error thrown while trying to load data for
     * chart."
     */
    private String LOAD_CHART_DATA_ERROR_TOKEN = "prm.global.chart.errors.loaddataerror.message";


    public LateTaskChart() {
        setShowLegend(true);
        setShowValueLabels(false);
    }

    /**
     * Populate the internal variables based on values passed from an html form.
     * The data in this object can be populated either using this method or its
     * overloaded method.
     *
     * @see #populateParameters(ReportData)
     * @param request a <code>HttpServletRequest</code> object that will be the
     * source of the data to populate a chart.
     * @throws MissingChartDataException if the parameter "objectID" was not
     * passed to this method.
     */
    public void populateParameters(HttpServletRequest request) throws MissingChartDataException {
        String id = request.getParameter("objectID");

        if ((id == null) || (id.trim().length() == 0)) {
            throw new MissingChartDataException(
                PropertyProvider.get(MISSING_PARAMETER_ERROR_TOKEN, "objectID"));
        }

        //We are going to get our data directly from the request, so we need to
        //setup it up from the parameters that were passed through http
        data = new LateTaskReportData();
        data.setSpaceID(id);
        data.updateParametersFromRequest(request);

        //Set the proposed height and width of the new chart.
        width = (request.getParameter("width") != null ? Integer.parseInt(request.getParameter("width")) : -1);
        height = (request.getParameter("height") != null ? Integer.parseInt(request.getParameter("height")) : -1);
    }

    /**
     * Provide the necessary data to load this chart based on a data object
     * already populated somewhere else.
     *
     * @param data a <code>ReportData</code> which should actually be an instance
     * of "LateTaskReportData".
     */
    public void populateParameters(ReportData data) {
        this.data = (LateTaskReportData)data;
    }

    /**
     * This method produces a pie chart that displays the number of overdue tasks,
     * overdue milestones, and uncompleted tasks in a schedule.
     *
     * @throws net.project.chart.ChartingException if any error occurs while creating the chart.
     */
    protected void setupChart(JFreeChart chart) throws Exception {
        //Load the data for this chart, if it hasn't already been loaded
        try {
            data.load();
        } catch (PersistenceException e) {
            throw new ChartingException(
                PropertyProvider.get(LOAD_CHART_DATA_ERROR_TOKEN), e);
        }

        //Check to make sure there is data found
        if (data.getDetailedData().size() == 0) {
            throw new NoDataFoundException();
        }

        //Get the data for the Chart
        LateTaskReportSummaryData trd = data.getSummaryData();

        //Create the data structure and fill it with the appropriate values
        //from the database
        dataset.setValue(OVERDUE_TASKS, new Double(trd.getOverdueTaskCount()).doubleValue());
        dataset.setValue(OVERDUE_MILESTONES, new Double(trd.getOverdueMilestoneCount()));

        //Get other tasks that aren't completed and that aren't overdue
        int otherTasks = trd.getTaskCount() - trd.getCompletedTaskCount() - trd.getOverdueTaskCount()
            - trd.getOverdueMilestoneCount();
        dataset.setValue(OTHER_TASKS, new Double(otherTasks).doubleValue());

        //Set the colors of the sections of the chart
        PiePlot piePlot = (PiePlot)chart.getPlot();
        piePlot.setSectionPaint(0, ChartColor.RED);
        piePlot.setSectionPaint(1, ChartColor.BLUE);
        piePlot.setSectionPaint(2, ChartColor.GREEN);

    }

    protected BufferedImage getExceptionImage(Exception e) {
        if (e instanceof NoDataFoundException) {
            return getNoRecordsFoundImage();
        } else {
            return super.getExceptionImage(e);
        }
    }

}

