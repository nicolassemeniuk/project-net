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

package net.project.financial;

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.chart.ChartColor;
import net.project.chart.MissingChartDataException;
import net.project.chart.PieChart;

import net.project.report.ReportData;
import net.project.security.SessionManager;

import org.jfree.chart.JFreeChart;

import org.jfree.chart.plot.PiePlot;


public class FinancialActualCostsTypesOverTotalChart extends PieChart {
    /** Financial space that we are reporting on. */
    private FinancialSpaceBean financialSpace = null;

    /**
     * Error message to be shown in the chart if a chart is attempting to be
     * produced that would be composed of more than one currency type.
     */
    private String NO_CHART_WITH_MULTIPLE_CURRENCIES = PropertyProvider.get("prm.portfolio.personal.chart.budgetchart.errors.multiplecurrencies.message");
    /**
     * Error message to be show if the chart is trying to be produced on a
     * portfolio that doesn't have budgeted or actual costs entered for at least
     * one project.
     */
    private String NO_CHART_WITHOUT_COSTS = PropertyProvider.get("prm.portfolio.personal.chart.budgetchart.errors.nochartwithoutcosts.message");    
    
    private String METHOD_NOT_SUPPORTED_BY_THIS_CHART = PropertyProvider.get("prm.portfolio.personal.chart.budgetchart.errors.methodnotsupport.message");
    
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
		if(SessionManager.getUser().getCurrentSpace() instanceof FinancialSpaceBean)
			financialSpace = (FinancialSpaceBean)SessionManager.getUser().getCurrentSpace();

        if (financialSpace == null)
            throw new MissingChartDataException("Unable to create chart; missing session attribute 'financialSpace'");
        else
        {
            setWidth(375);
            setHeight(150);
            setShowLegend(true);
            setShowValueLabels(false);
        }
    }

    public void setupChart(JFreeChart pieChart) throws Exception {
        dataset.setValue("Resources", 10000);
        dataset.setValue("Materials", 5000);
        dataset.setValue("Discretional", 100);

        PiePlot piePlot = (PiePlot)pieChart.getPlot();
        piePlot.setSectionPaint(0, ChartColor.GREEN);
        piePlot.setSectionPaint(1, ChartColor.YELLOW);
        piePlot.setSectionPaint(2, ChartColor.RED);    
    }

    /**
     * Unsupported method to populate data.  This is here due to a quirk in the
     * API.
     *
     * @param data ignored.
     * @throws UnsupportedOperationException always.
     */
    public void populateParameters(ReportData data) {
        throw new UnsupportedOperationException(METHOD_NOT_SUPPORTED_BY_THIS_CHART);
    }
}
