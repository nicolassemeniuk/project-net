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

 package net.project.base;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.admin.RegistrationBean;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.resource.PersonList;
import net.project.resource.PersonStatus;

import org.apache.log4j.Logger;

/**
 * Provides directory lookup services for the Project.net Default directory (system directory).
 */
public class DefaultDirectory {

	  /**
     * Return true if user is found in this directory
     * @param email User's email address
     * @return true if user is found
     */
    public static boolean userExists(String email) {
        DBBean db = new DBBean();
        try {
        	
             String query = "select person_id from pn_person_view where user_status != ? " +
                " and ( lower(email) = lower(?)" +
                " or lower(alternate_email_1) = lower(?)" +
                " or lower(alternate_email_2) = lower(?)" +
                " or lower(alternate_email_3) = lower(?) )";
             boolean isValidUser = false;

	         db.prepareStatement(query);
	         db.pstmt.setString(1, PersonStatus.DELETED.getID());
	         db.pstmt.setString(2, email);
	         db.pstmt.setString(3, email);
	         db.pstmt.setString(4, email);
	         db.pstmt.setString(5, email);
	         db.executePrepared();
	
	         if (db.result.next()) {
	             isValidUser = true;
	         } else {
	             isValidUser = false;
	         }
     
	         return isValidUser;
         } catch (SQLException sqle) {
             throw new PnetRuntimeException("Unable to check whether user exists " +
                 "in database -- unexpected SQL Exception.", sqle);
         } finally {
             db.release();
         }
    }


    /**
     * Indicates whether there is a registered user using the specified
     * email address.
     * <p>
     * A registered user with the specified email address is defined
     * to exist if there is a person with that email address (primary
     * or alternate).
     * </p>
     * <p>
     * This does not return true if an invited but unregistered person
     * (stub person) has the email address.
     * </p>
     * @param email the email address to check
     * @return true if there is a registered user with that email address;
     * false otherwise (the user is not registered or there is no
     * user with the email address)
     */
    public static boolean isUserRegistered(String email) {

        // Locate the person with the specified email address
        Person person = lookupByEmail(email);

        // Determine whether that person is registered or a stub user
        // (or null)
        return isPersonRegistered(person);
    }

    /**
     * Indicates whether the personID specified has a valid user record.
     * @param userID
     * @return
     */
    public static boolean hasValidUserEntry (String userID) {

        DBBean db = new DBBean();
        String query = "select username from pn_user where user_id = ?";
        boolean isValidUser = false;

        try {

            db.prepareStatement(query);
            db.pstmt.setString(1, userID);
            db.executePrepared();

            if (db.result.next()) {
                isValidUser = true;
            } else {
                isValidUser = false;
            }

        } catch (SQLException sqle) {
            isValidUser = false;
        } finally {
            db.release();
        }

        return isValidUser;
    }

    /**
     * Return true if the user specified by this email address is registered
     * @param personID the ID of the person to check
     * @return true if user is registered, false if not
     */
    public static boolean isUserRegisteredByID(String personID) {

        Person person = lookupByID(personID);

        return isPersonRegistered(person);
    }


    /**
     * Indicates whether the specified person has completed registration.
     * A person is defined as registered if they have a status that
     * is not {@link net.project.resource.Person#UNREGISTERED_USER_STATUS}
     * nor {@link net.project.resource.Person#DELETED_USER_STATUS}.
     * @param person the person to check
     * @return true if the person is registered; false otherwise
     */
    public static boolean isPersonRegistered(Person person) {

        boolean isRegistered = false;

        if (person == null) {
            isRegistered = false;

        } else {
            String userStatus = person.getStatus().getID();

            if (userStatus == null ||
                    userStatus.equals(PersonStatus.UNREGISTERED.getID()) ||
                    userStatus.equals(PersonStatus.DELETED.getID())) {

                // Not registered if no status or unregistered
                // or deleted user
                isRegistered = false;

            } else {
                isRegistered = true;

            }

        }

        return isRegistered;
    }


