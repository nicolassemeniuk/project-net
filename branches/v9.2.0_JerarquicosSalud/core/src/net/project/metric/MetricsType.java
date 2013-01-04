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
 * Enumerated type representing the various types of <code>Metrics</code>
 * 
 * @author Philip Dixon
 * @since Version 7.7
 */
public class MetricsType {

    /**
     * ID of the metric type
     */
    private final String id;

    /** Display name for this MetricsType */
    private final String displayName;

    /**
     * Static instantiation of a PERSONAL_ASSIGNMENT <code>MetricsType</code>
     */
    public static MetricsType PERSONAL_ASSIGNMENT = new MetricsType("PersonalAssignmentMetrics", "XX Personal Assignment Metrics");


    /**
     * Private Contstructor to enforce the usage of the statically defined types
     * 
     * @param id of the MetricsType
     */
    private MetricsType(String id, String name) {
        this.id = id;
        this.displayName = name;
    }

    /**
     * returns the ID of the MetricsType
     *
     * @return the ID of the MetricsType
     */
    public String getID() {
        return this.id;
    }


    /**
     * Returns the display name of this MetricsType
     * @return
     */
    public String getDisplayName() {
        return this.displayName;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetricsType)) return false;

        final MetricsType metricsType = (MetricsType) o;

        if (id != null ? !id.equals(metricsType.id) : metricsType.id != null) return false;

        return true;
    }

    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    public String toString() {
        return this.id;
    }

}
