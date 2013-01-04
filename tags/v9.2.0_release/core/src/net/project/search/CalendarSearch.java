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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.ObjectType;
import net.project.base.ObjectTypeNotSupportedException;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.database.DBBean;
import net.project.resource.FacilityType;
import net.project.resource.FacilityTypeDomain;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.DateUtils;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;


/**
 * This class is used for searching Event Objects.  It provides the forms for
 * simple and advanced searching as well as a properly formatted XML string of
 * the results.
 *
 * @author  Mike Brevoort
 * @since 04/00
 */
public class CalendarSearch extends GenericSearch {
    // a few internal constants used for searching options -mdb
    private static final String EVENT = "0";
    private static final String MEETING = "1";

    /** Constant referring to all event types in a selection list. */
    private static final int EVENT_TYPE_ALL = -1;
    /** Constant referring to all facility types in a selection list. */
    private static final int FACILITY_TYPE_ALL = -1;
    /** Available facility types */
    private List facilityTypes = null;

    public CalendarSearch() {
        super();
    }

    /**
     * This is used to set the type of search context, that the object will
     * perform it's doSearch() and getSearchForm() methods in.
     *
     * @param type one of the enumerations of the search types
     */
    public void setSearchType(int type) {
        this.search_type = type;
        return;
    }


    /**
     Get the display name of the this search Type
     */
    public String getDisplayName() {
        return PropertyProvider.get("prm.global.search.calendar.results.channel.title"); // Calendar
    }


    /**
     * This returns a properly formated HTML form for performing the type of
     * search that has been set by setSearchType(). This will be called from a
     * JSP page for the user to search for a specific object.
     *
     * @return the HTML Form UI for doing a simple search
     */
    public String getSearchForm(String formName, HttpServletRequest request) {
        if (this.search_type == SIMPLE_SEARCH)
            return getSimpleSearchForm(formName, request);
        else if (this.search_type == ADVANCED_SEARCH)
            return getAdvancedSearchForm(formName, request);
        else
            return null;
    }

    /**
     * This returns a properly formated HTML form for performing a simple search
     * on the object. This will be called from a JSP page for the user to search
     * for a specific object.  It does not include the opening and closing
     * <FORM> tags.  This is so you specify where it is posting to and add any
     * necessary hidden form fields. It also tries to restore field values from
     * the request.
     *
     * @return the HTML Form UI for doing a simple search
     */
    public String getSimpleSearchForm(String formName, HttpServletRequest request) {
    	
    	String nameInRequest = getParameterSafe(request, "NAME");
    	String eventTypeInRequest = getParameterSafe(request, "EVENTTYPE");
    	
        StringBuffer formString = new StringBuffer();

        formString.append("<TABLE  border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"500\">");

        formString.append("\n<tr>");
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-left_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n<td class=\"channelHeader\" width=98% colspan=2 align=left NOWRAP>" + PropertyProvider.get("prm.global.search.calendar.simple.event.title") + "</td>"); // Simple Calendar Event Search:
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-right_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n</tr>");

