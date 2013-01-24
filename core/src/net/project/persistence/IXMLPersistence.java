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
package net.project.persistence;

import java.sql.SQLException;


/**
 * Implementing classes provide XML representations of themselves.
 *
 * @author Unascribed
 * @since Version 1
 */
public interface IXMLPersistence {

    /** The XML version tag, currently <code>"&lt;?xml version="1.0" ?>\n"</code>. */
    public static String XML_VERSION = "<?xml version=\"1.0\" ?>\n";

    /**
     * Returns this object's XML representation, including the XML version tag.
     * @return XML representation of this object
     * @throws SQLException 
     * @see net.project.persistence.IXMLPersistence#getXMLBody
     * @see net.project.persistence.IXMLPersistence#XML_VERSION
     */
    public String getXML() throws SQLException;


    /**
     * Returns this object's XML representation, without the XML version tag.
     * @return XML representation of this object
     * @throws SQLException 
     * @see net.project.persistence.IXMLPersistence#getXML
     */
    public String getXMLBody() throws SQLException;

}
