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
|   $Revision: 20800 $
|       $Date: 2010-05-05 07:05:35 -0300 (mié, 05 may 2010) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.directory.nativedir;

import net.project.base.directory.AuthenticationFailedException;
import net.project.base.directory.DirectoryException;
import net.project.base.directory.TooManyEntriesException;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * The NativeDirectoryProvider provides authentication and directory
 * services based on the default database.
 */
public class NativeDirectoryProvider extends net.project.base.directory.DirectorySPI {
    
    protected void authenticate(boolean shadowLogin, boolean isFromSSO) throws AuthenticationFailedException, DirectoryException {
    	
    	boolean ssoEnabled = PropertyProvider.getBoolean("prm.global.login.sso.allowSSO");
        
        // Build query to look for user
        StringBuffer query = new StringBuffer();
        query.append("select u.user_id, u.user_status ");
        query.append("from pn_user_view u, pn_user_default_credentials udc ");
        query.append("where lower(u.username) = lower(?) and u.domain_id = ? ");
        //support for SSO auth or shadow login is performed then no need in password
        if ((shadowLogin || (ssoEnabled && isFromSSO)))
        	query.append("and udc.user_id = u.user_id");
        else
        	query.append("and udc.user_id = u.user_id and lower(udc.password) = lower(?) ");

        DBBean db = new DBBean();

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, getAuthenticationContext().getUsername());
            db.pstmt.setString(++index, getAuthenticationContext().getDomainID());
            if (!(shadowLogin || (ssoEnabled && isFromSSO)))
            db.pstmt.setString(++index, encryptPassword(getAuthenticationContext().getClearTextPassword()));

            db.executePrepared();

