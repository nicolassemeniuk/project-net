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

 package net.project.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;

/**
 * Base class class to represent bar charts.
 *
 * @author Matthew Flower
 * @since 27 Nov 2005
 */
public abstract class BarChart extends XYChart {
    protected JFreeChart internalChartSubtype() {
        return ChartFactory.createBarChart(
            getChartTitle() == null ? "" : getChartTitle(),
            getCategoryTitle() == null ? "" : getCategoryTitle(),
            getValueTitle() == null ? "" : getValueTitle(),
            dataset,
            PlotOrientation.VERTICAL,
            isShowLegend(),
            false,
            false
        );

//        BarChart barChart = new BarChart();
//        setupChart(barChart);
//        barChart.set3DDepth(5);
//        barChart.setLegendOn(true);
//        barChart.setValueLinesOn(true);
//        barChart.setValueLinesColor(Color.BLACK);
    }

    protected void applyChartSettings(JFreeChart chart) {
        super.applyChartSettings(chart);

        BarRenderer renderer = (BarRenderer)((CategoryPlot)chart.getPlot()).getRenderer();
        renderer.setMaximumBarWidth(0.50);
    }
}
