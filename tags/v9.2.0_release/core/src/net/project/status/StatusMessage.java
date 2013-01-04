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

 package net.project.status;


import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Record used for holding information about a particular Status Message
 */
public class StatusMessage {
    /** Message ID. */
    private String m_messageId = null;
    /** The Message Title. */
    private String m_messageTitle = null;
    /** The Message Text. */
    private String m_messageText = null;
    /** The Message Status. */
    private String messageStatus = null;
    /** The time at which the message was created. */
    private String m_timeStamp = null;
    /** The Message status string. */
    private String messageStatusString = null;

    /**
     * Does nothing
     */
    public StatusMessage(){
        //do nothing
    }

    /**
     * Returns the Message ID.
     * 
     * @return The Message ID
     */
    public String getID(){
        return this.m_messageId;
    }

    /**
     * Sets the ID for Message
     * 
     * @param id     The Message ID
     */
    public void setID(String id){
        this.m_messageId = id;
    }
    
    public String getMessageText(){
        return this.m_messageText;
    }

    public void setMessageText(String text) {
        this.m_messageText = text;
    }

    public String getMessageTitle(){
        return this.m_messageTitle;
    }
    
    public void setMessageTitle(String title){
        this.m_messageTitle = title;
    }

    
    public String getMessageStatus(){
        return this.messageStatus;
    }

    public void setMessageStatus(String status){
        this.messageStatus = status;
    }

    public String getMessageStatusString(){

        if (this.messageStatus != null && this.messageStatus.equals("A")){
            this.messageStatusString = "Active";
        } else {
            this.messageStatusString = "InActive";
        }
        return this.messageStatusString;
    }

    public void setMessageStatusString(String statusString){
       this.messageStatusString = statusString;
    }

    public String getTimeStamp(){
        return this.m_timeStamp;
    }

    public void setTimeStamp(String timestamp){
        this.m_timeStamp = timestamp;
    }

     /**
         Converts the object to XML representation
         This method returns the object as XML text.
         @return XML representation of the object
     */
    public String getXML(){
        StringBuffer xml = new StringBuffer();

        xml.append("<?xml version=\"1.0\" ?>\n\n");
        xml.append(getXMLBody());
        //System.out.println( xml.toString());
        return xml.toString();

    } // end getXML()

      /**
     * return an XML description of the message
     * 
     * @return     xml string
     */
    public String getXMLBody(){
        StringBuffer sb = new StringBuffer();

        sb.append("<status_message>\n");
        sb.append("<message_id>" + XMLUtils.escape(m_messageId) + "</message_id>\n");
        sb.append("<message_title>" + XMLUtils.escape(m_messageTitle) + "</message_title>\n");
        sb.append("<message_status>" + XMLUtils.escape(getMessageStatusString()) + "</message_status>\n");
        sb.append("<message_text>" + XMLUtils.escape(m_messageText)+ "</message_text>\n");
        sb.append("<message_status>" + XMLUtils.escape(messageStatus) + "</message_status>\n");
        sb.append("<message_timestamp>" + XMLUtils.escape(m_timeStamp) + "</message_timestamp>\n");
        sb.append("</status_message>\n");
        
        return sb.toString();
    }


    /**
     * change the status of this method in the database.
     * For now- this is called in the "store" method.
     * 
     * @exception PersistenceException
     */
    public void changeStatus()
        throws PersistenceException {

        DBBean db = new DBBean();
        try {

            db.prepareCall ("{call MESSAGE.SET_STATUS (?,?,?)}");

            Logger.getLogger(StatusMessage.class).debug("m_messageId = " + m_messageId);
            db.cstmt.setInt(1, Integer.parseInt(m_messageId));
            db.cstmt.setString(2, messageStatus);
            db.cstmt.registerOutParameter(3, java.sql.Types.INTEGER);

            db.executeCallable();
        } catch (SQLException sqle)  {
        	Logger.getLogger(StatusMessage.class).debug("StatusMessage:changeStatus() " + m_messageId+ ", unable to execute stored procedure: " + sqle);
           throw new PersistenceException("Status Message Change Status Operation Failed!", sqle);
       } finally {
           db.release();
       } // end finally
          
    }

