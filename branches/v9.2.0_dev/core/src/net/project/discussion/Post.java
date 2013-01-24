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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.project.base.ObjectType;
import net.project.base.RecordStatus;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.link.ILinkableObject;
import net.project.notification.EventCodes;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.HTMLUtils;
import net.project.util.TextFormatter;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;


/**
 * Maintains a discussion group post
 *
 * @author Adam Klatzkin
 * @since 01/00
 */
public class Post implements ILinkableObject, IXMLPersistence, IJDBCPersistence, java.io.Serializable {
    /**
     * The maximum length of a Post line, currently <code>85</code>.
     * Used when word wrapping a post.
     * @see #getPostbody
     */
    public static final int POST_LINE_LENGTH = 85;

    private String m_post_id = null;

    private String m_parent_post_id = null;
    
    /**
     * The discussion group that this post is related to.  This is more than
     * likely empty unless you've populated it yourself.
     */
    private DiscussionGroup discussionGroup;

    private String m_number_of_views = null;

    private String m_discussion_group_id = null;
    
    private String m_discussion_group_name = null;

    private String m_subject = null;

    private String m_person_id = null;
    
    private String m_person_full_name = null;

    private String m_urgency_id = null;

    private String m_urgency = null;

    private String m_post_body = null;

    private String m_post_body_id = null;

    private User m_user = null;

    public Date m_date_posted = null;
    /**
     * The number of replies to this post.
     */
    private int replyCount = 0;

    /**
     * The record status of this post.
     */
    private RecordStatus recordStatus = null;

    private boolean m_loaded = false;
    private boolean m_hasRead = false;
    private static final String POST_CLOB_TABLE_NAME = "pn_post_body_clob";

    /**
     * Creates an empty Post.
     */
    public Post() {
        // Nothing to initialize
    }

    /**
     * Creates a new post with the specified id.
     * Use this method to prepare a Post for loading.
     * @param post_id the database object_id of this post
     */
    public Post(String  post_id) {
        m_post_id = post_id;
    }

    /**
     * Sets the user context for this post.
     *
     * @param user The user context for this post.
     */
    public void setUser(User user) {
        m_user = user;
    }

    /**
     * Gets the user context for this post.
     *
     * @return a <code>User</code> object which is the user context for this
     * post.
     */
    public User getUser() {
        return m_user;
    }

    /**
     * Gets the id of this post
     *
     * @deprecated as we prefer the use of getID()
     */
    public String getId() {
        return m_post_id;
    }

    /**
     * Sets the id of this post
     *
     * @param id the post id
     */
    public void setID(String id) {
        m_post_id = id;
    }

    /**
     * Gets the id of the post
     *
     * @return a <code>String</code> value containing the post id
     */
    public String getID() {
        return m_post_id;
    }

    /**
     * Get the subject of the post
     *
     * @return a <code>String</code> value containing the post name
     * @see ILinkableObject
     */
    public String getName() {
        return getSubject();
    }

    /**
     * Get the type of this object.  This will always be ObjectType.POST
     * for a post object.
     *
     * @return a <code>String</code> value containing the object type
     * @see ILinkableObject
     */
    public String getType() {
        return ObjectType.POST;
    }

    /**
     * Get a valid URL for this post
     *
     * @return String  the object's external URL reference
     * @see ILinkableObject
     */
    public String getURL() {
        return URLFactory.makeURL(getID(), ObjectType.POST, m_discussion_group_id, m_discussion_group_name);
    }

    /**
     * @deprecated as of Version 7.4.  Please use {@link #getParentPostID}
     * instead, which follows our naming convention.
     * @param parent_id the post's parent id number
     */
    public void setParentid(String parent_id) {
        m_parent_post_id = parent_id;
    }

    /**
     * The discussion group that this post is in.  More than likely, this is
     * empty.
     */
    public DiscussionGroup getDiscussionGroup() {
        return discussionGroup;
    }

    /**
     * Set the discussion group that this post is a member of.
     */
    public void setDiscussionGroup(DiscussionGroup discussionGroup) {
        this.discussionGroup = discussionGroup;
    }

    /**
     * Determine whether the user has ever read this post.
     *
     * @return boolean true if the user has ever read the post
     */
    public boolean hasRead() {
        return m_hasRead;
    }

