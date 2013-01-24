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

 package net.project.chart;

import java.awt.image.BufferedImage;

import javax.servlet.http.HttpServletRequest;

import net.project.report.ReportData;

/**
 * This chart proxy is good to use when you have a chart that can be rendered as
 * a bar chart and a line graph, or any combination of more than one chart.
 *
 * To have this capability, override this class and implement the "chooseChart"
 * method.  Make sure to return an appropriate subclass of IChart for the
 * parameters provided to your chart.
 *
 * @author Matthew Flower
 * @since 16 Dec 2005
 */
public abstract class ChartProxy implements IChart {
    protected IChart chartImplementation;

    /**
     * Subclasses use this method to choose the appropriate implemented of IChart
     * for their needs.
     *
     * If you don't need to be choosing an implementation of IChart dynamically,
     * you shouldn't be using this object, and you should use the appropriate
     * subclass of PieChart, BarChart, LineChart, etc.
     *
     * @param request a <code>HttpServletRequest</code> containing the parameters
     * from the parameters page of the report setup.
     * @return an <code>implementation</code> of IChart which is appropriate for
     * the situation.
     */
    protected abstract IChart chooseChart(HttpServletRequest request);

    public void populateParameters(HttpServletRequest request) throws MissingChartDataException {
        chartImplementation.populateParameters(request);
    }

    public void populateParameters(ReportData data) {
        chartImplementation.populateParameters(data);
    }

    public BufferedImage getChart() throws ChartingException {
        return chartImplementation.getChart();
    }
}