    /**
     * Perform a directory lookup by email address.
     * <p>
     * Will not return users with a "Deleted" status.  It is assumed
     * that the email address is unique when ignoring case for
     * non-deleted persons.  This applies to both the primary and
     * all alternate email addresses.
     * </p>
     * <p>
     * Currently if more than one person is found with the same email
     * address, the first person is returned.
     * </p>
     * @param email the email address by which to locate the person
     * @return the loaded Person with the specified email address;
     * or null if there is no person with the specified email address
     */
    public static Person lookupByEmail(String email) {

        DBBean db = new DBBean();
        Person person = null;
        
        String query = "select person_id from pn_person_view where user_status != ? " +
        " and ( lower(email) = lower(?)" +
        " or lower(alternate_email_1) = lower(?)" +
        " or lower(alternate_email_2) = lower(?)" +
        " or lower(alternate_email_3) = lower(?) )";

        try {
            db.prepareStatement(query);
            db.pstmt.setString(1, PersonStatus.DELETED.getID());
            db.pstmt.setString(2, email);
            db.pstmt.setString(3, email);
            db.pstmt.setString(4, email);
            db.pstmt.setString(5, email);
            db.executePrepared();
            
            if (db.result.next()) {
                person = new Person(db.result.getString("person_id"));
                person.load();
            }

        } catch (java.sql.SQLException sqle) {
            Logger.getLogger(DefaultDirectory.class).debug("Directory.lookupByEmail() threw an SQLE: " + sqle);
            person = null;

        } catch (net.project.persistence.PersistenceException pe) {
        	Logger.getLogger(DefaultDirectory.class).debug("Directory.lookupByEmail() threw a PersistenceException: " + pe);
            person = null;

        } finally {
            db.release();
        }

        return person;
    }


    /**
     * Perform a directory lookup by person's database ID.
     * @return the Person object (loaded from database) that matches the person's ID in the database, null if there is no match.
     */
    public static Person lookupByID(String personID) {
        Person person = null;

        if (personID == null || personID.trim().equals("")) {
            return null;
        }

        try {

            person = new Person();
            person.setID(personID);
            person.load();

        } catch (net.project.persistence.PersistenceException pe) {
            person = null;
        }

        return person;
    }


    /**
     * Get the email address for a personID.
     * @deprecated as of 7.5.1; no replacement
     * This method is unused and therefore is unsupported
     */
    public static String getEmailForID(String personID) {

        DBBean db = new DBBean();
        String email = null;

        try {
            db.executeQuery("select email from pn_person where person_id = " + personID);

            if (db.result.next()) {
                email = db.result.getString("email");
            }
        } catch (java.sql.SQLException sqle) {
        	Logger.getLogger(DefaultDirectory.class).debug("Directory.getEmailForID() threw an SQLE: " + sqle);
        } finally {
            db.release();
        }

        return email;
    }


    /**
     * Checks whether or not a person already exists in the application
     * for the specified email address.
     * @param email the email address to check
     * @return true if a person with the email address already exists;
     * false otherwise
     * @see #checkEmailExists(String, DBBean)
     */
    public static boolean checkEmailExists(String email) {

        boolean isExisting = false;
        DBBean db = new DBBean();

        try {
            db.setAutoCommit(false);
            isExisting = checkEmailExists(email, db);
            db.commit();

        } catch (java.sql.SQLException sqle) {
            // Suppresses error messages
            // Simply returns false
        	Logger.getLogger(DefaultDirectory.class).error("DefaultDirectory.checkEmailExists() threw an SQLException:  " + sqle);
            isExisting = false;

        } finally {
            try {
                db.rollback();
            } catch (java.sql.SQLException sqle) {
                // Simply release
            }
            db.release();

        }

        return isExisting;
    }


    /**
     * Checks whether or not a person already exists in the application
     * for the specified email address.
     * Allows the specification of a DBBean in which to perform the
     * read operation. No COMMIT or ROLLBACK is performed.
     * <p>
     * An email is defined to exist if there is a person with that
     * email address (primary or alternate), including invited but
     * unregistered persons (stub persons).
     * </p>
     * <p>
     * If this method returns true, use {@link #isUserRegistered} to
     * determine whether the email address belongs to a registered
     * or unregistered user.
     * </p>
     * @param email the email address to check
     * @param db the DBBean in which to perform the check
     * @return true if a person with the email address already exists;
     * false otherwise
     */
    public static boolean checkEmailExists(String email, DBBean db) {

        boolean exists = true;

        String query = "select count (person_id) as count from pn_person_view " +
                " where user_status != '" + PersonStatus.DELETED.getID() + "'" +
                " and ( lower(email) = lower('" + email.trim() + "')" +
                " or lower(alternate_email_1) = lower('" + email.trim() + "')" +
                " or lower(alternate_email_2) = lower('" + email.trim() + "')" +
                " or lower(alternate_email_3) = lower('" + email.trim() + "') )";

        try {
            db.executeQuery(query);

            if (db.result.next()) {

                if (!db.result.getString("count").equals("0")) {
                    exists = true;

                } else {
                    exists = false;

                }

            } else {
                exists = false;

            }

        } catch (SQLException sqle) {
        	Logger.getLogger(DefaultDirectory.class).error("DefaultDirectory.checkEmailExists() threw an SQLException:  " + sqle);
            exists = true;
        }

        return exists;
    }

