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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.license;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

/**
 * Provides a reason why a license was disabled through the interface.  This
 * message is shown to the user if they attempt to log in with an invalid
 * license.
 *
 * @author Vishwajeet Lohakarey
 * @since Gecko Update 3
 */
public class DisabledReason {
    /** Reason Code for this reason. */
    private String reasonCode = null;
    /** Short nameReason for this reason. */
    private String shortName = null;
    /** Message for this reason. */
    private String message = null;
    /** Standard message for this reason if no reason code has been provided. */
    private String standardMessage = "This license has been disabled but no reason code has been provided.\n";

    /**
     * Creates a new instance of <code>DisabledReason</code>.
     */
    public DisabledReason() {
    }

    /**
     * Creates a new instance of <code>DisabledReason</code>, loading an existing
     * code from the database based on the reason code passed through the
     * <code>reasonCode</code> parameter.
     *
     * @param reasonCode the code for this disabled reason.
     * @throws PersistenceException if there is an error loading the
     * <code>DisabledReason</code> from the database.
     */
    public DisabledReason(String reasonCode) throws PersistenceException {
        this.reasonCode = reasonCode;
        if (reasonCode != null) {
            this.load();
        } else {
            this.message = this.standardMessage;
        }
    }

    /**
     * Sets the reason code for this disabled reason.
     * @param reasonCode a <code>String</code> value containing a reason code.
     */
    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    /**
     * Gets the reason code for this disabled reason.
     * @return String, reason code.
     */
    public String getReasonCode() {
        return this.reasonCode;
    }

    /**
     * Sets the short name for this disabled reason.
     * @param shortName a <code>String</code> short name for this disabled reason.
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the short name for this disabled reason.
     * @return String, short name.
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * Sets the message for this disabled reason.
     *
     * @param message a <code>String</code> containing the short name for this
     * disabled reason.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the message for this disabled reason.
     *
     * @return String, reason message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Gets the disabled reason message along with it's code.
     *
     * @return reason message with reason code.
     */
    public String getMessageWithCode() {
        return ("Reason Code - " + this.reasonCode + " :<br>" + this.message);
    }

    /**
     * Loads a DisabledReason based on it's reason_code.
     *
     * @throws PersistenceException if there is a problem loading the reason
     * message.
     */
    private void load() throws PersistenceException {

        if (reasonCode == null) {
            this.message = this.standardMessage;
            return;
        }
        DBBean db = new DBBean();
        String query = "SELECT reason_code, short_name, message FROM pn_license_disabled_reasons " +
            " where reason_code = ?";
        try {
            db.prepareStatement(query);
            db.pstmt.setString(1, this.reasonCode);
            db.executePrepared();

            while (db.result.next()) {
                this.setShortName(db.result.getString("short_name"));
                this.setMessage(db.result.getString("message"));
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("DisabledReason load operation failed: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

    /**
     * This method gets a list of HTML &lt;option> tags for each type of disable
     * reason that already exists in the database.
     *
     * @return a <code>String</code> of HTML &lt;option> values for the existing
     * disable reasons.
     * @throws PersistenceException if an error occurs while loading the disable
     * codes from the database.
     */
    public static String getReasonOptionList() throws PersistenceException {
        DBBean db = new DBBean();
        StringBuffer sb = new StringBuffer();
        String query = "SELECT reason_code, short_name, message FROM pn_license_disabled_reasons ";

        try {
            db.prepareStatement(query);
            db.executePrepared();

            while (db.result.next()) {
                sb.append("<OPTION VALUE='" + db.result.getString("reason_code") + "' >");
                sb.append(db.result.getString("reason_code"));
                sb.append("-");
                sb.append(db.result.getString("short_name"));
                sb.append("</OPTION>");
                //db.result.getString("message");
            }
            return sb.toString();
        } catch (SQLException sqle) {
            throw new PersistenceException("DisabledReason load operation failed: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Store the current <code>DisabledReason</code> object in the database.
     *
     * @throws PersistenceException if an error occurs while saving the
     * <code>DisabledReason</code>.
     */
    public void store() throws PersistenceException {
        DBBean db = new DBBean();
        StringBuffer sb = new StringBuffer();
        int reasonCode = 0;
        int index = 0;
        String query = "SELECT max(reason_code) as reason_code FROM pn_license_disabled_reasons ";
        String insertQuery = "INSERT INTO pn_license_disabled_reasons (reason_code, short_name, message) VALUES (?,?,?)";
        try {
            db.prepareStatement(query);
            db.executePrepared();

            while (db.result.next()) {
                reasonCode = db.result.getInt("reason_code") + 1;
            }
            db.prepareStatement(insertQuery);
            db.pstmt.setString(++index, String.valueOf(reasonCode));
            db.pstmt.setString(++index, this.shortName);
            db.pstmt.setString(++index, this.message);
            db.executePrepared();

        } catch (SQLException sqle) {
            throw new PersistenceException("DisabledReason store operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }
    }
}
