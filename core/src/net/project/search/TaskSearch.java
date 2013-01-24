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

import javax.servlet.http.HttpServletRequest;

import net.project.base.ObjectType;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.schedule.TaskType;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.DateUtils;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * This class is used for searching Schedule Objects.  It provides the forms for
 * simple and advanced searching as well as a properly formatted XML string of
 * the results.
 *
 * @author Mike Brevoort
 * @since 04/00
 */
public class TaskSearch extends GenericSearch {
    // a few internal constants used for searching options -mdb

    /** Indicates a Task or Summary task search. */
    private static final int TASK = 0;

    /** Indicates a milestone search. */
    private static final int MILESTONE = 1;

    /** Defines the IDs of task types to look for. */
    private static final String[] TASKTYPE_ARRAY = {TaskType.SUMMARY.getID().toUpperCase(), TaskType.MILESTONE.getID().toUpperCase()};

    /**
     * Creates a new instance of <code>TaskSearch</code>.
     */
    public TaskSearch() {
        super();
    }

    /**
     * This is used to set the type of search context, that the object will
     * perform its doSearch() and getSearchForm() methods in.
     *
     * @param type one of the enumerations of the search types
     */
    public void setSearchType(int type) {
        this.search_type = type;
        return;
    }

    /**
     * Get the display name of the this search Type.
     */
    public String getDisplayName() {
        return PropertyProvider.get("prm.global.search.task.results.channel.title"); // Task
    }

