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
package net.project.billing.bill.recurrence;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Provides the collection of available recurrences.
 */
public class FrequencyCollection
        extends ArrayList
        implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * Returns the loaded collection of frequencys.
     * @return the loaded frequencys
     */
    public static FrequencyCollection getAll() {
        FrequencyCollection frequencyCollection = new FrequencyCollection();
        Frequency frequency = null;

        frequency = new Frequency();
        frequency.setID(FrequencyID.ONCE);
        frequencyCollection.add(frequency);
        
        frequency = new Frequency();
        frequency.setID(FrequencyID.MONTHLY);
        frequencyCollection.add(frequency);

        frequency = new Frequency();
        frequency.setID(FrequencyID.ANNUAL);
        frequencyCollection.add(frequency);
        
        return frequencyCollection;
    }

    //
    // Instance members
    //

    /**
     * Creates a new, empty frequencys list
     */
    private FrequencyCollection() {
        super();
    }


    /**
     * Returns the frequency for the given id.
     * @param FrequencyID the id of the frequency to get
     * @return the frequency, or null if there is no frequency for
     * the specified id
     */
    public Frequency getFrequency(FrequencyID frequencyID) {
        Frequency frequency = null;
        boolean isFound = false;

        // Iteratate over the frequencyCollection in this collection
        // Until we find one with matching id
        for (Iterator it = iterator(); it.hasNext() & !isFound; ) {
            frequency = (Frequency) it.next();
            if (frequency.getID().equals(frequencyID)) {
                isFound = true;
            }
        }

        return frequency;
    }

}
