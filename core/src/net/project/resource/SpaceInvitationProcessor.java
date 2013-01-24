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

 package net.project.resource;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.project.base.DefaultDirectory;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.notification.EventCodes;
import net.project.notification.NotificationException;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.security.group.GroupException;
import net.project.security.group.GroupProvider;
import net.project.space.Space;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;

/**
 * Performs actual invitation work, including updating rosters sending
 * notifications.
 */
public class SpaceInvitationProcessor {

    /** The space to which invitees are being added. */
    private Space targetSpace = null;

    /** The user performing the invitation. */
    private User invitingUser = null;

    /** The list of invitees. */
    private List inviteeList = null;

    /** Flag (default true) indicating whether to send email notifications on inviation */
    private boolean notifyOnInvite = true;

    /** The parameters for the invitation. */
    private SpaceInvitationManager.SpaceInvitationParameters parameters =
        new SpaceInvitationManager.SpaceInvitationParameters();

    /**
     * Creates a new SpaceInvitationProcessor for inviting the specified
     * invitees to the specified space.
     * 
     * @param targetSpace the space to which invitees are to be added
     * @param invitingUser the user performing the invitation
     * @param inviteeList the list of invitees; each element must
     * @param parameters additional information about the invitation. (See
     * {@link net.project.resource.SpaceInvitationManager.SpaceInvitationParameters})
     * @param notifyOnInvite a <code>boolean</code> indicating if the system
     * will send notifications on invite.
     */
    public SpaceInvitationProcessor(Space targetSpace, User invitingUser, List inviteeList, SpaceInvitationManager.SpaceInvitationParameters parameters, boolean notifyOnInvite) {

        this.targetSpace = targetSpace;
        this.invitingUser = invitingUser;
        this.inviteeList = new LinkedList(inviteeList);
        this.parameters = parameters;
        this.notifyOnInvite = notifyOnInvite;
    }

    /**
     * Returns the space to which invitees are to be added.
     * 
     * @return the space
     */
    public Space getTargetSpace() {
        return this.targetSpace;
    }

    /**
     * Returns the user performing the invite.
     * 
     * @return the user
     */
    public User getInvitingUser() {
        return this.invitingUser;
    }

    /**
     * Returns the list of invitees.
     * 
     * @return the invitees being invited; each element is of type
     *         <code>Invitee</code>
     */
    public List getInviteeList() {
        return this.inviteeList;
    }


    /**
     * Sets a flag indicating whether to send email notifications on invitation
     * 
     * @param notify flag indicating whether to send emails
     */
    public void setNotifyOnInvite(boolean notify) {
        this.notifyOnInvite = notify;
    }

    /**
     * Invites all invitees to the target space. Modifies invitees in the
     * invitee list to set their email address to the primary address (in the
     * case where the invitee represents an existing person).
     * 
     * @throws PersistenceException if a database problem occurs during invite
     * @throws SpaceInvitationException if there is a problem inviting invitees;
     * note that when this exception occurs, notification has been attempted on
     * the successfully invited invitees.
     * @throws NotificationException if all invitees were invited, but a problem
     * occurs Notifying the members of their invitation
     * @throws IllegalStateException if there inviteeList is empty
     */
    public void inviteAll(boolean sendNotifications) throws PersistenceException, SpaceInvitationException, NotificationException {

        if (inviteeList.isEmpty()) {
            throw new IllegalStateException("No invitees to invite");
        }

        SpaceInvitationException invitationException = null;
        List successfulPersonList = new ArrayList();

        try {
            // Create invitations for all invitees, updating the list
            // of successful Persons; we use the Person object since
            // it will contain the correct display name
            createInvitations(inviteeList, successfulPersonList);

        } catch (SpaceInvitationException e) {
            // Bad problem occurred (that is not a PersistenceException)
            // We'll try to notify successful invitees and rethrow
            // this exception later
            invitationException = e;
        }

        // if the flag has been set to send notifications on invite, try to create the notifications
        if (this.notifyOnInvite) {

            try {
                // Regardless of any exception, try to send notifications to
                // successfully invited persons
                createNotifications(successfulPersonList);

            } catch (NotificationException e) {
                // Problem sending notifications
                // If we saved an earlier exception, rethrow it
                // The earlier exception will take precedence over this
                // notification exception
                // Otherwise, we propogate the NotificationException since
                // it is the only problem here
            
                if (invitationException != null) {
                    throw invitationException;
                } else {
                    throw e;
                }
            }
        }

        // Finally, handle the case where notification was successful
        // for those people invited, but there was a problem inviting
        // everyone.
        if (invitationException != null) {
            throw invitationException;
        }

        // At this point, no exceptions occurred in the whole process
        // Terminate silently

    }


