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

import net.project.base.property.PropertyProvider;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.discussion.postlist.FlatPostList;
import net.project.discussion.postlist.IPostListMaintainer;
import net.project.discussion.postlist.ThreadedPostList;
import net.project.notification.EventCodes;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.ErrorLogger;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * This class is a discussion group which is responsible for managing the posts
 * within it.
 *
 * @author AdamKlatzkin
 * @since 01/00
 */
public class DiscussionGroup implements IJDBCPersistence, IXMLPersistence, java.io.Serializable {
    public static final int VIEW_THREADED = 0;
    public static final int VIEW_FLAT = 1;
    public static final int VIEW_UNREADPOSTS = 2;
    public static final String[] VIEW_STRINGS = {
        "prm.discussion.group.view.option.threaded.name",
        "prm.discussion.group.view.option.flat.name",
        "prm.discussion.group.view.option.unreadfirst.name"
    };    // transformed into token values in "DiscussionGroupBean.java"

    public static final int SORT_SUBJECT = 0;
    public static final int SORT_NAME = 1;
    public static final int SORT_DATE = 2;
    public static final int SORT_URGENCY = 3;

    public static final int ORDER_ASCENDING = 0;
    public static final int ORDER_DESCENDING = 1;
    public static final String[] ORDER_STRINGS = {"asc", "desc"};

    public int m_view = VIEW_THREADED;
    public int m_sort = SORT_DATE;
    public int m_order = ORDER_ASCENDING;

    /** The primary key of this discussion group. */
    private String id = null;
    /** The human-readable name of this discussion group. */
    private String name = null;
    /** Additional text describing what the discussion group was designed for. */
    private String description = null;
    /**
     * A <code>String</code> value containing the charter for this discussion
     * group.
     */
    private String charter = null;
    /** The total number of active posts in this discussion group. */
    private String numPosts = null;
    /** The total number of unread posts in this discussion group. */
    private String unreadCount = null;
    /** The space in which this discussion group resides. */
    private Space space = null;
    private String objectID = null;
    private User user = null;

    // Database access bean
    protected IPostListMaintainer m_PostListMaintainer = null;

    /**
     * Standard Constructor which creates a new discussion group.
     */
    public DiscussionGroup() {
    }

    /**
     * Set the id of this discussion group.  This is generally done when we are
     * going to load the group.
     *
     * @param id a <code>id</code> containing the primary key of this discussion
     * group.
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Get the database id of this discussion group.
     *
     * @return a <code>String</code> value containing the database id of this
     * discussion group.
     */
    public String getID() {
        return this.id;
    }

    /**
     * Set a description of the rules governing the use of this discussion
     * group.
     *
     * @see #getCharter
     * @since Gecko Update 2
     * @param charter a <code>String</code> value containing the rules governing
     * the use of this group.
     */
    public void setCharter(String charter) {
        this.charter = charter;
    }

    /**
     * Get the rules governing the use of this discussion group.
     *
     * @see #setCharter
     * @since Gecko Update 2
     * @return a <code>String</code> value containing the rules governing the
     * use of this discussion group.
     */
    public String getCharter() {
        return this.charter;
    }

    /**
     * Set the description of this discussion group.
     *
     * @see #getDescription
     * @since Gecko Update 2
     * @param description a <code>String</code> value containing the description of
     * this discussion group.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the description of this discussion group.
     *
     * @see #setDescription
     * @since Gecko Update 2
     * @return a <code>String</code> value containing the description of this
     * discussion group.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the name of this discussion group as will appear in the title of the
     * discussion group.
     *
     * @see #getName
     * @since Gecko Update 2
     * @param name a <code>String</code> value containing the
     * name of this discussion group.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the name of this discussion group.
     *
     * @see #setName
     * @since Gecko Update 2
     * @return a <code>String</code> value containing the name of this discussion
     * group.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the number of posts that have been made to this discussion group.
     *
     * @since Gecko Update 2
     * @see #getNumPosts
     * @param numPosts a <code>String</code> value containing a the number
     * of posts made to this group.
     */
    public void setNumPosts(String numPosts) {
        this.numPosts = numPosts;
    }

    /**
     * Describe <code>getNumPosts</code> method here.
     *
     * @since Gecko Update 2
     * @see #setNumPosts
     * @return a <code>String</code> value
     */
    public String getNumPosts() {
        return numPosts;
    }

