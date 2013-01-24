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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.project.base.ObjectType;
import net.project.base.PnetRuntimeException;
import net.project.base.finder.DomainOption;
import net.project.base.property.PropertyProvider;

/**
 * An AssignmentType is a type of assignable object.  It is used for filtering
 * assignments.
 *
 * @author Tim Morrow
 * @since Emu
 */
public class AssignmentType extends DomainOption implements java.io.Serializable {

    /** All the assignment types in an arraylist for easy iteration */
    private static ArrayList assignmentTypes = new ArrayList();
    
    /** All the assignment types in an arraylist for easy iteration */
    private static ArrayList spaceAssignmentTypes = new ArrayList();

    private static int nextID = 0;

    /**
     * Returns the AssignmentType for the specified object type.
     * @param objectType the objectType of an assignable object
     * @return the assignment type or null if no type found for that
     * object type
     */
    public static AssignmentType forObjectType(String objectType) {

        AssignmentType assignmentType = null;
        boolean isTypeFound = false;

        // Loop through assignment types, find the one with
        // matching object type
        for (Iterator it = assignmentTypes.iterator(); it.hasNext() && !isTypeFound;) {
            AssignmentType nextAssignmentType = (AssignmentType) it.next();
            if (nextAssignmentType.getObjectType().equals(objectType)) {
                assignmentType = nextAssignmentType;
                isTypeFound = true;
            }

        }
        
        //if still not found then loop through space assignments
        // find the one with matching object type
        for (Iterator it = spaceAssignmentTypes.iterator(); it.hasNext() && !isTypeFound;) {
            AssignmentType nextAssignmentType = (AssignmentType) it.next();
            if (nextAssignmentType.getObjectType().equals(objectType)) {
                assignmentType = nextAssignmentType;
                isTypeFound = true;
            }

        }

        return assignmentType;
    }

    //
    // Private Instance variables
    //

    /** ID of this AssignmentType */
    private int id = -1;

    /** The ObjectType of an assignable object of this assignment type. */
    private final String objectType;

    /** The display name of this assignment type. */
    private final String displayName;

    /** The name of the class that provides Assignments of this type. */
    private final String assignmentClassName;

    /** Indicates if an assignment type can "capture work". */
    private final boolean canCaptureWork;
    
    /** Indicates if an assignment type can "capture work". */
    private final boolean isSpaceAssignment;

    /**
     * Creates a new assignment type for the specified object type.
     * @param objectType the object type of an assignable object of this
     * assignment type.
     * @param displayName the display name of this assignment type
     */
    private AssignmentType(String objectType, String displayName, String assignmentClassName, boolean canCaptureWork, boolean isSpaceAssignment) {
        super(objectType, displayName);
        this.id = nextID++;
        this.objectType = objectType;
        this.displayName = displayName;
        this.assignmentClassName = assignmentClassName;
        this.canCaptureWork = canCaptureWork;
        this.isSpaceAssignment = isSpaceAssignment;
        
        if(isSpaceAssignment)
            spaceAssignmentTypes.add(this);
        else
            assignmentTypes.add(this);
    }

    /**
     * Returns the unique ID of this assignment type.
     * @return the ID, unique amongst all other assignment types
     */
    private int getID() {
        return this.id;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public boolean canCaptureWork() {
        return canCaptureWork;
    }
    
    public boolean isSpaceAssignment() {
        return isSpaceAssignment;
    }
    

    /**
     * Returns the display name of this assignment type.
     * @return the display name
     */
    public String getDisplayName() {
        return PropertyProvider.get(this.displayName);
    }

    /**
     * Returns the name of the class that provides Assignments for this
     * type.
     * @return the fully qualified class name
     */
    private String getClassName() {
        return this.assignmentClassName;
    }

    /**
     * Creates a new Assignment class for this AssignmentType.
     * @return the Assignment class
     */
    public Assignment newAssignment() {

        Assignment assignment;
        Class assignmentClass;

        try {
            assignmentClass = Class.forName(getClassName());
            assignment = (Assignment) assignmentClass.newInstance();

        } catch (ClassNotFoundException e) {
            throw new PnetRuntimeException("Unable locate assignment class with name " + getClassName(), e);

        } catch (InstantiationException e) {
            throw new PnetRuntimeException("Unable to instantiate assignment class with name " + getClassName(), e);

        } catch (IllegalAccessException e) {
            throw new PnetRuntimeException("Unable to instantiate assignment class with name " + getClassName(), e);

        }

        return assignment;
    }

    /**
     * Indicates whether specified object is equal to this assignment type.
     * Two assignment types are equal if they have the same ID.
     * @return true if the specified object is the same as this assignment type;
     * false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssignmentType)) return false;

        final AssignmentType assignmentType = (AssignmentType) o;

        if (id != assignmentType.id) return false;

        return true;
    }

    public int hashCode() {
        return id;
    }

    //
    // Assignment Types
    // These go at bottom of class so that all other private static and otherwise
    // class and instance variables get initialized before the constructors are called

    /** Meeting. */
    public static final AssignmentType MEETING = new AssignmentType(ObjectType.MEETING, "@prm.directory.resource.assignmenttype.meeting.name", "net.project.resource.MeetingAssignment", false, false);
    
    /** Task. */
    public static final AssignmentType TASK = new AssignmentType(ObjectType.TASK, "@prm.directory.resource.assignmenttype.task.name", "net.project.resource.ScheduleEntryAssignment", true, false);
    
    /** Activity. */
    public static final AssignmentType ACTIVITY = new AssignmentType(ObjectType.ACTIVITY, "@prm.directory.resource.assignmenttype.activity.name", "net.project.resource.ActivityAssignment", false, false);
    
    /** Form. */
    public static final AssignmentType FORM = new AssignmentType(ObjectType.FORM_DATA, "@prm.directory.resource.assignmenttype.form.name", "net.project.form.assignment.FormAssignment", true, false);


    /** Project Invitation. */
    public static final AssignmentType PROJECT = new AssignmentType(ObjectType.PROJECT, "@prm.directory.resource.assignmenttype.project.name", "net.project.resource.SpaceAssignment", false, true);

    /** Business Invitation. */
    public static final AssignmentType BUSINESS = new AssignmentType(ObjectType.BUSINESS, "@prm.directory.resource.assignmenttype.business.name", "net.project.resource.SpaceAssignment", false, true);

    /** Application Invitation. */
    public static final AssignmentType APPLICATION = new AssignmentType(ObjectType.APPLICATION, "@prm.directory.resource.assignmenttype.application.name", "net.project.resource.SpaceAssignment", false, true);

    /** Configuration Invitation. */
    public static final AssignmentType CONFIGURATION = new AssignmentType(ObjectType.CONFIGURATION, "@prm.directory.resource.assignmenttype.configuration.name", "net.project.resource.SpaceAssignment", false, true);

    /** Enterprise Invitation. */
    public static final AssignmentType ENTERPRISE = new AssignmentType(ObjectType.ENTERPRISE_SPACE, "@prm.directory.resource.assignmenttype.enterprise.name", "net.project.resource.SpaceAssignment", false, true);

    /** All Assignment types. */
    public static final Collection ALL = Collections.unmodifiableCollection(assignmentTypes);
}