    //
    // Invitation methods
    //


    /**
     * Invites all invitees. If a person is found matching the invitee email,
     * the invitee email is reset to the person's primary email (which may be
     * different from the one they were invited by)
     * 
     * @param successPersonList the list of <code>Person</code>s that
     * corresponds to the invitees that were successful invited.
     * @throws PersistenceException if there is a problem in the database
     * @throws SpaceInvitationException if some other problem occurs
     */
    private void createInvitations(List inviteeList, List successPersonList) throws PersistenceException, SpaceInvitationException {

        // Iterate over all invitees, creating an invitation

        for (Iterator it = inviteeList.iterator(); it.hasNext();) {
            Invitee nextInvitee = (Invitee) it.next();

            Person nextPerson = createInvitation(nextInvitee);
            // At this point, the invitation was a success
            // add to our success lsit
            successPersonList.add(nextPerson);

        }

    }


    /**
     * Invites invitee.
     * Email address may be updated to existing person's primary email.
     * Creates a person stub if no registered person exists with
     * the invitee's email.
     * Creates the invitation and adds an assignment
     * @param invitee the invitee to invite
     * @return the Person created or loaded that corresponds to the invitee
     * @throws PersistenceException if a database operation fails
     * @throws SpaceInvitationException if some other problem occurs
     */
    private Person createInvitation(Invitee invitee) throws PersistenceException, SpaceInvitationException {
        Person newMember = null;
        Roster roster = new Roster();
        roster.setSpace(invitee.getSpace());

        //Step 1, ensure that we have a person record for the person being invited
        if (invitee.getInvitedPerson() != null) {
            //This person is obviously in the system because the user set a
            //person record for them.  Use that record.
            newMember = invitee.getInvitedPerson();
        } else if (!DefaultDirectory.userExists(invitee.getEmail())) {
            //The users isn't already in the system, create a person record for
            //them.
            newMember = createPersonStub(invitee);
        } else {
            //The user is in the system, but when they were invited, their
            //person record wasn't available.  Load it now.
            newMember = DefaultDirectory.lookupByEmail(invitee.getEmail());

            // reset invitee email to the members primary email address
            invitee.setEmail(newMember.getEmail());
        }
        
        if(StringUtils.isNotEmpty(invitee.getResponsibilities())) {
        	newMember.setResponsibilities(invitee.getResponsibilities());
        }
        
        if(StringUtils.isNotEmpty(invitee.getTitle())) {
        	newMember.setTitle(invitee.getTitle());
        }
        
        if(invitee.getRoles() != null) {
        	newMember.setRoles(invitee.getRoles());
        }

        // Person Stub must be committed at this point
        // Note:  It is not necessarily a problem if the rest
        // of the invitation process fails; even with a committed
        // person stub, the person can be re-invited.


        // Step 2: 
        // Now we can invite them to the project
        if (newMember == null) {
            // Team Member not found, even after creating stub
            throw new SpaceInvitationException("Person invite operation failed:  " +
                "Unable to find person or create new person");
        }

        DBBean db = new DBBean();

        try {
            db.setAutoCommit(false);

            // Create/Update invitation
            String invitationCode = processInvitation(db, newMember);

            // If we now have a valid invitation code, add or update an assignment
            // for this user and space
            if (invitationCode != null) {

                // COMMIT WORK
                db.commit();
                roster.storePersonRosterProperties(newMember);

                // TODO: 01/15/2002 - Tim - Change this to pass in "db"
                // to add assignment in same transaction
                // Add or update the assignment
                addUserAssignment(newMember);
                
                // Create notification for member invite event
    			net.project.project.ProjectEvent event = new net.project.project.ProjectEvent();
    			event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
    			event.setTargetObjectID(SessionManager.getUser().getCurrentSpace().getID());
    			event.setTargetObjectType(EventCodes.INVITE_PROJECT_PARTICIPANT);
    			event.setTargetObjectXML(Roster.getXMLMailBody(newMember.getDisplayName()));
    			event.setEventType(EventCodes.INVITE_PROJECT_PARTICIPANT);
    			event.setUser(SessionManager.getUser());
    			event.setDescription(PropertyProvider.get("prm.notification.type.participantinvited.description") +": \"" + newMember.getDisplayName() + "\"");
    			event.store();

            } else {
                // Invitation code was null
                throw new SpaceInvitationException("Person invite operation " +
                    "failed: missing invitation code");

            }

        } catch (SQLException sqle) {
        	Logger.getLogger(SpaceInvitationProcessor.class).error("SpaceInvitiationProcessor.invite() threw " +
                "a SQLException: " + sqle);
            throw new PersistenceException("Invite operation failed: " + sqle, sqle);
        } catch (PersistenceException e) {
        	Logger.getLogger(SpaceInvitationProcessor.class).error("Error occurred while creating notification  SpaceInvitationProcessor.createInvitation() : "+ e.getMessage());
		} finally {
            try {
                db.rollback();
            } catch (SQLException sqle) {
                // Simply release
            }

            db.release();
        }

        return newMember;
    }