    /**
     * Checks whether any other person , other than the one identified by the ID is using the email ID
     *
     * @param email email the email address to check
     * @param personID the person who is not be checked
     * @return true if a person with the email address already exists
     * @exception PersistenceException
     */
    public static boolean checkEmailExistsExceptSpecified(String email, String personID)
            throws PersistenceException {

        boolean exists = true;
        StringBuffer query = new StringBuffer();
        
        DBBean dbean = new DBBean();
        try {
            query.append("select count (person_id) as count from pn_person_view ");
            query.append(" where person_id <> ? and user_status != '" + PersonStatus.DELETED.getID() + "'");
            query.append(" and ( lower(email) = lower('" + email.trim() + "')");
            query.append(" or lower(alternate_email_1) = lower('" + email.trim() + "')");
            query.append(" or lower(alternate_email_2) = lower('" + email.trim() + "')");
            query.append(" or lower(alternate_email_3) = lower('" + email.trim() + "'))");

            int index = 0;

            dbean.prepareStatement(query.toString());
            dbean.pstmt.setInt(++index, Integer.parseInt(personID));

            dbean.executePrepared();

            if (dbean.result.next()) {

                if (!dbean.result.getString("count").equals("0")) {
                    exists = true;

                } else {
                    exists = false;

                }

            } else {
                exists = false;

            }

        } catch (SQLException sqle) {
        	Logger.getLogger(DefaultDirectory.class).error("DefaultDirectory.checkEmailExists() threw an SQLException:  " + sqle);
            throw new PersistenceException("Check email operation failed: " + sqle, sqle);

        } finally {
            dbean.release();
        }

        return exists;
    }


    /**
     * Get the person's display name for the specified personID from the directory.
     * @param personID  the id of the person for whom to get their name
     * @return the person's display name for the passed person's database ID, null if there is no match.
     */
    public static String getDisplayNameByID(String personID) {

        DBBean db = new DBBean();
        String fullName = null;

        try {
            db.executeQuery("select display_name from pn_person where person_id=" + personID);

            if (db.result.next()) {
                fullName = db.result.getString("display_name");
            }

        } catch (java.sql.SQLException sqle) {
        	Logger.getLogger(DefaultDirectory.class).error("DefaultDirectory.getDisplayNameByID threw an SQLException: " + sqle);

        } finally {
            db.release();

        }

        return fullName;
    }

    /**
     * Returns a PersonList containing every Person.
     * @return a list containing every Person
     * @throws PersistenceException if there is a problem loading
     */
    public static PersonList getAllUsersPersonList() throws PersistenceException {

        PersonList personList = new PersonList();
        DBBean db = new DBBean();

        try {
            loadAllPeople(personList, db);
        } finally {
            db.release();
        }

        return personList;
    }


    /**
     * Load all persons in the database into the personList data structure specified.
     * @param personList A personList data
     * @param db an already instantiated DBBean
     */
    public static void loadAllPeople(PersonList personList, DBBean db) throws PersistenceException {
        Person person = null;
        StringBuffer query = new StringBuffer();

        query.append("select p.person_id, p.display_name, p.first_name, p.last_name, p.middle_name, ");
        query.append("p.email, p.timezone_code, p.user_status, p.has_license, p.company_name, p.address_id, u.last_login, u.username ");
        query.append("from pn_person_view p, pn_user_view u ");
        query.append("where u.user_id(+) = p.person_id ");
        query.append("order by lower(last_name), lower (first_name) asc ");

        try {
            db.prepareStatement(query.toString());
            db.executePrepared();

            //Populate the PersonList with our result set.
            while (db.result.next()) {

                person = new Person();

                person.setID(db.result.getString("person_id"));
                person.setUserName(db.result.getString("username"));
                person.setDisplayName(db.result.getString("display_name"));
                person.setFirstName(db.result.getString("first_name"));
                person.setMiddleName(db.result.getString("middle_name"));
                person.setLastName(db.result.getString("last_name"));
                person.setLicensed(net.project.util.Conversion.toBoolean(db.result.getString("has_license")));
                person.setEmail(db.result.getString("email"));
                person.setTimeZoneCode(db.result.getString("timezone_code"));
                person.setStatus(PersonStatus.getStatusForID(db.result.getString("user_status")));
                person.setLastLogin(db.result.getTimestamp("last_login"));

                personList.add(person);
            }

            //Let the Personlist know that data is loaded
            personList.setIsLoaded(true);
        } catch (SQLException sqle) {
            throw new PersistenceException("DefaultDirectory.loadAllPeople() threw a SQL Exception", sqle);
        } finally {
            db.release();
        }
    }


