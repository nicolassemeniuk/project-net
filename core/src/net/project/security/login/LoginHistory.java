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

 package net.project.security.login;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.User;

import org.apache.log4j.Logger;

/**
 * Class that encapsulates access to the login history of a user.
 *
 * @author Matthew Flower (12/04/2001)
 * @since Gecko
 */
public class LoginHistory {
    /** All login histories are associated with a user. **/
    private User user = null;


    /**
     * Creates a new LoginHistory class, initializing the login date.
     */
    public LoginHistory() {
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    /**
     * Store an entry in the database indicating that the user has visited the
     * PRM application.  The date stored in the database will be based on the
     * current time stored in the database.
     *
     * @throws PersistenceException when an error occurs saving the login
     * history into the database.
     */
    public void logVisit() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            logVisit(db);
        } catch (SQLException sqle) {
        	Logger.getLogger(LoginHistory.class).error("Unable to log user login in User.logVisit(): "+
                    sqle + "  Error Code: " + sqle.getErrorCode());
            throw new PersistenceException("Unable to log user login in User.logVisit. "+sqle.toString(), sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Store an entry in the database indicating that the user has visited the
     * PRM application.  The date stored in the database will be based on the
     * current time stored in the database.
     *
     * @param db a <code>DBBean</code> object guaranteed not to be committed or
     * rolled back.
     * @throws SQLException when an error occurs saving the login
     * history into the database.
     */
    public void logVisit(DBBean db) throws SQLException {

        Timestamp loginTime = new Timestamp(new Date().getTime());
        SecureRandom random;

        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (java.security.NoSuchAlgorithmException e) {
            // Runtime exception since this method never previously declared one
            throw new IllegalStateException("Error generating random string: " + e);
        }

        long concurrency = random.nextLong();

        storeLoginHistory(db, loginTime, concurrency);

        //PCD (11/23/04)
        //
        // As the size of the login history table grows, its utility as a source
        // for "last login" information becomes less useful -- primarily because the only
        // way to get meaningful information is to use the "max" function on the results returned
        // by this table.
        // As such, we will add an additional "last login" entry to the pn_user table to
        // denormalize typical access to this information, while retaining the running history
        // of user logins.

        updateUserRecord(db, loginTime);

    }


    /**
     * Adds a denormalized entry for last login to the pn_user table
     * @param db
     * @throws SQLException
     */
    private void updateUserRecord (DBBean db, Timestamp loginTime) throws SQLException {

        StringBuffer update = new StringBuffer();

        update.append ("update pn_user ");
        update.append ("set last_login = ? ");
        update.append ("where user_id = ?");

        db.prepareStatement(update.toString());
        db.pstmt.setTimestamp(1, loginTime);
        db.pstmt.setString(2, user.getID());

        db.executePrepared();
    }

    private void storeLoginHistory(DBBean db, Timestamp loginTime, long concurrency) throws SQLException {

        StringBuffer insertStatement = new StringBuffer();

        insertStatement.append("insert into pn_login_history ");
        insertStatement.append("    (person_id, login_date, login_name_used, login_concurrency) ");
        insertStatement.append("values (?, ?, ?, ?) ");

        db.prepareStatement(insertStatement.toString());
        db.pstmt.setString(1, user.getID());
        db.pstmt.setTimestamp(2, loginTime);
        db.pstmt.setString(3, user.getLogin());
        db.pstmt.setLong(4, concurrency);

        db.executePrepared();
    }

    /**
     * Store an entry in the database indicating that the user has visited the
     * PRM application.  This is a convenience method that calls {@link #logVisit}
     * and eliminates the need to construct an instance of LoginHistory in order
     * to log a visit.
     *
     * @param user - the User to be associated with the login history.
     * @throws PersistenceException if an error occurs while trying to record
     * the login history.
     */
    public static void logVisit(User user) throws PersistenceException {
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setUser(user);
        loginHistory.logVisit();
    }
}
