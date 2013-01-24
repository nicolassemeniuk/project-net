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

 
package net.project.security;

import net.project.base.property.PropertyProvider;

public class EventCodes {

    public static final String CREATE_ROLE = "role_create";
    public static final String MODIFY_ROLE = "role_modify";
    public static final String REMOVE_ROLE = "role_remove";
    
    public static String getName (String eventCode) {

	String name = null;
        if (eventCode.equals(CREATE_ROLE))
	    name = PropertyProvider.get("prm.security.eventcodes.createrole.name");
	else if (eventCode.equals(MODIFY_ROLE))
	    name = PropertyProvider.get("prm.security.eventcodes.modifyrole.name");
	else if (eventCode.equals(REMOVE_ROLE))
	    name = PropertyProvider.get("prm.security.eventcodes.removerole.name");
        return name;
    }

} // end class EventCodes
