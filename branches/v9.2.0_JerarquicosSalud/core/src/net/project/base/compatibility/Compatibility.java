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


/**
 * Provides a mechanism for accessing implementations of functionality
 * that varies between J2EE containers.
 * <p>
 * This class is a factory that provides different implementations of various interfaces
 * to provide support for Bluestone and non-Bluestone application servers.
 * Each interface provides an abstracted view of functionality that differs between
 * Bluestone and other application servers.
 * </p>
 * <p>
 * The type of container is determined dynamically by finding the first implementation
 * class of known <code>ICCompatibilityProvider</code> implementations.
 * Therefore, only one implementation should be compiled into the application.
 * The compatibility type is determined once in the VM.
 * </p>
 * @author Tim Morrow
 * @since Version 7.7
 */
public final class Compatibility {

    /**
     * Determines the current compatibility type from the first implementor
     * if <code>ICompatibilityProvider</code> in the VM.
     * We use a type class to allow us to lazily instantiate the implementation.
     */
    private static final CompatibilityType type = CompatibilityType.findType();

    public static ISessionProvider getSessionProvider() {
        return type.getCompatibilityProvider().getSessionProvider();
    }

    public static IResourceProvider getResourceProvider() {
        return type.getCompatibilityProvider().getResourceProvider();
    }

    public static IXSLProvider getXSLProvider() {
        return type.getCompatibilityProvider().getXSLProvider();
    }

    public static IConfigurationProvider getConfigurationProvider() {
        return type.getCompatibilityProvider().getConfigurationProvider();
    }

    public static IConnectionProvider getConnectionProvider() {
        return type.getCompatibilityProvider().getConnectionProvider();
    }

    public static IClobProvider getClobProvider() {
        return type.getCompatibilityProvider().getClobProvider();
    }

    public static IMailSessionProvider getMailSessionProvider() {
        return type.getCompatibilityProvider().getMailSessionProvider();
    }

}
