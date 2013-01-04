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
package net.project.form.report.formitemtimeseries;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;

import net.project.chart.BarChart;
import net.project.chart.MissingChartDataException;
import net.project.form.FormField;
import net.project.form.report.FormDataFormatter;
import net.project.persistence.PersistenceException;
import net.project.portfolio.chart.NoDataFoundException;
import net.project.report.ReportData;
import net.project.security.SessionManager;
import net.project.util.DateFormat;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.category.StackedBarRenderer;

/**
 * Chart for the Form Item Time Series Report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class FormItemTimeSeriesStackedBarChart extends BarChart {
    private FormItemTimeSeriesChartHelper helper = new FormItemTimeSeriesChartHelper();

    public FormItemTimeSeriesStackedBarChart() {
        helper.onConstruct(this);
    }

    public void populateParameters(HttpServletRequest request) throws MissingChartDataException {
        helper.populateParameters(request);
    }

    public void populateParameters(ReportData data) {
        helper.populateParameters(data);
    }

    protected void setupChart(JFreeChart chart) throws Exception {
        populateDatasetAndLegend(chart);
    }

    protected BufferedImage getExceptionImage(Exception e) {
        if (e instanceof NoDataFoundException) {
            return getNoRecordsFoundImage();
        } else {
            return super.getExceptionImage(e);
        }
    }

    /**
     * Populate the dataset with information from the
     * <code>FormItemTimeSeriesData</code> object into the dataset required to
     * render the chart.
     *
     * @param chart a <code>Chart</code> object to which we are going to
     * add data and legend properties.
     */
    private void populateDatasetAndLegend(JFreeChart chart) throws PersistenceException {
        FormItemTimeSeriesReportData data = helper.getData();

        data.load();
        chart.getCategoryPlot().setRenderer(new StackedBarRenderer());

        //Get the data we are going to use to create the chart dataset
        FormItemTimeSeriesData dataSource = data.getData();

        SortedSet dates = dataSource.getAllDatesInSeries();
        SortedSet fields = dataSource.getAllFieldValues();

        FormField field = data.getFormField();
        DateFormat df = SessionManager.getUser().getDateFormatter();

        for (Iterator it = dates.iterator(); it.hasNext();) {
            Date currentDate = (Date)it.next();
            for (Iterator it2 = fields.iterator(); it2.hasNext();) {
                String currentField = (String)it2.next();

                Integer count = dataSource.getCountOnDate(currentDate, currentField);
                count = (count != null ? count : new Integer(0));

                //Clean up the field for use in presentation
                if (currentField == null) {
                    currentField = "";
                }

                String formattedField = FormDataFormatter.formatSimpleData(field, currentField);
                String formattedDate = df.formatDate(currentDate);

                dataset.addValue(count, formattedField, formattedDate);
            }
        }
    }
}
