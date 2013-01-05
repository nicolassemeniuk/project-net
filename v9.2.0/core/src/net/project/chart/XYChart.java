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

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Common parent between charts that are plotted on a 2-dimensional grids.
 * These types of charts have common features like having titles for their
 * axes and requiring a certain color for their background.
 *
 * @author Matthew Flower
 * @since 3 December 2005
 */
public abstract class XYChart extends AbstractChart {
    protected String categoryTitle;
    protected Font categoryFont;
    protected String valueTitle;
    protected Font valueFont;
    protected Color plotterBackgroundColor = Color.LIGHT_GRAY;
    protected Color plotterGridlineColor = Color.BLACK;
    /**
     * Show the values on the range (Y-Axis by default) as integers, not as
     * fractional numbers.
     */
    protected boolean rangeAsIntegersOnly = false;
    protected DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getValueTitle() {
        return valueTitle;
    }

    public void setValueTitle(String valueTitle) {
        this.valueTitle = valueTitle;
    }

    public Font getCategoryFont() {
        return categoryFont;
    }

    public void setCategoryFont(Font categoryFont) {
        this.categoryFont = categoryFont;
    }

    public Font getValueFont() {
        return valueFont;
    }

    public void setValueFont(Font valueFont) {
        this.valueFont = valueFont;
    }

    public Color getPlotterBackgroundColor() {
        return plotterBackgroundColor;
    }

    public void setPlotterBackgroundColor(Color plotterBackgroundColor) {
        this.plotterBackgroundColor = plotterBackgroundColor;
    }

    public Color getPlotterGridlineColor() {
        return plotterGridlineColor;
    }

    public void setPlotterGridlineColor(Color plotterGridlineColor) {
        this.plotterGridlineColor = plotterGridlineColor;
    }

    protected void applyChartSettings(JFreeChart chart) {
        super.applyChartSettings(chart);

        CategoryPlot plot = (CategoryPlot)chart.getPlot();
        plot.setBackgroundPaint(getPlotterBackgroundColor());
        plot.setRangeGridlinePaint(getPlotterGridlineColor());

        if (categoryFont != null) {
            plot.getDomainAxis().setLabelFont(categoryFont);
        }
        if (valueFont != null) {
            plot.getRangeAxis().setLabelFont(valueFont);
        }

        if (rangeAsIntegersOnly) {
            NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        }
    }

    public boolean isRangeAsIntegersOnly() {
        return rangeAsIntegersOnly;
    }

    public void setValueLabelsAsIntegersOnly(boolean rangeAsIntegersOnly) {
        this.rangeAsIntegersOnly = rangeAsIntegersOnly;
    }
}
