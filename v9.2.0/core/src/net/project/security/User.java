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

 package net.project.security;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import net.project.base.PnetException;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.document.DocumentManager;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.resource.Address;
import net.project.resource.PersonStatus;
import net.project.resource.UserActivityStatus;
import net.project.security.domain.UserDomain;
import net.project.security.group.GroupTypeID;
import net.project.space.Space;
import net.project.util.Conversion;
import net.project.xml.XMLFormatter;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;


/**
 * The current user of the application. This class should only be used for User
 * authentication, access checks and User context within other object. Use the
 * net.project.resource.Person class for referring to people other than the
 * current user of the application.
 * 
 * @author unascribed
 * @author Tim
 * @since v1
 */
public class User extends net.project.resource.Person implements IJDBCPersistence, IXMLPersistence, Serializable {

	//
	// Static members
	//

	/**
	 * Returns a query for loading a user. Includes a WHERE clause that contains
	 * joins
	 * 
	 * @return the load query
	 */
	protected static String getLoadQuery() {
		StringBuffer query = new StringBuffer();

		query.append("select p.person_id, p.prefix_name, p.first_name, p.middle_name, p.last_name, p.suffix_name, p.display_name, ");
		query.append("p.email, p.alternate_email_1, p.alternate_email_2, p.alternate_email_3, p.skype, p.skills_bio, ");  
		query.append("p.company_name, p.company_division, p.job_description_code, p.verification_code, ");
		query.append("p.locale_code, p.language_code, p.timezone_code, ");
		query.append("p.user_status, p.membership_portfolio_id, p.has_license, p.image_id, ");
		query.append("a.country_code, a.address_id, ");
		query.append("u.username, u.domain_id, u.last_login as login_date, u.last_brand_id, ");
		query.append("pna.delivery_address as default_notification_address, pna.delivery_type_id as default_delivery_type ");
		query.append("from pn_person_view p, pn_user_view u, pn_address a, pn_person_notification_address pna ");
		query.append("where u.user_id(+) = p.person_id ");
		query.append("and a.address_id = p.address_id ");
		query.append("and pna.person_id = p.person_id and pna.is_default = 1 ");

		return query.toString();
	}

	/**
	 * Populates a User object from the current row of the specified result set.
	 * 
	 * @param result
	 *            the ResultSet to populate the user from
	 * @param user
	 *            the user to populate
	 * @throws SQLException
	 *             if there is a problem getting a column value from the result
	 *             set
	 * @throws PersistenceException
	 *             if the user has no country or language code
	 */
	protected static void populate(java.sql.ResultSet result, User user) throws SQLException, PersistenceException {

		// User properties
		user.setID(result.getString("person_id"));
		user.username = result.getString("username");
		user.prefixName = result.getString("prefix_name");
		user.firstName = result.getString("first_name");
		user.middleName = result.getString("middle_name");
		user.lastName = result.getString("last_name");
		user.suffixName = result.getString("suffix_name");
		user.hasLicense = net.project.util.Conversion.toBoolean(result.getString("has_license"));
		user.setDisplayName(result.getString("display_name"));
		user.setEmail(result.getString("email"));
		user.alternateEmail1 = result.getString("alternate_email_1");
		user.alternateEmail2 = result.getString("alternate_email_2");
		user.alternateEmail3 = result.getString("alternate_email_3");
		user.skype = result.getString("skype");
		user.skillsBio = result.getString("skills_bio");
		user.imageId=result.getInt("image_id");


		user.setLastLogin(result.getTimestamp("login_date"));
		user.lastBrandID = result.getString("last_brand_id");
		user.defaultNotificationAddress = result.getString("default_notification_address");
		user.defaultNotificationDeliveryType = result.getString("default_delivery_type");

		user.verificationCode = result.getString("verification_code");
		user.setStatus(PersonStatus.getStatusForID(result.getString("user_status")));
		user.personalPortfolioID = result.getString("membership_portfolio_id");

		// Set the domain against which the user is registered
		user.setUserDomain(result.getString("domain_id"));

		//
		// Internationalization properties
		//

		user.localeCode = result.getString("locale_code");
		user.languageCode = result.getString("language_code");
		user.countryCode = result.getString("country_code");

		if ((user.localeCode != null) && !user.localeCode.equals("")) {
			try {
				user.locale = net.project.util.LocaleProvider.getLocale(user.localeCode);

			} catch (net.project.util.InvalidLocaleException ile) {
				throw new PersistenceException(ile.getMessage(), ile);
			}

		} else {
			Logger.getLogger(User.class).debug("User.load() failed: Missing locale. ");
			throw new PersistenceException("Failed to load user; missing country or language code");
		}

		// Set the timezone
		if (result.getString("timezone_code") != null) {
			String timeZoneID = result.getString("timezone_code");
			user.setTimeZoneCode(timeZoneID);
		}

		//
		// End of Internationalization properties
		//

		user.initDateFormatter();

		// User's number format for locale-specific numbers and currency.
		user.initNumberFormatter();

		// Load User's Address properties
		if (user.address == null) {
			user.address = new Address();
		} else {
			user.address.clear();
		}

		user.address.setID(result.getString("address_id"));
		user.address.load();

	}

	//
	// Instance members
	//

	/*
	 * ------------------------------- User Properties
	 * -------------------------------
	 */

	/** The username (login name) of the user */
	protected String username = null;

	/** the creation date of the user */
	protected Date creationDate = null;

	/** The user's verification code */
	protected String verificationCode = null;

	/** The ID of the users personal Portfolio */
	protected String personalPortfolioID = null;

	/** Alternate email address 1 */
	protected String alternateEmail1 = null;

	/** Alternate email address 2 */
	protected String alternateEmail2 = null;

	/** Alternate email address 3 */
	protected String alternateEmail3 = null;
	
