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

package net.project.project;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import net.project.chart.ChartColor;
import net.project.chart.MissingChartDataException;
import net.project.project.ProjectSpaceBean;
import net.project.security.SessionManager;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;

public class ProjectTotalCostChart extends TotalCostChart {
    /** Project that we are reporting on. */
    private ProjectSpaceBean projectSpace = null;

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

    public void setupChart(JFreeChart chart) throws Exception {
        //-------------------------------------------------------------------
        // Setup the chart
        //-------------------------------------------------------------------
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryItemRenderer renderer = new BarRenderer();
        plot.setRenderer(renderer);
    	
        //-------------------------------------------------------------------
        // Collect data needed to render the chart
        //-------------------------------------------------------------------
        
        // Actual Cost To Date + Discretional Cost
        BigDecimal actualCostToDate = projectSpace.getActualCostToDate().getValue().add(projectSpace.getDiscretionalCost().getValue());
        
        // Current Estimated Total Cost + Discretional Cost
        BigDecimal currentEstimatedTotalCost = projectSpace.getCurrentEstimatedTotalCost().getValue().add(projectSpace.getDiscretionalCost().getValue());
        
        // Budgeted Total Cost
        BigDecimal budgetedTotalCost = projectSpace.getBudgetedTotalCost().getValue();
        
        // Scale all values over thousand
        dataset.addValue(actualCostToDate.divide(new BigDecimal(1000.0)), ACTUAL_COST_TO_DATE, "");
        dataset.addValue(currentEstimatedTotalCost.divide(new BigDecimal(1000.0)), CURRENT_ESTIMATED_TOTAL_COST, "");
        dataset.addValue(budgetedTotalCost.divide(new BigDecimal(1000.0)), BUDGETED_TOTAL_COST, "");
        
        renderer.setSeriesPaint(0, ChartColor.LIGHT_BLUE);
        renderer.setSeriesPaint(1, ChartColor.BLUE);
        renderer.setSeriesPaint(2, ChartColor.DARK_BLUE);
    }
}
