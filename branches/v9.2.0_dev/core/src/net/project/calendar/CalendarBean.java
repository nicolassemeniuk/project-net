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


import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;

import net.project.xml.XMLFormatter;

import org.apache.log4j.Logger;

/**
 * Calendar for use within JSP pages.
 *
 * @author                                                    AdamKlatzkin    03/00
 */
public class CalendarBean extends PnCalendar implements Serializable {
    // request parameters the bean uses
    public static final String PARAM_date = "DisplayDate";
    public static final String PARAM_mode = "DisplayMode";
    public static final String PARAM_type = "DisplayType";

    // display modes
    public static final String MODE_day = "day";
    public static final String MODE_week = "week";
    public static final String MODE_month = "month";
    public static final String MODE_year = "year";

    // display types
    public static final String TYPE_graphic = "graphic";
    public static final String TYPE_list = "list";

    // default state
    private static final String DEFAULT_mode = "month";
    private static final String DEFAULT_type = "graphic";

    private static final String FORMAT_date = "MMddyyyy";


    // current display properties
    private String m_displayDate = null;
    private String m_displayMode = DEFAULT_mode;
    private String m_displayType = DEFAULT_type;

    // Contains XML formatting information and utilities specific to this object
    private final XMLFormatter m_formatter;

    /**
     * Construct a calendar bean initialized to the current user and space.
     */
    public CalendarBean() {
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
     * Sets the stylesheet file name used to render this component.  This method
     * accepts the name of the stylesheet used to convert the XML representation
     * of the component to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML
     * representation of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        m_formatter.setStylesheet(styleSheetFileName);
    }

    /**
     * Sets the current display state of the calendar.
     * If all parameters are null the calendar will be set to the default display state.
     * As long as one paramter is set, null for the other parameters will result in
     * no change of setting for that state item.
     *
     * @param date display date -- should be in the format "MMddyyyy"
     * @param mode display mode -- one of MODE_*
     * @param type display type -- one of TYPE_*
     */
    public void setState(String date, String mode, String type) {
        if ((date == null) && (mode == null) && (type == null)) {
            m_displayDate = null;
            m_displayMode = DEFAULT_mode;
            m_displayType = DEFAULT_type;
            return;
        }

        if (date != null)
            setDisplayDate(date);
        if (mode != null)
            setDisplayMode(mode);
        if (type != null)
            setDisplayType(type);
    }

    /**
     * Force the display date to the specified value
     *
     * @param date   display date
     */
    private void setDisplayDate(String date) {
        m_displayDate = date;
    }

    /**
     * Force the display mode to the specified value
     *
     * @param mode   display mode -- one of MODE_*
     */
    private void setDisplayMode(String mode) {
        m_displayMode = mode;
    }

    /**
     * @return String    the current display mode -- one of MODE_*
     */
    public String getDisplayMode() {
        return m_displayMode;
    }

    /**
     * Force the display type to the specified value
     *
     * @param type a <code>String</code> value which matches one of the
     * constant display type (TYPE_*) defined in this object.
     */
    private void setDisplayType(String type) {
        m_displayType = type;
    }

    /**
     * @return String    the current display type -- one of TYPE_*
     */
    public String getDisplayType() {
        return m_displayType;
    }

    /**
     * @return String    the current state in HTTP query string format
     */
    public String getStateAsQueryString() {
        return getStateAsQueryString((String)null, null, null);
    }

    /**
     * @param theDate    use the specified date object to override the stored date when
     *                   generating the QS
     * @return String    the current state in HTTP query string format
     */
    public String getStateAsQueryString(Date theDate) {
        return getStateAsQueryString(theDate, null, null);
    }

    /**
     * get the current state as a HTTP query string
     *
     * @param theDate use the specified date object to override the stored date when
     * generating the QS
     * @param mode use the specified mode to override the stored mode when
     * generating the QS
     * @param type use the specified type to override the stored type when
     * generating the QS
     * @return String the state in query string format
     */
    public String getStateAsQueryString(Date theDate, String mode, String type) {
        return getStateAsQueryString(formatDateAs(theDate, FORMAT_date), mode, type);
    }

    /**
     * Get the current state as a HTTP query string.
     *
     * @param date use the specified date to override the stored date when
     * generating the QS
     * @param mode use the specified mode to override the stored mode when
     * generating the QS
     * @param type use the specified type to override the stored type when
     * generating the QS
     * @return String the state in query string format
     */
    public String getStateAsQueryString(String date, String mode, String type) {
        StringBuffer qs = new StringBuffer("?");
        if (date != null)
            qs.append(PARAM_date + "=" + date + "&");
        else if (m_displayDate != null)
            qs.append(PARAM_date + "=" + m_displayDate + "&");

        if (mode != null)
            qs.append(PARAM_mode + "=" + mode + "&");
        else if (m_displayMode != null)
            qs.append(PARAM_mode + "=" + m_displayMode + "&");

        if (type != null)
            qs.append(PARAM_type + "=" + type);
        else if (m_displayType != null)
            qs.append(PARAM_type + "=" + m_displayType);

        return qs.toString();
    }

    /**
     * @return String the current state represented as a collection of HTML
     * hidden input fields.
     */
    public String getStateAsFormBody() {
        StringBuffer qs = new StringBuffer();

        if (m_displayDate != null)
            qs.append("<INPUT TYPE=\"HIDDEN\" NAME=\"" + PARAM_date + "\" VALUE=\"" + m_displayDate + "\">");

        if (m_displayMode != null)
            qs.append("<INPUT TYPE=\"HIDDEN\" NAME=\"" + PARAM_mode + "\" VALUE=\"" + m_displayMode + "\">");

        if (m_displayType != null)
            qs.append("<INPUT TYPE=\"HIDDEN\" NAME=\"" + PARAM_type + "\" VALUE=\"" + m_displayType + "\">");

        return qs.toString();
    }

    /**
     * @return Date the current display date as a date object
     */
    public Date getDisplayDateAsObject() {
        if (m_displayDate == null)
            return null;

        //return getUser().getDateFormatter().parseDate(m_displayDate, FORMAT_date);
        //return getUser().getDateFormatter().parseDate(m_displayDate);
        Date displayDate = null;
        try {
            displayDate = getUser().getDateFormatter().parseDateString(m_displayDate, FORMAT_date);

        } catch (net.project.util.InvalidDateException ide) {
            //We don't expect this to happen hence,
            //we suck the exception and return a value of null
        	Logger.getLogger(CalendarBean.class).debug("CalendarBean.getDisplayDateAsObject(): InvalidDateException: " + ide);

        }
        return displayDate;
    }

    /**
     * Creates a string date representation of 'theDate' using the specified
     * pattern.
     *
     * @param theDate    date object to format
     * @param pattern    pattern to use
     * @return String    formatted date string
     */
    public String formatDateAs(Date theDate, String pattern) {
        return getUser().getDateFormatter().formatDate(theDate, pattern);
    }
}
