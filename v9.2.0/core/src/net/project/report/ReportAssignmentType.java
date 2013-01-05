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
package net.project.report;

import java.util.ArrayList;


import net.project.base.property.PropertyProvider;

public class ReportAssignmentType {

    /** The collection of all report assignments types available. */
    private static ArrayList<ReportAssignmentType> reports = new ArrayList<ReportAssignmentType>();

    /** Show only task assignments on report.*/
    public static final ReportAssignmentType TASK_ASSIGNMENT_REPORT = new ReportAssignmentType("task", "prm.report.assignmenttypejsp.taskassignments.message");
    /** Show only form assignments on report.*/
    public static final ReportAssignmentType FORM_ASSIGNMENT_REPORT = new ReportAssignmentType("form", "prm.report.assignmenttypejsp.formassignments.message");    
    /** Show all type of assignments on report.*/    
    public static final ReportAssignmentType ALL_ASSIGNMENT_REPORT = new ReportAssignmentType("all",  "prm.report.assignmenttypejsp.allassignments.message");
    /** Deafault assignment type.*/
    public static final ReportAssignmentType DEAFULT_ASSIGNMENT_REPORT = TASK_ASSIGNMENT_REPORT;
    
    /**
     * Get the report assignment type that corresponds to the string provided to this
     * method.
     *
     * @param reportAssignmentTypeID a <code>String</code> value that identifies one
     * type of assignment.
     * @return a <code>ReportAssignmentType</code> that corresponds to the report
     * assignmetn type id identified by the parameter to this method.
     */
    public static ReportAssignmentType getForID(String reportAssignmentTypeID) {
        ReportAssignmentType toReturn = DEAFULT_ASSIGNMENT_REPORT;
        for(ReportAssignmentType currentReport : reports){
            if (currentReport.getID().equals(reportAssignmentTypeID)) {
                toReturn = currentReport;
            }
        }
        return toReturn;
    }  
    
    /**
     * Get a <code>String</code> containing HTML radio buttons for each report
     * assignment type.
     *
     * @param htmlName a <code>String</code> containing the name that each radio
     * button should have in the html input tags that are returned form this method.
     * @param selected a <code>ReportAssignmentType</code> which should be selected
     * initially.
     * @return a <code>String</code> value containing HTML option tags.
     */
    public static String getHTMLRadioList(String htmlName, ReportAssignmentType selected) {
        return getHTMLRadioList(htmlName, (ReportAssignmentType[])reports.toArray(), selected);
    }

    /**
     * Get a <code>String</code> containing HTML radio buttons for each report
     * assignment type.
     *
     * @param htmlName a <code>String</code> containing the name that each radio
     * button should have in the html input tags that are returned form this method.
     * @param validOptions a <code>ReportAssignmentType[]</code> value which indicates
     * all of the report assignment types that should be displayed.
     * @param selected a <code>ReportAssignmentType</code> which should be selected
     * initially.
     * @return a <code>String</code> value containing HTML option tags for each
     * report assignment type specified in the parameter.
     */
    public static String getHTMLRadioList(String htmlName, ReportAssignmentType[] validOptions, ReportAssignmentType selected) {
        StringBuffer html = new StringBuffer();

        for (int i = 0; i < validOptions.length; i++) {
            html.append("<input type=\"radio\" name=\"").append(htmlName)
                .append("\" value=\"").append(validOptions[i].getID())
                .append("\"").append((validOptions[i].equals(selected) ? " checked" : ""))
                .append(">").append(validOptions[i].getName()).append("<br>");
        }

        return html.toString();
    }    
    

    //--------------------------------------------------------------------------
    //Instance implementation
    //--------------------------------------------------------------------------

    /**
     * A string that uniquely identifies this report assignment type to non-java entities,
     * such as JSP.
     */
    private String uniqueID;
    
    /**
     * Points to a token value where a human-readable name for this report assignment
     * type can be found.
     */
    private String nameToken;
    
    
    /**
     * Standard private constructor designed to be used internally.
     *
     * @param reportTypeID a <code>String</code> value that uniquely identifies
     * this report.
     * @param nameToken a <code>String</code> value pointing to the human-readable
     * name for this <code>ReportType</code>.
     */
    private ReportAssignmentType(String reportTypeID, String nameToken) {
        reports.add(this);
        this.uniqueID = reportTypeID;
        this.nameToken = nameToken;
    }

    /**
     * Get a <code>String</code> that uniquely identifies this report type id
     * outside of java classes.
     *
     * @return a <code>String</code> value that uniquely identifies this
     * ReportType.
     */
    public String getID() {
        return uniqueID;
    }
    
    /**
     * Get a <code>String</code> representation of this report assignment type id.
     *
     * @return a <code>String</code> value containing the unique id for this
     * object.
     * @see #getID
     */
    public String toString() {
        return uniqueID;
    }

    /**
     * Get a human-readable name for this assignment type.
     *
     * @return a <code>String</code> value that contains a human-readable name
     * for this assignment type.
     */
    public Object getName() {
        return PropertyProvider.get(nameToken);
    }    
}

