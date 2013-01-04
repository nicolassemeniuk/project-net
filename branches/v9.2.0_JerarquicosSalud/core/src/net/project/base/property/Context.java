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

 package net.project.base.property;

import java.io.Serializable;

/**
 * Defines a paring of a ContextID and  a Language.
 * This is the smallest unit for whcih a collection of tokens is managed.
 *
 * @author Vishwajeet
 * @since 7.4
 */
class Context implements Serializable {

    /**
     * This Context's ID.
     */
    private final String contextID;
    /**
     * This Context's Language.
     */
    private final String language;

    /**
     * Constructs a Context for a given ID and language.
     * @param contextID This context's ID.
     * @param language  This context's language.
     */
    public Context(String contextID, String language) {

        this.contextID = contextID;
        this.language = language;

    }

    /**
     * Gets the id of this Context.
     * @return 	   The id of this context
     */
    String getID() {
        return this.contextID;
    }

    /**
     * Gets the language of this context.
     * @return 		Language of this context
     */
    String getLanguage() {
        return this.language;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Context)) return false;

        final Context context = (Context) o;

        if (!contextID.equals(context.contextID)) return false;
        if (!language.equals(context.language)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = contextID.hashCode();
        result = 29 * result + language.hashCode();
        return result;
    }
}
