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
|    $Revision: 15748 $
|    $Date: 2007-03-01 20:20:09 +0530 (Thu, 01 Mar 2007) $
|    $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.resource;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.project.base.finder.DomainOption;
import net.project.base.property.PropertyProvider;

/**
 * A typed enumeration of TimesheetStatus classes.
 *
 * @author Sachin Mittal
 * @since Version 8.2.0
 */
public class TimesheetStatus extends DomainOption implements Serializable {

    /** This list contains all of the possible types for this typed enumeration. */
    private static Map types = new LinkedHashMap();
    public static final TimesheetStatus DRAFT = new TimesheetStatus("10", "prm.resource.timesheet.status.draft.name");
    public static final TimesheetStatus SUBMITTED = new TimesheetStatus("20", "prm.resource.timesheet.status.submitted.name");
    public static final TimesheetStatus APPROVED = new TimesheetStatus("30", "prm.resource.timesheet.status.approved.name");
    public static final TimesheetStatus REJECTED = new TimesheetStatus("40", "prm.resource.timesheet.status.rejected.name");
    public static final TimesheetStatus CANCELLED = new TimesheetStatus("50", "prm.resource.timesheet.status.cancelled.name");
    public static final TimesheetStatus DEFAULT = DRAFT;
	
    /**
     * Get the AssignmentStatus that corresponds to a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of a
     * <code>AssignmentStatus</code> we want to find.
     * @return a <code>AssignmentStatus</code> corresponding to the supplied ID, or
     * the DEFAULT <code>AssignmentStatus</code> if one cannot be found.
     */
    public static TimesheetStatus getForID(String id) {
    	TimesheetStatus toReturn = (TimesheetStatus)types.get(id);
        if (toReturn == null) {
            toReturn = DEFAULT;
        }

        return toReturn;
    }

    public static Collection getValidStatuses() {
        return Collections.unmodifiableCollection(types.values());
    }
    
    //**************************************************************************
    // Implementation code
    //**************************************************************************
    /** A Unique identifier for this AssignmentStatus */
    private String id;
    /** A token used to find a human-readable name for this AssignmentStatus */
    private String displayToken;
    
    /**
	 * @param value
	 * @param displayToken
	 */
	private TimesheetStatus(String id, String displayToken) {
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
     * Return a human-readable display name for this TimesheetStatus.
     *
     * @return a <code>String</code> containing a human-readable version of this
     * TimesheetStatus.
     */
    public String getDisplayName() {
        return PropertyProvider.get(displayToken);
    }

    /**
     * Return a human-readable display name for this TimesheetStatus.
     *
     * @return a <code>String</code> containing a human-readable version of this
     * TimesheetStatus.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }

	public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final TimesheetStatus timesheetStatus = (TimesheetStatus) o;

        if (id != null ? !id.equals(timesheetStatus.id) : timesheetStatus.id != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