    /**
     * Load people from the database matching the passed filter keyword.
     * The keyword can be the complete first name, complete last name, or first and last name separated by a space.
     * The keyword can also be a person's email address.
     * If the keyword is a single character, all the last names beginning with that character will be returned.
     * @param personList an instantiated person list object
     * @param filter the directory filter options
     * @param db an instantiated DBBean object
     */
    public static void loadFilteredPeople(PersonList personList, DirectoryFilter filter, String sortOrder, DBBean db) throws PersistenceException {

        StringBuffer query = new StringBuffer();
        ArrayList bindList = new ArrayList();
        String keywordFilter = filter.getKeywordFilter();
        String[] userStatusFilter = filter.getUserStatusFilter();
        String[] userDomainFilter = filter.getUserDomainFilter();
        String[] licenseFilter = filter.getLicenseStatusCodeFilter();

        if (keywordFilter == null) {
            throw new PersistenceException("DefaultDirectory.loadFilteredPeople(): No filter was specified");
        }

        query.append("select ");
        query.append("  p.person_id, p.display_name, p.first_name, p.last_name, p.middle_name, p.email, ");
        query.append("  p.alternate_email_1, p.alternate_email_2, p.alternate_email_3, p.timezone_code, ");
        query.append("  p.user_status, p.company_name, p.address_id, u.last_login, u.username, u.domain_id, p.has_license, ");
        query.append("  l.license_status ");
        query.append("from ");
        query.append("  pn_person_view p, pn_user_view u, pn_person_has_license phl, pn_license l ");
        query.append("where ");
        query.append("  u.user_id(+) = p.person_id ");
        query.append("  and p.person_id = phl.person_id(+) ");
        query.append("  and phl.license_id = l.license_id(+) ");

        if (keywordFilter.length() > 0) {

            // if the keywordFilter is a single character, we will do an search for all last names beginning with that character
            if (keywordFilter.length() == 1) {
                keywordFilter = keywordFilter + "%";
                query.append(" and UPPER(p.last_name) like UPPER(?) ");
                bindList.add(keywordFilter);
            } else {
                keywordFilter = "%" + keywordFilter + "%";
                query.append(" and (UPPER(p.last_name) like UPPER(?) or UPPER(p.first_name) like UPPER(?) or UPPER(p.first_name||' '||p.last_name) like UPPER(?) or UPPER(p.email) like UPPER(?) or UPPER(p.alternate_email_1) like UPPER(?) or UPPER(p.alternate_email_2) like UPPER(?) or UPPER(p.alternate_email_3) like UPPER(?)) ");
                bindList.add(keywordFilter);
                bindList.add(keywordFilter);
                bindList.add(keywordFilter);
                bindList.add(keywordFilter);
                bindList.add(keywordFilter);
                bindList.add(keywordFilter);
                bindList.add(keywordFilter);
            }
        }

        // now add userStatusFilters
        if (userStatusFilter != null && userStatusFilter.length > 0) {

            query.append(" and (");

            for (int i = 0; i < userStatusFilter.length; i++) {

                // first addition to the query, no "or" is needed
                if (i == 0) {
                    query.append("p.user_status = '" + userStatusFilter[i] + "'");
                } else {
                    query.append(" or p.user_status = '" + userStatusFilter[i] + "'");
                }
            }

            query.append(")");
        }

        // now add domain filters
        if (userDomainFilter != null && userDomainFilter.length > 0) {

            query.append(" and (");

            for (int i = 0; i < userDomainFilter.length; i++) {

                // first addition to the query, no "or" is needed
                if (i == 0) {
                    query.append("u.domain_id = '" + userDomainFilter[i] + "'");
                } else {
                    query.append(" or u.domain_id = '" + userDomainFilter[i] + "'");
                }
            }

            query.append(")");
        }

        //Add filters to show only active, cancelled, or disabled users.  We also
        //allow a "null" filter to be specified.  This is equivalent to a user that
        //has not yet dealt with licensing and has not been assigned a row in the
        //pn_person_has_license class.
        if (licenseFilter != null && licenseFilter.length > 0) {
            boolean showNulls = false;

            query.append(" and (l.license_status in (");

            if (licenseFilter.length > 0) {
                query.append(licenseFilter[0]);
            }

            for (int i = 1; i < licenseFilter.length; i++) {
                //See if the user has added a null filter.  If they did, we cannot insert
                //it in the "in" list.  Instead, we will add it below.
                if (licenseFilter[i] == null) {
                    showNulls = true;
                    continue;
                }

                query.append(",").append(licenseFilter[i]);
            }

            //Close the "in" statement
            query.append(")");

            //If the user has specified a null filter, show all users that don't
            //have an entry in pn_person_has_license.
            if (showNulls) {
                query.append(" or (l.license_status is null)");
            }

            query.append(")");
        }

        // FINALLY, set the sort order
        if (sortOrder != null && !sortOrder.equals("")) {
            //query.append(" order by " + sortOrder + " asc");
            // We get the asc/desc order in the sortorder parameter itself.
            query.append(" order by " + sortOrder);
        } else {
            query.append(" order by lower (p.last_name), lower (p.first_name), p.user_status asc");
        }


        // now that we have the query constructed, run it.
        populateFilteredPeople(personList, query, bindList, db);
    }

