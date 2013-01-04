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
package net.project.portfolio;

/**
 * An interface that must be implemented by objects that are contained in a Portfolio.
 */
public interface IPortfolioEntry extends net.project.persistence.IXMLPersistence {

    /**
     * Gets the ID of the object wrapped by the portfolio entry.
     * @return the id
     */
    public String getID();

    /**
     * Gets the name of the object wrapped by the portfolio entry.
     * @return the name
     */
    public String getName();

    /**
     * Gets the parent space of this entry; typically the space of higer order that owns this entry.
     * @return the parent space id
     */
    public String getParentSpaceID();

    /**
     * Get the type of this entry.
     * @return the type
     * @see net.project.space.ISpaceTypes
     */
    public String getType();

    /**
     * Returns the XML format of this portfolio entry.
     * @return the XML format
     */
    public String getXMLBody();

    /**
     * Returns the XML format of this portfolio entry, including the version tag.
     * @return the XML format
     */
    public String getXML();

}