    /**
     * Creates a new person (stub) in the database for the new team member. The
     * record_status of this user will be set to Active "A" and the user_status
     * will be set to "Unregistered" meaning  that they still need to register.
     *
     * @return a <code>Person</code> record associated with the new person.
     * @throws PersistenceException if there is a database error
     */
    private Person createPersonStub(Invitee invitee) throws PersistenceException {
        DBBean db = new DBBean();
        Person person = null;

        try {
            db.setAutoCommit(false);
            person = createPersonStub(db, invitee);
            db.commit();

        } catch (SQLException sqle) {
        	Logger.getLogger(SpaceInvitationProcessor.class).error("SpaceInvitationProcessor.createPerson() operation failed: " + sqle);
            throw new PersistenceException("Invitation create person operation failed: " + sqle, sqle);

        } finally {
            try {
                db.rollback();
            } catch (SQLException sqle) {
                // Simply release
            }
            db.release();
        }

        return person;
    }

    /**
     * Create a new person (stub) in the database for the new team member. The
     * record_status of this user will be set to Active "A" and the user_status
     * will be set to "Unregistered" meaning  that they still need to register.
     * <b>Note:</b> This does NOT commit/rollback/release the DBBean
     * 
     * @param db the DBBean in which to perform the transaction
     * @param invitee the invitee for whom to create a person stub
     * @throws PersistenceException if there is a database error
     */
    private Person createPersonStub(DBBean db, Invitee invitee) throws PersistenceException {

        Person newPerson = null;

        try {
            // add the new person to the database.
            // they still need to register to set username, password, proper name, etc.
            newPerson = new Person();
            newPerson.setEmail(invitee.getEmail());
            newPerson.setFirstName(invitee.getFirstName());
            newPerson.setLastName(invitee.getLastName());
            newPerson.setDisplayName(invitee.getDisplayName());
            // this will always be unregistered because no one will create a user
            // unless they are not already registered
            newPerson.setStatus(PersonStatus.UNREGISTERED);
            newPerson.createStub(db);

            db.commit();

        } catch (SQLException sqle) {
            throw new PersistenceException("Invitation create person operation failed: " + sqle, sqle);

        } finally {
            try {
                db.rollback();
            } catch (SQLException sqle) {
                // Simply release
            }
            db.release();
        }

        return newPerson;
    }


