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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.calendar.vcal.property;

import net.project.calendar.vcal.VCalException;
import net.project.calendar.vcal.parameter.Parameter;

/**
 * This defines the categories for the vCalendar entity.
 */
public class CategoriesProperty extends Property implements net.project.calendar.vcal.ICalendarConstants {

    public CategoriesProperty() {
        super(FIELD_CATEGORIES);
    }

    public CategoriesProperty(String value) {
        super(FIELD_CATEGORIES, value);
    }

    /**
     * This property supports no paramters.
     * @throws VCalException always
     */
    public void addParameter(Parameter parameter) throws VCalException {
        throw new VCalException("Categories Property supports no parameters");
    }
}