    /**
     * A discussion group is associated with either a space or an object.
     * To define or load a discussion group you must call this method or setSpace.
     *
     * @param objectID a <code>String</code> value indicating the object that the
     * discussion belongs to
     * @since 01/00
     */
    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    /**
     * Get the object id that this discussion group belongs to.
     *
     * @return a <code>String</code> value containing the object id that this
     * discussion group belongs to.
     */
    public String getObjectID() {
        return this.objectID;
    }

    /**
     * Set the space that this discussion group belongs to.
     *
     * A discussion group is associated with either a space or an object.  To define
     * or load a discussion group you must call this method or {@link #setSpace}
     *
     * (MAF) Note: this method has the side effect of setting the object id as well
     * as the space.
     *
     * @since 01/00
     * @see #getSpace
     * @param space the space context for this discussion group.
     */
    public void setSpace(Space space) {
        this.space = space;
        this.objectID = space.getID();
    }

    /**
     * Get the space that this discussion group belongs to.
     *
     * @since Gecko Update 2
     * @see #setSpace
     * @return a <code>Space</code> value for this discussion group.
     */
    public Space getSpace() {
        return this.space;
    }

    /**
     * Set whether or not this discussion group has been read.
     *
     * @since Gecko Update 2
     * @see #getUnread
     * @param unread a <code>String</code> value.
     */
    public void setUnread(String unread) {
        this.unreadCount = unread;
    }

    /**
     * Get a string indicating whether this discussion group has
     * been read.
     *
     * @since Gecko Update 2
     * @see #setUnread
     * @return a <code>String</code> value
     */
    public String getUnread() {
        return this.unreadCount;
    }

    /**
     * Set the user context for this group.
     *
     * @since 01/00
     * @see #getUser
     * @param user an <code>User</code> value indicating the user context of
     * this discussion group.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Get the user context for this discussion group.
     *
     * @since Gecko Update 2
     * @see #setUser
     * @return an <code>User</code> value
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Is the current discussion group displayed in threaded mode.
     * @return true if threaded, false otherwise.
     */
    public boolean isThreaded() {
        return (m_view == VIEW_THREADED);
    }

    /**
     * Add a new post to the discussion group
     *
     * @param post   the post to add
     */
    public void newPost(Post post) throws PersistenceException {
        post.setAuthorID(user.getID());
        post.setAuthorDisplayName(user.getDisplayName());
        // When a user creates a post he is automatically set as a reader
        post.setRead(true);
        post.setNumberOfViews("1");
        post.setDiscussionGroupID(id);
        post.setDiscussionGroupName(name);
        post.setUser(user);
        post.store();

        if (isThreaded()) {
            // the threaded maintainer is capable of supporting a cache
            // because it does its own sorting
            m_PostListMaintainer.addPostToList(post);

            //Walk up the parent tree and increment the reply count for each.
            String parentPostID = post.getParentPostID();
            while (!Validator.isBlankOrNull(parentPostID)) {
                Post parentPost = getPost(parentPostID);
                parentPost.setReplyCount(parentPost.getReplyCount()+1);

                //Posts without parents have their parent post id set to their own id.
                //(Why is a good question -- I'm not sure.)
                if (!parentPostID.equals(parentPost.getParentPostID())) {
                    parentPostID = parentPost.getParentPostID();
                } else {
                    parentPostID = null;
                }
            }

            //Calls to the getPost() method will affect the position of the
            //current post pointer.  Reset it.
            m_PostListMaintainer.setCurrentPostID(post.getID());
        } else {
            // non-threaded maintainer's must be reloaded to allow the database
            // to resort the post list
            load();
        }
    }

