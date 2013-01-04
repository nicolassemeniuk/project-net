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
package net.project.chart;

import java.awt.image.BufferedImage;

import javax.servlet.http.HttpServletRequest;

import net.project.report.ReportData;

/**
 * This interface defines a way that the
 * {@link net.project.chart.servlet.ChartingServlet} interfaces with the
 * individual charts.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public interface IChart {
    /**
     * Populate the internal data structures with all of the information that is
     * necessary to construct a report of this type.
     *
     * @param request a <code>HttpServletRequest</code> variable that this method
     * will use to get all the necessary variables to provide this chart with
     * data.
     * @throws MissingChartDataException if one or more variables required to
     * create this chart are missing.
     */
    public abstract void populateParameters(HttpServletRequest request) throws MissingChartDataException;

    /**
     * Populate the internal data structures with data that the chart object
     * needs to produce a chart.
     *
     * @param data a <code>ReportData</code> subclass specific to the current
     * chart that has all the data required to produce a chart.
     */
    public abstract void populateParameters(ReportData data);

    /**
     * Get a <code>BufferedImage</code> of a chart.
     *
     * @return a <code>BufferedImage</code> of a chart that corresponds to the input data.
     * @throws net.project.chart.ChartingException whenever any exception occurs.  Generally, this will only
     * occur when there is a database error or when someone programming a chart has made an
     * error in their logic.
     */
    public abstract BufferedImage getChart() throws ChartingException;
}





