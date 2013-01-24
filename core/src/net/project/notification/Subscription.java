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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 20869 $
|       $Date: 2010-05-24 11:38:06 -0300 (lun, 24 may 2010) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.notification;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.security.group.Group;
import net.project.security.group.GroupCollection;
import net.project.security.group.GroupException;
import net.project.security.group.GroupProvider;
import net.project.space.ISpaceTypes;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.DateFormat;
import net.project.util.DateUtils;
import net.project.util.HTMLUtils;
import net.project.util.StringUtils;
import net.project.workflow.StepGroup;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;


/**
 * When A user wants to be infomed whenever some event occurs which is of
 * interest to him/her, the User creates a <CODE>Subscription</CODE>
 * to that event.<P>
 * This class  encapsulates data about that <CODE>Subscription</CODE>.
 *
 * @since aardvark
 * @author Chad
 * @author Tim
 */
public class Subscription implements IJDBCPersistence, IXMLPersistence, INotificationDB {

    private DBBean dbBean = new DBBean();

    // Attribute information, used when setting attribute values
    private static final int ATTR_NAME_SIZE = 80;
    private static final int ATTR_DESCRIPTION_SIZE = 500;

    /** the textual message the receiver will receive when he/she is notified */
    String customMessage;

    /** the name that this notification was given by the subscriber */
    String name;

    /** contains the information regarding the scheduling of delivery of the associated Notificaiton */
    private SubscriptionSchedule subscriptionSchedule = new SubscriptionSchedule();


    /** the system defined type of subscription this is */
    private String subscriptionType;

    /** the GUID of this object */
    String objectID;

    /** the GUID of the entity which creaed this subscription */
    private String subscriberBatchID = null;

    private ArrayList notificationTypeList = new ArrayList();
    
    private ArrayList eventTypeList = new ArrayList();

    /**  the interval (weekly daily immediately monthly)  which represents the frequency the subscriber wants to receive  notificaitons from this subscription   */
    private String deliveryIntervalID;

    /**  the type of object this subscription is for   */
    private String targetObjectType;

    /** the description fo this subscription obtained from either the subscriber or the default value if the subscriber didn't customize this field.*/
    String description;

    /**
     * The ID of the Space to which the Subscription belongs
     */
    String spaceID;

    /** the GUID of the object the Subscription was applied to.*/
    private String targetObjectID;

    /** the creator of this subscription*/
    String createdByID;

    /** the date this Subscritption was created*/
    Date createdDate;
    
    /** the date this Subscritption was modified*/
    private Date modifiedDate; 

    private boolean isTypeSubscription = false;

    /**
     * The Collection of subscribers for the Subscription
     */
    SubscriberCollection subscribers = new SubscriberCollection();

    /** the entity which has created the subscription */
    private User user;

    /**
     * The Space to which the subscription belongs to
     */
    Space space;


    /** the URI that the notification associated with this subscription will be sent to */
    //    private AddressCollection deliveryAddress = new AddressCollection();

    //    private ArrayList subscribers = new ArrayList();


    /** true if  */
    private boolean isLoaded;
    
    /**
     * The status to set notification  enabled or disabled
     */
    private boolean status;
    
    private String objectName;
    
    /**
     * Creates a new, empty Subscription
     */
    public Subscription() {
    }

    /**
     * Creates a new subscription setting the current user context.
     * @param user the current user
     */
    public Subscription(User user) {
        this.user = user;
    }

    /**
     * Returns the display name of this Subscription.
     *
     * @return the display name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for this Subscription.
     * Names may be up to 80 characters.
     * @param name the name of the subscription
     */
    public void setName(String name) {
        if (name != null && name.length() > ATTR_NAME_SIZE) {
            this.name = name.substring(0, ATTR_NAME_SIZE);
        } else {
            this.name = name;
        }
    }

    /**
     * Sets the Space ID
     *
     * @param spaceID the Space ID
     */
    public void setSpaceID(String spaceID) {
        this.spaceID = spaceID;
    }

    /**
     * Returns the Space ID
     *
     * @return String the Space ID
     */
    public String getSpaceID() {
        return this.spaceID;
    }

    /**
     * Returns the entity in the system which created this Subscription.
     */
    public void setCreatedByID(String createdByID) {
        this.createdByID = createdByID;
    }

    /**
     * Adds the Notification Type
     *
     * @param notifyTypeID
     *               the ID for Notification Type
     */
    public void addNotificationType(String notifyTypeID) {
        this.notificationTypeList.add(notifyTypeID);
    }

    public void addEventType(String eventType) {
        this.eventTypeList.add(eventType);
    }    
    
    /**
     * Returns the message, defined by the creator, which will be what the
     * receiver gets wehnt he notification is sent out.
     *
     * @return the message
     */
    public String getCustomMessage() {
        return customMessage;
    }

