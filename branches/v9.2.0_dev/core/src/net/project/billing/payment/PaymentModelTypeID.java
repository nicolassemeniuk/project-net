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
package net.project.billing.payment;

import java.util.ArrayList;
import java.util.Iterator;

public class PaymentModelTypeID implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * Maintains the finite list of ids.
     */
    private static ArrayList typeIDs = new ArrayList();
    

    /**
     * Returns the PaymentModelTypeID object for the specified string id.
     * @param id for which to get the matching PaymentModelTypeID
     * @return the object with the specified internal id or null if
     * there one is not found
     */
    public static PaymentModelTypeID forID(String id) {
        PaymentModelTypeID groupTypeID = null;
        boolean isFound = false;
        
        // Loop over all the items, finding the one with matching internal id
        Iterator it = PaymentModelTypeID.typeIDs.iterator();
        while (it.hasNext() & !isFound) {
            groupTypeID = (PaymentModelTypeID) it.next();
            if (groupTypeID.getID().equals(id)) {
                isFound = true;                
            }
        }

        return groupTypeID;
    }


    //
    // Instance members
    //

    /** The internal id of a PaymentModelTypeID. */
    private String id = null;


    private PaymentModelTypeID(String id) {
        this.id = id;
        // Add this to the collection of all typeIDs
        PaymentModelTypeID.typeIDs.add(this);
    }

    /**
     * Returns the value of this PaymentModelTypeID.
     * @return this object's ID
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the value of this PaymentModelTypeID.
     * @return this object's ID
     */
    public String toString() {
        return getID();
    }
    
    public boolean equals(Object obj) {
        if (obj != null &&
            obj instanceof PaymentModelTypeID &&
            ((PaymentModelTypeID) obj).getID().equals(this.getID())) {
            
            return true;
        }
        
        return false;
    }

    public int hashCode() {
        // Returns the ID's hashcode, since two of these objects are equal
        // if their IDs equal
        return getID().hashCode();
    }

    //
    // Static Constants located at end of class to ensure all other
    // static initializations occur before this
    //

    /** Charge Code payment model type id, currently <code>100</code>. */
    public static PaymentModelTypeID CHARGE_CODE = new PaymentModelTypeID("100");
    
    /** Credit card payment model type id, currently <code>200</code>. */
    public static PaymentModelTypeID CREDIT_CARD = new PaymentModelTypeID("200");

    /** Trial payment model type id, currently <code>300</code>. */
    public static PaymentModelTypeID TRIAL = new PaymentModelTypeID("300");

}
