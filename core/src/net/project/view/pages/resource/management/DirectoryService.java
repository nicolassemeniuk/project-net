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
/**
 * 
 */
package net.project.view.pages.resource.management;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.business.BusinessSpace;
import net.project.business.BusinessSpaceFinder;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.notification.NotificationException;
import net.project.persistence.PersistenceException;
import net.project.resource.Address;
import net.project.resource.AssignmentStatus;
import net.project.resource.Person;
import net.project.resource.PersonStatus;
import net.project.resource.Roster;
import net.project.resource.SpaceAssignment;
import net.project.resource.SpaceInvitationException;
import net.project.resource.SpaceInvitationManager;
import net.project.resource.SpaceInvitationProcessor;
import net.project.resource.SpaceInviteNotification;
import net.project.resource.SpaceUnregisteredInviteNotification;
import net.project.security.User;
import net.project.security.group.Group;
import net.project.security.group.GroupCollection;
import net.project.security.group.GroupException;
import net.project.security.group.GroupProvider;
import net.project.security.group.GroupTypeID;
import net.project.space.Space;

import org.apache.log4j.Logger;

public class DirectoryService {

	private static final String BUSINESS_SPACE_SELECT_COLUMNS = "b.business_space_id, b.business_id, b.space_type, "
			+ "b.complete_portfolio_id, b.record_status, b.is_master, "
			+ "b.business_category_id, b.brand_id, b.billing_account_id, b.address_id, "
			+ "b.business_name, b.business_desc, b.business_type, b.logo_image_id, "
			+ "b.is_local, b.remote_host_id, b.remote_business_id, b.num_projects, " + "b.num_people ";

	/**
	 * Some of the methods in the class have used direct SQL statements,
	 * this is just the intermediate solution. 
	 * Some of the functionality/API's are not available in hibernate implemetation of pnet.
	 * So, temporarily using queries, which will eventually be developed using hibernate api's.
	 *
	 */
	public DirectoryService() {

	}

	public Person getPersonDetail(String personid) {
		Person person = null;
		DBBean db = new DBBean();

		String query = "select p.person_id, p.display_name, p.first_name, p.last_name, p.middle_name, p.email, p.timezone_code,"
				+ " p.user_status, p.has_license, a.address_id, a.office_phone, a.fax_phone, a.mobile_phone, a.pager_phone,"
				+ " a.pager_email, a.website_url"
				+ " from pn_person_view p, pn_address a"
				+ " where p.person_id = "+ personid
				+ " and a.address_id  = p.address_id ";

		try {
			db.setQuery(query);
			db.executeQuery();
			if (db.result.next()) {
				person = new Person();
				populate(person, db.result, true);
			}

			query = "select p.person_id, p.display_name, p.first_name, p.last_name, p.middle_name, p.email, "
					+ " p.timezone_code, p.user_status, p.has_license "
					+ " from pn_person_view p  where   p.person_id = " + personid;

			if (person == null) {
				db.setQuery(query);
				db.executeQuery();
				if (db.result.next()) {
					person = new Person();
					populate(person, db.result, false);
				}
			}
		} catch (SQLException sqle) {
			Logger.getLogger(Roster.class).error("Roster.java: SQL Exception thrown in search " + sqle);
		} finally {
			db.release();
		}
		return person;
	}

	private void populate(Person person, ResultSet result, boolean hasAddress) throws SQLException {
		Address address;
		person.setID(result.getString("person_id"));
		person.setDisplayName(result.getString("display_name"));
		person.setFirstName(result.getString("first_name"));
		person.setLastName(result.getString("last_name"));
		//person.setResponsibilities(result.getString("responsibilities"));
		//person.setTitle(result.getString("member_title"));
		person.setEmail(result.getString("email"));
		person.setStatus(PersonStatus.getStatusForID(result.getString("user_status")));

		// Address fields (partial)
		address = new Address();
		if (hasAddress) {
			address.setID(result.getString("address_id"));
			address.setOfficePhone(result.getString("office_phone"));
			address.setFaxPhone(result.getString("fax_phone"));
			address.setMobilePhone(result.getString("mobile_phone"));
			address.setPagerPhone(result.getString("pager_phone"));
			address.setPagerEmail(result.getString("pager_email"));
			address.setWebsiteURL(result.getString("website_url"));
		} else {
			address.setID("");
			address.setOfficePhone("");
			address.setFaxPhone("");
			address.setMobilePhone("");
			address.setPagerPhone("");
			address.setPagerEmail("");
			address.setWebsiteURL("");
		}
		person.setAddress(address);
	}