    /**
     * Set whether or not the user has ever read this post.
     *
     * @param state true if the user has read the post false to mark a post as
     * not read
     */
    public void setRead(boolean state) {
        m_hasRead = state;
    }

    /**
     * Get the url of a thumbnail image to be displayed with a link to the post.
     *
     * @return String the url of a thumbnail image to be displayed with a link
     * to the post
     */
    public String getImageURL() {
        return "/images/post.gif";
    }

    /**
     * Get the link color for this post.  This will depend on whether or not the
     * post has already been read.
     *
     * @return a <code>String</code> value containing the color a link to this
     * post should be displayed in
     */
    public String getLinkColor() {
        if (hasRead()) {
            return "#666666";
        } else {
            return "#000000";
        }
    }

    /**
     * Get the subject of this post.
     *
     * @return a <code>String</code> value containing the post's subject.  This
     * will be an empty string (as opposed to a null one) if there isn't a
     * subject.
     */
    public String getSubject() {
        if (m_subject == null) {
            return "";
        } else {
            return HTMLUtils.escape(m_subject);
        }
    }

    /**
     * Set the subject of this post.
     *
     * @param subject a <code>String</code> value containing the new subject of
     * this post.
     */
    public void setSubject(String subject) {
        m_subject = subject;
    }

    /**
     * Set the urgency level of this post.
     * @param urgency a <code>String</code> value containing the new urgency
     * level of this post.
     */
    public void setUrgency(String urgency) {
        if (m_urgency_id != urgency) {
            m_urgency_id = urgency;
            m_urgency = null;
        }
    }

    /**
     * Get the urgency level of this post
     *
     * @return a <code>String</code> value containing the urgency level of this
     * post.
     */
    public String getUrgency() {
        if (m_urgency == null) {
            net.project.code.TableCodeDomain domain = new net.project.code.TableCodeDomain();
            domain.setTableName("pn_post");
            domain.setColumnName("urgency_id");
            domain.load();
            m_urgency = domain.getCodeName(m_urgency_id);
        }

        return m_urgency;
    }

    /**
     * Get the body text (the message) of this post.
     * The text is wrapped at the column position given by <code>{@link #POST_LINE_LENGTH}</code>.
     * The body is adjusted so that URLs (http://host.com, https://host.com, www.whatever.com)
     * are converted to HTML anchors, special HTML characters are also converted.
     * @return a <code>String</code> value containing the post body
     */
    public String getPostbody() {
        if (m_post_body == null) {
            return "";

        } else {
            // Word wrap MUST be performed first.
            // It is not possible to word wrap after inserting HTML tags;
            // it may accidentally word wrap in the middle of a tag, thus
            // changing the nature of the tag.  For example, <br /> becomes <b
            // making the entire post BOLD.
            return TextFormatter.makeHyperlinkable(HTMLUtils.formatHtml(TextFormatter.adjustRightColumn(m_post_body, POST_LINE_LENGTH, SessionManager.getUser().getLocale())));

        }
    }


    /**
     * Sets the body text (the message) of this post.
     *
     * @param body the post body
     */
    public void setPostbody(String body) {
        m_post_body = body;
    }

    /**
     * Reformats a text string to have word wraps at a predefined column number.
     * The column width is defined in POST_LINE_LENGTH
     *
     * @deprecated In the Gecko release in favor of using
     * net.project.util.TextFormatter.adjustRightColumn().
     * @param  text      the string to be word wrapped
     * @return String    the word wrapped string
     */
    public String wordWrap(String text) {
        return net.project.util.TextFormatter.adjustRightColumn(text, POST_LINE_LENGTH, SessionManager.getUser().getLocale());
    }

    /**
     * Get the full name of the person who authored this post.
     *
     * @return a <code>String</code> value containing the full name of the post
     * author
     * @deprecated as of Version 7.4.  Please use {@link #getAuthorDisplayName}
     * and {@link #setAuthorDisplayName} instead.
     */
    public String getFullname() {
        if (m_person_full_name == null) {
            return "";
        } else {
            return m_person_full_name;
        }
    }

    /**
     * Get the display name of the user that authored the post.
     *
     * @return a <code>String</code> which contains the display name of the user
     * that authored the post.
     */
    public String getAuthorDisplayName() {
        if (m_person_full_name == null) {
            return "";
        } else {
            return m_person_full_name;
        }
    }

