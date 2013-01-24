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
|   $Revision: 20454 $
|       $Date: 2010-02-23 10:48:31 -0300 (mar, 23 feb 2010) $
|     $Author: ritesh $
|
+-----------------------------------------------------------------------------*/

package net.project.gui.calendar;

import net.project.security.SessionManager;

/**
 * This class allows the user to construct a gui button that will pop up a
 * calendar.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class CalendarPopup {

    /**
     * Get the HTML to create the calendar popup icon, which is used in numerous
     * places to allow a user to get a popup calendar to be used when selecting
     * dates.
     *
     * @param fieldName a <code>String</code> value indicating the field that the
     * popup should be filling in with a date.
     * @param formName a <code>String</code> value containing the name of a form.
     * If supplied, the javascript for the popup will search in the specified
     * form for the field name being supplied.  If omitted (null), the
     * javascript will search in the current form.
     * @return a <code>String</code> value containing the html needed to create
     * the calendar popup icon.
     */
    public static String getCalendarPopupHTML(String fieldName, String formName) {
        StringBuffer html = new StringBuffer();
        html.append("<a href=\"javascript:autoDate('" + fieldName + "',");
        html.append("'" + SessionManager.getJSPRootURL() + "'");

        if (formName != null) {
            html.append(",'" + formName + "'");
        }
        html.append(")\">");
        html.append("<img src=\""+ SessionManager.getJSPRootURL() +"/images/calendar.gif\" width=\"16\" align=\"absmiddle\" height=\"16\" border=\"0\"></a>");
        html.append("<span class=\"calendarPopup\">(" + SessionManager.getUser().getDateFormatter().getDateFormatExample() + ")</span>");
        return html.toString();
    }
}
