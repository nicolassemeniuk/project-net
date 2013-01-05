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
package net.project.billing.bill.dueness;

import java.util.Date;

import net.project.billing.bill.category.Category;
import net.project.billing.bill.category.CategoryID;
import net.project.license.LicenseException;
import net.project.license.system.MasterProperties;
import net.project.license.system.PropertyName;
import net.project.persistence.PersistenceException;

/**
 * Dueness of a bill describes when it is next due 
 * @author Vishwajeet
 */
public class Dueness {

    /** The date on which the bill is due. */
    private Date dueDate = null;

    /** The date on which the bill was created. */
    private Date creationDate = null;

    /** The date on which this bill is due. */
    private Category category = null;
    
    /**
     * Creates an empty Dueness.
     */
    public Dueness() {
        // Do nothing
    }

    /**
     * Creates a Dueness based on bill's category.
     */
    public Dueness(Category category) {
        this.setCategory(category);
    }

    /**
     * Sets the due date.
     * @param the due date
     */
    public void setDueDate(Date date) {
        this.dueDate = date;
    }

    /**
     * Indicates the due date.
     * @returns the due date
     */
    public Date getDueDate() 
	throws LicenseException, PersistenceException {
	if (this.dueDate == null) {
	    calculateDueDate();
	}
        return this.dueDate;
    }

    /**
     * Sets the creation date.
     * @param the creation date
     */
    public void setCreationDate(Date date) {
        this.creationDate = date;
    }

    /**
     * Indicates the creation date.
     * @returns the creation date
     */
    public Date getCreationDate() {
        if (this.creationDate == null) {
	    setCreationDate(new java.util.Date());
	}
	return this.creationDate;
    }

    /**
     * Sets the bill category.
     * @param the bill category
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Indicates the bill's category.
     * @returns the bill category for which the dueness
     */
    public Category getCategory() {
        return this.category;
    }
    
    /**
     * Calculates the due date based on the creation date, bill category  and 
     * the billing master properties
     * Assumes {@link #setCreationDate} {@link #setCategory} and has been called.
     * the next due date is calculated to be <code>creationDate + offset</code> where
     * the offset is the timespan specified by a master property for a particular type of bill.
     *  For example, if a bill is of type LICENSE_USAGE_BILLING_PERIOD_TYPEA the master property value 
     *  for which is x , the due date is <code>creationDate + x days</code>.
     */
    private void calculateDueDate() 
	throws LicenseException, PersistenceException {
	MasterProperties props = MasterProperties.getInstance();

	// Currently we are assuming that there would be only one type of license (Enterprise License) 
	//installed on a system. For this type of license there would be two bills associated with it-- the 
	// base cost bill and the maintenance cost bill. The dueness of these bills is calculated  
	// by refering to the corresponding license master properties.
	// In future, there could be more than one type of license installed on same system , the billing
	// cycles for which could be different. 

	CategoryID categoryID = category.getID();
	String billingPeriod = null;

	if (categoryID.equals(CategoryID.LICENSE_USAGE_TYPE_A)) {
	    int days = 0;
            billingPeriod = props.get(PropertyName.LICENSE_USAGE_BILLING_PERIOD_TYPEA).getValue();
	    
	    if (billingPeriod != "") {
                days = java.lang.Integer.parseInt(billingPeriod); 
	    }
	    
	    if (days <= 0) {
		setDueDate(getCreationDate());
	    } else {
		setDueDate(net.project.util.DateUtils.addDay(getCreationDate(), days));
            }
            
            
	} else if (categoryID.equals(CategoryID.LICENSE_MAINTENANCE_TYPE_B)) {

	    billingPeriod = props.get(PropertyName.LICENSE_MAINTENANCE_BILLING_PERIOD_TYPEB).getValue();
	    if (billingPeriod == null) {
		throw new LicenseException("No billing period specified for LICENSE_MAINTENANCE_BILLING_PERIOD_TYPEB");
	    }
	    int months = java.lang.Integer.parseInt(billingPeriod); 
         
	    if (months <= 0) {
		setDueDate(getCreationDate());
	    } else {
		setDueDate(net.project.util.DateUtils.addMonth(getCreationDate(), months));
            }
         
        }
	
    }
    
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

