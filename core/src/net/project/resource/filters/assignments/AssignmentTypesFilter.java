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
package net.project.resource.filters.assignments;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.project.base.ObjectType;
import net.project.base.finder.EmptyFinderFilter;
import net.project.resource.AssignmentFinder;
import net.project.resource.AssignmentType;

/**
 * Filter to indicate to the {@link net.project.resource.AssignmentFinder} that
 * you only want to load a subset of the {@link net.project.resource.AssignmentType}s
 * that can be loaded.  (For example only to load Task Assignments).
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class AssignmentTypesFilter extends EmptyFinderFilter {
    /** Contains all the types of assignments that the finder is going to load. */
    private Collection assignmentTypes = null;

    /**
     * Standard constructor which sets up a unique identifier for this filter.
     *
     * @param id a <code>String</code> parameter which unique identifies this
     * filter.
     */
    public AssignmentTypesFilter(String id) {
        super(id);
    }

    /**
     * Set the types of assignments that you like to load when the find method
     * is called from within the AssignmentFinder.
     *
     * @param assignmentTypes a <code>List</code> of
     * {@link net.project.resource.AssignmentType} objects.
     */
    public void setAssignmentTypes(Collection assignmentTypes) {
        this.assignmentTypes = assignmentTypes;
    }

    /**
     * Set the types of assignments that you like to load when the find method
     * is called from within the AssignmentFinder.
     *
     * @param assignmentTypeID an array of strings which should be ID's to
     * {@link net.project.resource.AssignmentType} objects.
     */
    public void setAssignmentTypes(String[] assignmentTypeID) {
        Set assignmentTypes = new HashSet();

        for (int i = 0; i < assignmentTypeID.length; i++) {
            assignmentTypes.add(AssignmentType.forObjectType(assignmentTypeID[i]));
        }

        setAssignmentTypes(assignmentTypes);
    }

    /**
     * Get the assignment types that we are going to load.  If this list is empty,
     * we'll load all types.
     *
     * @return a <code>Collection</code> containing the type of assignments we
     * are going to load.
     */
    public Collection getAssignmentTypes() {
        return assignmentTypes;
    }

    public String getWhereClause() {
        StringBuffer whereClause = new StringBuffer();
        String spaceClause = "("+AssignmentFinder.SPACE_TYPE_COLUMN.getColumnName()+"=''{0}'')";
        String typeClause = "("+AssignmentFinder.OBJECT_TYPE_COLUMN.getColumnName()+"=''{0}'')";

        //If a null values has gotten in here, remove it.
        if(assignmentTypes != null && assignmentTypes.contains(null))
            assignmentTypes.remove(null);
        
        if (assignmentTypes != null && assignmentTypes.size() > 0) {
            whereClause.append("(");
            boolean first = true;
            for (Iterator iterator = assignmentTypes.iterator(); iterator.hasNext();) {
                if (first) {
                    first = false;
                } else {
                    whereClause.append(" or ");
                }

                AssignmentType assignmentType = (AssignmentType) iterator.next();

                //sjmiittal: refactored it to get on work type of assignments and not sapce type
//                if (assignmentType.equals(AssignmentType.APPLICATION)) {
//                    whereClause.append(MessageFormat.format(typeClause, new Object[]{ISpaceTypes.APPLICATION_SPACE}));
//                } else if (assignmentType.equals(AssignmentType.BUSINESS)) {
//                    whereClause.append(MessageFormat.format(typeClause, new Object[]{ISpaceTypes.BUSINESS_SPACE}));
//                } else if (assignmentType.equals(AssignmentType.CONFIGURATION)) {
//                    whereClause.append(MessageFormat.format(typeClause, new Object[]{ISpaceTypes.CONFIGURATION_SPACE}));
//                } else if (assignmentType.equals(AssignmentType.PROJECT)) {
//                    whereClause.append(MessageFormat.format(typeClause, new Object[]{ISpaceTypes.PROJECT_SPACE}));
//                } 
                if (assignmentType.equals(AssignmentType.MEETING)) {
                    whereClause.append(MessageFormat.format(typeClause, new Object[]{ObjectType.MEETING}));
                } else if (assignmentType.equals(AssignmentType.TASK)) {
                    whereClause.append(MessageFormat.format(typeClause, new Object[]{ObjectType.TASK}));
                } else if (assignmentType.equals(AssignmentType.ACTIVITY)) {
                    whereClause.append(MessageFormat.format(typeClause, new Object[]{ObjectType.ACTIVITY}));
                } else if (assignmentType.equals(AssignmentType.FORM)) {
                    whereClause.append(MessageFormat.format(typeClause, new Object[]{ObjectType.FORM_DATA}));
                }
//                else if(assignmentType.equals(AssignmentType.ENTERPRISE)){
//                	whereClause.append(MessageFormat.format(typeClause, new Object[]{ObjectType.ENTERPRISE_SPACE}));
//                }
            }
            whereClause.append(")");
        }

        return whereClause.toString();
    }
}
