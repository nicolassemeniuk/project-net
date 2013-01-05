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

 package net.project.news;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import net.project.base.DBErrorCodes;
import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.ObjectType;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.events.EventType;
import net.project.notification.EventCodes;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * A news item.
 */
public class News implements IJDBCPersistence, IXMLPersistence, Serializable {

    /**
     * The ID of the News
     */
    private String newsID = null;
    /**
     * The Topic for the news Item
     */
    private String topic = null;
    /**
     * The message of the News Item
     */
    private String message = null;
    /**
     * The Priority ID for the News Item
     */
    private String priorityID = null;
    /**
     * The notification ID for the News Item
     */
    private String notificationID = null;
    /**
     * The ID of the person who had posted the message
     */
    private String postedByID = null;
    /**
     * The time at whch the item was posted
     */
    private Date postedDatetime = null;
    /**
     * The creator for the post
     */
    private String createdByID = null;
    /**
     * The Time at which the news Item was created
     */
    private Date createdDatetime = null;
    /**
     * The modifier for the post
     */
    private String modifiedByID = null;
    /**
     * The time at which the post has been modified
     */
    private Date modifiedDatetime = null;
    /**
     * the crc
     */
    private Date crc = null;
    /**
     * The record status for the posted item in the database
     */
    private String recordStatus = null;
    /**
     * the space ID to which the News Item belongs
     */
    private String spaceID = null;

    /**
     * The name for the priority
     */
    private String priorityName = null;
    /**
     * The description for the  Priority
     */
    private String priorityDescription = null;
    /**
     */
    private String postedByFullName = null;
    /**
     * The full name for the creator
     */
    private String createdByFullName = null;
    /**
     * The full name of the modifier
     */
    private String modifiedByFullName = null;
    /**
     * The space to which the news item belongs to
     */
    private Space space = null;

    /**
     * The flag to indicate whether the news item is loaded from the space or not
     */
    private boolean isLoaded = false;

    /** current user */
    private User user = null;
    private DBBean db = new DBBean();

    /**
     * The length to which the "presentable message" is to be truncated to
     */
    private int truncatedPresentableMessageLength = 390;

    /**
     * The maximum number of paragraphs to display in the "presentable message"
     * before truncating.
     */
    private int truncatedPresentableMessageMaxParagraphs = 5;

    /**
     * Indicates whether the news may be removed.  This is set to true only
     * when the appropriate checks have been performed
     */
    private boolean isRemovePermitted = false;
    /** Indicates whether the news removal was successful or not. */
    private boolean isRemoveSuccessful = false;

    /**
     * Create a new news.
     */
    public News() {
        // does nothing right now
    }

    /**
     * Return the news id
     * @return the id of the news
     */
    public String getID() {
        return this.newsID;
    }

    /**
     * sets the topic for the news item
     *
     * @param topic  the topic for the news item
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Returns the topic for the news item
     *
     * @return the topic for the news item
     */
    public String getTopic() {
        return this.topic;
    }

    /**
     * Rturns the length for the truncated Presentable Message
     *
     * @return the length for the truncated Presentable Message
     */
    public int getTruncatedPresentableMessageLength() {
        return this.truncatedPresentableMessageLength;
    }

    /**
     * Sets the length for the truncated Presentable Message
     *
     * @param length the length for the truncated Presentable Message
     */
    public void setTruncatedPresentableMessageLength(int length) {
        this.truncatedPresentableMessageLength = length;
    }

    public int getTruncatedPresentableMessageMaxParagraphs() {
        return this.truncatedPresentableMessageMaxParagraphs;
    }

    public void setTruncatedPresentableMessageMaxParagraphs(int maxParagraphs) {
        this.truncatedPresentableMessageMaxParagraphs = maxParagraphs;
    }

    /**
     * Sets the message for the news Item
     *
     * @param message the message for the news Item
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the message for the news Item
     *
     * @return the message for the news Item
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Sets the priority ID
     *
     * @param priorityID
     */
    public void setPriorityID(String priorityID) {
        this.priorityID = priorityID;
    }

