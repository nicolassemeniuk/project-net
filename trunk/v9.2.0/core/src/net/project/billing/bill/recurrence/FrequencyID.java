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
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Enumeration of <code>FrequencyID</code>s.
 * @author tim
 */
public class FrequencyID {

    //
    // Static members
    //

    /**
     * Maintains the finite list of <code>FrequencyID</code>s.
     */
    private static List list = new ArrayList();
    

    /**
     * Returns the <code>FrequencyID</code> for the specified string id.
     * @param id for which to get the matching <code>FrequencyID</code>
     * @return the object with the specified internal id or null if
     * there one is not found
     */
    public static FrequencyID forID(String id) {
        FrequencyID nextItem = null;
        boolean isFound = false;
        
        // Loop over all the items, finding the one with matching internal id
        for (Iterator it = FrequencyID.list.iterator(); it.hasNext() & !isFound; ) {
            nextItem = (FrequencyID) it.next();
            if (nextItem.getID().equals(id)) {
                isFound = true;                
            }
        }

        return nextItem;
    }


    //
    // Instance members
    //
    
    /** The internal id of this <code>FrequencyID</code>. */
    private String id = null;

    /**
     * The calendar unit value for this frequency id.
     */
    private CalendarUnit calendarUnit = null;
    
    /**
     * Creates a new <code>FrequencyID</code>.
     * @param id the internal id
     * @param calendarUnit the value for a single unit addition to a calendar
     */
    private FrequencyID(String id, CalendarUnit calendarUnit) {
        this.id = id;
        FrequencyID.list.add(this);
        this.calendarUnit = calendarUnit;
    }

    /**
     * Returns the internal id of this <code>FrequencyID</code>.
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns a composition of the calendar field and amount that should
     * be added to a calendar to increment it by one unit for this frequency.
     */
    private CalendarUnit getCalendarUnit() {
        return this.calendarUnit;
    }
    
    public boolean equals(Object obj) {
        if (obj != null &&
        obj instanceof FrequencyID &&
        ((FrequencyID) obj).getID().equals(this.getID())) {
            
            return true;
        }
        
        return false;
    }

    
    /**
     * Increments calendar by one period based on this frequency.
     * Modifies the calendar.
     */
    public void increment(Calendar calendar) {
        calendar.add(getCalendarUnit().getField(), getCalendarUnit().getAmount());
    }
    
    //
    // Static Constants located at end of class to ensure all other
    // static initializations occur before this
    //
    
    /** Single event, currently <code>100</code>. */
    public static final FrequencyID ONCE = new FrequencyID("100", new CalendarUnit(Calendar.MILLISECOND,0));

// Commented items not supported yet    
//    /** Daily Frequency, currently <code>200</code>. */
//    public static final FrequencyID DAILY = new FrequencyID("200");
    
//    /** Weekly Frequency, currently <code>300</code>. */
//    public static final FrequencyID WEEKLY = new FrequencyID("300");

//    /** Bi-weekly Frequency (every 2 weeks), currently <code>400</code>. */
//    public static final FrequencyID BIWEEKLY = new FrequencyID("400");
    
//    /** Twice per month frequency, currently <code>500</code>. */
//    public static final FrequencyID TWICE_MONTHLY = new FrequencyID("500");

    /** Monthly Frequency, currently <code>600</code>. */
    public static final FrequencyID MONTHLY = new FrequencyID("600", new CalendarUnit(Calendar.MONTH,1));

    /** Annual Frequency, currently <code>700</code>. */
    public static final FrequencyID ANNUAL = new FrequencyID("700", new CalendarUnit(Calendar.YEAR,1));
    

    /**
     * Provides a composition of calendar field constant and amount that
     * may be added / substracted to the calendar to increment or decrement
     * a calendar by one unit.
     */
    private static class CalendarUnit {
        private int field = 0;
        private int amount = 0;
        
        CalendarUnit(int field, int amount) {
            this.field = field;
            this.amount = amount;
        }
        
        int getField() {
            return this.field;
        }
        
        int getAmount() {
            return this.amount;
        }
    
    }
    
}
