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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.project.base.DefaultDirectory;
import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.database.DBFormat;
import net.project.hibernate.service.ServiceFactory;
import net.project.notification.EventCodes;
import net.project.notification.NotificationException;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.portfolio.IPortfolioEntry;
import net.project.portfolio.ProjectPortfolio;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.security.group.Group;
import net.project.security.group.GroupException;
import net.project.security.group.GroupProvider;
import net.project.space.Space;
import net.project.util.DateFormat;
import net.project.util.HTMLUtils;
import net.project.xml.XMLUtils;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * A Roster of people within a Space context.
 * 
 * @author Carlos Montemuiño 2008-May
 * @author unascribed
 * @since Version 1
 */
public class Roster extends PersonList implements IXMLPersistence {

	private static final String PERSON_SELECT_COLUMNS = "select p.person_id, p.display_name, p.first_name, p.last_name, "
			+ "p.middle_name, p.email, p.timezone_code, p.user_status, p.image_id, p.skype, u.last_login, u.username, "
			+ "shp.responsibilities, shp.member_title, "
			+ "a.address_id, a.office_phone, a.fax_phone, a.mobile_phone, a.pager_phone, a.pager_email, a.website_url, u.domain_id  ";

	/**
	 * The last search key used for searching.
	 */
	private String searchKey = null;

	/**
	 * The space for which the roster is loaded.
	 */
	private Space space = null;

	/**
	 * This variable contains a list of persons loaded by the getAnyPerson()
	 * method.
	 */
	private final Map personCache = new WeakHashMap();

	/**
	 * Map pointing to the invited status for the users who are currently in the
	 * Roster.
	 */
	private final Map invitedStatus = new HashMap();

	/**
	 * Creates a new, empty roster
	 */
	public Roster() {
		// Do nothing
	}

	/**
	 * Creates a new, empty roster for the specified space.
	 * 
	 * @param space
	 *            the Space that this roster is for.
	 */
	public Roster(Space space) {
		this();
		setSpace(space);
	}

	/**
	 * Sets the space that this Roster is for. This will clear out any entries
	 * in the roster
	 * 
	 * @param space
	 *            the space that this roster is for
	 */
	public void setSpace(Space space) {
		this.clear();
		this.space = space;
	}

	/**
	 * Returns the current space id that this Roster is for.
	 * 
	 * @return the current space id or null if there is no current space
	 * @deprecated As of 7.7.1; No replacement. This will become private; unused
	 *             outside of this class.
	 */
	public String getCurrentSpaceID() {
		return getSpaceID();
	}

	/**
	 * Returns the current space id that this Roster is for.
	 * 
	 * @return the current space id or null if there is no current space
	 */
	public String getSpaceID() {
		return (this.space == null ? null : this.space.getID());
	}

	/**
	 * Returns the last key specified for the search.
	 * 
	 * @return the key
	 */
	public String getSearchKey() {
		return searchKey;
	}

	/**
	 * Clears all the entries in the Roster.
	 * <p/>
	 * Does not clear the current space.
	 */
	public void clear() {
		super.clear();
		this.searchKey = null;
		super.setIsLoaded(false);
	}

	/**
	 * Creates an HTML option list of the rosters member's ID values are roster
	 * member's person id's.
	 * 
	 * @param personIDList
	 *            default selected option(s). This can be a single ID or a
	 *            comma-delimited list of ID's.
	 * @return the HTML option list
	 */
	public String getSelectionList(String personIDList) {
		StringBuffer options = new StringBuffer();

		boolean isListed = false;

		for (int i = 0; i < size(); i++) {
			Person member = (Person) get(i);
			String memberStatus = new InvitationStatusChecker().checkStatus(member);
			boolean isPersonSelected = ((personIDList != null) && (personIDList.indexOf(member.getID()) != -1));

			if (isPersonSelected) {
				isListed = true;
				options.append(getOptionSelected(memberStatus, member));

			} else {
				options.append(getOption(memberStatus, member));
			}
		}

		if (!isListed && personIDList != null && !personIDList.trim().equals("")) {

			Person assignedPerson = new Person();
			assignedPerson.setID(personIDList);
			String str = new InvitationStatusChecker().checkStatus(assignedPerson);

			if ((str != null && str.equals("Deleted")) || (assignedPerson.getStatus() != null && assignedPerson.getStatus().getID().equals("Deleted"))) {
				options.append(formatOption(assignedPerson.getID(), assignedPerson.getDisplayName() + " (" + InviteeStatus.DELETED.getName() + ")", true));
			}
		}
		return options.toString();
	}

