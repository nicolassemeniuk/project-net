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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/

package net.project.xml;

import net.project.base.PnetRuntimeException;

/**
 * This class should be thrown when an error is thrown in one of the IXMLPersistence
 * methods that isn't declared in their method signatures.  At this point, there
 * is too much code written to change all of the implementations, so if an error
 * is thrown, we are going to throw it as an unchecked exception.  This is a
 * pretty bad hack, but unfortunately, I cannot see another way around it without
 * breaking a lot of customer and pnet code.
 *
 * @author Matthew Flower
 * @since Gecko Update 4
 */
public class XMLConstructionException extends PnetRuntimeException {
    /**
     * Common constructor to be used when exception has a message describing why
     * the exception occurred.
     *
     * @param message a <code>String</code> value containing a message which
     * explains why the error has occurred.
     */
    public XMLConstructionException(String message) {
        super(message);
    }

    /**
     * Common constructor to be used when the exception has been caused by
     * another exception.
     *
     * @param message a <code>String</code> value containing a message which
     * explains why the error has occurred.
     * @param cause a <code>Throwable</code> value which caused this exception.
     */
    public XMLConstructionException(String message, Throwable cause) {
        super(message, cause);
    }
}