    /**
     * Gets the priority ID
     * @return  the priority ID
     */
    public String getPriorityID() {
        return this.priorityID;
    }

    /**
     *  Sets the notification ID
     * @param notificationID the notification ID
     */
    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    /**
     * Returns the notification ID
     * @return The notifiaction ID
     */
    public String getNotificationID() {
        return this.notificationID;
    }

    /**
     * Sets the poster's ID
     * @param postedByID the poster's ID
     */
    public void setPostedByID(String postedByID) {
        this.postedByID = postedByID;
    }

    /**
     * Returns the poster's ID
     * @return the poster's ID
     */
    public String getPostedByID() {
        return this.postedByID;
    }

    /**
     * Sets the time at which the new item was posted
     * @param postedDatetime instance of <code>java.util.Date</code>
     */
    public void setPostedDatetime(Date postedDatetime) {
        this.postedDatetime = postedDatetime;
    }

    /**
     * Returns the time at which the new item was posted
     * @return the date at which the news item was posted which is an instance of <code>java.util.Date</code>
     */
    public Date getPostedDatetime() {
    	//Avinash: -----------------
    	if(this.postedDatetime==null)
    		this.postedDatetime = new Date();
    	//------------------------
        return this.postedDatetime;
    }

    /**
     * Returns the ID of the creator for the news item
     * @return the ID of the creator for the news item
     */
    public String getCreatedByID() {
        return this.createdByID;
    }

    void setCreatedByID(String createdByID) {
        this.createdByID = createdByID;
    }

    public Date getCreatedDatetime() {
        return this.createdDatetime;
    }

    void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public String getModifiedByID() {
        return this.modifiedByID;
    }

    void setModifiedByID(String modifiedByID) {
        this.modifiedByID = modifiedByID;
    }

    public Date getModifiedDatetime() {
        return this.modifiedDatetime;
    }

    void setModifiedDatetime(Date modifiedDatetime) {
        this.modifiedDatetime = modifiedDatetime;
    }

    java.util.Date getCrc() {
        return this.crc;
    }

