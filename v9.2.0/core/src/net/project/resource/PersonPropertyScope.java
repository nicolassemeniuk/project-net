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

/**
 * Provides some default implementations of IPersonPropertyScope.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
abstract class PersonPropertyScope implements IPersonPropertyScope {

    private ScopeType scopeType;

    /** The ID of the user for whom to store properties in this scope. */
    private String userID;

    /**
     * Empty constructor required for serialization.
     */
    protected PersonPropertyScope() {
    }

    /**
     * Creates a scope for the specified user.
     * @param scopeType
     * @param userID the ID of the user for whom to store person properties
     */
    PersonPropertyScope(ScopeType scopeType, String userID) {
        this.scopeType = scopeType;
        this.userID = userID;
    }

    public ScopeType getScopeType() {
        return this.scopeType;
    }

    public final String getUserID() {
        return this.userID;
    }

    public abstract String getSpaceID();

    /**
     * Indicates if the specified scope is the same is this scope.
     * @param scope the scope to compare
     * @return true if the specified scope is not null and it's user ID
     * and space ID are equal to this scope's user ID and space ID
     */
    public boolean isSameScope(IPersonPropertyScope scope) {
        if (scope == null) {
            return false;
        }

        return ((getUserID().equals(scope.getUserID())) &&
                (getSpaceID().equals(scope.getSpaceID())));
    }

    public String formatRequestParameters() {
        return "scopeTypeID=" + scopeType + "&userID=" + getUserID() + "&spaceID=" + getSpaceID();
    }
}
