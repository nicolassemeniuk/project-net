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
|    $Revision: 19579 $
|        $Date: 2009-07-27 06:45:23 -0300 (lun, 27 jul 2009) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.compatibility;

import javax.servlet.http.HttpSession;

/**
 * Provides a mechanism for accessing the session.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public interface ISessionProvider {

    /**
     * Puts an attribute in the session.
     * @param name the name of the attribute
     * @param value the value
     */
    void setAttribute(String name, Object value);

    /**
     * Gets the named attribute from the session.
     * @param name the name of the attribute to get
     * @return the attribute or null if none was found with that name
     */
    Object getAttribute(String name);

    void setLocalSession(Object session);

}
