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

 package net.project.document;

import net.project.base.PnetException;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

/**
 * A DocumentException is the general error indicating a problem occurred in the
 * Documents module.
 */
public class DocumentException extends PnetException {
    public DocumentException() {
        super();
    }

    /**
     * Creates a DocumentException with the specified message.
     *
     * @param message the message
     */
    public DocumentException(String message) {
        super(message);
    }

    /**
     * Creates a DocumentException with the specified message and severity.
     *
     * @deprecated Use #DocumentException(message, cause) or #DocumentException(message)
     * instead.  We don't track severity in this way any more.
     * @param message the message
     * @param severity the severity
     */
    public DocumentException(String message, String severity) {
        super(message);
    }


    /**
     * Creates a new DocumentException with the specified message and causing
     * throwable.
     *
     * @param message detailed message
     * @param cause the causing exception
     */
    public DocumentException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
    public String getLocalizedMessage() {
        return (getMessage());
    }

    public void log() {
        super.log(getName(), getMessage());
    }

    public String getXML() {

        StringBuffer xml = new StringBuffer();

        xml.append("<exception>\n");

        xml.append("<name>" + XMLUtils.escape(getName()) + "</name>\n");
        xml.append("<message>" + XMLUtils.escape(getMessage()) + "</message>\n");
        xml.append("<severity>" + XMLUtils.escape(getSeverity()) + "</severity>\n");
        xml.append("<reason_code>" + XMLUtils.escape(Conversion.intToString(getReasonCode())) + "</reason_code>\n");
        xml.append("<stack_trace>" + XMLUtils.escape("STACK_TRACE_HERE") + "</stack_trace>\n");

        xml.append("</exception>\n\n");

        return xml.toString();

    } // end getXML()


} // end CheckInFailedNullException
