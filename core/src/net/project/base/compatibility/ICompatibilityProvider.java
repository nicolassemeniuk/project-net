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
 * Allows for various runtime implementations of interfaces.
 * <p>
 * Implementations of this class should never be referred to directly; only through reflection.
 * </p>
 * @author Tim Morrow
 * @since Version 7.7
 */
public interface ICompatibilityProvider {

    /**
     * Returns a new instance of the appropriate concrete implementation.
     * @return a new instance
     */
    ISessionProvider getSessionProvider();

    /**
     * Returns a new instance of the appropriate concrete implementation.
     * @return a new instance
     */
    IResourceProvider getResourceProvider();

    /**
     * Returns a new instance of the appropriate concrete implementation.
     * @return a new instance
     */
    IXSLProvider getXSLProvider();

    /**
     * Returns a new instance of the appropriate concrete implementation.
     * @return a new instance
     */
    IConfigurationProvider getConfigurationProvider();

    /**
     * Returns a new instance of the appropriate concrete implementation.
     * @return a new instance
     */
    IConnectionProvider getConnectionProvider();

    /**
     * Returns a new instance of the appropriate concrete implementation.
     * @return a new instance
     */
    IClobProvider getClobProvider();

    /**
     * Returns a new instance of the appropriate concrete implementation.
     * @return a new instance
     */
    IMailSessionProvider getMailSessionProvider();

}
