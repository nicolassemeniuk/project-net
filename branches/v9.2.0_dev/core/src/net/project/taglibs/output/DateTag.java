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
package net.project.taglibs.output;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.security.User;
import net.project.util.DateFormat;

/**
 * Provides a formatted presentation of a date.
 * <p>
 * <ul>
 * <li>date - the <code>java.util.Date</code> object to format.
 * <li>format - the format; one of <code>"date"</code>, <code>"datetime"</code> or <code>"time"</code>.
 * </ul>
 * Assumes there is a valid <code>user</code> object in the session.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.5.0
 */
public class DateTag extends TagSupport {

    /**
     * Indicates date format required.
     */
    private static final String FORMAT_DATE = "date";

    /**
     * Indicates time format required.
     */
    private static final String FORMAT_DATETIME = "datetime";

    /**
     * Indicates datetime format required.
     */
    private static final String FORMAT_TIME = "time";

    /**
     * The default presentation format, currently {@link #FORMAT_DATETIME}.
     */
    private static final String FORMAT_DEFAULT = FORMAT_DATETIME;

    /**
     * The date to format.
     */
    private Date date = null;

    /**
     * The format to produce.
     */
    private String format = null;

    /**
     * Creates an empty DateTag.
     */
    public DateTag() {
        super();
    }

    /**
     * Formats the date.
     * @return {@link #EVAL_PAGE} always.
     * @throws JspException if there is a problem formatting the date
     */
    public int doEndTag() throws JspException {

        try {
            JspWriter out = pageContext.getOut();
            out.print(formatDate());

        } catch (IOException ioe) {
            throw ((JspException) new JspTagException("Error in Date tag: " + ioe).initCause(ioe));

        } finally {
            // Clear all attributes for re-use of this tag
            clear();
        }

        return EVAL_PAGE;
    }

    /**
     * Formats the current date.
     * @return the formatted date
     * @throws JspException if any required parameters are null or have
     * unexpected values
     */
    private String formatDate() throws JspException {

        // Check for mandatory parameters
        if (date == null) {
            throw new JspException("'date' is a required attribute");
        }

        // Grab the current user and construct a date formatter for them
        User user = getUser();
        DateFormat dateFormat = new DateFormat(user);

        // Determine the correct display format; use the default if none
        // was specified.
        String displayFormat = this.format;
        if (displayFormat == null) {
            displayFormat = FORMAT_DEFAULT;
        }

        String formattedDate = null;

        if (displayFormat.equals(FORMAT_DATE)) {
            formattedDate = dateFormat.formatDate(this.date);

        } else if (displayFormat.equals(FORMAT_DATETIME)) {
            formattedDate = dateFormat.formatDateTime(this.date);

        } else if (displayFormat.equals(FORMAT_TIME)) {
            formattedDate = dateFormat.formatTime(this.date);

        } else {
            throw new JspException("Unexpected value for 'format' attribute: '" + displayFormat + "'.  Must be one of: 'date', 'datetime', 'time'.");
        }

        return formattedDate;
    }


    /**
     * Returns the current user context.
     * @return the current user from the session scope
     */
    private User getUser() {
        return (User) pageContext.getAttribute("user", PageContext.SESSION_SCOPE);
    }

    /**
     * Clears the values in this tag for reuse.
     */
    private void clear() {
        this.date = null;
        this.format = null;
    }

    //
    // Attribute Setters
    //

    /**
     * Specifies the date to format.
     * @param date the date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Specifies the format to produce.
     * Optional attribute.  Default value is <code>"datetime"</code>.
     * @param format the format; One of <code>"date"</code>, <code>"datetime"</code> or <code>"time"</code>.
     */
    public void setFormat(String format) {
        this.format = format;
    }

}

