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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 18:17:28 +0530 (Fri, 21 Nov 2008) $
|   $Author: umesha $
+--------------------------------------------------------------------------------------*/
package net.project.search;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import net.project.base.ObjectType;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;
import net.project.util.StringUtils;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;


public class BlogSearch extends GenericSearch {
    /**
     * Creates a new instance of <code>BlogSearch</code>.
     */
    public BlogSearch() {
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
     * Get the display name of the this search Type.
     */
    public String getDisplayName() {
        return PropertyProvider.get("prm.global.search.blog.results.channel.title"); // Blog
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
     * This returns a properly formated HTML form for performing a simple search on the
     * object. This will be called from a JSP page for the user to search for a specific
     * object.  It does not include the opening and closing <FORM> tags.  This is so you
     * specify where it is posting to and add any necessary hidden form fields.
     *
     * @return the HTML Form UI for doing a simple search
     */
    public String getSimpleSearchForm(String formName, HttpServletRequest request) {
        StringBuffer formString = new StringBuffer();

        formString.append("<TABLE  border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"500\">");

        formString.append("\n<tr>");
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-left_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n<td class=\"channelHeader\" width=98% colspan=2 align=left NOWRAP>" + PropertyProvider.get("prm.global.search.blog.simple.title") + "</td>"); // Simple Blog Search:
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-right_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n</tr>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.blog.simple.namedescription.label") + "&nbsp;&nbsp;</TD>"); // Name or Description:
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"KEYWORD\">");
        formString.append("</TD><td>&nbsp;</td></TR></TABLE>");

        return formString.toString();
    }

