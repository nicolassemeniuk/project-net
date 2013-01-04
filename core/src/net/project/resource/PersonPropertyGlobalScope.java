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

import net.project.channel.ScopeType;
import net.project.security.User;

/**
 * Provides a global scope against which person properties
 * may be stored.
 * <p>
 * This means properties are not limited to a specific space,
 * instead are applied to all spaces.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class PersonPropertyGlobalScope extends PersonPropertyScope implements IPersonPropertyScope {

    /** The global scope ID. */
    private static final String GLOBAL_SCOPE_ID = "100";

    /**
     * Creates a global scope for the specified user.
     * <p>
     * Properties for that user are stored irrespective of any space.
     * </p>
     * @param user the user for whom to store person properties
     */
    public PersonPropertyGlobalScope(User user) {
        this(user.getID());
    }

    public PersonPropertyGlobalScope(String userID) {
        super(ScopeType.GLOBAL, userID);
    }

    /**
     * Returns the ID for storage of person properties against the
     * global scope, which is <code>100</code>.
     * @return the ID for global scope
     */
    public final String getSpaceID() {
        return GLOBAL_SCOPE_ID;
    }

}