    /**
     * This returns a properly formated HTML form for performing the type of
     * search that has been set by setSearchType(). This will be called from a
     * JSP page for the user to search for a specific object.
     *
     * @return the HTML Form UI for doing a simple search.
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
	 * for a specific object. It does not include the opening and closing <FORM>
	 * tags. This is so you specify where it is posting to and add any necessary
	 * hidden form fields. It also tries to restore field values from the
	 * request.
	 * 
	 * @return the HTML Form UI for doing a simple search
	 */
    public String getSimpleSearchForm(String formName, HttpServletRequest request) {
    	
    	String nameInRequest = getParameterSafe(request, "NAME");
    	String taskTypeInRequest = getParameterSafe(request, "TASKTYPE");
    	
        StringBuffer formString = new StringBuffer();

        formString.append("<TABLE  border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"500\">");

        formString.append("\n<tr>");
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-left_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n<td class=\"channelHeader\" width=98% colspan=2 align=left NOWRAP>" + PropertyProvider.get("prm.global.search.task.simple.title") + "</td>"); // Simple Schedule Search:
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-right_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n</tr>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.task.simple.namecontains.label") + "&nbsp;&nbsp;</TD>"); // Name Contains:
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"NAME\" VALUE=\""+nameInRequest+"\">");
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.task.simple.tasktype.label") + "&nbsp;&nbsp;</TD><TD>"); // Task Type:
        formString.append("<SELECT NAME=\"TASKTYPE\"><OPTION VALUE=\""
            + TASK + "\" "+getOptionSelection(TASK,taskTypeInRequest)+">" + PropertyProvider.get("prm.global.search.task.simple.option.task.name") + "<OPTION VALUE=\"" // Task
            + MILESTONE + "\" "+getOptionSelection(MILESTONE,taskTypeInRequest)+">" + PropertyProvider.get("prm.global.search.task.simple.option.milestone.name") + "</SELECT>"); // Milestone
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("</TABLE>");

        return formString.toString();
    }

    /**
     * This returns a properly formatted HTML form for performing an advanced
     * search on the object. This will be called from a JSP page for the user to
     * search for a specific object.It also tries to restore field values from
     * the request.
     *
     * @return the HTML Form UI for doing an advanced search.
     */
    public String getAdvancedSearchForm(String formName, HttpServletRequest request) {
    	
    	String nameInRequest = getParameterSafe(request, "NAME");
    	String taskTypeInRequest = getParameterSafe(request, "TASKTYPE");
    	String startDateInRequest = getParameterSafe(request, "STARTDATE");
    	String endDateInRequest = getParameterSafe(request, "ENDDATE");
    	
        StringBuffer formString = new StringBuffer();

        formString.append("<TABLE  border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"500\">");
        formString.append("\n<tr>");
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-left_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n<td class=\"channelHeader\" width=98% colspan=2 align=left NOWRAP>" + PropertyProvider.get("prm.global.search.task.advanced.title") + "</td>"); // Advanced Schedule Search:
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-right_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n</tr>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.task.advanced.namecontains.label") + "&nbsp;&nbsp;</TD>"); // Name Contains:
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"NAME\" VALUE=\""+nameInRequest+"\">");
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.task.advanced.tasktype.label") + "&nbsp;&nbsp;</TD>"); // Task Type:
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<SELECT NAME=\"TASKTYPE\"><OPTION VALUE=\""
            + TASK + "\" "+getOptionSelection(TASK,taskTypeInRequest)+">" + PropertyProvider.get("prm.global.search.task.advanced.option.task.name") + "<OPTION VALUE=\"" // Task
            + MILESTONE + "\" "+getOptionSelection(MILESTONE,taskTypeInRequest)+">" + PropertyProvider.get("prm.global.search.task.advanced.option.milestone.name") + "</SELECT>"); // Milestone
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.task.advanced.afterdate.label", DateFormat.getInstance().getDateFormatExample()) + "&nbsp;&nbsp;</TD>"); // After Date (mm/dd/yyyy):
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"STARTDATE\" VALUE=\""+startDateInRequest+"\" MAXLENGTH=\"10\">");
        formString.append("<a href=\"javascript:autoDate('STARTDATE','"+SessionManager.getJSPRootURL()+"','"+formName+"')\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/calendar.gif\" width=\"16\" align=\"middle\" height=\"16\" border=\"0\"></a>");
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.task.advanced.beforedate.label", DateFormat.getInstance().getDateFormatExample()) + "&nbsp;&nbsp;</TD>"); // Before Date (mm/dd/yyyy):
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"ENDDATE\" VALUE=\""+endDateInRequest+"\" MAXLENGTH=\"10\">");
        formString.append("<a href=\"javascript:autoDate('ENDDATE','"+SessionManager.getJSPRootURL()+"','"+formName+"')\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/calendar.gif\" width=\"16\" align=\"middle\" height=\"16\" border=\"0\"></a>");
        formString.append("</TD><td>&nbsp;</td></TR>");

        formString.append("</TABLE>");

        return formString.toString();
    }


    /**
     * This will actually search the database for the objects matching the
     * criteria the user entered into the form generated by getSearchForm()
     * method. It retrieves this criteria from the HttpServletRequest object
     * that gets passed into it.  It will then store these results somewhere
     * internally in the class, so that they can be used by the getXMLResults()
     * method.
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
     * This will actually search the database for the object matching the passed
     * in keyword.  It will then store these results somewhere internally in the
     * class, so that they can be used by the getXMLResults() method.
     *
     * @param keyword the keyword to search on.
     */
    public void doKeywordSearch(String keyword) {
        StringBuffer query = new StringBuffer();

        m_results = new ArrayList();

        query.append("SELECT pn_task.task_id, pn_task.task_name, pn_task.task_desc ");
        query.append("FROM pn_task, pn_plan_has_task, pn_space_has_plan ");
        query.append("WHERE pn_space_has_plan.space_id = ");
        query.append(getFirstSpaceID() + " ");
        query.append("AND pn_space_has_plan.plan_id = pn_plan_has_task.plan_id ");
        query.append("AND pn_plan_has_task.task_id = pn_task.task_id ");
        query.append("AND pn_task.record_status = 'A' ");
        if (keyword != null && keyword.length() > 0) {
            query.append("AND (UPPER(pn_task.task_name) LIKE UPPER('%" + keyword + "%') ");
            query.append("OR UPPER(pn_task.task_desc) LIKE UPPER('%" + keyword + "%') )");
        }

        try {
            m_db.executeQuery(query.toString());

            while (m_db.result.next()) {
                String[] result_row = {
                    m_db.result.getString(1),
                    m_db.result.getString(2),
                    m_db.result.getString(3)
                };
                m_results.add(result_row);
            }
            m_db.release();
        } catch (SQLException sqle) {
        	Logger.getLogger(TaskSearch.class).error("TaskSearch.doSimpleSearch() threw an SQL exception");
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
        int tasktype = 0;

        m_results = new ArrayList();
        String name = m_request.getParameter("NAME");
        tasktype = Integer.parseInt(m_request.getParameter("TASKTYPE"));

        query.append("SELECT pn_task.task_id, pn_task.task_name, pn_task.task_desc ");
        query.append("FROM pn_task, pn_plan_has_task, pn_space_has_plan ");
        query.append("WHERE pn_space_has_plan.space_id = ");
        query.append(getFirstSpaceID() + " ");
        query.append("AND pn_space_has_plan.plan_id = pn_plan_has_task.plan_id ");
        query.append("AND pn_plan_has_task.task_id = pn_task.task_id ");

        if (name != null && name.length() > 0) {
            query.append("AND UPPER(pn_task.task_name) LIKE UPPER('%" + name + "%') ");
        }

        if (tasktype == TASK) {
            // We want tasks and summary tasks
            query.append("AND (UPPER(pn_task.task_type) = UPPER('" + TaskType.SUMMARY.getID().toUpperCase() + "') ");
            query.append("OR UPPER(pn_task.task_type) = UPPER('" + TaskType.TASK.getID().toUpperCase() + "')) ");
            // Tasks only
            query.append("AND pn_task.is_milestone = 0 ");

        } else {
            // Milestones only
            query.append("AND pn_task.is_milestone = 1 ");
        }

        query.append("AND pn_task.record_status = 'A'");

        try {
            m_db.executeQuery(query.toString());

            while (m_db.result.next()) {
                String[] result_row = {
                    m_db.result.getString(1),
                    m_db.result.getString(2),
                    m_db.result.getString(3)
                };
                m_results.add(result_row);
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(TaskSearch.class).error("TaskSearch.doSimpleSearch() threw an SQL exception");
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
        
        // clear error messages
		searchErrors.clear();
        
        int tasktype = 0;
        String startDate = null;
        String endDate = null;


        m_results = new ArrayList();
        String name = m_request.getParameter("NAME");
        tasktype = Integer.parseInt(m_request.getParameter("TASKTYPE"));
        startDate = m_request.getParameter("STARTDATE");
        endDate = m_request.getParameter("ENDDATE");

        //Try to parse the start and end dates to see if they are valid.
        Date startDateParsed = null;
        Date endDateParsed = null;
        DateFormat df = DateFormat.getInstance();

        if (!Validator.isBlankOrNull(startDate)) {
            try {
                startDateParsed = df.parseDateString(startDate);
            } catch (net.project.util.InvalidDateException e) {
                String message = PropertyProvider.get("prm.global.search.task.advanced.afterdate.invalid.message", startDate);
                searchErrors.add(message);
            }
        }

        if (!Validator.isBlankOrNull(endDate)) {
            try {
                endDateParsed = df.parseDateString(endDate);
            } catch (net.project.util.InvalidDateException e) {
                String message = PropertyProvider.get("prm.global.search.task.advanced.beforedate.invalid.message", endDate);
                searchErrors.add(message);
            }
        }
        
        if(startDateParsed != null && endDateParsed != null && startDateParsed.after(endDateParsed)){
            String message = PropertyProvider.get("prm.global.search.advanced.daterange.invalid.message");
            searchErrors.add(message);
        }
        
		// stop processing if we already have errors
		if (searchErrors.size() > 0)
			return;

        query.append("SELECT pn_task.task_id, pn_task.task_name, pn_task.task_desc ");
        query.append("FROM pn_task, pn_plan_has_task, pn_space_has_plan ");
        query.append("WHERE pn_space_has_plan.space_id = ");
        query.append(getFirstSpaceID() + " ");
        query.append("AND pn_space_has_plan.plan_id = pn_plan_has_task.plan_id ");
        query.append("AND pn_plan_has_task.task_id = pn_task.task_id ");

        if (tasktype == TASK) {
            // We want tasks and summary tasks
            query.append("AND (UPPER(pn_task.task_type) = UPPER('" + TaskType.SUMMARY.getID().toUpperCase() + "') ");
            query.append("OR UPPER(pn_task.task_type) = UPPER('" + TaskType.TASK.getID().toUpperCase() + "')) ");

        } else {
            // Milestones only
            query.append("AND pn_task.is_milestone = 1 ");
        }

        if (name != null && name.length() > 0) {
            query.append("AND UPPER(pn_task.task_name) LIKE UPPER('%" + name + "%') ");
        }

        if (startDateParsed != null && endDateParsed != null) {
            query.append("AND (");
            query.append("( pn_task.date_start <= " + DateUtils.getDatabaseDateString(startDateParsed));
            query.append("AND pn_task.date_finish >= " + DateUtils.getDatabaseDateString(startDateParsed)+" ) ");
            query.append(" OR ");
            query.append("( pn_task.date_start <= " + DateUtils.getDatabaseDateString(endDateParsed));
            query.append("AND pn_task.date_finish >= " + DateUtils.getDatabaseDateString(endDateParsed) + " ) ");
            query.append(" OR ");
            query.append("( pn_task.date_start > " + DateUtils.getDatabaseDateString(startDateParsed));
            query.append("AND pn_task.date_finish < " + DateUtils.getDatabaseDateString(endDateParsed)+" ) ");
            query.append(") ");
        } else if (startDateParsed != null) {
            query.append("AND pn_task.date_finish >= " + DateUtils.getDatabaseDateString(startDateParsed));
        } else if (endDateParsed != null) {
            query.append("AND pn_task.date_start <= " + DateUtils.getDatabaseDateString(endDateParsed));
        }

        query.append("AND pn_task.record_status = 'A'");

        try {
            m_db.executeQuery(query.toString());

            while (m_db.result.next()) {
                String[] result_row = {m_db.result.getString(1),
                                       m_db.result.getString(2),
                                       m_db.result.getString(3)};
                m_results.add(result_row);

            }
        } catch (SQLException sqle) {
        	Logger.getLogger(TaskSearch.class).error("TaskSearch.doAdvancedSearch() threw an SQL exception");
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
        sb.append("<col>" + PropertyProvider.get("prm.global.search.task.results.name.column") + "</col>\n"); // Event
        sb.append("<col>" + PropertyProvider.get("prm.global.search.task.results.description.column") + "</col>\n"); // Description
        sb.append("</table_def>\n");
        sb.append("<content>\n");

        for (int i = 0; i < m_results.size(); i++) {
            String id = ((String[])m_results.get(i))[0];
            String one = ((String[])m_results.get(i))[1];
            String two = ((String[])m_results.get(i))[2];
            String href = URLFactory.makeURL(id, ObjectType.TASK);
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
        sb.append("<col>" + PropertyProvider.get("prm.global.search.task.results.name.column") + "</col>\n"); // Event
        sb.append("<col>" + PropertyProvider.get("prm.global.search.task.results.description.column") + "</col>\n"); // Description
        sb.append("</table_def>\n");
        sb.append("<content>\n");

        for (int i = m_start; i <= m_end; i++) {
            String id = ((String[])m_results.get(i))[0];
            String one = ((String[])m_results.get(i))[1];
            String two = ((String[])m_results.get(i))[2];
            String href = URLFactory.makeURL(id, ObjectType.TASK);
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
     * @param searchType the search type constant.
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

}
