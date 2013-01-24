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

 package net.project.resource;

import java.io.Serializable;

import net.project.channel.ScopeType;
import net.project.security.User;
import net.project.space.Space;

/**
 * Provides storage of person properties against a specific space
 * (the traditional mechanism).
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class PersonPropertySpaceScope extends PersonPropertyScope implements
    IPersonPropertyScope, Serializable {

    /** The ID of the space for this scope. */
    private String spaceID;

    /**
     * Plain constructor required for serializable.
     */
    public PersonPropertySpaceScope() {
    }

    /**
     * Creates a new Space Scope for the specified space.
     * @param user the user for whom to store person properties
     * @param space the space against which person properties are to
     * be stored.
     */
    public PersonPropertySpaceScope(User user, Space space) {
        this(user.getID(), space.getID());
    }

    /**
     * Creates a new Space scope for the user and space for the specified IDs.
     * @param userID the ID of the user for whom to store person properties
     * @param spaceID the ID of the space against which person properties are to
     * be store
     */
    public PersonPropertySpaceScope(String userID, String spaceID) {
        super(ScopeType.SPACE, userID);
        this.spaceID = spaceID;
    }

    /**
     * Returns the ID for this scope which is the space ID.
     * @return the space ID
     */
    public String getSpaceID() {
        return this.spaceID;
    }

}