    /**
     * Set the display name of the user that authored the post.
     *
     * @param displayName a <code>String</code> containing the full name of the
     * user which authored the post.
     */
    public void setAuthorDisplayName(String displayName) {
        m_person_full_name = displayName;
    }

    /**
     * Gets the date on which the post was authored.
     *
     * @return a <code>String</code> value containing the post date formatted
     * using the user's date formatter
     */
    public String getPostdate() {
        if (m_date_posted == null) {
            return "";
        } else {
            return m_user.getDateFormatter().formatDateMedium(m_date_posted) + " "
                + m_user.getDateFormatter().formatTime(m_date_posted) ;
        }
    }

    /**
     * Determines whether or not the post body has been loaded.
     *
     * @return a <code>boolean</code> value containing true if the post's body
     * is loaded
     */
    public boolean isLoaded() {
        return m_loaded;
    }

    /**
     * Indicate whether or not the post's body has been loaded.
     *
     * @param loaded the post's state (true if loaded)
     */
    public void setLoaded(boolean loaded) {
        m_loaded = loaded;
    }

    /**
     * The first time this method is called, the post's readers are obtained
     * from the repository
     *
     * @return ArrayList   List of PostReader objects representing reader's of
     * this post
     */
    public ArrayList getReaderList() throws PersistenceException {
        ArrayList reader_list = new ArrayList();
        String query = "select pn_person.display_name, pn_post_reader.date_read " +
            "from pn_post_reader, pn_person " +
            "where pn_post_reader.post_id = " + m_post_id + " " +
            "and pn_person.person_id = pn_post_reader.person_id " +
            "order by pn_post_reader.date_read asc";

        DBBean db = new DBBean();
        try {
            db.executeQuery(query);

            while (db.result.next()) {
                PostReader reader = new PostReader();
                reader.m_person_name = db.result.getString(1);
                //reader.m_date_read = m_user.getDateFormatter().formatDate((java.util.Date) result.getTimestamp(2), "MM/dd/yy hh:mm a zzz");
                Date date_read = db.result.getTimestamp(2);

                reader.m_date_read = m_user.getDateFormatter().formatDate(date_read) + " "
                    + m_user.getDateFormatter().formatTime(date_read);
                reader_list.add(reader);
            }
        } catch ( java.sql.SQLException sqle) {
        	Logger.getLogger(Post.class).debug("Post.getReaderList() threw an SQL exception: " + sqle);
            throw new PersistenceException("failed to load post readers listing " +
                "from database", sqle);
        } finally {
            db.release();
        }

        return reader_list;
    }

    /**
     * Sets the MessageID for the post.
     *
     * @param post_body_id a <code>String</code> value containing the message
     * (body) id for this post.
     */
    public void setPostMessageID (String post_body_id) {
        this.m_post_body_id=post_body_id;
    }

    /**
     * Get the Message ID for the object.
     *
     * @return a <code>String</code> value containing the message id for this
     * post.
     */
    public String getPostMessageID() {
        return this.m_post_body_id;
    }


    /**
     * Converts this post into a string format.
     *
     * @return a <code>String</code> representation of the post
     */
    public String toString() {
        return  "Post ID: " + m_post_id +
            "\nSubject: " + m_subject +
            "\nparent id: " + m_parent_post_id +
            "\nfull name: " + m_person_full_name +
            "\ndate posted: " + m_date_posted +
            "\nnumber views: " + m_number_of_views;
    }