            if (db.result.next()) {

                // Check for duplicate user
                // This would be another entry with the same username, same password
                // and same domain.  This is a data error; it can only occur if
                // with erroneous data (such as two usernames differing by case only)
                if(db.result.next()){
                    throw new TooManyEntriesException("Error authenticating user; too many entries found");
                }
            
            } else {
                // No row found
                throw new AuthenticationFailedException("Invalid username or password");

            }

            
        } catch (java.sql.SQLException sqle) {
        	Logger.getLogger(NativeDirectoryProvider.class).error("NativeDirectoryProvider.authenticate() throw an SQLException: " + sqle);     
            throw new DirectoryException("Authentication Failed: " + sqle, sqle);

        } catch (net.project.security.InvalidPasswordForEncryptionException e) {
            // This error occurs when the entered password contains characters
            // that cannot be encrypted; it will have been impossible to create
            // a password with these characters, so the password is implicitly
            // invalid
            // Currently, our password encryption mechanism supports only a subset
            // of ASCII
            throw new AuthenticationFailedException("Invalid username or password");

        } catch (net.project.security.EncryptionException e) {
            throw new DirectoryException("Authentication Failed: " + e, e);
        
        } finally {
            db.release();

        }
       
    }
    
    protected void authenticate() throws AuthenticationFailedException, DirectoryException {
    	authenticate(false, false);
    }

    /**
     * Returns the directory entry from the Native directory for the
     * user represented by the current authentication context.
     * @throws AuthenticationFailedException if there is a problem authenticating
     * due to invalid username or password
     * @throws DirectoryException if there is a problem authenticating,
     * for example unable to contact the database
     */    
    protected net.project.base.directory.IDirectoryEntry getAuthenticatedDirectoryEntry() 
            throws AuthenticationFailedException, DirectoryException {

        // Try authenticating
        authenticate();

        // If we reach this point, authentication was successful
        // Grab the directory entry
        return getDirectoryEntry(getAuthenticationContext().getUsername());

    }


    /**
     * Returns the directory entry from the Native directory for the
     * user with the specified username.
     * @throws DirectoryException if there is a problem fetching the
     * entry, for example the user is not found or there is a problem
     * contacting the database
     */    
    protected net.project.base.directory.IDirectoryEntry getDirectoryEntry(String username) 
            throws DirectoryException {

        
        StringBuffer query = new StringBuffer();
        query.append("select u.user_id, u.username, udc.password, ");
        query.append("udc.jog_phrase, udc.jog_answer ");
        query.append("from pn_user u, pn_user_default_credentials udc ");
        query.append("where u.domain_id = ? and u.user_id = udc.user_id ");
        query.append("and lower(u.username) = lower(?)  ");

        DBBean db = new DBBean();
        NativeDirectoryEntry entry = new NativeDirectoryEntry();

        try {
            int index = 0;
            db.prepareStatement(query.toString());

            db.pstmt.setString(++index , this.getConfiguration().getDomainID());
            db.pstmt.setString(++index, username);

            db.executePrepared();

            if (db.result.next()) {
                // Populate directory entry
                entry.setLogin(db.result.getString("username"));
                entry.setEncryptedPassword(db.result.getString("password"));
                entry.setEncryptedHintPhrase(db.result.getString("jog_phrase"));
                entry.setEncryptedHintAnswer(db.result.getString("jog_answer"));

            } else {
                // No rows found for username
                throw new DirectoryException("Directory entry not found");

            }
            
        } catch (java.sql.SQLException sqle) {
        	Logger.getLogger(NativeDirectoryProvider.class).error("NativeDirectoryProvider.getDirectoryEntry() throw an SQLException: " + sqle);
            throw new DirectoryException("Get Directory Entry operation failed: " + sqle, sqle);
        
        
        } finally {
            db.release();

        }
        
        return entry;
    }

    /**
     * Encrypts the specified password using our default password
     * encryption mechanism.
     * @param clearTextPassword the password to encrypt
     * @return the encrypted password
     * @throws net.project.security.EncryptionException if there is a problem
     * encrypting
     */
    private String encryptPassword(String clearTextPassword) 
            throws net.project.security.EncryptionException {
        
        return net.project.security.EncryptionManager.pbeEncrypt(clearTextPassword);
    }

    /**
     * Updates the user in this directory.
     * This updates the password, jog phrase and jog answer.
     * @param user the user to update
     * @param directoryEntry the directory information about the user
     * including password, jog phrase and jog answer
     * @throws DirectoryException if there is a problem updating
     */
    protected void updateUser(net.project.security.User user, net.project.base.directory.IDirectoryEntry directoryEntry) 
        throws DirectoryException {
       
        DBBean db = new DBBean();

        try {
            updateUser(user, directoryEntry, db);
        } finally {
            db.release();
        }
    }
    
    /**
     * Updates the user in this directory.
     * This updates the password, jog phrase and jog answer.
     * @param user the user to update
     * @param directoryEntry the directory information about the user
     * including password, jog phrase and jog answer
     * @throws DirectoryException if there is a problem updating
     */
    protected void updateUser(net.project.security.User user, net.project.base.directory.IDirectoryEntry directoryEntry , DBBean db) 
            throws DirectoryException {

        NativeDirectoryEntry entry = (NativeDirectoryEntry) directoryEntry;

        try {

            StringBuffer query = new StringBuffer();
            java.util.List bindVariables = new java.util.ArrayList();

            if (isExisting(user , db)) {
                query.append("update pn_user_default_credentials ");
                query.append("set password = ?, jog_phrase = ?, jog_answer = ? ");
                query.append("where user_id = ?  and domain_id = ?" );
                

                bindVariables.add(entry.getEncryptedPassword());
                bindVariables.add(entry.getEncryptedHintPhrase());
                bindVariables.add(entry.getEncryptedHintAnswer());
                bindVariables.add(user.getID());
                bindVariables.add(user.getUserDomainID());
            
            } else {
                query.append("insert into pn_user_default_credentials ");
                query.append("(user_id, password, jog_phrase, jog_answer , domain_id) ");
                query.append("values (?, ?, ?, ? , ?) ");
            
                bindVariables.add(user.getID());
                
                if(directoryEntry != null) {
	                bindVariables.add(entry.getEncryptedPassword());
	                bindVariables.add(entry.getEncryptedHintPhrase());
	                bindVariables.add(entry.getEncryptedHintAnswer());
                } else {
               	 bindVariables.add("-");
	                bindVariables.add("-");
	                bindVariables.add("-");
                }
                
                bindVariables.add(user.getUserDomainID());
    
            }


            int index = 0;
            db.prepareStatement(query.toString());

            for (java.util.Iterator it = bindVariables.iterator(); it.hasNext(); ) {
                db.pstmt.setString(++index, (String) it.next());
            }

            db.executePrepared();

        } catch (java.sql.SQLException sqle) {
            throw new DirectoryException("Native directory update user operation failed: " + sqle, sqle);
        
        } catch (PersistenceException pe) {
            throw new DirectoryException("Native directory update user operation failed: " + pe, pe);
        }

    }

    /**
     * Removes the user from this directory.
     * This removes the jog question, answer, password etc.
     * @param user the user to remove
     * @param db the database bean
     * @throws DirectoryException if there is a problem removing the user
     */
    protected void removeUser(net.project.security.User user , DBBean db)
            throws DirectoryException {

        try {

            StringBuffer query = new StringBuffer();
            query.append("delete from pn_user_default_credentials where user_id = ? and domain_id = ?");

            int index = 0;

            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, user.getID());
            db.pstmt.setString(++index , user.getUserDomainID());

            db.executePrepared();

        } catch (java.sql.SQLException sqle) {
            throw new DirectoryException("Native directory remove user operation failed: " + sqle, sqle);
        
        }

    }

    /**
     * Removes the user from this directory.
     * This removes the jog question, answer, password etc.
     * @param user the user to remove
     * @throws DirectoryException if there is a problem removing the user
     */
    protected void removeUser(net.project.security.User user )
            throws DirectoryException {

        DBBean db = new DBBean();

        try {
            removeUser(user , db);
        } finally {
            db.release();
        }
    }

    /**
     * Indicates whether the specified user already exists in
     * this directory.
     * @param user the user to check
     * @return true if the user is already stored within this
     * directory
     * @throws PersistenceException if there is a problem checking
     * the user
     */
    private boolean isExisting(net.project.security.User user , DBBean  db) 
            throws PersistenceException {

        boolean isExisting = false;

        StringBuffer query = new StringBuffer();
        query.append("select 1 from pn_user_default_credentials udc ");
        query.append("where udc.user_id = ? and udc.domain_id = ? ");

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, user.getID());
            db.pstmt.setString(++index , user.getUserDomainID());

            db.executePrepared();

            if (db.result.next()) {
                isExisting = true;
            }
        
        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("User check operation failed: " + sqle, sqle);

        } 

        return isExisting;
    }


    /**
     * Indicates whether this Native directory allows searching.
     * Native directories do NOT provide searching, since it doesn't
     * actually maintain any useful information; it relies on
     * application person directory to maintain that kind of info.
     * @return false always
     */
    public boolean isSearchableForInvitation() {
        return false;
    }

    /**
     * Returns the SearchableDirectory for this Native directory.
     * @return null always since Native directories do NOT provide
     * searching
     */
    public net.project.base.directory.search.ISearchableDirectory getSearchableDirectory() {
        return null;
    }

}