	/**
	 * Creates an HTML option list of the rosters member's ID values are roster
	 * member's person id's.
	 * 
	 * @param personIDList
	 *            default selected option(s). This can be a single email address
	 *            or a comma-delimited list of emails.
	 * @return the HTML option list
	 */
	public String getSelectionListByEmail(String personEmailList) {
		StringBuffer options = new StringBuffer();

		boolean isListed = false;
		String selectedPersonId = null;
		for (int i = 0; i < size(); i++) {
			Person member = (Person) get(i);
			String memberStatus = new InvitationStatusChecker().checkStatus(member);
			boolean isPersonSelected = ((personEmailList != null) && (personEmailList.indexOf(member.getEmail()) != -1));

			if (isPersonSelected) {
				isListed = true;
				selectedPersonId = member.getID();
				options.append(getOptionSelected(memberStatus, member));

			} else {
				options.append(getOption(memberStatus, member));
			}
		}

		if (!isListed && personEmailList != null && !personEmailList.trim().equals("")) {

			Person assignedPerson = new Person();
			assignedPerson.setID(selectedPersonId);
			String str = new InvitationStatusChecker().checkStatus(assignedPerson);

			if ((str != null && str.equals("Deleted")) || (assignedPerson.getStatus() != null && assignedPerson.getStatus().getID().equals("Deleted"))) {
				options.append(formatOption(assignedPerson.getID(), assignedPerson.getDisplayName() + " (" + InviteeStatus.DELETED.getName() + ")", true));
			}
		}
		return options.toString();
	}

	/**
	 * Returns an HTML option element for the specified person and status where
	 * that person is selected.
	 * 
	 * @param memberStatus
	 *            the current status
	 * @param person
	 *            the person for whom to produce the option
	 * @return the option where the value is the person ID and the display is
	 *         they name, including status display for Rejected or Deleted
	 *         members and the option is selected
	 */
	private String getOptionSelected(String memberStatus, Person person) {
		String option;
		if (memberStatus != null && memberStatus.equals("Rejected")) {
			option = formatOption(person.getID(), person.getDisplayName() + " (" + InviteeStatus.REJECTED.getName() + ")", true);
		} else if (person.getStatus() != null && person.getStatus().getID().equals("Deleted")) {
			option = formatOption(person.getID(), person.getDisplayName() + " (" + InviteeStatus.DELETED.getName() + ")", true);
		} else {
			option = formatOption(person.getID(), person.getDisplayName(), true);
		}

		return option;
	}

	/**
	 * Returns an HTML option element for the specified person and status where
	 * that person is not selected.
	 * <p/>
	 * Note that Rejected or Deleted persons cause an empty string to be
	 * returned, since they will not be displayed when not selected.
	 * 
	 * @param memberStatus
	 *            the current status
	 * @param person
	 *            the person for whom to produce the option
	 * @return the option where the value is the person ID and the display is
	 *         the name
	 */
	private String getOption(String memberStatus, Person person) {
		String option;
		if (memberStatus != null && memberStatus.equals("Rejected")) {
			option = "";
		} else if (person.getStatus() != null && person.getStatus().getID().equals("Deleted")) {
			option = "";
		} else {
			option = formatOption(person.getID(), person.getDisplayName(), false);
		}
		return option;
	}

	private String formatOption(String value, String display, boolean isSelected) {
		return "<option " + (isSelected ? "selected " : "") + "value=\"" + value + "\">" + HTMLUtils.escape(display) + "</option>";
	}

	/**
	 * creates an HTML option list of the rosters member's ID values are roster
	 * member's person id's
	 * 
	 * @param list
	 *            <code>List</code> selected options
	 * @return the HTML option list
	 */
	public String getSelectionList(List list) {

		StringBuffer options = new StringBuffer();
		List tempList = list;

		for (int i = 0; i < size(); i++) {

			boolean isRosterMember = false;
			String personID;
			Person member = (Person) get(i);

			for (int j = 0; j < list.size(); j++) {

				personID = (String) list.get(j);

				if (personID != null && personID.equals(member.getID())) {
					isRosterMember = true;
					tempList.remove(j);
				}

			}

			String memberStatus = new InvitationStatusChecker().checkStatus(member);

			if (isRosterMember) {
				options.append(getOptionSelected(memberStatus, member));

			} else {
				options.append(getOption(memberStatus, member));
			}
		}

		for (int index = 0; index < tempList.size(); index++) {

			String personID = (String) list.get(index);

			if (personID != null && !personID.trim().equals("")) {

				Person assignedPerson = new Person();
				assignedPerson.setID(personID);
				String str = new InvitationStatusChecker().checkStatus(assignedPerson);

				if ((str != null && str.equals("Deleted")) || (assignedPerson.getStatus() != null && assignedPerson.getStatus().getID().equals("Deleted"))) {
					options.append(formatOption(assignedPerson.getID(), assignedPerson.getDisplayName() + " (" + InviteeStatus.DELETED.getName() + ")", true));
				} else if (str == null) {
					assignedPerson = getAnyPerson(personID);
					options.append(formatOption(assignedPerson.getID(), assignedPerson.getDisplayName() + " (" + InviteeStatus.DELETED.getName() + ")", true));
				}

			}

		}

		return options.toString();
	}

