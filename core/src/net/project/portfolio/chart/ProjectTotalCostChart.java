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

 package net.project.portfolio.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.chart.BarChart;
import net.project.chart.ChartColor;
import net.project.chart.MissingChartDataException;
import net.project.project.ProjectSpaceBean;
import net.project.report.ReportData;
import net.project.security.SessionManager;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;

/**
 * Generates a chart which categorizes the color code status of charts by project costs
 * and produces a stacked bar graph of that data.
 */
public class ProjectTotalCostChart extends BarChart {
	/** Label for the actual cost to date */
    private String ACTUAL_COST_TO_DATE = PropertyProvider.get("prm.project.dashboard.chart.totalcostchart.legend.actualcosttodate.name");

    /** Label for the current estimated total cost */
    private String CURRENT_ESTIMATED_TOTAL_COST = PropertyProvider.get("prm.project.dashboard.chart.totalcostchart.legend.currentestimatedtotalcost.name");

    /** Label for the budgeted total cost */
    private String BUDGETED_TOTAL_COST = PropertyProvider.get("prm.project.dashboard.chart.totalcostchart.legend.budgetedtotalcost.name");
    
    /**
     * Error message to be show if the chart is trying to be produced on a
     * project that doesn't have budgeted, current or actual costs entered.
     */
    private String NO_CHART_WITHOUT_COSTS = PropertyProvider.get("prm.project.dashboard.chart.totalcostchart.errors.nochartwithoutcosts.message");
    
    /** Token value pointing to the label for the y-axis. ("Costs (in thousands)"). */
    private String Y_AXIS_LABEL = PropertyProvider.get("prm.project.dashboard.chart.totalcostchart.yaxis.label.name");
    
    /** Font in which to render the Y-axis label. */
    private String Y_AXIS_LABEL_FONT = PropertyProvider.get("prm.project.dashboard.chart.totalcostchart.yaxis.font.name");
    
    /** The size of the font for the y-axis label. */
    private int Y_AXIS_LABEL_FONT_SIZE = PropertyProvider.getInt("prm.project.dashboard.chart.totalcostchart.yaxis.font.size");

    /** Project that we are reporting on. */
    private ProjectSpaceBean projectSpace = null;

    public ProjectTotalCostChart() {
        setShowLegend(true);
        setChartTitle("");
        
        setValueTitle(Y_AXIS_LABEL);
        setValueFont(new Font(Y_AXIS_LABEL_FONT, Font.BOLD, Y_AXIS_LABEL_FONT_SIZE));
        setValueLabelsAsIntegersOnly(true);
        
        setPlotterBackgroundColor(Color.WHITE);
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
		if(SessionManager.getUser().getCurrentSpace() instanceof ProjectSpaceBean)
			projectSpace = (ProjectSpaceBean)SessionManager.getUser().getCurrentSpace();

        if (projectSpace == null)
            throw new MissingChartDataException("Unable to create chart; missing session attribute 'projectSpace'");
        else
        {
            setWidth(550);
            setHeight(150);
        }
    }

    /**
     * Populate the internal data structures with data that the chart object
     * needs to produce a chart.
     *
     * @param data a <code>ReportData</code> subclass specific to the current
     * chart that has all the data required to produce a chart.
     */
    public void populateParameters(ReportData data) {
    }

    public void setupChart(JFreeChart chart) throws Exception {
        //-------------------------------------------------------------------
        // Setup the chart
        //-------------------------------------------------------------------
        // chart.setBorderStroke(new BasicStroke(15));

        CategoryPlot plot = chart.getCategoryPlot();
        CategoryItemRenderer renderer = new BarRenderer();
        plot.setRenderer(renderer);
    	
        //-------------------------------------------------------------------
        // Collect data needed to render the chart
        //-------------------------------------------------------------------
        dataset.addValue(projectSpace.getActualCostToDate().getValue().divide(new BigDecimal(1000.0)), ACTUAL_COST_TO_DATE, "");
        dataset.addValue(projectSpace.getCurrentEstimatedTotalCost().getValue().divide(new BigDecimal(1000.0)), CURRENT_ESTIMATED_TOTAL_COST, "");
        dataset.addValue(projectSpace.getBudgetedTotalCost().getValue().divide(new BigDecimal(1000.0)), BUDGETED_TOTAL_COST, "");
        
        renderer.setSeriesPaint(0, ChartColor.LIGHT_BLUE);
        renderer.setSeriesPaint(1, ChartColor.BLUE);
        renderer.setSeriesPaint(2, ChartColor.DARK_BLUE);
    }

    protected BufferedImage getExceptionImage(Exception e) {
        if (e instanceof NoDataFoundException) {
            Logger.getLogger(ProjectTotalCostChart.class).debug("No Data found exception: "+e, e);
            return getImageWithText(NO_CHART_WITHOUT_COSTS, 10, false, "Arial", true);
        } else {
            return super.getExceptionImage(e);
        }
    }
}
