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

 package net.project.notification;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.group.Group;
import net.project.workflow.StepGroup;

import org.apache.log4j.Logger;

/**
 * Provides a collection of {@link Subscriber}s in a single subscriber
 * batch.  This can contain a mixture of external and non-external subscribers.
 *
 * @author Unascribed
 * @since Version 2
 */
public class SubscriberCollection extends ArrayList {

    /**
     * The ID of the batch to which the Collection belongs.
     */
    private String batchID = null;

    /**
     * The ID of the subscription to which this collection of subscribers
     * are subscribed.
     */
    private String subscriptionID = null;

    /**
     * Determines whether the Collection object have been loaded or not , i.e , whether ,
     * the load method have been invoked or not
     */
    private boolean isLoaded = false;

    /* -------------------------------  Constructors  ------------------------------- */

    /**
     * Creates an empty SubscriberCollection.
     */
    public SubscriberCollection() {
        // do nothing.
    }


    /**
     * Adds the Subscriber to this Collection.
     *
     * @param subscriber the subscriber
     */
    private void addEntry(Subscriber subscriber) {

        if (!containsSubscriber(subscriber)) {
            subscriber.setIsNewEntry();
            this.add(subscriber);
        }

    }

    /**
     * Indicates whether this collection contains the specified subscriber.
     * Based on subscribers having matching group IDs or matching
     * external addresses.
     *
     * @param subscriber the subscriber to look for
     * @return true if this collection contains the specified subscriber;
     * false if not
     * @see Subscriber#equals
     */
    private boolean containsSubscriber(Subscriber subscriber) {

        Iterator list = this.iterator();
        Subscriber sub = null;
        boolean found = false;

        while (list.hasNext()) {
            sub = (Subscriber) list.next();

            if (sub.equals(subscriber)) {
                found = true;
                break;
            }

        } // end while

        return found;
    }

    /**
     * Constructs a new non-external <CODE>Subscriber</CODE> for the specified
     * group ID and deliver type and adds it to this collection.
     *
     * @param subscriberGroupID
     *               The ID of the Group to which the Subscriber belongs
     * @param deliveryType
     *               The Delivery type for subscription
     */
    public void addSubscriber(String subscriberGroupID, String deliveryType) {

        Subscriber subscriber = new Subscriber();

        subscriber.setIsExternalSubscriber(false);

        subscriber.setSubscriberGroupID(subscriberGroupID);
        subscriber.setDeliveryTypeID(deliveryType);

        addEntry(subscriber);

    }


    /**
     * Adds a new external subscriber to this Collection.
     *
     * @param deliveryAddress
     *               The Address of the Suscriber
     * @param deliveryType
     *               The Type in which the subscription is desired . Email is the only supportde option
     *               right now
     */
    public void addExternalSubscriber(String deliveryAddress, String deliveryType) {

        Subscriber subscriber = new Subscriber();

        subscriber.setIsExternalSubscriber(true);

        subscriber.generateAddressID();
        subscriber.setDeliveryAddress(deliveryAddress, deliveryType);
        subscriber.setDeliveryTypeID(deliveryType);

        addEntry(subscriber);
    }

    /**
     * Loads all external and non-external subscribers and adds each once to
     * this collection.
     * @throws IllegalStateException if the current batch ID is null
     */
    public void load() {

        if (this.batchID == null) {
            throw new IllegalStateException("BatchID is required");
        }

        DBBean db = new DBBean();

        try {
            loadSubscribers(db);
            loadExternalAddresses(db);

            this.isLoaded = true;
        } catch (SQLException sqle) {
        	Logger.getLogger(SubscriberCollection.class).debug("SubscriberCollection.load() threw an SQLException: " + sqle);
        } catch (PersistenceException pe) {
        	Logger.getLogger(SubscriberCollection.class).debug("SubscriberCollection.load() threw a PersistenceException: " + pe);
        } finally {
            db.release();
        }

    }


    /**
     * Loads the external subscribers for the current batch ID and adds them to
     * this collection.
     * <p>
     * The subscriber's address has no recipient display name.
     * For external subscribers, the customization user is defined to be
     * the creator of the subscription record.  That is, when a notification
     * is sent to the subscriber, it will be based on the i18n and l10n settings
     * of the subscription creator (language, timezone, date and number format etc.).
     * </p>
     *
     * @throws SQLException if there is a problem loading
     * @throws NullPointerException if the current batchID is null
     */
    private void loadExternalAddresses(DBBean db) throws SQLException {

        String qstrLoadAddressGroup = "select pda.address_id, pda.address_group_id, pda.address, pda.delivery_type_id, pns.created_by_id " +
                "from pn_delivery_address pda, pn_subscription pns " +
                "where pda.address_group_id = " + this.batchID + " and pns.subscriber_batch_id = pda.address_group_id ";

        db.executeQuery(qstrLoadAddressGroup);

        while (db.result.next()) {
            Subscriber subscriber = new Subscriber();

            subscriber.setIsExternalSubscriber();

            subscriber.setExternalAddressID(db.result.getString("address_id"));
            subscriber.setSubscriberBatchID(db.result.getString("address_group_id"));
            subscriber.setDeliveryAddress(db.result.getString("address"), db.result.getString("delivery_type_id"), null, db.result.getString("created_by_id"));
            subscriber.setDeliveryTypeID(db.result.getString("delivery_type_id"));

            this.add(subscriber);
        }

    }