    /**
     * getXMLBody
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     *
     * @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<post>\n");
        xml.append("<id>" + m_post_id + "</id>\n");
        xml.append("<parentId>" + m_parent_post_id + "</parentId>\n");
        xml.append("<views>" + m_number_of_views + "</views>\n");
        xml.append("<groupId>" + m_discussion_group_id + "</groupId>\n");
        xml.append("<groupName>" + m_discussion_group_name + "</groupName>\n");
        xml.append("<subject>" + XMLUtils.escape(m_subject) + "</subject>\n");
        xml.append("<personId>" + m_person_id + "</personId>");
        xml.append("<person>" + XMLUtils.escape(m_person_full_name) + "</person>\n");
        xml.append("<urgency>" + XMLUtils.escape(m_urgency) + "</urgency>\n");
        xml.append("<postBody>" + XMLUtils.escape(m_post_body) + "</postBody>\n");
        xml.append("<datePosted>" + XMLUtils.formatISODateTime(m_date_posted) + "</datePosted>\n");
        xml.append("<Space>\n");
        xml.append("<name>"+XMLUtils.escape(SessionManager.getUser().getCurrentSpace().getName())+"</name>\n");
        xml.append("<SpaceType>"+XMLUtils.escape(SessionManager.getUser().getCurrentSpace().getSpaceType().getName())+"</SpaceType>\n");
        xml.append("</Space>\n");
        xml.append("</post>\n");

        return xml.toString();
    }

    /**
     * Converts the object to XML representation.  This method returns the
     * object as XML text.
     *
     * @return XML representation
     */
    public String getXML() {
        return( IXMLPersistence.XML_VERSION + getXMLBody() );
    }

    /**
     * Does this post have any replies.
     *
     * @return a <code>boolean</code> value indicating whether this post has any
     * replies
     */
    public boolean hasReplies() {
        return replyCount > 0;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    /**
     * Specifies the record status of this post.
     * @param recordStatus the record status
     */
    private void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }

    /**
     * Returns the current record status.
     * @return the current record status
     */
    public RecordStatus getRecordStatus() {
        return this.recordStatus;
    }

