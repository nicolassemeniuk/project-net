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

import org.apache.log4j.Logger;

/**
 * The License Cost is a cost associated with a license.
 */
public abstract class LicenseCost implements ILicenseCost {

    //
    // Static Members
    //

    /**
     * Creates a license cost from the specified license cost element.
     * The license cost is fully populated from the element.
     * @param licenseCostElement the xml element from which to create the
     * license cost
     * @return the license cost of the appropriate type for the licenseCostElement
     * @throws LicenseException if there is a problem creating the license cost;
     * for example, the element has no name or the cost class cannot be instantiated
     */
    public static LicenseCost create(org.jdom.Element licenseCostElement) 
            throws LicenseException {
        
        String elementName = licenseCostElement.getName();
        if (elementName == null) {
            throw new LicenseException("Error constructing license cost; no element name");
        }

        // get the license cost
        LicenseCostType costType = LicenseCostTypes.getAll().getLicenseCostTypeForElementName(elementName);
        LicenseCost cost = newLicenseCost(costType);
        cost.populate(licenseCostElement);

        return cost;
    }

    /**
     * Constructs a new LicenseCost object of the appropriate class for the
     * specified cost type.
     * @param costType the type of license cost for which to
     * construct the appopriate license cost class
     * @return a new, empty license cost of the appropriate class for
     * the cost type
     * @throws LicenseException if there is a problem constructing the
     * license cost for the cost type; for example, the class could
     * not be found or instantiated
     */
    private static LicenseCost newLicenseCost(LicenseCostType costType) 
                throws LicenseException {

        LicenseCost licenseCost = null;

        // Create an instance for the appropriate cost type
        try {
            Class costClass = Class.forName(costType.getClassName());
            licenseCost = (LicenseCost) costClass.newInstance();
        
        } catch (ClassNotFoundException cnfe) {
        	Logger.getLogger(LicenseCost.class).error("LicenseCost.newLicenseCost threw a ClassNotFoundException when trying to create class " + 
                    costType.getClassName() + ": " + cnfe);
            throw new LicenseException("Unable to create a LicenseCost: " + cnfe, cnfe);

        } catch (InstantiationException ie) {
        	Logger.getLogger(LicenseCost.class).error("LicenseCost.newLicenseCost threw a InstantiationException when trying to create class " + 
                    costType.getClassName() + ": " + ie);
            throw new LicenseException("Unable to create a LicenseCost: " + ie, ie);
        
        } catch (IllegalAccessException iae) {
        	Logger.getLogger(LicenseCost.class).error("LicenseCost.newLicenseCost trhew a IllegalAccessException when trying to create class " + 
                    costType.getClassName() + ": " + iae);
            throw new LicenseException("Unable to create a LicenseCost: " + iae, iae);
        
        }

        return licenseCost;
    }


    //
    // Instance members
    //

    /**
     * Creates an empty LicenseCost.
     */
    LicenseCost() {
        // Do nothing
    }

}