	/**
	 * Get the Person in the roster indentified by the specified ID.
	 * 
	 * @param personID
	 *            the database ID of the person to return.
	 * @return the Person in the Roster with the passed personID, null if a
	 *         Person with the specified personID is not in the Roster.
	 */
	public Person getPerson(String personID) {
		// uses Person.equals() to compare by Person.getID() only.
		for (int i = 0; i < this.size(); i++) {
			if (((Person) this.get(i)).getID().equals(personID))
				return (Person) this.get(i);
		}

		return null;
	}

	/**
	 * Returns a Person for the specified person ID, even if that person is not
	 * loaded in this roster.
	 * <p/>
	 * This is useful for locating Persons who once belonged in the Roster since
	 * their IDs may still be in use in the workspace for which this Roster is
	 * loaded.
	 * <p/>
	 * This method maintains a cache of the Persons to make repeated requests
	 * for a person less costly.
	 * 
	 * @param personID
	 *            the id of the person we are trying to look up
	 * @return the person for that id, or null if the person does not exist in
	 *         the entire application
	 */
	public Person getAnyPerson(String personID) {
		Person p = (Person) personCache.get(personID);
		if (p == null) {
			p = DefaultDirectory.lookupByID(personID);
			if (p != null) {
				personCache.put(personID, p);
			}
		}

		return p;
	}

	/**
	 * Is the specified person on the Roster?
	 * 
	 * @deprecated As of 7.7.1; Use {@link #hasPerson(Person)} instead.
	 */
	public boolean hasPerson(String personID) {
		if (personID == null)
			return false;
		else
			return (getPerson(personID) != null);
	}

	/**
	 * Indicates whether the specified person exists in this Roster.
	 * <p/>
	 * 
	 * @param person
	 *            the person to locate; when null or when their ID is null,
	 *            false is always returned
	 * @return true if a person with a matching ID is found; false otherwise
	 */
	public boolean hasPerson(Person person) {
		if (person == null || person.getID() == null) {
			return false;
		} else {
			return (getPerson(person.getID()) != null);
		}
	}

	/**
	 * Returns information about when the a person last visited a space. The
	 * result may be a status or an empty string.
	 * <p/>
	 * <li>If the user person is not active as determined by
	 * <code>!{@link net.project.resource.Person#ACTIVE_USER_STATUS}</code> then
	 * their status is returned. This might be <b>Unregistered</b> or
	 * <b>Unconfirmed</b>
	 * <li>
	 * <li>Otherwise, the person is active and the return value is based on
	 * their invitation as given by the constants in
	 * {@link net.project.resource.SpaceInvitationManager}</li>
	 * <li>If no status could be determined then the empty string is returned.
	 * This most commonly occurs with space creators, who are active, but never
	 * received an invitation</li>
	 * 
	 * @param person
	 *            the person for whom to get the last visit information
	 * @return the status of their visit to this space, for example
	 *         Unregistered, Unconfirmed, Invited, Declined, Accepted
	 */
	private String getSpaceVisitStatus(Person person) {
		String visitStatus;

		// Determine whether user is active or not
		if (!person.getStatus().equals(PersonStatus.ACTIVE)) {
			// if the person hasn't registered yet -- or has been cancelled....
			// we want to show that information
			visitStatus = person.getStatus().getID();

		} else {
			// Person has registered, so return their status with respect to
			// this space
			visitStatus = getInvitationStatus(person.getID());

		}

		// finally, if lastVisit still hasn't been set, then display an empty
		// string
		if (visitStatus == null) {
			visitStatus = "";
		}

		return visitStatus;
	}

	/**
	 * Determines whether a user has accepted an invitation to belong to this
	 * Roster's project.
	 * 
	 * @param personId
	 *            the person identification to search into this roster
	 * @return <code>true</code> in case the user did accept to belog to this
	 *         Roster's project. Otherwise, it returns <code>false</code>
	 * @since 8.4
	 */
	public boolean isUserAccepted(String personId) {
		InviteeStatus status = (InviteeStatus) MapUtils.getObject(this.invitedStatus, personId);
		if (null == status || !StringUtils.equalsIgnoreCase(status.getID(), InviteeStatus.ACCEPTED.getID())) {
			return false;
		}
		return true;
	}

	/**
	 * Returns User's invitation status.
	 * 
	 * @param personID
	 *            id of the person in question
	 */
	public String getInvitationStatus(String personID) {
		InviteeStatus status = (InviteeStatus) invitedStatus.get(personID);

		// This if statement was ported from the old code. This seems to be a
		// fix for users that don't have an entry in the pn_invited_users table,
		// presumably because they predate it.
		if (status == null) {
			status = InviteeStatus.ACCEPTED;
		}

		return status.getID();
	}

