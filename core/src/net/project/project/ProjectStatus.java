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
package net.project.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;

/**
 * Provides an Enumeration of values.
 */
public class ProjectStatus implements net.project.gui.html.IHTMLOption {

    //
    // Static members
    //

    private static List statusList = new ArrayList();

    public static final ProjectStatus NOT_STARTED = new ProjectStatus("100", "prm.project.status.notstarted.name");

    public static final ProjectStatus IN_PROCESS = new ProjectStatus("200", "prm.project.status.inprocess.name");

    public static final ProjectStatus ON_HOLD = new ProjectStatus("300", "prm.project.status.onhold.name");

    public static final ProjectStatus COMPLETED = new ProjectStatus("400", "prm.project.status.completed.name");

    public static final ProjectStatus PROPOSED = new ProjectStatus("500", "prm.project.status.proposed.name");

    public static final ProjectStatus IN_PLANNING = new ProjectStatus("600", "prm.project.status.inplanning.name");

    public static final ProjectStatus DEFAULT = IN_PROCESS;

    /**
     * Returns the ProjectStatus with the specified id.
     * @param id the id of the value to find
     * @return the ProjectStatus with matching id, or null if no value is
     * found with that id
     */
    public static ProjectStatus findByID(String id) {
        ProjectStatus foundProjectStatus = null;
        boolean isFound = false;

        for (Iterator it = ProjectStatus.statusList.iterator(); it.hasNext() && !isFound;) {
            ProjectStatus nextProjectStatus = (ProjectStatus) it.next();
            if (nextProjectStatus.getID().equals(id)) {
                foundProjectStatus = nextProjectStatus;
                isFound = true;
            }
        }

        return foundProjectStatus;
    }

    public static List getAllProjectStatus() {
        return Collections.unmodifiableList(ProjectStatus.statusList);
    }


    //
    // Instance members
    //

    /**
     * The unique id.
     */
    private String id = null;

    /**
     * The token which provides the display name.
     */
    private String nameToken = null;

    /**
     * Creates a new ProjectStatus.
     * @param id the id
     * @param nameToken the token for the display name
     */
    private ProjectStatus(String id, String nameToken) {
        this.id = id;
        this.nameToken = nameToken;
        statusList.add(this);
    }

    /**
     * Returns the internal id of this ProjectStatus.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the token that provides the name of this ProjectStatus.
     * @return the token
     */
    public String getNameToken() {
        return this.nameToken;
    }

    /**
     * Returns the display name of this ProjectStatus.
     * @return the display name
     */
    public String getName() {
        return PropertyProvider.get(this.nameToken);
    }

    /**
     * Indicates whether the specified object is a ProjectStatus with
     * matching ID.
     * @param o the ProjectStatus object to compare
     * @return true if the specified ProjectStatus has a matching id; false otherwise
     */
    public boolean equals(Object o) {
        boolean isEqual = false;

        if (this == o) {

            isEqual = true;

        } else {

            if (o instanceof ProjectStatus) {
                ProjectStatus statusList = (ProjectStatus) o;
                if (id.equals(statusList.id)) {
                    isEqual = true;
                }
            }

        }

        return isEqual;
    }

    public int hashCode() {
        return id.hashCode();
    }

    //
    // Implementing IHtmlOption
    //

    /**
     * Returns the value for the <code>value</code> attribute of the HTML
     * option.
     * @return the id
     */
    public String getHtmlOptionValue() {
        return getID();
    }

    /**
     * Returns the value for the content part of the HTML option.
     * @return the display name
     */
    public String getHtmlOptionDisplay() {
        return getName();
    }

    //
    // End IHtmlOption
    //

}
