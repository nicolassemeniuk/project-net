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
package net.project.billing.bill;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Enumeration of <code>BillStatusID</code>s.
 * @author tim
 */
public class BillStatusID {

    //
    // Static members
    //

    /**
     * Maintains the finite list of <code>BillStatusID</code>s.
     */
    private static List list = new ArrayList();
    

    /**
     * Returns the <code>BillStatusID</code> for the specified string id.
     * @param id for which to get the matching <code>BillStatusID</code>
     * @return the object with the specified internal id or null if
     * there one is not found
     */
    public static BillStatusID forID(String id) {
        BillStatusID nextItem = null;
        boolean isFound = false;
        
        // Loop over all the items, finding the one with matching internal id
        for (Iterator it = BillStatusID.list.iterator(); it.hasNext() & !isFound; ) {
            nextItem = (BillStatusID) it.next();
            if (nextItem.getID().equals(id)) {
                isFound = true;                
            }
        }

        return nextItem;
    }


    //
    // Instance members
    //
    
    /** The internal id of this <code>BillStatusID</code>. */
    private String id = null;
    
    /**
     * Creates a new <code>BillStatusID</code>.
     * @param id the internal id
     */
    private BillStatusID(String id) {
        this.id = id;
        BillStatusID.list.add(this);
    }

    /**
     * Returns the internal id of this <code>BillStatusID</code>.
     */
    public String getID() {
        return this.id;
    }
    
    public boolean equals(Object obj) {
        if (obj != null &&
        obj instanceof BillStatusID &&
        ((BillStatusID) obj).getID().equals(this.getID())) {
            
            return true;
        }
        
        return false;
    }

    //
    // Static Constants located at end of class to ensure all other
    // static initializations occur before this
    //
    
    /** Unrecorded bill, which means it has not been entered in the ledger, currently <code>100</code>. */
    public static final BillStatusID UNRECORDED = new BillStatusID("100");
    
    /** Recorded bill, which means this bill has a ledger entry, currently <code>200</code>. */
    public static final BillStatusID RECORDED = new BillStatusID("200");

    // May not need this, since we are not going to delete any of the bills
    /** Deleted bill, currently <code>300</code>. */
    public static final BillStatusID DELETED = new BillStatusID("300");
    
}