	/**
	 * Returns the date of the person's last visit to the specified space.
	 * 
	 * @param person
	 *            the person for whom to get the last visit date time
	 * @param spaceID
	 *            the ID of the space in which to look
	 * @return the date and time of their last visit or null if they have never
	 *         visited
	 */
	private Date getUserLastVisitDatetime(Person person, String spaceID) {

		StringBuffer query = new StringBuffer();
		Date lastVisitDatetime = null;

		query.append("select access_date from pn_space_access_history ");
		query.append("where space_id = ? and person_id = ? ");

		DBBean db = new DBBean();
		try {
			int index = 0;
			db.prepareStatement(query.toString());
			db.pstmt.setString(++index, spaceID);
			db.pstmt.setString(++index, person.getID());
			db.executePrepared();

			// if the user has entered the space it will be shown here
			if (db.result.next()) {
				lastVisitDatetime = db.result.getTimestamp("access_date");

			}

		} catch (SQLException sqle) {
			Logger.getLogger(Roster.class).error("Roster.getUserLastVisitDatetime() threw an SQLException: " + sqle);
		} finally {
			db.release();
		}

		return lastVisitDatetime;
	}

	/**
	 * Returns the last visit date-time of the specified person to the specified
	 * space.
	 * 
	 * @param person
	 *            the person for whom to get the last visit datetime
	 * @param spaceID
	 *            the id of the space in question
	 * @return the datetime of their last visit as a string, or an empty string
	 *         if they have never visited the space (regardless of whether they
	 *         are regisitered, confirmed, invited etc.)
	 * @deprecated As of 7.7.1; No replacement. Unused; the XML for this Roster
	 *             contains the appropriate information
	 */
	public String getUserLastVisitDatetimeFormatted(Person person, String spaceID) {
		String lastVisitDatetimeFormatted;

		Date lastVisitDatetime = getUserLastVisitDatetime(person, spaceID);

		// now format the date
		User tmpUser = net.project.security.SessionManager.getUser();
		lastVisitDatetimeFormatted = tmpUser.getDateFormatter().formatDateTime(lastVisitDatetime);

		return lastVisitDatetimeFormatted;
	}

	/**
	 * Converts the object to XML node representation without the xml header
	 * tag. This method returns the object as XML text.
	 * <p/>
	 * Loads the Roster if not already loaded.
	 * 
	 * @return XML node representation
	 */
	public String getXMLBody() {

		StringBuffer xml = new StringBuffer();

		if (!isLoaded())
			this.load();

		xml.append("<roster>\n");

		xml.append("<space_id>");
		xml.append(getSpaceID());
		xml.append("</space_id>\n");
		xml.append("<space>" + SessionManager.getUser().getCurrentSpace().getType() + "</space>");
		xml.append("<securedConnection>" + SessionManager.getSiteScheme().toLowerCase().contains("https") + "</securedConnection>");
		for (int i = 0; i < this.size(); i++) {

			Person person = (Person) this.get(i);

			xml.append("<person>");
			xml.append("<space_id>");
			xml.append(getSpaceID());
			xml.append("</space_id>\n");
			xml.append("<projectSpaceType>" + Space.PROJECT_SPACE + "</projectSpaceType>");
			xml.append("<moduleId>" + Module.PROJECT_SPACE + "</moduleId>");
			xml.append(person.getXMLProperties());

			if (InviteeStatus.getForID(getSpaceVisitStatus(person)) != null) {
				xml.append(InviteeStatus.getForID(getSpaceVisitStatus(person)).getXMLBody());
			}

			xml.append("<space_visit_status>" + getSpaceVisitStatus(person) + "</space_visit_status>");
			xml.append("<last_visit>" + DateFormat.getInstance().formatDateMedium(getUserLastVisitDatetime(person, getSpaceID())) + "</last_visit>");
			xml.append("</person>");
		}

		xml.append("</roster>\n");

		return xml.toString();
	}

	/**
	 * Converts the object to XML representation This method returns the object
	 * as XML text.
	 * 
	 * @return XML representation of the object
	 */
	public String getXML() {
		return (IXMLPersistence.XML_VERSION + getXMLBody());
	}

	/**
	 * Removes the Persons in the specified person list from this roster.
	 * 
	 * @param personList
	 *            the list of persons to remove
	 * @deprecated As of 7.7.1; Use {@link #removePerson(String)} for each
	 *             person's ID instead
	 */
	public void removePersons(PersonList personList) {
		String rosterID;
		String personID;
		boolean isRemoved;
		Iterator personIt;
		Iterator rosterIt;

		// Iterate over persons in personList
		// Locate corresponding person in this Roster
		// Remove that person from this Roster

		personIt = personList.iterator();
		while (personIt.hasNext()) {
			personID = ((Person) personIt.next()).getID();

			rosterIt = iterator();
			isRemoved = false;
			while (rosterIt.hasNext() & !isRemoved) {
				rosterID = ((Person) rosterIt.next()).getID();

				// If the roster person id matches, remove it from roster
				if (personID.equals(rosterID)) {
					rosterIt.remove();
					isRemoved = true;
				}
			}
		}
	}