    /**
     * Changes the status of a person
     * @param personID the ID of the person to be modified
     * @param userStatus the status to change the user to
     */
    public static void changePersonStatus(String personID, String userStatus) throws PersistenceException {

        Person person = lookupByID(personID);
        changePersonStatus(person, userStatus);


    }

    /**
     * Changes the status of a person
     * @param person the person to be modified
     * @param userStatus the status to change the user to
     */
    public static void changePersonStatus(Person person, String userStatus) throws PersistenceException {

        // If we aren't trying to delete the user, just change their status,
        // otherwise remove the user
        if (!userStatus.equals(PersonStatus.DELETED.getID())) {
            person.changeStatus(userStatus);
        } else {
            net.project.security.User user = new net.project.security.User(person);
            user.remove();
        }
    }


    /**
     * Creates a person stub entry.
     * The resulting stub must still be registered and a profile created.<br>
     * <b>Note:</b> This does NOT release the DBBean.
     * @param person the populated Person
     * @param db DBBean instance in which to perform the transaction
     * @throws SQLException if a database problem occurs
     */
    public static void createPersonStub(Person person, DBBean db) throws SQLException {
        db.prepareCall("{call PROFILE.CREATE_PERSON_STUB(?, ?, ?, ?, ?, ?)}");
        db.cstmt.setString(1, person.getEmail().trim());
        db.cstmt.setString(2, person.getFirstName());
        db.cstmt.setString(3, person.getLastName());
        db.cstmt.setString(4, person.getFirstName() + " " + person.getLastName());
        db.cstmt.setString(5, person.getStatus().getID());
        db.cstmt.registerOutParameter(6, java.sql.Types.VARCHAR);
        db.executeCallable();
        person.setID(db.cstmt.getString(6));
    }


    /**
     * Creates a profile for an existing stub user.
     * Should probably be swapped with an IProfile instead of RegistrationBean
     * @param register profile IProfile object containing required info for profile creation
     * @throws PersistenceException if db operation fails
     */
    public static void createPersonProfile(RegistrationBean register) throws PersistenceException {

        DBBean db = new DBBean();

        try {
            // call create person profile with the member DBBean
            createPersonProfile(register, db);

        } finally {
            db.release();
        }
    }