    /**
     * This returns a properly formatted HTML form for performing an advanced search on the
     * object. This will be called from a JSP page for the user to search for a specific
     * object.
     *
     * @return the HTML Form UI for doing an advanced search
     */
    public String getAdvancedSearchForm(String formName, HttpServletRequest request) {
        StringBuffer formString = new StringBuffer();

        formString.append("<TABLE  border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"500\">");

        formString.append("\n<tr>");
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-left_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n<td class=\"channelHeader\" width=98% colspan=2 align=left NOWRAP>" + PropertyProvider.get("prm.global.search.blog.advanced.title") + "</td>"); // Advanced Blog Search:
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-right_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n</tr>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.blog.advanced.subject.label") + "&nbsp;&nbsp;</TD>"); // Name:
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"NAME\">");
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.blog.advanced.message.label") + "&nbsp;&nbsp;</TD>"); // Description:
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"DESC\">");
        formString.append("</TD><td>&nbsp;</td></TR>");

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
     * @param m_keyword the keyword to search on
     */
    public void doKeywordSearch(String m_keyword) {
        StringBuffer query = new StringBuffer();
        String keyword;

        m_results = new ArrayList();
        keyword = m_keyword;
   
        query.append("SELECT we.weblog_entry_id as id, we.title as name, we.text as description, '" + ObjectType.BLOG + "' as object_type ");
        query.append(" FROM pn_weblog_entry we, pn_weblog w ");
        query.append(" WHERE w.space_id = ");
        query.append(getFirstSpaceID() + " ");
        query.append(" AND w.weblog_id = we.weblog_id AND we.status = 'PUBLISHED' ");

        if (keyword != null && keyword.length() > 0) {
            query.append(" AND (UPPER(we.title) LIKE UPPER('%");
            query.append(keyword);
            query.append("%') ");
            query.append(" OR UPPER(we.text) LIKE UPPER('%");
            query.append(keyword);
            query.append("%')) ");
        }
        query.append(" AND w.is_active = '1' AND w.is_enabled = '1' ");

        try {
            m_db.executeQuery(query.toString());
            while (m_db.result.next()) {
                String[] result_row = {m_db.result.getString("id"),
                                       m_db.result.getString("name"),
                                       m_db.result.getString("description"),
                                       m_db.result.getString("object_type")};
                m_results.add(result_row);
            }
            m_db.release();
        } catch (SQLException sqle) {
        	Logger.getLogger(DocumentSearch.class).error("DocumentSearch.doSimpleSearch() threw an SQL exception");
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
        String keyword;


        m_results = new ArrayList();
        keyword = m_request.getParameter("KEYWORD");

        query.append("SELECT we.weblog_entry_id as id, we.title as name, we.text as description, '" + ObjectType.BLOG + "' as object_type ");
        query.append(" FROM pn_weblog_entry we, pn_weblog w ");
        query.append(" WHERE w.space_id = ");
        query.append(getFirstSpaceID() + " ");
        query.append(" AND w.weblog_id = we.weblog_id AND we.status = 'PUBLISHED' ");

        if (keyword != null && keyword.length() > 0) {
            query.append(" AND (UPPER(we.title) LIKE UPPER('%");
            query.append(keyword);
            query.append("%') ");
            query.append(" OR UPPER(we.text) LIKE UPPER('%");
            query.append(keyword);
            query.append("%')) ");
        }
        query.append(" AND w.is_active = '1' AND w.is_enabled = '1' ");

        try {
            m_db.executeQuery(query.toString());
            while (m_db.result.next()) {
                String[] result_row = {m_db.result.getString("id"),
                                       m_db.result.getString("name"),
                                       m_db.result.getString("description"),
                                       m_db.result.getString("object_type")};
                m_results.add(result_row);
            }
            m_db.release();
        } catch (SQLException sqle) {
        	Logger.getLogger(DocumentSearch.class).error("DocumentSearch.doSimpleSearch() threw an SQL exception");
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
        String name;
        String desc;


        m_results = new ArrayList();
        name = m_request.getParameter("NAME");
        desc = m_request.getParameter("DESC");

        query.append("SELECT we.weblog_entry_id as id, we.title as name, we.text as description, '" + ObjectType.BLOG + "' as object_type ");
        query.append(" FROM pn_weblog_entry we, pn_weblog w ");
        query.append(" WHERE w.space_id = ");
        query.append(getFirstSpaceID() + " ");
        query.append(" AND w.weblog_id = we.weblog_id AND we.status = 'PUBLISHED' ");
        
        if (name != null && name.length() > 0) {
            query.append("AND UPPER(we.title) LIKE UPPER('%");
            query.append(name);
            query.append("%') ");
        }
        if (desc != null && desc.length() > 0) {
            query.append(" AND UPPER(we.text) LIKE UPPER('%");
            query.append(desc);
            query.append("%') ");
        }

        query.append(" AND w.is_active = '1' AND w.is_enabled = '1' ");

        try {
            m_db.executeQuery(query.toString());
            while (m_db.result.next()) {
                String[] result_row = {m_db.result.getString("id"),
                                       m_db.result.getString("name"),
                                       m_db.result.getString("description"),
                                       m_db.result.getString("object_type")};
                m_results.add(result_row);
            }
            m_db.release();
        } catch (SQLException sqle) {
        	Logger.getLogger(DocumentSearch.class).error("DocumentSearch.doSimpleSearch() threw an SQL exception");
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
        sb.append("<col>" + PropertyProvider.get("prm.global.search.blog.results.subject.column") + "</col>\n"); // Blog entry title
        //sb.append("<col>" + PropertyProvider.get("prm.global.search.blog.results.message.column") + "</col>\n"); // Blog entry text
        sb.append("</table_def>\n");
        sb.append("<content>\n");

        for (int i = 0; i < m_results.size(); i++) {
            String id = ((String[])m_results.get(i))[0];
            String name = ((String[])m_results.get(i))[1];
            String description = ((String[])m_results.get(i))[2];
            String objectType = ((String[])m_results.get(i))[3];
            String href = URLFactory.makeURL(id, objectType);

            sb.append("<row>\n");
            sb.append("<data_href>\n");
            if(StringUtils.isNotEmpty(name)){
            	sb.append("<label>" + XMLUtils.escape(name) + "</label>\n");
            } else {
            	sb.append("<label>" + PropertyProvider.get("prm.global.search.blog.results.nosubject.message") + "</label>\n");
            }
            sb.append("<href>" + href + "</href>\n");
            sb.append("<id>" + XMLUtils.escape(id) + "</id>");
            sb.append("</data_href>\n");
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
        sb.append("<col>" + PropertyProvider.get("prm.global.search.blog.results.subject.column") + "</col>\n"); // Blog entry title
        //sb.append("<col>" + PropertyProvider.get("prm.global.search.blog.results.message.column") + "</col>\n"); // Blog entry text
        sb.append("</table_def>\n");
        sb.append("<content>\n");

        for (int i = m_start; i <= m_end; i++) {
            String id = ((String[])m_results.get(i))[0];
            String name = ((String[])m_results.get(i))[1];
            String description = ((String[])m_results.get(i))[2];
            String objectType = ((String[])m_results.get(i))[3];
            String href = URLFactory.makeURL(id, objectType);

            sb.append("<row>\n");
            sb.append("<data_href>\n");
            if(StringUtils.isNotEmpty(name)){
            	sb.append("<label>" + XMLUtils.escape(name) + "</label>\n");
            } else {
            	sb.append("<label>" + PropertyProvider.get("prm.global.search.blog.results.nosubject.message") + "</label>\n");
            }
            sb.append("<href>" + href + "</href>\n");
            sb.append("<id>" + XMLUtils.escape(id) + "</id>");
            sb.append("</data_href>\n");
            sb.append("</row>\n");

        }

        sb.append("</content>\n");
        sb.append("</channel>\n");

        return sb.toString();

    }


    /**
     * Will return the number of results found by the search.
     *
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
            searchType == ADVANCED_SEARCH ||
            searchType == BROWSE_SEARCH) {
            return true;
        }
        return false;
    }

}
