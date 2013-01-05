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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;

/**
 * Loads baselines from the database.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class BaselineList implements IXMLPersistence {
    private List baselines = Collections.EMPTY_LIST;

    public List getBaselines() {
        return baselines;
    }

    public void loadBaselinesForObject(String objectID) throws PersistenceException {
        BaselineFinder bf = new BaselineFinder();
        baselines = bf.findForObjectID(objectID);
    }

    /**
     * Returns this object's XML representation, including the XML version tag.
     *
     * @return XML representation of this object
     * @see net.project.persistence.IXMLPersistence#getXMLBody
     * @see net.project.persistence.IXMLPersistence#XML_VERSION
     */
    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Returns this object's XML representation, without the XML version tag.
     *
     * @return XML representation of this object
     * @see net.project.persistence.IXMLPersistence#getXML
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<BaselineList>");

        for (Iterator iterator = baselines.iterator(); iterator.hasNext();) {
            Baseline baseline = (Baseline) iterator.next();
            xml.append(baseline.getXMLBody());
        }

        xml.append("</BaselineList>");

        return xml.toString();
    }
}
