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
package net.project.space;

/**
 * A SpaceMember represents a user's relationship to a space.  That includes
 * things such as their responsibilities in a project.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class SpaceMember {

    /**
     * The space to which the member belongs.
     */
    private String spaceID = null;

    /**
     * The member's ID.
     */
    private String personID = null;

    /**
     * The relationship between space and member.
     */
    private String relationship = null;

    /**
     * The member's responsibilities in the space.
     */
    private String responsibilities = null;

    /**
     * The member's title in the space.
     */
    private String title = null;

    /**
     * Sets the id of the space to which this membership pertains.
     * @param spaceID the space id
     * @see #getSpaceID
     */
    public void setSpaceID(String spaceID) {
        this.spaceID = spaceID;
    }

    /**
     * Returns the id of the space to which this membership pertains.
     * @return the id of the space
     * @see #setSpaceID
     */
    public String getSpaceID() {
        return this.spaceID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getPersonID() {
        return this.personID;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getRelationship() {
        return this.relationship;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public String getResponsibilities() {
        return this.responsibilities;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

}

