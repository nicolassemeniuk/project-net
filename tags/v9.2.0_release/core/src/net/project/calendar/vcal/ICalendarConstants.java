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
package net.project.calendar.vcal;

/**
 * Constants for the calendar.
 * These are all the property and parameter names used when constructing
 * the calendar.
 * @author Tim
 * @since emu
 */
public interface ICalendarConstants {

    /**
     * The current version of vCalendar supported.
     */
    public static final String VCAL_VERSION = "1.0";

    /**
     * The product ID of this vCalendar product. 
     */
    public static final String VCAL_PRODUCTIDENTIFIER = "-//Project.net, Inc//NONSGML PRMServer//EN";

    //===========================================================================

    //// Valid values for Strings
    /** 
     * Values that are NONSEMI cannot contain these values.
     */
    public static final char[] NONSEMI = {';', '\r', '\n'};

    /** 
     * Values that are WORD cannot contain these values.
     */
    public static final char[] WORD    = {'[', ']', '=', ':', '.', ','};

    /** Carriage return, linefeed */
    public static final String CRLF = "\r\n";

    //===========================================================================

    //// Top-level properties
    public static final String BEGIN = "BEGIN";
    public static final String END = "END";
    public static final String VCALENDAR = "VCALENDAR";
    public static final String VTODO = "VTODO";
    public static final String VEVENT = "VEVENT";

    //===========================================================================

    public static final String EXTENSION = "X-";

    //===========================================================================

    //// Calendar properties
    public static final String CAL_DAYLIGHT = "DAYLIGHT";
    public static final String CAL_GEO = "GEO";
    public static final String CAL_PRODID = "PRODID";
    public static final String CAL_TZ = "TZ";
    public static final String CAL_VERSION = "VERSION";

    //===========================================================================

    //// Property parameters
    public static final String PARAM_TYPE = "TYPE";
    public static final String PARAM_VALUE = "VALUE";
    public static final String PARAM_ENCODING = "ENCODING";
    public static final String PARAM_CHARSET = "CHARSET";
    public static final String PARAM_LANGUAGE = "LANGUAGE";
    public static final String PARAM_ROLE = "ROLE";
    public static final String PARAM_RSVP = "RSVP";
    public static final String PARAM_EXPECT = "EXPECT";
    public static final String PARAM_STATUS = "STATUS";
    public static final String PARAM_X = EXTENSION; 

    //===========================================================================

    //// To do / Event Entity fields

    //// These are singleton values
    public static final String FIELD_STATUS = "STATUS";      // required
    public static final String FIELD_COMPLETED = "COMPLETED";   // required
    public static final String FIELD_DESCRIPTION = "DESCRIPTION"; // required
    public static final String FIELD_DTSTART = "DTSTART";     // required
    public static final String FIELD_DTEND = "DTEND";       // required
    public static final String FIELD_DUE = "DUE";         // required

    public static final String FIELD_DCREATED = "DCREATED";
    public static final String FIELD_LASTMODIFIED = "LAST-MODIFIED";
    public static final String FIELD_EXRULE = "EXRULE";

    public static final String FIELD_CLASS = "CLASS";
    public static final String FIELD_LOCATION = "LOCATION";
    public static final String FIELD_RNUM = "RNUM";
    public static final String FIELD_PRIORITY = "PRIORITY";
    public static final String FIELD_RELATEDTO = "RELATED-TO";
    public static final String FIELD_RRULE = "RRULE";
    public static final String FIELD_SEQUENCE = "SEQUENCE";
    public static final String FIELD_SUMMARY = "SUMMARY";
    public static final String FIELD_TRANSP = "TRANSP";
    public static final String FIELD_URL = "URL";
    public static final String FIELD_UID = "UID";
    public static final String FIELD_X = EXTENSION;

    //// These are list values
    public static final String FIELD_CATEGORIES = "CATEGORIES";  // required

    public static final String FIELD_AALARM = "AALARM";
    public static final String FIELD_DALARM = "DALARM";
    public static final String FIELD_MALARM = "MALARM";
    public static final String FIELD_PALARM = "PALARM";
    public static final String FIELD_EXDATE = "EXDATE";
    public static final String FIELD_RDATE = "RDATE";

    public static final String FIELD_ATTACH = "ATTACH";
    public static final String FIELD_RESOURCES = "RESOURCES";
    public static final String FIELD_ATTENDEE = "ATTENDEE";

}
