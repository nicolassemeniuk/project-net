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
package net.project.taglibs.input;

import java.util.Date;
import java.util.TimeZone;

import javax.servlet.jsp.JspTagException;

import net.project.calendar.TimeBean;

/**
 * A Time HTML input tag.
 * <p>
 * A time tag provides entry for times, customized for the current users locale.
 * For example, some locales allow specification of AM/PM.  Other locales use
 * 24-hour clock.
 * </p>
 * <p>
 * This tag produces 2 or 3 input fields.  The first is a selection list for
 * hours named <code><i>name</i>_hour</code> where <code>name</code> is an attribute
 * for this taglib.  The second field is a selection list for minutes named
 * <code><i>name</i>_minute</code>.  The third field is optional and is named
 * <code><i>name</i>_ampm</code>; it is included only if the locale uses
 * AM/PM in its short time format.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.5
 */
public class TimeTag extends AbstractInputTag {

    /**
     * The time to initialize the selection lists to.
     */
    private Date time;

    /**
     * Indicates whether the field is optional or not.
     */
    private Boolean isOptional;

    /**
     * Specifies the style of the minutes to display.
     */
    private int minuteStyle = TimeBean.MINUTE_STYLE_NORMAL;

    /**
     * Indicates whether to include the time zone display.
     */
    private Boolean isIncludeTimeZone;

    /**
     * The time zone to use instead of current user's time zone.
     */
    private TimeZone timeZone;

    /**
     * Creates an empty TimeTag.
     */
    public TimeTag() {
        super();
    }

    /**
     * Constructs the input element for capturing Money values.
     * This includes a text field and either a hidden field or a select list.
     * @return the HTML for inputting a money value
     * @throws JspTagException if there is a problem constructing the input element;
     * for example, neither money nor currency attributes have been specified
     */
    protected String constructInputElement() throws JspTagException {

        // Name attribute is required
        if (getAttributeValueMap().get("name") == null) {
            throw new JspTagException("Name attribute is required");
        }

        // Use the TimeBean to generate the presentation
        TimeBean timeBean = new TimeBean();
        timeBean.setTag((String) getAttributeValueMap().get("name"));

        String id = (String) getAttributeValueMap().get("id");
        if (id != null) {
            timeBean.setID(id);
        }

        timeBean.setDate(this.time);
        timeBean.setAttributes(getAttributeValueMap());
        if (isOptional != null) {
            timeBean.setOptional(isOptional.booleanValue());
        }
        timeBean.setMinuteStyle(minuteStyle);
        if (isIncludeTimeZone != null) {
            timeBean.setIncludeTimeZone(isIncludeTimeZone.booleanValue());
        }
        if (timeZone != null) {
            timeBean.setTimeZone(timeZone);
        }

        // Now construct the HTML
        StringBuffer elementText = new StringBuffer();
        elementText.append(timeBean.getPresentation());

        return elementText.toString();
    }

    /**
     * Clears the values in this tag for reuse.
     */
    protected void clear() {
        this.time = null;
    }

    //
    // Attribute Setters
    //

    /**
     * Specifies the time to initialize the selection lists to.
     * This is optional; the current time is used
     * @param currentTime a date whose time component will be used to
     * initialize the selection list
     */
    public void setTime(Date currentTime) {
        this.time = currentTime;
    }

    /**
     * Indicates whether the time field is optional.
     * When optional an empty value is included to indicate the time is unset.
     * When not optional, the time field always has a value.
     * @param isOptional true if this time field is optional; false if it
     * is mandatory
     */
    public void setIsOptional(boolean isOptional) {
        this.isOptional = new Boolean(isOptional);
    }

    /**
     * Sets the display style of minutes, either 0..59 or 00, 15, 30, 45.
     * @param minuteStyle the minute style; one of <code>calendar</code> or
     * <code>normal</code> (default)
     * @throws JspTagException if an illegal value is specified
     */
    public void setMinuteStyle(String minuteStyle) throws JspTagException {

        if (minuteStyle != null) {
            if (minuteStyle.equals("calendar")) {
                this.minuteStyle = TimeBean.MINUTE_STYLE_CALENDAR;

            } else if (minuteStyle.equals("normal")) {
                this.minuteStyle = TimeBean.MINUTE_STYLE_NORMAL;

            } else {
                throw new JspTagException("Invalid minute style '" + minuteStyle + "'; must be one of: calendar, normal (default)");
            }
        }

    }

    /**
     * Indicates whether to include a display of the time zone.
     * <p>
     * Default is <code>false</code>.
     * </p>
     * @param isIncludeTimeZone true if the current time zone should be
     * included; false otherwise
     */
    public void setIsIncludeTimeZone(boolean isIncludeTimeZone) {
        this.isIncludeTimeZone = new Boolean(isIncludeTimeZone);
    }

    /**
     * Specifies a time zone to use for displaying and input of
     * a time overriding the default of the current user's time zone.
     * @param timeZone the time zone to use instead of the
     * current user's time zone
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

}