        formString.append("<TR  align=\"left\" valign=\"middle\"><td>&nbsp;</td>");
        formString.append("<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.calendar.simple.eventname.label") + "&nbsp;&nbsp;</TD><TD class=\"tableContent\">"); // Event Name:
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"NAME\" VALUE=\""+nameInRequest+"\"></TD><td>&nbsp;</td></TR>");
        formString.append("<TR  align=\"left\" valign=\"middle\"><td>&nbsp;</td>");
        formString.append("<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.calendar.simple.eventtype.label") + "&nbsp;&nbsp;</TD><TD class=\"tableContent\">"); // Event Type:
        formString.append("<SELECT NAME=\"EVENTTYPE\"><OPTION VALUE=\""
            + EVENT_TYPE_ALL + "\" "+getOptionSelection(EVENT_TYPE_ALL,eventTypeInRequest)+">" + PropertyProvider.get("prm.global.search.calendar.simple.option.all.name") + "<OPTION VALUE=\"" // All
            + EVENT + "\" "+getOptionSelection(EVENT,eventTypeInRequest)+">" + PropertyProvider.get("prm.global.search.calendar.simple.option.event.name") + "<OPTION VALUE=\"" // Event
            + MEETING + "\" "+getOptionSelection(MEETING,eventTypeInRequest)+">" + PropertyProvider.get("prm.global.search.calendar.simple.option.meeting.name") + "</SELECT></TD><td>&nbsp;</td></TR>"); // Meeting

        formString.append("<TR  align=\"left\" valign=\"middle\"><td>&nbsp;</td>");
        formString.append("<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.calendar.simple.facilitytype.label") + "&nbsp;&nbsp;</TD><TD class=\"tableContent\">"); // Facility Type:
        formString.append(getFacilityTypesOptionList(request));
        formString.append("</TD><td>&nbsp;</td></TR>");

        formString.append("</TABLE>");


        return formString.toString();

    }

    /**
     * This returns a properly formatted HTML form for performing an advanced
     * search on the object. This will be called from a JSP page for the user to
     * search for a specific object. It also tries to restore field values from
     * the request
     * 
     * @return a <code>String</code> value containing the HTML Form UI for doing
     * an advanced search
     */
    public String getAdvancedSearchForm(String formName, HttpServletRequest request) {
    	    	
    	String nameInRequest = getParameterSafe(request, "NAME");
    	String descInRequest = getParameterSafe(request, "DESC");
    	String eventTypeInRequest = getParameterSafe(request, "EVENTTYPE");
    	String startDateInRequest = getParameterSafe(request, "STARTDATE");
    	String endDateInRequest = getParameterSafe(request, "ENDDATE");
    	
        StringBuffer formString = new StringBuffer();
        formString.append("<TABLE  border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"500\">");

        formString.append("\n<tr>");
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-left_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n<td class=\"channelHeader\" width=98% colspan=2 align=left NOWRAP>" + PropertyProvider.get("prm.global.search.calendar.advanced.event.title") + "</td>"); // Advanced Calendar Event Search:
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-right_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n</tr>");

        formString.append("<TR  align=\"left\" valign=\"middle\"><td>&nbsp;</td>");
        formString.append("<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.calendar.advanced.eventname.label") + "&nbsp;&nbsp;</TD><TD class=\"tableContent\">"); // Event Name:
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"NAME\" VALUE=\""+nameInRequest+"\"></TD><td>&nbsp;</td></TR>");
        formString.append("<TR  align=\"left\" valign=\"middle\"><td>&nbsp;</td>");
        formString.append("<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.calendar.advanced.eventdescription.label") + "&nbsp;&nbsp;</TD><TD class=\"tableContent\">"); // Event Description:
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"DESC\" VALUE=\""+descInRequest+"\"></TD><td>&nbsp;</td></TR>");
        formString.append("<TR  align=\"left\" valign=\"middle\"><td>&nbsp;</td>");
        formString.append("<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.calendar.advanced.eventtype.label") + "&nbsp;&nbsp;</TD><TD class=\"tableContent\">"); // Event Type:
        formString.append("<SELECT NAME=\"EVENTTYPE\"><OPTION VALUE=\""
            + EVENT_TYPE_ALL + "\" "+getOptionSelection(EVENT_TYPE_ALL,eventTypeInRequest)+">" + PropertyProvider.get("prm.global.search.calendar.advanced.option.all.name") + "<OPTION VALUE=\"" // All
            + EVENT + "\" "+getOptionSelection(EVENT,eventTypeInRequest)+">" + PropertyProvider.get("prm.global.search.calendar.advanced.option.event.name") + "<OPTION VALUE=\"" // Event
            + MEETING + "\" "+getOptionSelection(MEETING,eventTypeInRequest)+">" + PropertyProvider.get("prm.global.search.calendar.advanced.option.meeting.name") + "</SELECT></TD><td>&nbsp;</td></TR>"); // Meeting

        formString.append("<TR  align=\"left\" valign=\"middle\"><td>&nbsp;</td>");
        formString.append("<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.calendar.advanced.facilitytype.label") + "&nbsp;</TD><TD class=\"tableContent\">"); // Facility Type:
        formString.append(getFacilityTypesOptionList(request));
        formString.append("</TD><td>&nbsp;</td></TR>");

        formString.append("\n");
        formString.append("<TR  align=\"left\" valign=\"middle\"><td>&nbsp;</td>");
        formString.append("<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.calendar.advanced.afterdate.label", DateFormat.getInstance().getDateFormatExample()) + "&nbsp;&nbsp;</TD><TD class=\"tableContent\">"); // After Date (mm/dd/yyyy):
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"STARTDATE\" VALUE=\""+startDateInRequest+"\" MAXLENGTH=\"10\">");
        formString.append("<a href=\"javascript:autoDate('STARTDATE','"+SessionManager.getJSPRootURL()+"','"+formName+"')\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/calendar.gif\" width=\"16\" align=\"middle\" height=\"16\" border=\"0\"></a>");
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("<TR  align=\"left\" valign=\"middle\"><td>&nbsp;</td>");
        formString.append("<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.calendar.advanced.beforedate.label", DateFormat.getInstance().getDateFormatExample()) + "&nbsp;&nbsp;</TD><TD class=\"tableContent\">"); // Before Date (mm/dd/yyyy):
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"ENDDATE\"  VALUE=\""+endDateInRequest+"\" MAXLENGTH=\"10\">");
        formString.append("<a href=\"javascript:autoDate('ENDDATE','"+SessionManager.getJSPRootURL()+"','"+formName+"')\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/calendar.gif\" width=\"16\" align=\"middle\" height=\"16\" border=\"0\"></a>");
        formString.append("</TD><td>&nbsp;</td></TR>");

        formString.append("</TABLE>");

        return formString.toString();
    }


    /**
     * Return the HTML option list of facility types, including an "All" option.
     * It also tries to restore field values from the request
     * 
     * @param request 
     *
     * @return the HTML option list
     */
    private String getFacilityTypesOptionList(HttpServletRequest request) {
    	
    	String facilityTypeInRequest = getParameterSafe(request, "FACILITYTYPE");
    	
        StringBuffer optionList = new StringBuffer();
        FacilityType facilityType = null;
        Iterator it = getFacilityTypes().iterator();

        // Start of select element
        optionList.append("<select name=\"FACILITYTYPE\">");

        // "All" facility type option
        optionList.append("<option value=\"");
        optionList.append(FACILITY_TYPE_ALL);
        optionList.append("\""+getOptionSelection(FACILITY_TYPE_ALL,facilityTypeInRequest)+">");
        optionList.append(PropertyProvider.get("prm.global.search.calendar.facilitytype.option.all.name")); // All
        optionList.append("</option>");

        // Add each facility type option
        while (it.hasNext()) {
            facilityType = (FacilityType)it.next();
            optionList.append("<option value=\"");
            optionList.append(facilityType.getID());
            optionList.append("\""+getOptionSelection(facilityType.getID(),facilityTypeInRequest)+">");
            optionList.append(facilityType.getName());
            optionList.append("</option>");
        }

        // End of select element
        optionList.append("</select>");

        return optionList.toString();
    }

    /**
     * This will actually search the database for the objects matching the criteria the user
     * entered into the form generated by getSearchForm() method. It retrieves
     * this criteria from the HttpServletRequest object that gets passed into it.  It will
     * then store these results somewhere internally in the class, so that they can be used
     * by the getXMLResults() method.
     *
     * @param m_request the Request object.
     */
    public void doSearch(HttpServletRequest m_request) {
        if (this.search_type == SIMPLE_SEARCH)
            doSimpleSearch(m_request);
        else if (this.search_type == ADVANCED_SEARCH)
            doAdvancedSearch(m_request);

        return;
    }

    /**
     * This will actually seach the database for the object matching the passed in keyowrd.
     * It will then store these results somewhere internally in the class, so that they
     * can be used by the getXMLResults() method.
     *
     * @param keyword the keyword to search on
     */
    public void doKeywordSearch(String keyword) {
        StringBuffer query = new StringBuffer();

        m_results = new ArrayList();

        StringBuffer whereClause = new StringBuffer();
        whereClause.append("where shc.space_id = " + getFirstSpaceID() + " ");
        whereClause.append("and c.calendar_id = shc.calendar_id and che.calendar_id = c.calendar_id ");
        whereClause.append("and ce.calendar_event_id = che.calendar_event_id ");

        if (keyword != null && keyword.length() > 0) {
            whereClause.append("AND (UPPER(ce.event_name) LIKE UPPER('%" + keyword + "%') ");
            whereClause.append("OR BASE.CLOB_LIKE(ce.event_desc_clob, '%" + keyword + "%', 0) > 0 ) ");
        }
        whereClause.append("and ce.record_status = 'A' and c.record_status = 'A' ");

        // Build query by unioning a query to find the events and a query
        // to find the meetings
        // This is necessary in order to get the ID of the meeting; the meeting
        // ID is different from the underlying event ID
        query.append("select unique ce.calendar_event_id as id, ce.event_name, ce.start_date, ce.event_type_id ");
        query.append("from pn_space_has_calendar shc, pn_calendar c, pn_calendar_has_event che, pn_calendar_event ce ");
        query.append(whereClause.toString());
        query.append(" AND ce.event_type_id = 200 ");
        query.append(" union ");
        query.append("select unique m.meeting_id as id, ce.event_name, ce.start_date, ce.event_type_id ");
        query.append("from pn_space_has_calendar shc, pn_calendar c, pn_calendar_has_event che, pn_calendar_event ce, pn_meeting m ");
        query.append(whereClause.toString());
        query.append(" AND m.calendar_event_id = ce.calendar_event_id ");
        query.append(" AND ce.event_type_id = 100 ");

        try {
            m_db.executeQuery(query.toString());

            while (m_db.result.next()) {
                String[] result_row = {m_db.result.getString(1),
                                       m_db.result.getString(2),
                                       m_user.getDateFormatter().formatDateTime(m_db.result.getTimestamp(3)),
                                       m_db.result.getString(4)
                };
                m_results.add(result_row);

            }
        } catch (SQLException sqle) {
        	Logger.getLogger(CalendarSearch.class).error("CalendarSearch.doKeywordSearch() threw an SQL exception");
        } finally {
            m_db.release();
        }

        return;
    }


    /**
     * This will actually search the database for the objects matching the criteria the user
     * entered into the form generated by getSimpleSearchForm() method. It retrieves
     * this criteria from the HttpServletRequest object that gets passed into it.  It will
     * then store these results somewhere internally in the class, so that they can be used
     * by the getXMLResults() method.
     *
     * @param m_request the Request object.
     */
    public void doSimpleSearch(HttpServletRequest m_request) {
        StringBuffer query = new StringBuffer();
        int eventType = 0;
        int facilityType = 0;


        m_results = new ArrayList();
        String name = m_request.getParameter("NAME");
        eventType = Integer.parseInt(m_request.getParameter("EVENTTYPE"));
        facilityType = Integer.parseInt(m_request.getParameter("FACILITYTYPE"));


        StringBuffer subWhereClause = new StringBuffer();
        subWhereClause.append("WHERE shc.space_id = " + getFirstSpaceID() + " ");
        subWhereClause.append("AND shc.calendar_id = che.calendar_id ");
        subWhereClause.append("AND che.calendar_event_id = ce.calendar_event_id ");
        if (facilityType != FACILITY_TYPE_ALL) {
            subWhereClause.append("AND UPPER(f.facility_type) = UPPER('" + facilityType + "') ");
            subWhereClause.append("AND ce.facility_id = f.facility_id ");
        }
        if (name != null && name.length() > 0) {
            subWhereClause.append("AND UPPER(ce.event_name) LIKE UPPER('%" + name + "%') ");
        }

        subWhereClause.append("AND che.calendar_id = c.calendar_id ");
        subWhereClause.append("AND ce.record_status = 'A' AND c.record_status = 'A' ");


        if (eventType == Integer.parseInt(EVENT) || eventType == EVENT_TYPE_ALL) {
            query.append("SELECT DISTINCT ce.calendar_event_id as id, ce.event_name, ce.start_date, ce.event_type_id ");
            query.append("FROM  pn_space_has_calendar shc, pn_calendar c, pn_calendar_has_event che, pn_calendar_event ce, pn_facility f ");
            query.append(subWhereClause.toString());
            query.append(" AND ce.event_type_id = 200 ");

        }
        if (eventType == EVENT_TYPE_ALL) {
            query.append(" union ");
        }
        if (eventType == Integer.parseInt(MEETING) || eventType == EVENT_TYPE_ALL) {
            query.append("SELECT DISTINCT m.meeting_id as id, ce.event_name, ce.start_date, ce.event_type_id ");
            query.append("FROM  pn_space_has_calendar shc, pn_calendar c, pn_calendar_has_event che, pn_calendar_event ce, pn_facility f, pn_meeting m ");
            query.append(subWhereClause.toString());
            query.append(" AND m.calendar_event_id = ce.calendar_event_id ");
            query.append(" AND ce.event_type_id = 100 ");
        }


        try {
            m_db.executeQuery(query.toString());

            while (m_db.result.next()) {
                String[] result_row = {m_db.result.getString(1),
                                       m_db.result.getString(2),
                                       m_user.getDateFormatter().formatDate((m_db.result.getDate(3))),
                                       m_db.result.getString(4)
                };
                m_results.add(result_row);

            }
        } catch (SQLException sqle) {
        	Logger.getLogger(CalendarSearch.class).error("CalendarSearch.doSimpleSearch() threw an SQL exception");
        } finally {
            m_db.release();
        }

        return;
    }

    /**
     * This will actually search the database for the objects matching the criteria the user
     * entered into the form generated by getAdvancedSearchForm() method. It retrieves
     * this criteria from the HttpServletRequest object that gets passed into it.  It will
     * then store these results somewhere internally in the class, so that they can be used
     * by the getXMLResults() method.
     *
     * @param m_request the Request object.
     */
    public void doAdvancedSearch(HttpServletRequest m_request) {
        StringBuffer query = new StringBuffer();
        
        //clear error messages
        searchErrors.clear();

        m_results = new ArrayList();
        String name = m_request.getParameter("NAME");
        String description = m_request.getParameter("DESC");
        int eventType = Integer.parseInt(m_request.getParameter("EVENTTYPE"));
        int facilityType = Integer.parseInt(m_request.getParameter("FACILITYTYPE"));
        String startDate = m_request.getParameter("STARTDATE");
        String endDate = m_request.getParameter("ENDDATE");

        //Try to parse the dates the user entered
        DateFormat df = DateFormat.getInstance();
        Date startDateParsed = null;
        Date endDateParsed = null;
        if (!Validator.isBlankOrNull(startDate)) {
            try {
                startDateParsed = df.parseDateString(startDate);
            } catch (net.project.util.InvalidDateException e) {
                String message = PropertyProvider.get("prm.global.search.calendar.advanced.afterdate.invalid.message",startDate);
                searchErrors.add(message);
            }
        }

        if (!Validator.isBlankOrNull(endDate)) {
            try {
                endDateParsed = df.parseDateString(endDate);
            } catch (net.project.util.InvalidDateException e) {
                String message = PropertyProvider.get("prm.global.search.calendar.advanced.beforedate.invalid.message",endDate);
                searchErrors.add(message);
            }
        }
        
        if(startDateParsed != null && endDateParsed != null && startDateParsed.after(endDateParsed)){
            String message = PropertyProvider.get("prm.global.search.advanced.daterange.invalid.message");
            searchErrors.add(message);
        }

        // stop processing if we already have errors
        if(searchErrors.size()>0)
        	return;
        
        StringBuffer subWhereClause = new StringBuffer();
        subWhereClause.append("WHERE shc.space_id = " + getFirstSpaceID() + " ");
        subWhereClause.append("AND shc.calendar_id = che.calendar_id ");
        subWhereClause.append("AND che.calendar_event_id = ce.calendar_event_id ");
        if (facilityType != FACILITY_TYPE_ALL) {
            subWhereClause.append("AND UPPER(f.facility_type) = UPPER('" + facilityType + "') ");
            subWhereClause.append("AND ce.facility_id = f.facility_id ");
        }
        if (name != null && name.length() > 0) {
            subWhereClause.append("AND UPPER(ce.event_name) LIKE UPPER('%" + name + "%') ");
        }
        if (description != null && description.length() > 0) {
            // Case insensitive clob comparison
            subWhereClause.append("AND BASE.CLOB_LIKE(ce.event_desc_clob, '%" + description + "%', 0) > 0 ");
        }
        if (startDateParsed != null && endDateParsed != null) {
            subWhereClause.append("AND (");
            subWhereClause.append("( ce.start_date <= " + DateUtils.getDatabaseDateString(startDateParsed));
            subWhereClause.append("AND ce.end_date >= " + DateUtils.getDatabaseDateString(startDateParsed) + " ) ");
            subWhereClause.append(" OR ");
            subWhereClause.append("( ce.start_date <= " + DateUtils.getDatabaseDateString(endDateParsed));
            subWhereClause.append("AND ce.end_date >= " + DateUtils.getDatabaseDateString(endDateParsed) + " ) ");
            subWhereClause.append(" OR ");
            subWhereClause.append("( ce.start_date > " + DateUtils.getDatabaseDateString(startDateParsed));
            subWhereClause.append("AND ce.end_date < " + DateUtils.getDatabaseDateString(endDateParsed) + " ) ");
            subWhereClause.append(") ");
        } else if (startDateParsed != null) {
            subWhereClause.append("AND ce.end_date >= " + DateUtils.getDatabaseDateString(startDateParsed));
        } else if (endDateParsed != null) {
            subWhereClause.append("AND ce.start_date <= " + DateUtils.getDatabaseDateString(endDateParsed));
        }

        subWhereClause.append("AND che.calendar_id = c.calendar_id ");
        subWhereClause.append("AND ce.record_status = 'A' AND c.record_status = 'A' ");


        if (eventType == Integer.parseInt(EVENT) || eventType == EVENT_TYPE_ALL) {
            query.append("SELECT DISTINCT ce.calendar_event_id as id, ce.event_name, ce.start_date, ce.event_type_id ");
            query.append("FROM  pn_space_has_calendar shc, pn_calendar c, pn_calendar_has_event che, pn_calendar_event ce, pn_facility f ");
            query.append(subWhereClause.toString());
            query.append(" AND ce.event_type_id = 200 ");

        }
        if (eventType == EVENT_TYPE_ALL) {
            query.append(" union ");
        }
        if (eventType == Integer.parseInt(MEETING) || eventType == EVENT_TYPE_ALL) {
            query.append("SELECT DISTINCT m.meeting_id as id, ce.event_name, ce.start_date, ce.event_type_id ");
            query.append("FROM  pn_space_has_calendar shc, pn_calendar c, pn_calendar_has_event che, pn_calendar_event ce, pn_facility f, pn_meeting m ");
            query.append(subWhereClause.toString());
            query.append(" AND m.calendar_event_id = ce.calendar_event_id ");
            query.append(" AND ce.event_type_id = 100 ");
        }


        try {
            m_db.executeQuery(query.toString());

            while (m_db.result.next()) {
                String[] result_row = {m_db.result.getString(1),
                                       m_db.result.getString(2),
                                       m_user.getDateFormatter().formatDate((m_db.result.getDate(3))),
                                       m_db.result.getString(4)
                };
                m_results.add(result_row);
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(CalendarSearch.class).error("CalendarSearch.doAdvancedSearch() threw an SQL exception");
        } finally {
            m_db.release();
        }

        return;
    }

    /**
     * Generates a properly formatted XML string. The XML string will contain the results
     * from the search as well as the column headers for the two fields used to describe
     * each of the result objects.
     *
     * The XML should be in the following format:
     *
     * <channel>
     *   <table_def>
     *     <col>COLUMN1 NAME</col>
     *     <col>COLUMN2 NAME</col>
     *        .
     *        .
     *   </table_def>
     *   <content>                        --YOU MUST HAVE THE SAME NUMBER OF DATA
     *     <row>                          -- DATA TAGS IN EACH ROW AS COLUMNS
     *       <data>TEXT DATA</data>
     *       <data_href>                  --USE <DATA> AND <DATA_HREF> INTERCHANGABLY
     *         <label>TEXT DATA</label>
     *         <href>URL</href>
     *       </data_href>
     *        .
     *        .
     *     </row>
     *      .
     *      .
     *    </content>
     *  </channel>
     *
     * @return XML Formatted results of the search
     */
    public String getXMLResults() {
        StringBuffer sb = new StringBuffer();
        sb.append("<channel>\n");
        sb.append("<table_def>\n");
        sb.append("<col>" + PropertyProvider.get("prm.global.search.calendar.results.meetingeventname.column") + "</col>\n"); // Meeting/Event Name
        sb.append("<col>" + PropertyProvider.get("prm.global.search.calendar.results.startdate.column") + "</col>\n"); // Start Date
        sb.append("</table_def>\n");
        sb.append("<content>\n");

        for (int i = 0; i < m_results.size(); i++) {
            String id = ((String[])m_results.get(i))[0];
            String one = ((String[])m_results.get(i))[1];
            String two = ((String[])m_results.get(i))[2];
            String type = ((String[])m_results.get(i))[3];
            String href = "";

            if (type.equals(PnCalendar.EVENT)) {
                href = URLFactory.makeURL(id, ObjectType.EVENT);
            } else if (type.equals(PnCalendar.MEETING)) {
                href = URLFactory.makeURL(id, ObjectType.MEETING);
            } else {
                throw new ObjectTypeNotSupportedException("CalendarSearch.getXMLResults(int,int) " +
                    "encountered a type that it doesn't " +
                    "support: " + type);
            }

            sb.append("<row>\n");
            sb.append("<data_href>\n");
            sb.append("<label>" + XMLUtils.escape(one) + "</label>\n");
            sb.append("<href>" + href + "</href>\n");
            sb.append("<id>" + XMLUtils.escape(id) + "</id>");
            sb.append("</data_href>\n");
            sb.append("<data>" + XMLUtils.escape(two) + "</data>");
            sb.append("</row>\n");

        }
        sb.append("</content>\n");
        sb.append("</channel>\n");
        return sb.toString();
    }


    /**
     * Generates a properly formatted XML string. The XML string will contain the results
     * from the search as well as the column headers for the two fields used to describe
     * each of the result objects.
     *
     * The XML should be in the following format:
     *
     * <channel>
     *   <table_def>
     *     <col>COLUMN1 NAME</col>
     *     <col>COLUMN2 NAME</col>
     *        .
     *        .
     *   </table_def>
     *   <content>                        --YOU MUST HAVE THE SAME NUMBER OF DATA
     *     <row>                          -- DATA TAGS IN EACH ROW AS COLUMNS
     *       <data>TEXT DATA</data>
     *       <data_href>                  --USE <DATA> AND <DATA_HREF> INTERCHANGABLY
     *         <label>TEXT DATA</label>
     *         <href>URL</href>
     *       </data_href>
     *        .
     *        .
     *     </row>
     *      .
     *      .
     *    </content>
     *  </channel>
     *
     * @return XML Formatted results of the search
     */
    public String getXMLResults(int start, int end) {
        String href = "";
        String id;
        String one;
        String two;
        String type;

        if (m_results.size() < 1)
            return null;

        int m_start = start;
        int m_end = end;

        if (m_start > m_results.size())
            m_start = m_results.size();

        if (m_start < 1)
            m_start = 1;

        if (m_end > m_results.size())
            m_end = m_results.size();

        if (m_end < 1)
            m_end = 1;

        m_start--;
        m_end--;

        StringBuffer sb = new StringBuffer();
        sb.append("<channel>\n");
        sb.append("<table_def>\n");
        sb.append("<col>" + PropertyProvider.get("prm.global.search.calendar.results.eventname.column") + "</col>\n"); // Event Name
        sb.append("<col>" + PropertyProvider.get("prm.global.search.calendar.results.startdate.column") + "</col>\n"); // Start Date
        sb.append("</table_def>\n");
        sb.append("<content>\n");

        for (int i = m_start; i <= m_end; i++) {
            id = ((String[])m_results.get(i))[0];
            one = ((String[])m_results.get(i))[1];
            two = ((String[])m_results.get(i))[2];
            type = ((String[])m_results.get(i))[3];

            if (type.equals(PnCalendar.EVENT)) {
                href = URLFactory.makeURL(id, ObjectType.EVENT);
            } else if (type.equals(PnCalendar.MEETING)) {
                href = URLFactory.makeURL(id, ObjectType.MEETING);
            } else {
                throw new ObjectTypeNotSupportedException("CalendarSearch.getXMLResults(int,int) " +
                    "encountered a type that it doesn't " +
                    "support: " + type);
            }

            sb.append("<row>\n");
            sb.append("<data_href>\n");
            sb.append("<label>" + XMLUtils.escape(one) + "</label>\n");
            sb.append("<href>" + href + "</href>\n");
            sb.append("<id>" + XMLUtils.escape(id) + "</id>");
            sb.append("</data_href>\n");
            sb.append("<data>" + XMLUtils.escape(two) + "</data>");
            sb.append("</row>\n");

        }
        sb.append("</content>\n");
        sb.append("</channel>\n");
        return sb.toString();


    }


    /**
     * Will return the number of results found by the search.
     *
     * @return the number of results found by the search.
     */
    public int getResultCount() {
        return m_results.size();
    }

    /**
     * Returns the default search type, used to determine which should be
     * presented to the user by default.
     *
     * @return the search type
     * @see #SIMPLE_SEARCH
     * @see #ADVANCED_SEARCH
     * @see #BROWSE_SEARCH
     */
    public int getDefaultSearchType() {
        return SIMPLE_SEARCH;
    }

    /**
     * Indicates whether a particular search type is supported, used to determine
     * which search type options are presented to the user.
     *
     * @param searchType search type constant
     * @return true if the search type is supported, false otherwise
     * @see #SIMPLE_SEARCH
     * @see #ADVANCED_SEARCH
     * @see #BROWSE_SEARCH
     */
    public boolean isSearchTypeSupported(int searchType) {
        if (searchType == SIMPLE_SEARCH ||
            searchType == ADVANCED_SEARCH) {
            return true;
        }
        return false;
    }

    /**
     * Returns the facility types in the system, loading if necessary.
     *
     * @return the facility types
     */
    private List getFacilityTypes() {
        if (this.facilityTypes == null) {
            loadFacilityTypes();
        }
        return this.facilityTypes;
    }

    /**
     * Load the facility types.
     */
    private void loadFacilityTypes() {
        this.facilityTypes = new ArrayList();
        FacilityTypeDomain domain = new FacilityTypeDomain();
        domain.load();
        facilityTypes.addAll(domain.getCodes());
    }


    /**
     * This method has been added to circumvent ambiguous Object Model and
     * Database model between Calendar Event and Meeting . The method should be
     * removed in the future as a part of refactoring . The method assumes that
     * Meeting "is a" Calendar Event which is what the object Model suggests.
     *
     * @return The meeting ID
     */
    private static String getMeetingID(String eventID) {

        DBBean db = new DBBean();
        String meetingID = null;
        StringBuffer query = new StringBuffer();

        query.append("select meeting_id from pn_meeting where calendar_event_id = ? ");

        try {
            db.prepareStatement(query.toString());

            int index = 0;

            db.pstmt.setString(++index, eventID);

            db.executePrepared();

            if (db.result.next()) {
                meetingID = db.result.getString("meeting_id");
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(CalendarSearch.class).error("CalendarSearch.getMeetingID() threw an SQL exception for event " + eventID);
        } finally {
            db.release();
        }

        return meetingID;
    }

}
