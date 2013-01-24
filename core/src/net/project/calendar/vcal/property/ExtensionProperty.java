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


/**
 * The clear-text encoding provides a "standard mechanism for doing non-standard 
 * things". This extension support is provided for implementers to "push the 
 * envelope" on the existing version of the specification. Extension properties 
 * are specified by property and/or property parameter names that have the 
 * initial sub-string of X- (the two character sequence: Capital X character 
 * followed by the Dash character). It is recommended that vendors concatenate 
 * onto this sentinel an added short sub-string to identify the vendor. This 
 * will facilitate readability of the extensions and minimize possible collision 
 * of names between different vendors.
 */
public class ExtensionProperty extends Property implements net.project.calendar.vcal.ICalendarConstants {

    public ExtensionProperty(String name) {
        super(name);
    }

    public ExtensionProperty(String name, String value) {
        super(name, new SimplePropertyValue(value));
    }

}