    /**
     * Sets the message, defined by the creator, that is displayed when the
     * notification is sent.
     * @param customMessage the custom message
     */
    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }


    /**
     * Returns a textual description of this subscription.  Intended for human
     * consumption.
     *
     * @return a <code>String</code> value containing the textual description of
     * this subscription.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets a textual description of this subscription.
     * Descriptions may be up to 500 characters in length.
     *
     * @param description the description of this subscription
     */
    public void setDescription(String description) {
        if (description != null && description.length() > ATTR_DESCRIPTION_SIZE) {
            this.description = description.substring(0, ATTR_DESCRIPTION_SIZE);
        } else {
            this.description = description;
        }
    }

    /**
     * Returns the GUID of the object which is the target of this Subscription,
     * if there is a single target.
     *
     * @return a <code>String</code> value containing the GUID of the object
     * which is the target of this subscription.
     */
    public String getTargetObjectID() {
        return targetObjectID;
    }

    /**
     * Sets the GUID of the object which is the target of this Subscription, if there is a single target
     * @param targetObjectID
     */
    public void setTargetObjectID(String targetObjectID) {
        this.targetObjectID = targetObjectID;
    }


    public void setIsTypeSubscription() {
        setIsTypeSubscription(true);
    }

    public void setIsTypeSubscription(boolean flag) {
        this.isTypeSubscription = flag;
    }

    private boolean isTypeSubscription() {
        return this.isTypeSubscription;
    }

    /**
     * Returns the object which contains the delivery schedule for notification
     * and duration of this subscription information.
     *
     * @return a <code>SubscriptionSchedule</code> object which contains the
     * delivery schedule for notification and duration of this schedule
     * information.
     */
    private SubscriptionSchedule getSubscriptionSchedule() {
        return subscriptionSchedule;
    }

    /**
     * Sets the object which contains meta information about delivery intervals and subscription lifetime
     * @param subscriptionSchedule
     *               The object which maintains scheduling information for this Subscription
     */
    public void setSubscriptionSchedule(SubscriptionSchedule subscriptionSchedule) {
        this.subscriptionSchedule = subscriptionSchedule;
    }

    /**
     * Sets the ID for Delivery Interval which can be immediate , weekly , monthly , etc
     *
     * @param deliveryIntervalID ID for the Delivery Interval
     */
    public void setDeliveryIntervalID(String deliveryIntervalID) {
        this.deliveryIntervalID = deliveryIntervalID;
        this.subscriptionSchedule.setDeliveryIntervalID(deliveryIntervalID);
    }

    /**
     * Sets the ID for Delivery Type which can be email , fax , etc . But , ight now , delivery by email
     * is supported only
     *
     * @param deliveryTypeID ID for the Delivery Type
     */
    public void setDeliveryTypeID(String deliveryTypeID) {
    }

    /**
	 * @return the objectName
	 */
	public String getObjectName() {
		return objectName;
	}
	
	/**
	 * @param objectName the objectName to set
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

    /**
     * Adds the External Subscriber ( Unregistered) to the subscriber
     * collection.
     *
     * @param deliveryAddress the Address at which the Subscription is to be delivered to
     * @param deliveryType the delivery Type ID which is Email for now
     */
    public void addExternalSubscriber(String deliveryAddress, String deliveryType) {
        this.subscribers.addExternalSubscriber(deliveryAddress, deliveryType);
    }


    /**
     * Adds the subscriber to the Subscription List
     *
     * @param subscriberGroupID
     * @param deliveryType
     *               The delivery Type ID which is Email for now
     */
    public void addSubscriber(String subscriberGroupID, String deliveryType) {
        this.subscribers.addSubscriber(subscriberGroupID, deliveryType);
    }

    /**
     * Resets the <CODE>Subscription<CODE> object
     */
    public void clear() {
        clearNotificationTypes();
        clearSubscribers();
        clearEventTypes();

        customMessage = null;
        name = null;
        subscriptionType = null;
        objectID = null;
        subscriberBatchID = null;
        deliveryIntervalID = null;
        targetObjectType = null;
        description = null;
        spaceID = null;
        targetObjectID = null;
        createdByID = null;
        isTypeSubscription = false;
        isLoaded = false;
    }


    public void clearNotificationTypes() {
        this.notificationTypeList.clear();
    }

    public void clearEventTypes() {
        this.eventTypeList.clear();
    }    

    /**
     * Clears the <CODE>ArrayList<CODE> containing the Subscribers
     */
    public void clearSubscribers() {
        this.subscribers.clear();
    }


    /**
     * Returns the URI that the Notification associated with this Subscription
     * will be sent to.
     *
     * @return a <code>String</code> value containing the URI that the
     * notification associated with this subscription will be sent to.
     */
    public SubscriberCollection getSubscriberCollection() {
        return this.subscribers;
    }


    public void setTargetObjectType(String objectType) {
        this.targetObjectType = objectType;
    }


    public String getTargetObjectType() {
        return this.targetObjectType;
    }


    /**
     * Returns the system defined GUID for this object.
     *
     * @return a <code>String</code> value containing the system-defined GUID
     * for this object.
     */
    public String getID() {
        return objectID;
    }


    /**
     * Sets the system defined GUID of this object.
     *
     * @param objectID
     */
    public void setID(String objectID) {
        this.objectID = objectID;
    }


    /**
     * Sets the GUID of the entity which has created  this Subscription .
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
	 * @return the status
	 */
	public boolean getStatus() {
		return status;
	}
	
	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	
	
	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * @return the subscriptionType
	 */
	public String getSubscriptionType() {
		return PropertyProvider.get(subscriptionType);
	}
	
    private void loadSubscribers(String subscriberBatchID) {

        this.subscribers.setSubscriptionID(this.getID());
        this.subscribers.setBatchID(subscriberBatchID);
        this.subscribers.load();

    }

    
    /**
     * Method to get item location 
     * 
     */
    public String getItemLocation() {
    	return space.getName();
    }

	/**
	 * Method to get space url for item location
	 * 
	 * @return url
	 */
    public String getSpaceUrl(){
    	String url = "";
    	if(space.getSpaceType().getID().equals(ISpaceTypes.PROJECT_SPACE)){
    		url = SessionManager.getJSPRootURL() + "/project/Dashboard?id=" + space.getID();
    	} else if(space.getSpaceType().getID().equals(ISpaceTypes.PERSONAL_SPACE)){
    		url = SessionManager.getJSPRootURL() + "/blog/view/" + space.getID() + "/" + space.getID() + "/"
					+ space.getSpaceType().getID() + "/" + Module.PERSONAL_SPACE + "/?module=" + Module.PERSONAL_SPACE;
    	} else if(space.getSpaceType().getID().equals(ISpaceTypes.BUSINESS_SPACE)){
    		url = SessionManager.getJSPRootURL() + "/business/Main.jsp?id=" + space.getID();
    	}
    	return url; 
    }
    
    /**
     * Loads the data from the persistent store into this object
     * The id of this subscription must be non-null
     * @exception PersistenceException
     *                   if the db query fails
     */
    public void load() throws PersistenceException {

        // Enter the mininum requirements for the sucessful loading of this table
        if (objectID == null)
            throw new NullPointerException("the id of this object is null.  Can't load.");

        // Construct the query string to retrieve data from the table
        StringBuffer query = new StringBuffer("select s.subscription_id, ");
        query.append(" s.name, s.description, s.custom_message_clob, s.subscription_type_id, s.subscriber_batch_id, s.created_date, ");
        query.append(" s.created_by_id, s.modified_date, s.modified_by, s.delivery_interval, s.status, ");
        query.append(" s.subscriber_batch_id , sr.name as recurrence_name , ss.space_id , sub_type.obj_type ");
        query.append(" from pn_space_has_subscription ss , pn_subscription s  , pn_subscription_recurrence sr ");
        query.append(" , (select ot.display_name as obj_type, ots.SUBSCRIPTION_ID ");
        query.append("    from pn_object_type_subscription ots, pn_notification_object_type ot ");
        query.append("    where ots.object_type = ot.object_type ");
        query.append("    union  ");
        query.append("    select ott.display_name as obj_type, ohs.SUBSCRIPTION_ID  ");
        query.append("    from pn_object_has_subscription ohs, pn_object o, pn_notification_object_type ott  ");
        query.append("    where ohs.OBJECT_ID = o.OBJECT_ID and o.OBJECT_TYPE = ott.OBJECT_TYPE) sub_type  ");     
        query.append("  where s.delivery_interval = sr.recurrence_id and ss.subscription_id = s.subscription_id ");
        query.append("  and s.subscription_id = sub_type.subscription_id and s.subscription_id ");
        query.append(" = ");
        query.append(getID());

        try {
            dbBean.prepareStatement(query.toString());
            dbBean.executePrepared();

            if (dbBean.result.next()) {
                // Fill in this Subscription object
                this.createdByID = dbBean.result.getString("created_by_id");
                this.createdDate = dbBean.result.getTimestamp("created_date");
                this.modifiedDate = dbBean.result.getTimestamp("modified_date");
                this.customMessage = ClobHelper.read(dbBean.result.getClob("custom_message_clob"));
                this.description = dbBean.result.getString("description");
                this.name = dbBean.result.getString("name");
                this.objectID = dbBean.result.getString("subscription_id");
                this.deliveryIntervalID = dbBean.result.getString("delivery_interval");
                this.subscriptionSchedule.setDeliveryIntervalID(dbBean.result.getString("delivery_interval"));
                this.subscriberBatchID = dbBean.result.getString("subscriber_batch_id");
                this.subscriptionSchedule.setDeliveryIntervalName(PropertyProvider.get(dbBean.result.getString("recurrence_name")));
                this.spaceID = dbBean.result.getString("space_id");
                this.subscriptionType = dbBean.result.getString("obj_type");
                this.status = dbBean.result.getBoolean("status");
                loadSubscribers(this.subscriberBatchID);
                loadNotificationTypes(getID());	
                if (spaceID != null) {
                    loadSpace();
                }
                	
            } else {
                throw new PersistenceException();
            }
        }// try
        catch (SQLException sqle) {
        	Logger.getLogger(Subscription.class).debug("Subscription.load() threw an SQLException:  " + sqle);
            throw new PersistenceException("Subscription.load() threw an SQLException: " + sqle, sqle);
        } finally {
            dbBean.release();
        }

        isLoaded = true;
    }

    private void loadNotificationTypes(String subscriptionID) throws SQLException {
    	StringBuffer query = new StringBuffer("select nt.description ");
    	query.append(" from pn_sub_has_notify_type shnt, pn_notification_type nt");
    	query.append(" where shnt.notification_type_id = nt.notification_type_id ");
    	query.append(" and shnt.subscription_id =  ");
    	query.append(subscriptionID);
    	
        dbBean.prepareStatement(query.toString());
        dbBean.executePrepared();
        
        while (dbBean.result.next()){
        	addEventType(dbBean.result.getString("description"));
        }
    }
    
    private void storeNotificationTypes(DBBean db, String subscriptionID) throws SQLException {

        String qstrStoreNotifyTypes = "insert into pn_sub_has_notify_type " +
                "(subscription_id, notification_type_id) " +
                "values (?,?)";

        db.prepareStatement(qstrStoreNotifyTypes);

        for (int i = 0; i < this.notificationTypeList.size(); i++) {
            db.pstmt.setString(1, subscriptionID);
            db.pstmt.setString(2, (String) this.notificationTypeList.get(i));

            db.executePrepared();
        }

    }

    /**
     * Creates the Object  Subscription
     *
     * @exception SQLException
     * @exception PersistenceException
     *                   thrown if anything goes wrong
     */
    private void createObjectSubscription(DBBean db) throws SQLException, PersistenceException {

        int index = 0;
        int customMessageClobIndex = 0;
        int objectIDIndex = 0;

        db.setAutoCommit(false);
        db.prepareCall("{call notification.create_object_subscription(?,?,?,?,?,?,?,?,?,?,?,?)}");
        db.cstmt.setString(++index, this.name);
        db.cstmt.setString(++index, this.description);
        db.cstmt.setString(++index, this.subscriptionType);
        db.cstmt.setString(++index, this.targetObjectID);
        db.cstmt.setString(++index, this.getSubscriptionSchedule().getDeliveryIntervalID());
        db.cstmt.setString(++index, this.subscribers.getBatchID());
        db.cstmt.setString(++index, this.user.getID());
        db.cstmt.setString(++index, this.spaceID);
        db.cstmt.setInt(++index, (this.customMessage == null ? 1 : 0));
        db.cstmt.setBoolean(++index, this.status);
        db.cstmt.registerOutParameter((customMessageClobIndex = ++index), java.sql.Types.CLOB);
        db.cstmt.registerOutParameter((objectIDIndex = ++index), java.sql.Types.INTEGER);

        // Execute the callable statement
        db.executeCallable();

        if (this.customMessage != null) {
            // Stream the custom message to the clob
            ClobHelper.write(db.cstmt.getClob(customMessageClobIndex), this.customMessage);
        }

        // Grab the newly created ID
        this.objectID = db.cstmt.getString(objectIDIndex);

    }

    /**
     * Creates the Object Type Subscription.
     * @param db the DBBean in which to perform the transaction
     *
     * @exception SQLException
     * @exception PersistenceException
     *                   thrown if anything goes wrong
     */
    private void createObjectTypeSubscription(DBBean db) throws SQLException, PersistenceException {

        int index = 0;
        int customMessageClobIndex = 0;
        int objectIDIndex = 0;

        db.prepareCall("{call notification.create_type_subscription(?,?,?,?,?,?,?,?,?, ?,?,?)}");
        db.cstmt.setString(++index, this.name);
        db.cstmt.setString(++index, this.description);
        db.cstmt.setString(++index, this.subscriptionType);
        db.cstmt.setString(++index, this.targetObjectType);
        db.cstmt.setString(++index, this.getSubscriptionSchedule().getDeliveryIntervalID());
        db.cstmt.setString(++index, this.subscribers.getBatchID());
        db.cstmt.setString(++index, this.user.getID());
        db.cstmt.setString(++index, this.spaceID);
        db.cstmt.setInt(++index, (this.customMessage == null ? 1 : 0));
        db.cstmt.setBoolean(++index, this.status);
        db.cstmt.registerOutParameter((customMessageClobIndex = ++index), java.sql.Types.CLOB);
        db.cstmt.registerOutParameter((objectIDIndex = ++index), java.sql.Types.INTEGER);

        // Execute the callable statement
        db.executeCallable();

        if (this.customMessage != null) {
            // Stream the custom message to the clob
            ClobHelper.write(db.cstmt.getClob(customMessageClobIndex), this.customMessage);
        }

        // Grab the newly created ID
        this.objectID = db.cstmt.getString(objectIDIndex);

    }

    /**
     * Persists the information maintained in this object
     * @exception PersistenceException
     */
    public void store() throws PersistenceException {

        DBBean db = new DBBean();

        try {
            db.setAutoCommit(false);

            if (this.objectID != null) {
                // Update existing subscription

                // First update non-clob properties
                StringBuffer query = new StringBuffer();
                query.append("update pn_subscription set ");
                query.append("name = ?, description = ?, status = ?, modified_date = ?, ");
                if (this.customMessage == null) {
                    query.append("custom_message_clob = null ");
                } else {
                    query.append("custom_message_clob = empty_clob() ");
                }
                query.append("where subscription_id = ? ");

                int index = 0;
                db.prepareStatement(query.toString());
                db.pstmt.setString(++index, this.name);
                db.pstmt.setString(++index, this.description);
                db.pstmt.setBoolean(++index, this.status);
                DatabaseUtils.setTimestamp(db.pstmt, ++index, new Date());
                db.pstmt.setString(++index, getID());

                db.executePrepared();

                // Now update clob
                StringBuffer selectQuery = new StringBuffer();
                selectQuery.append("select custom_message_clob ")
                        .append("from pn_subscription ")
                        .append("where subscription_id = ? ");

                index = 0;
                db.prepareStatement(selectQuery.toString());
                db.pstmt.setString(++index, getID());

                db.executePrepared();

                if (db.result.next()) {
                    ClobHelper.write(db.result.getClob("custom_message_clob"), this.customMessage);

                } else {
                    // There is a problem if we can't find the record we just
                    // updated
                    throw new PersistenceException("Error updating custom message clob");

                }


            } else {

                if (!this.isLoaded) {
                    // Only create subscription if this is a new Subscription object
                    this.subscribers.setBatchID();

                    if (isTypeSubscription()) {
                        createObjectTypeSubscription(db);

                    } else {
                        createObjectSubscription(db);

                    }

                }

            }

            storeNotificationTypes(db, this.objectID);

            db.commit();

        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
                // Throw original error and release
            }
            throw new PersistenceException("Subscription store operation failed: " + sqle, sqle);

        } finally {
            db.release();
        }

        // now add the subscribers
        this.subscribers.setSubscriptionID(this.objectID);
        this.subscribers.store();


    } // store