    /**
     * Creates a profile for an existing stub user.
     * Should probably be swapped with an IProfile instead of RegistrationBean
     * @param register profile IProfile object containing required info for profile creation
     * @throws PersistenceException if db operation fails
     */
    public static void createPersonProfile(RegistrationBean register, DBBean db) throws PersistenceException {

        int errorCode = -1;
        int statusIndex = -1;

        try {

            // call Stored Procedure to create a new user.
            db.prepareCall("begin  profile.CREATE_PERSON_PROFILE (?,?,?,?,?,?,?,   ?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?, ?); end;");

            int index = 0;

            db.cstmt.setString(++index, register.getID());
            db.cstmt.setString(++index, register.getFirstName());
            db.cstmt.setString(++index, register.getLastName());
            db.cstmt.setString(++index, register.getDisplayName());
            db.cstmt.setString(++index, register.getLogin());
            db.cstmt.setString(++index, register.getEmail().toLowerCase().trim());
            if (register.getAlternateEmail1() == null) {
                db.cstmt.setString(++index, null);            	
            } else {
                db.cstmt.setString(++index, register.getAlternateEmail1().trim());            	
            }
            if (register.getAlternateEmail2() == null) {
                db.cstmt.setString(++index, null);            	
            } else {
                db.cstmt.setString(++index, register.getAlternateEmail2().trim());            	
            }
            if (register.getAlternateEmail3() == null) {
                db.cstmt.setString(++index, null);            	
            } else {
                db.cstmt.setString(++index, register.getAlternateEmail3().trim());            	
            }
            db.cstmt.setString(++index, register.getNamePrefix());
            db.cstmt.setString(++index, register.getMiddleName());
            db.cstmt.setString(++index, ""); // SECOND LAST NAME
            db.cstmt.setString(++index, register.getNameSuffix());
            db.cstmt.setString(++index, register.getLocaleCode());
            db.cstmt.setString(++index, register.getLanguageCode());
            db.cstmt.setString(++index, register.getTimeZoneCode());
            //db.cstmt.setString(++index, register.getDateFormatID());
            db.cstmt.setString(++index, register.getVerificationCode());

            db.cstmt.setString(++index, register.getAddress1());
            db.cstmt.setString(++index, register.getAddress2());
            db.cstmt.setString(++index, register.getAddress3());
            db.cstmt.setString(++index, register.getAddress4());
            db.cstmt.setString(++index, register.getAddress5());
            db.cstmt.setString(++index, register.getAddress6());
            db.cstmt.setString(++index, register.getAddress7());
            db.cstmt.setString(++index, register.getCity());
            db.cstmt.setString(++index, register.getCityDistrict());
            db.cstmt.setString(++index, register.getRegion());

            db.cstmt.setString(++index, register.getState());
            db.cstmt.setString(++index, register.getCountry());
            db.cstmt.setString(++index, register.getZipcode());
            db.cstmt.setString(++index, register.getOfficePhone());
            db.cstmt.setString(++index, register.getFaxPhone());

            // register status ID
            db.cstmt.registerOutParameter((statusIndex = ++index), java.sql.Types.INTEGER);

            // execute the stored proc
            db.executeCallable();
            errorCode = db.cstmt.getInt(statusIndex);

            net.project.database.DBExceptionFactory.getException("DefaultDirectory.createPersonProfile()", errorCode);
        } catch (SQLException sqle) {
        	Logger.getLogger(DefaultDirectory.class).error("SQL exception: " + sqle);
            throw new PersistenceException("DefaultDirectory.createPersonProfile(): threw an SQLException.", sqle);
        } catch (net.project.base.PnetException pe) {
        	Logger.getLogger(DefaultDirectory.class).error("DefaultDirectory.createPersonProfile() had problems: " + pe);
            throw new PersistenceException("Unexpected error in DefaultDirectory.createPersonProfile()", pe);
        } catch (Exception e) {
        	Logger.getLogger(DefaultDirectory.class).error("Unable to Encrypt user info: " + e);
            throw new PersistenceException("Unexpected error in DefaultDirectory.createPersonProfile()", e);
        }
    }


    /**
     * Creates a complete profile for a user -- can only be used when a stub user does not exist
     * Should probably be swapped with an IProfile instead of RegistrationBean
     * @param register profile IProfile object containing required info for profile creation
     * @throws PersistenceException if db operation fails
     */
    public static void createPerson(RegistrationBean register) throws PersistenceException {

        DBBean db = new DBBean();

        try {
            // call create person with the member DBBean
            createPerson(register, db);

        } finally {
            db.release();
        }
    }

