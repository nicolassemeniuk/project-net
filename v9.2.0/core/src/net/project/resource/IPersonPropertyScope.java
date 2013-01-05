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
 * Implementing classes allow Person Properties to be stored against a specific
 * space or globally across all spaces.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public interface IPersonPropertyScope {

    /**
     * Returns the type of this scope.
     * @return the scope type
     */
    ScopeType getScopeType();

    /**
     * Returns the ID used to identify the user scope.
     * @return the user scope ID
     */
    String getUserID();

    /**
     * Returns the ID used to identify the space scope.
     * This would be a space ID or some other identifier
     * if scope is not for a particular space.
     * @return the space scope ID
     */
    String getSpaceID();

    /**
     * Indicates whether the specified scope is the same as this one.
     * @param scope the scope to compare
     * @return true if the specified scope is the same; false otherwise
     */
    boolean isSameScope(IPersonPropertyScope scope);

    /**
     * Formats this scope's type and values as request parameters.
     * <p>
     * The resulting string should not start with <code>?</code> or <code>&amp;</code>.
     * @return the values as request parameters, for example <code>scopeType=space&amp;userID=123&amp;spaceID=456</code>.
     */
    String formatRequestParameters();

}