	public final List<BusinessSpace> getBusinessesByPersonId(String personId, String recordStatus)
			throws PersistenceException {

		// Construct the query
		StringBuffer query = new StringBuffer();
		query.append("select distinct ").append(BUSINESS_SPACE_SELECT_COLUMNS);
		query.append("from pn_business_space_view b, ");
		query.append("pn_space_has_person shp ");
		query.append("where b.record_status = ? ");
		query.append("and shp.space_id = b.business_space_id ");
		query.append("and (shp.person_id = ? or b.includes_everyone = 1) ");
		query.append("and shp.record_status = ? ");
		query.append("order by b.business_name asc ");

		List<BusinessSpace> businessSpaceList = new ArrayList<BusinessSpace>();
		DBBean db = new DBBean();

		try {
			db.prepareStatement(query.toString());
			int index = 0;
			db.pstmt.setString(++index, recordStatus);
			db.pstmt.setString(++index, personId);
			db.pstmt.setString(++index, recordStatus);
			db.executePrepared();

			while (db.result.next()) {
				BusinessSpace businessSpace = new BusinessSpace();
				populate(db.result, businessSpace);
				businessSpaceList.add(businessSpace);
			}
		} catch (SQLException sqle) {
			Logger.getLogger(BusinessSpaceFinder.class).error("Error loading all business spaces: " + sqle);
			throw new PersistenceException("subBusiness find for business operation failed: " + sqle, sqle);
		} finally {
			db.release();

		}
		return businessSpaceList;
	}
	
	public List<BusinessSpace> getSubBusinessByBusinessId(String businessId) throws PersistenceException {
		List<BusinessSpace> businessSpaceList = new ArrayList<BusinessSpace>();
		DBBean db = new DBBean();

		String query = " select " + BUSINESS_SPACE_SELECT_COLUMNS + " from pn_business_space_view b "
				+ " where b.business_id in (  select distinct shs.child_space_id from pn_space_has_space shs "
				+ " where shs.child_space_type = 'business' and shs.parent_space_id = " + businessId
				+ " and shs.record_status='A')";
		try {
			db.setQuery(query);
			db.executeQuery();
			while (db.result.next()) {
				BusinessSpace businessSpace = new BusinessSpace();
				populate(db.result, businessSpace);
				businessSpaceList.add(businessSpace);
			}
		} catch (SQLException sqle) {
			Logger.getLogger(BusinessSpaceFinder.class).error("Error loading all business spaces: " + sqle);
			throw new PersistenceException("BusinessSpace find for user operation failed: " + sqle, sqle);

		} finally {
			db.release();

		}
		return businessSpaceList;
	}

	private static void populate(ResultSet result, BusinessSpace businessSpace) throws SQLException {

		businessSpace.setID(result.getString("business_id"));
		businessSpace.setName(result.getString("business_name"));
		businessSpace.setDescription(result.getString("business_desc"));
		// businessSpace.m_complete_portfolio_id = result.getString("complete_portfolio_id");
		businessSpace.setFlavor(result.getString("business_type"));
		businessSpace.setLogoID(result.getString("logo_image_id"));
		businessSpace.setNumProjects(result.getString("num_projects"));
		businessSpace.setNumMembers(result.getString("num_people"));
		businessSpace.setRecordStatus(result.getString("record_status"));
		// businessSpace.m_address.setID(result.getString("address_id"));
		businessSpace.setLoaded(true);

	}

	public void addSpace(Space space, Person newMember, User invitor, String responsibility, String role) throws PersistenceException, SpaceInvitationException {
		DBBean db = new DBBean();
		try {
			db.setAutoCommit(false);
			String invitationCode = processInvitation(db, newMember, space, responsibility, invitor.getID(), role);
			if (invitationCode != null) {
				// COMMIT WORK
				db.commit();
				addUserAssignment(newMember, space.getID(), invitor);

			} else {
				// Invitation code was null
				throw new SpaceInvitationException("Person invite operation " + "failed: missing invitation code");

			}

		} catch (SQLException sqle) {
			Logger.getLogger(SpaceInvitationProcessor.class).error(
					"SpaceInvitiationProcessor.invite() threw " + "a SQLException: " + sqle);
			throw new PersistenceException("Invite operation failed: " + sqle, sqle);
		} finally {
			try {
				db.rollback();
			} catch (SQLException sqle) {
				// Simply release
			}
			db.release();
		}
		
		/*send notification mail to person
		
		try {
			createNotifications(space, newMember, invitor, role);
        } catch (NotificationException e) {
        	Logger.getLogger(SpaceInvitationProcessor.class).error("SpaceInvitationProcessor.notify(): InviteNotification failed");
        }*/
		
	}

