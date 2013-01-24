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
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

/**
 * This class should be the superclass of all pie charts created in Project.net.
 *
 * @author Matthew Flower
 * @since 27-Nov-2005
 */
public abstract class PieChart extends AbstractChart {
    protected DefaultPieDataset dataset = new DefaultPieDataset();
    private boolean showValueLabels = true;

    /**
     * Create a new chart object and set it up in a way consistent with the other
     * chart objects on the Project.Net website.
     *
     * @return a <code>PieChart</code> object which is setup and ready for use.
     * The calling method will still need to setup (at least) samples and series.
     */
    protected JFreeChart internalChartSubtype() {
        return ChartFactory.createPieChart(
                getChartTitle() == null ? "" : getChartTitle(),
                dataset,
                isShowLegend(),
                false,
                false
        );

//        PieChart pieChart = new PieChart();
//        setupChart(pieChart);
//        pieChart.set3DDepth(1);
//        pieChart.setAngle(30);
//        pieChart.setLegendOn(true);
//        pieChart.setSeriesLabelsOn(true);
//        pieChart.setSeriesLabelStyle(PieChart.INSIDE);
//        return pieChart;
    }

    protected void applyChartSettings(JFreeChart chart) {
        super.applyChartSettings(chart);

        PiePlot plot = (PiePlot)chart.getPlot();
        plot.setSectionOutlinePaint(Color.DARK_GRAY);

        if (!showValueLabels) {
            plot.setLabelGenerator(null);
        } else {
            plot.setLabelFont(new Font("Arial", Font.PLAIN, 9));
        }
    }

    public boolean isShowValueLabels() {
        return showValueLabels;
    }

    public void setShowValueLabels(boolean showValueLabels) {
        this.showValueLabels = showValueLabels;
    }
}
