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

import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.xml.document.XMLDocument;

/**
 * A quantity and work metric is used to represent any metric which has "count" and a summarized work value.
 * <p>
 * <code>QuantityAndWorkMetric</code> is a concrete implementation of the <code>SimpleMetric</code> class.
 * <pre>
 * Usage Example:
 *
 * New Assignments This Week Metric -- Quantity: 14, Total Work:  63h
 *
 * @author Philip Dixon
 * @since Version 7.7
 */
public class QuantityAndWorkMetric extends SimpleMetric {

    /**
     * represents the "count" of this metric
     */
    private int quantity = 0;
    /**
     * represents the total work for this metric
     */
    private TimeQuantity work = new TimeQuantity(0, TimeQuantityUnit.SECOND);


    /**
     * Create a new QuantityAndWorkMetric with the specified <code>SimpleMetricType</code>
     *
     * @param type (ie. weekly, monthly)
     */
    public QuantityAndWorkMetric(SimpleMetricType type) {
        super(type);
    }

    /**
     * Create a new QuantityAndWorkMetric
     *
     * @param quantity The "count" or quantity represented by this metric
     * @param work     The total work represented by this metric
     * @param type
     */
    public QuantityAndWorkMetric(int quantity, TimeQuantity work, SimpleMetricType type) {
        this(type);
        this.quantity = quantity;
        this.work = work;
    }


    public QuantityAndWorkMetric add (QuantityAndWorkMetric metric, SimpleMetricType type) {

        QuantityAndWorkMetric result = new QuantityAndWorkMetric(type);

        result.setQuantity (metric.getQuantity() + this.getQuantity());
        result.setWork (metric.getWork().add(this.getWork()));

        return (result);
    }

    public void setQuantity (int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public TimeQuantity getWork() {
        return this.work;
    }

    public void setWork (TimeQuantity work) {
        this.work = work;
    }

    /**
     * Provides an XML structure of this SimpleMetric.
     * This structure may be used for presentation purposes.
     *
     * @return the XML structure
     */
    public XMLDocument getXMLDocument() {
        XMLDocument xml = new XMLDocument();

        xml.startElement("QuantityAndWorkMetric");
        xml.addAttribute("type", getType().getID());
        xml.addElement("Quantity", new Integer(quantity));
        xml.addElement("Work", work.convertTo(TimeQuantityUnit.HOUR, 2).toShortString(0, 2));
        xml.endElement();

        return (xml);
    }
}