	/**
	 * Removes the Person specified by the personID from the roster.
	 * <p>
	 * Sends a notification to the person if the person being removed is not a
	 * space administrator; only sends the notification if they are not deleted
	 * nor unregistered.
	 * </p>
	 * 
	 * @param personID
	 *            the database ID of the Person to remove.
	 * @throws PersistenceException
	 *             if there is a problem locating the space admin group or the
	 *             person being removed is the last member of the space admin
	 *             group
	 */
	public void removePerson(String personID) throws PersistenceException {

		Group spaceAdmin;
		int statusId = 0;
		GroupProvider groupProvider = new GroupProvider();
		String personDisplayName;

		try {
			spaceAdmin = groupProvider.getSpaceAdminGroup(getSpaceID());
			spaceAdmin.load();

			if (!spaceAdmin.isMember(personID)) {
				// Person is not a space administrator
				// We send them a notification

				// Load the person to be removed
				Person person = new Person(personID);
				person.load();

				if (!(person.hasStatus(PersonStatus.DELETED) || person.hasStatus(PersonStatus.UNREGISTERED))) {
					// Only send the delete notification if the person is
					// neither
					// deleted nor unregistered
					// "dead soldiers can't read"
					ParticipantDeleteNotification pin = new ParticipantDeleteNotification();
					pin.initialize(space, SessionManager.getUser(), person);
					pin.post();
					personDisplayName = person.getDisplayName();

					// Create notification for member delete event
					net.project.project.ProjectEvent event = new net.project.project.ProjectEvent();
					event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
					event.setTargetObjectID(SessionManager.getUser().getCurrentSpace().getID());
					event.setTargetObjectType(EventCodes.REMOVE_PROJECT_PARTICIPANT);
					event.setTargetObjectXML(Roster.getXMLMailBody(personDisplayName));
					event.setEventType(EventCodes.REMOVE_PROJECT_PARTICIPANT);
					event.setUser(SessionManager.getUser());
					event.setDescription(PropertyProvider.get("prm.notification.type.participantdeletion.description") + ": \"" + personDisplayName + "\"");
					event.store();
				}

			} else {
				// No notification

				// If person being removed is a space administrator and
				// they are the only (that is, last) space administrator
				// We will not remove them; the project may only be disabled
				if (spaceAdmin.isMember(personID) && spaceAdmin.getPersonMembers().size() == 1) {
					if (space.getType().equals(ObjectType.PROJECT)) {
						throw new PersistenceException(PropertyProvider.get("prm.directory.resource.roster.removeperson.projectspaceadmingroup.message"));
					} else {
						throw new PersistenceException(PropertyProvider.get("prm.directory.resource.roster.removeperson.businessspaceadmingroup.message"));
					}

				}

			}

		} catch (GroupException ge) {
			// Problem locating space admin group
			throw new PersistenceException("Remove person operation failed", ge);

		} catch (NotificationException ne) {
			// Eat up that & log the Exception
			Logger.getLogger(Roster.class).info("Roster.java: NotificationException thrown in removePerson() " + ne);
		}

		// Now do the delete

		DBBean db = new DBBean();
		try {
			// call Stored Procedure to insert or update all the tables involved
			// in storing a discussion group.
			db.prepareCall("begin PROFILE.REMOVE_PERSON_FROM_SPACE(?,?,?); end;");
			db.cstmt.setInt(1, Integer.parseInt(getSpaceID()));
			db.cstmt.setInt(2, Integer.parseInt(personID));
			db.cstmt.registerOutParameter(3, java.sql.Types.INTEGER);
			db.executeCallable();
			statusId = db.cstmt.getInt(3);

		} catch (SQLException sqle) {
			throw new PersistenceException("Error removing person from roster", sqle);

		} catch (NumberFormatException nfe) {
			throw new PersistenceException("ParseInt Failed in Roster.removePerson()", nfe);

		} finally {
			db.release();

		}

		try {
			// Handle (throw) any database exceptions
			DBExceptionFactory.getException("Roster.removePerson()", statusId);

		} catch (net.project.base.PnetException pe) {
			throw new PersistenceException("Roster.removePerson() threw an exception: ", pe);

		}

	}

	public void storePersonRosterProperties(Person person) {
		storePersonRosterProperties(person, false);
	}

	/**
	 * Save the specified Person's Space-specific information.
	 * 
	 * @param person
	 *            a <code>Person</code> object containing information that needs
	 *            to be saved.
	 */
	public void storePersonRosterProperties(Person person, boolean isSetRoleFromOutside) {
		if (person == null) {
			throw new NullPointerException("person is null");
		}

		if (person.getID() == null) {
			throw new NullPointerException("person_id is null");
		}

		StringBuffer sb = new StringBuffer();

		sb.append("update pn_space_has_person set member_title=" + DBFormat.varchar2(person.getTitle()));

		if (person.getResponsibilities() != null)
			sb.append(", responsibilities=" + DBFormat.varchar2(person.getResponsibilities()));

		sb.append(" where space_id = " + getSpaceID() + " and person_id=" + person.getID());

		DBBean db = new DBBean();
		try {
			db.setQuery(sb.toString());
			db.executeQuery();
			if (isSetRoleFromOutside) {
				GroupProvider groupProvider = new GroupProvider();
				ArrayList<String> roleList = new ArrayList<String>();
				String[] rolesId = person.getRoles();
				if (rolesId != null) {
					for (int index = 0; index < rolesId.length; index++) {
						if (StringUtils.isNotEmpty(rolesId[index])) {
							roleList.add(rolesId[index]);
						}
					}
					groupProvider.addMemberToGroups(person, roleList);
				}
			}
			db.commit();

		} catch (SQLException sqle) {
			Logger.getLogger(Roster.class).error("Roster.java: SQL Exception thrown in storePersonRosterProperties " + sqle);
		} catch (PersistenceException sqle) {
			Logger.getLogger(Roster.class).error("Roster.java: SQL Exception thrown in storePersonRosterProperties " + sqle);
		} catch (GroupException sqle) {
			Logger.getLogger(Roster.class).error("Roster.java: SQL Exception thrown in storePersonRosterProperties " + sqle);
		} finally {
			db.release();
		}
	}

