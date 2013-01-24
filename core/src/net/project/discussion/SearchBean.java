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
package net.project.discussion;

import java.io.Serializable;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.Action;
import net.project.security.SessionManager;

/**
 * Used for searching posts within a discussion group.
 *
 * @author AdamKlatzkin
 * @since 02/00
 */
public class SearchBean implements Serializable {
    static final int SUBJECT_MAX_CHARS = 28;

    public String m_searchName = "";
    public String m_searchSubject = "";
    public String m_searchBody = "";
    public DiscussionGroup m_discussion = null;

    /**
     * Public Constructor
     */
    public SearchBean() {
    }

    /**
     * @param group      the DiscussionGroup to search on
     */
    public void setDiscussion(DiscussionGroup group) {
        m_discussion = group;
    }

    /**
     * @param subject    the subject string to search on
     */
    public void setSubject(String subject) {
        m_searchSubject = subject;
    }

    /**
     * @param name    the name string to search on
     */
    public void setName(String name) {
        m_searchName = name;
    }

    /**
     * @param body   the body string to search on
     */
    public void setBody(String body) {
        m_searchBody = body;
    }

    /**
     * execute the search and return the results rendered as an HTML table
     *
     * @return String    the rendered HTML
     */
    public String getHTML() throws PersistenceException {
        if (m_discussion == null) {
            return PropertyProvider.get("prm.discussion.findpost.noactivegroup.message");
        }

        StringBuffer sb = new StringBuffer();
        // set up the top of the table
        sb.append("<TABLE valign=top BORDER=0 CELLSPACING=0 CELLPADDING=0><TR></TR>");
        sb.append("<tr class=\"tableHeader\"><th ALIGN=\"left\" WIDTH=170>" + PropertyProvider.get("prm.discussion.findpost.subject.label") + "</th>");
        sb.append("<th ALIGN=\"left\" WIDTH=100>" + PropertyProvider.get("prm.discussion.findpost.from.label") + "</th>");
        sb.append("<th ALIGN=\"left\" WIDTH=90>" + PropertyProvider.get("prm.discussion.findpost.date.label") + "</th></tr>");

        sb.append("<tr class=\"tableLine\">");
        sb.append("<td  colspan=\"3\" class=\"tableLine\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/spacers/trans.gif\" width=\"1\" height=\"2\" border=\"0\" alt=\"\"/></td>");
        sb.append("</tr>");

        // do the query
        // We are doing a case insensitive search on the post body CLOB
        StringBuffer query = new StringBuffer();
        query.append("SELECT pn_post.post_id ")
                .append("FROM pn_post, pn_post_body_clob pbc, pn_person ")
                .append("WHERE pn_post.discussion_group_id = ").append(m_discussion.getID()).append(" ")
                .append("AND pbc.object_id = pn_post.post_body_id ")
                .append("AND pn_post.person_id = pn_person.person_id ")
                .append("AND pn_post.record_status = 'A' ");

        if (m_searchName != null) {
            query.append("AND UPPER(pn_person.display_name) LIKE UPPER('%").append(m_searchName).append("%') ");
        }
        if (m_searchSubject != null) {
            query.append("AND UPPER(pn_post.subject) LIKE UPPER('%").append(m_searchSubject).append("%') ");
        }
        if (m_searchBody != null) {
            query.append("AND base.clob_like(pbc.clob_field, '%").append(m_searchBody).append("%', 0) > 0 ");
        }

        DBBean db = new DBBean();
        try {
            db.executeQuery(query.toString());

            if (db.result.next()) {
                do {
                    String postId = db.result.getString(1);
                    Post post = m_discussion.getPostHeader(postId);
                    if (post != null) {
                        String subject = post.getSubject();
                        if (subject.length() > SUBJECT_MAX_CHARS)
                            subject = subject.substring(0, SUBJECT_MAX_CHARS - 4) + "...";

                        sb.append("<tr><td class=\"tableContent\"><A HREF=\"ThreadList.jsp?module=" + Module.DISCUSSION + "&action=" + Action.VIEW + "&id=" + m_discussion.getID() + "&postid=" + post.getID() + "\" title=\"" + post.getSubject()
                            + "\" target=\"upper_frame\">" + subject + "</A></td>\n");
                        sb.append("<td class=\"tableContent\">" + post.getAuthorDisplayName() + "</td>\n");
                        sb.append("<td class=\"tableContent\">" + post.getPostdate() + "</td></tr>\n");
                        sb.append("<tr class=\"tableLine\">");
                        sb.append("<td  colspan=\"3\" class=\"tableLine\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/spacers/trans.gif\" width=\"1\" height=\"1\" border=\"0\" alt=\"\"/></td>");
                        sb.append("</tr>");
                    }
                } while (db.result.next());
            } else {
                sb.append("<tr><td colspan=\"3\" class=\"tableContent\">" + PropertyProvider.get("prm.discussion.findpost.nopostsfound.message") + "</td></tr>");
            }
        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("Error searching: " + sqle, sqle);

        } finally {
            db.release();
        }

        // end the table
        sb.append("</TABLE>");

        return sb.toString();
    }
} // SearchBean
