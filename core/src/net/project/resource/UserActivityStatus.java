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

 /*--------------------------------------------------------------------------------------+
|
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.resource;

import java.io.Serializable;

import net.project.base.property.PropertyProvider;

/**
 *  This class defines the activities of an User 
 */

public class UserActivityStatus implements Serializable {
    
    public static final UserActivityStatus LOGGED_OUT = new UserActivityStatus(100 ,"@prm.directory.resource.useractivitystatus.loggedout.name") ; 

    public static final UserActivityStatus LOGGED_IN = new UserActivityStatus(200 ,"@prm.directory.resource.useractivitystatus.loggedin.name") ; 
    
    public static final UserActivityStatus ACTIVE = new UserActivityStatus(300 ,"@prm.directory.resource.useractivitystatus.active.name") ; 

    public static final UserActivityStatus INACTIVE = new UserActivityStatus(400 ,"@prm.directory.resource.useractivitystatus.inactive.name") ; 

    private int id = 0;

    private String displayName = null;

    private UserActivityStatus status = null;

    public UserActivityStatus() {
        //constructs an empty object
    }

    public UserActivityStatus(int id , String displayName) {
        this.id = id ;
        this.displayName = displayName ;

        // construct object . . . 
    } 

    public String getDisplayName() {

        if ( this.status == null) { 
            return PropertyProvider.get(LOGGED_OUT.displayName) ;
        } else {
            return PropertyProvider.get(this.status.displayName);
        }
        
    }

    public UserActivityStatus getStatus () { 
        if ( this.status == null) { 
            return UserActivityStatus.LOGGED_OUT ;
        } else {
            return this.status;
        }
    } 

    public void setStatus(UserActivityStatus status) { 
        this.status = status ;
    }
    
    public boolean equals(UserActivityStatus userActivityStatus)
    {
    	return this.getDisplayName().equals(userActivityStatus.getDisplayName());
    }
}

