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

import net.project.base.directory.IDirectoryEntry;
import net.project.database.DBBean;
import net.project.security.SecurityException;
import net.project.security.User;

import org.apache.log4j.Logger;


/**
 * Manager class which provides utility and transaction brokering between users, domains and the security system.
 * 
 * @author Philip Dixon
 * @see net.project.security.domain.UserDomain
 * @since Gecko
 */
public class UserDomainManager implements java.io.Serializable {

    /** Static for the default Project.net domain */
    public static final String DEFAULT_PROJECT_NET_DOMAIN_ID = "1000";

    public UserDomainManager() {
        // do nothing
    }


    /* ------------------------------- Interaction Methods  ------------------------------- */

    /**
     * Add a user to the specified domain.
     * @param user user to be added to the domain
     * @param domainID the id of the domain to which to add user
     * @param directoryEntry the directory-specific entry information
     * captured while creating the user
     * @throws DomainException if there is a problem adding the user
     * @since Gecko
     */
    public static void addUserToDomain (User user, String domainID, IDirectoryEntry directoryEntry)
            throws DomainException {
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            db.openConnection();
            addUserToDomain(user, domainID, directoryEntry, db);
            db.commit();
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
            	Logger.getLogger(UserDomainManager.class).debug("A fatal error has occurred.  Unable " +
                    "to roll back the addition of a user to domain in " +
                    "UserDomainManager.addUserToDomain().");
            }
        } finally {
            db.release();
        }
    }

    /**
     * Add a user to the specified domain.
     * @param user user to be added to the domain
     * @param domainID the id of the domain to which to add user
     * @param directoryEntry the directory-specific entry information
     * captured while creating the user
     * @param db a <code>DBBean</code> object already in a transaction.
     * @throws DomainException if there is a problem adding the user
     * @since Gecko
     */
    public static void addUserToDomain (User user, String domainID,
        IDirectoryEntry directoryEntry, DBBean db) throws DomainException {

        try {
            UserDomain domain = UserDomainFactory.makeDomainForDomainID(domainID, db);
            domain.addUser(user, directoryEntry, db);
        } catch (SecurityException se) {
            throw new DomainException ("UserDomainManager.addUserToDomain() operation failed: ", se);
        }
    }

    /**
     * Updates a user in the specified domain.
     * @param user the user to update in the domain
     * @param domainID the id of the domain in which to update user
     * @param directoryEntry the directory-specific entry information
     * captured while creating the user
     * @throws DomainException if there is a problem updating the user
     * in the domain or directory
     */
    public static void updateUserInDomain(User user, String domainID, IDirectoryEntry directoryEntry )
            throws DomainException {

        DBBean db = new DBBean();

        try {
            updateUserInDomain(user, domainID , directoryEntry , db);
        } finally {
            db.release();
        }
        
    }

    /**
     * Updates a user in the specified domain.
     * @param user the user to update in the domain
     * @param domainID the id of the domain in which to update user
     * @param directoryEntry the directory-specific entry information
     * captured while creating the user
     * @throws DomainException if there is a problem updating the user
     * in the domain or directory
     */
    public static void updateUserInDomain(User user, String domainID, IDirectoryEntry directoryEntry , DBBean dbean)
            throws DomainException {

        try {
            UserDomain domain = UserDomainFactory.makeDomainForDomainID(domainID);
            domain.updateUser(user, directoryEntry , dbean);

        } catch (SecurityException se) {
            throw new DomainException ("UserDomainManager.updateUserInDomain() operation failed: ", se);
        
        }


    }

// 04/02/2002 - Tim
// Obsolete?
//     /**
//      * Returns a <code>UserDomain</code> object for the specified <code>User</code>
//      * The work is delegated to the <code>UserDomainFactory</code> object
//      *
//      * @param user User object
//      * @return An instantiated UserDomain object.
//      *
//      * @exception SecurityException
//      *                   Throws a <code>SecurityException</code> if the context object (or the information contained therein) is not found or registered to any domain
//      * @see net.project.security.domain.UserDomainFactory
//      * @see net.project.security.domain.UserDomain
//      * @see net.project.security.User
//      * @since Gecko
//      */
//      public static UserDomain getUserDomain (User user) throws SecurityException {
//
//         return ( UserDomainFactory.getUserDomain (user) );
//     }
//
//
//     /**
//      * Returns a <code>UserDomain</code> object for the specified <code>AuthenticationContext</code>
//      * The work is delegated to the <code>UserDomainFactory</code> object
//      *
//      * @param context Object containing an authentication context
//      * @return An instantiated UserDomain object.
//      *
//      * @exception SecurityException
//      *                   Throws a <code>SecurityException</code> if the context object (or the information contained therein) is not found or registered to any domain
//      * @see net.project.security.domain.UserDomainFactory
//      * @see net.project.security.domain.UserDomain
//      * @since Gecko
//      */
//     public static UserDomain getUserDomain (AuthenticationContext context) throws SecurityException {
//
//         return ( UserDomainFactory.getUserDomain (context) );
//     }
//
//
//     /**
//      * Returns a <code>UserDomain</code> object for the specified user_id.
//      *
//      * @param user_id ID of the user in question
//      * @return An instantiated UserDomain object.
//      *
//      * @exception SecurityException
//      *                   Throws a <code>SecurityException</code> if the user is not found or registered to any domain
//      * @see net.project.security.UserDomain
//      * @since Gecko
//      */
//     public static UserDomain getUserDomain (String user_id) throws SecurityException {
//         return null;
//     }



    /**
     * Returns an instantiated <code>UserDomain</code> object for the system
     * default UserDomain
     * 
     * @return An instantiated <code>UserDomain</code> object for the system default UserDomain
     * @since Gecko
     */
    public static UserDomain getDefaultUserDomain() throws SecurityException {
        return UserDomainFactory.makeDomainForDomainID (DEFAULT_PROJECT_NET_DOMAIN_ID);
    }



        /* ------------------------------- Query Convenience Methods  ------------------------------- */    

