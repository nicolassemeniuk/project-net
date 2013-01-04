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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.calendar;

import java.sql.SQLException;

import net.project.xml.XMLFormatter;

/**
 * Presentation class for meetings
 *
 * @author Roger Bly
 * @author AdamKlatzkin
 * @since 03/00
 */

public class MeetingBean extends Meeting implements java.io.Serializable {
    /** Contains XML formatting information and utilities specific to this object **/
    protected XMLFormatter m_formatter;

    /**
     * Public constructor which creates a new instance of a <code>MeetingBean</code>.
     */
    public MeetingBean() {
        m_formatter = new XMLFormatter();
    }

    /**
     * Gets the presentation of the component.  This method will apply the
     * stylesheet to the XML representation of the component and return the
     * resulting text.
     *
     * @return presetation of the component
     * @throws SQLException 
     */
    public String getPresentation() throws SQLException {
        return m_formatter.getPresentation(getXML());
    }

    /**
     * Gets the presentation of the meetings agenda items
     */
    public String getAgendaPresentation() {
        return m_formatter.getPresentation(getAgendaXML());
    }

    /**
     * Gets the presentation of the meetings attendees
     */
    public String getAttendeePresentation() {
        return m_formatter.getPresentation(getAttendeesXML());
    }

    /**
     * Sets the stylesheet file name used to render this component.
     * This method accepts the name of the stylesheet used to convert the XML representation
     * of the component to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML representation
     * of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        m_formatter.setStylesheet(styleSheetFileName);
    }
}