	/** Skype */
	protected String skype = null;
	
	/** Skill / Bio */
	protected String skillsBio = null;

	/** The user's default notification address (for now, this is usually email) */
	protected String defaultNotificationAddress = null;

	/** The users default notifcation delivery type (typically email) */
	protected String defaultNotificationDeliveryType = null;

	/*
	 * ------------------------------- Localization Properties
	 * -------------------------------
	 */

	/** The stored id of the users dateFormat */
	protected String dateFormatID = null;

	/** The user's actual date format (ie. M/d/y) */
	protected String dateFormat = null;

	/** A readable example of the user's date format (ie, 06/25/1976) */
	protected String dateFormatExample = null;

	/** The stored id of the users time Format */
	protected String timeFormatID = null;

	/** The user's actual time format */
	protected String timeFormat = null;

	/** A readable example of the user's time format (ie, 13:23) */
	protected String timeFormatExample = null;

	/** Represents the users locale (set at load time) */
	protected Locale locale = null;

	/** Date formatter/parser for this user's locale */
	protected net.project.util.DateFormat dateFormatter = null;

	/** Number formatter/parser for this user's locale */
	protected net.project.util.NumberFormat numberFormatter = null;

	/*
	 * ------------------------------- Billing / User management Properties
	 * -------------------------------
	 */

	/**
	 * for billing. The master business this person is an employee of (NOT YET
	 * USED
	 */
	private Space masterBusiness = null;

	/*
	 * ------------------------------- Security and Space Management
	 * -------------------------------
	 */

	/** Reflects the user's current space */
	protected Space currentSpace = null;

	/** from the security manager */
	protected SecurityProvider securityProvider = null;

	/** Specifies whether the user has been authenticated */
	protected boolean isAuthenticated = false;

	/** Represents whether or not this user is an application administrator */
	private Boolean isApplicationAdministrator = null;

	/** Used only to prevent redundant access logging */
	private String previousSpaceID = null;

	/* ------------------------------- General ------------------------------- */

	/** XML formatting information and utilities specific to this object */
	private XMLFormatter xmlFormatter = new XMLFormatter();

	/** captures the "last" page the user was on (when set) */
	private String referrer = null;

	/** captures the "last" recorded Activity for the user */
	private Date lastRecordedActivityDate = null;

	private UserActivityStatus userActivityStatus = null;

	/** represents the last brand the user logged in as */
	private String lastBrandID = null;

	/**
	 * The loaded domain corresponding to the current domain ID.
	 */
	private UserDomain domain = null;

	/*
	 * ------------------------------- Constructor(s)
	 * -------------------------------
	 */

	/**
	 * Creates an empty user. Initializes a default date and number formatter.
	 * 
	 * @since v1
	 */
	public User() {

		// Initialize default formatters
		// This is required since the empty User object is used
		// during certain processes (like Registration)
		// Replaced when user is loaded
		initDateFormatter();
		initNumberFormatter();

	}

	/**
	 * Instantiate a new User object and load it from the database based on the
	 * specified userID.
	 * 
	 * @param userID
	 *            The userID of the user for loading
	 * @since The beginning of time
	 */
	public User(String userID) {

		try {
			setID(userID);
			load();

		} catch (PersistenceException pe) {
			// do nothing
		}
	}

	/**
	 * Instantiate a new User object from the person object. This constructure
	 * will <b>not</b> load the user object from the database!
	 * 
	 * @param person
	 *            An instantiated person object.
	 * @since The beginning of time
	 */
	public User(net.project.resource.Person person) {
		setID(person.getID());
	}

	/*
	 * ------------------------------- Getter(s) and Setter(s)
	 * -------------------------------
	 */

	/** set the master business the user is currently acting as an employee of */
	public void setMasterBusiness(Space business) {
		this.masterBusiness = business;
	}

	/** get the master business the user is currently acting as an employee of */
	public Space getMasterBusiness() {
		return this.masterBusiness;
	}

