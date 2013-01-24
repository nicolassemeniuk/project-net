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
 * Provides the base class for all Simple Metric types.
 * <p>
 * A SimpleMetric is essentially a primative metric which represents a basic measure and does
 * not imply a collection or aggregation of various other metrics.
 * <p>
 * Each SimpleMetric must be instantiated with a specified <code>SimpleMetricType</code>.
 * <p>
 * Example SimpleMetric is a <code>QuantityAndWorkMetric</code>
 *
 * @author Philip Dixon
 * @since Version 7.7
 */
public abstract class SimpleMetric {

    /**
     * Represents the type of this metric
     */
    private final SimpleMetricType type;

    /**
     * Constructor to enforce the specification of SimpleMetricType
     */
    protected SimpleMetric(SimpleMetricType type) {
        if (type == null) {
            throw new NullPointerException("SimpleMetricType is Required");
        }
        this.type = type;
    }

    /**
     * Returns the SimpleMetricType of this SimpleMetric.
     * @return
     */
    public SimpleMetricType getType() {
        return this.type;
    }

    /**
     * Provides an XML structure of this SimpleMetric.
     * This structure may be used for presentation purposes.
     *
     * @return the XML structure
     */
    public abstract XMLDocument getXMLDocument();



}
