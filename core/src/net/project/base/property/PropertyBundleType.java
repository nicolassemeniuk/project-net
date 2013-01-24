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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.PnetRuntimeException;
import net.project.base.compatibility.ICompatibilityProvider;

public class PropertyBundleType {
    private static final List ALL_TYPES = new ArrayList();

    // NOTE: The order of these is important; it defines
    // the order in which an implementation is searched for
    static {
        new PropertyBundleType("test", "net.project.base.property.TestPropertyBundle", true);
        new PropertyBundleType("normal", "net.project.base.property.PropertyBundle", true);
    }

    /**
     * Finds the PropertyBundle by locating the first type who's
     * <code>PropertyBundle</code> implementation class is present.
     * <p>
     * Each type is checked in order.
     * </p>
     * @return a PropertyBundleType
     * @throws PnetRuntimeException if no implementation class can be found
     */
    static PropertyBundleType findType() {

        PropertyBundleType foundType = null;

        for (Iterator iterator = ALL_TYPES.iterator(); iterator.hasNext() && (foundType == null);) {
            PropertyBundleType nextCompatibilityType = (PropertyBundleType) iterator.next();

            try {
                Class.forName(nextCompatibilityType.propertyBundleClassName);
                // If we reach here then class is found
                // new property bundle per session, instead of reusing one bundle for new sessions.
                foundType = new PropertyBundleType(nextCompatibilityType.name, nextCompatibilityType.propertyBundleClassName, false);

            } catch (ClassNotFoundException e) {
                // Silently continue with next class
            }

        }

        if (foundType == null) {
            throw new PnetRuntimeException("No PropertyBundle implementations found.");
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
    private final String propertyBundleClassName;

    /**
     * The lazily instantiated implementation.
     */
    private PropertyBundle propertyBundle;

    /**
     * Creates a new CompatibilityType with the specified properties.
     * <p>
     * The compatibility provider class is not instantiated until first
     * request due to the fact that this class is instantiated statically
     * but the provider class will be available only at runtime.
     * </p>
     * @param name the unique name of the type
     * @param propertyBundleClassName the name of the class that implements
     * <code>net.project.base.compatibility.ICompatibilityProvider</code>
     */
    private PropertyBundleType(String name, String propertyBundleClassName, boolean cache) {
        this.name = name;
        this.propertyBundleClassName = propertyBundleClassName;
        if (cache)
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
            throw new PnetRuntimeException("Unable to locate container PropertyBundle class: " + e, e);
        } catch (InstantiationException e) {
            throw new PnetRuntimeException("Unable to instantiate container PropertyBundle class: " + e, e);
        } catch (IllegalAccessException e) {
            throw new PnetRuntimeException("Unable to invoke default constructor on container PropertyBundle class: " + e, e);
        }

        return object;
    }

    /**
     * Returns an instance of this type's concrete implementation
     * of <code>PropertyBundle</code>.
     * The same instance is returned every time; it is instantiated only once per type.
     * @return a PropertyBundle implementation
     * @throws net.project.base.PnetRuntimeException if there is a problem instantiating
     */ 
    PropertyBundle getPropertyBundle() {
        if (this.propertyBundle == null) {
            this.propertyBundle = (PropertyBundle) newInstance(this.propertyBundleClassName);
        }
        return this.propertyBundle;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PropertyBundleType)) {
            return false;
        }

        final PropertyBundleType compatibilityType = (PropertyBundleType) o;

        if (!name.equals(compatibilityType.name)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return "name=" + this.name + ", propertyBundleClassName=" + this.propertyBundleClassName;
    }

}