    /**
     * Remove a post from the discussion group.  This method will both remove
     * the post from the internal group cache and run the "remove" method of
     * the post.
     *
     * @param post a <code>Post</code> object that we are going to remove from
     * the database and from the internal cache.
     * @throws PersistenceException if there is a failure deleting the post from
     * the database.
     */
    public void removePost(Post post, boolean recursive) throws PersistenceException {
        if (recursive) {
            post.removeRecursive();

            //We need to do some special handling here to figure out which node
            //would have been selected if we weren't doing a reload.  If we don't,
            //we wouldn't know which post to select.
            m_PostListMaintainer.selectPostDeleteNode(post.getID());
            String currentPostID = m_PostListMaintainer.getCurrentPostID();
            load();
            m_PostListMaintainer.setCurrentPostID(currentPostID);
            return;
        } else {
            post.remove();
            m_PostListMaintainer.removePostFromList(post.getID());
        }

        //Walk up the parent tree and reduce all of the reply counts
        String currentPost = m_PostListMaintainer.getCurrentPostID();
        String parentPostID = post.getParentPostID();
        while (!Validator.isBlankOrNull(parentPostID)) {
            Post parentPost = getPost(parentPostID);
            parentPost.setReplyCount(parentPost.getReplyCount()-1);
            //Posts without parents have their parent post id set to their own id.
            //(Why is a good question -- I'm not sure.)
            if (!parentPostID.equals(parentPost.getParentPostID())) {
                parentPostID = parentPost.getParentPostID();
            } else {
                parentPostID = null;
            }
        }
        m_PostListMaintainer.setCurrentPostID(currentPost);
    }

    /**
     * Get a post in the thread list without populating the body
     *
     * @param postID the post's id number
     * @return a <code>Post</code> object which matches the postID parameter
     * passed to this method.
     */
    public Post getPostHeader(String postID) {
        return m_PostListMaintainer.getPostHeader(postID);
    }

    /**
     * Get a post in the thread list.
     *
     * @param postID the post's id number
     * @return the post which matches the postID parameter passed to this method.
     */
    public Post getPost(String postID) throws PersistenceException {
        Post post = m_PostListMaintainer.getPost(postID);
        post.setDiscussionGroupID(getID());
        post.setDiscussionGroup(this);
        populatePost(post);
        return post;
    }

    /**
     * Get the first post in the current display.
     *
     * @return the first post in the current display list.
     */
    public Post getFirstPost() throws PersistenceException {
        Post post = m_PostListMaintainer.getFirstPost();
        populatePost(post);
        return post;
    }

    /**
     * Get the post that following this previously read post.
     * @return the first next psot in the current display list.
     */
    public Post getNextPost() throws PersistenceException {
        Post post = m_PostListMaintainer.getNextPost();
        populatePost(post);
        return post;
    }

    /**
     * Get the post before the last read post.
     * @return Post  the previous post crossing thread boundries if required,
     *               the current Post if there is no previous post.
     */
    public Post getPreviousPost() throws PersistenceException {
        Post post = m_PostListMaintainer.getPreviousPost();
        populatePost(post);
        return post;
    }

    /**
     * Get the posts which have not been read by this user.
     * If threaded is set true, the top of each thread will be sorted in the order specified.
     * The posts within each thread at the same level will be sorted as specified.
     * <br>
     * Valid options for order_by are defined in DiscussionProvider and include:
     * <br>
     * DiscussionProvider.SUBJECT
     * <br>
     * DiscussionProvider.DATE
     * <br>
     * DiscussionProvider.AUTHOR
     * @param order_by the field to sort the posts by.
     * @param threaded group the posts by thread.
     * @param use_cache get the posts from the object cache and reorder as specified.
     *        If set false or no cache exists, the posts will be extracted from the database.
     * @return an ArrayList of Post objects.
     */
    public ArrayList getUnreadPosts(int order_by, boolean threaded, boolean use_cache) {
        // Not Yet Implemented
        return null;
    }

    /**
     * should be called to flip the expanded/collapse state of a thread
     * starting from a specified post.
     *
     * @param    postId      the post id of the post whose thread UI state should
     *                       be flipped
     */
    public void handleThreadClick(String postId) {
        if (isThreaded()) {
            // invertThreadViewState is not defined by IPostListMaintainer.
            // Since it only exists for ThreadedPostList and currently
            // that class is the only existing class to handle threaded
            // views, I will just cast the current maintainer into
            // the class.  This should be generalized.
            ((ThreadedPostList)m_PostListMaintainer).invertThreadViewState(postId);
        }
    }

    /**
     * Clear removes any previous state that is stored in this javabean,
     * returning it to its default state after creation.
     */
    public void clear() {
        id = null;
        name = null;
        description = null;
        numPosts = null;
        unreadCount = null;
        space = null;
        objectID = null;
        user = null;
        m_PostListMaintainer = null;
    }

