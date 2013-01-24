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
|   $Revision: 18995 $
|       $Date: 2009-03-05 08:36:26 -0200 (jue, 05 mar 2009) $
|     $Author: avinash $
|
+-----------------------------------------------------------------------------*/
package net.project.portfolio.chart;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.money.InvalidCurrencyException;
import net.project.base.money.Money;
import net.project.base.property.PropertyProvider;
import net.project.chart.ChartColor;
import net.project.chart.MissingChartDataException;
import net.project.chart.PieChart;
import net.project.portfolio.view.PersonalPortfolioViewResults;
import net.project.project.ProjectSpace;
import net.project.report.ReportData;
import net.project.security.SessionManager;
import net.project.security.User;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;

/**
 * Produces a pie chart which shows the portion of a budget which hasn't already
 * been spent.  If it has been overspent, it instead shows the overspent amount
 * as a portion of the whole amount.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class PortfolioBudgetChart extends PieChart {
    /** This list of project which serve as source data. */
    private List projects = new ArrayList();
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
    /**
     * Pie sector label for the total number of dollars remaining.
     */
    private String DOLLARS_REMAINING = PropertyProvider.get("prm.portfolio.personal.chart.budgetchart.legend.dollarsremaining.name");
    /**
     * Pie section label for the total number of dollars spent.
     */
    private String DOLLARS_SPENT = PropertyProvider.get("prm.portfolio.personal.chart.budgetchart.legend.dollarsspent.name");
    /**
     * Pie section label for the total number of dollars overspent.
     */
    private String DOLLARS_OVERSPENT = PropertyProvider.get("prm.portfolio.personal.chart.budgetchart.legend.dollarsoverspent.name");
    /**
     * Pie section label for the total number of budgeted dollars.
     */
    private String DOLLARS_BUDGETED = PropertyProvider.get("prm.portfolio.personal.chart.budgetchart.legend.dollarsbudgeted.name");
    /**
     * Title of the chart which shows the total amount of money budgeted in this portfolio.
     */
    private String TOTAL_BUDGET = "prm.portfolio.personal.chart.budgetchart.totalbudget.name";
    /**
     * Title of the chart which shows the total amount of money spent in this portfolio.
     */
    private String TOTAL_AMOUNT_SPENT = "prm.portfolio.personal.chart.budgetchart.totalamountspent.name";

    private String METHOD_NOT_SUPPORTED_BY_THIS_CHART = PropertyProvider.get("prm.portfolio.personal.chart.budgetchart.errors.methodnotsupport.message");

    public PortfolioBudgetChart() {
        setShowLegend(false);
        setShowValueLabels(true);
        setChartTitle("");
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
        // Grab the collection of ProjectSpaces required to produce the chart
        Collection projectsToInclude = (Collection) request.getSession().getAttribute("portfolioBudgetChartEntryCollection");

        //This is an admitted hack.  It's because in some browsers, like IE, the request is posted
        // twice.
        if (projectsToInclude == null) {
            Object potentialProjects = request.getSession().getAttribute("viewResults");
            if (potentialProjects instanceof PersonalPortfolioViewResults) {
                projectsToInclude = ((PersonalPortfolioViewResults)potentialProjects).getProjectSpaceResultElements();
            }
        }
        
        if (projectsToInclude == null) {
            throw new MissingChartDataException("Unable to create chart; missing session attribute 'portfolioBudgetChartEntryCollection'");

        } else {
            projects.addAll(projectsToInclude);
            request.getSession().removeAttribute("portfolioBudgetChartEntryCollection");
            setWidth(365);
            setHeight(150);
        }
    }

    /**
     *
     * @param pieChart
     * @throws InvalidCurrencyException if more than one currency is encountered
     * while summing data.
     * @throws NoDataFoundException if no data is found to build the pie chart.
     */
    public void setupChart(JFreeChart pieChart) throws Exception {
        //Only try to build the chart if there is information available.
        if (projects.size() > 0) {
            User user = SessionManager.getUser();

            ProjectSpace firstProject = (ProjectSpace)projects.get(0);
            Money budgetedCost = new Money("0", firstProject.getDefaultCurrency());
            Money actualCost = new Money("0", firstProject.getDefaultCurrency());

            for (Iterator it = projects.iterator(); it.hasNext();) {
                ProjectSpace projectSpace = (ProjectSpace)it.next();
                if (projectSpace.getBudgetedTotalCost() != null) {
                    budgetedCost = budgetedCost.add(projectSpace.getBudgetedTotalCost());
                }
                if (projectSpace.getActualCostToDate() != null) {
                    actualCost = actualCost.add(projectSpace.getActualCostToDate());
                }
            }

            Money remainingCost = budgetedCost.subtract(actualCost);

            if (budgetedCost.getValue().equals(new BigDecimal(0)) || actualCost.getValue().equals(new BigDecimal(0))) {
                throw new NoDataFoundException();
            } else {
                if (actualCost.getValue().compareTo(budgetedCost.getValue()) <= 0) {
                    //We show this chart if the actual cost is still less than the budgeted cost.  It is the "happy" chart
                    pieChart.setTitle(PropertyProvider.get(TOTAL_BUDGET, budgetedCost.format(user, false)));

                    dataset.setValue(DOLLARS_REMAINING, budgetedCost.subtract(actualCost).getValue().abs());
                    dataset.setValue(DOLLARS_SPENT, actualCost.getValue().abs().doubleValue());

                    //chart.setSampleColors(new Color[] { ChartColor.BLUE, ChartColor.GREEN });
                    PiePlot piePlot = (PiePlot)pieChart.getPlot();
                    piePlot.setSectionPaint(0, ChartColor.BLUE);
                    piePlot.setSectionPaint(1, ChartColor.GREEN);
                } else {
                    pieChart.setTitle(PropertyProvider.get(TOTAL_AMOUNT_SPENT, actualCost.format(user, false)));

                    dataset.setValue(DOLLARS_OVERSPENT, budgetedCost.subtract(actualCost).getValue().abs().doubleValue());
                    dataset.setValue(DOLLARS_BUDGETED, budgetedCost.getValue().abs().doubleValue());

                    //todonow -- figure out how to change the sample colors.
                    //chart.setSampleColors(new Color[] { ChartColor.RED, ChartColor.BLUE });
                    PiePlot piePlot = (PiePlot)pieChart.getPlot();
                    piePlot.setSectionPaint(0, Color.RED);
                    piePlot.setSectionPaint(1, Color.BLUE);
                }

                //todonow -- try out "exploded slice" api in JFreeCharts to see how it looks
            }
        } else {
            throw new NoDataFoundException();
        }
    }

    protected BufferedImage getExceptionImage(Exception e) {
        if (e instanceof InvalidCurrencyException) {
            return getImageWithText(NO_CHART_WITH_MULTIPLE_CURRENCIES, 10, false, "Arial", true);
        } else if (e instanceof NoDataFoundException) {
            return getImageWithText(NO_CHART_WITHOUT_COSTS, 10, false, "Arial", true);
        } else {
            return super.getExceptionImage(e);
        }
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
