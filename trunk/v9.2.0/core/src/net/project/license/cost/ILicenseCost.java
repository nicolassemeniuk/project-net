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
package net.project.license.cost;

import net.project.license.LicenseException;

public interface ILicenseCost {

    /**
     * Returns the license cost type id for this license cost.
     * @return the license cost type id
     */
    public LicenseCostTypeID getLicenseCostTypeID();
    
    /**
     * Returns the xml Element for this LicenseCost.
     * @return the element
     */
    public org.jdom.Element getXMLElement();

    /**
     * Populates this license cost from the xml element.
     * The element can be assumed to be of the correct type for the license cost.
     * @param element the xml element from which to populate this license cost
     * @throws LicenseException if there is a problem populating this license cost
     */
    public void populate(org.jdom.Element element) throws LicenseException;

    /**
     * Returns the XMLDocument representation of this LicenseCost.
     * @return the XMLDocument representation
     */
    public net.project.xml.document.XMLDocument getXMLDocument();

    /**
     * Calculates the total charge of this license cost for the specified license.
     * It is expected that the license is the same license to which this
     * cost belongs.  This allows a license cost's actual charge to be based on other
     * license costs or models within the license.
     * @param license the license for whom this license cost total cost is
     * being calculated
     * @return the license charge for this license cost
     */
    public LicenseCharge calculateLicenseCharge(net.project.license.License license);

}