    /**
     * Load the discussion group from persistent storage.
     */
    public void load() throws PersistenceException {
        // This should be a runtime decision based on the list type the user
        // wants to display.  For now we will only support a threaded list.
        if (isThreaded()) {
            m_PostListMaintainer = new ThreadedPostList(user, id);
        } else {
            m_PostListMaintainer = new FlatPostList(user, id);
        }

        // load the posts associated with the discussion group
        // Note: this does not load the post bodies.  They are loaded with each post read.
        loadPosts();
    }

    /*
     * initialize/load group properties
     * @param String groupId
     * Id is explicitly kept as parameter, because this.id could be postId 
     * @see loadFromPostID it assumed id is post id 
     * Keeping groupId separate with method scope will limit any impact or conflicts
     */
    public void loadGroup( String groupId ) throws PersistenceException{
    	
    	DBBean db = new DBBean();
    	
    	String query =
            "SELECT " +
            "  dg.DISCUSSION_GROUP_NAME, " +
            "  dg.DISCUSSION_GROUP_DESCRIPTION, " +
            "  dg.DISCUSSION_GROUP_CHARTER_CLOB " +
            "FROM " +
            "  pn_discussion_group dg " +
    		"WHERE " +
    		"  dg.DISCUSSION_GROUP_ID = ? ";

        try {
            db.setAutoCommit(false);

            // prepare statement to retieve group properties
            db.prepareStatement(query);
            db.pstmt.setInt(1, Integer.parseInt(groupId));
            
            db.executePrepared();
            
            if (db.result.next()) {
	            this.setName(db.result.getString(1));
	            this.setDescription(db.result.getString(2));
	            this.setCharter(ClobHelper.read(db.result.getClob(3)));
            }
            
        } catch (java.sql.SQLException sqle) {
        	Logger.getLogger(DiscussionGroup.class).debug("DiscussionGroup.loadGroup() threw an SQL exception: " + sqle);
            throw new PersistenceException("failed to load group details from database", sqle);
        } finally {
            db.release();
        }
    	
    }
    
    /**
     * the current set id is actually a post id.  Determine what discussion
     * group it belongs to and load that group.
     */
    public void loadFromPostID() throws PersistenceException {
        if (id == null)
            throw new PersistenceException("Post ID must be set");

        String query = "select p.discussion_group_id, dg.discussion_group_name " +
            " from pn_post p, pn_discussion_group dg where p.post_id = " + id +
            " AND dg.discussion_group_id = p.discussion_group_id and p.record_status = 'A'";

        // The current discussion group id is actually a post id.
        // Determine what discussion group it belongs to and set the id

        DBBean db = new DBBean();
        try {
            db.executeQuery(query);

            if (db.result.next()) {
                id = db.result.getString(1);
                name = db.result.getString(2);
            }
        } catch (java.sql.SQLException sqle) {
        	Logger.getLogger(DiscussionGroup.class).debug("DiscussionGroup.loadFromPostID() threw an SQL exception: " + sqle);
            throw new PersistenceException("failed to load group post listing " +
                "from database", sqle);
        } finally {
            db.release();
        }

        // This should be a runtime decision based on the list type the user
        // wants to display.  For now we will only support a threaded list.
        if (isThreaded()) {
            m_PostListMaintainer = new ThreadedPostList(user, id);
        } else {
            m_PostListMaintainer = new FlatPostList(user, id);
        }

        // load the posts associated with the discussion group
        // Note: this does not load the post bodies.  They are loaded with each post read.
        loadPosts();
    }

