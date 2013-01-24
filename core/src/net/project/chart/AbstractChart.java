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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import net.project.base.property.PropertyProvider;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.AbstractDataset;
import org.jfree.ui.RectangleEdge;

/**
 * An abstract chart object that defines some helper methods for creating charts.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
abstract class AbstractChart implements IChart {
    /** The proposed width of the chart.  A value of -1 means to use the default width. */
    protected int width = -1;
    /** The proposed height of the chart.  A value of -1 means to use the default height. */
    protected int height = -1;
    /** The default width of the chart if one is not chosen. */
    private int DEFAULT_WIDTH = 500;
    /** The default height of the chart if one is not chosen. */
    private int DEFAULT_HEIGHT = 200;
    /** Font used to draw the No Data Found image. */
    private String NO_DATA_FOUND_IMAGE_FONT = PropertyProvider.get("prm.global.chart.nodatafound.font");  //"Helvetica";
    /** Whether or not the text in the no data found image is emboldened. */
    private boolean NO_DATA_FOUND_IMAGE_TEXT_BOLD = PropertyProvider.getBoolean("prm.global.chart.nodatafound.text.bold");  //true
    /** The size that the text in the "No Data Found" image. */
    private int NO_DATA_FOUND_IMAGE_TEXT_SIZE = PropertyProvider.getInt("prm.global.chart.nodatafound.text.size");   //16;
    /** The text to be displayed in the "No Data Found" image. */
    private String NO_DATA_FOUND_TEXT = PropertyProvider.get("prm.global.chart.nodatafound.text");  //"No Data Available";

    /** Determine whether the legend should be shown. */
    private boolean showLegend;
    /** Which edge the legend should be anchored on -- North, South, East, or West. */
    private LegendPosition legendPosition = LegendPosition.EAST;
    /** The title of the chart. */
    private String chartTitle;
    /** The font in which the chart title will be rendered. */
    private Font chartTitleFont = new Font("Arial", Font.BOLD, 12);
    /** The background color of the chart. */
    private Color backgroundColor = Color.WHITE;
    /** Dataset used to create the chart. */
    private AbstractDataset dataset;

    /**
     * Create a chart which is appropriate for whatever type of chart this is.  This class can't
     * really know what the appropriate type of chart is, so we have to defer that decision to a
     * subclass.  (For example, a PieChart subtype would create a PieChart).
     *
     * @return a <code>JFreeChart</code> instance.  We will return this here so we can do common
     * things like create the buffered image for the chart.
     */
    protected abstract JFreeChart internalChartSubtype();


    /**
     * This method creates exception text as an image that can be shown instead of a chart.  This
     * is important because otherwise any error messages are hidden from us during development, and
     * the idea that there is a problem is hidden from the user.
     *
     * @return a <code>BufferedImage</code> which has exception text written into it.
     */
    protected BufferedImage getExceptionImage(Exception e) {
        return getImageWithText(e.getMessage(), 10, false, "Arial", true);
    }

    /**
     * Get a <code>BufferedImage</code> of a chart.
     *
     * @return a <code>BufferedImage</code> of a chart that corresponds to the input data.
     * @throws net.project.chart.ChartingException whenever any exception occurs.  Generally, this will only
     * occur when there is a database error or when someone programming a chart has made an
     * error in their logic.
     */
    public BufferedImage getChart() throws ChartingException {
        JFreeChart chart = internalChartSubtype();
        BufferedImage image;

        try {
            applyChartSettings(chart);
            setupChart(chart);
            image = chart.createBufferedImage(getWidth(), getHeight());
        } catch (Exception e) {
            Logger.getLogger(AbstractChart.class).debug(e);
            image = getExceptionImage(e);
        }

        return image;
    }

    protected void applyChartSettings(JFreeChart chart) {
        if (backgroundColor != null) {
            chart.setBackgroundPaint(backgroundColor);
        }

        //Set the title for the chart
        if (chartTitle != null) {
            TextTitle textTitle = new TextTitle(chartTitle, chartTitleFont);
            chart.setTitle(textTitle);
        }

        //Convert our legend type to one that JFreeChart understands
        LegendTitle legendTitle = chart.getLegend();
        if (legendTitle != null) {
            legendTitle.setPosition(convertLegendPosition(legendPosition));
        }
    }

    private RectangleEdge convertLegendPosition(LegendPosition legendPosition) {
        if (legendPosition.equals(LegendPosition.NORTH)) {
            return RectangleEdge.TOP;
        } else if (legendPosition.equals(LegendPosition.EAST)) {
            return RectangleEdge.RIGHT;
        } else if (legendPosition.equals(LegendPosition.SOUTH)) {
            return RectangleEdge.BOTTOM;
        } else {
            return RectangleEdge.LEFT;
        }
    }

    /**
     * Setup whatever data should be in this instance of the chart.
     *
     * @param chart a <code>JFreeChart</code> instance of the appropriate chart.
     */
    protected abstract void setupChart(JFreeChart chart) throws Exception;

    /**
     * Find the maximum value of any single "bar" in a bar chart or line in a
     * line chart.  This will allow us to size the charts correctly.
     *
     * @param chart a <code>Chart</code> object we are finding the maximum range
     * value taken by any sample.
     * @return a <code>double</code> value corresponding to the maximum value
     * of any sample of a chart.
     */
    protected static double findMaximumRange(JFreeChart chart) {
        double maxValue = 0;

//        boolean calculateCumulative = false;
//        if ((chart instanceof BarChart) && (((BarChart)chart).getBarType() == BarChart.STACKED_BARS)) {
//            calculateCumulative = true;
//        }
//
//        //Iterate through each sample, attempting to find the largest series
//        for (int i = 0; i < chart.getSampleCount(); i++) {
//            double currentSeries = 0;
//            for (int j = 0; j < chart.getSeriesCount(); j++) {
//                if (calculateCumulative) {
//                    currentSeries += chart.getSampleValue(j, i);
//                } else {
//                    currentSeries = Math.max(currentSeries, chart.getSampleValue(j,i));
//                }
//            }
//            maxValue = Math.max(maxValue, currentSeries);
//        }

        //Multiply the maximum value by 1.2 does it doesn't touch the top of the chart.
        return maxValue;
    }

    /**
     * Fix the upper range of a chart so it will show from zero to the maximum
     * value * 1.2.  Otherwise, charts have "100" as their maximum range by
     * default.
     *
     * @param chart a <code>Chart</code> object for which we are going to change
     * the maximum range.
     */
    protected void fixChartRange(JFreeChart chart) {
//        double maxValue = Math.ceil(findMaximumRange(chart)*1.2);
//        chart.setRange(0, maxValue);
    }

    /**
     * Return an image suitable for display when there isn't data available to
     * display a chart the user has requested.
     *
     * @return a <code>BufferedImage</code> with white background displaying the
     * text found in the NO_DATA_AVAILABLE token.
     */
    protected BufferedImage getNoRecordsFoundImage() {
        return getImageWithText(NO_DATA_FOUND_TEXT, NO_DATA_FOUND_IMAGE_TEXT_SIZE,
            true, NO_DATA_FOUND_IMAGE_FONT, NO_DATA_FOUND_IMAGE_TEXT_BOLD);
    }

    /**
     * Return an image suitable for display when there isn't data available to
     * display a chart the user has requested.
     *
     * @return a <code>BufferedImage</code> with white background displaying the
     * text found in the NO_DATA_AVAILABLE token.
     */
    protected BufferedImage getImageWithText(String imageText, int fontSize,
                                             boolean drawBoundingRect, String fontName, boolean isBold) {

        BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bi.createGraphics();

        //Set background color to white
        graphics.setPaint(Color.WHITE);
        graphics.fill(new Rectangle(1,1,getWidth()-2,getHeight()-2));

        //Set up the default colors
        graphics.setBackground(Color.WHITE);
        graphics.setPaint(Color.BLACK);
        int fontStyle = (isBold ? Font.BOLD : Font.PLAIN);
        graphics.setFont(new Font(fontName, fontStyle, fontSize));

        //Get the text width and height
        if (imageText == null) imageText = "";
        StringTokenizer tok = new StringTokenizer(imageText, "\n", false);
        int height=0;
        ArrayList linesToDraw = new ArrayList();
        while (tok.hasMoreTokens()) {
            String currentLine = tok.nextToken();
            Rectangle2D textSize = graphics.getFontMetrics().getStringBounds(
                currentLine, graphics);
            height += textSize.getHeight();

            linesToDraw.add(new LineToDraw(currentLine, textSize.getHeight(), textSize.getWidth()));
        }


        //Now that we have the text, find the correct position to draw it
        //  (note that you have to subtract 1 from width and height because they
        //   are zero-based)
        int yPosition = Math.round((getHeight()-1) / 2 - ((height-1) / 2));

        //Draw the string on the window
        for (Iterator it = linesToDraw.iterator(); it.hasNext();) {
            LineToDraw lineToDraw = (LineToDraw)it.next();
            int xPosition = Math.round((getWidth()-1) / 2 - ((lineToDraw.width-1) / 2));
            graphics.drawString(lineToDraw.lineText, xPosition, yPosition);
            yPosition += lineToDraw.height;
        }


        return bi;
    }

    /**
     * Get the proposed width of the chart.
     *
     * @return an <code>int</code> value indicating the proposed width of the
     * chart (in pixels).
     */
    public int getWidth() {
        int widthToReturn = DEFAULT_WIDTH;

        if (width > -1) {
            widthToReturn = width;
        }

        return widthToReturn;
    }

    /**
     * Get the proposed height of the chart.
     *
     * @return an <code>int</code> value indicating the proposed height of the
     * chart (in pixels).
     */
    public int getHeight() {
        int heightToReturn = DEFAULT_HEIGHT;

        if (height > -1) {
            heightToReturn = height;
        }

        return heightToReturn;
    }

    /**
     * Set the proposed width of the chart image.
     *
     * @param width a <code>int</code> value containing the proposed width of
     * the chart.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Set the proposed height of the chart image.
     *
     * @param height a <code>int</code> value containing the proposed height of
     * the chart.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    public BufferedImage imageToBufferedImage(java.awt.Image image) {
        BufferedImage bi = new BufferedImage(image.getWidth(null),
            image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.drawImage(image, null, null);
        return bi;
    }

    public boolean isShowLegend() {
        return showLegend;
    }

    public void setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
    }

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

    public Font getChartTitleFont() {
        return chartTitleFont;
    }

    public void setChartTitleFont(Font chartTitleFont) {
        this.chartTitleFont = chartTitleFont;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public LegendPosition getLegendPosition() {
        return legendPosition;
    }

    public void setLegendPosition(LegendPosition legendPosition) {
        this.legendPosition = legendPosition;
    }
}

/**
 * Helper class used to draw strings inside of an image.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
class LineToDraw {
    /** The text of the line to draw. */
    String lineText;
    /** The font metric height of the text. */
    int height;
    /** The font metric width of the text. */
    int width;

    /**
     * Constructor to prefill data values.
     *
     * @param lineText a <code>String</code> value containing the text of the
     * line to draw.
     * @param height a <code>int</code> value containing the height in pixels of
     * the line to draw.
     * @param width a <code>int</code> value containing the width in pixels of
     * the line to draw.
     */
    public LineToDraw(String lineText, double height, double width) {
        this.lineText = lineText;
        this.height = (int)height;
        this.width = (int)width;
    }
}

