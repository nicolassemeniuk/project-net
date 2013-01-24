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

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

/**
 * Created by IntelliJ IDEA.
 * User: Matthew Flower
 * Date: Nov 27, 2005
 * Time: 11:46:01 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class LineChart extends XYChart {
    protected JFreeChart internalChartSubtype() {
        return ChartFactory.createLineChart(
            getChartTitle(),
            getCategoryTitle(),
            getValueTitle(),
            dataset,
            PlotOrientation.VERTICAL,
            isShowLegend(),
            false,
            false
        );
//        LineChart lineChart = new LineChart();
//        setupChart(lineChart);
//        lineChart.set3DModeOn(false);
//        lineChart.setValueLinesOn(true);
//        lineChart.setValueLinesColor(Color.BLACK);
//        lineChart.setChartBackground(Color.LIGHT_GRAY);
//        lineChart.setValueLabelsOn(true);
//
//        return lineChart;
    }

    protected void applyChartSettings(JFreeChart chart) {
        super.applyChartSettings(chart);

        CategoryPlot plot = (CategoryPlot)chart.getPlot();
        LineAndShapeRenderer renderer = (LineAndShapeRenderer)plot.getRenderer();
        renderer.setShapesVisible(true);
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
        renderer.setFillPaint(Color.WHITE);
    }
}
