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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+-----------------------------------------------------------------------------*/
package net.project.form.report.formitemsummaryreport;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.chart.ChartingException;
import net.project.chart.MissingChartDataException;
import net.project.chart.PieChart;
import net.project.form.FormField;
import net.project.form.report.FormDataFormatter;
import net.project.persistence.PersistenceException;
import net.project.portfolio.chart.NoDataFoundException;
import net.project.report.ReportData;
import net.project.util.Validator;

import org.jfree.chart.JFreeChart;

/**
 * Class to create a chart representing the values found in the
 * <code>FormItemSummaryReport</code>.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class FormItemSummaryReportChart extends PieChart {
    /**
     * This variable provides data from the database which is required to build
     * the report.
     */
    private FormItemSummaryReportData data = new FormItemSummaryReportData();
    /**
     * Token pointing to: "Unexpected error thrown while trying to load data for
     * chart."
     */
    private String LOAD_CHART_DATA_ERROR_TOKEN = "prm.global.chart.errors.loaddataerror.message";
    /** Token to use when a field value is blank.  This value will be used in the legend and field counts. */
    private String EMPTY_FIELD_LABEL = "prm.form.report.common.emptyfieldvalue.message";
    /**
     * The font that will be used to draw the legend.
     */
    private Font chartFont = new Font("dialog", Font.PLAIN, 12);

    public FormItemSummaryReportChart() {
        setShowLegend(false);
        setShowValueLabels(true);
    }

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
    public void populateParameters(HttpServletRequest request) throws MissingChartDataException {
        data = new FormItemSummaryReportData();
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
        this.data = (FormItemSummaryReportData)data;
    }

    protected void setupChart(JFreeChart chart) throws Exception {
        //Load the data for the chart if it hasn't already been loaded.
        try {
            data.load();
        } catch(PersistenceException e) {
            throw new ChartingException(LOAD_CHART_DATA_ERROR_TOKEN);
        }

        //Check to make sure there is data found
        if (data.getSummaryData().fieldValues.size() == 0) {
            throw new NoDataFoundException();
        }

        SummarizedFieldData sfd = data.getSummaryData();
        FormField field = data.getFormField();

        for (Iterator it = sfd.getFieldValues().keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            Integer value = (Integer)sfd.getFieldValues().get(key);
            String displayFieldName = FormDataFormatter.formatSimpleData(field, key);
            if (Validator.isBlankOrNull(displayFieldName)) {
                displayFieldName = PropertyProvider.get(EMPTY_FIELD_LABEL);
            }

            dataset.setValue(displayFieldName, value.doubleValue());
        }
    }

    protected BufferedImage getExceptionImage(Exception e) {
        if (e instanceof NoDataFoundException) {
            return getNoRecordsFoundImage();
        } else {
            return super.getExceptionImage(e);
        }
    }

    /**
     * Get the font that is going to be used to draw the legend.
     *
     * @return a <code>Font</code> object that is going to be used to draw the
     * legend.
     */
    public Font getChartFont() {
        return chartFont;
    }

    /**
     * Set the font that is going to be used to draw the legend.
     *
     * @param chartFont a <code>Font</code> object that is going to be used to
     * draw the legend.
     */
    public void setChartFont(Font chartFont) {
        this.chartFont = chartFont;
    }
}
