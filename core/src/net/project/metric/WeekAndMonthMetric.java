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

 package net.project.metric;

import net.project.xml.document.XMLDocument;

/**
 * A WeekAndMonthMetric is a SimpleMetric which contains two QuantityAndWorkMetrics (week and month).
 * The metric includes weekly and monthly QuantityAndWorkMetric values
 * <pre>
 * For Example:
 *
 * Item             Week Qty.   Week Work       Month Qty.  Month Work
 * New Assignments     17           43h             64          212h
 * </pre>
 * 
 * @author Philip Dixon
 * @since Version 7.7
 */
public class WeekAndMonthMetric extends SimpleMetric {

    /** QuantityAndWorkMetric representing the values of this metric for a one-week period */
    private QuantityAndWorkMetric week = new QuantityAndWorkMetric(SimpleMetricType.WEEKLY);
    /** QuantityAndWorkMetric representing the values of this metric for a one-month period */
    private QuantityAndWorkMetric month = new QuantityAndWorkMetric(SimpleMetricType.MONTHLY);


    /**
     * creates a new week and month metric
     */
    public WeekAndMonthMetric() {
        super(SimpleMetricType.WEEKANDMONTH);
    }

    /**
     * Creates a new WeekAndMonthMetric with the specified values for week and month metrics.
     * @param week
     * @param month
     */
    public WeekAndMonthMetric (QuantityAndWorkMetric week, QuantityAndWorkMetric month) {

        this();
        this.week = week;
        this.month = month;
    }


    public QuantityAndWorkMetric getWeek() {
        return week;
    }

    public void setWeek(QuantityAndWorkMetric week) {
        this.week = week;
    }

    public QuantityAndWorkMetric getMonth() {
        return month;
    }

    public void setMonth(QuantityAndWorkMetric month) {
        this.month = month;
    }

    /**
     * Returns the "sum" of two WeekAndMonthMetrics.
     * <p>
     * Note this method does not affect the state of the instantiated Metrics.
     * @param metric
     * @return Returns a new WeekAndMonthMetric representing the "sum" of two WeekAndMonthMetrics.
     */
    public WeekAndMonthMetric add (WeekAndMonthMetric metric) {

        WeekAndMonthMetric result = new WeekAndMonthMetric();

        result.setWeek(getWeek().add(metric.getWeek(), SimpleMetricType.WEEKLY));
        result.setMonth(getMonth().add(metric.getMonth(), SimpleMetricType.MONTHLY));

        return (result);
    }

    protected void addXMLElements(XMLDocument xml) {
        // do nothing
    }


    /**
     * Provides an XML structure of this SimpleMetric.
     * This structure may be used for presentation purposes.
     *
     * @return the XML structure
     */
    public XMLDocument getXMLDocument() {
        XMLDocument xml = new XMLDocument();

        xml.startElement("WeekAndMonthMetric");
        xml.addElement(getWeek().getXMLDocument());
        xml.addElement(getMonth().getXMLDocument());
        xml.endElement();

        return (xml);
    }
}