	/** is the specified Space loaded */
	public boolean isLoaded(Space space) {
		if (!getSpaceID().equals(space.getID())) {
			return false;

		} else {
			return isLoaded();
		}
	}

	/**
	 * Reload the current roster list with existing search, sort and filter
	 * criteria.
	 */
	public void reload() {
		// clear the underlying list first so we don't get duplicate entries
		super.clear();
		load();
	}

	/**
	 * Searches the database for people who's first name or last name match the
	 * passed string in the current space.
	 * <p/>
	 * The specified key is saved so that it is available via
	 * {@link #getSearchKey()}.
	 * 
	 * @param key
	 *            the text fragment to search the people's first and last names
	 *            for.
	 * @throws NullPointerException
	 *             if the current space or its ID is null
	 */
	public void search(String key) {
		Person person;
		StringBuffer query = new StringBuffer();
		List bindList = new LinkedList();

		if (getSpaceID() == null) {
			setIsLoaded(false);
			throw new NullPointerException("Current space is null in Roster search");
		}

		if (key == null) {
			key = searchKey;
		}

		// Save the key so that it may be re-displayed
		searchKey = key;

		query.append(PERSON_SELECT_COLUMNS);
		query.append("from pn_space_has_person shp, pn_person_view p, pn_user_view u, pn_address a ");
		query.append("where shp.space_id = ? ");
		query.append("and p.person_id = shp.person_id and u.user_id(+) = p.person_id ");
		query.append("and p.address_id = a.address_id (+) and p.record_status = 'A' ");

		// First parameter is space id
		bindList.add(getSpaceID());

		if (key != null) {
			key = key.trim();
		}
		if ((key != null) && (key.length() > 0)) {
			// if the key is a single character, we will do an search for all
			// first names beginning with that character
			if (key.length() == 1) {
				key = key + "%";
				query.append("and UPPER(p.first_name) like UPPER(?) ");
				bindList.add(key);

			} else {
				key = "%" + key + "%";
				query.append(" and (UPPER(p.first_name) like UPPER(?) or UPPER(p.last_name) like UPPER(?) or UPPER(p.last_name||' '||p.first_name) like UPPER(?)) ");
				bindList.add(key);
				bindList.add(key);
				bindList.add(key);

			}
		}
		query.append("order by UPPER(p.first_name) asc,UPPER(p.last_name) asc ");

		DBBean db = new DBBean();
		try {
			int index = 0;
			Iterator it = bindList.iterator();

			db.prepareStatement(query.toString());

			// Bind all parameters to the statement
			while (it.hasNext()) {
				db.pstmt.setString(++index, (String) it.next());
			}

			db.executePrepared();

			while (db.result.next()) {
				person = new Person();
				populate(person, db.result);
				this.add(person);
			}
		} catch (SQLException sqle) {
			Logger.getLogger(Roster.class).error("Roster.java: SQL Exception thrown in search " + sqle);
		} finally {
			db.release();
		}
		// even if no rows are found by query, we still set isLoaded= true (0
		// results for search may be expected);
		setIsLoaded(true);
	}

