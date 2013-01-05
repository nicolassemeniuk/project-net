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

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.calendar.CalendarQuarter;
import net.project.calendar.PnCalendar;
import net.project.chart.BarChart;
import net.project.chart.ChartColor;
import net.project.chart.MissingChartDataException;
import net.project.code.ColorCode;
import net.project.portfolio.view.PersonalPortfolioViewResults;
import net.project.project.ProjectSpace;
import net.project.report.ReportData;
import net.project.security.SessionManager;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;

/**
 * Generates a chart which categorizes the color code status of charts by end
 * date and produces a nice stacked bar graph of that data.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class PortfolioStatusChart extends BarChart {
    /** Token value that points to the legend string for status red projects. */
    private String RED = PropertyProvider.get("prm.portfolio.personal.chart.statuschart.legend.red.label");
    /** Token value that points to the legend string for status yellow projects. */
    private String YELLOW = PropertyProvider.get("prm.portfolio.personal.chart.statuschart.legend.yellow.label");
    /** Token value that points to the legend string for status green projects. */
    private String GREEN = PropertyProvider.get("prm.portfolio.personal.chart.statuschart.legend.green.label");
    /**
     * String to be displayed when no projects are available and thus, no data
     * can be displayed.  ("No projects available.")
     */
    private String NO_PROJECTS_AVAILABLE = PropertyProvider.get("prm.portfolio.personal.chart.statuschart.noprojectavailable.message");
    /** Token value pointing to the label for the x-axis. ("Project Completion Date"). */
    private String X_AXIS_LABEL = PropertyProvider.get("prm.portfolio.personal.chart.statuschart.xaxis.label.name");
    /** Font in which to render the X-axis label. */
    private String X_AXIS_LABEL_FONT = PropertyProvider.get("prm.portfolio.personal.chart.statuschart.xaxis.font.name");
    /** The size of the font for the X-axis label. */
    private int X_AXIS_LABEL_FONT_SIZE = PropertyProvider.getInt("prm.portfolio.personal.chart.statuschart.xaxis.font.size");
    /** Token value pointing to the label for the y-axis. ("# of Projects"). */
    private String Y_AXIS_LABEL = PropertyProvider.get("prm.portfolio.personal.chart.statuschart.yaxis.label.name");
    /** Font in which to render the Y-axis label. */
    private String Y_AXIS_LABEL_FONT = PropertyProvider.get("prm.portfolio.personal.chart.statuschart.yaxis.font.name");
    /** The size of the font for the y-axis label. */
    private int Y_AXIS_LABEL_FONT_SIZE = PropertyProvider.getInt("prm.portfolio.personal.chart.statuschart.yaxis.font.size");
    /**
     * Label to show for the column which shows projects which don't have a
     * scheduled end date.
     */
    private String UNSCHEDULED = PropertyProvider.get("prm.portfolio.personal.chart.statuschart.columns.unscheduled.name");
    /** List of projects that we are reporting on. */
    private List projects = new ArrayList();

    public PortfolioStatusChart() {
        setShowLegend(false);
        setChartTitle("");
        setCategoryTitle(X_AXIS_LABEL);
        setCategoryFont(new Font(X_AXIS_LABEL_FONT, Font.BOLD, X_AXIS_LABEL_FONT_SIZE));
        setValueTitle(Y_AXIS_LABEL);
        setValueFont(new Font(Y_AXIS_LABEL_FONT, Font.BOLD, Y_AXIS_LABEL_FONT_SIZE));
        setValueLabelsAsIntegersOnly(true);
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
        Collection projectsToInclude = (Collection) request.getSession().getAttribute("portfolioStatusChartEntryCollection");

        //This is an admitted hack.  For some reason, on Solaris the parameters aren't
        //coming through.  We are really close to release, and this needs to be
        //fixed.  Sorry to whomever eventually really fixes this.
        if (projectsToInclude == null) {
            Object potentialProjects = request.getSession().getAttribute("viewResults");
            if (potentialProjects instanceof PersonalPortfolioViewResults) {
                projectsToInclude = ((PersonalPortfolioViewResults)potentialProjects).getProjectSpaceResultElements();
            }
        }

        if (projectsToInclude == null) {
            throw new MissingChartDataException("Unable to create chart; missing session attribute 'portfolioStatusChartEntryCollection'");

        } else {
            projects.addAll(projectsToInclude);
            request.getSession().removeAttribute("portfolioStatusChartEntryCollection");
            setWidth(400);
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
        // Collect data needed to render the chart
        //-------------------------------------------------------------------

        //Calendar used to calculate the quarter in which a date occurs.
        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        //Count of how many times a project with a given color appears in a quarter. */
        Map statusCount = new HashMap();
        UnscheduledProjectCounts unscheduled = new UnscheduledProjectCounts();

        //Collect all the data, knowing that it will be out of sequence
        for (Iterator it = projects.iterator(); it.hasNext();) {
            ProjectSpace projectSpace = (ProjectSpace)it.next();

            if (projectSpace.getEndDate() != null) {
                CalendarQuarter quarter = cal.getQuarter(projectSpace.getEndDate());
                PortfolioStatusKey key = new PortfolioStatusKey(quarter, projectSpace.getColorCode());
                BigDecimal keyCount = (BigDecimal)statusCount.get(key);

                if (keyCount == null) {
                    statusCount.put(key, new BigDecimal(1));
                } else {
                    keyCount = new BigDecimal(keyCount.intValue()+1);
                    statusCount.put(key, keyCount);
                }
            } else {
                unscheduled.incrementProjectCount(projectSpace.getColorCode());
            }
        }

        //Check to make sure there is valid data
        if ((statusCount.keySet().size() == 0) && (!unscheduled.hasOccurrences())) {
            throw new NoDataFoundException();
        }

        //Now, sort the data
        LinkedList list = new LinkedList(statusCount.keySet());
        Collections.sort(list);

        //Determine the number of calendar quarters we need to show
        CalendarQuarter firstQuarter = null, lastQuarter = null;
        int quartersToShow;
        if ((list.size() > 0) && (list.getLast() != null) && (list.getFirst() != null)) {
            lastQuarter = ((PortfolioStatusKey)list.getLast()).getQuarter();
            firstQuarter = ((PortfolioStatusKey)list.getFirst()).getQuarter();

            //Always show one more than the difference, because if we took Q3-Q1
            //that would be equal to 2 quarters, but we'd want to show 3 (Q1,Q2,Q3)
            quartersToShow = lastQuarter.subtract(firstQuarter)+1;
        } else {
            quartersToShow = 0;
        }

        //Adjust the date range we are going to show
        quartersToShow = Math.min(quartersToShow, 5);
        PnCalendar cal1 = new PnCalendar(SessionManager.getUser());
        CalendarQuarter previousQuarter = cal1.getQuarter(cal1.getTime()).subtract(1);
        firstQuarter = CalendarQuarter.max(previousQuarter, firstQuarter);

        //Determine if we have unscheduled values to show
        int sampleCount = (unscheduled.hasOccurrences() ? quartersToShow+1 : quartersToShow);

        //Add the data for the quarters we are going to show
        double[] redList = new double[sampleCount];
        double[] yellowList = new double[sampleCount];
        double[] greenList = new double[sampleCount];
        String[] barLabels = new String[sampleCount];

        for (int i = 0; i < quartersToShow; i++) {
            CalendarQuarter currentQuarter = firstQuarter.add(i);
            BigDecimal redCount = (BigDecimal)statusCount.get(new PortfolioStatusKey(currentQuarter, ColorCode.RED));
            redList[i] = (redCount != null ? redCount.doubleValue() : 0);
            BigDecimal yellowCount = (BigDecimal)statusCount.get(new PortfolioStatusKey(currentQuarter, ColorCode.YELLOW));
            yellowList[i] = (yellowCount != null ? yellowCount.doubleValue() : 0);
            BigDecimal greenCount = (BigDecimal)statusCount.get(new PortfolioStatusKey(currentQuarter, ColorCode.GREEN));
            greenList[i] = (greenCount != null ? greenCount.doubleValue() : 0);
            barLabels[i] = currentQuarter.toString();
        }

        //Now set the unscheduled information
        if (unscheduled.hasOccurrences()) {
            redList[redList.length-1] = unscheduled.getCount(ColorCode.RED);
            yellowList[yellowList.length-1] = unscheduled.getCount(ColorCode.YELLOW);
            greenList[greenList.length-1] = unscheduled.getCount(ColorCode.GREEN);
            barLabels[barLabels.length-1] = UNSCHEDULED;
        }

        //-------------------------------------------------------------------
        // Setup the chart
        //-------------------------------------------------------------------
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryItemRenderer renderer = new StackedBarRenderer();
        plot.setRenderer(renderer);

        //There is an assumption here that redList, yellowList, greenList, and barlabels are the same.
        for (int i = 0; i < redList.length; i++) {
            String barLabel = barLabels[i];

            dataset.addValue(greenList[i], "Green", barLabels[i]);
            dataset.addValue(yellowList[i], "Yellow", barLabels[i]);
            dataset.addValue(redList[i], "Red", barLabels[i]);
        }

        renderer.setSeriesPaint(0, ChartColor.GREEN);
        renderer.setSeriesPaint(1, ChartColor.YELLOW);
        renderer.setSeriesPaint(2, ChartColor.RED);
    }

    protected BufferedImage getExceptionImage(Exception e) {
        if (e instanceof NoDataFoundException) {
            Logger.getLogger(PortfolioStatusChart.class).debug("No Data found exception: "+e, e);
            return getImageWithText(NO_PROJECTS_AVAILABLE, 10, false, "Arial", true);
        } else {
            return super.getExceptionImage(e);
        }
    }
}
