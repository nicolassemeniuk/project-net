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

 package net.project.schedule.metric;

import net.project.base.property.PropertyProvider;
import net.project.xml.document.XMLDocument;

/**
 * Enumerated Type to represent various assignment types, including :completed, in progress and all assignments.
 * Note: these id represent the "completion" flag of the Assignment object and are also used in XSL.
 * <br>
 *
 * @author Philip Dixon
 * @since Version 7.7
 */
public class TaskAssignmentMetricsType {

    /**
     * ID of the TaskAssignmentType.  Used in XSL for presentation
     */
    private final String id;

    /**
     * Display name of the <code>TaskAssignmentType</code>
     */
    private final String nameToken;

    /**
     * Static instantiation of a COMPLETED <code>TaskAssignmentMetricsType</code>
     * <p>
     * A "completed" assignment type represents all assignments which have been completed by a user.
     */
    public static TaskAssignmentMetricsType COMPLETED = new TaskAssignmentMetricsType("completedAssignments", "prm.personal.assignmentmetrics.type.completed");

    /**
     * Static instantiation of a IN_PROGRESS <code>TaskAssignmentMetricsType</code>
     * <p>
     * An "in progress" assignment type represents all assignments which are currently in progress by the user.
     */
    public static TaskAssignmentMetricsType IN_PROGRESS = new TaskAssignmentMetricsType("inProgressAssignments", "prm.personal.assignmentmetrics.type.inprogress");

    /**
     * Static instantiation of a ALL <code>TaskAssignmentMetricsType</code>
     * <p>
     * A "all" assignment type represents all assignments for the specified user.
     */

    public static TaskAssignmentMetricsType ALL = new TaskAssignmentMetricsType("allAssignments", "prm.personal.assignmentmetrics.type.all");


    /**
     * Creates a new TaskAssignmentMetricsType.
     * Due to private access, a new assignment type can only be instantiated via the static types above.
     *
     * @param id
     * @param nameToken
     */
    private TaskAssignmentMetricsType(String id, String nameToken) {
        this.id = id;
        this.nameToken = nameToken;

    }

    /**
     * Returns the ID of this <code>TaskAssignmentType</code>.
     * Used by XSL for formatting and by the <code>TaskAssignmentMetrics</code> class for query development.
     *
     * @return
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the display name of this metrics type.
     *
     * @return
     */
    public String getName() {
        return (PropertyProvider.get(this.nameToken));
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskAssignmentMetricsType)) return false;

        final TaskAssignmentMetricsType assignmentCompletionStatus = (TaskAssignmentMetricsType) o;

        if (!id.equals(assignmentCompletionStatus.id)) return false;

        return true;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        return this.id;
    }

    /**
     * Provides an XML structure of this SimpleMetric.
     * This structure may be used for presentation purposes.
     *
     * @return the XML structure
     */
    public XMLDocument getXMLDocument() {
        XMLDocument xml = new XMLDocument();

        xml.startElement("TaskAssignmentMetricsType");
        xml.addElement("ID", getID());
        xml.addElement("DisplayName", getName());
        xml.endElement();

        return (xml);
    }

}