    /**
     * Store a new discussion group.
     * @throws PersistenceException if there is a problem storing
     */
    public void store() throws PersistenceException {

        DBBean db = new DBBean();

        try {
            db.setAutoCommit(false);

            // Pass parameters for creating the welcome message
            String[] props = {name, description, StringUtils.isEmpty(charter) ? "" : charter};
            String welcomeMessagePost = PropertyProvider.get("prm.discussion.discussiongroup.create.welcomemessage.post", props);
            String welcomeMessageSubject = PropertyProvider.get("prm.discussion.discussiongroup.create.welcomemessage.subject");

            int index = 0;
            int groupIDIndex = 0;
            int welcomeMessageClobIndex = 0;
            int charterClobIndex = 0;

            // call Stored Procedure to insert or update all the tables involved in storing a discussion group.
            db.prepareCall("{call DISCUSSION.CREATE_DISCUSSION_GROUP(?,?,?,?,?,?,?,?, ?,?,?)}");

            boolean isCreating;
            if (id == null) {
                isCreating = true;
                db.cstmt.setNull(++index, java.sql.Types.INTEGER);
            } else {
                isCreating = false;
                db.cstmt.setInt(++index, Integer.parseInt(id));
            }

            db.cstmt.setString(++index, space.getID());
            db.cstmt.setString(++index, objectID);
            db.cstmt.setString(++index, name);
            db.cstmt.setInt(++index, Integer.parseInt(user.getID()));
            db.cstmt.setString(++index, description);
            // Indicates we want to create a welcome message
            // This is ignored if we're updating a discussion group
            // Same with Subject
            db.cstmt.setInt(++index, 1);
            db.cstmt.setString(++index, welcomeMessageSubject);
            db.cstmt.registerOutParameter((groupIDIndex = ++index), java.sql.Types.INTEGER);
            // WelcomeMessageClobIndex only returned if creating
            db.cstmt.registerOutParameter((welcomeMessageClobIndex = ++index), java.sql.Types.CLOB);
            db.cstmt.registerOutParameter((charterClobIndex = ++index), java.sql.Types.CLOB);

            db.executeCallable();

            // Whether creating or updating we rewrite the charter
            // Get the clob locater and write the charter to the charter CLOB
            ClobHelper.write(db.cstmt.getClob(charterClobIndex), this.charter);

            if (isCreating) {
                // Grab the newly created ID
                this.id = Integer.toString(db.cstmt.getInt(groupIDIndex));

                // Write the welcome message to the clob
                // Only done once when creating a group; when updating
                // a handle is not even returned
                ClobHelper.write(db.cstmt.getClob(welcomeMessageClobIndex), welcomeMessagePost);
            }

            // Commit results
            db.commit();

            // Now generate an event
            DiscussionEvent event = new DiscussionEvent();
            event.setSpaceID(this.user.getCurrentSpace().getID());
            event.setTargetObjectID(getID());
            event.setUser(this.user);
            event.setTargetObjectXML(this.getXMLBody());
            if(isCreating){
	            event.setEventType(EventCodes.CREATE_DISCUSSION_GROUP);
	            event.setName(EventCodes.getName(EventCodes.CREATE_DISCUSSION_GROUP));
	            event.setTargetObjectType(EventCodes.CREATE_DISCUSSION_GROUP);            
	            event.setDescription("New Discussion Group created : \"" + this.name + "\"");
            } else {
	            event.setEventType(EventCodes.MODIFY_DISCUSSION_GROUP);
	            event.setName(EventCodes.getName(EventCodes.MODIFY_DISCUSSION_GROUP));
	            event.setTargetObjectType(EventCodes.MODIFY_DISCUSSION_GROUP);            
	            event.setDescription("Discussion Group Modified : \"" + this.name + "\"");            	
            }
            event.store();

        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
                // Do nothing
            }
            throw new PersistenceException("Error storing discussion group: " + sqle, sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Set the status code of the discussion group to 'D' so that it will no
     * longer appear in the list of discussion groups.
     */
    public void remove() throws PersistenceException {
        if (id == null)
            throw new NullPointerException("discussion id is null");

        String query = "UPDATE pn_discussion_group SET record_status='D' WHERE discussion_group_id=" + id;

        DBBean db = new DBBean();
        try {
            db.executeQuery(query);
            DiscussionEvent event = new DiscussionEvent();
            event.setTargetObjectID(getID());
            event.setSpaceID(this.user.getCurrentSpace().getID());
            event.setTargetObjectType(EventCodes.REMOVE_DISCUSSION_GROUP);
            event.setTargetObjectXML(this.getXMLBody());
            event.setEventType(EventCodes.REMOVE_DISCUSSION_GROUP);
            event.setName(EventCodes.getName(EventCodes.REMOVE_DISCUSSION_GROUP));
            event.setUser(this.user);
            event.setDescription("Discussion Group deleted : \"" + this.name + "\"");
            event.store();
        } catch (java.sql.SQLException sqle) {
        	Logger.getLogger(DiscussionGroup.class).debug("DiscussionGroup.remove() threw an SQL exception: " + sqle);
            throw new PersistenceException("could not remove discussion group", ErrorLogger.HIGH);
        } finally {
            db.release();
        }
    }

    /**
     * Load all the posts in this discussion group.
     */
    private void loadPosts() throws PersistenceException {
        String query =
            "SELECT " +
            "  pn_post.post_id, " +
            "  pn_post.subject, " +
            "  pn_post.parent_id, " +
            "  pn_person.display_name, " +
            "  pn_global_domain.code_name as urgency, " +
            "  pn_post.date_posted as post_date, " +
            "  number_of_views.reader_count, " +
            "  SUBSTR(CONCAT(has_user_read.state, '0'), 0, 1) read_state,   " +
            "  nvl(replies.reply_count,0) as reply_count " +
            "FROM " +
            "  pn_post, " +
            "  pn_person, " +
            "  pn_global_domain,  " +
            "  (SELECT " +
            "     pn_post_reader.post_id, " +
            "     COUNT(pn_post_reader.person_id) reader_count  " +
            "   FROM pn_post_reader  " +
            "   GROUP BY pn_post_reader.post_id) number_of_views,  " +
            "  (SELECT " +
            "     pn_post_reader.post_id, " +
            "     COUNT(pn_post_reader.post_id) state  " +
            "   FROM pn_post_reader  " +
            "   WHERE pn_post_reader.person_id = " + user.getID() + "  " +
            "   GROUP BY pn_post_reader.post_id ) has_user_read,   " +
            "  (select      " +
            "     parent_id as post_id, " +
            "     count(*) as reply_count" +
            "   from " +
            "     pn_post p2 " +
            "   where" +
            "     p2.record_status = 'A' "+
            "   group by parent_id) replies " +
            "WHERE " +
            "   pn_post.discussion_group_id = " + id +
            "   AND replies.post_id(+)=pn_post.post_id " +
            "   AND number_of_views.post_id (+) = pn_post.post_id " +
            "   AND has_user_read.post_id (+) = pn_post.post_id " +
            "   AND pn_person.person_id = pn_post.person_id " +
            "   AND pn_global_domain.table_name='pn_post' " +
            "   AND pn_global_domain.column_name='urgency_id' " +
            "   AND pn_global_domain.code=pn_post.urgency_id " +
            "   AND pn_post.record_status = 'A' ";

        if (isThreaded()) {
            query += "ORDER BY pn_post.date_posted";
        } else {
            if (m_view == VIEW_UNREADPOSTS)
                query += "ORDER BY read_state asc, ";
            else
                query += "ORDER BY ";

            switch (m_sort) {
                case SORT_SUBJECT:
                    query += "UPPER(pn_post.subject) " + ORDER_STRINGS[m_order];
                    break;
                case SORT_NAME:
                    query += "UPPER(pn_person.display_name) " + ORDER_STRINGS[m_order];
                    break;
                case SORT_DATE:
                    query += "post_date " + ORDER_STRINGS[m_order];
                    break;
                case SORT_URGENCY:
                    break;
            }
        }

        DBBean db = new DBBean();
        try {
            db.executeQuery(query);

            while (db.result.next()) {
                Post post = new Post();
                post.setUser(user);
                post.setID(db.result.getString("post_id"));
                post.setSubject(db.result.getString("subject"));
                post.setParentPostID(db.result.getString("parent_id"));
                post.setAuthorDisplayName(db.result.getString("display_name"));
                post.setUrgency(PropertyProvider.get(db.result.getString("urgency")));
                post.setDatePosted(db.result.getTimestamp("post_date"));
                post.setNumberOfViews(db.result.getString("reader_count"));
                post.setReplyCount(db.result.getInt("reply_count"));

                String state = db.result.getString("read_state");
                if ((state == null) || (state.equals("0")))
                    post.setRead(false);
                else
                    post.setRead(true);

                m_PostListMaintainer.addPostToList(post);
            }
        } catch (java.sql.SQLException sqle) {
        	Logger.getLogger(DiscussionGroup.class).debug("DiscussionGroup.loadPosts() threw an SQL exception: " + sqle);
            throw new PersistenceException("failed to load group post listing " +
                "from database", sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Populate a post object by obtaining its body from the database.
     * This is performed when the user reads a post, therefore this method
     * will update the user as a post reader if necessary.
     *
     * @param post the post to populate
     */
    private void populatePost(Post post) throws PersistenceException {
        if ((post.isLoaded()) || (post.getID() == null) || (post.getID().equals("-1"))) {
            return;
        }

        String query =
            "select " +
            "  pn_post.post_body_id, " +
            "  pn_post.person_id " +
            "from " +
            "  pn_post, " +
            "  pn_person " +
            "where " +
            "  pn_post.post_id = " + post.getID() + " " +
            "  and pn_person.person_id = pn_post.person_id " +
            "  and pn_post.record_status = 'A'";

        int errorCode = 0;
        DBBean db = new DBBean();
        try {
            db.executeQuery(query);

            if (db.result.next()) {
                post.setPostMessageID(db.result.getString(1));
                if (post.getPostMessageID() != null) {
                    post.setPostbody(post.loadPostMessage());
                } else {
                    post.setPostbody("");
                }

                post.setAuthorID(db.result.getString(2));
            }

            post.setLoaded(true);
            if (!post.hasRead()) {
                post.setRead(true);
                post.setNumberOfViews(Integer.toString(Integer.parseInt(post.getNumberOfViews()) + 1));
            }

            // insert the user as a reader if necessary
            db.prepareCall("begin DISCUSSION.UPDATE_POST_READER(?,?,?,?); end;");
            db.cstmt.setInt(1, Integer.parseInt(user.getID()));
            db.cstmt.setInt(2, Integer.parseInt(id));
            db.cstmt.setInt(3, Integer.parseInt(post.getID()));
            db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);
            db.executeCallable();
            errorCode = db.cstmt.getInt(4);
        } catch (SQLException sqle) {
        	Logger.getLogger(DiscussionGroup.class).debug("DiscussionGroup.populatePost() threw an SQL exception: " + sqle);
            throw new PersistenceException("Error updating post reader", sqle);
        } catch (NumberFormatException nfe) {
        	Logger.getLogger(DiscussionGroup.class).debug("DiscussionGroup.populatePost() threw a Number Format Exception: " + nfe);
            throw new PersistenceException("ParseInt Failed in DiscussionGroup.populatePost()", ErrorLogger.HIGH);
        } finally {
            db.release();
        }

        try {
            // Handle (throw) any database exceptions
            DBExceptionFactory.getException("DiscussionGroup.populatePost()", errorCode);
        } catch (net.project.base.PnetException pe) {
            throw new PersistenceException("DiscussionGroup.populatePost() " +
                "threw an exception; " + pe, pe);
        }
    }

    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<DiscussionGroup>\n");
        xml.append(getXMLElements());
        xml.append("</DiscussionGroup>\n");

        return xml.toString();
    }

    /**
     * Returns the basic news XML elements
     *
     * @return XML elements as string
     */
    private String getXMLElements() {
        StringBuffer xml = new StringBuffer();

        xml.append("<space_id>" + XMLUtils.escape(space.getID()) + "</space_id>\n");
        xml.append("<discussion_group_id>" + XMLUtils.escape(getID()) + "</discussion_group_id>\n");
        xml.append("<discussion_group_name>" + XMLUtils.escape(name) + "</discussion_group_name>\n");
        xml.append("<charter>" + XMLUtils.escape(charter) + "</charter>\n");
        xml.append("<unread>" + XMLUtils.escape(unreadCount) + "</unread>\n");
        xml.append("<description>" + XMLUtils.escape(description) + "</description>\n");
        xml.append("<number_of_post>" + XMLUtils.escape(numPosts) + "</number_of_post>\n");
        xml.append("<Space>\n");
        xml.append("<name>" + XMLUtils.escape(SessionManager.getUser().getCurrentSpace().getName()) + "</name>\n");
        xml.append("<SpaceType>" + XMLUtils.escape(SessionManager.getUser().getCurrentSpace().getSpaceType().getName()) + "</SpaceType>\n");
        xml.append("</Space>\n");
        return xml.toString();
    }
}