	private String processInvitation(DBBean db, Person newMember, Space space, String responsibilities, String invitor, String role)
			throws PersistenceException {

		String invitationCode = null;
		invitationCode = getInvitationKey(newMember.getID(), space.getID(), db);

		if (invitationCode == null) {
			invitationCode = createSpaceInvitation(space.getID(), SpaceInvitationManager.ACCEPTED_SPACE_STATUS, db,
					newMember, responsibilities, invitor);

		} else {
			updateSpaceInvitation(invitationCode, SpaceInvitationManager.ACCEPTED_SPACE_STATUS, newMember,
					responsibilities, db);

		}

		try {
			// Step 2
			// now put the user in the space -- but without affording access rights
			space.addMember(newMember.getID(), invitationCode, db);

			// if user is autoaccepted, then grant access rights to the space and add the space to ther
			// user's portfolio
			// if (invitationStatus == SpaceInvitationManager.ACCEPTED_SPACE_STATUS) {

			// this "accepts" the assignement and invitation, and adds the user to the team member role
			acceptInvitation(invitationCode, space.getID(), newMember.getID(), db);

			// and finally, add user to their appropriate roles if any are available
			addUserToRoles(newMember.getID(), role, false, db);
			// }

		} catch (SQLException sqle) {
			throw new PersistenceException("Space invite operation failed: " + sqle, sqle);

		}

		return invitationCode;
	}

	public String createSpaceInvitation(String spaceId, String invitationStatus, DBBean db, Person newMember,
			String responsibilities, String invitorId) throws PersistenceException {
		
		String invitationCode = null;
		
		int statusCode;
		try {
			int invitationCodeIndex;
			int statusCodeIndex;
			int index = 0;
			db.prepareCall("{call space.invite_person (?,?,?,?,?,?,?,?,?)}");
			db.cstmt.setString(++index, spaceId);
			db.cstmt.setString(++index, newMember.getID());
			db.cstmt.setString(++index, newMember.getEmail().toLowerCase().trim());
			db.cstmt.setString(++index, newMember.getFirstName());
			db.cstmt.setString(++index, newMember.getLastName());
			db.cstmt.setString(++index, responsibilities);
			db.cstmt.setString(++index, invitorId);
			db.cstmt.registerOutParameter((invitationCodeIndex = ++index), java.sql.Types.INTEGER);
			db.cstmt.registerOutParameter((statusCodeIndex = ++index), java.sql.Types.INTEGER);
			db.executeCallable();

			invitationCode = db.cstmt.getString(invitationCodeIndex);
			statusCode = db.cstmt.getInt(statusCodeIndex);

			DBExceptionFactory.getException("SpaceInvitationProcessor.createSpaceInvitation()", statusCode);

		} catch (SQLException sqle) {
			Logger.getLogger(SpaceInvitationProcessor.class).error(
					"SpaceInvitationProcessor.createSpaceInvitation() threw an SQLException: " + sqle);
			throw new PersistenceException("Create space invitation operation failed; " + sqle, sqle);

		} catch (net.project.base.PnetException pe) {
			throw new PersistenceException("Create space invitation operation failed: " + pe, pe);

		}

		return invitationCode;
	}