    /**
     * Creates a complete profile for a user -- can only be used when a stub user does not exist
     * Should probably be swapped with an IProfile instead of RegistrationBean
     * @param register profile IProfile object containing required info for profile creation
     * @throws PersistenceException if db operation fails
     */
    public static void createPerson(RegistrationBean register, DBBean db) throws PersistenceException {

        int errorCode = -1;
        int newPersonIDIndex = -1;
        int statusIndex = -1;

        try {

            // call Stored Procedure to create a new user.
            db.prepareCall("begin  profile.CREATE_PERSON (?,?,?,?,?,?,?,  ?,?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?); end;");

            int index = 0;

            db.cstmt.setString(++index, register.getLogin());
            db.cstmt.setString(++index, register.getEmail());
            db.cstmt.setString(++index, register.getAlternateEmail1());
            db.cstmt.setString(++index, register.getAlternateEmail2());
            db.cstmt.setString(++index, register.getAlternateEmail3());
            db.cstmt.setString(++index, register.getNamePrefix());
            db.cstmt.setString(++index, register.getFirstName());

            db.cstmt.setString(++index, register.getMiddleName());
            db.cstmt.setString(++index, register.getLastName());
            db.cstmt.setString(++index, ""); // SECOND LAST NAME
            db.cstmt.setString(++index, register.getNameSuffix());
            db.cstmt.setString(++index, register.getDisplayName());
            db.cstmt.setString(++index, register.getLocaleCode());
            db.cstmt.setString(++index, register.getLanguageCode());
            db.cstmt.setString(++index, register.getTimeZoneCode());
            // db.cstmt.setString(++index, register.getDateFormatID());
            db.cstmt.setString(++index, register.getVerificationCode());
            db.cstmt.setString(++index, register.getAddress1());

            db.cstmt.setString(++index, register.getAddress2());
            db.cstmt.setString(++index, register.getAddress3());
            db.cstmt.setString(++index, register.getAddress4());
            db.cstmt.setString(++index, register.getAddress5());
            db.cstmt.setString(++index, register.getAddress6());
            db.cstmt.setString(++index, register.getAddress7());
            db.cstmt.setString(++index, register.getCity());
            db.cstmt.setString(++index, register.getCityDistrict());
            db.cstmt.setString(++index, register.getRegion());
            db.cstmt.setString(++index, register.getState());

            db.cstmt.setString(++index, register.getCountry());
            db.cstmt.setString(++index, register.getZipcode());
            db.cstmt.setString(++index, register.getOfficePhone());
            db.cstmt.setString(++index, register.getFaxPhone());
            // Default to UNCONFIRMED user status if the status has not already been set.
            PersonStatus status = register.getStatus();
            if (status != null) {
                db.cstmt.setString(++index, status.getID());
            } else {
                db.cstmt.setString(++index, PersonStatus.UNCONFIRMED.getID());
            }
            // new person id
            db.cstmt.registerOutParameter((newPersonIDIndex = ++index), java.sql.Types.INTEGER);

            // register  ID
            db.cstmt.registerOutParameter((statusIndex = ++index), java.sql.Types.INTEGER);

            // execute the stored proc
            db.executeCallable();

            errorCode = db.cstmt.getInt(statusIndex);
            net.project.database.DBExceptionFactory.getException("DefaultDirectory.createPerson()", errorCode);

            // assuming no error was thrown set the id
            register.setID(db.cstmt.getString(newPersonIDIndex));
        } catch (SQLException sqle) {
        	Logger.getLogger(DefaultDirectory.class).error("SQL exception: " + sqle);
            throw new PersistenceException("DefaultDirectory.createPerson(): threw an SQLException: " + sqle, sqle);
        } catch (net.project.base.PnetException pe) {
        	Logger.getLogger(DefaultDirectory.class).error("DefaultDirectory.createPerson() had problems: " + pe);
            throw new PersistenceException("Unexpected error in DefaultDirectory.createPerson(): " + pe, pe);
        } catch (Exception e) {
        	Logger.getLogger(DefaultDirectory.class).error("Unable to Encrypt user info: " + e);
            throw new PersistenceException("Unexpected error in DefaultDirectory.createPerson(): " + e, e);
        }
    }