	/**
	 * Load the roster from the database.
	 */
	public void load() {

		if (getSpaceID() == null) {
			setIsLoaded(false);
			throw new NullPointerException("must setSpace() before loading.");
		}

		// final String query = PERSON_SELECT_COLUMNS +
		// "from pn_space_has_person shp, pn_person_view p, pn_user_view u, pn_address a "
		// +
		// "where shp.space_id = " + getSpaceID() + " " +
		// "and p.person_id = shp.person_id " +
		// "and p.record_status = 'A' " +
		// "and u.user_id(+) = p.person_id " +
		// "and a.address_id(+) = p.address_id " +
		// "order by lower(p.first_name) asc, lower(p.last_name) asc ";

		final String query = "select " + "   p.person_id," + "   p.display_name," + "   p.first_name," + "   p.last_name," + "   pp.middle_name,"
				+ "   p.email," + "   p.image_id," + "   pp.timezone_code," + "   pp.skype," + "   p.user_status," + "   u.last_login," + "   u.username,"
				+ "   shp.responsibilities," + "   shp.member_title," + "   a.address_id," + "   a.office_phone," + "   a.fax_phone," + "   a.mobile_phone, "
				+ "   a.pager_phone, " + "   a.pager_email," + "   a.website_url, " + "   u.domain_id " + " from " + " pn_space_has_person shp,"
				+ " pn_person p, " + " pn_person_profile pp, " + " pn_user u, " + " pn_address a " + " where " + "  shp.space_id = " + getSpaceID()
				+ "  and p.person_id = shp.person_id " + "  and p.record_status = 'A' " + "  and u.user_id(+) = p.person_id "
				+ "  and a.address_id(+) = pp.address_id " + "  and p.person_id = pp.person_id (+) " + " order by " + "  lower(p.first_name) asc, "
				+ "  lower(p.last_name) asc ";

		DBBean db = new DBBean();
		try {

			db.setQuery(query);
			db.executeQuery();

			while (db.result.next()) {
				Person person = new Person();
				populate(person, db.result);
				this.add(person);
			}
			setIsLoaded(true);

		} catch (SQLException sqle) {
			sqle.printStackTrace();
			Logger.getLogger(Roster.class).error("Roster.java: SQL Exception thrown in load. " + sqle);

		} finally {
			db.release();
		}

		loadInvitationStatus();
	}

	public void loadForSpaces(ProjectPortfolio projectPortfolio) {
		if (projectPortfolio.size() > 0) {
			String query = "select DISTINCT" + "   p.person_id," + "   p.display_name," + "   p.first_name," + "   p.last_name," + "   pp.middle_name,"
					+ "   p.email," + "   p.image_id," + "   pp.timezone_code," + "   pp.skype," + "   p.user_status," + "   u.last_login," + "   u.username,"
					+ "   shp.responsibilities," + "   shp.member_title," + "   a.address_id," + "   a.office_phone," + "   a.fax_phone,"
					+ "   a.mobile_phone, " + "   a.pager_phone, " + "   a.pager_email," + "   a.website_url, " + "   u.domain_id " + " from "
					+ " pn_space_has_person shp," + " pn_person p, " + " pn_person_profile pp, " + " pn_user u, " + " pn_address a " + " where ";

			query = query.concat(" shp.space_id in ( ");

			int counter = 1;
			for (Iterator<IPortfolioEntry> it = projectPortfolio.iterator(); it.hasNext(); counter++) {
				query = query.concat("'");
				query = query.concat(it.next().getID());
				query = query.concat("' ");
				if (counter < projectPortfolio.size()) {
					query = query.concat(", ");
				}
			}
			query = query.concat(") ");

			String endQuery = "  and p.person_id = shp.person_id " + "  and p.record_status = 'A' " + "  and u.user_id(+) = p.person_id "
					+ "  and a.address_id(+) = pp.address_id " + "  and p.person_id = pp.person_id (+) " + " order by " + "  lower(p.first_name) asc, "
					+ "  lower(p.last_name) asc ";

			query = query.concat(endQuery);

			DBBean db = new DBBean();
			try {

				db.setQuery(query);
				db.executeQuery();

				while (db.result.next()) {
					Person person = new Person();
					populate(person, db.result);
					this.add(person);
				}
				setIsLoaded(true);

			} catch (SQLException sqle) {
				sqle.printStackTrace();
				Logger.getLogger(Roster.class).error("Roster.java: SQL Exception thrown in load. " + sqle);

			} finally {
				db.release();
			}

			loadInvitationStatus();
		} else{
			setIsLoaded(true);
		}

	}

	/**
	 * Populates the specified person with the column values appropriate for a
	 * person and the subset of Address fields appropriate for displaying in the
	 * roster.
	 * 
	 * @param person
	 *            the person to populate
	 * @param result
	 *            the resultset from which to get the column values
	 * @throws SQLException
	 *             if there is a problem populating
	 */
	protected void populate(Person person, ResultSet result) throws SQLException {
		Address address;

		person.populatePersonProperties(result);
		person.setResponsibilities(result.getString("responsibilities"));
		person.setTitle(result.getString("member_title"));

		// Address fields (partial)
		address = new Address();
		address.setID(result.getString("address_id"));
		address.officePhone = result.getString("office_phone");
		address.faxPhone = result.getString("fax_phone");
		address.mobilePhone = result.getString("mobile_phone");
		address.pagerPhone = result.getString("pager_phone");
		address.pagerEmail = result.getString("pager_email");
		address.websiteURL = result.getString("website_url");
		person.skype = result.getString("skype");
		// this is just enough address info for display in the roster, not fully
		// loaded.
		address.m_isLoaded = false;
		person.setAddress(address);
	}

