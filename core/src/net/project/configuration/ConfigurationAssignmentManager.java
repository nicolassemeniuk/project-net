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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.configuration;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.User;

import org.apache.log4j.Logger;

/**
  * Accept / Reject configuration space assignments
  */
public class ConfigurationAssignmentManager extends net.project.resource.SpaceAssignmentManager  implements java.io.Serializable{

    /** Current user */
    private User user = null;

    private String configurationID = null;
    private String configurationName = null;
    private String configurationDesc = null;
    private String responsibilities = null;
    private boolean isLoaded = false;

    /**
      * Create new ConfigurationAssignmentManager
      */
    public ConfigurationAssignmentManager() {
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

    public void setConfigurationID(String objectID) {
        configurationID = objectID;
    }

    public String getConfigurationID() {
        return configurationID;
    }

    public String getConfigurationDescription() {
        return configurationDesc;
    }

    public void setConfigurationDescription(String description) {
        configurationDesc = description;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String name) {
        configurationName = name;
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
        storeInvitationResponse(this.configurationID, this.user.getID(), this.user.getEmail(), response);
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
        this.isLoaded = false;

        query.append("select configSpace.configuration_name, configSpace.configuration_desc, ");
        query.append("iu.invitee_responsibilities ");
        query.append("from pn_configuration_space configSpace, pn_invited_users iu ");
        query.append("where configSpace.configuration_id = ? ");
        query.append("and iu.space_id = configSpace.configuration_id ");
        query.append("and iu.person_id = ? ");

        DBBean db = new DBBean();
        try {
            db.prepareStatement(query.toString());
            int index = 0;
            db.pstmt.setString(++index, getConfigurationID());
            db.pstmt.setString(++index, getUser().getID());
            db.executePrepared();

            if (db.result.next()) {
                configurationName = db.result.getString("configuration_name");
                configurationDesc = db.result.getString("configuration_desc");
                responsibilities = db.result.getString("invitee_responsibilities");
                this.isLoaded = true;

            } else {
            	Logger.getLogger(ConfigurationAssignmentManager.class).info("ConfigurationAssignmentManager.load() failed to load row for id " + getConfigurationID());

            }

        } catch (SQLException sqle) {
        	Logger.getLogger(ConfigurationAssignmentManager.class).error("ConfigurationAssignmentManager.load() " +
                "threw an SQL exception: " + sqle);
            throw new PersistenceException ("Configuration assignment manager " +
                "load operation failed.", sqle);

        } finally {
            db.release();
        } //end try

    }
}