/**
 * Method to remove notification types
 * 
 * @param db
 * @param subscriptionID
 * @throws SQLException
 */
    private void removeNotificationTypes(DBBean db, String subscriptionID) throws SQLException {
        String qstrStoreNotifyTypes = "delete from pn_sub_has_notify_type where subscription_id = " + subscriptionID;
        try {
            db.prepareStatement(qstrStoreNotifyTypes);
            db.executePrepared();
        } catch (SQLException sqle) {
        	Logger.getLogger(Subscription.class).debug("Subscription.removeNotificationTypes() threw an SQLException: " + sqle.getMessage());
        } finally {
        	db.release();
        }
    }

    public void remove() {

        String qstrDeleteSubscription = "update pn_subscription set record_status = 'D' where subscription_id = " + this.getID();

        try {

            this.dbBean.executeQuery(qstrDeleteSubscription);
        } catch (SQLException sqle) {
        	Logger.getLogger(Subscription.class).debug("Subscription.remove() threw an SQLException: " + sqle);
        } finally {
            this.dbBean.release();
        }
    }


    public void removeSubscriber(String subscriberGroupID) throws NotificationException {
        this.subscribers.removeSubscriber(subscriberGroupID);
    }


    /**
     * Returns the properties of this <CODE>Subscription</CODE> as an XML
     * <CODE>String</CODE>, which is then wrapped inside an XML subscription
     * tag.
     *
     * @return The XML representation of this <CODE>Subscription</CODE>.
     */
    public String getXMLBody() {
        // this makes the  XML representation of Subscription available to be contained by  some other, arbitrary outer-level  XML document, since it doesn't contain
        // the VERSION_STRING. To display a single Subscription, call this through getXML.

        StringBuffer xmlBody = new StringBuffer();

        xmlBody.append("<subscription>\n");
        xmlBody.append(getXMLProperties());
        xmlBody.append("</subscription>\n");

        return xmlBody.toString();
    }


    /**
     * Returns the properties of this Subscription object as an XML String.
     *
     * @return the properties of this object in an XML String.
     */
    private String getXMLProperties() {
    	DateFormat dateFormat = DateFormat.getInstance();	
        StringBuffer xmlProperties = new StringBuffer();
        String tab = "\t\t";
        String notificationStatus = (this.status ? PropertyProvider.get("prm.notification.editsubscription.statusenable.label") 
        								: PropertyProvider.get("prm.notification.editsubscription.statusdisable.label"));
        xmlProperties.append(tab + "<jsp_root_url>" + XMLUtils.escape(SessionManager.getJSPRootURL()) + "</jsp_root_url>\n");
        xmlProperties.append(tab + "<status>" + XMLUtils.escape(notificationStatus) + "</status>\n");
        xmlProperties.append(tab + "<name>" + XMLUtils.escape(this.name) + "</name>\n");
        xmlProperties.append(tab + "<created_by_id>" + XMLUtils.escape(this.createdByID) + "</created_by_id>\n");
        if (this.createdDate != null) {
            xmlProperties.append(tab + "<created_date>" + 
            		(DateUtils.isCurrentYear(this.createdDate) ? dateFormat.formatDate(this.createdDate, "MMM d") : 
            						dateFormat.formatDate(this.createdDate, "MMM d, yyyy")) + "</created_date>\n");
        }
        if (this.modifiedDate != null) {
            xmlProperties.append(tab + "<modified_date>" + (!this.createdDate.equals(this.modifiedDate) ? 
            		(DateUtils.isCurrentYear(this.modifiedDate) ? dateFormat.formatDate(this.modifiedDate, "MMM d") : 
            			dateFormat.formatDate(this.modifiedDate, "MMM d, yyyy")) : "") + "</modified_date>\n");
        }
        xmlProperties.append(tab + "<custom_message>" + XMLUtils.escape(this.customMessage) + "</custom_message>\n");
        xmlProperties.append(tab + "<description>" + XMLUtils.escape(this.description) + "</description>\n");
        xmlProperties.append(tab + "<object_id>" + XMLUtils.escape(this.objectID) + "</object_id>\n");
        xmlProperties.append(tab + "<subscription_type>" + XMLUtils.escape(this.subscriptionType) + "</subscription_type>\n");
        xmlProperties.append(tab + "<target_object_id>" + XMLUtils.escape(this.targetObjectID) + "</target_object_id>\n");
        xmlProperties.append(tab + "<object_id>" + XMLUtils.escape(this.objectID) + "</object_id>\n");

        if (this.user != null) {
            xmlProperties.append(tab + "<user>" + XMLUtils.escape(this.user.toString()) + "</user>\n");
        }

        // Hard-Coded for a while
        xmlProperties.append(tab + "<delivery_method>" + XMLUtils.escape(PropertyProvider.get("prm.notifications.subscriptions.details.deliverymethod.type.email.text")) + "</delivery_method>\n"); // Email
        xmlProperties.append(tab + "<notification_types>");
        for (int i = 0; i < this.eventTypeList.size(); i++) {
        	xmlProperties.append(tab + "<notification_type>" + XMLUtils.escape((String)this.eventTypeList.get(i)) + "</notification_type>\n");            
        }        
        xmlProperties.append(tab + "</notification_types>\n");
        xmlProperties.append(tab + "<item_name>" + XMLUtils.escape(getItemName(this.objectID)) + "</item_name>\n");
        xmlProperties.append(tab + "<created_by_name>" + XMLUtils.escape(getSubscriptionCreatedByName()) + "</created_by_name>\n" );
        xmlProperties.append(tab + "<Participant>" + XMLUtils.escape(getSpaceParticipantGroups(this.user ,this.spaceID)) + "</Participant>\n" );
        
        
        xmlProperties.append(this.subscribers.getXMLBody());
        xmlProperties.append(this.subscriptionSchedule.getXMLBody());

        if (space != null) {
            xmlProperties.append(this.space.getXMLProperties());
        }

        tab = "\t";

        return xmlProperties.toString();
    } // end getXML()


    /**
     * returns a well formed XML representation of this <CODE>Subscription</CODE>, including the XML Version string
     *
     * @return a well-formed XML representation of this
     * <code>Subscription</code>.
     */
    public String getXML() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(net.project.xml.IXMLTags.XML_VERSION_STRING);
        stringBuffer.append(getXMLBody());
        return stringBuffer.toString();
    }

    private void loadSpace() throws PersistenceException {
        this.space = SpaceFactory.constructSpaceFromID(spaceID);
        this.space.load(); // load the space
    }

    /**
     *  Retrieves  the default information for a <code>Subscription</code> type and places that information into this <code>Subscription</code> object
     *
     * @exception PnetException
     *                   If <code>notificationTypeID</code> is null
     * @since JDK1.3

     public void  assignDefaultInformationForNotificationType( )  throws PnetException
     {
     if ( notificationTypeID == null )
     {
     throw new PnetException("Unable to process response. Please try subscribing again. DEBUG notificaitonType was null");
     }

     String  qstr =QSTRGetNotificationTypeDefaultInformation + SINGLE_QUOTE   + notificationTypeID +  SINGLE_QUOTE ;

     try
     {
     dbBean.executeQuery (qstr);

     while ( dbBean.result.next() )
     {
     description = dbBean.result.getString("description");
     customMessage = dbBean.result.getString("default_message");
     }
     }

     catch ( SQLException sqle )
     {
     PnDebug.log ("notification.Subscription.getNotificationTypeDefaultInformation threw an SQL exception: " + sqle, true);
     throw new PnetException("Unable to process response. Please try subscribing again.");
     } // end catch

     finally
     {
     dbBean.release();
     }
     }
     */
    
  /**
   * Method to get html check box list of selected notification events
   * 
   * @param subscriptionId
   * @return html string
   */
  public String getEventSubscribeCheckList(String subscriptionId) {
      StringBuffer eventTypes = new StringBuffer();
      String sql = "";
      String objectType = "";
      String [] notificationTypeIds = null;
      DomainListBean domainListBean = new DomainListBean();
      int count = 0;
      
      try {
           if (subscriptionId != null) {
 	          sql = " select nt.notification_type_id, nt.object_type " + 
 	          		" from pn_sub_has_notify_type shnt, pn_notification_type nt " + 
					" where shnt.notification_type_id = nt.notification_type_id " + 
					" and shnt.subscription_id = '" + subscriptionId + "'  order by nt.notification_type_id ";

	          dbBean.executeQuery(sql);
	          ResultSet resultSet = dbBean.result;
	          notificationTypeIds = new String[15];
	          while (resultSet.next()) {
	        	  notificationTypeIds[count++] = resultSet.getString("notification_type_id");
	        	  objectType = resultSet.getString("object_type");
	          }

           }
      } catch (SQLException sqle) {
    	  Logger.getLogger(Subscription.class).debug("notification.Subscription.getEventSubscribeCheckList threw an SQL exception: " + sqle.getMessage());
      } // end catch
      finally {
          dbBean.release();
      }
      return  domainListBean.getNotificationTypesSubscribeCheckList(objectType, notificationTypeIds);
  }
  
	/**
	 * Method to get image url for item location
	 * 
	 * @return imageUrl
	 */
  public String getSpaceImageUrl(){
  	String imageUrl = "";
  	if(space.getSpaceType().getID().equals(ISpaceTypes.PROJECT_SPACE)){
  		imageUrl = SessionManager.getJSPRootURL() + "/images/project-icon.gif";
  	} else if(space.getSpaceType().getID().equals(ISpaceTypes.PERSONAL_SPACE)){
  		imageUrl = SessionManager.getJSPRootURL() + "/images/user-online-icon.gif";
  	}
  	return imageUrl; 
  }
  
  /**
   * Method to get item name of selected object
   * 
   * @param subscriptionId
   * @return string
   */
  public String getItemName(String subscriptionId) {
		String itemName = "";
		if (subscriptionId != null) {
			itemName = ServiceFactory.getInstance().getPnObjectNameService().getObjectNameBySubscriptionId(
					Integer.valueOf(subscriptionId));
		}
		if (StringUtils.isEmpty(itemName)) {
			itemName = PropertyProvider.get(this.subscriptionType);
		}
		return itemName;
	}
  
	/**
	 * Method to get enabled/disabled status list of subscriptions from targetObjectID
	 * 
	 * @param targetObjectID
	 * @return ArrayList object
	 */
  	public static ArrayList<Boolean> getSubscriptionStatusListByObjectID(String targetObjectID){
  		DBBean db = new DBBean();
        ArrayList<Boolean> statusList = new ArrayList<Boolean>();
        String sql = "";
        try {
        	if (StringUtils.isNotEmpty(targetObjectID)) {
        		sql = " (SELECT ps.status FROM pn_subscription ps, pn_object_has_subscription os " +
        		      " where ps.subscription_id = os.subscription_id and ps.record_status = 'A' and os.object_id = ? )" +
        		      " UNION " + 
        		      " (SELECT distinct ps.status FROM pn_subscription ps, pn_object_type_subscription ots, pn_space_has_subscription shs " +
        		      " where  ps.subscription_id = ots.subscription_id " + 
        		      " and ps.subscription_id = shs.subscription_id " +
        		      " and ots.subscription_id = shs.subscription_id " +    
        		      " and ps.record_status = 'A' and ots.object_type = (select object_type from pn_object where object_id = ?))" ;
        		   
        		int index = 0;
        		db.prepareStatement(sql);
        		db.pstmt.setString(++index, targetObjectID);
        		db.pstmt.setString(++index, targetObjectID);
        		db.executePrepared();
        		while (db.result.next()) {
        			statusList.add("1".equals(db.result.getString("status")));
        		}
        	}
        } catch (SQLException sqle) {
      	  Logger.getLogger(Subscription.class).debug("notification.Subscription.getSubscriptionStatusListByObjectID threw an SQL exception: " + sqle.getMessage());
        } finally {
        	db.release();
        }
        return statusList;	
  	}
  	
  	/**
  	 * Method to get the Subscription creator name
  	 * 
  	 * @return String
  	 */
  	public String getSubscriptionCreatedByName(){
  		if(StringUtils.isNotEmpty(this.createdByID)){
  			return	((PnPerson)ServiceFactory.getInstance().getPnPersonService().getPersonNameById(Integer.parseInt(this.createdByID))).getDisplayName();
  		}
  		return null;
  	}
  	
  	/**
  	 * Method to check the subscription created by user or not 
  	 * 
  	 * @param subscriptionId
  	 * @param userId
  	 * @return boolean
  	 */
  	public static boolean isSubscriptionCreator(String subscriptionId, String userId){
  		DBBean db = new DBBean();
  		boolean isCreator = false;
        String sql = "";
        try {
        	sql = " select ps.name from pn_subscription ps where ps.subscription_id = ? " +
        		  " and ps.created_by_id = ? and ps.record_status = 'A'";
        	int index = 0;
        	db.prepareStatement(sql);
        	db.pstmt.setString(++index, subscriptionId);
        	db.pstmt.setString(++index, userId);
        	db.executePrepared();
        	ResultSet resultSet = db.result;
        	isCreator = resultSet.next();
        } catch (SQLException sqle) {
      	  	Logger.getLogger(Subscription.class).debug("notification.Subscription.isSubscriptionCreator threw an SQL exception: " + sqle.getMessage());
        } finally {
        	db.release();
        }
        return isCreator;	
  	}
  	
    /**
     * Returns a comma separated list of all the Space participants
     *
     * @param user the instance of <code>User</code>
     * @param spaceID The Space ID
     * @return String comma separated list of all the Space participants
     */
    public String getSpaceParticipantGroups(User user, String spaceID) {
        StringBuffer participants = new StringBuffer();
        GroupCollection groupList = new GroupCollection();
        Iterator it = null;
        Group group = null;
        String display = null;
        GroupProvider provider = null;
        boolean isOrderByGroupName = true;

        try {
            Space space = SpaceFactory.constructSpaceFromID(spaceID);
            groupList.setSpace(space);
            groupList.loadAll(isOrderByGroupName);
            groupList.updateWithOwningSpace();
            
            // added separately envelope creator role if exist in current subscription
            if(StringUtils.isNotEmpty(this.subscriberBatchID) && StringUtils.isNotEmpty(this.getID())){
            	if(hasEnvelopeCreator(this.getID(), this.subscriberBatchID)){
                 	provider = new GroupProvider();
           		 	group = provider.newGroup(StepGroup.ENVELOPE_CREATOR_GROUP_ID);
           		 	group.setName(PropertyProvider.get("prm.workflow.group.envelopecreator.name"));
           		 	groupList.add(group);
            	}
            }
            Collections.sort(groupList);
            it = groupList.iterator();
            while (it.hasNext()) {
                group = (Group) it.next();
                display = group.getName();
                if (checkForSubscribers(group.getID())) {
                	participants.append(HTMLUtils.escape(display) + ", ");
                }
            }
            
            if(participants.length() > 0)
            	participants = participants.deleteCharAt(participants.length() - 2);
        } catch (PersistenceException pe) {
        	Logger.getLogger(SubscriptionBean.class).debug("SubscriptionBean.getSpaceParticipantGroups() threw a PersistenceException: " + pe);
        } catch (GroupException ge) {
            // No groups
        	Logger.getLogger(SubscriptionBean.class).debug("SubscriptionBean.getSpaceParticipantGroups() threw a GroupException: " + ge);
        }
        return participants.toString();
    }

    /**
     * Checks whether a particular subscriber belongs to a certain subscriber or not .
     * Returns true if affirmative or otherwise
     *
     * @param groupID The ID for the Subscriber group
     * @return boolean
     */
    protected boolean checkForSubscribers(String groupID) {
        Iterator itr = this.subscribers.iterator();
        while (itr.hasNext()) {
            Subscriber subscriber = (Subscriber) itr.next();
            if (subscriber != null && subscriber.getSubscriberGroupID() != null && subscriber.getSubscriberGroupID().equals(groupID)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Update and Persists the information maintained in this object
     * @exception PersistenceException
     */
    public void update() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            if (this.objectID != null) {
                // Update existing subscription
                // First update non-clob properties
                StringBuffer query = new StringBuffer();
                query.append("update pn_subscription set ");
                query.append("name = ?, description = ?, status = ?, modified_date = ?, ");
                if (this.customMessage == null) {
                    query.append("custom_message_clob = null ");
                } else {
                    query.append("custom_message_clob = empty_clob() ");
                }
                query.append("where subscription_id = ? ");

                int index = 0;
                db.prepareStatement(query.toString());
                db.pstmt.setString(++index, this.name);
                db.pstmt.setString(++index, this.description);
                db.pstmt.setBoolean(++index, this.status);
                DatabaseUtils.setTimestamp(db.pstmt, ++index, new Date());
                db.pstmt.setString(++index, getID());

                db.executePrepared();

                // Now update clob
                StringBuffer selectQuery = new StringBuffer();
                selectQuery.append("select custom_message_clob ")
                        .append("from pn_subscription ")
                        .append("where subscription_id = ? ");

                index = 0;
                db.prepareStatement(selectQuery.toString());
                db.pstmt.setString(++index, getID());
                db.executePrepared();

                if (db.result.next()) {
                    ClobHelper.write(db.result.getClob("custom_message_clob"), this.customMessage);

                } else {
                    // There is a problem if we can't find the record we just
                    // updated
                    throw new PersistenceException("Error updating custom message clob");
                }
            } 

            removeNotificationTypes(db, this.objectID);
            storeNotificationTypes(db, this.objectID);
            db.commit();

        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
                // Throw original error and release
            }
            throw new PersistenceException("Subscription store operation failed: " + sqle, sqle);

        } finally {
            db.release();
        }

        // now add the subscribers
        this.subscribers.setSubscriptionID(this.objectID);
        this.subscribers.store();
    } 
    
    /**
  	 * Method to check the subscription has envelope creator 
  	 * 
  	 * @param subscriptionId
  	 * @param subscriberBatchID
  	 * @return boolean
  	 */
  	public static boolean hasEnvelopeCreator(String subscriptionID, String subscriberBatchID) {
  		DBBean db = new DBBean();
  		boolean isEnvelopeCreator = false;
        try {
        	String query = " select delivery_group_id  from pn_subscription_has_group " +
        		  		 " where subscription_id = ? and  subscriber_batch_id = ? and delivery_group_id = ?";
        	int index = 0;
        	db.prepareStatement(query);
        	db.pstmt.setString(++index, subscriptionID);
        	db.pstmt.setString(++index, subscriberBatchID);
        	db.pstmt.setString(++index, StepGroup.ENVELOPE_CREATOR_GROUP_ID);
        	db.executePrepared();
        	ResultSet resultSet = db.result;
        	isEnvelopeCreator = resultSet.next();
        } catch (SQLException sqle) {
      	  	Logger.getLogger(Subscription.class).error("notification.Subscription.hasEnvelopCreator threw an SQL exception: " + sqle.getMessage());
        } finally {
        	db.release();
        }
        return isEnvelopeCreator;	
  	}
 }