	/** sets the verification code used in the registration process */
	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
		if (this.verificationCode != null)
			this.verificationCode = this.verificationCode.trim();
	}

	/** gets the verification code used in the registration process */
	public String getVerificationCode() {
		return this.verificationCode;
	}

	/** Refresh the security groups (roles) in cache for the user. */
	public void refreshGroups() {

		this.securityProvider = SecurityProvider.getInstance();
		this.securityProvider.refreshGroups(this);
	}

	/**
	 * Sets the current space context (personal, project, business) for the
	 * user. This is the space that the user is currently in. Also logs access
	 * to the space if it is a new space.
	 * 
	 * @param space
	 *            the new space
	 * @throws PnetException
	 *             if there is a problem setting the space
	 */
	public void setCurrentSpace(Space space) throws PnetException {

		String spaceID = space.getID();

		// only log access if the user is not "switching" to the current space
		if (spaceID == null || !spaceID.equals(previousSpaceID)) {

			previousSpaceID = spaceID;
			// Space has definitely changed to a different space
			space.logAccess(this);
		}

		// reset the currentSpace to the space passed in (doesn't matter if
		// redundant)
		this.currentSpace = space;

		// get the singleton securityProvider and set for the current space we will 
		// be checking access security for.
		// SecurityProvider.getInstance() returns the SecurityProvider stored in the user's
		// session if it exists, otherwise it instanitates a new securityProvider and 
		// saves it in session before returning it here.
		this.securityProvider = SecurityProvider.getInstance();
		this.securityProvider.setCurrentSpace(this, space);

		// finally switch the current space of the docManager (WHY?)
		DocumentManager docManager = DocumentManager.getInstance();

		if (docManager != null)
			docManager.setSpace(space);
	}

	/**
	 * Get's the installations application Space id
	 */
	public String getApplicationSpaceID() {

		String qstrGetConfigurationID = "select application_id from pn_application_space";
		String id = null;

		DBBean db = new DBBean();
		try {
			db.executeQuery(qstrGetConfigurationID);

			if (db.result.next())
				id = db.result.getString("application_id");
		}

		catch (SQLException sqle) {
			Logger.getLogger(User.class).debug("User.getApplicationSpaceID() threw an SQLException: " + sqle);
		}

		finally {
			db.release();
		}

		return id;
	}

	/**
	 * Returns the last brand the user logged in under.
	 * <p>
	 * Note: This method may return a null value if the user has not yet logged
	 * in.
	 * 
	 * @return The last brand the user logged in under.
	 */
	public String getLastUsedBrandID() {
		return this.lastBrandID;
	}

	/**
	 * Gets the current brand from the logged in session
	 */
	public String getCurrentConfigurationID() {

		String qstrGetConfigurationID = "select configuration_id from pn_configuration_space where brand_id = " + net.project.base.property.PropertyProvider.getActiveBrandID();
		String configurationID = null;

		DBBean db = new DBBean();
		try {
			db.executeQuery(qstrGetConfigurationID);

			if (db.result.next())
				configurationID = db.result.getString("configuration_id");
		}

		catch (SQLException sqle) {
			Logger.getLogger(User.class).debug("User.getCurrentConfigurationID() threw an SQLException: " + sqle);
		}

		finally {
			db.release();
		}

		return configurationID;
	}

	/**
	 * Gets the current space context (personal, project, business) for the
	 * user. This is the space that the user is currently in.
	 */
	public Space getCurrentSpace() {
		return this.currentSpace;
	}

	/**
	 * Gets all the spaces (person, project, business) that the user has access
	 * to (a member of). Performs a database query to get the most recent
	 * information.
	 */
	public ArrayList getAllSpaces() {
		ArrayList spaceList = null;

		DBBean db = new DBBean();
		try {
			db.setQuery("select space_id from pn_space_has_person shp where shp.person_id =" + this.getID()); 
			db.executeQuery();

			if (!db.result.next())
				return null;

			spaceList = new ArrayList();
			do {
				spaceList.add(db.result.getString(1));
			} while (db.result.next());
		} catch (SQLException sqle) {
			Logger.getLogger(User.class).error("User.getAllSpaces failed " + sqle);
		} finally {
			db.release();
		}

		return spaceList;
	}

	/**
	 * Gets the user'sLogin name
	 */
	public String getLogin() {
		return this.username;
	}

	/**
	 * Sets the user's Login name
	 */
	public void setLogin(String value) {
		if (value != null)
			value = value.trim();
		this.username = value;
	}
	
	/*
	 * ------------------------------- Address Wrappers
	 * -------------------------------
	 */

	public String getEcom_ShipTo_Postal_Street_Line1() {
		return address.getAddress1();
	}

	public void setEcom_ShipTo_Postal_Street_Line1(String address1) {
		if (address1 != null)
			address1 = address1.trim();
		this.address.setAddress1(address1);
	}

	public void setEcom_ShipTo_Postal_City(String city) {
		if (city != null)
			city = city.trim();
		this.address.setCity(city);
	}

	public String getEcom_ShipTo_Postal_City() {
		return address.getCity();
	}

	public void setEcom_ShipTo_Postal_PostalCode(String zipcode) {
		if (zipcode != null)
			zipcode = zipcode.trim();
		this.address.setZipcode(zipcode);
	}

	public String getEcom_ShipTo_Postal_PostalCode() {
		return address.getZipcode();
	}

	public void setEcom_ShipTo_Postal_CountryCode(String country) {
		if (country != null)
			country = country.trim();
		this.address.setCountry(country);
	}

	public String getEcom_ShipTo_Postal_CountryCode() {
		return address.getCountry();
	}

	public void setEcom_ShipTo_Telecom_Phone_Number(String officePhone) {
		if (officePhone != null)
			officePhone = officePhone.trim();
		this.address.setOfficePhone(officePhone);
	}

	public String getEcom_ShipTo_Telecom_Phone_Number() {
		return address.getOfficePhone();
	}

	public void setEcom_ShipTo_Postal_StateProv(String district) {
		if (district != null)
			district = district.trim();
		this.address.setState(district);
	}

	public void setEcom_ShipTo_Postal_Street_Line2(String address2) {
		if (address2 != null)
			address2 = address2.trim();
		this.address.setAddress2(address2);
	}

	public String getEcom_ShipTo_Postal_Street_Line2() {
		return address.getAddress2();
	}

	/**
	 * Gets the Email
	 */
	public String getEcom_ShipTo_Online_Email() {
		return getEmail();
	}

	/**
	 * Sets the Email
	 */
	public void setEcom_ShipTo_Online_Email(String value) {
		setEmail(value);
	}

	public void setAlternateEmail1(String email) {
		this.alternateEmail1 = email;
	}

	public String getAlternateEmail1() {
		return this.alternateEmail1;
	}

	public void setAlternateEmail2(String email) {
		this.alternateEmail2 = email;
	}

	public String getAlternateEmail2() {
		return this.alternateEmail2;
	}

	public void setAlternateEmail3(String email) {
		this.alternateEmail3 = email;
	}

	public String getAlternateEmail3() {
		return this.alternateEmail3;
	}
	
	/**
	 * @return the skype
	 */
	public String getSkype() {
		return skype;
	}

	/**
	 * @param skype the skype to set
	 */
	public void setSkype(String skype) {
		this.skype = skype;
	}

	/**
	 * @return the skillsBio
	 */
	public String getSkillsBio() {
		return skillsBio;
	}

	/**
	 * @param skillsBio the skillsBio to set
	 */
	public void setSkillsBio(String skillsBio) {
		this.skillsBio = skillsBio;
	}

	/**
	 * Gets the CreationDate
	 */
	public java.util.Date getCreationDate() {
		return this.creationDate;
	}

	/**
	 * Sets the CreationDate
	 */
	public void setCreationDate(java.util.Date value) {
		this.creationDate = value;
	}

	/**
	 * Gets the Name prefix. i.e. Mr. Ms., Honerable, HRM, Dr.
	 */

	public java.lang.String getEcom_ShipTo_Postal_Name_Prefix() {
		return getNamePrefix();
	}

	/**
	 * Sets the Prefix
	 */
	public void setEcom_ShipTo_Postal_Name_Prefix(java.lang.String value) {
		setNamePrefix(value);
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	public java.lang.String getReferrer() {
		return this.referrer;
	}

	/**
	 * Gets the FirstName
	 * 
	 * @return id
	 */
	public java.lang.String getEcom_ShipTo_Postal_Name_First() {
		return this.firstName;
	}

	/**
	 * Sets the FirstName
	 * 
	 * @param value
	 *            the FirstName of the FirstName
	 */
	public void setEcom_ShipTo_Postal_Name_First(java.lang.String value) {
		if (value != null)
			value = value.trim();
		this.firstName = value;
	}

	/**
	 * Gets the MiddleName
	 * 
	 * @return id
	 */
	public java.lang.String getEcom_ShipTo_Postal_Name_Middle() {
		return this.firstName;
	}

	/**
	 * Sets the MiddleName
	 * 
	 * @param value
	 *            the MiddleName of the MiddleName
	 */
	public void setEcom_ShipTo_Postal_Name_Middle(java.lang.String value) {
		if (value != null)
			value = value.trim();
		this.middleName = value;
	}

	/**
	 * Gets the LastName
	 * 
	 * @return id
	 */
	public java.lang.String getEcom_ShipTo_Postal_Name_Last() {
		return this.lastName;
	}

	/**
	 * Sets the LastName
	 * 
	 * @param value
	 *            the LastName of the LastName
	 */
	public void setEcom_ShipTo_Postal_Name_Last(java.lang.String value) {
		if (value != null)
			value = value.trim();
		lastName = value;
	}

	/**
	 * Gets the Name suffix. i.e. Jr. III., JD, MBA,
	 */
	public java.lang.String getEcom_ShipTo_Postal_Name_Suffix() {
		return this.firstName;
	}

	/**
	 * Sets the Name suffix
	 */
	public void setEcom_ShipTo_Postal_Name_Suffix(java.lang.String value) {
		if (value != null)
			value = value.trim();
		this.suffixName = value;
	}

	/**
	 * Gets the Authenticated
	 * 
	 * @return id
	 */
	public boolean getAuthenticated() {
		return this.isAuthenticated;
	}

	/**
	 * Sets the Authenticated
	 * 
	 * @param value
	 *            the Authenticated of the Authenticated
	 */
	public void setAuthenticated(boolean value) {
		this.isAuthenticated = value;

		StringBuffer query = new StringBuffer();
		query.append("update pn_user set is_login =");

		if (this.isAuthenticated) {
			query.append(" 1 ");
		} else {
			query.append(" 0 ");
		}
		query.append(" where user_id = ? ");

		DBBean db = new DBBean();
		try {
			int index = 0;

			db.prepareStatement(query.toString());
			db.pstmt.setString(++index, this.getID());

			db.executePrepared();

		} catch (SQLException sqle) {
			Logger.getLogger(User.class).error("User.setAuthenticated() failed " + sqle);
		} finally {
			db.release();
		}
	}

	/**
	 * Gets the Date/Time formatter for this user.
	 */
	public net.project.util.DateFormat getDateFormatter() {
		return dateFormatter;
	}

	/**
	 * @deprecated use getMembershipPortfolioID() instead. The portfolio is not
	 *             limited to projects (it now contains configurations).
	 * @return the membership portfolio id
	 * @see #getMembershipPortfolioID()
	 */
	public String getProjectPortfolioID() {
		return this.personalPortfolioID;
	}

	/**
	 * Returns the user's personal portfolio id.
	 * 
	 * @return user's personal portfolio id.
	 */
	public String getMembershipPortfolioID() {
		return this.personalPortfolioID;
	}

	public String formatNumberForLocale(double number) {
		return this.numberFormatter.formatNumber(number);
	}

	public String formatNumberForLocale(long number) {
		return this.numberFormatter.formatNumber(number);
	}

	public String formatCurrencyForLocale(double number) {
		return this.numberFormatter.formatCurrency(number);
	}

	public String formatDateForLocale(Date date) {
		return this.dateFormatter.formatDate(date);
	}

	public String formatDateForLocale(long dateMS) {

		Date date = new Date(dateMS);
		return formatDateForLocale(date);
	}

	/**
	 * Sets the locale for this user.
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * Get the locale for this user.
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Hardwired to EMAIL for now.
	 */
	public String getDefaultNotificationDeliveryType() {
		return this.defaultNotificationDeliveryType;
	}

	public String getDefaultNotificationDeliveryAddress() {
		return this.defaultNotificationAddress;
	}

	public String getNotificationDeliveryAddress(String deliveryType) {

		String address = null;

		if (deliveryType.equals(net.project.notification.INotificationDB.EMAIL_DELIVERABLE))
			address = getEmail();

		return address;
	}

	/**
	 * Sets the date format ID for this user.
	 * 
	 * @param formatID
	 *            the date format ID to set
	 * @deprecated As of V7.4; we now use the localized date formats provided by
	 *             Java.
	 */
	public void setDateFormatID(String formatID) {
		this.dateFormatID = formatID;
	}

	/**
	 * Gets the date format ID of this user's date format.
	 * 
	 * @return the date format ID
	 * @deprecated as of V7.4; we now use the localized date formats provided by
	 *             Java.
	 */
	public String getDateFormatID() {
		return this.dateFormatID;
	}

	/**
	 * Returns the <b>NON-LOCALIZED</b> date format pattern for this user.
	 * 
	 * @return the date format pattern
	 * @deprecated As of 7.5; no replacement Dates are formatted and parsed
	 *             using {@link net.project.util.DateFormat}
	 */
	public String getDateFormat() {
		return this.getDateFormatter().getDateFormatPattern();
	}

	/**
	 * Sets the time format ID.
	 * 
	 * @param formatID
	 *            the formatID to set.
	 * @deprecated As of V7.4; we now use the localized time formats provided by
	 *             Java.
	 */
	public void setTimeFormatID(String formatID) {
		this.timeFormatID = formatID;
	}

	/**
	 * Gets the time format ID.
	 * 
	 * @return the time format ID.
	 * @deprecated As of V7.4; we now use the localized time formats provided by
	 *             Java.
	 */
	public String getTimeFormatID() {
		return this.timeFormatID;
	}

	/**
	 * Returns the <b>NON-LOCALIZED</b> time format pattern for this user.
	 * 
	 * @return the time format pattern
	 * @deprecated As of 7.5; no replacement Dates are formatted and parsed
	 *             using {@link net.project.util.DateFormat}
	 */
	public String getTimeFormat() {
		return this.getDateFormatter().getTimeFormatPattern();
	}

	/**
	 * Gets the Last Recorded Activity date for this user.
	 * 
	 * @return The date
	 */
	public Date getLastRecordedActivityDate() {
		if (this.lastRecordedActivityDate == null) {

			StringBuffer query = new StringBuffer();

			query.append("select max(access_date) max_access_date from pn_space_access_history where space_id = ? ");
			query.append(" and person_id = ? ");

			DBBean db = new DBBean();
			try {
				int index = 0;

				db.prepareStatement(query.toString());
				db.pstmt.setString(++index, this.getCurrentSpace().getID());
				db.pstmt.setString(++index, this.getID());

				db.executePrepared();

				if (db.result.next()) {
					java.sql.Timestamp timestamp = db.result
							.getTimestamp("max_access_date");
					this.lastRecordedActivityDate = new Date(timestamp
							.getTime());
				} else {
					this.lastRecordedActivityDate = new Date();
				}

			} catch (SQLException sqle) {

				this.lastRecordedActivityDate = new Date();
				Logger.getLogger(User.class).error("User.getLastRecordedActivityDate() failed " + sqle);

			} finally {
				db.release();
			}

		} else {
			return this.lastRecordedActivityDate;
		}

		return this.lastRecordedActivityDate;
	}

	/**
	 * Sets the Last Recorded Activity date for this user.
	 * 
	 * @param lastRecordedActivityDate
	 *            The date
	 */
	public void setLastRecordedActivityDate(Date lastRecordedActivityDate) {
		this.lastRecordedActivityDate = lastRecordedActivityDate;
	}

	/**
	 * Sets application administrator flag.
	 * 
	 * @param isApplicationAdministrator
	 *            true implies user is an application administator; false
	 *            implies not.
	 */
	private void setApplicationAdministrator(boolean isApplicationAdministrator) {
		this.isApplicationAdministrator = Boolean.valueOf(isApplicationAdministrator);
	}

	/**
	 * Indicates whether user is an application administrator.
	 * 
	 * @return true if user is an application administrator; false otherwise
	 */
	public boolean isApplicationAdministrator() {

		if (this.isApplicationAdministrator == null) {
			determineSecurity();
		}

		return this.isApplicationAdministrator.booleanValue();
	}

	/**
	 * Indicates whether user is a space administrator.
	 * 
	 * @return true if user is a space administrator; false otherwise
	 */
	public boolean isSpaceAdministrator() {

		return (this.currentSpace != null) ? this.currentSpace.isUserSpaceAdministrator(this) : false;
	}
	
	/**
	 * Indicates whether user is a specified group's member in the current space.
	 * 
	 * @param groupMember the group which we are checking for
	 * @return true if user is a space group member; false otherwise
	 */
	public boolean isSpaceGroupMember(net.project.security.group.GroupTypeID groupTypeID) {

		return (this.currentSpace != null) ? this.currentSpace.isUserGroupUser(this, groupTypeID) : false;
	}

	public void updateInvitedAssignments() {

	}

	/**
	 * Sets the domain ID of the domain against which the user is registering.
	 * The domain determines the directory which in turn affects the flow of the
	 * registration wizard. <b>Note:</b>Setting the ID actually instantiates
	 * the user domain and will throw an error if the domainID is invalid.
	 * 
	 * @param userDomainID
	 *            the ID of the domain; empty values are maintained as null
	 *            values; if the specified value is different from the currently
	 *            loaded domain's ID then it is cleared out
	 * @throws PersistenceException
	 *             if the domainID is invalid or if the domain represented by
	 *             the ID can not be loaded.
	 * @see #getUserDomainID
	 * @see #getUserDomain
	 */
	public void setUserDomain(String userDomainID) throws PersistenceException {

		UserDomain domain = new UserDomain(userDomainID);
		domain.load();
		setUserDomain(domain);
	}

	/**
	 * Returns the id of the domain to which this user belongs. If no domain has
	 * been loaded, the domainID will be returned as null.
	 * 
	 * @return the domain id
	 * @see #setUserDomain
	 */
	public String getUserDomainID() {
		String domainID;

		try {

			if (getUserDomain() == null) {
				domainID = null;
			} else {
				domainID = getUserDomain().getID();
			}

		} catch (PersistenceException pe) {
			domainID = null;
		}

		return domainID;
	}

	/**
	 * Sets the domain corresponding to the current userDomainID. As a side
	 * effect, the userDomainID attribute will be set too.
	 * 
	 * @param domain
	 *            the loaded domain or null to clear out the domain
	 * @see #getUserDomain
	 */
	public void setUserDomain(UserDomain domain) {
		this.domain = domain;
	}

	/**
	 * Returns a loaded UserDomain for the current userDomainID. The domain is
	 * loaded once; subsequent calls without changing the userDomainID will
	 * return the same domain.
	 * 
	 * @precondition a domain ID is available with {@link #getUserDomainID}
	 * @throws IllegalStateException
	 *             if there is no current domain ID
	 * @throws PersistenceException
	 *             if there is a problem loading
	 */
	public UserDomain getUserDomain() throws PersistenceException {
		return this.domain;
	}

	//
	// Implementing IXMLPersistence
	//

	public String getXMLBody() {
		StringBuffer sb = new StringBuffer();

		sb.append("<user>\n");
		addBaseAttributes(sb);

		if (this.address != null) {
			sb.append(address.getXMLBody());
		}

		sb.append("</user>\n");

		return sb.toString();
	}

	public String getXML() {
		return (XML_VERSION + "\n" + getXMLBody());
	}

	/**
	 * Gets the presentation of the User This method will apply the stylesheet
	 * to the XML representation of the component and
	 * 
	 * return the resulting text
	 * 
	 * @return presetation of the component
	 */
	public String getPresentation() {
		return xmlFormatter.getPresentation(getXML());
	}

	/**
	 * Sets the stylesheet file name used to render this component. This method
	 * accepts the name of the stylesheet used to convert the XML representation
	 * of the component to a presentation form.
	 * 
	 * @param styleSheetFileName
	 *            name of the stylesheet used to render the XML representation
	 *            of the component
	 */
	public void setStylesheet(String styleSheetFileName) {
		xmlFormatter.setStylesheet(styleSheetFileName);
	}

	/***************************************************************************
	 * Implementing IJDBCPersistence.
	 **************************************************************************/

	/**
	 * Load properties from persistent storage based on the current ID. The
	 * user's address information (Address class) will also be loaded for
	 * performance reasons.
	 * 
	 * @throws PersistenceException
	 *             if there is a problem loading the user or the user is not
	 *             found for the current id
	 * @throws NullPointerException
	 *             if the current id is null
	 * @see #setID
	 */
	public void load() throws PersistenceException {

		if (getID() == null) {
			throw new NullPointerException("User load operation failed; missing id");
		}

		// Add where clause based on ID
		StringBuffer query = new StringBuffer(User.getLoadQuery());
		query.append("and p.person_id = ? ");

		// Add ID as a bindvariable
		List bindVariables = new ArrayList();
		bindVariables.add(getID());

		// Load the user
		load(query.toString(), bindVariables);

		if (!this.isLoaded) {
			Logger.getLogger(User.class).error("User.load() failed to find user with id: " + getID());
			throw new PersistenceException("User load operation failed; unknown user");
		}

	}

	/**
	 * Load properties from persistent storage based on the specified email
	 * address. The user's address information (Address class) will also be
	 * loaded for performance reasons. <b>Note:</b> If the user is not found,
	 * no exception is thrown; however <code>isLoaded</code> remains false
	 * 
	 * @throws PersistenceException
	 *             if there is a problem loading the user
	 * @throws NullPointerException
	 *             if the specified email address is null
	 * @see #setID
	 */
	public void loadForEmail(String email) throws PersistenceException {

		if (email == null) {
			throw new NullPointerException("User load operation failed; missing email");
		}

		// Add a where clause for email address
		StringBuffer query = new StringBuffer(User.getLoadQuery());
		query.append("and lower(p.email) = lower(?) and p.user_status != 'Deleted' ");

		// Add the email address to the bindvariables
		List bindVariables = new ArrayList();
		bindVariables.add(email);

		// Load the user
		load(query.toString(), bindVariables);

	}

	/**
	 * Load properties from persistent storage based on the specified query and
	 * bindvariables for that query. The user's address information (Address
	 * class) will also be loaded for performance reasons. <b>Note:</b> Sets
	 * <code>isLoaded</code> to false and only sets it to true if a user is
	 * found and populated successfully
	 * 
	 * @throws PersistenceException
	 *             if there is a problem loading the user
	 */
	private void load(String query, List bindVariables) throws PersistenceException {

		DBBean db = new DBBean();
		try {
			this.isLoaded = false;

			int index = 0;
			db.prepareStatement(query);

			// Add all bindvariables (assumes each is a string)
			for (Iterator it = bindVariables.iterator(); it.hasNext();) {
				String nextVariable = (String) it.next();
				db.pstmt.setString(++index, nextVariable);
			}

			// Execute the load query
			db.executePrepared();

			if (db.result.next()) {
				// Populate this User object from the result set
				User.populate(db.result, this);
				this.isLoaded = true;
			}

		} catch (SQLException sqle) {
			Logger.getLogger(User.class).error("User.load() threw an SQLException: " + sqle);
			throw new PersistenceException("User load operation failed", sqle);

		} finally {
			db.release();

		}

	}

	/**
	 * Stores this user. Only supports updates the existing user records. Use
	 * the stored procedure CREATE_PERSON_DEFAULT for creating new users.
	 * 
	 * @throws PersistenceException
	 *             if there is a problem storing the user.
	 */
	public void store() throws PersistenceException {
		DBBean db = new DBBean();

		try {
			store(db);

		} finally {
			db.release();
		}

	}

	/**
	 * Stores this user. Only supports updates the existing user records. Use
	 * the stored procedure CREATE_PERSON_DEFAULT for creating new users.
	 * 
	 * @throws PersistenceException
	 *             if there is a problem storing the user.
	 */
	public void store(DBBean dbean) throws PersistenceException {

		StringBuffer updatePersonQuery = new StringBuffer();
		StringBuffer updateProfileQuery = new StringBuffer();
		StringBuffer updateUserQuery = new StringBuffer();

		if (getID() == null) {
			throw new NullPointerException("this.userID is null.  can't store.");
		}

		// Build the query for updating the person record
		// only update non-null fields
		updatePersonQuery.append("update pn_person set person_id = " + getID());
		if (this.firstName != null) {
			updatePersonQuery.append(", first_name=" + DBFormat.varchar2(this.firstName));
		}
		if (this.lastName != null) {
			updatePersonQuery.append(", last_name=" + DBFormat.varchar2(this.lastName));
		}
		if (this.displayName != null) {
			updatePersonQuery.append(", display_name=" + DBFormat.varchar2(this.displayName));
		}
		if (getEmail() != null) {
			updatePersonQuery.append(", email=" + DBFormat.varchar2(getEmail()).trim());
		}
		if (getStatus() != null) {
			updatePersonQuery.append(", user_status=" + DBFormat.varchar2(getStatus().getID()));
		}
		updatePersonQuery.append(" where person_id=" + getID());

		// Build the query for updating the person profile record
		updateProfileQuery.append("update pn_person_profile set person_id = " + getID());
		if (this.address != null) {
			updateProfileQuery.append(", address_id=" + this.address.getID());
		}
		if (this.alternateEmail1 != null) {
			updateProfileQuery.append(", alternate_email_1="+ DBFormat.varchar2(this.alternateEmail1).trim());
		}
		if (this.alternateEmail2 != null) {
			updateProfileQuery.append(", alternate_email_2="+ DBFormat.varchar2(this.alternateEmail2).trim());
		}
		if (this.alternateEmail3 != null) {
			updateProfileQuery.append(", alternate_email_3="+ DBFormat.varchar2(this.alternateEmail3).trim());
		}
		if (this.skype != null) {
			updateProfileQuery.append(", skype="+ DBFormat.varchar2(this.skype).trim());
		}
		if (this.skillsBio != null) {
			updateProfileQuery.append(", skills_bio="+ DBFormat.varchar2(this.skillsBio).trim());
		}
		if (this.prefixName != null) {
			updateProfileQuery.append(", prefix_name="+ DBFormat.varchar2(this.prefixName));
		}
		if (this.middleName != null) {
			updateProfileQuery.append(", middle_name="+ DBFormat.varchar2(this.middleName));
		}
		if (getDisplayName() != null) {
			updateProfileQuery.append(", personal_space_name="+ DBFormat.varchar2(getDisplayName()));
		}
		if (this.suffixName != null) {
			updateProfileQuery.append(", suffix_name="+ DBFormat.varchar2(this.suffixName));
		}
		if (this.timeZoneCode != null) {
			updateProfileQuery.append(", timezone_code="+ DBFormat.varchar2(this.timeZoneCode));
		}

		if (languageCode != null) {
			updateProfileQuery.append(", language_code="+ DBFormat.varchar2(languageCode));
		}

		if (this.verificationCode != null) {
			updateProfileQuery.append(", verification_code="+ DBFormat.varchar2(this.verificationCode));
		}
		if (this.localeCode != null) {
			updateProfileQuery.append(", locale_code="+ DBFormat.varchar2(this.localeCode));
		}
		updateProfileQuery.append(" where person_id=" + getID());

		// Build query for updating the user record
		if (this.username != null) {
			updateUserQuery.append("update pn_user ");
			updateUserQuery.append("set username = "+ DBFormat.varchar2(this.username));
			updateUserQuery.append(" where user_id = " + getID());
		}

		try {
			dbean.createStatement();

			dbean.stmt.addBatch(updatePersonQuery.toString());
			dbean.stmt.addBatch(updateProfileQuery.toString());
			if (updateUserQuery.length() > 0) {
				dbean.stmt.addBatch(updateUserQuery.toString());
			}
			dbean.executeBatch();

		} catch (SQLException sqle) {
			Logger.getLogger(User.class).error("User.store failed " + sqle);
			throw new PersistenceException("User store operation failed: " + sqle, sqle);
		}

	}

	/**
	 * Removes the user from the system. Delegates to the UserDomain and
	 * LicenseManager to disable the users ability to login and to free up the
	 * users license. Also, the user's "user_status" is set to "Deleted"
	 * 
	 * @exception PersistenceException
	 *                if there is a problem removing the user
	 */
	public void remove() throws PersistenceException {

		DBBean db = new DBBean();

		try {

			// In order to make this transaction atomic, we first need to
			// disable autocommit on the db connection
			db.setAutoCommit(false);

			// first, change the user's status to deleted
			// NOTE, this does not require the user object to be loaded, simply
			// requires the ID to be set
			changeStatus(PersonStatus.DELETED.getID(), db);

			// NEXT, try and remove the user from their domain. This operation
			// (including the user.load()) will fail
			// in the case of an UNREGISTERED user because "unregistered" users
			// do not have user/domain records, so we swallow the errors.
			try {

				// if the user is not loaded, load the user object
				if (!this.isLoaded()) {
					load();
				}

				// next, remove the user's entry from the domain. This will also
				// remove any directory specific
				// information (via delegation by UserDomain).
				UserDomain domain = UserDomain.getInstance(this);
				domain.removeUser(this, db);

			} catch (net.project.security.domain.DomainException de) {
				// If there is a domain exception, we don't want to do anything
				// about it.
				// IN the user.remove() method, UNREGISTERED users will not be
				// associatated with a domain
				// and will cause this exception to be thrown by
				// UserDomain.remove();

			} catch (net.project.persistence.PersistenceException pe) {
				// If there is a persitence exception, do nothing
				// This may be thrown by either the user.load() method above in
				// the case of an UNREGISTERED user
				// where the user doesn't have a pn_person_profile, or a pn_user
				// record.
			}

			// Now, after attempting to remove the user from their domain,
			// disassociate them from whatever license they may be using.
			net.project.license.LicenseManager licenseManager = new net.project.license.LicenseManager();
			licenseManager.disassociateCurrentLicense(db, this, false);

			// Finally remove the actual user record
			db.prepareStatement("delete from pn_user where user_id = ?");
			db.pstmt.setString(1, getID());
			db.executePrepared();

			// Finally, commit if all has gone according to plan
			db.commit();

		} catch (SQLException sqle) {
			try {
				db.rollback();
			} catch (SQLException sqle2) { /* do nothing */
			}
			throw new PersistenceException(
					"User.remove() threw an SQL Exception: " + sqle, sqle);

		} finally {
			db.release();
		}
	}

	public void logout() {
		setAuthenticated(false);
	}

	/***************************************************************************
	 * Private Methods
	 **************************************************************************/

	/**
	 * Initialize the date formatter for this user. If locale-specific
	 * formatting is required, the setLocale() method must be called first.
	 */
	private void initDateFormatter() {
		dateFormatter = new net.project.util.DateFormat(this);
		// dateFormatter.setDateFormatExample(this.dateFormatExample);
		// dateFormatter.setTimeFormatExample(this.timeFormatExample);

	}

	private void initNumberFormatter() {
		this.numberFormatter = new net.project.util.NumberFormat(this);
	}

	/**
	 * Determines security settings for user. <br>
	 * Sets isApplicationAdministrator based on user's membership in appropriate
	 * groups.
	 * 
	 * @see SecurityProvider#isUserApplicationAdministrator()
	 */
	private void determineSecurity() {
		// Flag indicating whether user is administrator or not.
		// This is cached on the user at load time to avoid excessive
		// roundtrips for a reasonably static property
		SecurityProvider securityProvider = new SecurityProvider();
		securityProvider.setUser(this);
		setApplicationAdministrator(securityProvider.isUserApplicationAdministrator());
	}

	/**
	 * Base properties for XML methods. NOTE: WE SHOULD "extend" PERSON's XML
	 * HERE, but have not yet
	 */
	private void addBaseAttributes(StringBuffer sb) {

		// append user-id tag for UserId property
		sb.append("<user-id>");
		sb.append(XMLUtils.escape(getID()));
		sb.append("</user-id>\n");

		// append login tag for Login property
		sb.append("<login>");
		sb.append(XMLUtils.escape(getLogin()));
		sb.append("</login>\n");

		// append login tag for Login property
		sb.append("<hasLicense>");
		sb.append(XMLUtils.escape(net.project.util.Conversion.booleanToString(isLicensed())));
		sb.append("</hasLicense>\n");

		// append email tag for Email property
		sb.append("<email>");
		sb.append(XMLUtils.escape(getEmail()));
		sb.append("</email>\n");

		// append creation-date tag for CreationDate property
		if (getCreationDate() == null) {
			sb.append("<creation-date/>");
		} else {
			sb.append("<creation-date>");
			sb.append(XMLUtils.formatISODateTime(getCreationDate()));
			sb.append("</creation-date>\n");
		}

		// append last-visit-date tag for LastVisitDate property

		if (getLastLogin() == null) {
			sb.append("<last-visit-date/>");
		} else {
			sb.append("<last-visit-date>");
			sb.append(XMLUtils.formatISODateTime(getLastLogin()));
			sb.append("</last-visit-date>\n");
		}
		sb.append("<skype>");
		sb.append(XMLUtils.escape(getSkype()));
		sb.append("</skype>\n");
		// append first-name tag for FirstName property
		sb.append("<first-name>");
		sb.append(XMLUtils.escape(getFirstName()));
		sb.append("</first-name>\n");

		// append middle-name tag for MiddleName property
		sb.append("<middle-name>");
		sb.append(XMLUtils.escape(getMiddleName()));
		sb.append("</middle-name>\n");

		// append last-name tag for LastName property
		sb.append("<last-name>");
		sb.append(XMLUtils.escape(getLastName()));
		sb.append("</last-name>\n");

		// append last-name tag for LastName property
		sb.append("<full-name>");
		sb.append(XMLUtils.escape(getDisplayName()));
		sb.append("</full-name>\n");

		// append authenticated tag for Authenticated property
		sb.append("<authenticated>");
		sb.append(XMLUtils.escape(Conversion.booleanToString(getAuthenticated())));
		sb.append("</authenticated>\n");

		sb.append("<activity_status>");
		sb.append(XMLUtils.escape(getUserActivityStatusInstance().getDisplayName()));
		sb.append("</activity_status>\n");

		sb.append("<alternate_email_1>");
		sb.append(XMLUtils.escape(this.alternateEmail1));
		sb.append("</alternate_email_1>\n");

		sb.append("<alternate_email_2>");
		sb.append(XMLUtils.escape(this.alternateEmail2));
		sb.append("</alternate_email_2>\n");

		sb.append("<alternate_email_3>");
		sb.append(XMLUtils.escape(this.alternateEmail3));
		sb.append("</alternate_email_3>\n");
	}

	/** Clear all the properties of this User */
	public void clear() {
		if (address != null)
			address.clear();

		setID(null);
		this.username = null;
		this.alternateEmail1 = null;
		this.alternateEmail2 = null;
		this.alternateEmail3 = null;
		this.skype = null;
		this.skillsBio = null;
		this.creationDate = null;
		this.verificationCode = null;
		this.isAuthenticated = false;
		locale = null;
		timeZone = null;
		countryCode = null;
		languageCode = null;
		isApplicationAdministrator = null;
		this.isLoaded = false;
		this.domain = null;
	}

	public UserActivityStatus getUserActivityStatusInstance() {

		if (this.userActivityStatus == null) {
			this.userActivityStatus = new UserActivityStatus();
		}

		return this.userActivityStatus;
	}
}
