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
|   $Revision: 20776 $
|       $Date: 2010-04-30 09:24:27 -0300 (vie, 30 abr 2010) $
|     $Author: uroslates $
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
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.DateUtils;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * This class is used for searching Post Objects.  It provides the forms for simple
 * and advanced searching as well as a properly formatted XML string of the results.
 *
 * @author  Mike Brevoort
 * @since 04/00
 */
public class PostSearch extends GenericSearch {
    // a few internal constants used for searching options -mdb
    private static final String DATE_SORT = "0";
    private static final String AUTHOR_SORT = "1";
    private static final String SUBJECT_SORT = "2";

    private static final String[] SORT_BY_ARRAY = {"pn_post.date_posted", "pn_person.last_name", "pn_post.subject"};


    public PostSearch() {
        super();
    }

    /**
     * This is used to set the type of search context, that the object will perform it's
     * doSearch() and getSearchForm() methods in.
     *
     * @param type one of the enumerations of the search types
     */
    public void setSearchType(int type) {
        this.search_type = type;
        return;
    }

    /**
     * Get the display name of the this search Type
     */
    public String getDisplayName() {
        return PropertyProvider.get("prm.global.search.discussion.results.channel.title"); // Discussion
    }

    /**
     * This returns a properly formated HTML form for performing the type of search that has
     * been set by setSearchType(). This will be called from a JSP page for the user
     * to search for a specific object.
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
	 * for a specific object. It does not include the opening and closing <FORM>
	 * tags. This is so you specify where it is posting to and add any necessary
	 * hidden form fields. It also tries to restore field values from the
	 * request.
	 * 
	 * @return the HTML Form UI for doing a simple search
	 */
    public String getSimpleSearchForm(String formName, HttpServletRequest request) {
        StringBuffer formString = new StringBuffer();
        
        String nameInRequest = getParameterSafe(request, "NAME");
    	String subjectInRequest = getParameterSafe(request, "SUBJECT");
        
        formString.append("<TABLE  border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"500\">");
        formString.append("\n<tr>");
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-left_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n<td class=\"channelHeader\" width=98% colspan=2 align=left NOWRAP>" + PropertyProvider.get("prm.global.search.discussion.simple.title") + "</td>"); // Simple Post Search:
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-right_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n</tr>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.discussion.simple.authorcontains.label") + "&nbsp;&nbsp;</TD>"); // Author Contains:
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"NAME\" VALUE=\""+nameInRequest+"\">");
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.discussion.simple.subjectcontains.label") + "&nbsp;&nbsp;</TD>"); // Subject Contains:
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"SUBJECT\" VALUE=\""+subjectInRequest+"\">");
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("</TABLE>");

        return formString.toString();
    }

    /**
     * This returns a properly formatted HTML form for performing an advanced search on the
     * object. This will be called from a JSP page for the user to search for a specific
     * object.It also tries to restore field values from the request.
     *
     * @return the HTML Form UI for doing an advanced search
     */
    public String getAdvancedSearchForm(String formName, HttpServletRequest request) {
        StringBuffer formString = new StringBuffer();

        String nameInRequest = getParameterSafe(request, "NAME");
    	String subjectInRequest = getParameterSafe(request, "SUBJECT");
    	String startDateInRequest = getParameterSafe(request, "STARTDATE");
    	String endDateInRequest = getParameterSafe(request, "ENDDATE");
    	String sort1InRequest = getParameterSafe(request, "SORT1");
    	String sort2InRequest = getParameterSafe(request, "SORT2");
        
        formString.append("<TABLE  border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"500\">");
        formString.append("\n<tr>");
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-left_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n<td class=\"channelHeader\" width=98% colspan=2 align=left NOWRAP>" + PropertyProvider.get("prm.global.search.discussion.advanced.title") + "</td>"); // Advanced Post Search:
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-right_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n</tr>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.discussion.advanced.authorcontains.label") + "&nbsp;</TD>"); // Author Contains:
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"NAME\" VALUE=\""+nameInRequest+"\">");
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.discussion.advanced.subjectcontains.label") + "&nbsp;&nbsp;</TD>"); // Subject Contains:
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"SUBJECT\" VALUE=\""+subjectInRequest+"\">");
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.discussion.advanced.afterdate.label", DateFormat.getInstance().getDateFormatExample()) + "&nbsp;&nbsp;</TD>"); // After Date (mm/dd/yyyy):
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"STARTDATE\" VALUE=\""+startDateInRequest+"\" MAXLENGTH=\"10\">");
        formString.append("<a href=\"javascript:autoDate('STARTDATE','"+SessionManager.getJSPRootURL()+"','"+formName+"')\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/calendar.gif\" width=\"16\" align=\"middle\" height=\"16\" border=\"0\"></a>");
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.discussion.advanced.beforedate.label", DateFormat.getInstance().getDateFormatExample()) + "&nbsp;&nbsp;</TD>"); // Before Date (mm/dd/yyyy):
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"ENDDATE\" VALUE=\""+endDateInRequest+"\" MAXLENGTH=\"10\">");
        formString.append("<a href=\"javascript:autoDate('ENDDATE','"+SessionManager.getJSPRootURL()+"','"+formName+"')\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/calendar.gif\" width=\"16\" align=\"middle\" height=\"16\" border=\"0\"></a>");
        formString.append("</TD><td>&nbsp;</td></TR>");

        formString.append("<TR  align=\"left\" valign=\"middle\"><td>&nbsp;</td>");
        formString.append("<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.discussion.advanced.sortresultsonfirst.label") + "&nbsp;&nbsp;</TD><TD class=\"tableContent\">"); // Sort Results On First:
        formString.append("<SELECT NAME=\"SORT1\"><OPTION VALUE=\""
            + DATE_SORT + "\" "+getOptionSelection(DATE_SORT,sort1InRequest)+">" + PropertyProvider.get("prm.global.search.discussion.advanced.option.date.name") + "<OPTION VALUE=\"" // Date
            + SUBJECT_SORT + "\" "+getOptionSelection(SUBJECT_SORT,sort1InRequest)+">" + PropertyProvider.get("prm.global.search.discussion.advanced.option.subject.name") + "<OPTION VALUE=\"" // Subject
            + AUTHOR_SORT + "\" "+getOptionSelection(AUTHOR_SORT,sort1InRequest)+">" + PropertyProvider.get("prm.global.search.discussion.advanced.option.authorslastname.name") + "</SELECT></TD><td>&nbsp;</td></TR>"); // Author's Last Name
        formString.append("<TR  align=\"left\" valign=\"middle\"><td>&nbsp;</td>");
        formString.append("<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.discussion.advanced.sortresultsonsecond.label") + "&nbsp;&nbsp;</TD><TD class=\"tableContent\">"); // Sort Results On Second:
        formString.append("<SELECT NAME=\"SORT2\"><OPTION VALUE=\""
            + SUBJECT_SORT + "\" "+getOptionSelection(SUBJECT_SORT,sort2InRequest)+">" + PropertyProvider.get("prm.global.search.discussion.advanced.option.subject.name") + "<OPTION VALUE=\"" // Subject
            + DATE_SORT + "\" "+getOptionSelection(DATE_SORT,sort2InRequest)+">" + PropertyProvider.get("prm.global.search.discussion.advanced.option.date.name") + "<OPTION VALUE=\""  // Date
            + AUTHOR_SORT + "\" "+getOptionSelection(AUTHOR_SORT,sort2InRequest)+">" + PropertyProvider.get("prm.global.search.discussion.advanced.option.authorslastname.name") + "</SELECT></TD><td>&nbsp;</td></TR>"); // Author's Last Name

        formString.append("</TABLE>");

        return formString.toString();

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

        query.append("SELECT pn_post.post_id, pn_post.subject, pn_person.display_name, pn_post.date_posted, pn_post.discussion_group_id ");
        query.append("FROM pn_post, pn_person, pn_object_has_discussion, pn_discussion_group, pn_post_body_clob pc ");
        query.append("WHERE pn_object_has_discussion.object_id = ");
        query.append(getFirstSpaceID() + " ");
        query.append("AND pc.object_id = pn_post.post_body_id ");
        query.append("AND pn_post.discussion_group_id = pn_object_has_discussion.discussion_group_id ");
        query.append("AND pn_post.person_id = pn_person.person_id ");
        query.append("AND pn_discussion_group.discussion_group_id = pn_post.discussion_group_id ");
        query.append("AND pn_post.record_status = 'A' ");
        query.append("AND pn_discussion_group.record_status = 'A' ");

        if (keyword != null && keyword.length() > 0) {
            query.append("AND (UPPER(pn_person.display_name) LIKE UPPER('%" + keyword + "%') ");
            query.append("OR UPPER(pn_post.subject) LIKE UPPER('%" + keyword + "%') ");
            query.append("OR BASE.CLOB_LIKE(pc.clob_field, '%" + keyword + "%', 0) > 0 )");
        }

        try {
            m_db.executeQuery(query.toString());

            while (m_db.result.next()) {
                // Construct the data elements to be drawn
                // We must format the date at this point since producing the
                // search XML is a generic operation that is unaware of any
                // datatype
                String[] result_row = {m_db.result.getString(1),
                                       m_db.result.getString(2),
                                       m_db.result.getString(3),
                                       getUser().getDateFormatter().formatDateTime(m_db.result.getTimestamp(4)),
                                       m_db.result.getString(5)};
                m_results.add(result_row);

            }
            m_db.release();
        } catch (SQLException sqle) {
        	Logger.getLogger(PostSearch.class).error("PostSearch.doAdvancedSearch() threw an SQL exception");
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
        String name = null;
        String subject = null;
        String objId = m_request.getParameter("objectId");

        m_results = new ArrayList();
        name = m_request.getParameter("NAME");
        subject = m_request.getParameter("SUBJECT");

        query.append("SELECT pn_post.post_id, pn_post.subject, pn_person.display_name, pn_post.date_posted, pn_post.discussion_group_id ");
        query.append("FROM pn_post, pn_person, pn_object_has_discussion, pn_discussion_group ");
        query.append("WHERE pn_object_has_discussion.discussion_group_id IN ( ");
        query.append("	(SELECT pn_object_has_discussion.discussion_group_id FROM pn_object_has_discussion");
        query.append("	 	WHERE pn_object_has_discussion.object_id = ").append(getFirstSpaceID() + ") ");
        if(objId != null) {		// is Number
        	query.append(", " + objId).append(" ) AND ");	// end IN block
        } else {
	        query.append(") AND ");							// end IN block
        }
        query.append("pn_object_has_discussion.discussion_group_id = pn_post.discussion_group_id AND ");
        query.append("pn_post.discussion_group_id = pn_discussion_group.discussion_group_id AND ");
        query.append("pn_post.person_id = pn_person.person_id AND ");        
        query.append("pn_post.record_status = 'A' AND ");
        query.append("pn_discussion_group.record_status = 'A' ");
        if (name != null && name.length() > 0) {
            query.append("AND UPPER(pn_person.display_name) LIKE UPPER('%" + name + "%') ");
        }
        if (subject != null && subject.length() > 0) {
            query.append("AND UPPER(pn_post.subject) LIKE UPPER('%" + subject + "%') ");
        }

        try {
            m_db.executeQuery(query.toString());

            while (m_db.result.next()) {
                String[] result_row = {m_db.result.getString(1),
                                       m_db.result.getString(2),
                                       m_db.result.getString(3),
                                       getUser().getDateFormatter().formatDateTime(m_db.result.getTimestamp(4)),
                                       m_db.result.getString(5)};
                m_results.add(result_row);

            }
            m_db.release();
        } catch (SQLException sqle) {
        	Logger.getLogger(PostSearch.class).error("PostSearch.doSimpleSearch() threw an SQL exception");
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
        
        String startDate = null;
        String endDate = null;
        int sort1Index = Integer.parseInt(DATE_SORT);
        int sort2Index = Integer.parseInt(SUBJECT_SORT);
        Date startDateParsed = null;
        Date endDateParsed = null;

        m_results = new ArrayList();
        String name = m_request.getParameter("NAME");
        String subject = m_request.getParameter("SUBJECT");
        startDate = m_request.getParameter("STARTDATE");
        endDate = m_request.getParameter("ENDDATE");
        sort1Index = Integer.parseInt(m_request.getParameter("SORT1"));
        sort2Index = Integer.parseInt(m_request.getParameter("SORT2"));
        String objId = m_request.getParameter("objectId");

        DateFormat df = DateFormat.getInstance();
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
		if (searchErrors.size() > 0)
			return;

        query.append("SELECT pn_post.post_id, pn_post.subject, pn_person.display_name, pn_post.date_posted, pn_post.discussion_group_id ");
        query.append("FROM pn_post, pn_person, pn_object_has_discussion, pn_discussion_group ");
        query.append("WHERE pn_object_has_discussion.discussion_group_id IN ( ");
        query.append("	(SELECT pn_object_has_discussion.discussion_group_id FROM pn_object_has_discussion");
        query.append("	 	WHERE pn_object_has_discussion.object_id = ").append(getFirstSpaceID() + ") ");
        if(objId != null) {		// is Number
        	query.append(", " + objId).append(" ) AND ");	// end IN block
        } else {
        	query.append(") AND ");							// end IN block        	
        }
        query.append("pn_object_has_discussion.discussion_group_id = pn_post.discussion_group_id AND ");
        query.append("pn_post.discussion_group_id = pn_discussion_group.discussion_group_id AND ");
        query.append("pn_post.person_id = pn_person.person_id AND ");
        query.append("pn_post.record_status = 'A' AND ");
        query.append("pn_discussion_group.record_status = 'A' ");

        if (name != null && name.length() > 0) {
            query.append("AND UPPER(pn_person.display_name) LIKE UPPER('%" + name + "%') ");
        }
        if (subject != null && subject.length() > 0) {
            query.append("AND UPPER(pn_post.subject) LIKE UPPER('%" + subject + "%') ");
        }
        if (startDateParsed != null) {
            query.append("AND pn_post.date_posted >= " + DateUtils.getDatabaseDateString(startDateParsed));
        }
        if (endDateParsed != null) {
            query.append("AND pn_post.date_posted <= " + DateUtils.getDatabaseDateString(endDateParsed));
        }

        query.append("ORDER BY " + SORT_BY_ARRAY[sort1Index] + ", " + SORT_BY_ARRAY[sort2Index]);

        try {
            m_db.executeQuery(query.toString());

            while (m_db.result.next()) {
                String[] result_row = {m_db.result.getString(1),
                                       m_db.result.getString(2),
                                       m_db.result.getString(3),
                                       getUser().getDateFormatter().formatDateTime(m_db.result.getTimestamp(4)),
                                       m_db.result.getString(5)};
                m_results.add(result_row);

            }
            m_db.release();
        } catch (SQLException sqle) {
        	Logger.getLogger(PostSearch.class).error("PostSearch.doAdvancedSearch() threw an SQL exception");
        } finally {
            m_db.release();
        }

        return;

    }

    /**
     * Generates a properly formatted XML string. The XML string will contain the results
     * from the search as well as the column headers for the two fields used to describe
     * each of the result objects.
     * @return XML Formatted results of the search
     */
    public String getXMLResults() {
        return getXMLResults(1, m_results.size());
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
     * @param start first result to return.  Results are numbered from 1.
     * @param end the last result to return; if this exceeds the number of
     * results, the last available result is returned
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
        sb.append("<col>" + PropertyProvider.get("prm.global.search.discussion.results.subject.column") + "</col>\n"); // Subject
        sb.append("<col>" + PropertyProvider.get("prm.global.search.discussion.results.from.column") + "</col>\n"); // From
        sb.append("<col>" + PropertyProvider.get("prm.global.search.discussion.results.date.column") + "</col>\n"); // Date
        sb.append("</table_def>\n");
        sb.append("<content>\n");

        for (int i = m_start; i <= m_end; i++) {
            String id = ((String[])m_results.get(i))[0];
            String one = ((String[])m_results.get(i))[1];
            String two = ((String[])m_results.get(i))[2];
            String three = ((String[])m_results.get(i))[3];
            String postGroupID = ((String[])m_results.get(i))[4];
            String href = "/discussion/GroupView.jsp?module=" + net.project.base.Module.DISCUSSION + 
            			  "&amp;action=" + net.project.security.Action.VIEW + 
            			  "&amp;id=" + postGroupID + "&amp;postid=" + id;
            
            sb.append("<row>\n");
            sb.append("<data_href>\n");
            sb.append("<label>" + XMLUtils.escape(one) + "</label>\n");
            sb.append("<href>" + href + "</href>\n");
            sb.append("<id>" + XMLUtils.escape(id) + "</id>");
            sb.append("</data_href>\n");
            sb.append("<data>" + XMLUtils.escape(two) + "</data>");
            sb.append("<data>" + XMLUtils.escape(three) + "</data>");
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
     * @param searchType the search type constant
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