    /**
     * load a post from the database.
     *
     */
    public void load() throws PersistenceException {
        if (m_user == null) {
            m_user = net.project.security.SessionManager.getUser();
        }

        String query =
            "select " +
            "  p.discussion_group_id, " +
            "  p.subject, " +
            "  p.parent_id, " +
            "  p.person_id, " +
            "  pn_person.display_name, " +
            "  p.urgency_id, " +
            "  pn_global_domain.code_name as urgency, " +
            "  p.date_posted as post_date, " +
            "  number_of_views.reader_count, " +
            "  p.post_body_id, " +
            "  nvl(replies.reply_count,0) as reply_count," +
            "  p.record_status "+
            "from " +
            "  pn_post p, " +
            "  pn_person, " +
            "  pn_global_domain,  " +
            "  (SELECT " +
            "     pn_post_reader.post_id, " +
            "     COUNT(pn_post_reader.person_id) reader_count  " +
            "   FROM " +
            "     pn_post_reader  " +
            "   GROUP BY " +
            "     pn_post_reader.post_id) number_of_views, " +
            "  (select " +
            "     parent_id as post_id, " +
            "     count(*) as reply_count " +
            "   from " +
            "     pn_post p2 " +
            "   where" +
            "     p2.record_status = 'A' "+
            "   group by parent_id) replies "+
            "WHERE " +
            "  p.post_id = " + m_post_id +
            "  AND replies.post_id(+)=p.post_id "+
            "  AND number_of_views.post_id (+) = p.post_id " +
            "  AND pn_person.person_id = p.person_id " +
            "  AND pn_global_domain.table_name='pn_post' " +
            "  AND pn_global_domain.column_name='urgency_id' " +
            "  AND pn_global_domain.code=p.urgency_id ";

        DBBean db = new DBBean();
        try {
            db.executeQuery(query);

            if (db.result.next()) {
                m_discussion_group_id = db.result.getString("discussion_group_id");
                m_subject = db.result.getString("subject");
                m_parent_post_id = db.result.getString("parent_id");
                m_person_id = db.result.getString("person_id");
                m_person_full_name = db.result.getString("display_name");
                m_urgency_id = db.result.getString("urgency_id");
                m_urgency = PropertyProvider.get(db.result.getString("urgency"));
                m_date_posted = db.result.getTimestamp("post_date");
                m_number_of_views = db.result.getString("reader_count");
                m_post_body_id = db.result.getString("post_body_id");
                replyCount = db.result.getInt("reply_count");
                setRecordStatus(RecordStatus.findByID(db.result.getString("record_status")));

                if (m_post_body_id != null) {
                    m_post_body = loadPostMessage();
                } else {
                    m_post_body = "";
                }
            }
        } catch ( SQLException sqle) {
        	Logger.getLogger(Post.class).debug("Post.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("failed to load  post from database", sqle);
        } finally {
            db.release();
        }

        setLoaded(true);
    }

    /**
     * Store a new post.
     */
    public void store() throws PersistenceException {
        DBBean db = new DBBean();

        try {
            String parent = null;
            boolean hasParent = false;

            // Set the parent post id for this Post store to the appropriate value
            if ((m_parent_post_id == null) || (m_parent_post_id.length() == 0)) {
                parent = "NULL";
                hasParent = false;

            } else {
                parent = m_parent_post_id;
                hasParent = true;

            }

            db.openConnection();
            db.connection.setAutoCommit(false);

            // First store the Post message in a CLOB row
            net.project.database.Clob clob = new net.project.database.Clob(db, POST_CLOB_TABLE_NAME, "OBJECT_ID", "CLOB_FIELD");
            clob.setData(m_post_body);
            clob.store();

            // call Stored Procedure to insert or update all the tables involved in storing a discussion group.
            int datePostedIndex = 0;
            int postIDIndex = 0;
            int index = 0;

            db.prepareCall("begin DISCUSSION.CREATE_POST(?,?,?,?,?,?,?,?, ?); end;");
            db.cstmt.setInt(++index, Integer.parseInt(m_user.getCurrentSpace().getID()));
            db.cstmt.setInt(++index, Integer.parseInt(m_discussion_group_id));
            if (hasParent) {
                db.cstmt.setInt(++index, Integer.parseInt(parent));
            } else {
                db.cstmt.setNull(++index, java.sql.Types.INTEGER);
            }
            db.cstmt.setInt(++index, Integer.parseInt(m_person_id));
            db.cstmt.setString(++index, m_subject);
            db.cstmt.setInt(++index, Integer.parseInt(m_urgency_id));
            db.cstmt.setInt(++index, Integer.parseInt(clob.getID()));
            db.cstmt.registerOutParameter((datePostedIndex = ++index), java.sql.Types.TIMESTAMP);
            db.cstmt.registerOutParameter((postIDIndex = ++index), java.sql.Types.INTEGER);
            db.executeCallable();
            m_date_posted = db.cstmt.getTimestamp(datePostedIndex);
            m_post_id = Integer.toString(db.cstmt.getInt(postIDIndex));

            db.commit();

            // Now create the event
            DiscussionEvent event = new DiscussionEvent();
            event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
            event.setUser(SessionManager.getUser());

            if (hasParent && m_parent_post_id != null && discussionGroup != null) {
                Post parentPost = discussionGroup.getPost(m_parent_post_id);
                event.setDescription("Created : \"" + parentPost.getName() + "\"");
                event.setTargetObjectXML(parentPost.getXMLBody());
                event.setTargetObjectID(m_parent_post_id);
                event.setTargetObjectType(EventCodes.CREATE_REPLY);
                event.setEventType(EventCodes.CREATE_REPLY);
                event.setName(EventCodes.getName(EventCodes.CREATE_REPLY));
                event.store();
               //Another event for the discussion group
               // event.setTargetObjectID(getDiscussionGroupID());
               // event.setTargetObjectType(EventCodes.CREATE_DISCUSSION_POST);
               //  event.store();
            } else {
                event.setDescription("Created : \"" + getName() + "\"");
                event.setTargetObjectXML( this.getXMLBody() );
                event.setTargetObjectID(getDiscussionGroupID());
                event.setTargetObjectType(EventCodes.CREATE_DISCUSSION_POST);
                event.setEventType(EventCodes.CREATE_DISCUSSION_POST);
                event.setName(EventCodes.getName(EventCodes.CREATE_DISCUSSION_POST));
                event.store();
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("Error storing post: " + sqle, sqle);

        } catch (NumberFormatException nfe) {
            throw new PersistenceException("ParseInt Failed in Post.store(): " + nfe, nfe);

        } finally {
            db.release();
        }

    }

    /**
     * Remove a post and assign all of the child posts to this posts parent.
     * That is, if you had the following tree:
     *
     * <pre><code>
     * A
     * --> A.1
     * --------> A.1.1
     * --------> A.1.2
     * --> A.2
     * </code></pre>
     *
     * If you were to delete <code>A.1</code>, <code>A.1.1</code> and
     * <code>A.1.2</code> would both have A as their parent now, resulting in
     * the following tree:
     *
     * <pre><code>
     * A
     * --> A.1.1
     * --> A.1.2
     * --> A.2
     * </code></pre>
     */
    public void remove() throws PersistenceException {
        //Delete the post
        DBBean db = new DBBean();
        try {
            //First, assign any post whose parent is this post to have this
            //post's parent
            db.prepareStatement("update pn_post set parent_id = ? where parent_id = ?");
            db.pstmt.setString(1, getParentPostID());
            db.pstmt.setString(2, getID());
            db.executePrepared();

            db.prepareStatement("update pn_post set record_status = 'D' where post_id = ?");
            db.pstmt.setString(1, getID());
            db.executePrepared();
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to delete discussion post " +
                "due to an unexpected exception: "+sqle,sqle);
        } finally {
            db.release();
        }

        //Send out a notification about the removal of the post
        DiscussionEvent event = new DiscussionEvent();
        event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
        event.setTargetObjectID(getID());
        event.setTargetObjectType(EventCodes.REMOVE_POST);
        event.setTargetObjectXML( this.getXMLBody() );
        event.setEventType(EventCodes.REMOVE_POST);
        event.setName(EventCodes.getName(EventCodes.REMOVE_POST));
        event.setUser(SessionManager.getUser());
        event.setDescription("Post Removed : \"" + getName() + "\"");
        event.store();

        //Send out a notification in the case where there is a modify discussion
        //group notification set.
        //new event object must be created here because method event.store() add objects in list
        //if we object in list and then chenge it properties will also change properties of
        //the one in the list (IT IS THE SAME ONE!!!)
        DiscussionEvent groupEvent = new DiscussionEvent();
        groupEvent.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
        groupEvent.setTargetObjectID(getDiscussionGroupID());        
        groupEvent.setTargetObjectType(EventCodes.REMOVE_DISCUSSION_POST);
        groupEvent.setTargetObjectXML( this.getXMLBody() );
        groupEvent.setEventType(EventCodes.REMOVE_DISCUSSION_POST);
        groupEvent.setName(EventCodes.getName(EventCodes.REMOVE_DISCUSSION_POST));
        groupEvent.setUser(SessionManager.getUser());
        groupEvent.setDescription("Post Removed : \"" + getName() + "\"");        
        groupEvent.store();
    }

    /**
     * Remove a post and all "subposts" underneath that post.
     */
    public void removeRecursive() throws PersistenceException {
        Set postsToDelete = new HashSet();
        DBBean db = new DBBean();
        try {
            db.prepareStatement(
                "select post_id, record_status " +
                "from pn_post " +
                "connect by parent_id = prior post_id start with post_id = ?"
            );
            db.pstmt.setString(1, getID());
            db.executePrepared();
            while (db.result.next()) {
                if (db.result.getString("record_status").equals("A")) {
                    postsToDelete.add(db.result.getString("post_id"));
                }
            }

            db.prepareStatement(
                "update pn_post "+
                "set record_status = 'D' "+
                "where post_id in "+
                "  (SELECT post_id "+
                "   FROM pn_post "+
                "   CONNECT BY parent_id = prior post_id start with post_id = ?) "
            );
            db.pstmt.setString(1, getID());
            db.executePrepared();
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to delete discussion post " +
                "due to an unexpected exception: "+sqle,sqle);
        } finally {
            db.release();
        }

        //Send out a notification about the removals
        if (discussionGroup != null) {
            for (Iterator it = postsToDelete.iterator(); it.hasNext();) {
                String postID = (String)it.next();
                Post post = discussionGroup.getPost(postID);

                DiscussionEvent event = new DiscussionEvent();
                event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
                event.setTargetObjectID(post.getID());
                event.setTargetObjectType(EventCodes.REMOVE_POST);
                event.setTargetObjectXML( post.getXMLBody() );
                event.setEventType(EventCodes.REMOVE_POST);
                event.setName(EventCodes.getName(EventCodes.REMOVE_POST));
                event.setUser(SessionManager.getUser());
                event.setDescription("Post Removed : \"" + post.getName() + "\"");
                event.store();

                DiscussionEvent groupEvent = new DiscussionEvent();
                groupEvent.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
                groupEvent.setTargetObjectID(getDiscussionGroupID());        
                groupEvent.setTargetObjectType(EventCodes.REMOVE_DISCUSSION_POST);
                groupEvent.setTargetObjectXML( post.getXMLBody() );
                groupEvent.setEventType(EventCodes.REMOVE_DISCUSSION_POST);
                groupEvent.setName(EventCodes.getName(EventCodes.REMOVE_DISCUSSION_POST));
                groupEvent.setUser(SessionManager.getUser());
                groupEvent.setDescription("Post Removed : \"" + getName() + "\"");        
                groupEvent.store();                
            }
        }
    }


