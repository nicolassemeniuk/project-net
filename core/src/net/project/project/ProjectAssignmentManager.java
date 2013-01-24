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
|
+--------------------------------------------------------------------------------------*/

package net.project.project;

import java.sql.SQLException;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.User;

/**  
    This object will generate a description for the object type specified
*/
public class ProjectAssignmentManager extends net.project.resource.SpaceAssignmentManager {
    // user needing object description
    protected User m_user = null;

    protected String m_projectID = null;
    protected String m_description = null;
    protected String m_percentComplete = null;
    protected String m_responsibilities = null;
    protected String m_projectName = null;
    protected String m_status = null;
    protected String m_subProject = null;
    protected java.util.Date m_startdate = null;
    protected java.util.Date m_enddate = null;
    protected boolean m_isLoaded = false;
    
    private int invitationActionStatus = 0;

    // db access bean    
    protected DBBean m_db = new DBBean();


    // constructor
    public ProjectAssignmentManager() {
        // do nothing
    }

    public void setUser(User user) {
        m_user = user;
    }

    public User getUser() {
        return m_user;
    }

    public void setProjectID(String objectID) {
        m_projectID = objectID;
    }

    public String getProjectID() {
        return m_projectID;
    }

    public String getProjectDescription() {
        return m_description;
    }

    public void setProjectDescription(String description) {
        m_description = description;
    }

    public String getProjectName() {
        return m_projectName;
    }

    public void setProjecName(String name) {
        m_projectName = name;
    }

    public java.util.Date getStartDate() {
        return m_startdate;
    }

    public void setStartDate(java.util.Date startdate) {
        m_startdate = startdate;
    }

    public java.util.Date getEndDate() {
        return m_enddate;
    }

    public void setEndDate(java.util.Date enddate) {
        m_enddate = enddate;
    }

    public String getStatus() {
        return m_status;
    }

    public void setStatus(String status) {
        m_status = status;
    }

    public String getPercentComplete() {
        return m_percentComplete;
    }

    public void setPercentComplete(String percent) {
        m_percentComplete = percent;
    }    

    public String getSubProject() {
        return m_subProject;
    }

    public void setSubProject(String subproject) {
        m_subProject = subproject;
    }

    public String getResponsibilities() {
        return m_responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        m_responsibilities = responsibilities;
    }


    /**
    * Stores invitation response to space and takes appropriate actions
    * @param response the user's response
    */
    public void storeInvitationResponse(String response) throws PersistenceException {
        storeInvitationResponse(m_projectID, m_user.getID(), response);
    }

    /***************************************************************************************
    *  Implementing IJDBCPersistence
    ***************************************************************************************/

    /** is an object descrption loaded */
    public boolean isLoaded() {
        return m_isLoaded;
    }

    /**
     * Returns whether the Invitation or assignment has been acted upon or not  
     * 
     * @return boolean
     */
    public boolean hasInvitationActedUpon() {
        if (this.invitationActionStatus == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
    Load the description for an object
    */
    public void load() throws PersistenceException
    {
        String query =  "select pv.project_name, pv.project_desc, pv.percent_complete,"+
                        " pv.status, pv.is_subproject, pv.start_date, iu.invitee_responsibilities,"+
                        " pv.end_date, pv.project_logo_id , iu.invitation_acted_upon " +
                        " from pn_project_view pv, pn_invited_users iu" + 
                        " where pv.project_id = iu.space_id and iu.space_id = " + m_projectID + 
                        " and iu.person_id = '" + m_user.getID() + "'";
        try {
            m_db.setQuery(query);
            m_db.executeQuery();
            if (m_db.result.next()) {
                m_projectName = m_db.result.getString("project_name");
                m_description = m_db.result.getString("project_desc");
                m_percentComplete = m_db.result.getString("percent_complete");
                m_status = PropertyProvider.get(m_db.result.getString("status"));
                m_subProject = m_db.result.getString("is_subproject");
                m_startdate = m_db.result.getDate("start_date");
                m_enddate = m_db.result.getDate("end_date");
                m_responsibilities = m_db.result.getString("invitee_responsibilities");
               this.invitationActionStatus = m_db.result.getInt("invitation_acted_upon");
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw new PersistenceException("Unable to load project manager", sqle);
        } finally {
            m_db.release();
        }
    }
}
