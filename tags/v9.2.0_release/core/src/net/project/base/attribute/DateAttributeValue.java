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

package net.project.base.attribute;

import java.util.Date;

/**
 * Represents Date Type Attribute Value
 * @author deepak
 */
public  class DateAttributeValue extends AbstractAttributeValue {
    /**
     * Date Type of standard Java Type
     */
    private Date date=null;
    /**
     * Attribute Type of CSV Form
     */
    private IAttribute iAttribute=null;
    
    /**
     * Constructor which takes in IAAtribute Type as parameter
     * @param iAttribute 
     */
    public DateAttributeValue(IAttribute iAttribute){
       this.iAttribute=iAttribute;
   }
    /**
     * Returns the Date 
     * @return   Date
     */

    public Date getDateValue(){
        //System.out.println(date.toString());
        return date;
    }
    
    /**
     * Sets the Date Value
     *@param Date to be stored
     */

    public void setDateValue(Date date){
        this.date=date;
    }


    /**
     *  Gets the Attribute for then AttributeValue
     * @return IAttribute
     */

    public IAttribute getAttribute(){
       return iAttribute;
   }
}

