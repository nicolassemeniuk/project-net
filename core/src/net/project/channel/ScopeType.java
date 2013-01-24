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

 package net.project.channel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.project.resource.IPersonPropertyScope;
import net.project.resource.PersonPropertyGlobalScope;
import net.project.resource.PersonPropertySpaceScope;
import net.project.security.User;

/**
 * An enumeration of Scopes for storing channel settings.
 * 
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class ScopeType implements Serializable {

    //
    // Static members
    //
        
    /**
     * Provides a map where each key is a <code>String</code> ID and
     * each value is a <code>ScopeType</code>.
     * The map contains all SettingsScopes.
     */
    private static final Map ALL_MAP = new HashMap();

    /** Global settings scope; settings stored for current user indepednent of space. */
    public static final ScopeType GLOBAL = new ScopeType("global", new GlobalScopeMaker());

    /** Space scope; settings stored for current user in current space. */
    public static final ScopeType SPACE = new ScopeType("space", new SpaceScopeMaker());

    /**
     * Returns the ScopeType with the specified id.
     * 
     * @param id the id of the ScopeType to get
     * @return the ScopeType with matching id
     * @throws IllegalArgumentException if there is no ScopeType
     * with that id
     */
    public static ScopeType getForID(String id) {
        ScopeType foundSettingsScope = (ScopeType) ALL_MAP.get(id);
        if (foundSettingsScope == null) {
            throw new IllegalArgumentException("Unable to find ScopeType for id " + id);
        }
        return foundSettingsScope;
    }

    /**
     * Constructs a scope from the parameters in the specified request.
     * <p>
     * The request parameters should have been constructed by <code>IPersonPropertyScope.formatRequestParameters</code>
     * </p>
     * @param request the request containing the parameters for making the scope
     * @return the scope
     * @throws NullPointerException if any request parameters are missing
     */
    public static IPersonPropertyScope makeScopeFromRequest(HttpServletRequest request) {
        String scopeTypeID = request.getParameter("scopeTypeID");

        if (scopeTypeID == null) {
            throw new NullPointerException("Missing request parameter scopeTypeID");
        }

        return ScopeType.getForID(scopeTypeID).makeScope(request);
    }

    //
    // Instance Members
    //
    
    /** The unique ID. */
    private final String id;

    /**
     * The implementing class that constructs an <code>IPersonPropertyScope</code>
     * based on this scope type.
     */
    private final IScopeMaker scopeMaker;

    /**
     * Creates a ScopeType with the specified id.
     * 
     * @param id the id of the ScopeType to create
     * @param scopeMaker
     * @throws IllegalArgumentException if there is already a
     * ScopeType with that id
     */
    private ScopeType(String id, IScopeMaker scopeMaker) {
        this.id = id;
        this.scopeMaker = scopeMaker;
        if (ALL_MAP.containsKey(id)) {
            throw new IllegalArgumentException("Attempted to create another ScopeType with id " + id);
        }
        ALL_MAP.put(id, this);
    }

    /**
     * Creates a scope of the appropriate type based on the specified
     * user.
     * @param user the user for whom to construct the scope
     * @return the scope for that user
     */
    public IPersonPropertyScope makeScope(User user) {
        return scopeMaker.makeScope(user);
    }

    /**
     * Creates a scope of the appropriate type from the request.
     * @param request the request containing the parameters required to make
     * a scope of this type
     * @return the scope
     */
    private IPersonPropertyScope makeScope(HttpServletRequest request) {
        return scopeMaker.makeScope(request);
    }

    /**
     * Returns a string representation of this ScopeType,
     * suitable for debugging.
     * 
     * @return the string representation
     */
    public String toString() {
        return id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScopeType)) {
            return false;
        }

        final ScopeType settingsScope = (ScopeType) o;

        if (!id.equals(settingsScope.id)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return id.hashCode();
    }

    //
    // Nested top-level classes
    //

    /**
     * Implementing classes can construct <code>IPersonPropertyScope</code>
     * objects of the appropriate type.
     */
    private interface IScopeMaker {
        /**
         * Makes a scope for the specified user.
         * @param user the user for whom to make the scope
         * @return the scope
         */
        IPersonPropertyScope makeScope(User user);

        /**
         * Makes a scope from the specified request.
         * @param request the request containing parameters required for
         * creating the scope
         * @return the scope
         */
        IPersonPropertyScope makeScope(HttpServletRequest request);
    }

    /**
     * Makes a Space scope.
     */
    private static class SpaceScopeMaker implements IScopeMaker, Serializable {
        public IPersonPropertyScope makeScope(User user) {
            return new PersonPropertySpaceScope(user, user.getCurrentSpace());
        }

        public IPersonPropertyScope makeScope(HttpServletRequest request) {
            String userID = request.getParameter("userID");
            String spaceID = request.getParameter("spaceID");

            if (userID == null) {
                throw new NullPointerException("Missing request parameter userID");
            }

            if (spaceID == null) {
                throw new NullPointerException("Missing request parameter spaceID");
            }

            return new PersonPropertySpaceScope(userID, spaceID);
        }
    }

    /**
     * Makes a Global scope.
     */
    private static class GlobalScopeMaker implements IScopeMaker {
        public IPersonPropertyScope makeScope(User user) {
            return new PersonPropertyGlobalScope(user);
        }

        public IPersonPropertyScope makeScope(HttpServletRequest request) {
            String userID = request.getParameter("userID");
            return new PersonPropertyGlobalScope(userID);
        }
    }

}
