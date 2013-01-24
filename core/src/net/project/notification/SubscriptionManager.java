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
|   $Revision: 20476 $
|       $Date: 2010-02-26 12:12:30 -0300 (vie, 26 feb 2010) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.notification;

import java.sql.SQLException;
import java.util.HashMap;

import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.security.group.GroupDAO;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.ErrorLogger;

import org.apache.log4j.Logger;

/**
 * Manages subscriptions.
 *
 * @author chad
 * @author Tim
 * @since aardvark
 */
public class SubscriptionManager implements INotificationDB {

    /**
     * the User whose subscriptions are being managed.
     */
    private User user;

    // Map of Spaces to which subscription belongs

    private HashMap spaceMap = new HashMap();

    // Used for gettting the subscriptions for Space
    private String spaceID = null;

    /**
     * Creates an empty SubscriptionManager.
     */
    public SubscriptionManager() {
        // Do nothing
    }

    /**
     * Sets the User whose Subscriptions are being managed by this object
     * @param user the current user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the current user.
     * @return the current user
     */
    private User getUser() {
        return this.user;
    }

    /**
     * Retrieves all the Subscriptions this User is subscribed to and returns a SubscriptinCollection object containing them.
     * @return collection of <code>{@link Subscription}</code>s
     * @throws PersistenceException if there is a problem loading subscriptions
     */
    SubscriptionCollection getSubscriptions() throws PersistenceException {
        return fetchSubscriptions();
    }

    /**
     * Retrieves all the Subscriptions this User & for the Space is subscribed to and returns a SubscriptinCollection object containing them.
     *
     * @param spaceID ID of the Space
     * @return collection of <code>{@link Subscription}</code>s
     * @exception PersistenceException
     *                   if there is a problem loading subscriptions
     */
    SubscriptionCollection getSubscriptionsForSpace(String spaceID) throws PersistenceException {

        this.spaceID = spaceID;
        return fetchSubscriptions();
    }

    /**
     * Retrieves all the Subscriptions created by and subscribed to this User and returns a SubscriptinCollection object containing them.
     * @return collection of <code>{@link Subscription}</code>s
     * @throws PersistenceException if there is a problem loading subscriptions
     */
    private SubscriptionCollection fetchSubscriptions() throws PersistenceException {

        DBBean db = new DBBean();
        StringBuffer query = new StringBuffer();
        SubscriptionCollection subscriptionCollection = new SubscriptionCollection();

        // Query is as follows:
        // Select all the subscription columns from PN_SUBSCRIPTION
        // where a subscription has a group where the current user is in
        // that group (either directly or indirectly)
        // It fetches only distinct subscriptions (a user may be a member of two
        // groups that a subscription has; avoids listing subscription twice)
        query.append("select distinct s.subscription_id, s.name, s.description, ");
        query.append("s.subscription_type_id, s.created_date, s.created_by_id, s.modified_date, s.modified_by , sps.space_id , s.status ");
        query.append("from pn_space_has_subscription sps ,pn_subscription s, ");
        query.append("(select distinct shg.subscription_id from pn_subscription_has_group shg, ");
        query.append(" (" + GroupDAO.getQueryFetchAllGroupIDsForPerson() + ") all_groups ");
        query.append(" where shg.delivery_group_id = all_groups.group_id ) distinct_subscriptions ");

        if (spaceID != null) {
        	query.append("where (s.subscription_id = distinct_subscriptions.subscription_id ");
        	query.append("and sps.space_id = " + this.spaceID + " and s.record_status = 'A' and s.subscription_id = sps.subscription_id) ");
        	query.append("or (sps.space_id = " + this.spaceID + " and s.created_by_id = " + getUser().getID() + " and s.record_status = 'A' ");
        	query.append("and s.subscription_id = sps.subscription_id) ");
        } else {
            query.append("where s.subscription_id = distinct_subscriptions.subscription_id ");
            query.append("and s.record_status = 'A' and s.subscription_id = sps.subscription_id ");
        }

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, getUser().getID());
            db.pstmt.setString(++index, getUser().getID());
            db.executePrepared();

            // Fill in this SubscriptionCollection object with the next Subscription
            while (db.result.next()) {
                Subscription subscription = new Subscription(user);

                subscription.objectID = db.result.getString(SUBSCRIPTION_ID);
                subscription.name = db.result.getString(NAME);
                subscription.description = db.result.getString(DESCRIPTION);
                subscription.createdByID = db.result.getString(CREATED_BY_ID);
                subscription.createdDate = db.result.getDate(CREATED_DATE);
                subscription.spaceID = db.result.getString("SPACE_ID");
                subscription.space = OwnerSpaceGetter.getSpace(subscription.spaceID, spaceMap);
                subscription.setStatus(db.result.getBoolean("STATUS"));

                subscriptionCollection.add(subscription);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(SubscriptionManager.class).error("SubscriptionManager.getSubscriptions(): threw and SQLE: " + sqle);
            throw new PersistenceException("SubscriptionManager.getSubscriptions(): threw and SQLE: " + sqle, sqle);

        } finally {
            db.release();

        }
        return subscriptionCollection;
    }

    /**
     * This Inner class is used to get the Space object on the basis of Space ID
     * being passed  . It creates one Space Object if none is found  & also updates
     *  , HashMap containing various spaces accordingly
     *
     */
    private static class OwnerSpaceGetter {
        private static Space getSpace(String spaceID, HashMap spaceMap) throws PersistenceException {

            Space space = (Space) spaceMap.get(spaceID);
            if (space != null) {
                return space;
            } else {
                space = SpaceFactory.constructSpaceFromID(spaceID);
                try {
                    space.load(); // load the space
                    spaceMap.put(spaceID, space); // Add it to HashMap
                } catch (PersistenceException pe) {
                	Logger.getLogger(SubscriptionManager.class).error("Cannot load Space  for Subscription Manager" + pe);
                    throw new PersistenceException("Failed to load Subscription Manager ", ErrorLogger.HIGH);
                }
                return space;
            }
        }
    }
}
