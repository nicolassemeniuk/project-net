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
|    $Revision: 18948 $
|        $Date: 2009-02-21 09:39:24 -0200 (sáb, 21 feb 2009) $
|      $Author: ritesh $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.compatibility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.PnetRuntimeException;

/**
 * Provides a mechanism for getting an implementation of <code>ICompatibilityProvider</code>.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
class CompatibilityType {

    private static final List ALL_TYPES = new ArrayList();

    // NOTE: The order of these is important; it defines
    // the order in which an implementation is searched for
    static {
        new CompatibilityType("test", "net.project.base.compatibility.test.TestCompatabilityProviderImpl");
        // removed weblogic compatibility provider         
        // new CompatibilityType("weblogic", "net.project.base.compatibility.weblogic.WeblogicCompatibilityProviderImpl");
        new CompatibilityType("modern", "net.project.base.compatibility.modern.ModernCompatibilityProviderImpl");
    }

    /**
     * Finds the CompatibilityType by locating the first type who's
     * <code>ICompatibilityProvider</code> implementation class is present.
     * <p>
     * Each type is checked in order.
     * </p>
     * @return a CompatibilityType
     * @throws PnetRuntimeException if no implementation class can be found
     */
    static CompatibilityType findType() {

        CompatibilityType foundType = null;

        for (Iterator iterator = ALL_TYPES.iterator(); iterator.hasNext() && (foundType == null);) {
            CompatibilityType nextCompatibilityType = (CompatibilityType) iterator.next();

            try {
                Class.forName(nextCompatibilityType.compatibilityProviderClassName);
                // If we reach here then class is found
                foundType = nextCompatibilityType;

            } catch (ClassNotFoundException e) {
                // Silently continue with next class
            }

        }

        if (foundType == null) {
            throw new PnetRuntimeException("No ICompatibilityProvider implementations found.");
        }

        return foundType;
    }

    //
    // Instance Methods
    //

    /**
     * The unique name of the type.
     */
    private final String name;

    /**
     * The name of the class providing the implementation.
     */
    private final String compatibilityProviderClassName;

    /**
     * The lazily instantiated implementation.
     */
    private ICompatibilityProvider compatibilityProvider;

    /**
     * Creates a new CompatibilityType with the specified properties.
     * <p>
     * The compatibility provider class is not instantiated until first
     * request due to the fact that this class is instantiated statically
     * but the provider class will be available only at runtime.
     * </p>
     * @param name the unique name of the type
     * @param compatibilityProviderClassName the name of the class that implements
     * <code>net.project.base.compatibility.ICompatibilityProvider</code>
     */
    private CompatibilityType(String name, String compatibilityProviderClassName) {
        this.name = name;
        this.compatibilityProviderClassName = compatibilityProviderClassName;
        ALL_TYPES.add(this);
    }

    /**
     * Creates a new instance of the class for the specified name.
     *
     * @param className the name of the class to instantiate
     * @return the instantiated class
     * @throws net.project.base.PnetRuntimeException if there is a problem instantiating
     */
    private Object newInstance(String className) {

        Object object;

        try {
            object = Class.forName(className).newInstance();

        } catch (ClassNotFoundException e) {
            throw new PnetRuntimeException("Unable to locate container compatibility class: " + e, e);
        } catch (InstantiationException e) {
            throw new PnetRuntimeException("Unable to instantiate container compatibility class: " + e, e);
        } catch (IllegalAccessException e) {
            throw new PnetRuntimeException("Unable to invoke default constructor on container compatibility class: " + e, e);
        }

        return object;
    }

    /**
     * Returns an instance of this type's concrete implementation
     * of <code>ICompatibilityProvider</code>.
     * The same instance is returned every time; it is instantiated only once per type.
     * @return a compatibility provider implementation
     * @throws net.project.base.PnetRuntimeException if there is a problem instantiating
     */ 
    ICompatibilityProvider getCompatibilityProvider() {
        if (this.compatibilityProvider == null) {
            this.compatibilityProvider = (ICompatibilityProvider) newInstance(this.compatibilityProviderClassName);
        }
        return this.compatibilityProvider;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompatibilityType)) {
            return false;
        }

        final CompatibilityType compatibilityType = (CompatibilityType) o;

        if (!name.equals(compatibilityType.name)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return "name=" + this.name + ", compatibilityProviderClassName=" + this.compatibilityProviderClassName;
    }
}
