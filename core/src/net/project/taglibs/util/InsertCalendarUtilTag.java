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
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|
+----------------------------------------------------------------------*/

package net.project.taglibs.util;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.gui.calendar.CalendarPopup;

/**
 * Provides a JSP tag that inserts the the presentation code for rendering
 * a calendar popup link.
 */
public class InsertCalendarUtilTag extends TagSupport {

    /**
     * The name of the HTML form field that will receive the selected
     * calendar value.
     */
    String fieldName = null;

    /**
     * The name of the HTML form that will receive the selected calendar value.
     */
    String formName = null;

    /**
     * Sets the HTML form field name that will receive the selected calendar
     * value.
     * @param name the HTML form field name
     */
    public void setFieldName(String name) {
        this.fieldName = name;
    }

    /**
     * Sets the HTML form name that on which the specified field name is displayed.
     * @param formName the HTML form name
     */
    public void setFormName(String formName) {
        this.formName = formName;
    }

    /**
     * Sets the root URL to use when inserting the path to the popup JSP page.
     * @param rootURL the root of the URL to use when invoking the calendar JSP page
     * @deprecated as of 7.6.3; no replacement
     * The rootURL will always be assumed to be the JSP root url.
     */
    public void setRootURL(String rootURL) {
        // Do nothing
    }

    /* -------------------------------  Constructors  ------------------------------- */

    public InsertCalendarUtilTag() {
        super();
    }


    /* -------------------------------  Overriding TagSupport methods  ------------------------------- */

    public int doStartTag() throws JspTagException {

        StringBuffer html = new StringBuffer();
        JspWriter out = pageContext.getOut();

        html.append(CalendarPopup.getCalendarPopupHTML(fieldName, formName));

        try {
            out.print(html.toString());
        } catch (IOException ioe) {
            throw new JspTagException("I/O exception: " + ioe);
        }

        return SKIP_BODY;

    }

    /* -------------------------------  Implementing utility methods  ------------------------------- */

}
