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

 package net.project.document;

import net.project.base.PnetException;
import net.project.security.User;

public interface IManagedObject {


    public String getID();

    public boolean isTypeOf (String objectType);

    public String getType();

    /** Check in this document */
     public void checkIn () throws PnetException;

   /** Check out this document */
    public void checkOut () throws PnetException;

    /** undo the check out on this document */
    public void undoCheckOut() throws PnetException;

    public boolean isLoaded();

    public User getUser();

    public boolean isCheckedOut();

    public String getCheckedOutBy();
    public String getCheckedOutByID();

}