    /**
     * Search for people who's first name or last name match the passed query.
     * @param personList an instantiated person list object
     * @param query the query to execute
     * @param bindList the array list of bindings for the query object
     * @param db an instantiated DBBean object
     */
    private static void populateFilteredPeople(PersonList personList, StringBuffer query, ArrayList bindList, DBBean db) throws PersistenceException {
        Person person = null;

        try {
            int index = 0;
            Iterator it = bindList.iterator();

            db.prepareStatement(query.toString());
            while (it.hasNext()) {
                db.pstmt.setString(++index, (String) it.next());
            }
            db.executePrepared();

            //Populate the PersonList with our result set.
            while (db.result.next()) {

                person = new Person();

                person.setID(db.result.getString("person_id"));
                person.setUserName(db.result.getString("username"));
                person.setDisplayName(db.result.getString("display_name"));
                person.setFirstName(db.result.getString("first_name"));
                person.setMiddleName(db.result.getString("middle_name"));
                person.setLastName(db.result.getString("last_name"));
                person.setLicensed(net.project.util.Conversion.toBoolean(db.result.getString("has_license")));
                person.setEmail(db.result.getString("email"));
                person.setTimeZoneCode(db.result.getString("timezone_code"));
                person.setStatus(PersonStatus.getStatusForID(db.result.getString("user_status")));

                person.setLastLogin(db.result.getTimestamp("last_login"));
                person.setDomaninID(db.result.getInt("domain_id"));
                personList.add(person);
            }

            //Let the Personlist know that data is loaded
            personList.setIsLoaded(true);

        } catch (SQLException sqle) {
            throw new PersistenceException("DefaultDirectory.loadFilteredPeople () threw a SQL Exception", sqle);

        } finally {
            db.release();
        }
    }

    /**
     * Get the ID of the<code>Person</code> with the specified email address
     *
     * @param email  the specified email address
     * @return the ID of the Person
     */
    public static String getPersonIDForEmail(String email) {

        DBBean db = new DBBean();
        StringBuffer query = new StringBuffer();
        String personID = null;

        if (email == null) {
            // returns null if the email that has been passed
            // on as parameter is null
            return null;
        }

        query.append("select person_id from pn_person_view ");
        query.append("where lower(email) = lower(?) ");
        query.append(" and user_status != '" + PersonStatus.DELETED.getID() + "'");
        query.append(" or lower(alternate_email_1) =  lower(?) ");
        query.append(" or lower(alternate_email_2) =  lower(?) ");
        query.append(" or lower(alternate_email_3) =  lower(?) ");

        try {
            int index = 0;
            db.prepareStatement(query.toString());

            db.pstmt.setString(++index, email);
            db.pstmt.setString(++index, email);
            db.pstmt.setString(++index, email);
            db.pstmt.setString(++index, email);

            db.executePrepared();

            if (db.result.next()) {
                personID = db.result.getString("person_id");
            }

        } catch (java.sql.SQLException sqle) {
        	Logger.getLogger(DefaultDirectory.class).debug("DefaultDirectory.getIDForEmail() threw an SQLE: " + sqle);
        } finally {
            db.release();
        }

        return personID;
    }

    /**
     * Get the ID of the<code>User</code> with the specified email address
     *
     * @param email  the specified email address
     * @return the ID of the User
     */
    public static String getUserIDForEmail(String email) {

        DBBean db = new DBBean();
        StringBuffer query = new StringBuffer();
        String personID = null;

        if (email == null) {
            // returns null if the email that has been passed
            // on as parameter is null
            return null;
        }

        query.append("select p.person_id from pn_person_view p ");
        query.append("where (lower(email) = lower(?) ");
        query.append(" or lower(alternate_email_1) =  lower(?) ");
        query.append(" or lower(alternate_email_2) =  lower(?) ");
        query.append(" or lower(alternate_email_3) =  lower(?) )  ");
        query.append(" and user_status != '" + PersonStatus.DELETED.getID() + "'");
        query.append(" and EXISTS (select u.user_id from pn_user u where u.user_id = p.person_id) ");

        try {
            int index = 0;
            db.prepareStatement(query.toString());

            db.pstmt.setString(++index, email);
            db.pstmt.setString(++index, email);
            db.pstmt.setString(++index, email);
            db.pstmt.setString(++index, email);

            db.executePrepared();

            if (db.result.next()) {
                personID = db.result.getString("person_id");
            }

        } catch (java.sql.SQLException sqle) {
        	Logger.getLogger(DefaultDirectory.class).debug("DefaultDirectory.getIDForEmail() threw an SQLE: " + sqle);
        } finally {
            db.release();
        }

        return personID;
    }

}