    /**
     * Create (or update) the a person's entry in the invited users table.
     * <b>Note:</b> Does not commit/rollback/release This entry will be used to
     * track the user's current status in the space
     * 
     * @param db DBBean passed in which to perform the transactions
     * @param newMember the new team member being invited
     * @return the invitation code created
     * @throws PersistenceException if there is a problem in the database
     */
    private String processInvitation(DBBean db, Person newMember) throws PersistenceException {

        String invitationCode = null;

        // Step 1
        // Look for an existing invitation to the target space
        // for the person's email address
        // If one is found, it is updated to reflect the new invitation
        // status
        // If an invitation is not found, one is created
        invitationCode = SpaceInvitationManager.getInvitationKey(newMember.getID(), getTargetSpace().getID(), db);
        if (invitationCode == null) {
            invitationCode = createSpaceInvitation(getTargetSpace().getID(), db, newMember);

        } else {
            updateSpaceInvitation(invitationCode, db, newMember);

        }

        try {
            // Step 2
            // now put the user in the space -- but without affording access rights
            getTargetSpace().addMember(newMember.getID(), invitationCode, db);

            // this "accepts" the assignement and invitation, and adds the user to the team member role
            SpaceInvitationManager.acceptInvitation(invitationCode, getTargetSpace().getID(), newMember.getID(), db);

            // and finally, add user to their appropriate roles if any are available
            addUserToRoles(newMember.getID(), newMember.getRoles() != null ? newMember.getRoles() : this.parameters.getRoles(), false, db);

        } catch (SQLException sqle) {
            throw new PersistenceException("Space invite operation failed: " + sqle, sqle);

        }

        return invitationCode;
    }


    /**
     * Creates an invitation to the space based on the state of the internal
     * member properties of this class. <b>Note:</b> The DBBean is NOT
     * released.
     * 
     * @param spaceID the spaceID of the space to be invited to
     * @param invitationStatus will be set to Invited or Accepted
     * @param db a DBBean instance passed in from elsewhere
     * @param newMember the person to invite
     * @return invitationCode the space invitation code
     * @throws PersistenceException if there is a database problem
     */
    private String createSpaceInvitation(String spaceID, DBBean db, Person newMember) throws PersistenceException {

        String invitationCode;
        int statusCode;

        try {

            int invitationCodeIndex;
            int statusCodeIndex;
            int index = 0;
            db.prepareCall("{call space.invite_person (?,?,?,?,?,?,?,?,?)}");
            db.cstmt.setString(++index, spaceID);
            db.cstmt.setString(++index, newMember.getID());
            db.cstmt.setString(++index, newMember.getEmail().toLowerCase().trim());
            db.cstmt.setString(++index, newMember.getFirstName());
            db.cstmt.setString(++index, newMember.getLastName());
            db.cstmt.setString(++index, StringUtils.isNotEmpty(newMember.getResponsibilities()) ? newMember.getResponsibilities() : this.parameters.getResponsibilities());
            db.cstmt.setString(++index, getInvitingUser().getID());
            db.cstmt.registerOutParameter((invitationCodeIndex = ++index), java.sql.Types.INTEGER);
            db.cstmt.registerOutParameter((statusCodeIndex = ++index), java.sql.Types.INTEGER);
            db.executeCallable();

            invitationCode = db.cstmt.getString(invitationCodeIndex);
            statusCode = db.cstmt.getInt(statusCodeIndex);

            DBExceptionFactory.getException("SpaceInvitationProcessor.createSpaceInvitation()", statusCode);

        } catch (SQLException sqle) {
        	Logger.getLogger(SpaceInvitationProcessor.class).error("SpaceInvitationProcessor.createSpaceInvitation() threw an SQLException: " + sqle);
            throw new PersistenceException("Create space invitation operation failed; " + sqle, sqle);

        } catch (net.project.base.PnetException pe) {
            throw new PersistenceException("Create space invitation operation failed: " + pe, pe);

        }

        return invitationCode;
    }

