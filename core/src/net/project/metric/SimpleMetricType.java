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

/**
 * Enumerated Type to represent various <code>SimpleMetric</code> types.
 * <p>The types describe the granularity of the metric.  For Example, periods:  week, month, etc.
 *
 * @author Philip Dixon
 * @since Version 7.7
 */
public class SimpleMetricType {

    /**
     * ID of the metric type
     */
    private final String id;

    /**
     * Static <code>SimpleMetricType</code> representing a weekly Simple Metric
     */
    public static SimpleMetricType WEEKLY = new SimpleMetricType("week");
    /**
     * Static <code>SimpleMetricType</code> representing a weekly Simple Metric
     */
    public static SimpleMetricType MONTHLY = new SimpleMetricType("month");

    /** Static type representing a <code>WeekAndMonthMetric</code> */
    public static SimpleMetricType WEEKANDMONTH = new SimpleMetricType("weekAndMonth");

    /** Static type representing a <code>QuantityAndWorkMetric</code> */
    public static SimpleMetricType QUANTITYANDWORK = new SimpleMetricType("quantityAndWork");

    /**
     * Private Contstructor to enforce the usage of the statically defined types
     *
     * @param id of the metric
     */
    private SimpleMetricType(String id) {
        this.id = id;

    }


    /**
     * Returns the ID of this metric type.
     * Note, the ID is used in XSL
     *
     * @return id of this SimpleMetricType
     */
    public String getID() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleMetricType)) return false;

        final SimpleMetricType assignmentCompletionStatus = (SimpleMetricType) o;

        if (!id.equals(assignmentCompletionStatus.id)) return false;

        return true;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        return this.id;
    }
}