	private String getInvitationKey(String personID, String spaceID, DBBean db) throws PersistenceException {

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
			Logger.getLogger(SpaceInvitationManager.class).error(
					"SpaceInvitationProcessor.getInvitationKey() threw an SQLException: " + sqle);
			throw new PersistenceException("Space invite operation failed: " + sqle, sqle);
		}
		return inviteCode;
	}

	private void updateSpaceInvitation(String inviteCode, String inviteStatus, Person newMember,
			String responsibilities, DBBean db) throws PersistenceException {

		StringBuffer query = new StringBuffer();

		query.append("update pn_invited_users set invited_status = ? ");
		query.append(", invitee_firstname = ? ");
		query.append(", invitee_lastname = ? ");
		query.append(", invitee_responsibilities = ? ");
		query.append(" where invitation_code = ? ");

		try {

			int index = 0;
			db.prepareStatement(query.toString());
			db.pstmt.setString(++index, inviteStatus);
			db.pstmt.setString(++index, newMember.getFirstName());
			db.pstmt.setString(++index, newMember.getLastName());
			db.pstmt.setString(++index, responsibilities);
			db.pstmt.setString(++index, inviteCode);
			db.executePrepared();

		} catch (SQLException sqle) {
			Logger.getLogger(SpaceInvitationProcessor.class).error(
					"SpaceInvitationProcessor.updateSpaceInvitation() threw an SQLException: " + sqle);
			throw new PersistenceException("Create space invitation operation failed; " + sqle, sqle);

		}
	}

	public void acceptInvitation(String invitationCode, String spaceID, String userID, DBBean db) throws SQLException {
		db.prepareCall("{call space.accept_invitation (?,?,?)}");
		db.cstmt.setString(1, spaceID);
		db.cstmt.setString(2, userID);
		db.cstmt.setString(3, invitationCode);
		db.executeCallable();
	}

	private void addUserToRoles(String personID, String roles, boolean throwErrorIfNoRoles, DBBean db)
			throws SQLException {

		boolean nullRoles = false;
		GroupProvider groupProvider = new GroupProvider();

		if (roles == null) {
			nullRoles = true;
		}

		if (throwErrorIfNoRoles && nullRoles) {
			throw new SQLException(
					"SpaceInvitationManager.addUserToRoles: Unable to add user to roles; list of roles is null");

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

	private void addUserAssignment(Person newMember, String spaceId, User invitor) throws PersistenceException {

		// need to create an assignment for the invited
		SpaceAssignment inviteAssignment = new SpaceAssignment();

		// add the assignment to the user's personal space
		inviteAssignment.setSpaceID(newMember.getID());
		// identify the invited user
		inviteAssignment.setPersonID(newMember.getID());
		// set object id of the space
		inviteAssignment.setObjectID(spaceId);
        // set the assignor
        inviteAssignment.setAssignorID(invitor != null ? invitor.getID() : null);
		// hard coded to be status assigned;
		inviteAssignment.setStatus(AssignmentStatus.ACCEPTED);
		inviteAssignment.store();
	}
	
	 private void createNotifications(Space space, Person person, User invitor, String role)
     throws net.project.notification.NotificationException {

     SpaceInviteNotification notification;
     String roles[] = {role};
     
     SpaceInvitationManager.SpaceInvitationParameters parameters = new SpaceInvitationManager.SpaceInvitationParameters();
     //parameters.setInviteURL(url);
     //parameters.setMessage(message)
     //parameters.setResponsibilities(responsibilities);
     parameters.setAutoAcceptInvite(true);
     parameters.setRoles(roles);
     
         // Construct correct notification
         if (person.getStatus() != null && person.getStatus().equals(PersonStatus.UNREGISTERED)) {
             notification = new SpaceUnregisteredInviteNotification();

         } else {
             notification = new SpaceInviteNotification();

         }

         try {
             // Initialize and send the notification
             notification.initialize(space, invitor, person, parameters);
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
	
	public String getRoleOptionList(Space space,  User user) {
        // Option list to append to
        String options = "";
        boolean showSpaceAdmin = space.isUserSpaceAdministrator(user) ? true : false;
        try {

            GroupCollection groupList = new GroupCollection();
            groupList.setSpace(space);
            groupList.loadOwned();
            options = "[";	
            for (Iterator it = groupList.iterator(); it.hasNext();) {
                Group group = (Group) it.next();
                // now filter out groups we don't want
                if (!group.getGroupTypeID().equals(GroupTypeID.PRINCIPAL) && !group.getGroupTypeID().equals(GroupTypeID.TEAM_MEMBER) && !group.getGroupTypeID().equals(GroupTypeID.EVERYONE)) {
                    // Show group if it is not a Space Admin group or we want to see space admin groups
                    if (!group.getGroupTypeID().equals(GroupTypeID.SPACE_ADMINISTRATOR) ||
                        showSpaceAdmin) {
                    	if(!options.equals("[")){
                    		options += ",";
                    	}else{
                    		options += " ['0', 'Team Member'], ";
                    	}
                        options += " ['" + group.getID() + "', '" + group.getName() + "']";
                    }
                }
            }
            options += "]";

        } catch (PersistenceException pe) {
            // do nothing
        }

        return options;
    }
}
