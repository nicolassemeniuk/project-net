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

 package net.project.business;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.SpaceAssignmentManager;
import net.project.security.User;

/**
 * This object will generate a description for the object type specified.
 */
public class BusinessAssignmentManager extends SpaceAssignmentManager {
    // user needing object description
    protected User m_user = null;

    protected String m_businessID = null;
    protected String m_description = null;
    protected String m_responsibilities = null;
    protected String m_businessName = null;
    protected boolean m_isLoaded = false;
    private int invitationActionStatus = 0;

    // constructor
    public BusinessAssignmentManager() {
        // do nothing
    }

    public void setUser(User user) {
        m_user = user;
    }

    public User getUser() {
        return m_user;
    }

    public void setBusinessID(String objectID) {
        m_businessID = objectID;
    }

    public String getBusinessID() {
        return m_businessID;
    }

    public String getBusinessDescription() {
        return m_description;
    }

    public void setBusinessDescription(String description) {
        m_description = description;
    }

    public String getBusinessName() {
        return m_businessName;
    }

    public void setBusinessName(String name) {
        m_businessName = name;
    }

    public String getResponsibilities() {
        return m_responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        m_responsibilities = responsibilities;
    }

    /**
     * Stores invitation response to space and takes appropriate actions
     *
     * @param response the user's response
     */
    public void storeInvitationResponse(String response) throws PersistenceException {
        storeInvitationResponse(m_businessID, m_user.getID(), response);
    }


    /***************************************************************************************
     *  Implementing IJDBCPersistence
     ***************************************************************************************/

    /**
     * is an object descrption loaded
     */
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
     * Load the description for an object
     */
    public void load() {

        StringBuffer query = new StringBuffer();

        query.append("select b.business_name, b.business_desc,");
        query.append(" iu.invitee_responsibilities, b.logo_image_id , iu.invitation_acted_upon");
        query.append(" from pn_business b, pn_business_space bs, pn_invited_users iu");
        query.append(" where b.business_id = bs.business_id ");
        query.append(" and bs.business_space_id = iu.space_id");
        query.append(" and iu.space_id = ? ");
        query.append(" and iu.person_id = ? ");

        DBBean db = new DBBean();
        try {
            int index = 0;

            db.prepareStatement(query.toString());

            db.pstmt.setString(++index, m_businessID);
            db.pstmt.setString(++index, this.m_user.getID());

            db.executePrepared();

            if (db.result.next()) {
                m_businessName = db.result.getString("business_name");
                m_description = db.result.getString("business_desc");
                m_responsibilities = db.result.getString("invitee_responsibilities");
                this.invitationActionStatus = db.result.getInt("invitation_acted_upon");
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            db.release();
        }
    }
}