//     /**
//      * Return the ID of the UserDomain that the specified user belongs to.
//      *
//      * @param userID ID of the user in question
//      * @return UserDomain ID of the domain the user belongs to.
//      *
//      * @exception SecurityException
//      *                   Throws <code>SecurityException</code> if there is an SQLException or if there is no domain record found for the specified userID.
//      * @since Gecko
//      */
//     public static String getDomainIDForUserID (String userID) throws SecurityException {
//
//         DBBean db = new DBBean();
//         String domainID = null;
//
//         try {
//             domainID = getDomainIDForUserID (userID, db);
//         }
//
//         finally {
//             db.release();
//         }
//
//         return domainID;
//     }
//
//
//     /**
//      * Return the ID of the UserDomain that the specified user belongs to.
//      *
//      * @param userID ID of the user in question
//      * @param db     Already instantiated database bean.
//      * @return UserDomain ID of the domain the user belongs to.
//      *
//      * @exception SecurityException
//      *                   Throws <code>SecurityException</code> if there is an SQLException or if there is no domain record found for the specified userID.
//      * @since Gecko
//      */
//     public static String getDomainIDForUserID (String userID, DBBean db) throws SecurityException {
//
//         String qstrGetDomainIDForUser = "select domain_id from pn_user_view where user_id = " + userID;
//         String domainID = null;
//
//         try {
//
//             db.executeQuery (qstrGetDomainIDForUser);
//
//             if ( db.result.next() )
//                 domainID = db.result.getString ("domain_id");
//         }
//
//         catch (SQLException sqle) {
//             throw new SecurityException ("UserDomainManager.getDomainIDForUser() threw an SQLE: " + sqle);
//         }
//
//         finally {
//             db.release();
//         }
//
//         // If the domain ID is null, throw an exception
//         if (domainID == null) {
//             throw new SecurityException ("UserDomainManager.getDomainIDForUser(): No domain was found for userID: " + userID);
//         }
//
//         return domainID;
//     }
//
//
//
//     /**
//      * Return the ID of the UserDomain that the specified user belongs to.
//      *
//      * @param userID ID of the user in question
//      * @return UserDomain ID of the domain the user belongs to.
//      *
//      * @exception SecurityException
//      *                   Throws <code>SecurityException</code> if there is an SQLException or if there is no domain record found for the specified userID.
//      * @since Gecko
//      */
//     public static String getDomainIDForUsername (String username) throws SecurityException {
//
//         DBBean db = new DBBean();
//         String domainID = null;
//
//         try {
//             domainID = getDomainIDForUsername (username, db);
//         }
//
//         finally {
//             db.release();
//         }
//
//         return domainID;
//     }
//
//
//
//     /**
//      * Return the ID of the UserDomain that the specified user belongs to.
//      *
//      * @param username ID of the user in question
//      * @param db     Already instantiated database bean.
//      * @return UserDomain ID of the domain the user belongs to.
//      *
//      * @exception SecurityException
//      *                   Throws <code>SecurityException</code> if there is an SQLException or if there is no domain record found for the specified userID.
//      * @since Gecko
//      */
//     public static String getDomainIDForUsername (String username, DBBean db) throws SecurityException {
//
//         String qstrGetDomainIDForUser = "select domain_id from pn_user where username = " + username;
//         String domainID = null;
//
//         try {
//
//             db.executeQuery (qstrGetDomainIDForUser);
//
//             if ( db.result.next() )
//                 domainID = db.result.getString ("domain_id");
//         }
//
//         catch (SQLException sqle) {
//             throw new SecurityException ("UserDomainManager.getDomainIDForUser() threw an SQLE: " + sqle);
//         }
//
//         finally {
//             db.release();
//         }
//
//         // If the domain ID is null, throw an exception
//         if (domainID == null) {
//             throw new SecurityException ("UserDomainManager.getDomainIDForUser(): No domain was found for username: " + username);
//         }
//
//         return domainID;
//     }
//
//

    /**
     * Returns and HTML option list of all available domains for the specifed configuration
     * 
     * @param configurationID
     *               The id of the users current configuration
     * @return HTML Option List (string)
     * @since Gecko
     */
    public static String getDomainOptionList (String configurationID) {

        DBBean db = new DBBean();
        StringBuffer optionList = new StringBuffer();
        String qstrGetDomainList = "select d.domain_id, d.name " +
            " from pn_user_domain d, pn_user_domain_supports_config c " +
            " where c.configuration_id = ? and c.domain_id = d.domain_id and d.record_status = 'A'";

        try {

            db.prepareStatement (qstrGetDomainList);
            db.pstmt.setString (1, configurationID);
            db.executePrepared();

            while (db.result.next()) {

                optionList.append ("<option value=\"" + db.result.getString("domain_id") + "\">" +
                                   db.result.getString ("name") + "</option>");
            }
        }

        catch (SQLException sqle) {
            // do nothing
        }

        finally {
            db.release();
        
        }

        return optionList.toString();
    }

}
