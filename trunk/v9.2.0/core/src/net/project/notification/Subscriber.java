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

import org.apache.log4j.Logger;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.group.Group;
import net.project.security.group.GroupException;
import net.project.security.group.GroupProvider;
import net.project.workflow.StepGroup;

/**
 * Represents a single subscriber for the notification in the System.
 *
 * @author Unascribed
 * @since Version 2
 */
public class Subscriber {

    /**
     * The ID of the Group to which the subscriber belongs.
     */
    private String subscriberGroupID = null;

    /**
     * The ID of the Batch to which the subscriber belongs.
     */
    private String subscriberBatchID = null;

    /**
     * The ID of the Delivery Type.
     */
    private String deliveryTypeID = null;

    /**
     * The ID of the Address for the subscriber.
     */
    private String externalAddressID = null;

    /**
     * Collection of Address.
     */
    final ArrayList addressCollection = new ArrayList();

    /**
     * Indicates whether the Subscriber is external to the system or not.
     */
    public boolean isExternalSubscriber = false;

    /**
     * Indicates whether the entry is new or not.
     */
    private boolean isNewEntry = false;

    /**
     * The name of the Group to which the subscriber belongs to.
     */
    private String groupName = null;

    /**
     * Does nothing except for the instantiation of new object.
     */
    public Subscriber() {
        // do nothing
    }

    /**
     * Sets the Subscriber group ID.
     * This is the group that this subscriber represents.
     *
     * @param subscriberID the ID of the group that this Subscriber represents
     */
    public void setSubscriberGroupID(String subscriberID) {
        this.subscriberGroupID = subscriberID;
    }

    /**
     * Returns the Group ID for the Subscriber
     *
     * @return String
     */
    public String getSubscriberGroupID() {
        return this.subscriberGroupID;
    }

    /**
     * Sets the batch ID.
     *
     * @param batchID String
     */
    public void setSubscriberBatchID(String batchID) {
        this.subscriberBatchID = batchID;
    }

    /**
     * Sets the Delivery ID.
     *
     * @param typeID String
     */
    public void setDeliveryTypeID(String typeID) {
        this.deliveryTypeID = typeID;
    }

    /**
     * Sets the ID for the Address
     *
     * @param id  String  The ID of the Address
     */
    public void setExternalAddressID(String id) {
        this.externalAddressID = id;
    }

    /**
     * Gets the ID for Address of the Subscriber
     *
     * @return ID for Address of the Subscriber
     */
    public String getExternalAddressID() {
        return this.externalAddressID;
    }

    /**
     * Sets the Group Name for the Subscriber.
     * This may be a token or plain text.
     *
     * @param name  String  The Group Name of the Subscriber
     */
    public void setGroupName(String name) {
        this.groupName = name;
    }

    /**
     * Gets the Group Name for the Subscriber.
     * This looks up the token if the group name is a token.
     *
     * @return name The Group Name of the Subscriber
     */
    private String getGroupName() {
        return PropertyProvider.get(this.groupName);
    }

    /**
     * Constructs a new <CODE>Address</CODE> object based on the textual representation
     * of the Address, delivery type ID and customizationUserID , which are being passed on as parameters and
     * adds it to the collection for addresses for the Subscriber in the System
     *
     * @param address The textual representation of Address
     * @param deliveryTypeID ID of the Delivery Type
     * @param recipientName the display name of the recipient
     * @param customizationUserID the id of the user whose customization settings would be used
     */
    public void setDeliveryAddress(String address, String deliveryTypeID, String recipientName, String customizationUserID) {
        Address recipientAddress = new Address(address, deliveryTypeID, recipientName, customizationUserID);
        this.addressCollection.add(recipientAddress);
    }

    /**
     * Constructs a new <CODE>Address</CODE> object based on the textual representation
     * of the Address and delivery type ID , which are being passed on as parameters and
     * adds it to the collection for addresses for the Subscriber in the System.
     * <p>
     * The address has no recipient name or customization user ID. This method
     * is only used when setting the address before storing the subscriber.
     * </p>
     *
     * @param address The textual representation of Address
     * @param deliveryTypeID
     *                ID of the Delivery Type
     */
    public void setDeliveryAddress(String address, String deliveryTypeID) {
        this.addressCollection.add(new Address(address, deliveryTypeID));
    }

    /**
     * Returns the collection of Addresses for the Subscriber
     *
     * @return Collection of Addresses for the Subscriber
     */
    public ArrayList getDeliveryAddressList() {
        return this.addressCollection;
    }

    /**
     * Loads the Delivery Address from the Database
     *
     * @exception PersistenceException
     *                   thrown if something is wrong with the loading process
     */
    public void loadDeliveryAddresses() throws PersistenceException {
        Group group = null;

        GroupProvider groupProvider = new GroupProvider();
        this.addressCollection.clear();

        if (getDeliveryTypeID() == null) {
            throw new PersistenceException("Subscriber.loadDeliveryAddresses(): deliveryTypeID can NOT be NULL");
        }
        if (!this.isExternalSubscriber) {

            // Get a new group for the specified group id
            try {
                group = groupProvider.newGroup(this.subscriberGroupID);
                group.load();

                Iterator people = group.getAllPersonMembers().iterator();
                while (people.hasNext()) {
                    net.project.resource.Person person = (net.project.resource.Person) people.next();

                    this.addressCollection.add(new Address(person.getEmail(), getDeliveryTypeID(), person.getDisplayName(), person.getID()));
                }
            } catch (GroupException ge) {
// Unable to create new group based on group id
// We ignore group
            }
        } else {
            // Is external subscriber

        }
    }

