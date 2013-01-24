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

 package net.project.process;

import java.io.Serializable;

/**
 * Represents the method to be used for calculating Project Percent Complete.
 * Currently, there are two methods:
 * - Manual
 * - Schedule Based
 */
public class ProgressReportingMethod implements Serializable {

    /** ID represented the caluculation method */
    private String id;

    /** String constant representing manual completion type.
     * This will be the value stored in the DB.
     */
    private static final String MANUAL_TYPE = "manual";

    /** String constant representing manual completion type.
     * This will be the value stored in the DB.
     */
    private static final String SCHEDULE_TYPE = "schedule";

    /** The type representing manual completion method */
    public static final ProgressReportingMethod MANUAL = new ProgressReportingMethod(MANUAL_TYPE);

    /** The type representing Schedule completion method */
    public static final ProgressReportingMethod SCHEDULE = new ProgressReportingMethod(SCHEDULE_TYPE);


    /** Returns the PercentCalculationMethod for the given ID.
     *
     * @param id the id of the completion method.
     * @return the completionmethod for the given id or null.
     */
    public static ProgressReportingMethod getForID (String id) {

        ProgressReportingMethod method = null;

        if (id.equals(MANUAL_TYPE)) {
            method = MANUAL;
        } else if (id.equals(SCHEDULE_TYPE)) {
            method = SCHEDULE;
        }

        return method;
    }

    public String getID() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProgressReportingMethod)) return false;

        final ProgressReportingMethod method = (ProgressReportingMethod) o;

        if (id != null ? !id.equals(method.id) : method.id != null) return false;

        return true;
    }

    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    /** Private constructor for this class */
    private ProgressReportingMethod (String id) {
        this.id = id;
    }

}
