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
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.compatibility;

/**
 * Provides access to configuration settings.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public interface IConfigurationProvider {

    /**
     * Returns the value of the setting for the specified name.
     * @param name the name of the setting to get
     * @return the value
     */
    String getSetting(String name);

    /**
     * Returns the default directory to which to place temporary
     * upload files.
     * <p>
     * This method exists because the value for Bluestone does not
     * come from the normal settings section.
     * </p>
     * @return the default upload temp directory
     */
    String getDefaultUploadTempDirectory();

    /**
     * Indicates whether this uses the modern-style configuration mechanism.
     * <p>
     * That is, does it use the default settings file with the database table for custom values.
     * This is necessary to decide whether to show the link in the administration space.
     * It is expected that this method disappears when Bluestone is eliminated (along with
     * all this other indirect "compatibility" stuff.
     * </p>
     * @return true if it uses the "modern" (i.e. non-Bluestone) settings stuff; false otherwise
     */
    boolean isModern();

}