    /**
     * Updates an already existing invitation record for a particular inviteCode
     * based on the state of the internal members of this class. <b>Note:</b>
     * The DBBean is NOT released.
     * 
     * @param inviteCode Space Invitation Code
     * @param db a DBBean instance passed in from elsewere (will not
     * db.release())
     * @param newMember the new Person being invited
     * @throws PersistenceException if there is a problem updating in the
     * database
     */
    private void updateSpaceInvitation(String inviteCode, DBBean db, Person newMember) throws PersistenceException {

        StringBuffer query = new StringBuffer();

        query.append("update pn_invited_users set ");
        query.append(" invitee_firstname = ? ");
        query.append(", invitee_lastname = ? ");
        query.append(", invitee_responsibilities = ? ");
        query.append(" where invitation_code = ? ");

        try {

            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, newMember.getFirstName());
            db.pstmt.setString(++index, newMember.getLastName());
            db.pstmt.setString(++index, StringUtils.isNotEmpty(newMember.getResponsibilities()) ? newMember.getResponsibilities() : this.parameters.getResponsibilities());
            db.pstmt.setString(++index, inviteCode);
            db.executePrepared();

        } catch (SQLException sqle) {
        	Logger.getLogger(SpaceInvitationProcessor.class).error("SpaceInvitationProcessor.updateSpaceInvitation() threw an SQLException: " + sqle);
            throw new PersistenceException("Create space invitation operation failed; " + sqle, sqle);

        }

    }

    /**
     * Adds user to the roles selected by the project administrator in the
     * wizard. <br> <b>Note:</b> Does not commit/rollback/release
     * 
     * @param personID the person we are adding
     * @param roles array of role IDs to add the user to
     * @param throwErrorIfNoRoles a <code>boolean</code>, if true will throw an
     * exception if roles[] is null
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if there is a problem in the database
     */
    private void addUserToRoles(String personID, String[] roles, boolean throwErrorIfNoRoles, DBBean db) throws SQLException {

        boolean nullRoles = false;
        GroupProvider groupProvider = new GroupProvider();

        if (roles == null || roles.length <= 0) {
            nullRoles = true;
        }

        if (throwErrorIfNoRoles && nullRoles) {
            throw new SQLException("SpaceInvitationManager.addUserToRoles: Unable to add user to roles; list of roles is null");

        } else if (!nullRoles) {
            ArrayList roleIDs = new ArrayList();
            roleIDs.addAll(java.util.Arrays.asList(roles));

            Person person = new Person();
            person.setID(personID);

            try {
                groupProvider.addMemberToGroups(person, roleIDs, db);

            } catch (GroupException ge) {
                throw new SQLException(ge.toString());

            }

        }
    }

    /**
     * Add the invited user to the invited users table. This actually updates an
     * assignment if one is already present.
     * 
     * @param newMember the person being invited
     * @throws PersistenceException if there is a problem adding the assignment
     */
    private void addUserAssignment(Person newMember) throws PersistenceException {

        // need to create an assignment for the invited
        SpaceAssignment inviteAssignment = new SpaceAssignment();

        // add the assignment to the user's personal space
        inviteAssignment.setSpaceID(newMember.getID());
        // identify the invited user
        inviteAssignment.setPersonID(newMember.getID());
        // set object id of the space
        inviteAssignment.setObjectID(getTargetSpace().getID());
        // set the assignor
        inviteAssignment.setAssignorID(getInvitingUser() != null ? getInvitingUser().getID() : null);
        // hard coded to be status assigned;
        inviteAssignment.setStatus(AssignmentStatus.ACCEPTED);

        inviteAssignment.store();
    }


    //
    // Notify methods
    //


    /**
     * Send an email notification to all invitees. Currently Sends ONE
     * invitation per person; Notifications don't yet support bulk sending of a
     * single notification
     * 
     * @param notifyPersonList the list of Persons to notify
     * @throws net.project.notification.NotificationException if there is a
     * problem sending notifications;  Sending is aborted on the first error
     */
    private void createNotifications(List notifyPersonList)
        throws net.project.notification.NotificationException {

        SpaceInviteNotification notification;

        // Iterate over each person and send appropriate notification
        for (Iterator it = notifyPersonList.iterator(); it.hasNext();) {
            Person nextPerson = (Person) it.next();

            // Construct correct notification
            if (nextPerson.getStatus() != null && nextPerson.getStatus().equals(PersonStatus.UNREGISTERED)) {
                notification = new SpaceUnregisteredInviteNotification();

            } else {
                notification = new SpaceInviteNotification();

            }

            try {
                // Initialize and send the notification
                notification.initialize(getTargetSpace(), getInvitingUser(), nextPerson, this.parameters);
                notification.post();

            } catch (net.project.notification.NotificationException ne) {

                if (ne.getReasonCode() == net.project.notification.Notification.NOTIFICATION_SEND_FAILED_EXCEPTION) {
                	Logger.getLogger(SpaceInvitationProcessor.class).error("SpaceInvitationProcessor.notify(): InviteNotification failed: Invalid Address");

                } else if (ne.getReasonCode() == net.project.notification.Notification.NOTIFICATION_MESSAGING_EXCEPTION) {
                	Logger.getLogger(SpaceInvitationProcessor.class).error("SpaceInvitationProcessor.notify(): InviteNotification failed: Messaging Exception");

                }

                throw ne;
            }
        }

    }

}
