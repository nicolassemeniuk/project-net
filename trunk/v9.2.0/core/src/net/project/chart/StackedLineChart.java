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
import org.jfree.data.xy.CategoryTableXYDataset;

/**
 * Create a stacked line chart.
 *
 * @author Matthew Flower
 * @since 16 December 2005
 */
public abstract class StackedLineChart extends XYChart {
    protected CategoryTableXYDataset dataset = new CategoryTableXYDataset();

    protected JFreeChart internalChartSubtype() {
        return ChartFactory.createStackedXYAreaChart(
            getChartTitle(),
            getCategoryTitle(),
            getValueTitle(),
            dataset,
            PlotOrientation.VERTICAL,
            isShowLegend(),
            false,
            false
        );
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
