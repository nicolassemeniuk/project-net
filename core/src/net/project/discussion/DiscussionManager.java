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

 package net.project.discussion;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;

import net.project.base.Module;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.xml.XMLFormatter;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;


/**
 * This class manages discussion groups.
 *
 * @author Adam Klatzkin
 * @since Version 1.0
 */
public class DiscussionManager implements Serializable, IXMLPersistence {

    // Discussion post list sort options.
    public static final int SUBJECT = 1;
    public static final int DATE = 2;
    public static final int AUTHOR = 3;

    // List of DiscussionGroups available for the Space context.
    private ArrayList discussionGroupList = null;

    protected Space m_space = null;
    protected String m_objectId = null;
    protected User m_user = null;

    // XML formatting information and utilities specific to this object
    private XMLFormatter m_xmlFormatter = new XMLFormatter();

    /**
     * Constructor
     */
    public DiscussionManager() {
        // Do nothing
    }

    /**
     * Construct a DiscussionManager with a specific User and Space context.
     *
     * @param user  the User context for this manager.
     * @param space the space context for this manager; project space, business space, etc.
     */
    public DiscussionManager(User user, Space space) {
        m_user = user;
        m_space = space;
        m_objectId = space.getID();
    }

    /**
     * @param user  the User context for this manager.
     */
    public void setUser(User user) {
        m_user = user;
    }

    /**
     * @return User  the User context for this manager.
     */
    public User getUser() {
        return m_user;
    }

    /**
     * a discussion group is associated with either a space or an object.
     * To use the discussion manager you must call this method or setObjectId.
     *
     * @param space  the Space context for this manager.
     */
    public void setSpace(Space space) {
        m_space = space;
        m_objectId = space.getID();
    }

    /**
     * a discussion group is associated with either a space or an object.
     * To use the discussion manager you must call this method or setSpace.
     *
     * @param objectId  the object that the discussion belongs to
     */
    public void setObjectId(String objectId) {
        m_objectId = objectId;
    }

    /**
     * @return Space  the Space context for this manager.
     */
    public Space getSpace() {
        return m_space;
    }


    public void copyGroups(String fromSpaceID, String toSpaceID) throws net.project.base.PnetException {

        int errorCode = 0;
        DBBean db = new DBBean();

        try {

            db.prepareCall("begin DISCUSSION.COPY_ALL  (?,?,?,?); end;");

            db.cstmt.setString(1, fromSpaceID);
            db.cstmt.setString(2, toSpaceID);
            db.cstmt.setString(3, net.project.security.SessionManager.getUser().getID());
            db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);

            db.executeCallable();
            errorCode = db.cstmt.getInt(4);
        } // end try
        catch (SQLException sqle) {
        	Logger.getLogger(DiscussionManager.class).debug("DiscussionManager.copyAll():  unable to execute stored procedure: " + sqle);
            throw new PersistenceException("DiscussionManager.copyAll " +
                "operation failed! ", sqle);
        } finally {
            db.release();
        }

