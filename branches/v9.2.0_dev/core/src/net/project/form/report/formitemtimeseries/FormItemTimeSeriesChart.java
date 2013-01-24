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

 package net.project.form.report.formitemtimeseries;

import javax.servlet.http.HttpServletRequest;

import net.project.chart.ChartProxy;
import net.project.chart.IChart;

/**
 * Proxy the appropriate type of chart according to what the use has selected
 * in the charting parameters.
 *
 * @author Matthew Flower
 * @since 16 Dec 2005
 */
public class FormItemTimeSeriesChart extends ChartProxy {
    protected IChart chooseChart(HttpServletRequest request) {
        String chartTypeParam = request.getParameter("renderedChartType");
        FormItemTimeSeriesChartType chartType = FormItemTimeSeriesChartType.getForID(chartTypeParam);
        return chartType.getImplementation();
    }
}
