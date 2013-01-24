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

 package net.project.security.domain;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.User;

/**
 * .
 * 
 * @author Philip Dixon
 * @see net.project.security.domain.UserDomain
 * @since Gecko
 */
class DomainUserCollection extends java.util.HashMap implements java.io.Serializable {

    /** ID of the domain this collection of users belongs to */
    String domainID = null;

    /** Database access bean */
    private DBBean db = new DBBean();

    /** isLoaded flag */
    private boolean isLoaded = false;


    /* ------------------------------- Constructor(s)  ------------------------------- */

    protected DomainUserCollection() {
        // do nothing
    }


    /**
     * Instantiate a new <code>DomainUserCollection</code> object with the domainID specified
     * 
     * @param domainID The id of the domain in question.
     * @since Gecko
     */
    protected DomainUserCollection (String domainID) {

        setDomainID (domainID);
    }


    /* ------------------------------- Interaction Methods  ------------------------------- */


    /**
     * Boolean indicator of whether or not the specified user is a member of this domain.
     * Will return false by default if the collection is not loaded.
     * 
     * @param userID ID of the user
     * @return True if the user is a member of the domain, false if not
     */
    protected boolean isDomainUser (String userID) {

        return ( this.isLoaded ) ? this.containsKey (userID) : false;
    }


    /**
     * Adds the specified user to the domain for the current domain ID.
     * @precondition a domain ID has been set with {@link #setDomainID}
     * @param user the user to add; requires ID and login to be
     * available
     * @throws PersistenceException if the user is already a member
     * of the domain, or if a problem occurs adding the user to the domain
     */
    protected void addUser(User user) throws PersistenceException {
        
        DBBean db = new DBBean();

        try {
            addUser(user);  
        } finally{
            db.release();
        }

    }

    /**
     * Adds the specified user to the domain for the current domain ID.
     * @precondition a domain ID has been set with {@link #setDomainID}
     * @param user the user to add; requires ID and login to be
     * available
     * @param db the database bean
     * @throws PersistenceException if the user is already a member
     * of the domain, or if a problem occurs adding the user to the domain
     */
    protected void addUser(User user , DBBean db) throws PersistenceException {

        if (isDomainUser(user.getID())) {
            throw new PersistenceException ("DomainUserCollection.addUser(): User is already a member of the domain");
        }

        boolean isNewUser = true;
        
        StringBuffer query = new StringBuffer();
        
        query.append("select 1 from pn_user where user_id = ? ");
    
       try {
            db.prepareStatement(query.toString());
            db.pstmt.setString(1,user.getID());

            db.executePrepared();

            if(db.result.next()){
                isNewUser = false ;
            }
            
            if (isNewUser) {

                String qstrCreateUser = "insert into pn_user (user_id, username, domain_id, record_status, is_login) " +
                    " values (?,?,?,'A',?)";
                
                int index = 0;
       
                db.prepareStatement(qstrCreateUser);

                db.pstmt.setString(++index, user.getID());
                db.pstmt.setString(++index, user.getLogin());
                db.pstmt.setString(++index, getDomainID());
                db.pstmt.setString(++index, "0");

                db.executePrepared();

            } else {

                String qstrUpdateUser = "update pn_user set username = ? ,  domain_id = ? where user_id = ? " ;

                int index = 0;

                db.prepareStatement(qstrUpdateUser);

                db.pstmt.setString(++index, user.getLogin());
                db.pstmt.setString(++index, getDomainID());
                db.pstmt.setString(++index, user.getID());
              
                db.executePrepared();

            }

        } catch (SQLException sqle) {
            throw new PersistenceException ("DomainUserCollection.addUser(): Add user operation failed due to a SQLE."+sqle, sqle);
        } 
    }


    /**
     * Populate this collection with the userIDs and usernames of all of the people
     * that are members of the domain specified by the class member "domainID".
     * This load operation performs a lazy load, bringing in only the userIDs & usernames - *not* fully populated
     * <code>User</code>objects for each user.
     * The load process does however insert EMPTY <code>User</code> objects into the collection -- mapped to the user_id
     * 
     * The usernames are included since that is actually the unique identifier
     * of a user within a particular domain and is used after authenticating
     * a user to fetch the actual user object.
     * 
     * @param dbean  The database bean
     * @exception PersistenceException
     *                   Throws a <code>PersistenceException</code> if the load operation fails or of the domainID is invalid.
     * @see net.project.persistence.PersistenceException
     * @see net.project.resource.Person
     * @since Gecko
     */
    protected void load(DBBean dbean) throws PersistenceException {

        String qstrLoadUsersForDomain = "select u.user_id, u.username from pn_user u where domain_id = " + getDomainID();

        if (this.domainID == null) {
            throw new PersistenceException ("DomainUserCollection.load() was unable to load the users for this domain " +
                                            "because of an invalid (null) domainID");
        }


        // now, first clear the collection so that we don't have weird data issues
        clear();

        try {

            dbean.executeQuery (qstrLoadUsersForDomain);

            while (dbean.result.next()) {

                // instantiate a new user object whose ID is the user_id returned from the database
                // then put that user into the HASH mapped against the user_id
                User user = new User();
                user.setID(dbean.result.getString ("user_id"));
                user.setUserName(dbean.result.getString("username"));
                this.put(user.getID(), user);
            }

            this.isLoaded = true;

        } catch (SQLException sqle) {

            this.isLoaded = false;
            throw new PersistenceException ("DomainUserCollection.load() threw an SQLE: ", sqle);

        }

    }

    /**
     * Populate this collection with the userIDs and usernames of all of the people 
     * that are members of the domain specified by the class member "domainID".
     * This load operation performs a lazy load, bringing in only the userIDs & usernames - *not* fully populated
     * <code>User</code>objects for each user.
     * The load process does however insert EMPTY <code>User</code> objects into the collection -- mapped to the user_id
     * 
     * The usernames are included since that is actually the unique identifier
     * of a user within a particular domain and is used after authenticating
     * a user to fetch the actual user object.
     *
     * @throws PersistenceException
     *                   Throws a <code>PersistenceException</code> if the load operation fails or of the domainID is invalid.
     * @see net.project.persistence.PersistenceException
     * @see net.project.resource.Person
     * @since Gecko
     */
    protected void load() throws PersistenceException {
        DBBean dbean = new DBBean ();

        try {
            load(dbean);
        } finally {
            dbean.release();
        }
    }


    /* ------------------------------- Getters and Setters  ------------------------------- */


    /**
     * Set the ID of this domain
     * 
     * @param domainID The ID of this domain
     */
    protected void setDomainID (String domainID) {
        this.domainID = domainID;
    }


    /**
     * Get the ID of this domain
     * 
     * @param domainID The ID of this domain
     */
    protected String getDomainID() {
        return this.domainID;
    }


    /**
     * Returns a boolean indicator represented the "loaded" status of this collection.
     * 
     * @return True if the collection is loaded, false if not.
     * @since Gecko
     */
    protected boolean isLoaded() {

        return this.isLoaded;
    }


    /* ------------------------------- Utility Methods  ------------------------------- */

    public void clear() {

        super.clear();
        this.isLoaded = false;
    }




}
