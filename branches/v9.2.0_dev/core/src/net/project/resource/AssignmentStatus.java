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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.resource;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.project.base.finder.DomainOption;
import net.project.base.property.PropertyProvider;
import net.project.gui.html.IHTMLOption;

/**
 * A typed enumeration of AssignmentStatus classes.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class AssignmentStatus extends DomainOption implements IHTMLOption {
    /** This list contains all of the possible types for this typed enumeration. */
    private static Map types = new LinkedHashMap();
    public static final AssignmentStatus ASSIGNED = new AssignmentStatus("10", "prm.global.assignment.status.assigned.name");
    public static final AssignmentStatus ACCEPTED = new AssignmentStatus("20", "prm.global.assignment.status.accepted.name");
    public static final AssignmentStatus IN_PROCESS = new AssignmentStatus("30", "prm.global.assignment.status.inprocess.name");
    public static final AssignmentStatus COMPLETED_PENDING = new AssignmentStatus("40", "prm.global.assignment.status.completedconfirmationpending.name");
    public static final AssignmentStatus COMPLETED_CONFIRMED = new AssignmentStatus("50", "prm.global.assignment.status.completedconfirmed.name");
    public static final AssignmentStatus DELEGATED = new AssignmentStatus("60", "prm.global.assignment.status.delegated.name");
    public static final AssignmentStatus REJECTED = new AssignmentStatus("70", "prm.global.assignment.status.rejected.name");
    public static final AssignmentStatus RETURNED = new AssignmentStatus("80", "prm.global.assignment.status.returned.name");
    public static final AssignmentStatus DEFAULT = ASSIGNED;

    public static List personalAssignmentTypes = Arrays.asList(new AssignmentStatus[] { ASSIGNED, ACCEPTED, IN_PROCESS });
    public static Collection ALL = Collections.unmodifiableCollection(types.values());
    public static Collection OPEN_STATUSES = Arrays.asList(new AssignmentStatus[] {ACCEPTED, IN_PROCESS});

    /**
     * Get the AssignmentStatus that corresponds to a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of a
     * <code>AssignmentStatus</code> we want to find.
     * @return a <code>AssignmentStatus</code> corresponding to the supplied ID, or
     * the DEFAULT <code>AssignmentStatus</code> if one cannot be found.
     */
    public static AssignmentStatus getForID(String id) {
        AssignmentStatus toReturn = (AssignmentStatus)types.get(id);
        if (toReturn == null) {
            toReturn = DEFAULT;
        }

        return toReturn;
    }

    public static Collection getValidStatuses() {
        return Collections.unmodifiableCollection(types.values());
    }

    public static List getPersonalAssignmentStatuses() {
        return Collections.unmodifiableList(personalAssignmentTypes);
    }

    //**************************************************************************
    // Implementation code
    //**************************************************************************
    /** A Unique identifier for this AssignmentStatus */
    private String id;
    /** A token used to find a human-readable name for this AssignmentStatus */
    private String displayToken;

    /**
     * Private constructor which creates a new AssignmentStatus instance.
     *
     * @param id a <code>String</code> value which is a unique identifier for 
     * this typed enumeration.
     * @param displayToken a <code>String</code> value which contains a token to
     * look up the display name for this type.
     */
    private AssignmentStatus(String id, String displayToken) {
        super(id, displayToken);
        this.id = id;
        this.displayToken = displayToken;
        types.put(id, this);
    }

    /**
     * Get the unique identifier for this type enumeration.
     *
     * @return a <code>String</code> value containing the unique id for this 
     * type.
     */
    public String getID() {
        return id;
    }

    /**
     * Return a human-readable display name for this AssignmentStatus.
     *
     * @return a <code>String</code> containing a human-readable version of this
     * AssignmentStatus.
     */
    public String getDisplayName() {
        return PropertyProvider.get(displayToken);
    }

    /**
     * Return a human-readable display name for this AssignmentStatus.
     *
     * @return a <code>String</code> containing a human-readable version of this
     * AssignmentStatus.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }

    /**
     * Returns the value for the <code>value</code> attribute of the HTML
     * option.
     *
     * @return a <code>String</code> value which will become the value="?" part
     * of the option tag.
     */
    public String getHtmlOptionValue() {
        return id;
    }

    /**
     * Returns the value for the content part of the HTML option.
     *
     * @return a <code>String</code> value that will be displayed for this
     * html option.
     */
    public String getHtmlOptionDisplay() {
        return getDisplayName();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final AssignmentStatus assignmentStatus = (AssignmentStatus) o;

        if (id != null ? !id.equals(assignmentStatus.id) : assignmentStatus.id != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
