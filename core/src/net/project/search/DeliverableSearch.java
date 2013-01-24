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
|
+--------------------------------------------------------------------------------------*/
package net.project.search;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import net.project.base.ObjectType;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.code.Code;
import net.project.code.TableCodeDomain;
import net.project.security.SessionManager;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * This class is used for searching Deliverable Objects.  It provides the forms
 * for simple and advanced searching as well as a properly formatted XML string
 * of the results.
 *
 * @author Brian Conneen
 * @since 03/00
 */
public class DeliverableSearch extends GenericSearch {
    private TableCodeDomain domain = new TableCodeDomain();

    /**
     * Creates a new instance of <code>DeliverableSearch</code>.
     */
    public DeliverableSearch() {
        super();
        domain.setTableName("pn_deliverable");
        domain.setColumnName("status_id");
        domain.load();
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
        return PropertyProvider.get("prm.global.search.deliverable.results.channel.title"); // Deliverable
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
        formString.append("\n<td class=\"channelHeader\" width=98% colspan=2 align=left NOWRAP>" + PropertyProvider.get("prm.global.search.deliverable.simple.title") + "</td>"); // Simple Deliverable Search:
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-right_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n</tr>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.deliverable.simple.namedescription.label") + "&nbsp;&nbsp;</TD>"); // Name or Description:
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
        ArrayList m_codes = domain.getCodes();

        StringBuffer formString = new StringBuffer();


        formString.append("<TABLE  border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"500\">");

        formString.append("\n<tr>");
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-left_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n<td class=\"channelHeader\" width=98% colspan=2 align=left NOWRAP>" + PropertyProvider.get("prm.global.search.deliverable.advanced.title") + "</td>"); // Advanced Deliverable Search:
        formString.append("\n<td class=\"channelHeader\" width=1%><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/channelbar-right_end.gif\" width=8 height=15 alt=\"\" border=\"0\"></td>");
        formString.append("\n</tr>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.deliverable.advanced.name.label") + "&nbsp;&nbsp;</TD>"); // Name:
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"NAME\">");
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("\n<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.deliverable.advanced.description.label") + "&nbsp;&nbsp;</TD>"); // Description:
        formString.append("\n<TD class=\"tableContent\">");
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"DESC\">");
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("\n<tr><td>&nbsp;</td>");
        formString.append("<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.deliverable.advanced.comments.label") + "&nbsp;&nbsp;</TD><TD class=\"tableContent\">"); // Comments:
        formString.append("<INPUT TYPE=\"TEXT\" NAME=\"COMMENT\">");
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("<TR  align=\"left\" valign=\"middle\">");
        formString.append("\n<td>&nbsp;</td>");
        formString.append("<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.deliverable.advanced.isoptional.label") + "&nbsp;&nbsp;</TD><TD class=\"tableContent\">"); // Is Optional:
        formString.append("<SELECT NAME=\"OPTIONAL\"><OPTION VALUE=\"\">" + PropertyProvider.get("prm.global.search.deliverable.advanced.option.either.name") + "<OPTION VALUE=\"1\">" + PropertyProvider.get("prm.global.search.deliverable.advanced.option.yes.name") + "<OPTION VALUE=\"0\">" + PropertyProvider.get("prm.global.search.deliverable.advanced.option.no.name") + "</SELECT>"); // | Either | Yes | No |
        formString.append("</TD><td>&nbsp;</td></TR>");
        formString.append("<TR align=\"left\" valign=\"middle\">");
        formString.append("\n<td>&nbsp;</td>");
        formString.append("<TD class=\"tableHeader\">" + PropertyProvider.get("prm.global.search.deliverable.advanced.status.label") + "&nbsp;&nbsp;</TD><TD class=\"tableContent\">");  // Status:
        formString.append("<SELECT NAME=\"STATUS\"><OPTION VALUE=\"\">" + PropertyProvider.get("prm.global.search.deliverable.advanced.option.any.name")); // Any
        for (int i = 0; i < m_codes.size(); i++) {
            String code = ((Code)m_codes.get(i)).getCode();
            String name = ((Code)m_codes.get(i)).getName();
            formString.append("<OPTION VALUE=\"" + code + "\">" + name);
        }
        formString.append("</SELECT>");
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
     * @param keyword the keyword to search on
     */
    public void doKeywordSearch(String keyword) {
        StringBuffer query = new StringBuffer();

        m_results = new ArrayList();

        query.append("SELECT pn_deliverable.deliverable_id, pn_deliverable.deliverable_name, pn_deliverable.deliverable_desc ");
        query.append("FROM pn_deliverable, pn_phase_has_deliverable, pn_phase, pn_space_has_process ");
        query.append("WHERE pn_space_has_process.space_id = ");
        query.append(getFirstSpaceID() + " ");
        query.append("AND pn_space_has_process.process_id = pn_phase.process_id ");
        query.append("AND pn_phase.phase_id = pn_phase_has_deliverable.phase_id ");
        query.append("AND pn_phase_has_deliverable.deliverable_id = pn_deliverable.deliverable_id ");
        if (keyword != null && keyword.length() > 0) {
            query.append("AND (UPPER(pn_deliverable.deliverable_name) LIKE UPPER('%");
            query.append(keyword);
            query.append("%') ");
            query.append("OR UPPER(pn_deliverable.deliverable_desc) LIKE UPPER('%");
            query.append(keyword);
            query.append("%')) ");
        }
        query.append("AND pn_deliverable.record_status = 'A'");

        try {
            m_db.executeQuery(query.toString());
            while (m_db.result.next()) {
                String[] result_row = {
                    m_db.result.getString("deliverable_id"),
                    m_db.result.getString("deliverable_name"),
                    m_db.result.getString("deliverable_desc")
                };
                m_results.add(result_row);
            }
            m_db.release();
        } catch (SQLException sqle) {
        	Logger.getLogger(DeliverableSearch.class).error("DeliverableSearch.doKeywordSearch() threw an SQL exception");
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

        m_results = new ArrayList();
        String keyword = m_request.getParameter("KEYWORD");

        query.append("SELECT pn_deliverable.deliverable_id, pn_deliverable.deliverable_name, pn_deliverable.deliverable_desc ");
        query.append("FROM pn_deliverable, pn_phase_has_deliverable, pn_phase, pn_space_has_process ");
        query.append("WHERE pn_space_has_process.space_id = ");
        query.append(getFirstSpaceID() + " ");
        query.append("AND pn_space_has_process.process_id = pn_phase.process_id ");
        query.append("AND pn_phase.phase_id = pn_phase_has_deliverable.phase_id ");
        query.append("AND pn_phase_has_deliverable.deliverable_id = pn_deliverable.deliverable_id ");
        if (keyword != null && keyword.length() > 0) {
            query.append("AND (UPPER(pn_deliverable.deliverable_name) LIKE UPPER('%");
            query.append(keyword);
            query.append("%') ");
            query.append("OR UPPER(pn_deliverable.deliverable_desc) LIKE UPPER('%");
            query.append(keyword);
            query.append("%')) ");
        }
        query.append("AND pn_deliverable.record_status = 'A'");

        try {
            m_db.executeQuery(query.toString());
            while (m_db.result.next()) {
                String[] result_row = {m_db.result.getString("deliverable_id"),
                                       m_db.result.getString("deliverable_name"),
                                       m_db.result.getString("deliverable_desc")};
                m_results.add(result_row);
            }
            m_db.release();
        } catch (SQLException sqle) {
        	Logger.getLogger(DeliverableSearch.class).error("DeliverableSearch.doSimpleSearch() threw an SQL exception");
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
        String name = null;
        String desc = null;
        String comment = null;
        String optional = null;
        String status = null;

        m_results = new ArrayList();
        name = m_request.getParameter("NAME");
        desc = m_request.getParameter("DESC");
        comment = m_request.getParameter("COMMENT");
        optional = m_request.getParameter("OPTIONAL").toUpperCase();
        status = m_request.getParameter("STATUS").toUpperCase();

        query.append("SELECT pn_deliverable.deliverable_id, pn_deliverable.deliverable_name, pn_deliverable.deliverable_desc ");
        query.append("FROM pn_deliverable, pn_phase_has_deliverable, pn_phase, pn_space_has_process ");
        query.append("WHERE pn_space_has_process.space_id = ");
        query.append(getFirstSpaceID() + " ");
        query.append("AND pn_space_has_process.process_id = pn_phase.process_id ");
        query.append("AND pn_phase.phase_id = pn_phase_has_deliverable.phase_id ");
        query.append("AND pn_phase_has_deliverable.deliverable_id = pn_deliverable.deliverable_id ");

        if (name != null && name.length() > 0) {
            query.append("AND UPPER(pn_deliverable.deliverable_name) LIKE UPPER('%");
            query.append(name);
            query.append("%') ");
        }
        if (desc != null && desc.length() > 0) {
            query.append("AND UPPER(pn_deliverable.deliverable_desc) LIKE UPPER('%");
            query.append(desc);
            query.append("%') ");
        }
        if (comment != null && comment.length() > 0) {
            // Case insensitive clob comparison
            query.append("AND BASE.CLOB_LIKE(pn_deliverable.deliverable_comments_clob, '%")
                    .append(comment).append("%', 0) > 0 ");
        }
        if (optional != null && optional.length() > 0) {
            query.append("AND pn_deliverable.is_optional = '");
            query.append(optional);
            query.append("' ");
        }
        if (status != null && status.length() > 0) {
            query.append("AND pn_deliverable.status_id = '");
            query.append(status);
            query.append("' ");
        }

        query.append("AND pn_deliverable.record_status = 'A'");

        try {
            m_db.executeQuery(query.toString());
            while (m_db.result.next()) {
                String[] result_row = {m_db.result.getString("deliverable_id"),
                                       m_db.result.getString("deliverable_name"),
                                       m_db.result.getString("deliverable_desc")};
                m_results.add(result_row);
            }
            m_db.release();
        } catch (SQLException sqle) {
        	Logger.getLogger(DeliverableSearch.class).error("DeliverableSearch.doAdvancedSearch() threw an SQL exception");
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
        sb.append("<col>" + PropertyProvider.get("prm.global.search.deliverable.results.name.column") + "</col>\n"); // Deliverable Name
        sb.append("<col>" + PropertyProvider.get("prm.global.search.deliverable.results.desc.column") + "</col>\n"); // Deliverable Desc
        sb.append("</table_def>\n");
        sb.append("<content>\n");

        for (int i = 0; i < m_results.size(); i++) {
            String id = ((String[])m_results.get(i))[0];
            String one = ((String[])m_results.get(i))[1];
            String two = ((String[])m_results.get(i))[2];
            String href = URLFactory.makeURL(id, ObjectType.DELIVERABLE);

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
     **/
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
        sb.append("<col>" + PropertyProvider.get("prm.global.search.deliverable.results.name.column") + "</col>\n"); // Deliverable Name
        sb.append("<col>" + PropertyProvider.get("prm.global.search.deliverable.results.desc.column") + "</col>\n"); // Deliverable Desc
        sb.append("</table_def>\n");
        sb.append("<content>\n");

        for (int i = m_start; i <= m_end; i++) {
            String id = ((String[])m_results.get(i))[0];
            String one = ((String[])m_results.get(i))[1];
            String two = ((String[])m_results.get(i))[2];
            String href = URLFactory.makeURL(id, ObjectType.DELIVERABLE);

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