    void setCrc(Date crc) {
        this.crc = crc;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    /**
     *
     * @param recordStatus
     */
    void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getPriorityName() {
        return this.priorityName;
    }

    void setPriorityName(String priorityName) {	
        this.priorityName = priorityName;
    }

    public String getPriorityDescription() {
        return this.priorityDescription;
    }

    void setPriorityDescription(String priorityDescription) {
        this.priorityDescription = priorityDescription;
    }

    public String getPostedByFullName() {
        return this.postedByFullName;
    }

    void setPostedByFullName(String postedByFullName) {
        this.postedByFullName = postedByFullName;
    }

    public String getCreatedByFullName() {
        return this.createdByFullName;
    }

    void setCreatedByFullName(String createdByFullName) {
        this.createdByFullName = createdByFullName;
    }

    public String getModifiedByFullName() {
        return this.modifiedByFullName;
    }

    void setModifiedByFullName(String modifiedByFullName) {
        this.modifiedByFullName = modifiedByFullName;
    }

    /**
     * Returns spaceID to which news belongs
     * @return the spaceID
     */
    public String getSpaceID() {
        return this.spaceID;
    }

    /**
     * Set the space to which the news belongs.
     * @param spaceID the spaceID
     */
    public void setSpaceID(String spaceID) {
        this.spaceID = spaceID;

        try {
            if (spaceID != null) {
                this.space = SpaceFactory.constructSpaceFromID(spaceID);
                this.space.load();
            }
        } catch (PersistenceException e) {
            // eat up
        }

    }

    /**
     * Set the current user.  This is required before manipulating news.
     * It is used to stamp the creation/modification userid in the database.
     * @param user the user manipulating the news.
     */
    public void setUser(User user) {
        this.user = user;
    }

    User getUser() {
        return this.user;
    }

    /**
     * Sets some default values for a new News item
     */
    public void setDefaultValues() {
        setPriorityID(NewsPriority.NORMAL.getID());
        setPostedByID(user.getID());
        setPostedByFullName(user.getDisplayName());
        setPostedDatetime(new Date());
    }

    /**
     * Clear all News properties
     */
    public void clear() {
        setID(null);
        setTopic(null);
        setMessage(null);
        setPriorityID(null);
        setNotificationID(null);
        setPostedByID(null);
        setPostedDatetime(null);
        setCreatedByID(null);
        setCreatedDatetime(null);
        setModifiedByID(null);
        setModifiedDatetime(null);
        setCrc(null);
        setRecordStatus(null);
        setSpaceID(null);

        setPriorityName(null);
        setPriorityDescription(null);
        setPostedByFullName(null);
        setCreatedByFullName(null);
        setModifiedByFullName(null);

        setLoaded(false);
    }

    /**
     * Returns if the instance has been populated from database or not
     *
     * @return True if instance has been populated from database
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }

    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    /*
        Implementing IJDBCPersistence
    */

    /**
     * Set the news id property
     * @param newsID the id property
     */
    public void setID(String newsID) {
        this.newsID = newsID;
    }

    /**
     * Load the News from the underlying JDBC object.
     * @throws PersistenceException Thrown to indicate a failure loading from the database, a system-level error.
     */
    public void load() throws net.project.persistence.PersistenceException {
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select n.space_id , n.news_id , n.topic , n.message_clob , n.priority_id, ");
        queryBuff.append("n.priority_description , n.priority_name , n.notification_id, n.posted_by_id , n.posted_datetime , n.posted_by_full_name, ");
        queryBuff.append("n.created_by_id , n.created_datetime , n.created_by_full_name , ");
        queryBuff.append("n.modified_by_id , n.modified_datetime, n.modified_by_full_name , n.crc , n.record_status ");
        queryBuff.append("from pn_news_view n ");
        queryBuff.append("where n.news_id = " + getID() + " ");

        try {
            db.executeQuery(queryBuff.toString());

            if (db.result.next()) {
                setID(db.result.getString("news_id"));
                setTopic(db.result.getString("topic"));
                setMessage(ClobHelper.read(db.result.getClob("message_clob")));
                setPriorityID(db.result.getString("priority_id"));
                setNotificationID(db.result.getString("notification_id"));
                setPostedByID(db.result.getString("posted_by_id"));
                setPostedDatetime(db.result.getTimestamp("posted_datetime"));
                setCreatedByID(db.result.getString("created_by_id"));
                setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                setModifiedByID(db.result.getString("modified_by_id"));
                setModifiedDatetime(db.result.getTimestamp("modified_datetime"));
                setCrc(db.result.getTimestamp("crc"));
                setRecordStatus(db.result.getString("record_status"));
                setSpaceID(db.result.getString("space_id"));

                setPriorityName(PropertyProvider.get(db.result.getString("priority_name")));
                setPriorityDescription(db.result.getString("priority_description"));
                setPostedByFullName(db.result.getString("posted_by_full_name"));
                setCreatedByFullName(db.result.getString("created_by_full_name"));
                setModifiedByFullName(db.result.getString("modified_by_full_name"));

                setLoaded(true);
                this.space = SpaceFactory.constructSpaceFromID(spaceID);
                this.space.load();
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(News.class).error("News.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("News load operation failed: " + sqle, sqle);

        } finally {
            db.release();
        }

    } // load()


    /**
     * Save the News to the underlying JDBC object.  This utilizes stored
     * procedures to do the work.<br>
     * Note: If the store is successful this objects properties will be
     * cleared, except for the newsID.  Thus a load() may be performed
     * afterwards to load the remaining properties.
     * @throws PersistenceException Thrown to indicate a failure storing
     * to the database, a system-level error.
     */
    public void store() throws PersistenceException {

        if (isLoaded()) {

            modifyNews();

        } else {

            createNews();

        } // if (isLoaded)

    }

    private void modifyNews() throws PersistenceException {

        /* NewsID which was updated */
        String storedNewsID;

        try {
            int index = 0;
            int messageClobIndex;

            db.setAutoCommit(false);
            db.prepareCall("{call news.modify_news(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            db.cstmt.setString(++index, getID());
            db.cstmt.setString(++index, getTopic());
            db.cstmt.setString(++index, getPriorityID());
            db.cstmt.setString(++index, getNotificationID());
            db.cstmt.setString(++index, getPostedByID());
            db.cstmt.setTimestamp(++index, new Timestamp(getPostedDatetime().getTime()));
            db.cstmt.setString(++index, getUser().getID());
            db.cstmt.setTimestamp(++index, new Timestamp(getCrc().getTime()));
            db.cstmt.setInt(++index, (this.message == null ? 1 : 0));
            db.cstmt.registerOutParameter((messageClobIndex = ++index), java.sql.Types.CLOB);
            db.executeCallable();

            if (this.message != null) {
                // Now stream the message to the clob locater returned by the
                // procedure
                ClobHelper.write(db.cstmt.getClob(messageClobIndex), this.message);
            }

            db.commit();

            // Remember newsID we just modified
            storedNewsID = getID();

            NewsEvent event = new NewsEvent();
            event.setSpaceID(this.user.getCurrentSpace().getID());
            event.setTargetObjectID(getID());
            event.setTargetObjectType(EventCodes.MODIFY_NEWS);
            event.setTargetObjectXML(this.getXMLBody());
            event.setEventType(EventCodes.MODIFY_NEWS);
            event.setUser(this.user);
            event.setDescription("News Item Modified: \"" + this.topic + "\"");
            event.store();
            
            // publishing event to asynchronous queue
        	net.project.events.NewsEvent newsEvent = (net.project.events.NewsEvent) EventFactory.getEvent(ObjectType.NEWS, EventType.EDITED);
        	newsEvent.setObjectID(getID());
        	newsEvent.setObjectType(ObjectType.NEWS);
        	newsEvent.setName(this.topic);
        	newsEvent.setObjectRecordStatus("A");
        	newsEvent.publish();
            
            clear();
            setID(storedNewsID);

        } catch (SQLException sqle) {
        	Logger.getLogger(News.class).error("News.store(): User: " + getUser().getID() + ", unable to execute stored procedure: " + sqle);
            throw new PersistenceException("News store operation failed: " + sqle, sqle);

        } catch (EventException e) {
			Logger.getLogger(News.class).error("News.modifyNews() :: News Edited Event Publishing Failed! ", e);
		} finally {
            try {
                db.rollback();
            } catch (SQLException sqle) {
                /* Nothing we can do here except release everything */
            }

            db.release();
        }


    }


    private void createNews() throws PersistenceException {

        /* NewsID which was updated */
        String storedNewsID;

        try {
            int index = 0;
            int newsIDIndex;
            int messageClobIndex;

            db.setAutoCommit(false);
            db.prepareCall("{call news.create_news(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            db.cstmt.setString(++index, getSpaceID());
            db.cstmt.setString(++index, getTopic());
            db.cstmt.setString(++index, getPriorityID());
            db.cstmt.setString(++index, getNotificationID());
            db.cstmt.setString(++index, getPostedByID());
            db.cstmt.setTimestamp(++index, new Timestamp(getPostedDatetime().getTime()));
            db.cstmt.setString(++index, getUser().getID());
            db.cstmt.setInt(++index, (this.message == null ? 1 : 0));
            db.cstmt.registerOutParameter((messageClobIndex = ++index), java.sql.Types.CLOB);
            db.cstmt.registerOutParameter((newsIDIndex = ++index), java.sql.Types.VARCHAR);
            db.executeCallable();

            // Get the newly created newsID
            storedNewsID = db.cstmt.getString(newsIDIndex);

            if (this.message != null) {
                // Now stream the message to the clob locater returned by the
                // procedure
                ClobHelper.write(db.cstmt.getClob(messageClobIndex), this.message);
            }

            db.commit();

            NewsEvent event = new NewsEvent();
            event.setSpaceID(this.user.getCurrentSpace().getID());
            event.setTargetObjectID(storedNewsID);
            event.setTargetObjectType(EventCodes.CREATE_NEWS);
            event.setTargetObjectXML(this.getXMLBody());
            event.setEventType(EventCodes.CREATE_NEWS);
            event.setUser(this.user);
            event.setDescription("Created News: \"" + this.topic + "\"");
            event.store();
            
            // publishing event to asynchronous queue
        	net.project.events.NewsEvent newsEvent = (net.project.events.NewsEvent) EventFactory.getEvent(ObjectType.NEWS, EventType.NEW);
        	newsEvent.setObjectID(storedNewsID);
        	newsEvent.setObjectType(ObjectType.NEWS);
        	newsEvent.setName(this.topic);
        	newsEvent.setObjectRecordStatus("A");
        	newsEvent.publish();
                        
            clear();
            setID(storedNewsID);

        } catch (SQLException sqle) {
        	Logger.getLogger(News.class).error("News.store(): User: " + getUser().getID() + ", unable to execute stored procedure: " + sqle);
            throw new PersistenceException("News store operation failed: " + sqle, sqle);

        } catch (EventException e) {
			Logger.getLogger(News.class).error("News.createNews() :: News Add Event Publishing Failed! ", e);
		} finally {
            try {
                db.rollback();
            } catch (SQLException sqle2) {
                /* Nothing we can do here except release everything */
            }

            db.release();
        }
    }

    /**
     * Prepare the news to be removed.
     * This routine populates the errorTable.
     */
    public void prepareRemove() {
        boolean isMetCriteria = true;
        setRemovePermitted(false);

        /*
            Currently no checks to be performed
         */

        setRemovePermitted(isMetCriteria);
    }

    String getPrepareRemoveErrorsPresentation() {
        return errors.getAllErrorsRemovePresentation();
    }

    String getRemoveResultPresentation() {
        return errors.getAllErrorsRemovePresentation();
    }

    public boolean isRemovePermitted() {
        return this.isRemovePermitted;
    }

    void setRemovePermitted(boolean isRemovePermitted) {
        this.isRemovePermitted = isRemovePermitted;
    }

    public boolean isRemoveSuccessful() {
        return this.isRemoveSuccessful;
    }

    void setRemoveSuccessful(boolean isRemoveSuccessful) {
        this.isRemoveSuccessful = isRemoveSuccessful;
    }

    /**
     * Delete the News from the database.
     * @throws PersistenceException Thrown to indicate a failure storing to the database, a system-level error.
     * @throws net.project.persistence.RemoveException The object does not allow destruction.
     */
    public void remove() throws PersistenceException {
        /* Default to a failed removal */
        setRemoveSuccessful(false);

        /* Repeat the checks that should have been done prior to this method call */
        prepareRemove();

        if (!isRemovePermitted()) {
            /*
                Remove is not permitted
                Message will have been created and will be displayed to user
             */

        } else {
            /*
                Remove is permitted.
                Do it.
             */
            removeNews();
            setRemoveSuccessful(true);
        }
    }

    /**
     * Performs the actual remove
     * @throws PersistenceException if there is a problem performing the remove
     */
    private void removeNews() throws PersistenceException {
        /* errorCode will be returned from stored procedures */
        int errorCode = -1;
        NewsEvent event = new NewsEvent();

        /*
        procedure remove_news (
            i_news_id        in varchar2,
            i_modified_by_id in varchar2,
            i_crc            in date,
            o_return_value   out number);
        */
        try {
            db.prepareCall("{call news.remove_news(?, ?, ?, ?)}");
            db.connection.setAutoCommit(false);
            db.cstmt.setString(1, getID());
            db.cstmt.setString(2, getUser().getID());
            db.cstmt.setTimestamp(3, new Timestamp(getCrc().getTime()));
            db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);

            db.executeCallable();

            errorCode = db.cstmt.getInt(4);
            db.closeCStatement();

            if (errorCode != DBErrorCodes.OPERATION_SUCCESSFUL) {
                /* Rollback if anything went wrong
                   An exception is thrown later based on this errorCode */
                db.connection.rollback();

            } else {
                /*
                   Successfully removed the news
                   Commit work
                 */
                db.connection.commit();
                event.setSpaceID(this.user.getCurrentSpace().getID());
                event.setTargetObjectID(getID());
                event.setTargetObjectType(EventCodes.REMOVE_NEWS);
                event.setTargetObjectXML(this.getXMLBody());
                event.setEventType(EventCodes.REMOVE_NEWS);
                event.setUser(this.user);
                event.setDescription("News Item Removed : \"" + this.topic + "\"");
                event.store();
                errorCode = DBErrorCodes.OPERATION_SUCCESSFUL;
                
                // publishing event to asynchronous queue
            	net.project.events.NewsEvent newsEvent = (net.project.events.NewsEvent) EventFactory.getEvent(ObjectType.NEWS, EventType.DELETED);
            	newsEvent.setObjectID(getID());
            	newsEvent.setObjectType(ObjectType.NEWS);
            	newsEvent.setName(this.topic);
            	newsEvent.setObjectRecordStatus("D");
            	newsEvent.publish();
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(News.class).error(
                    "News.remove(): User: " + getUser().getID() + ", unable to execute stored procedure: " +
                    sqle);
            throw new PersistenceException("News remove operation failed.", sqle);

        } catch (EventException e) {
			Logger.getLogger(News.class).error("News.removeNews() :: News Deleted Event Publishing Failed! ", e);
		} finally {
            if (db.connection != null) {
                try {
                    db.connection.rollback();
                } catch (SQLException sqle) {
                    /* Nothing we can do here except release everything */
                }
            }
            db.release();
        }

        // Handle (throw) any exceptions that were sucked up in PL/SQL
        try {
            DBExceptionFactory.getException("News.remove()", errorCode);

        } catch (net.project.base.UpdateConflictException e) {
            throw new PersistenceException("The news item has been modified by another user.  Please reload and try again.", e);

        } catch (net.project.persistence.RecordLockedException e) {
            throw new PersistenceException("The news item is currently locked by another user.  Please reload and try again.", e);

        } catch (PnetException e) {
            throw new PersistenceException(e.getMessage(), e);
        }

    }

    /*
        Implement IXMLPersistence
     */

    /**
     * Converts the news into an XML string including the XML header
     * tag.
     * @return the XML string
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<news>\n");
        xml.append(getXMLElements());
        xml.append("</news>\n");

        return xml.toString();
    }

    /**
     * Returns the basic news XML elements
     * @return XML elements as string
     */
    private String getXMLElements() {
        StringBuffer xml = new StringBuffer();
	xml.append("<jsp_root_url>"
		+ XMLUtils.escape(net.project.security.SessionManager
			.getJSPRootURL()) + "</jsp_root_url>");
	xml.append("<space_id>" + XMLUtils.escape(this.spaceID)
		+ "</space_id>\n");
	xml.append("<news_id>" + XMLUtils.escape(this.newsID) + "</news_id>\n");
	xml.append("<topic>" + XMLUtils.escape(this.topic) + "</topic>\n");
	xml
		.append("<message>" + XMLUtils.escape(this.message)
			+ "</message>\n");
	xml.append("<TruncatedPresentableMessageLength>"
		+ this.truncatedPresentableMessageLength
		+ "</TruncatedPresentableMessageLength>\n");
	xml.append("<TruncatedPresentableMessageMaxParagraphs>"
		+ this.truncatedPresentableMessageMaxParagraphs
		+ "</TruncatedPresentableMessageMaxParagraphs>\n");
	xml.append("<priority_id>" + XMLUtils.escape(this.priorityID)
		+ "</priority_id>\n");
	xml.append("<priority_name>" + XMLUtils.escape(this.priorityName)
		+ "</priority_name>\n");
	xml.append("<priority_description>"
		+ XMLUtils.escape(this.priorityDescription)
		+ "</priority_description>\n");
	xml.append("<notification_id>" + XMLUtils.escape(this.notificationID)
		+ "</notification_id>\n");
	xml.append("<posted_by_id>" + XMLUtils.escape(this.postedByID)
		+ "</posted_by_id>\n");
	xml.append("<posted_datetime>"
		+ XMLUtils.formatISODateTime(this.postedDatetime)
		+ "</posted_datetime>\n");
	xml.append("<posted_by_full_name>"
		+ XMLUtils.escape(this.postedByFullName)
		+ "</posted_by_full_name>\n");
	xml.append("<created_by_id>" + XMLUtils.escape(this.createdByID)
		+ "</created_by_id>\n");
	xml.append("<created_datetime>"
		+ XMLUtils.formatISODateTime(this.createdDatetime)
		+ "</created_datetime>\n");
	xml.append("<created_by_full_name>"
		+ XMLUtils.escape(this.createdByFullName)
		+ "</created_by_full_name>\n");
	xml.append("<modified_by_id>" + XMLUtils.escape(this.modifiedByID)
		+ "</modified_by_id>\n");
	xml.append("<modified_datetime>"
		+ XMLUtils.formatISODateTime(this.modifiedDatetime)
		+ "</modified_datetime>\n");
	xml.append("<modified_by_full_name>"
		+ XMLUtils.escape(this.modifiedByFullName)
		+ "</modified_by_full_name>\n");
	xml.append("<crc>" + XMLUtils.formatISODateTime(this.crc) + "</crc>\n");
	xml.append("<record_status>" + XMLUtils.escape(this.recordStatus)
		+ "</record_status>\n");
	if (this.space != null) {
	    xml.append("<Space>\n");
	    xml.append("<name>" + XMLUtils.escape(this.space.getName())
		    + "</name>\n");
	    xml.append("<SpaceType>"
		    + XMLUtils.escape(this.space.getSpaceType().getName())
		    + "</SpaceType>\n");
	    xml.append("</Space>\n");
	}
	return xml.toString();
    }

    /*
         * =================================================================================================
         * Error stuff
         * ================================================================================================
         */

    private ValidationErrors errors = new ValidationErrors();

    public void clearErrors() {
        errors.clearErrors();
    }

    /**
     * Indicate whether there are any errors
     * @return true if there are errors, false otherwise
     */
    public boolean hasErrors() {
        return errors.hasErrors();
    }

    public String getErrorsTable() {
        return errors.getAllErrorsDefaultPresentation();
    }

    public String getFlagError(String fieldID, String label) {
        return errors.flagError(fieldID, label);
    }

    /*
        Validation Routines
     */

    /**
     * Validates that all information in this News is suitable for storing.
     * 
     * @since 8.2.0
     */
    public void validate(){
	validateTopic();
	validatePriorityID();
    }    
    
    private void validateTopic() {
        if (Validator.isBlankOrNull(getTopic())) {
            errors.put("topic", "News Topic", PropertyProvider.get("prm.news.newstopic.required.validation.message"));
        } else if (getTopic().length() > 80) {
            errors.put("topic", "News Topic", PropertyProvider.get("prm.news.newstopic.toomanychars.validation.message"));
        }
    }

    public void validateMessage() {
        // Nothing to validate.  Messages no longer are restricted to 4000 characters
    }

    private void validatePriorityID() {
        if (getPriorityID() == null || getPriorityID().equals("")) {
            errors.put("priority_id", "Priority", PropertyProvider.get("prm.news.priorityid.requiredfield.validation.message"));
        }

    }

    public void validatePostedByID() {
        /* No validation */
    }

}