    /**
     * Returns	the Delivery Type ID for the Subscriber
     *
     * @return the Delivery Type ID for the Subscriber
     */
    public String getDeliveryTypeID() {
        return this.deliveryTypeID;
    }

    /**
     * Always sets the flag for <CODE>Subscriber</CODE> object as  being a new entry to
     * the System , to true
     */
    void setIsNewEntry() {
        this.isNewEntry = true;
    }

    /**
     * Sets the flag for <CODE>Subscriber</CODE> object as  being an External Subscriber
     * ( means that the subsciber isn't registered with the system )   to
     * the System , to the value being passed on as the parameter
     */
    void setIsExternalSubscriber(boolean flag) {
        this.isExternalSubscriber = flag;
    }

    /**
     * Always Sets the flag for <CODE>Subscriber</CODE> object as  being an External Subscriber
     * ( means that the subsciber isn't registered with the system )   to
     * the System , to true
     */
    void setIsExternalSubscriber() {
        setIsExternalSubscriber(true);
    }

    /**
     *  Returns a <code>boolean</code> value to indicate whether
     *  the Subscriber is external( not registered as yet) to the system
     */

    boolean isExternalSubscriber() {
        return this.isExternalSubscriber;
    }

    /**
     *  Returns a <code>boolean</code> value to indicate whether
     *  the Subscriber is new to system or not
     */
    boolean isNewEntry() {
        return this.isNewEntry;
    }

    /**
     * Generates the Address ID programatically based on the sequence no. from the database
     */
    void generateAddressID() {
        this.externalAddressID = net.project.database.DatabaseUtils.getNextSequenceValue();
    }

    /**
     * Overloaded to indicate whether the Subscriber passed as the parameter
     * is same or not
     *
     * @param subscriber <CODE>Subscriber</CODE>object passed to the parameter
     * @return
     */
    public boolean equals(Subscriber subscriber) {

        boolean found = false;

        if (this.isExternalSubscriber != subscriber.isExternalSubscriber)
            found = false;

        else if (!this.isExternalSubscriber) {
            if (subscriber.getSubscriberGroupID().equals(this.getSubscriberGroupID()))
                found = true;
        } else {
            if (subscriber.getExternalAddressID().equals(this.getExternalAddressID()))
                found = true;
        }

        return found;
    }

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
     * Returns a String representing the XML representation of Subscriber
     *
     * @return String the properties of this object in an XML String
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<subscriber>\n");
        xml.append(getXMLProperties());
        xml.append("</subscriber>\n");

        return xml.toString();
    }

    /**
     * Returns the properties of this Subscriber object as an XML String.
     *
     * @return the properties of this object in an XML String
     */
    private String getXMLProperties() {

        StringBuffer xml = new StringBuffer();

        xml.append("<subscriber_group_id>" + this.subscriberGroupID + "</subscriber_group_id>\n");
        xml.append("<subscriber_batch_id>" + this.subscriberBatchID + "</subscriber_batch_id>\n");

        if (this.groupName != null && !groupName.trim().equals("")) {
            xml.append("<group_name>" + getGroupName() + "</group_name>\n");
        } else {
            xml.append("<group_name>" + PropertyProvider.get("prm.notification.subscriber.group.external.name") + "</group_name>\n");
        }

        /* try {
             this.loadDeliveryAddresses();
         } catch (PersistenceException pe){
             ApplicationLogger.logEvent ("Subscriber.getXMLProperties() threw an PersistenceException: " + pe);
         }
        */
        xml.append("<address_collection>\n");

        //if(!this.isExternalSubscriber){
        Iterator itr = this.addressCollection.iterator();
        while (itr.hasNext()) {
            Address address = (Address) itr.next();
            xml.append(address.getXMLBody());
        }
        /* } else {
                 xml.append ("<Address>\n");
                 xml.append ("<AddressDetails>"+XMLUtils.escape(this.externalDeliveryAddress);
                 xml.append ("<\Address>\n");

         }
 */
        xml.append("</address_collection>\n");
        xml.append("<delivery_type_id>" + this.deliveryTypeID + "</delivery_type_id>\n");

        return xml.toString();
    }

    /**
     * Loads the Delivery Address from the Database for envelop creator role
     *
     * @exception PersistenceException
     *                   thrown if something is wrong with the loading process
     */
    public void loadDeliveryAddresses(String subscriptionBatchID) throws PersistenceException {
    	this.addressCollection.clear();

        if (!this.isExternalSubscriber) {
	        DBBean db = new DBBean();
	        String envelopeCreatorSQL = "select "+ 
						"DISTINCT s.created_by_id, p.display_name, p.email, shg.delivery_type_id "+
					"from "+ 
						"pn_subscription_has_group shg, pn_subscription s, pn_person_view p "+
					"where "+ 
						"s.subscription_id = shg.subscription_id and p.person_id = s.created_by_id "+
						"and p.record_status = 'A' and shg.delivery_group_id = ? "+ 
						"and shg.subscriber_batch_id = ? " ;
	        try{
	        	db.prepareStatement(envelopeCreatorSQL);
	        	db.pstmt.setString(1, StepGroup.ENVELOPE_CREATOR_GROUP_ID);
	        	db.pstmt.setString(2, subscriptionBatchID);
	        	db.executePrepared();
	        	
	        	while(db.result.next()){
	                this.addressCollection.add(new Address(db.result.getString("email"), db.result
							.getString("delivery_type_id"), db.result.getString("display_name"), db.result.getString("created_by_id")));
	        	}
	        }catch (SQLException e) {
	        	Logger.getLogger(Subscriber.class).error("Scubscriber.loadDeliveryAddresses() failed.."+e.getMessage());
			} finally{
				db.release();
			}
        }else {
            // Is external subscriber

        }
    }
}