    /**
     * Loads the non-external subscribers for the current batch ID.
     * <p>
     * Each subscriber maintains a collection of addresses, each one representing
     * a person derived from the contents of the subscriber group.
     * The customization user is defined to be the user receiving the notification.
     * </p>
     *
     * @throws SQLException if there is a problem loading
     * @throws PersistenceException if there is a problem loading
     * @throws NullPointerException if the current batchID is null
     */
    private void loadSubscribers(DBBean db) throws SQLException, PersistenceException {

        String qstrLoadSubscriberGroup = "select hg.subscription_id, hg.delivery_type_id, " +
                " hg.delivery_group_id , g.group_name " +
                "from pn_subscription_has_group hg , pn_group g where hg.subscriber_batch_id = " + this.batchID +
                " and hg.delivery_group_id = g.group_id ";

        db.executeQuery(qstrLoadSubscriberGroup);

        while (db.result.next()) {
            Subscriber subscriber = new Subscriber();

            subscriber.setSubscriberBatchID(this.batchID);
            subscriber.setSubscriberGroupID(db.result.getString("delivery_group_id"));
            subscriber.setDeliveryTypeID(db.result.getString("delivery_type_id"));
            subscriber.setGroupName(db.result.getString("group_name"));
            if(!subscriber.getSubscriberGroupID().equals(StepGroup.ENVELOPE_CREATOR_GROUP_ID)){
            	subscriber.loadDeliveryAddresses();
            } else {
            	subscriber.loadDeliveryAddresses(this.batchID);
            }

            this.add(subscriber);
        }

    }

    /**
     * Stores the information in the database
     */
    public void store() {

        storeSubscribers();
        storeAddresses();

        // now clear out the object and reload
        super.clear();
        this.load();

    }

    /**
     * Deletes the Addresses of the current from the database
     *
     * @exception SQLException
     */
    private void deleteCurrentAddresses(DBBean db) throws SQLException {
        String qstrDeleteCurrentSubscribers = "delete from pn_delivery_address where address_group_id = ?";

        // first delete the existing ones...
        db.prepareStatement(qstrDeleteCurrentSubscribers);
        db.pstmt.setString(1, getBatchID());
        db.executePrepared();
    }


    /**
     * Stores the Addresses of the Subscriber in the database
     */
    private void storeAddresses() {

        Iterator iterator = null;
        Subscriber subscriber = null;
        String qStrInsertAddresses = "insert into pn_delivery_address" +
                " (address_id, address_group_id, address, delivery_type_id) " +
                " values (?, ?, ?, ?)";

        if (getBatchID() == null)
            this.batchID = getNewObjectID();

        iterator = this.iterator();

        DBBean db = new DBBean();

        try {

            // first delete existing addresses
            deleteCurrentAddresses(db);

            db.prepareStatement(qStrInsertAddresses);

            while (iterator.hasNext()) {

                subscriber = (Subscriber) iterator.next();

                if (subscriber.isExternalSubscriber()) {

                    if (subscriber.isNewEntry()) {
                        db.pstmt.setString(1, subscriber.getExternalAddressID());
                        db.pstmt.setString(2, getBatchID());
                        db.pstmt.setString(3, ((Address) subscriber.getDeliveryAddressList().get(0)).getAddress());
                        db.pstmt.setString(4, subscriber.getDeliveryTypeID());

                        db.executePrepared();

                        // now get this object out of the collection
                        iterator.remove();

                    }

                } // end if is external
            } // end while

        } // end try
        catch (SQLException sqle) {
        	Logger.getLogger(SubscriberCollection.class).debug("SubscriberCollection.store() threw an SQLException: " + sqle);

        } finally {
            db.release();
        }
    }


    /**
     * Deletes the current set of subscribers
     *
     * @exception SQLException
     */
    private void deleteCurrentSubscribers(DBBean db) throws SQLException {
        String qstrDeleteCurrentSubscribers = "delete from pn_subscription_has_group where subscriber_batch_id = ? " +
                " and subscription_id = ?";

        db.prepareStatement(qstrDeleteCurrentSubscribers);
        int index = 0;
        db.pstmt.setString(++index, getBatchID());
        db.pstmt.setString(++index, getSubscriptionID());

        db.executePrepared();

    }

