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

import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.directory.GroupWrapper;
import net.project.notification.NotificationException;
import net.project.persistence.PersistenceException;
import net.project.security.group.Group;
import net.project.security.group.GroupCollection;
import net.project.security.group.GroupTypeID;
import net.project.space.Space;

import org.apache.log4j.Logger;


/**
 * Performs processing for adding team members to a Space.
 */
public abstract class SpaceInvitationManager
    extends AbstractInvitationManager {
	
	private static Logger log = Logger.getLogger(SpaceInvitationManager.class);

	public static final String INVITED_SPACE_STATUS = "Invited";
    public static final String ACCEPTED_SPACE_STATUS = "Accepted";
    public static final String DELETED_SPACE_STATUS = "Deleted";
    public static final String REJECTED_SPACE_STATUS = "Rejected";

    /**
     * The parameters for the invitation, including responsibilites, roles etc.
     */
    private SpaceInvitationParameters parameters = new SpaceInvitationParameters();

    /** URL for invitations that must be accepted. */
    private String acceptRequiredURL = null;

    /** URL for invitations that have been automatically accepted. */
    private String autoAcceptURL = null;

    /** Flag (default true) indicating whether to send notifications on workspace invite */
    private boolean notifyOnInvite = true;

    /**
     * Creates a new, empty SpaceInvitationManager.
     */
    public SpaceInvitationManager() {
        // Nothing
    }

    //
    // Directory Presentation
    //
    

    /**
     * Indicates whether this directory supports Participants.
     * 
     * @return true if this directory supports participants; false if not
     */
    public abstract boolean isParticipantsSupported();


    /**
     * Indicates whether this directory supports Org Chart.
     * 
     * @return true if this directory supports Org Chart; false if not
     */
    public abstract boolean isOrgChartSupported();


    /**
     * Indicates whether this directory supports Assignments.
     * 
     * @return true if this directory supports Assignments; false if not
     */
    public abstract boolean isAssignmentsSupported();


    /**
     * Indicates whether this directory supports Roles.
     * 
     * @return true if this directory supports Roles; false if not
     */
    public abstract boolean isRolesSupported();


    //
    // Common methods
    // 

    /**
     * Specifies whether invitees are to have their invitations automatically
     * accept.  Setting this allows invitees to be immediately assigned tasks
     * etc.
     * 
     * @param isAutoAcceptInvite true to automatically accept invitation; false
     * to require invitee to accept invitation
     * @see #isAutoAcceptInvite
     */
    public void setAutoAcceptInvite(boolean isAutoAcceptInvite) {
        this.parameters.setAutoAcceptInvite(isAutoAcceptInvite);
        // Now ensure the parameters are providing the correct
        // URL
        setInvitationURL();
    }

    public void setNotifyOnInvite(boolean notify) {
        this.notifyOnInvite = notify;
    }

    /**
     * Indicates whether invitees will auto-accept invitation when finally
     * invited.
     * 
     * @return true if invitations are automatically accepted; false otherwise
     * @see #setAutoAcceptInvite
     */
    public boolean isAutoAcceptInvite() {
        return this.parameters.isAutoAcceptInvite();
    }


    /**
     * Set the invited team members' notification message. This message is sent
     * to all invitees.
     * 
     * @param message the message to include in notifications
     * @see #getInviteeMessage
     */
    public void setInviteeMessage(String message) {
        if (message != null) {
            message = message.trim();
        }
        this.parameters.setMessage(message);
    }

    /**
     * Returns the invited team members' notification message.
     * 
     * @return the message to be included in notifications
     * @see #setInviteeMessage
     */
    public String getInviteeMessage() {
        return this.parameters.getMessage();
    }

    /**
     * Set the Invited team member's responsibilities. This is simply text
     * indicating their responsibilities in the space.  This text is included in
     * all invitees notifications.
     * 
     * @param responsibilities the responsibility text
     * @see #getInviteeResponsibilities
     */
    public void setInviteeResponsibilities(String responsibilities) {
        if (responsibilities != null) {
            responsibilities = responsibilities.trim();
        }
        this.parameters.setResponsibilities(responsibilities);
    }

    /**
     * Returns the invited team members' responsibilities.
     * 
     * @return the responsibilities include in notifications
     * @see #setInviteeResponsibilities
     */
    public String getInviteeResponsibilities() {
        return this.parameters.getResponsibilities();
    }


    /**
     * Sets the roles to which to add invitees.
     * 
     * @param roles the IDs of the roles to add invitees to
     * @see #getAssignedRoles
     */
    public void setAssignedRoles(String[] roles) {
        this.parameters.setRoles(roles);
    }

    /**
     * Returns the roles to which invitees are added.
     * 
     * @return the IDs of the roles to which to add invitees
     * @see #setAssignedRoles
     */
    public String[] getAssignedRoles() {
        return this.parameters.getRoles();
    }

    /**
     * Returns an HTML option list of all of the roles owned by the current
     * space.  Invitees may be added to any or all of these roles.+ <p> Note
     * that the Space Administrator role is included only if the inviting user
     * is a space administrator. Excludes Team Member role (since invitees are
     * always implictly added) and any Principal roles. </p>
     * 
     * @param defaultRoles the roles to select in the option list
     * @return the HTML option list
     */
    public String getRoleOptionList(String[] defaultRoles) {
        return getRoleOptionList(getSpace(), defaultRoles);
    }
    
    /**
     * Returns an list of all of the roles owned by the current
     * space.  Invitees may be added to any or all of these roles.+ <p> Note
     * that the Space Administrator role is included only if the inviting user
     * is a space administrator. Excludes Team Member role (since invitees are
     * always implictly added) and any Principal roles. 
     * @param defaultRoles
     * @return
     */
    public ArrayList getRoleOptionLists(String[] defaultRoles) {
        return getRoleOptionLists(getSpace(), defaultRoles);
    }


    /**
     * Returns an HTML option list of all of the roles owned in the specified
     * space.
     * 
     * @param space the space for which to get roles
     * @param defaultRoles the roles to select in the option list
     * @return the HTML option list
     */
    private String getRoleOptionList(Space space, String[] defaultRoles) {

        // Option list to append to
        StringBuffer options = new StringBuffer();
        boolean showSpaceAdmin = space.isUserSpaceAdministrator(getUser()) ? true : false;
        String selectedAttribute = "";
        
        // Convert array to a list for convenience
        // Need to handle null array since Arrays.asList doesn't like them
        java.util.List defaultRolesList = null;
        if (defaultRoles != null) {
            defaultRolesList = java.util.Arrays.asList(defaultRoles);
        } else {
            defaultRolesList = new ArrayList();
        }

        try {

            GroupCollection groupList = new GroupCollection();
            groupList.setSpace(space);
            groupList.loadOwned();

            for (Iterator it = groupList.iterator(); it.hasNext();) {
                Group group = (Group) it.next();

                // now filter out groups we don't want
                if (!group.getGroupTypeID().equals(GroupTypeID.PRINCIPAL) && !group.getGroupTypeID().equals(GroupTypeID.TEAM_MEMBER) && !group.getGroupTypeID().equals(GroupTypeID.EVERYONE)) {

                    // Show group if it is not a Space Admin group or we want to see space admin groups
                    if (!group.getGroupTypeID().equals(GroupTypeID.SPACE_ADMINISTRATOR) ||
                        showSpaceAdmin) {

                        // Option is selected if defaultRoles array contains the specified group id
                        if (defaultRolesList.contains(group.getID())) {
                            selectedAttribute = "selected ";
                        } else {
                            selectedAttribute = "";
                        }

                        options.append("<option " + selectedAttribute + "value=\"" + group.getID() + "\">" + group.getName() + "</option>");
                    }
                }

            }

        } catch (PersistenceException pe) {
            // do nothing
        }

        return options.toString();
    }
    
    /**
     Returns an list of all of the roles owned in the specified
     * space.
     * @param space
     * @param defaultRoles
     * @return
     */
    
    public ArrayList getRoleOptionLists(Space space, String[] defaultRoles) {
        // Option list to append to
        boolean showSpaceAdmin = space.isUserSpaceAdministrator(getUser()) ? true : false;
        ArrayList<GroupWrapper> usergroup = new ArrayList<GroupWrapper>();

        // Convert array to a list for convenience
        // Need to handle null array since Arrays.asList doesn't like them
        java.util.List defaultRolesList = null;
        if (defaultRoles != null) {
            defaultRolesList = java.util.Arrays.asList(defaultRoles);
        } else {
            defaultRolesList = new ArrayList();
        }
        try {
            GroupCollection groupList = new GroupCollection();
            groupList.setSpace(space);
            groupList.loadOwned();

            for (Iterator it = groupList.iterator(); it.hasNext();) {
                Group group = (Group) it.next();

                // now filter out groups we don't want
                if (!group.getGroupTypeID().equals(GroupTypeID.PRINCIPAL) && !group.getGroupTypeID().equals(GroupTypeID.TEAM_MEMBER) && !group.getGroupTypeID().equals(GroupTypeID.EVERYONE)) {
                    // Show group if it is not a Space Admin group or we want to see space admin groups
                    if (!group.getGroupTypeID().equals(GroupTypeID.SPACE_ADMINISTRATOR) ||  showSpaceAdmin) {
                    	GroupWrapper groupWrapper = new GroupWrapper(); 
                        // Option is selected if defaultRoles array contains the specified group id
                    	groupWrapper.setGroupId(group.getID());
                    	groupWrapper.setGroupName(group.getName());
                    	usergroup.add(groupWrapper);
                    }
                }

            }
        } catch (Exception e) {
			log.error("Error occured while getting role names list: " + e.getMessage());
		}

        return usergroup;
    }


    /**
     * Sets the URL to use when accepting of the invitation is required. This
     * would typically take the user to a page that allows them to accept or
     * decline the invitation.  For example:<br> <code>SessionManager.getJSPRootURL()
     * + "/personal/ProjectRegister?projectID=...&module=" +
     * Module.PERSONAL_SPACE</code><br> This is used when {@link
     * #isAutoAcceptInvite} is false.
     * 
     * @param url the absolute URL to use
     */
    public void setInvitationAcceptRequiredURL(String url) {
        this.acceptRequiredURL = url;
        setInvitationURL();
    }


    /**
     * Sets the URL to use when automatic acceptance of the invitation occurs.
     * This would typically take the user to a page that allows them to accept
     * or decline the invitation.  For example:<br> <code>SessionManager.getJSPRootURL()
     * + "/project/Dashboard?id...&module=" + Module.PERSONAL_SPACE</code> This
     * is used when {@link #isAutoAcceptInvite} is true.
     * 
     * @param url the absolute URL to use
     */
    public void setInvitationAutoAcceptURL(String url) {
        this.autoAcceptURL = url;
        setInvitationURL();
    }

    /**
     * Sets the correct URL to include in the invitation based on the
     * <code>isAutoAcceptInvite</code> flag. If true, uses the autoAcceptURL; if
     * false, uses the acceptRequiredURL Modifies the parameters object.
     */
    private void setInvitationURL() {
        if (this.parameters.isAutoAcceptInvite()) {
            this.parameters.setInviteURL(this.autoAcceptURL);
        } else {
            this.parameters.setInviteURL(this.acceptRequiredURL);
        }
    }

    /**
     * Returns the invitation URL depending on whether accpetance is required.
     * 
     * @return the invitation URL
     */
    public String getInvitationURL() {
        return this.parameters.getInviteURL();
    }

    /**
     * Indicates if the invitation manager will send notification messages to
     * the invited users.
     */
    public boolean isSendNotifications() {
        return this.notifyOnInvite;
    }

    /**
     * Indicate if notification messages should be sent to invited users.
     */
    public void setSendNotifications(boolean sendNotifications) {
        this.notifyOnInvite = sendNotifications;
    }

    /**
     * Clear the cached information from this manager.
     * This does NOT include the current user and space or
     * the invite URLs.
     * Essentially it prepares this manager for use in the wizard.
     */
    public void clear() {
      /*  this.directoryID = null;
        this.searchableDirectory = null;
        this.searchName = null;
        this.inviteeList = new InviteeList();
      */
	    super.clear();
        this.parameters = new SpaceInvitationParameters();
        this.notifyOnInvite = true;
    }

    /**
     * Commits the invitation. Uses the current space, user, invitee list and
     * other parameters such as roles, responsibilities etc. <b>Note:</b> In the
     * event of a PersistenceException, no attempt is made to notify any
     * successfully invited persons; however, when a SpaceInvitationException
     * occurs the process will have attempted to send notifications to the
     * successfully invited persons up to that point; however the invitation
     * process is subsequently aborted. By default notifications are sent but
     * this behavior can be modified by setting the notifyOnInvite flag to false
     * (the default is true)
     * 
     * @throws PersistenceException if there is a database problem
     * @throws SpaceInvitationException if the invitation process was not
     * entirely successful; that is, some members have not been invited
     * @throws NotificationException if a problem occurs Notifying the members
     * there were attempted to be invited
     */
    public void commit()
        throws PersistenceException, SpaceInvitationException, NotificationException {

        // Create a new processor to do the actual invitation
        SpaceInvitationProcessor processor = new SpaceInvitationProcessor(
            getSpace(), getUser(), getInviteeList(), this.parameters,
            this.notifyOnInvite
        );

        processor.inviteAll(this.notifyOnInvite);
    }


    /**
     * Accepts an invitation to a space.
     * 
     * @param invitationCode The invitation code to accept
     * @param spaceID the space in question
     * @param userID the user that is accepting the invite
     * @throws PersistenceException if there is a problem accepting the
     * invitation
     */
    protected static void acceptInvitation(String invitationCode, String spaceID, String userID) throws PersistenceException {

        DBBean db = new DBBean();

        try {
            db.setAutoCommit(false);
            acceptInvitation(invitationCode, spaceID, userID, db);
            db.commit();

        } catch (SQLException sqle) {
        	Logger.getLogger(SpaceInvitationManager.class).error("SpaceInvitiationManager.acceptInvitiation threw an SQLException: " + sqle);
            throw new PersistenceException("Invitation accept operation failed: " + sqle, sqle);

        } finally {
            try {
                db.rollback();
            } catch (SQLException sqle) {
                // Simply release
            }
            db.release();

        }

    }


    /**
     * Accepts an invitation to a space. <br> <b>Note:</b> Does not
     * commit/rollback/release
     * 
     * @param invitationCode The invitation code to accept
     * @param spaceID the space in question
     * @param userID the user that is accepting the invite
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if there was a database error
     */
    protected static void acceptInvitation (String invitationCode, String spaceID, String userID, DBBean db) throws SQLException {
        db.prepareCall ("{call space.accept_invitation (?,?,?)}");
        db.cstmt.setString (1, spaceID);
        db.cstmt.setString (2, userID);
        db.cstmt.setString (3, invitationCode);
        db.executeCallable();
    }


    /**
     * Declines an invitation to a space
     * 
     * @param invitationCode The invitation code to accept
     * @param spaceID the space in question
     * @param userID the user that is rejecting the invite
     * @throws PersistenceException
     */
    protected static void rejectInvitation(String invitationCode, String spaceID, String userID) throws PersistenceException {

        DBBean db = new DBBean();
        int errorCode = -1;

        try {

            db.prepareCall("begin space.decline_invitation (?,?,?,?); end;");

            db.cstmt.setString(1, spaceID);
            db.cstmt.setString(2, userID);
            db.cstmt.setString(3, invitationCode);
            db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);

            db.executeCallable();
            errorCode = db.cstmt.getInt(4);

            DBExceptionFactory.getException("SpaceInvitationManager.rejectInvitation()", errorCode);
        } catch (SQLException sqle) {
            throw new PersistenceException("SpaceInvitationManager.rejectInvitation() threw an SQLE: " + sqle, sqle);
        } catch (net.project.base.PnetException pe) {
            throw new PersistenceException("SpaceInvitationManager.rejectInvitation() threw an SQLE: " + pe, pe);
        } finally {
            db.release();
        }
    }


    /**
     * Returns a space invitation code based on an email address and spaceID.
     * Will only do so if the user already has an existing invitation to the
     * space.
     * 
     * @param personID an invitee's ID
     * @param spaceID the space in question
     * @return space invitation code or null if one not found for the specified
     *         email and space
     * @throws PersistenceException if there is a problem getting the invitation
     * code
     */
    protected static String getInvitationKey(String personID, String spaceID) throws PersistenceException {

        String inviteCode = null;

        DBBean db = new DBBean();

        try {
            db.setAutoCommit(false);
            inviteCode = getInvitationKey(personID, spaceID, db);
            db.commit();

        } catch (SQLException sqle) {
        	Logger.getLogger(SpaceInvitationManager.class).error("SpaceInvitiationManager.getInvitationKey threw an SQLException: " + sqle);
            throw new PersistenceException("Space Invitation get invitation code operation failed: " + sqle, sqle);

        } finally {
            try {
                db.rollback();
            } catch (SQLException sqle) {
                // Simply release
            }
            db.release();

        }

        return inviteCode;
    }

    /**
     * Returns a space invitation code based on an email address and spaceID.
     * Will only do so if the user already has an existing invitation to the
     * space. <b>Note:</b> no commit/rollback/release performed
     * 
     * @param personID an invitee's ID
     * @param spaceID the space in question
     * @param db a DBBean instance in which to perform the transactions
     * @return space invitation code or null if one not found for the specified
     *         email and space
     * @throws PersistenceException if there is a problem getting the invitation
     * code
     */
    protected static String getInvitationKey(String personID, String spaceID, DBBean db) throws PersistenceException {

        String inviteCode = null;

        StringBuffer query = new StringBuffer();
        query.append("select invitation_code ");
        query.append("from pn_invited_users ");
        query.append("where person_id = ? and space_id = ? ");

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, personID);
            db.pstmt.setString(++index, spaceID);
            db.executePrepared();

            if (db.result.next()) {
                inviteCode = db.result.getString("invitation_code");
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(SpaceInvitationManager.class).error("SpaceInvitationProcessor.getInvitationKey() threw an SQLException: " + sqle);
            throw new PersistenceException("Space invite operation failed: " + sqle, sqle);
        }

        return inviteCode;
    }

    /**
     * Loads the searchable directories for the project/business/license.
     * @throws net.project.base.directory.DirectoryException if there is a problem determining
     * the user's domain's directories
     * @throws PersistenceException if there is a problem getting
     * the user's domain
     */
    protected  void loadSearchableDirectories(boolean isIncludeCurrentBusiness) 
            throws net.project.base.directory.DirectoryException, PersistenceException {
    	// Do nothing
	//Subclasses should implement it to load the correct directories.
    }

    //
    // Nested top-level classes
    //

    /**
     * The parameters to invitation, including responsibilities text, roles
     * etc.
     */
    public static class SpaceInvitationParameters {

        /** The responsibility text for all invitees. */
        private String responsibilities = null;

        /** The message to be sent to all invitees. */
        private String message = null;


        /**
         * Indicates whether the invitation is to be considered "automatically
         * accepted" by all invitees.
         */
        private boolean isAutoAcceptInvite = false;

        /**
         * The selected roles to assign all invitees to. These are in addition
         * to Team Member role.
         */
        private String[] roles = null;

        /** The URL to invite to. */
        private String inviteURL = null;

        public void setResponsibilities(String responsibilities) {
            this.responsibilities = responsibilities;
        }

        public String getResponsibilities() {
            return this.responsibilities;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

        public void setAutoAcceptInvite(boolean isAutoAcceptInvite) {
            this.isAutoAcceptInvite = isAutoAcceptInvite;
        }

        public boolean isAutoAcceptInvite() {
            return this.isAutoAcceptInvite;
        }

        public void setRoles(String[] roles) {
            this.roles = roles;
        }

        public String[] getRoles() {
            return this.roles;
        }

        public void setInviteURL(String url) {
            this.inviteURL = url;
        }

        public String getInviteURL() {
            return this.inviteURL;
        }

    }


}
