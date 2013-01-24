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

public class StatusReason {
    /**
    * Reason Code for this reason.
    */
    private int reasonCode = -1;
    /**
    * Short nameReason for this reason.
    */
    private String shortName = null;
    /**
    * Message for this reason.
    */
    private String message = null;
    /**
    * Standard message for this reason if no reason code has been provided.
    */
    private String standardMessage = "This license has been disabled but no reason code has been provided.\n";

    /**
    * Constructor 
    */
    public StatusReason() {
    }

    /**
    * Constructor.
    * @param reasonCode the code for this disabled reason. 
    */
    public StatusReason(int reasonCode) 
	throws PersistenceException {
        this.reasonCode = reasonCode;
	if(reasonCode != -1){
	    this.load();
	}else {
            this.message = this.standardMessage;
	}
	
    }
    /**
    * Sets the reason code for this disabled reason.
    * @param String reason code.
    */
    public void setReasonCode(int reasonCode) {
	this.reasonCode = reasonCode;
    }
    /**
    * Gets the reason code for this disabled reason.
    * @return String, reason code.
    */
    public int getReasonCode() {
	return this.reasonCode;
    }

    /**
    * Sets the short name for this disabled reason.
    * @param String short name for this disabled reason.
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
    * @param String short name for this disabled reason.
    */
    public void setMessage(String message) {
	this.message = message;
    }

    /**
    * Gets the message for this disabled reason.
    * @return String, reason message.
    */
    public String getMessage() {
	return this.message;
    }
    /**
    * Gets the disabled reason message along with it's code.
    * @return reason message with reason code.
    */    
    public String getMessageWithCode() {
	return (" Reason Code - " + this.reasonCode + " :<br>" + this.message);
    }

    /**
     * Loads a DisabledReason based on it's reason_code.
     * @throws PersistenceException if there is a problem loading the reason message.
     */
    private void load()
	throws PersistenceException {
	
	if(reasonCode == -1) {
	    this.message = this.standardMessage;    
	    return;
	}
	DBBean db = new DBBean();
	String query = "SELECT reason_code, short_name, message FROM pn_license_status_reasons " +
	    " where reason_code = ?";
	try{
	
	    db.prepareStatement(query);
	    db.pstmt.setInt(1, this.reasonCode);
	    db.executePrepared();
    
	    while(db.result.next()){
		this.setShortName(db.result.getString("short_name"));
		this.setMessage(db.result.getString("message"));
	    }

	} catch (SQLException sqle) {
            throw new PersistenceException("StatusReason load operation failed: " + sqle, sqle);

        } finally {
            db.release();
        
        }

    }

    public static String getReasonOptionList() 
	throws PersistenceException {
	DBBean db = new DBBean();
	StringBuffer sb = new StringBuffer();
	String query = "SELECT reason_code, short_name, message FROM pn_license_status_reasons " ;
	   
	try{
	
	    db.prepareStatement(query);
	    db.executePrepared();
    
	    while(db.result.next()){
		sb.append("<OPTION VALUE='" + db.result.getString("reason_code") + "' >");
		sb.append(db.result.getString("reason_code"));
		sb.append("-");
		sb.append(db.result.getString("short_name"));
		sb.append("</OPTION>");
		//db.result.getString("message");
	    }
	    return sb.toString();

	} catch (SQLException sqle) {
            throw new PersistenceException("getReasonOptionList operation failed: " + sqle, sqle);

        } finally {
            db.release();
        
        }
    
    }

    public void store() 
	throws PersistenceException {
	DBBean db = new DBBean();
	StringBuffer sb = new StringBuffer();
	int reasonCode = 0;
	int index = 0;
	String query = "SELECT max(reason_code) as reason_code FROM pn_license_status_reasons " ;
	String insertQuery = "INSERT INTO pn_license_status_reasons (reason_code, short_name, message) VALUES (?,?,?)";   
	try{
            db.prepareStatement(query);
	    db.executePrepared();
    
	    while(db.result.next()){
		reasonCode = db.result.getInt("reason_code") + 1;
		this.setReasonCode(reasonCode);
	    }
	     db.prepareStatement(insertQuery);
	     db.pstmt.setString(++index, String.valueOf(reasonCode));
	     db.pstmt.setString(++index, this.shortName);
	     db.pstmt.setString(++index, this.message);
	     db.executePrepared();

	} catch (SQLException sqle) {
            throw new PersistenceException("StatusReason store operation failed: " + sqle, sqle);

        } finally {
            db.release();
        
        }
    }

    
}