    /**
     * Loads the post message from the appropriate clob object for this
     * post object.
     *
     * @throws PersistenceException if there is a problem loading the data
     */
    public String loadPostMessage() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.setClobTableName(POST_CLOB_TABLE_NAME);
            /*
              Set the history message by getting the clob for the current
              post message id and grabbing its data
            */
            return db.getClob(getPostMessageID()).getData();
        } finally {
            // Release must be performed since db.getClob sets auto-commit
            db.release();
        }
    }

    /**
     * Get the parent of the post in the threaded view.
     *
     * @return a <code>String</code> value containing the primary key of the
     * parent of this post.
     */
    public String getParentPostID() {
        return m_parent_post_id;
    }

    /**
     * Set the parent of the post as you would see in the threaded view.
     *
     * @param postID a <code>String</code> value containing the primary key of
     * the parent of this post.
     */
    public void setParentPostID(String postID) {
        this.m_parent_post_id = postID;
    }

    /**
     * Get the number of times that this post has been viewed.
     *
     * @return a <code>String</code> value which contains an integer which
     * indicates the number of times this post has been viewed.
     */
    public String getNumberOfViews() {
        return m_number_of_views;
    }

    /**
     * Set the number of times that this post has been viewed.
     *
     * @param numberOfViews a <code>String</code> value containing an integer
     * which indicates the number of times this post has been viewed.
     */
    public void setNumberOfViews(String numberOfViews) {
        this.m_number_of_views = numberOfViews;
    }

    /**
     * Get the primary key of the discussion group that this post belongs to.
     *
     * @return a <code>String</code> value indicating the primary key of the
     * discussion group to which this post belongs.
     */
    public String getDiscussionGroupID() {
        return m_discussion_group_id;
    }

    /**
     * Set the primary key of the discussion group to which this post belongs.
     *
     * @param discussionGroupID a <code>String</code> value indicating the
     * primary key of the discussion group to which this post belongs.
     */
    public void setDiscussionGroupID(String discussionGroupID) {
        this.m_discussion_group_id = discussionGroupID;
    }

    /**
     * Set the name of the discussion group to which this post belongs.
     *
     * @param discussionGroupName a <code>String</code> containing the name of
     * the discussion group to which this post belongs.
     */
    public void setDiscussionGroupName(String discussionGroupName) {
        this.m_discussion_group_name = discussionGroupName;
    }

    /**
     * Get the name of the discussion group to which this post belongs.
     *
     * @return a <code>String</code> value containing the name of the group to
     * which this post belongs.
     */
    public String getDiscussionGroupName() {
        return m_discussion_group_name;
    }

    /**
     * Get the primary key of the person id who created the post.
     *
     * @return a <code>String</code> value indicating the primary key of the
     * user who created the post.
     */
    public String getAuthorID() {
        return m_person_id;
    }

    /**
     * Set the primary key of the person who created the post.
     *
     * @param personID a <code>String</code> value containing the primary key of
     * the person who created the post.
     */
    public void setAuthorID(String personID) {
        m_person_id = personID;
    }

    /**
     * Get the date on which the author of this post published this post in the
     * discussion group.
     *
     * @return a <code>Date</code> value on which the post was originally
     * published.
     */
    public Date getDatePosted() {
        return m_date_posted;
    }

    /**
     * Set the date on which this post was published.
     *
     * @param datePosted a <code>Date</code> value which indicates the date on
     * which this post was originally published.
     */
    public void setDatePosted(Date datePosted) {
        this.m_date_posted = datePosted;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;

        final Post post = (Post)o;

        if (m_post_id != null ? !m_post_id.equals(post.m_post_id) : post.m_post_id != null) return false;

        return true;
    }

    public int hashCode() {
        return (m_post_id != null ? m_post_id.hashCode() : 0);
    }
}

