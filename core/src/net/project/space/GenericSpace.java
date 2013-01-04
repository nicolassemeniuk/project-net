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

 /*---------------------------------------------------------------------------------------------------------------------------------------------------+
|                                                                       
|    $RCSfile$
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|                                       
+-----------------------------------------------------------------------------------------------------------------------------------------------------*/
package net.project.space;

import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

/**
 * A generic, user-defined workspace.
 */
public class GenericSpace extends Space {
    /**
     * Construct an empty GenericSpace.
     */
    public GenericSpace() {
        super();
        setType(ISpaceTypes.GENERIC_SPACE);
        this.spaceType = SpaceTypes.GENERIC;
    }

    /**
     * Construct a GenericSpace with database id set.
     */
    public GenericSpace(String id) {
        super(id);
        setType(ISpaceTypes.GENERIC_SPACE);
        this.spaceType = SpaceTypes.GENERIC;
    }

    /**
     * Load the space.
     */
    public void load() throws PersistenceException {
        throw new PersistenceException("not implemented");
    }


    /**
     * Load the space.
     */
    public void store() throws PersistenceException {
        throw new PersistenceException("not implemented");
    }


    /**
     * Removes the space from the database.
     *
     * @throws PersistenceException Thrown to indicate a failure storing to the
     * database, a system-level error.
     */
    public void remove() throws PersistenceException {
        throw new PersistenceException("not implemented");
    }

    /**
     * Converts the object to XML representation This method returns the object
     * as XML text.
     *
     * @return XML representation of this object
     */
    public String getXML() {
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" ?>\n\n");
        xml.append(getXMLBody());
        return xml.toString();
    }


    /**
     * Get an XML representation of the Space without xml tag.
     *
     * @return an XML representation of this Space without the xml tag.
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<GenericSpace>\n");
        xml.append("<id>" + this.getID() + "</id>\n");
        xml.append("<name>" + XMLUtils.escape(this.getName()) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(this.getDescription()) + "</description>\n");
        xml.append("<type>" + XMLUtils.escape(this.getType()) + "</type>\n");
        xml.append("<userDefinedSubType>" + XMLUtils.escape(this.getUserDefinedSubtype()) + "</userDefinedSubType>\n");
        xml.append("<flavor>" + XMLUtils.escape(this.getFlavor()) + "</flavor>\n");
        xml.append("</GenericSpace>\n");
        return xml.toString();
    }
}