    /**
     * Stores the current set of subscribers to the database
     */
    private void storeSubscribers() {
        Iterator iterator = null;
        Subscriber subscriber = null;
        String qStrInsertSubscribers = "insert into pn_subscription_has_group" +
                " (subscription_id, delivery_type_id, subscriber_batch_id, delivery_group_id) " +
                " values (?, ?, ?, ?)";

        if (getBatchID() == null)
            this.batchID = getNewObjectID();

        iterator = this.iterator();

        DBBean db = new DBBean();

        try {

            deleteCurrentSubscribers(db);

            db.prepareStatement(qStrInsertSubscribers);

            while (iterator.hasNext()) {

                subscriber = (Subscriber) iterator.next();

                if (!subscriber.isExternalSubscriber()) {

                    // The deleteCurrentSubscribers() method remove ALL subscribers
                    // hence all subscribers need re-created!
                    db.pstmt.setString(1, getSubscriptionID());
                    db.pstmt.setString(2, subscriber.getDeliveryTypeID());
                    db.pstmt.setString(3, getBatchID());
                    db.pstmt.setString(4, subscriber.getSubscriberGroupID());

                    db.executePrepared();

                    // now get this object out of the collection
                    iterator.remove();

                }
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(SubscriberCollection.class).debug("SubscriptionCollection.store() threw an SQLException: " + sqle);

        } finally {
            db.release();
        }
    }


    /**
     * Resets the class
     */
    public void clear() {
        this.batchID = null;
        this.subscriptionID = null;
        super.clear();
    }


    /**
     * -------------------------------  REMOVAL  -------------------------------
     * Romoves the subscriber from the collection
     *
     * @param subscriberGroupID
     *               The ID for the Subscriber Group
     * @exception NotificationException
     */
    public void removeSubscriber(String subscriberGroupID) throws NotificationException {

        Iterator list = this.iterator();
        Subscriber subscriber = null;

        if (this.isLoaded) {

            while (list.hasNext()) {

                subscriber = (Subscriber) list.next();

                if (subscriberGroupID.equalsIgnoreCase(subscriber.getSubscriberGroupID()))
                    list.remove();

            } // end while
        } // end if isLoaded
        else
            throw new NotificationException("SubscriberCollection.removeSubscriber() failed because collection not loaded");
    }

    /* -------------------------------  Setter / Getter  ------------------------------- */

    /**
     * Returns a collection of addresses that are the addresses
     * for all subscribers in this collection.
     * Duplicate addresses are stripped out; a duplicate address is defined
     * as an address with the same recipient address and delivery type
     * as another address.
     * @return the collection of {@link Address} objects
     */
    public ArrayList getConsolidatedAddressList() {
        ArrayList allAddressList = new ArrayList();
        ArrayList finalAddressList = new ArrayList();
        Address address = null;

        // Iterate over all subscribers, build a collection of all the
        // delivery addresses for those subscribers
        Iterator subscriberIt = iterator();
        while (subscriberIt.hasNext()) {
            Subscriber sub = (Subscriber) subscriberIt.next();
            allAddressList.addAll(sub.getDeliveryAddressList());
        }

        // Iterate over all address, building a list of distinct addresses
        Iterator addressIt = allAddressList.iterator();
        while (addressIt.hasNext()) {
            address = (Address) addressIt.next();

            if (!finalAddressList.contains(address)) {
                finalAddressList.add(address);
            }

        }

        return finalAddressList;
    }


    /**
     * Creates a new Batch ID and sets it
     */
    public void setBatchID() {
        this.batchID = getNewObjectID();
    }

    /**
     * Sets the Batch ID as per the parameter that has been passed
     *
     * @param batchID The Batch ID
     */
    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }

    /**
     * Returns the Batch ID
     *
     * @return The ID for the Batch
     */
    public String getBatchID() {
        return this.batchID;
    }

    /**
     * Creates a new Batch ID
     *
     * @return
     */
    private String getNewObjectID() {
        return (net.project.database.DatabaseUtils.getNextSequenceValue());
    }

    public void setSubscriptionID(String id) {
        this.subscriptionID = id;
    }

    /**
     * Gets the Subscription ID
     *
     * @return    the Subscription ID
     */
    private String getSubscriptionID() {
        return this.subscriptionID;
    }

    /* -------------------------------  XML Stuff  ------------------------------- */

    /**
     * Returns a String representing the XML representation of Subscriber
     *
     * @return String
     */
    public String getXML() {

        StringBuffer xml = new StringBuffer();

        xml.append(net.project.xml.IXMLTags.XML_VERSION_STRING);
        xml.append(getXMLBody());

        return xml.toString();

    }

    /**
     * Returns a String representing the XML representation of SubscriberCollection
     *
     * @return String the body of this object in an XML String
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        Subscriber subscriber = null;
        Iterator i = this.iterator();

        xml.append("<subscriber_group_list>\n");

        while (i.hasNext()) {
            subscriber = (Subscriber) i.next();
            xml.append(subscriber.getXMLBody());
        }

        xml.append("</subscriber_group_list>\n");

        return xml.toString();
    }


}