	/**
	 * Loads the invitation status for all invited persons in the current space.
	 */
	private void loadInvitationStatus() {
		DBBean db = new DBBean();

		String query = "select person_id, invited_status from pn_invited_users where space_id = ?";

		if (getSpaceID() == null) {
			throw new NullPointerException("Space ID cannot be null");
		}

		try {
			invitedStatus.clear();

			int index = 0;

			db.prepareStatement(query);
			db.pstmt.setString(++index, getSpaceID());
			db.executePrepared();

			while (db.result.next()) {
				invitedStatus.put(db.result.getString("person_id"), InviteeStatus.getForID(db.result.getString("invited_status")));
			}
		} catch (SQLException sqle) {
			Logger.getLogger(Roster.class).error("Roster.loadInvitationStatus() threw an SQLException: " + sqle);
		} finally {
			db.release();
		}
	}

	public class InvitationStatusChecker {

		String checkStatus(String personID) {
			Person person = new Person();
			person.setID(personID);
			return checkStatus(person);
		}

		/**
		 * Returns the invitation status for the specified person, and populates
		 * some attributes of the person.
		 * <p/>
		 * These attributes are the only ones required for displaying in the
		 * roster
		 * 
		 * @param person
		 *            the person for whom to get the status, and to update
		 * @return the status
		 */
		public String checkStatus(Person person) {
			StringBuffer sb = new StringBuffer();
			int index = 0;
			String invitationStatus = null;
			DBBean db = new DBBean();

			try {
				db.openConnection();
				sb.append("select p.person_id , p.display_name , p.email email , p.user_status , ");
				sb.append(" pi.invited_status from pn_person p , pn_invited_users pi where ");
				sb.append(" p.person_id = ? and pi.space_id =? ");
				sb.append(" and p.person_id = pi.person_id ");

				db.prepareStatement(sb.toString());
				db.pstmt.setString(++index, person.getID());
				db.pstmt.setString(++index, Roster.this.getSpaceID());

				db.executePrepared();

				if (db.result.next()) {

					person.setID(db.result.getString("person_id"));
					person.setDisplayName(db.result.getString("display_name"));
					person.setEmail(db.result.getString("email"));
					person.setStatus(PersonStatus.getStatusForID(db.result.getString("user_status")));

					invitationStatus = db.result.getString("invited_status");
				}

			} catch (SQLException sqle) {
				Logger.getLogger(Roster.class).error("Roster.InvitationStatusChecker.checkStatus() threw an SQLException: " + sqle);
			} finally {
				db.release();
			}

			return invitationStatus;
		}
	}

	/**
	 * Creates an HTML option list of the rosters member's ID values are roster
	 * member's person id's.
	 * 
	 * @param personIDList
	 *            default selected option(s). This can be a single ID or a
	 *            comma-delimited list of ID's.
	 * @return the TML(Tapestry Markup language) option list
	 */
	public String getTMLSelectionList(String personIDList) {
		StringBuffer options = new StringBuffer();

		boolean isListed = false;

		for (int i = 0; i < size(); i++) {
			Person member = (Person) get(i);
			String memberStatus = new InvitationStatusChecker().checkStatus(member);
			options.append(getTMLOption(memberStatus, member));
			options.append(",");
		}

		if (!isListed && personIDList != null && !personIDList.trim().equals("")) {
			Person assignedPerson = new Person();
			assignedPerson.setID(personIDList);
			String str = new InvitationStatusChecker().checkStatus(assignedPerson);

			if ((str != null && str.equals("Deleted")) || (assignedPerson.getStatus() != null && assignedPerson.getStatus().getID().equals("Deleted"))) {
				options.append(assignedPerson.getID() + "=" + assignedPerson.getDisplayName() + " (" + InviteeStatus.DELETED.getName() + ")");
			}
		}
		return options.toString();
	}

	/**
	 * Returns an HTML option element for the specified person and status where
	 * that person is not selected.
	 * <p/>
	 * Note that Rejected or Deleted persons cause an empty string to be
	 * returned, since they will not be displayed when not selected.
	 * 
	 * @param memberStatus
	 *            the current status
	 * @param person
	 *            the person for whom to produce the option
	 * @return the option where the value is the person ID and the display is
	 *         the name
	 */
	private String getTMLOption(String memberStatus, Person person) {
		String option;
		if (memberStatus != null && memberStatus.equals("Rejected")) {
			option = "";
		} else if (person.getStatus() != null && person.getStatus().getID().equals("Deleted")) {
			option = "";
		} else {
			option = person.getID() + "=" + person.getDisplayName();
		}
		return option;
	}

	/**
	 * Method to get notification XML for directory
	 * 
	 * @return XML representation
	 */
	public static String getXMLMailBody(String displayName) {
		StringBuffer sb = new StringBuffer();
		String directoryUrl = SessionManager.getJSPRootURL() + "/" + Space.PROJECT_SPACE + "/DirectorySetup.jsp?module=" + Module.DIRECTORY + "&id="
				+ SessionManager.getUser().getCurrentSpace().getID();
		sb.append("<Participant>\n");
		sb.append("<Name>" + XMLUtils.escape(displayName) + "</Name>\n");
		sb.append("<URL>" + XMLUtils.escape(directoryUrl) + "</URL>\n");
		sb.append("</Participant>");
		return sb.toString();
	}

}