        net.project.database.DBExceptionFactory.getException("DiscussionManager.copyAll()", errorCode);

    }


    /**
     * Get the list of available discsussion groups available for the User and Space context.
     *
     * @return ArrayList     DiscussionGroup objects
     */
    public ArrayList getGroupList() {

        // If the space and user context are not defined, return null
        if ((m_objectId == null) || (m_user == null))
            return null;

        // create the query
        String query =
            "SELECT " +
            "  dg.DISCUSSION_GROUP_ID, " +
            "  dg.DISCUSSION_GROUP_NAME, " +
            "  dg.DISCUSSION_GROUP_DESCRIPTION, " +
            "  dg.DISCUSSION_GROUP_CHARTER_CLOB, " +
            "  read_posts.number_of_read_posts,	" +
            "  total_posts.total_number_of_posts " +
            "FROM " +
            "  pn_object_has_discussion ohd, " +
            "  pn_discussion_group dg, " +
            "  (SELECT " +
            "     pr.discussion_group_id, count(p1.post_id) number_of_read_posts "+
            "   FROM " +
            "     pn_post_reader pr," +
            "     pn_post p1" +
            "   WHERE " +
            "     pr.post_id = p1.post_id " +
            "     and p1.record_status = 'A' " +
            "     and pr.person_id = " + m_user.getID() +
            "   GROUP BY " +
            "     pr.discussion_group_id) read_posts, " +
            "  (SELECT " +
            "     discussion_group_id, count(post_id) total_number_of_posts " +
            "   FROM " +
            "     pn_post " +
            "   WHERE " +
            "     pn_post.record_status = 'A' " +
            "   GROUP BY pn_post.discussion_group_id) total_posts " +
            "WHERE " +
            "  ohd.object_id = " + m_objectId +
            "  and dg.discussion_group_id = ohd.discussion_group_id " +
            "  and dg.RECORD_STATUS = 'A' " +
            "  AND dg.DISCUSSION_GROUP_ID = read_posts.discussion_group_id (+) " +
            "  AND dg.DISCUSSION_GROUP_ID = total_posts.discussion_group_id (+)";

        DBBean db = new DBBean();

        try {
            db.executeQuery(query);
            discussionGroupList = new ArrayList();

            while (db.result.next()) {

                // populate a DiscussionGroup object from the current result row
                DiscussionGroup discussionGroup = new DiscussionGroup();
                discussionGroup.setID(db.result.getString(1));
                discussionGroup.setName(db.result.getString(2));
                discussionGroup.setDescription(db.result.getString(3));
                discussionGroup.setCharter(ClobHelper.read(db.result.getClob(4)));
                String num_read = db.result.getString(5);
                discussionGroup.setNumPosts(db.result.getString(6));

                // Determine the number of unread posts
                if (discussionGroup.getNumPosts() != null) {
                    if (num_read != null) {
                        discussionGroup.setUnread(
                            Integer.toString(Integer.parseInt(discussionGroup.getNumPosts()) -
                            Integer.parseInt(num_read)));
                    } else {
                        discussionGroup.setUnread(discussionGroup.getNumPosts());
                    }
                }

                // add the discussion group object to the ArrayList
                discussionGroupList.add(discussionGroup);
            }

        } catch (java.sql.SQLException sqle) {
        	Logger.getLogger(DiscussionManager.class).error("DiscussionManager.getGroupList() throw a SQLException: " + sqle);
            // Method signature does not throw PersistenceException

        } catch (PersistenceException e) {
            // Method signature does not currently throw one
            // Do nothing

        } finally {
            db.release();
        }

        return discussionGroupList;
    }

    /**
     * Get a loaded discussion group from the discussion manager
     *
     * @return DiscussionGroup   the group
     */
    public DiscussionGroup getDiscussionGroup(String id) {
        if (discussionGroupList == null)
            return null;

        for (int i = 0; i < discussionGroupList.size(); i++) {
            if (((DiscussionGroup)discussionGroupList.get(i)).getID().equals(id)) {
                return (DiscussionGroup)discussionGroupList.get(i);
            }
        }
        return null;
    }

    /**
     * Gets the presentation of the component
     * This method will apply the stylesheet to the XML representation of the component and
     * return the resulting text
     *
     * @return String    presentation of the component
     */
    public String getPresentation() {
        return m_xmlFormatter.getPresentation(getXML());
    }

    /**
     * Sets the stylesheet file name used to render this component.
     * This method accepts the name of the stylesheet used to convert the XML representation
     * of the component to a presentation form.
     *
     * @param styleSheetFileName     name of the stylesheet used to render the XML representation
     *                               of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        m_xmlFormatter.setStylesheet(styleSheetFileName);
    }


    /**
     * add the base attributes for the XML output
     *
     * @param sb output is written here
     */
    private void addBaseAttributes(StringBuffer sb) {
        discussionGroupList = getGroupList();
        if (discussionGroupList != null) {
            for (int i = 0; i < discussionGroupList.size(); i++) {
                DiscussionGroup dg = (DiscussionGroup)discussionGroupList.get(i);
                // SECURITY CHECK - make sure the current user has permission to view the discussion
                // group, otherwise we will not enumerate it.
                if (!SessionManager.getSecurityProvider().isActionAllowed(dg.getID(),
                    Integer.toString(net.project.base.Module.DISCUSSION),
                    net.project.security.Action.VIEW)) {
                    // security check failed, jump back to the beginning of the for loop
                    continue;
                }

                sb.append("<group>");

                sb.append("<group_id>");
                sb.append(XMLUtils.escape(dg.getID()));
                sb.append("</group_id>\n");

                sb.append("<query_string>");
                String name = "";
                if (dg.getName() != null) {
                    try {
                        name = "&name=" + java.net.URLEncoder.encode(dg.getName(), SessionManager.getCharacterEncoding());
                    } catch (UnsupportedEncodingException e) {
                        // Unlikely to happen; the character encoding is going
                        // to be UTF-8
                        // Name will remain empty
                    }
                }
                sb.append(XMLUtils.escape("?module=" + Module.DISCUSSION + "&action="
                    + Action.VIEW + "&id=" + dg.getID() + name));
                sb.append("</query_string>\n");

                sb.append("<group_name>");
                sb.append(XMLUtils.escape(dg.getName()));
                sb.append("</group_name>\n");

                sb.append("<num_posts>");
                sb.append(XMLUtils.escape(dg.getNumPosts()));
                sb.append("</num_posts>\n");

                sb.append("<unread_posts>");
                sb.append(XMLUtils.escape(dg.getUnread()));
                sb.append("</unread_posts>\n");

                sb.append("<description>");
                sb.append(XMLUtils.escape(dg.getDescription()));
                sb.append("</description>\n");

                sb.append("</group>\n");
            }
        }
    }

    /**
     * Gets the XML representation without the XML directive
     * This method is used to construct the body of an XML document without
     * the XML directive. It is provided so that objects can call upon other objects
     * in order to construct an XML document that is a composite of multiple objects
     *
     * @return String    XML representation without XML directives
     */
    public String getXMLBody() {
        StringBuffer sb = new StringBuffer();

        sb.append("<DiscussionGroupsList>\n");
        addBaseAttributes(sb);
        sb.append("</DiscussionGroupsList>\n");

        return sb.toString();
    }

    /**
     * Converts the object to XML representation
     * This method returns the object as XML text.
     *
     * @return String    XML representation
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }
}
