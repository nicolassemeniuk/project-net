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

import java.util.Calendar;
import java.util.Date;

import net.project.calendar.PnCalendar;

/**
 * Recurrence of a bill describes when it is nect due and the frequency of
 * the recurrence.
 * @author tim
 */
public class Recurrence {

    /** The next date on which this Recurrence is due. */
    private Date nextDueDate = null;
    
    /** The frequency id of this recurrence. */
    private FrequencyID frequencyID = null;
    
    /**
     * Creates an empty Recurrence.
     */
    public Recurrence() {
        // Do nothing
    }

    /**
     * Sets when this Recurrence is next due.
     * @param date when the recurrence is next due
     */
    public void setNextDueDate(Date date) {
        this.nextDueDate = date;
    }

    /**
     * Indicates when this Recurrence is next due.
     * @return the next due date
     */
    public Date getNextDueDate() {
        return this.nextDueDate;
    }
    
    /**
     * Sets this recurrence's frequency.
     * @param frequencyID the frequency
     */
    public void setFrequencyID(FrequencyID frequencyID) {
        this.frequencyID = frequencyID;
    }
    
    /**
     * Returns this recurrence's frequency.
     * @return the frequency
     */
    public FrequencyID getFrequencyID() {
        return this.frequencyID;
    }
    
    /**
     * Calculates the next due date based on the frequency and the current due
     * date.
     * Assumes {@link #setFrequencyID} has been called.
     * If the frequency is ONCE, the next due date remains the same as the current
     * due date; otherwise the next due date is calculated to be <code>currentDueDate + offset</code> where
     * the offset is the timespan of one recurrence.  For example, if frequency
     * is ANNUAL, next due date is <code>currentDueDate + one year</code>.
     * @see FrequencyID#increment
     */
    public void calculateNextDueDateFromFrequency() {
        // Create a new calendar; we use the default locale and timezone
        // since there may be no current user context (when bills are created
        // during registration)
        Calendar calendar = new PnCalendar();
        calendar.setTime(getNextDueDate());
        
        // Increment the calendar by an appropriate amount
        getFrequencyID().increment(calendar);
        
        // Set next due to the time from the calendar
        setNextDueDate(calendar.getTime());
    }
        
}