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

import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

/**
 * A portfolio of generic Spaces.
 */
public class GenericPortfolio
        extends Portfolio
        implements IXMLPersistence {

    /**
     * Creates a GenericPortfolio.
     */
    public GenericPortfolio() {
        super();
        setContentType(net.project.space.ISpaceTypes.GENERIC_SPACE);
    }

    /**
     * Not supported by <code>GenericPortfolio</code>.
     * @throws PersistenceException never
     * @throws UnsupportedOperationException always
     */
    public void load() throws PersistenceException {
        throw new UnsupportedOperationException("Generic Portfolio load operation not supported");
    }


    /**
     * @return a complete XML document representation of this object.
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }


    /**
     * @return an XML node representation of this object.
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<GenericPortfolio>\n");
        xml.append("<portfolioID>" + getID() + "</portfolioID>\n");
        xml.append("<parentSpaceID>" + getParentSpaceID() + "</parentSpaceID>\n");
        xml.append("<name>" + XMLUtils.escape(getName()) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(getDescription()) + "</description>\n");
        xml.append("<type>" + XMLUtils.escape(getType()) + "</type>\n");
        xml.append("<contentType>" + XMLUtils.escape(getContentType()) + "</contentType>\n");

        for (int i = 0; i < this.size(); i++)
            xml.append(((IPortfolioEntry) this.get(i)).getXMLBody());

        xml.append("</GenericPortfolio>\n");
        return xml.toString();
    }

}
