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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.document;


import net.project.security.User;

public interface IContainerObject {

    // returns the PnObjectID for the object
    public String getID();
    public void setID(String objectID);

    // returns a String with the name of the object
    public String getName();
    public void setName(String name);

    // returns a String with the type of the object
    public String getType();
    public void setType(String objectType);

    public boolean isTypeOf (String objectType);

    // returns a String with the containerID of the object
    public String getContainerID();
    public void setContainerID(String containerID);

    public String getDescription();

    // returns a String with container name
    public String getContainerName();

    // returns a String with XML
    public String getXML();

    public String getAppletXML(String spaceName);

    public String getXMLProperties();

    public String getXMLBody();

    public void loadProperties() throws net.project.persistence.PersistenceException;

    public boolean isLoaded();

    // set user for "identified" operations
    public void setUser (User user);

    public String getURL();


    // persist this object
    public void store() throws net.project.persistence.PersistenceException;

    // restore this object from the persistent store
    public void load() throws net.project.persistence.PersistenceException;

    // remove this object
    public void remove() throws net.project.persistence.PersistenceException;



}