    /**
     * Create a new Message in the database. This is called by the store method if no message id exists.
     * 
     * @exception PersistenceException
     */
    public void create()
        throws PersistenceException {

        DBBean db = new DBBean();
        try {
            db.prepareCall ("{call MESSAGE.ADD_MESSAGE (?,?,?,?) }");
            
            db.cstmt.setString(1, this.getMessageTitle());
            db.cstmt.setString(2, this.getMessageText());
            db.cstmt.setString(3, getMessageStatus());
            db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);

            db.executeCallable();
        } catch (SQLException sqle)  {
        	Logger.getLogger(StatusMessage.class).debug("StatusMessage:create() " + m_messageId+ ", unable to execute stored procedure: " + sqle);
           throw new PersistenceException("Status Message Create Operation Failed!", sqle);
        } finally {
           db.release();
        } // end finally
    }

    /**
     * Store this Message in the Database
     * 
     * @exception PersistenceException
     */
    public void store()
        throws PersistenceException {

         // if there is a message id store the values, if not this must be a new message - go to create();
        
        messageStatus = messageStatus == null || messageStatus.trim().equals("") ? "I" : messageStatus ;
        
        if( m_messageId == null ){

            create() ;

        } else if (!m_messageId.equals("-1")) {
        
            int index = 0 ;

            DBBean db = new DBBean();
            try {
                db.prepareCall ("{ call MESSAGE.EDIT_MESSAGE (?,?,?,?) }");
    
                db.cstmt.setString(++index, m_messageId);
                db.cstmt.setString(++index, m_messageTitle);
                db.cstmt.setString(++index, m_messageText);
                
                db.cstmt.registerOutParameter(++index, java.sql.Types.INTEGER);
    
                db.executeCallable();
            } catch (SQLException sqle)  {
            	Logger.getLogger(StatusMessage.class).debug("StatusMessage:store() " + m_messageId+ ", unable to execute stored procedure: " + sqle);
                throw new PersistenceException("Status Message Store Operation Failed!", sqle);
            } finally {
                db.release();
            } // end finally

            //for now - Change the status in the DB whenver we change the other parameters
            
            changeStatus();

        } 
    }

    /**
     * Load this Message from the database.
     * 
     * @exception PersistenceException
     */
    public void load()
        throws PersistenceException {

        if (m_messageId == null)
            throw new NullPointerException("messageId is null.  Can't load.");

        
        String query = "select msg.message_id, msg.title, msg.message, msg.active_indicator, msg.timestamp "
                        + "from status_messages msg ";

        if (m_messageId != null)
            query += "where msg.message_id =" + m_messageId;

        DBBean db = new DBBean();
        try {
            db.prepareStatement(query);
            db.executePrepared();
            
            while (db.result.next()) {
                
               // Status Message properties
                m_messageId = db.result.getString( 1);
                m_messageTitle = db.result.getString(2);
                m_messageText = db.result.getString(3);
                messageStatus = db.result.getString(4);
                m_timeStamp = db.result.getString(5);

            }

        } catch (SQLException sqle){
        	Logger.getLogger(StatusMessage.class).debug("StatusMessage.load() threw an SQL exception: " + sqle);        
        } finally {
            db.release();
        }
    }

    /**
     * creates and returns a well-formatted form for editing the
     * entries of a Status Message. If this message is empty
     * (i.e. there is no message id), form will return with "null"
     * values in fields.
     * 
     * @return status message form
     */
    public String getEditForm() {
        StringBuffer sb = new StringBuffer();
        sb.append("<form method=\"post\" action=\"" + SessionManager.getAppURL() +"/support/admin/MOTDAdmin.jsp?action=doEDIT\"> "
        + " <table width=\"41%\" border=\"1\">"
        + "<tr>  "
        + "<td width=\"39%\"><b>Message Id:</b></td>"
        + "<td width=\"61%\"> "
        +  m_messageId
        + "</td>       "
        + "</tr>  "
        + "<tr> "
        + "<td width=\"39%\"><b>Message Title</b></td> "
        + "<td width=\"61%\"> "
        + "<input type=\"text\" name=\"mtitle\" value=\"" + m_messageTitle +"\"> "
        + "</td> "
        + "</tr> "
        + "<tr>  "
        + "<td width=\"39%\"><b>Message Text</b></td> "
        + "<td width=\"61%\"> "
        + "<textarea name=\"mtext\">" + m_messageText + "</textarea>  "
        + "</td>  "
        + "</tr>  "
        + " <tr>   "
        + "<td width=\"39%\"><b>Message Status</b></td> "
        + "<td width=\"61%\"> "
        + "<select name=\"mstatus\">"
        + " <option value=\"A\">A</option>" ) ;
        if (messageStatus.equals("I"))
        {
            sb.append("<option value=\"I\" SELECTED>I</option>"); 
        }
        else
        {
            sb.append("<option value=\"I\">I</option>");
        }
                
        sb.append("</select> "
        + "</td>  "
        + "</tr> "
        + "<tr> "
        + "<td width=\"39%\"> "
        + "<input type=\"submit\" name=\"Submit\" value=\"Submit\"> "
        + "</td>      "
        + " <td width=\"61%\">  "
        + "<input type=\"reset\" name=\"Reset\" value=\"Reset\">   "
        + "</td>  "
        + "</tr>"
        + "</table> "
        + "<input type=\"HIDDEN\" name=\"id\" value=\"" + m_messageId +"\"> "
        + "</form> ");

       return sb.toString();
    }

    /**
     * Remove this message from the database. This is
     * different from the clear() method
     * 
     * @exception PersistenceException
     */
    public void remove()
        throws PersistenceException {

        DBBean db = new DBBean();
        try {

            db.prepareCall ("{call MESSAGE.DELETE_MESSAGE (?,?)}");
            
            db.cstmt.setString(1, m_messageId);
            db.cstmt.registerOutParameter(2, java.sql.Types.INTEGER);

            db.executeCallable();
        } catch (SQLException sqle)  {
        	Logger.getLogger(StatusMessage.class).debug("StatusMessage:remove() " + m_messageId+ ", unable to execute stored procedure: " + sqle);
            throw new PersistenceException("Status Message Remove Operation Failed!", sqle);
        } finally {
            db.release();
        } // end finally
    }

    /**
     * Clear out the member member variables of this message.
     */
    public void clear(){

       m_messageId = null;
       m_messageTitle = null;
       m_messageText = null;        
       messageStatus = null;
       m_timeStamp = null;

    }

}
