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

 package net.project.personal.metric;


/**
 * Enumerated type representing the various types of <code>PersonalAssignmentMetrics</code>
 * 
 * @author Philip Dixon
 * @since Version 7.7
 */
public class PersonalAssignmentMetricsType {

    /**
     * ID of the metric type
     */
    private final String id;

    /**
     * Static instantiation of a TASK <code>MetricsType</code>
     */
    public static PersonalAssignmentMetricsType TASK = new PersonalAssignmentMetricsType("task");

    /**
     * Static instantiation of a TASK <code>MetricsType</code>
     */
    public static PersonalAssignmentMetricsType MEETING = new PersonalAssignmentMetricsType("meeting");

        /**
     * Static instantiation of a TASK <code>MetricsType</code>
     */
    public static PersonalAssignmentMetricsType GROUP_TOTAL = new PersonalAssignmentMetricsType("groupTotal");


    /**
     * Private Contstructor to enforce the usage of the statically defined types
     * 
     * @param id of the MetricsType
     */
    private PersonalAssignmentMetricsType(String id) {
        this.id = id;
    }

    /**
     * returns the ID of the MetricsType
     *
     * @return the ID of the MetricsType
     */
    public String getID() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonalAssignmentMetricsType)) return false;

        final PersonalAssignmentMetricsType metricsType = (PersonalAssignmentMetricsType) o;

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
