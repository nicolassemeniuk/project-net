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

import java.awt.Font;

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.chart.MissingChartDataException;
import net.project.chart.XYChart;
import net.project.report.ReportData;

/**
 * Object-oriented hack.  Combine the functionality done with the line chart
 * and bar chart versions of the FormItemTimeSeries chart into a single location.
 *
 * @author Matthew Flower
 * @since 16 Dec 2005
 */
public class FormItemTimeSeriesChartHelper {
    /** Data required to construct the chart. */
    private FormItemTimeSeriesReportData data = new FormItemTimeSeriesReportData();

    /**
     * Token pointing to: "Unexpected error while producing chart."
     */
    private String UNEXPECTED_ERROR_TOKEN = "prm.global.chart.errors.unexpectederror.message";
    /** Label for the vertical axis. "Frequency". */
    private String FREQUENCY = "prm.form.chart.formitemtimeseries.verticalaxis.name";
    /** Label for the horizontal axis. "Date". */
    private String DATE = "prm.form.chart.formitemtimeseries.horizontalaxis.name";

    public void onConstruct(XYChart chart) {
        chart.setChartTitle("");
        chart.setCategoryTitle(PropertyProvider.get(DATE));
        chart.setCategoryFont(new Font("Arial", Font.BOLD, 12));
        chart.setValueTitle(PropertyProvider.get(FREQUENCY));
        chart.setValueFont(new Font("Arial", Font.BOLD, 12));
        chart.setValueLabelsAsIntegersOnly(true);
        chart.setShowLegend(true);

        chart.setHeight(300);
        chart.setWidth(800);
    }

    /**
     * Populate the internal data structures with all of the information that is
     * necessary to construct a report of this type.
     *
     * @param request a <code>HttpServletRequest</code> variable that this method
     * will use to get all the necessary variables to provide this chart with
     * data.
     * @throws net.project.chart.MissingChartDataException if one or more variables required to
     * create this chart are missing.
     */
    public void populateParameters(HttpServletRequest request) throws MissingChartDataException {
        data.updateParametersFromRequest(request);
    }

    /**
     * Populate the internal data structures with data that the chart object
     * needs to produce a chart.
     *
     * @param data a <code>ReportData</code> subclass specific to the current
     * chart that has all the data required to produce a chart.
     */
    public void populateParameters(ReportData data) {
        this.data = (FormItemTimeSeriesReportData)data;
    }

    public FormItemTimeSeriesReportData getData() {
        return data;
    }
}
