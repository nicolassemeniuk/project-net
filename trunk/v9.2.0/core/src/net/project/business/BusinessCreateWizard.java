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
|   $Revision: 19147 $
|       $Date: 2009-05-02 08:14:35 -0300 (sáb, 02 may 2009) $
|     $Author: vivana $
|
|
+----------------------------------------------------------------------*/
package net.project.business;

import java.sql.SQLException;
import java.sql.Types;

import net.project.base.PnetException;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.form.FormManager;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.space.Space;
import net.project.space.SpaceManager;
import net.project.space.SpaceRelationship;

/** 
 *  Provides methods to create a BusinessSpace.
 */
public class BusinessCreateWizard extends BusinessSpace {

    private User user = null;
    private Space relatedSpace;
    private SpaceRelationship relationship;
    private boolean masterBusiness = false;

    /**
     * Specifies a space that is related to this space.
     * @param relatedSpace the space related to this one
     */
    public void setRelatedSpace(Space relatedSpace) {
        this.relatedSpace = relatedSpace;
    }

    /**
     * Sets the relationship between this space and its related space.
     * @param relationship the relationship
     */
    public void setRelationship(SpaceRelationship relationship) {
        this.relationship = relationship;
    }

    /**
     * Sets the user context, the person who is creating the business.
     */
    public void setUser(User user)  { 
        this.user = user;
        m_address.setUser(user);
    }

    /**
     * Stores the business to the database.
     * Updates the ID to be the ID of the stored business when a business
     * is created.
     * @throws NullPointerException if the current user is null
     */
    public void store() throws PersistenceException  {

        if (this.user == null) {
            throw new NullPointerException("BusinessCreateWizard.store() must have a USER set to execute: ");
        }

        DBBean db = new DBBean();

        try {
            db.setAutoCommit(false);

            // Store the address
            m_address.store(db);

            // then create the business
            createBusiness(db);

            //create a relationship between this business and another space
            if ((relatedSpace != null) && (relationship != null)) {
                SpaceManager.addRelationship(db, relatedSpace, this, relationship);
            }

            //invite the business owner to the businees
            inviteOwner(db);

            db.commit();

            //clear out for new business
            clear();

        } catch (SQLException e) {
            try {
                db.rollback();
            } catch (SQLException ignored) {
                // Fail with original error
            }
            throw new PersistenceException("Error storing business or address: " + e, e);

        } finally {
            db.release();
        }

    }

  

    /**
     * Creates the business in the database.
     * Assumes that the address has been stored already.
     * Performs no commit or rollback.
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if there is a problem storing
     */
    private void createBusiness(DBBean db) throws SQLException {

        int index = 0;
        db.prepareCall("{call BUSINESS.CREATE_BUSINESS(?,?,?,?,?,?,?,?,?,?,?)}");
        db.cstmt.setString(++index, this.user.getID());
        db.cstmt.setString(++index, getName());
        db.cstmt.setString(++index, getDescription());
        db.cstmt.setString(++index, getFlavor());
        db.cstmt.setString(++index, null);  // logo id
        db.cstmt.setString(++index, getAddress().getID());

        if (masterBusiness) {
            db.cstmt.setString(++index,"1");
        } else {
            db.cstmt.setString(++index,"0");
        }

        db.cstmt.setString(++index,getBusinessCategoryID());
        db.cstmt.setNull(++index, Types.VARCHAR);
        db.cstmt.setNull(++index, Types.VARCHAR);
        int businessIDIndex;
        db.cstmt.registerOutParameter((businessIDIndex = ++index), java.sql.Types.VARCHAR);
        db.executeCallable();

        setID(db.cstmt.getString(businessIDIndex));

        // Update relationship to parent business
        if ((getParentSpaceID() != null) && !getParentSpaceID().equals("")) {
            BusinessSpace parentSpace = new BusinessSpace();
            parentSpace.setID(getParentSpaceID());
            SpaceManager.addSuperBusinessRelationship(db, parentSpace, this);
            
            FormManager.addSharedForms(db, parentSpace, this, false);
        }

    }

    /**
     * Add the person who just created the business to the directory.
     * Does not commit or rollback.
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if there is a problem storing
    */
    private void inviteOwner(DBBean db) throws SQLException {
        int statusId;
        int invitationCode;

        db.prepareCall("begin business.INVITE_PERSON_TO_BUSINESS(?,?,?,?,?,?,?,?,?); end;");
        // i_business_id IN NUMBER
        db.cstmt.setInt(1, new Integer(getID()).intValue());
        // i_person_id IN NUMBER
        db.cstmt.setInt(2, new Integer(this.user.getID()).intValue());
        // i_email IN VARCHAR2
        db.cstmt.setString(3, this.user.getEmail().trim());
        // i_firstname IN VARCHAR2
        db.cstmt.setString(4,this.user.getFirstName());
        // i_lastname IN VARCHAR2
        db.cstmt.setString(5,this.user.getLastName());
        // i_resonsibilties IN VARCHAR2
        db.cstmt.setString(6,"");
        // i_invitor_id IN NUMBER
        db.cstmt.setInt(7, new Integer(this.user.getID()).intValue());
        // o_invitation_code OUT NUMBER
        db.cstmt.registerOutParameter(8, java.sql.Types.INTEGER);
        // o_status OUT NUMBER
        db.cstmt.registerOutParameter(9, java.sql.Types.INTEGER);
        db.executeCallable();

        // get out parameters
        invitationCode = db.cstmt.getInt(8);
        statusId = db.cstmt.getInt(9);

        try {
            DBExceptionFactory.getException("BusinessCreateWizard.createBusiness() project.INVITE_PERSON_TO_BUSINESS", statusId);
        } catch (PnetException e) {
            throw (SQLException) new SQLException("Error in stored procedure INVITE_PERSON_TO_BUSINESS: " + e).initCause(e);
        }

        db.prepareStatement("Update pn_invited_users set invited_status = 'Accepted' where invitation_code = ?");
        db.pstmt.setInt(1,invitationCode);
        db.pstmt.executeQuery();

    }

    /**
     * Clear out any values stored in this wizard.
     *
     * @since Gecko Update 2 (ProductionLink)
     */
    public void clear() {
        relatedSpace = null;
        relationship = null;
        masterBusiness = false;
        user = null;
        super.clear();
    }
} 
