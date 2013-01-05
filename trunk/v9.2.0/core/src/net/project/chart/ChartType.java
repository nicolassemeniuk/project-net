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
package net.project.chart;

import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.form.report.formitemsummaryreport.FormItemSummaryReportChart;
import net.project.form.report.formitemtimeseries.FormItemTimeSeriesStackedBarChart;
import net.project.portfolio.chart.PortfolioBudgetChart;
import net.project.portfolio.chart.PortfolioStatusChart;
import net.project.schedule.report.latetaskreport.LateTaskChart;
import net.project.schedule.report.taskscomingdue.TasksComingDueChart;

/**
 * This class represents an individual type of chart that can be created.
 * Instances of this class don't actually create the chart themselves, that is
 * done through an implementation of the {@link net.project.chart.IChart}
 * interface.  Instances of this class can create one of these helper classes
 * by calling the {@link #getInstance} method.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ChartType {
    /** This variable contains all "ChartType" objects that can be instantiated. */
    private static ArrayList chartTypes = new ArrayList();
    /** Chart for the late task report. */
    public static ChartType LATE_TASKS = new ChartType("ltr", LateTaskChart.class);
    /** Chart for the tasks coming due report. */
    public static ChartType TASKS_COMING_DUE = new ChartType("tcdr", TasksComingDueChart.class);
    /** Chart for the Form Item Summary Report. */
    public static ChartType FORM_ITEM_SUMMARY_REPORT = new ChartType("fisr", FormItemSummaryReportChart.class);
    /** Chart for the Form Item Time Series Report. */
    public static ChartType FORM_ITEM_TIME_SERIES_CHART = new ChartType("fits", FormItemTimeSeriesStackedBarChart.class);
    /** Chart for the Personal Portfolio showing Budget variance. */
    public static ChartType PORTFOLIO_BUDGET_CHART = new ChartType("pbc", PortfolioBudgetChart.class);
    /** Chart for the status of project in the Personal Portfolio. */
    public static ChartType PORTFOLIO_STATUS_CHART = new ChartType("psc", PortfolioStatusChart.class);

    /**
     * Given a string value that the user received with {@link #getID}, find the
     * <code>ChartType</code> instance that matches that string identifier.
     *
     * @param idToFind a <code>String</code> value which identifies the
     * <code>ChartType</code> for which we are looking.
     * @return an instance of <code>ChartType</code> that corresponds to the
     * string identifier passed into the <code>idToFind</code> parameter.
     */
    public static ChartType getForID(String idToFind) {
        ChartType toReturn = null;

        for (Iterator it = chartTypes.iterator(); it.hasNext(); ) {
            ChartType currentChart = (ChartType)it.next();
            if (currentChart.getID().equals(idToFind)) {
                toReturn = currentChart;
                break;
            }
        }

        return toReturn;
    }

    //##########################################################################
    // Concrete implementation
    //##########################################################################

    /** Unique identifier for this Chart Type. */
    private String id;
    /**
     * A class variable that allows the user to instantiate a helper object to
     * create an instance of the chart.
     */
    private Class instantiator;
    /** Exception message if unable to create chart. */
    private String UNABLE_TO_CREATE_CHART = "prm.global.chart.exception.unabletocreatenew.message";

    /**
     * This method is a standard constructor.  It has been designed so that it
     * can be subclassed to create new chart types.
     *
     * @param chartAbbreviation a <code>String</code> value which is a unique
     * id for this chart.
     * @param classInstantiator a <code>Class</code> object of the subclass of
     * IChart which produces a chart for this chart type.  In order for this
     * chart.  This parameter <b>must</b> implement
     * {@link net.project.chart.IChart} and it must be able to be constructed
     * without any parameters in order for the {@link #getInstance} method to
     * work correctly.
     */
    protected ChartType(String chartAbbreviation, Class classInstantiator) {
        chartTypes.add(this);
        id = chartAbbreviation;
        instantiator = classInstantiator;
    }

    /**
     * Get a unique identifier that corresponds to this chart type.
     *
     * @return a <code>String</code> value which uniquely identifies this type
     * of chart.
     */
    public String getID() {
        return id;
    }

    /**
     * Get an instance of the class that can produce a chart of this type.
     * Instructions for how instantiate one of these charts can be found in the
     * appropriate charting object.
     *
     * @return an <code>IChart</code> instance which can be used to create a
     * report.
     * @throws ChartingException if this method is unable to create an instance
     * of the chart for any reason.
     */
    public IChart getInstance() throws ChartingException {
        IChart newInstance = null;

        try {
            newInstance = (IChart)instantiator.newInstance();
        } catch (Exception e) {
            throw new ChartingException(PropertyProvider.get(UNABLE_TO_CREATE_CHART), e);
        }

        return newInstance;
    }
}
