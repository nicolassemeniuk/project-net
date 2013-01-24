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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.IHTMLOption;

/**
 * Provides an Enumeration of Project ProjectVisibility values.
 * <p>
 * The visibility determines who may see a project listed in a portfolio.
 * It does not in any way affect who may navigate into a project space.
 * When a user defines a portfolio view, they may expand the set of projects
 * included in the view to more than just the projects that they are a member
 * of.
 * </p>
 * <p>
 * Called the class <code>ProjectVisibility</code> to avoid clashing with
 * <code>java.beans.Visibility</code> which is imported by default in JSP pages.
 * </p>
 * @since Version 7.4
 * @author Tim Morrow
 */
public class ProjectVisibility implements IHTMLOption, Serializable {

    //
    // Static members
    //

    private static Collection visibilityCollection = new ArrayList();

    /**
     * A project with this visibility is limited to be visible to participants
     * of a project.
     */
    public static final ProjectVisibility PROJECT_PARTICIPANTS = new ProjectVisibility("100", "prm.project.visibility.projectparticipants.name");

    /**
     * A project with this visibility may be seen a user's personal portfolio
     * if that user is a participant of the business that owns the project.
     */
    public static final ProjectVisibility OWNING_BUSINESS_PARTICIPANTS = new ProjectVisibility("200", "prm.project.visibility.owningbusinessparticipants.name");

    /**
     * A project with this visibility may be seen by any authenticated user.
     */
    public static final ProjectVisibility GLOBAL = new ProjectVisibility("300", "prm.project.visibility.global.name");

    /**
     * Returns the ProjectVisibility with the specified id.
     * @param id the id of the value to find
     * @return the ProjectVisibility with matching id, or null if no value is
     * found with that id
     */
    public static ProjectVisibility findByID(String id) {
        ProjectVisibility foundVisibility = null;
        boolean isFound = false;

        for (Iterator it = ProjectVisibility.visibilityCollection.iterator(); it.hasNext() && !isFound;) {
            ProjectVisibility nextVisibility = (ProjectVisibility) it.next();
            if (nextVisibility.getID().equals(id)) {
                foundVisibility = nextVisibility;
                isFound = true;
            }
        }

        return foundVisibility;
    }

    /**
     * Returns a collection of all the visibilities.
     * @return a collection where each element is a <code>ProjectVisibility</code>
     */
    public static Collection getAllVisibilities() {
        return Collections.unmodifiableCollection(ProjectVisibility.visibilityCollection);
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
     * Creates a new ProjectVisibility.
     * @param id the id
     * @param nameToken the token for the display name
     */
    private ProjectVisibility(String id, String nameToken) {
        this.id = id;
        this.nameToken = nameToken;
        visibilityCollection.add(this);
    }

    /**
     * Returns the internal id of this ProjectVisibility.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the display name of this ProjectVisibility.
     * @return the display name
     */
    public String getName() {
        return PropertyProvider.get(this.nameToken);
    }

    /**
     * Indicates whether the specified object is a ProjectVisibility with
     * matching ID.
     * @param o the ProjectVisibility object to compare
     * @return true if the specified ProjectVisibility has a matching id; false otherwise
     */
    public boolean equals(Object o) {
        boolean isEqual = false;

        if (this == o) {

            isEqual = true;

        } else {

            if (o instanceof ProjectVisibility) {
                ProjectVisibility visibility = (ProjectVisibility) o;
                if (id.equals(visibility.id)) {
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
    // Implementing IHTMLOption
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
    // End IHTMLOption
    //

}
