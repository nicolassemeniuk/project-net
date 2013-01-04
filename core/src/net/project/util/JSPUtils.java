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
|    $RCSfile$
|   $Revision: 20173 $
|       $Date: 2009-12-08 13:36:27 -0300 (mar, 08 dic 2009) $
|     $Author: avinash $
|
|
+----------------------------------------------------------------------*/
package net.project.util;

import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.space.SpaceTypes;

/**
 * Common snippets of code extracted from JSP.
 *
 * @author Matthew Flower
 * @since ProductionLink (Gecko Update 3)
 */
public class JSPUtils {
    /**
     * Determine if the string passed into this method is null or blank.
     *
     * @param parameter a <code>String</code> value to check as being null or
     * blank.
     * @return true if the parameter is null or blank.  False if it is not.
     */
    public static boolean isEmpty(String parameter) {
        return ((parameter == null) || (parameter.equals("")));
    }

    public static boolean isEqual(String parameter, String comparison) {
        boolean isEqual = false;

        if (!isEmpty(parameter)) {
            isEqual =  parameter.equalsIgnoreCase(comparison);
        }

        return isEqual;
    }
    
    /**
     * Get modified current space type for image icons.
     * 
     * @param currentSpace
     * @return currentSpace
     */
    public static String getModifiedCurrentSpaceType(Space currentSpace) {
		Space space = SessionManager.getUser().getCurrentSpace();
		if (currentSpace.isTypeOf(SpaceTypes.PERSONAL_SPACE)) {
			return "personal";
		} else if (currentSpace.isTypeOf(SpaceTypes.CONFIGURATION_SPACE)) {
			return SpaceTypes.APPLICATION_SPACE;
		} else if (currentSpace.isTypeOf(SpaceTypes.METHODOLOGY_SPACE)) {
			return SpaceTypes.PROJECT_SPACE;
		} else {
			return space.getType();
		}
	}
    
    public static String removeNull(String parameter) {
       return (parameter == null ? "" : parameter);
   }
    
}
