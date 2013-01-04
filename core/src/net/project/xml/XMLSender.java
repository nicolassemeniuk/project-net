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

 package net.project.xml;

import java.sql.SQLException;

import net.project.base.ObjectFactory;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;


/**
   Generates XML for the object represented by the database objectID.  
   The object represented by the objectID must support IJDBCPersistence and IXMLPersistence interfaces.
   @see IJDBCPersistence
   @see IXMLPersistence
   @author Roger Bly
*/
public class XMLSender
{
    ObjectFactory factory = new ObjectFactory();



    /**
        Get the XML representation of the object specified by the passed datababase object_id.
        This method will attempt to load the object from database persistence, then call it's getXML() method.
        The object represented by the objectID must support IJDBCPersistence and IXMLPersistence interfaces.
        @param objectID the database ID of the object to get XML representation of.
     * @throws SQLException 
        @see net.project.persistence.IJDBCPersistence
        @see net.project.persistence.IXMLPersistence
    */
    public String getXML(String objectID) throws SQLException 
    {
        Object object;

        try
        {
            // Make it
            if ((object = this.factory.make(objectID)) == null)
                return ("<?xml version=\"1.0\" ?>\n<error>object_type was not stored for this object or Project.net Object Factory doesn't support that object type</error>\n");

            // Load it
            if (Class.forName("net.project.persistence.IJDBCPersistence").isAssignableFrom(object.getClass()))
            {
                ((IJDBCPersistence)object).setID(objectID);
                ((IJDBCPersistence)object).load();
            }
            else
                return("<?xml version=\"1.0\" ?>\n<error>This Project.net object does not implement IJDBCPersistence</error>\n");

            // XML it
            if (Class.forName("net.project.persistence.IXMLPersistence").isAssignableFrom(object.getClass()))
                return((IXMLPersistence)object).getXML();
            else
                return("<?xml version=\"1.0\" ?>\n<error>This Project.net object does not implement IXMLPersistence</error>\n");
        }
        catch (ClassNotFoundException cnfe)
        {
            return("<?xml version=\"1.0\" ?>\n<error>Project.net Class not found for that object type</error>\n");
        }
        catch (PersistenceException pe)
        {
            return("<?xml version=\"1.0\" ?>\n<error>Object could not be loaded from Database Persistence. </error>\n");
        }
    }

}

