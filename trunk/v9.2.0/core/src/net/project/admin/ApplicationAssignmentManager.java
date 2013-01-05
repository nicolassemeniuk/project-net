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

 package net.project.admin;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.SpaceAssignmentManager;
import net.project.security.User;

import org.apache.log4j.Logger;


/**
  * Accept / Reject application space assignments
  */
public class ApplicationAssignmentManager extends SpaceAssignmentManager  implements java.io.Serializable {
    
    /** Current user */
    private User user = null;
    
    private String applicationID = null;
    private String applicationName = null;
    private String applicationDesc = null;
    private String responsibilities = null;
    private boolean isLoaded = false;

    /**
      * Create new ApplicationAssignmentManager
      */
    public ApplicationAssignmentManager() {
    }

    /**
      * Sets current user
      * @param user the current user
      */
    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setApplicationID(String objectID) {
        applicationID = objectID;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public String getApplicationDescription() {
        return applicationDesc;
    }

    public void setApplicationDescription(String description) {
        applicationDesc = description;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String name) {
        applicationName = name;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }


    /**
    * Stores invitation response to space and takes appropriate actions
    * @param response the user's response
    */
    public void storeInvitationResponse(String response) throws PersistenceException {
        storeInvitationResponse(this.applicationID, this.user.getID(), this.user.getEmail(), response);
    }

   
    /** is an object descrption loaded */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
      * Load the description for the assignment.
      * Used when showing an assignment to a user.
      */
    public void load() throws PersistenceException {
        StringBuffer query = new StringBuffer();
        
        query.append("select appSpace.application_name, appSpace.application_desc, ");
        query.append("iu.invitee_responsibilities ");
        query.append("from pn_application_space appSpace, pn_invited_users iu ");
        query.append("where appSpace.application_id = ? ");
        query.append("and iu.space_id = appSpace.application_id ");
        query.append("and iu.person_id = ? ");

        DBBean db = new DBBean();
        try {
            db.prepareStatement(query.toString());
            int index = 0;
            db.pstmt.setString(++index, getApplicationID());
            db.pstmt.setString(++index, getUser().getID());
            db.executePrepared();
            
            if(db.result.next()) {
                applicationName = db.result.getString("application_name");
                applicationDesc = db.result.getString("application_desc");
                responsibilities = db.result.getString("invitee_responsibilities");
                this.isLoaded = true;
            
            } else {
                // Not finding an assignment could be due to a number of reasons
                // Someone may be trying to access an assignment that is not
                // theirs
                this.isLoaded = false;
                Logger.getLogger(ApplicationAssignmentManager.class).info("ApplicationAssignmentManager.load() failed to load row for id " + getApplicationID());
             }

         } catch (SQLException sqle) {
        	 
        	 Logger.getLogger(ApplicationAssignmentManager.class).error("ApplicationAssignmentManager.load() threw an SQL exception: " + sqle);
             throw new PersistenceException ("Application assignment manager load operation failed.", sqle);

         } finally {
             db.release();

         } //end try

    }

}
