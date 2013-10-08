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

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import net.project.base.money.InvalidCurrencyException;
import net.project.chart.ChartColor;
import net.project.chart.MissingChartDataException;
import net.project.hibernate.service.ServiceFactory;
import net.project.project.ProjectSpace;
import net.project.project.TotalCostChart;
import net.project.security.SessionManager;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;

public class FinancialTotalCostChart extends TotalCostChart {
    /** Financial space that we are reporting on. */
    private FinancialSpaceBean financialSpace = null;

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
    	
        // Check all projects have the same currency
		ArrayList<ProjectSpace> projectList = financialSpace.getProjectsList();
		
		String firstCurrency = ServiceFactory.getInstance().getPnProjectSpaceService().getDefaultCurrency(projectList.get(0).getID());
		for(ProjectSpace project : projectList)
		{
			 String currentCurrency = ServiceFactory.getInstance().getPnProjectSpaceService().getDefaultCurrency(project.getID());
			 if(!currentCurrency.equals(firstCurrency))
				 throw new InvalidCurrencyException();
		}
        
        // Total Actual Cost To Date + Discretional Cost
        BigDecimal totalActualCostToDate = new BigDecimal(financialSpace.getTotalActualCostToDate()).add(new BigDecimal(financialSpace.getTotalDiscretionalCost()));
        
        // Total Current Estimated Cost + Discretional Cost
        BigDecimal totalCurrentEstimatedTotalCost = new BigDecimal(financialSpace.getTotalEstimatedCurrentCost()).add(new BigDecimal(financialSpace.getTotalDiscretionalCost()));
        
        // Total Budgeted Cost
        BigDecimal totalBudgetedTotalCost = new BigDecimal(financialSpace.getTotalBudgetedCost());		
		
        // Scale all values over thousand
        dataset.addValue(totalActualCostToDate.divide(new BigDecimal(1000.0)), ACTUAL_COST_TO_DATE, "");
        dataset.addValue(totalCurrentEstimatedTotalCost.divide(new BigDecimal(1000.0)), CURRENT_ESTIMATED_TOTAL_COST, "");
        dataset.addValue(totalBudgetedTotalCost.divide(new BigDecimal(1000.0)), BUDGETED_TOTAL_COST, "");
        
        renderer.setSeriesPaint(0, ChartColor.LIGHT_BLUE);
        renderer.setSeriesPaint(1, ChartColor.BLUE);
        renderer.setSeriesPaint(2, ChartColor.DARK_BLUE);
    }
}
