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
import java.util.TimeZone;

import net.project.base.attribute.AttributeCollection;
import net.project.base.attribute.AttributeStoreException;
import net.project.base.attribute.IAttribute;
import net.project.base.attribute.IAttributeReadable;
import net.project.base.attribute.IAttributeValue;
import net.project.base.attribute.IAttributeWriteable;
import net.project.base.attribute.IllegalAttributeValueException;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.database.DBBean;
import net.project.gui.html.IHTMLOption;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.util.Validator;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;


/**
 * Creates a person with all basic information, includes getter/setter methods to allow use as a bean
 * class.
 *
 * @author Dan Kelley
 * @author Roger Bly
 * @author Phil Dixon
 * @since The beginning of time
 */
public class Person implements IResource, IJDBCPersistence, IXMLPersistence, java.io.Serializable, IRosterEntry, net.project.security.group.IGroupMember, IAttributeReadable, IAttributeWriteable, IHTMLOption {

    /**
     *
     * @deprecated as of release 7.4
     *             replaced by {@link net.project.resource.PersonStatus#ACTIVE }
     */
    public static final String ACTIVE_USER_STATUS = "Active";
    /**
     *
     * @deprecated as of release 7.4
     *             replaced by {@link net.project.resource.PersonStatus#UNREGISTERED }
     */
    public static final String UNREGISTERED_USER_STATUS = "Unregistered";
    /**
     *
     * @deprecated as of release 7.4
     *             replaced by {@link net.project.resource.PersonStatus#UNCONFIRMED }
     */
    public static final String UNCONFIRMED_USER_STATUS = "Unconfirmed";
    /**
     *
     * @deprecated as of release 7.4
     *             replaced by {@link net.project.resource.PersonStatus#DISABLED }
     */
    public static final String DISABLED_USER_STATUS = "Disabled";
    /**
     *
     * @deprecated as of release 7.4
     *             replaced by {@link net.project.resource.PersonStatus#DELETED }
     */
    public static final String DELETED_USER_STATUS = "Deleted";

    /* ------------------------------- Person Properties  ------------------------------- */

    /** The unique id of the person */
    protected String personID = null;
    /** A person's email address -- a critical an system unique key */
    public String email = null;
    /** A person's username */
    protected String username = null;
    /** Person's prefex name (Dr., Mr., Ms., Sir, etc.) */
    protected String prefixName = null;
    /** Person's first name */
    public String firstName = null;
    /** Person's middle name */
    protected String middleName = null;
    /** Person's last name */
    public String lastName =null;
    /** Person's second last name (maiden name?) */
    protected String secondLastName =null;
    /** Person's suffix (Jr, Sr, Ph.D, etc.) */
    protected String suffixName = null;
    /** Person's display name -- typically a concatenation of the first and last names */
    public String displayName = null;
    /** The user's last login date/time */
    protected Date lastLogin = null;
    /** ID of the person's address record */
    protected String addressID = null;
    /** image id for uploaded person image */
    protected int imageId = 0;    
    /** Person's Address */
    protected Address address = new Address();
    /** Person's skype id */
    protected String skype = null;
    
    public String invite ="";
    
    private String[] roles;
    
    /**
     * The person's status in the application (active, unregistered, unconfirmed, disabled)
     *
     * @deprecated as of release 7.4
     *             No replacement
     */
    protected String userStatus = null;
    /** boolean represented whether or not a person has a license */
    protected boolean hasLicense = false;
    /* ------------------------------- Localization Properties  ------------------------------- */

    /** The default timezone for the user */
    protected String timeZoneCode = null;
    /** The user's country code */
    protected String countryCode = null;
    /** The users language code */
    protected String languageCode = null;
    /** The users locale code */
    protected String localeCode = null;

    /* ------------------------------- General  ------------------------------- */

    protected String responsibilities = null;
    protected String memberTitle = null;

    /* ------------------------------- General  ------------------------------- */

    /** Represents whether the object is loaded yet */
    protected boolean isLoaded = false;

    private PersonStatus personStatus = null;
    /** represents the users timeZone */
    protected TimeZone timeZone = null;
    
    public boolean isOnline = false;
    /** ID of the person's domain record */
    protected int domaninID;
    
    
    /* ------------------------------- Salary  ------------------------------- */
    
    public PersonSalary salary = null;
 

    /* ------------------------------- Constructor(s)  ------------------------------- */


    /**
     * Empty constructor to support bean compliance.
     * @since The beginning of time
     */
     public Person() {
        // do nothing
     }

    /**
     * Construct a person with the specified database ID.
     * @param person_id the id of the person.
     */
    public Person(String person_id) {
        this.personID = person_id;
    }

    /* ------------------------------- Getters and Setters  ------------------------------- */



    /**
     * Gets the Person's database id
     *
     * @return String    the person id
     */
    public String getID() {
        return this.personID;
    }

    /**
     * Sets the Person's database id
     *
     * @param id the person id
     */
    public void setID(String id) {
        this.personID = id;
    }

    /**
     * Gets the Person's display name
     *
     * @return the person display name
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Sets the Person's display name
     *
     * @param value the person display name
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

    /**
     * Gets the Person's display name
     *
     * @return the person display name
     *
     * @deprecated use getDisplayName()
     */
    public String getFullName() {
        return this.displayName;
    }

    /**
     * Sets the Person's display name
     *
     * @param value  the person display name
     * @deprecated  use getDisplayName()
     */
    public void setFullName(String value) {
        this.displayName = value;
    }

    /**
     * Gets the Person's first name
     *
     * @return  the person first name
     */
    public String getNamePrefix() {
        return this.prefixName;
    }

    public void setNamePrefix(java.lang.String value) {
        if (value!=null) value = value.trim();
        this.prefixName = value;
    }

    public java.lang.String getNameSuffix() {
        return this.suffixName;
    }

    public void setNameSuffix(java.lang.String value) {
        if (value!=null) value = value.trim();
        this.suffixName = value;
    }

    public String getFirstName() {
        return this.firstName;
    }

    /** Sets the Person's database id */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /** Gets the Person's database id */
    public String getLastName() {
        return this.lastName;
    }

    /** Sets the Person's database id */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /** Gets the Person's loginName */
    public String getUserName() {
        return this.username;
    }

    /** Sets the Person's login name */
    public void setUserName(String value) {
        this.username = value;
    }

    /** Gets the Person's email address */
    public String getEmail() {
        return this.email;
    }

    /** Sets the Person's email address */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Returns true if the person has a license
     */
    public boolean isLicensed() {
        return this.hasLicense;
    }

    /** Sets the license status of this user */
    public void setLicensed (boolean isLicensed) {
        this.hasLicense = isLicensed;
    }

    /** Gets the Person's project responsibilities */
    public String getResponsibilities() {
        return this.responsibilities;
    }

    /** Sets the Person's project responsibilities*/
    public void setResponsibilities(String value) {
        this.responsibilities = value;
    }


    /** Gets the Person's business title */
    public String getTitle() {
        return this.memberTitle;
    }

    /** Sets the Person's business title*/
    public void setTitle(String value) {
        this.memberTitle = value;
    }


    /**
     * Get the Person's user status.
     *
     * @return the user status
     * @deprecated as of release 7.4
     * replaced by {@link #getStatus() }
     */
    public String getUserStatus() {
       return this.userStatus;
    }

   /**
    * Set the Person's user status.
    *
    * @param userStatus
    * @deprecated as of release 7.4
    * replaced by {@link #setStatus(PersonStatus) }
    */
   public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
   }

    /**
     * Returns the Person's status
     *
     * @return the status of person
     */
    public PersonStatus getStatus() {
        return this.personStatus;
    }

    /**
     * Set the Person's status.
     *
     * @param status PersonStatus
     */
    public void setStatus(PersonStatus status) {
        this.personStatus = status;
    }

    /**
     * Returns true if the instantiated person object has a userStatus which matches
     * the specified value.
     *
     * @param userStatus The status to be verified
     * @return True if the statuses match, false if not.
     * @deprecated as of release 7.4
     * replaced by {@link #hasStatus(PersonStatus) }
     */
    public boolean isUserStatus (String userStatus) {
        return (getUserStatus() != null && getUserStatus().equals(userStatus));
   }

    /**
     * Returns true if the instantiated person object has a Status which matches
     * the specified value.
     *
     * @param status PersonStatus
     * @return True if the statuses match, false if not.
     */
    public boolean hasStatus (PersonStatus status) {
        return (getStatus() != null && getStatus().equals(status));
    }

    /** sets the locale code for the user */
    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    /** gets the locale code for the user */
    public String getLocaleCode() {
        return this.localeCode;
    }

    /** sets the language code for the user */
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    /** gets the language code for the user */
    public String getLanguageCode() {
        return this.languageCode;
    }


    /**
     * Sets the timezone for this user and updates the time zone
     * if the time zone code is not null.
     * @param zone the time zone code
     */
    public void setTimeZoneCode(String zone) {
        this.timeZoneCode = zone;

        if (!Validator.isBlankOrNull(zone)) {
            this.timeZone = TimeZone.getTimeZone(zone);
        }
    }

    /**
     * Returns the time zone code for this user.
     * @return the time zone code
     */
    public String getTimeZoneCode() {
        return this.timeZoneCode;
    }

    /**
     * Sets the timezone as a string for this user.
     * @param timeZone the time zone code string
     * @deprecated In favor of setTimeZoneCode()
     */
    public void setTimeZone(String timeZone) {
        setTimeZoneCode(timeZone);
    }


    /**
     * Sets the timezone for this user.
     */
    public void setTimeZone(TimeZone zone) {
        this.timeZone = zone;
    }

    /**
     * Returns the timezone for this user.
     */
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    /* ------------------------------- Address wrapper methods  ------------------------------- */

    /*-------------------------------------------------------------------------------------------------
     *  Wrappers for person's Address, for  convience in the Person bean.
     * ------------------------------------------------------------------------------------------------*/

    /** Get the Person's Address. */
    public Address getAddress() {
        return this.address;
    }

    /** Set the Person's Address  */
    public void setAddress(Address address) {
        this.address = address;
        this.addressID = address.getID();
    }


    /** Get the Person's address id.*/
    public String getAddressID() {
        return this.addressID;
    }

    /** Set the Person's address id. */
    public void setAddressID(String addressID) {
        this.addressID = addressID;
    }


    public void setAddress1(String address1) {
        this.address.address1 = address1;
    }
    public String getAddress1() {
        return this.address.address1;
    }

    public void setAddress2(String address2) {
        this.address.address2 = address2;
    }
    public String getAddress2() {
        return this.address.address2;
    }

    public void setAddress3(String address3) {
        this.address.address3 = address3;
    }
    public String getAddress3() {
        return this.address.address3;
    }

    public void setAddress4(String address4) {
        if (address4!=null) address4 = address4.trim();
        this.address.setAddress4(address4);
    }
    public String getAddress4() {
        return address.getAddress4();
    }


    public void setAddress5(String address5) {
        if (address5!=null) address5 = address5.trim();
        this.address.setAddress5(address5);
    }
    public String getAddress5() {
        return address.getAddress5();
    }


    public void setAddress6(String address6) {
        if (address6!=null) address6 = address6.trim();
        this.address.setAddress6(address6);
    }
    public String getAddress6() {
        return address.getAddress6();
    }


    public void setAddress7(String address7) {
        if (address7!=null) address7 = address7.trim();
        this.address.setAddress7(address7);
    }
    public String getAddress7() {
        return address.getAddress7();
    }


    public void setCity(String city) {
        this.address.city = city;
    }
    public String getCity() {
        return this.address.city;
    }

    public void setState(String state) {
        this.address.state = state;
    }
    public String getState() {
        return this.address.state;
    }

    public void setZipcode(String zipcode) {
        this.address.zipcode = zipcode;
    }
    public String getZipcode() {
        return this.address.zipcode;
    }

    public void setCountry (String country) {
        this.address.country = country;
    }
    public String getCountry() {
        return this.address.country;
    }

    public void setCityDistrict(String district) {
        if (district!=null) district = district.trim();
        this.address.setCityDistrict(district);
    }
    public String getCityDistrict() {
        return address.getCityDistrict();
    }

    public void setRegion(String region) {
        if (region!=null) region = region.trim();
        this.address.setRegion(region);
    }
    public String getRegion() {
        return address.getRegion();
    }

    public void setOfficePhone(String officePhone) {
        this.address.officePhone = officePhone;
    }
    public String getOfficePhone() {
        return this.address.officePhone;
    }

    public void setHomePhone(String homePhone) {
        this.address.homePhone = homePhone;
    }
    public String getHomePhone() {
        return this.address.homePhone;
    }

    public void setFaxPhone(String faxPhone) {
        this.address.faxPhone = faxPhone;
    }
    public String getFaxPhone() {
        return this.address.faxPhone;
    }

    public void setPagerPhone(String pagerPhone) {
        this.address.pagerPhone = pagerPhone;
    }
    public String getPagerPhone() {
        return this.address.pagerPhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.address.mobilePhone = mobilePhone;
    }
    public String getMobilePhone() {
        return this.address.mobilePhone;
    }

    public void setPagerEmail(String pagerEmail) {
        this.address.pagerEmail = pagerEmail;
    }
    public String getPagerEmail() {
        return this.address.pagerEmail;
    }

    public void setWebsiteURL(String websiteURL) {
        this.address.websiteURL = websiteURL;
    }
    public String getWebsiteURL() {
        return this.address.websiteURL;
    }

    /**
     * Set the date of the most recent login (may be null)
     */
    public void setLastLogin (Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * Get the date of the most recent login (may be null)
     */
    public Date getLastLogin() {
        return this.lastLogin;
    }

    /* ------------ IAttributeReadable/Writeable implementation ------------- */


    /**
     * Returns the attributes provided by this Person object. This includes the
     * address attributes.
     *
     * @return the attributes
     */
    public AttributeCollection getAttributes() {
        net.project.base.attribute.AttributeCollection attributes = null;
        // The PersonAttributeManager already returns Address attributes
        // too
        attributes = new PersonAttributeManager().getAttributes();
        return attributes;
    }

    public void setAttributeValue(IAttribute iattr,IAttributeValue iattrval) throws IllegalAttributeValueException {
    }

    public void storeAttributes() throws AttributeStoreException {
    }

    public void clearAttributeValues() {
    }

    /**
     * Returns this roster entry's type.
     *
     * @return the entry type
     * @see net.project.resource.RosterEntryType
     */
    public RosterEntryType getRosterEntryType() {
        return RosterEntryType.PERSON;
    }

    /**
     * Is this Person equal to the passed Person?  Database IDs determine
     * equivelence.
     *
     * @param person a <code>Person</code> object which we are going to
     * determine is equal to the current object.
     * @return a <code>boolean</code> value which indicates whether the person
     * parameter is equal to the current object.
     */
    public boolean equals(Person person) {
        // per java.lang.Object spec.
        if (person == null)
            return false;

        return( this.getID().equals(person.getID()) );
    }

    /**
     * @return a String representation of the Person object.
     */
    public String toString() {
        return("this.personID = " + this.personID + "\nthis.displayName = " + this.displayName);
    }

    //
    // Implementing IRecipient
    //

    /**
     * Returns this Person's email recipient name.
     *
     * @return the display name
     */
    public String getRecipientName() {
        return getDisplayName();
    }

    /**
     * Returns this Person's email address.
     *
     * @return a one element collection containing the email address of the form
     * "Display Name <user@domain.com>"
     */
    public java.util.Collection getRecipientEmailAddresses() {
        // Construct a one element List containing this Person's email address
        // of the form: "Display Name <user@domain.com>"
        return java.util.Arrays.asList(new String []{ getDisplayName() + "<" + getEmail() + ">"});
    }


    /***************************************************************************************
     *  Implementing IResource.
     ***************************************************************************************/

    /**
     * Returns the calendar for the person.  The calendar will contain the list
     * of specific non-working days.
     *
     * @param is_fiscal
     * @return a PnCalendar for this resource.
     */
    public PnCalendar getCalendar(boolean is_fiscal) {
        return null;
    }


    /**
     * Returns the list of time periods that this person is typically available
     * or working.
     *
     * @return an ArrayList of WorkingTime objects for a calendar week.
     */
    public ArrayList getAvailableTimes() {
        return null;
    }

    /**
     * Returns the list days that this person is not available on a recurring
     * weekly basis.
     *
     * @return an ArrayList of Date objects with the DAY field set to the
     * non-working day.
     */
    public ArrayList getNonAvailableDays() {
        return null;
    }

    /**
        Returns the list dates that this person is not available in the specified time period.
        @param start_date the inclusive date used to check for non-working dates.
        @param end_date the inclusive date usded to check for non-working dates.
        @return an ArrayList of Date objects for each non-working date.
    */
    public ArrayList getNonAvailableDates(java.util.Date start_date, java.util.Date end_date) {
        return null;
    }



    /**
     * Returns an instantiated Person object given an email address.
     *
     * @param email  The email address of the person to be loaded
     * @return An instantiated person object if one can be found, null if not
     */
    public static Person getInstance (String email) {
        Person person = net.project.base.DefaultDirectory.lookupByEmail (email);
        return person;
    }

    /**
     * Gets the User object for this person . The user object can be loosely
     * called as the authenticated Person object in the system . Sometimes ,
     * this user object is useful for enabling or disabling the security . In
     * that case , this method can be used ....
     *
     * @return User
     */
    public User getUserForPerson() {
        User user = new User(this.personID);
        return user;
    }

    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     *
     * @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<person>\n");
        xml.append (getXMLProperties());
        xml.append("</person>\n");

        return xml.toString();
    }

    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     *
     * @return XML representation
     */
    public String getXMLProperties() {
        StringBuffer xml = new StringBuffer();

        xml.append("<person_id>" + XMLUtils.escape(this.personID) + "</person_id>\n");
        xml.append("<first_name>" + XMLUtils.escape(this.firstName) + "</first_name>\n");
        xml.append("<last_name>" + XMLUtils.escape(this.lastName) + "</last_name>\n");
        xml.append("<middle_name>" + XMLUtils.escape(this.middleName) + "</middle_name>\n");
        xml.append("<hasLicense>" + XMLUtils.escape(net.project.util.Conversion.booleanToString(this.isLicensed())) + "</hasLicense>\n");

        if( this.displayName != null && !this.displayName.trim().equals(""))
            xml.append("<full_name>" + XMLUtils.escape(this.displayName) + "</full_name>\n");
        else
            xml.append("<full_name>" + XMLUtils.escape(getDisplayName(this.email)) + "</full_name>\n");

        // added for xml / xsl compatibility
        xml.append("<full-name>" + XMLUtils.escape(this.displayName) + "</full-name>\n");
        xml.append("<username>" + XMLUtils.escape(this.username) + "</username>\n");
        xml.append("<email_address>" + XMLUtils.escape(this.email) + "</email_address>\n");
        xml.append("<responsibilities>" + XMLUtils.escape(this.responsibilities) + "</responsibilities>\n");
        xml.append("<title>" + XMLUtils.escape(this.memberTitle) + "</title>\n");
//        xml.append("<company>" + XMLUtils.escape(m_company) + "</company>\n");

        if (this.personStatus != null ) {
            xml.append("<user_status>" + XMLUtils.escape(this.personStatus.getID()) + "</user_status>\n");
        }

        if (this.lastLogin != null) {
            xml.append ( "<last_login>" +XMLUtils.formatISODateTime(this.lastLogin) + "</last_login>\n");
        } else {
            xml.append ( "<last_login>"+ PropertyProvider.get("prm.resource.person.lastlogin.never.name")+"</last_login>");
        }
        
        if(PropertyProvider.getBoolean("prm.global.skype.isenabled", false)){
        	xml.append("<skype>" + XMLUtils.escape(skype) + "</skype>\n");
        }
        
        xml.append("<RosterEntryType>" + getRosterEntryType() + "</RosterEntryType>\n");
        
        xml.append("<domain_id>" + getDomaninID() + "</domain_id>\n");
        
        //When we create a license the generic user doesn't have a salary.
        if(getSalary()!=null){        	
	        xml.append("<cost_by_hour>" + getSalary().getCostByHour() + "</cost_by_hour>\n");
        } else {
        	xml.append("<cost_by_hour>" + "0.0" + "</cost_by_hour>\n");
        }
        

        if (this.address != null)
            xml.append (this.address.getXMLBody());

        if(this.personStatus != null)
            xml.append(this.personStatus.getXMLBody());

        return xml.toString();
    }

    /**
     * Converts the object to XML representation. This method returns the object
     * as XML text.
     *
     * @return XML representation
     */
    public String getXML() {
        return( IXMLPersistence.XML_VERSION + getXMLBody() );
    }

    /***************************************************************************************
    *  Implementing IJDBCPersistence
    ***************************************************************************************/

    /**
     * Is this object loaded from database persistence?
     *
     * @return
     */
    public boolean  isLoaded() {
        return this.isLoaded;
    }

    /**
     * Creates a person stub entry. The resulting stub must still be registered
     * and a profile created.
     *
     * @throws PersistenceException if there is a problem creating the stub
     */
    public void createStub () throws PersistenceException {
        DBBean db = new DBBean();

        try {
            db.setAutoCommit(false);

            // call create person with the member DBBean
            createStub (db);

            db.commit();
        } catch (SQLException sqle) {
        	Logger.getLogger(Person.class).error("Person.createStub() threw an SQLException: " + sqle);
            throw new PersistenceException("Person create operation failed: " + sqle, sqle);
        } finally {
            try {
                db.rollback();
            } catch (java.sql.SQLException sqle) {
                // Simply release
            }

            db.release();
        }
    }

    /**
     * Creates a person stub entry.
     * The resulting stub must still be registered and a profile created. <br>
     * <b>Note:</b> This does NOT release the DBBean.
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if a database problem occurs
     */
    public void createStub (DBBean db) throws SQLException {
        net.project.base.DefaultDirectory.createPersonStub (this, db);
    }

    /**
     * Store this person into the database.  Can only update a pn_person record.
     * Inserts must be done via.
     */
    public void store() throws PersistenceException {
        throw new UnsupportedOperationException("Person.store operation is not supported.");
    }

    /**
     * Load the person from the database.
     */
    public void load() throws PersistenceException {

        StringBuffer query = new StringBuffer();
        String selectionValue = this.personID;
        query.append("select " + 
						  "p.person_id,  " + 
						  "p.display_name,  " +  
						  "p.first_name,  " + 
						  "p.last_name, " +    
						  "pp.middle_name, " +    
						  "p.email, " +    
						  "p.image_id, " + 
						  "pp.timezone_code, " +    
						  "p.user_status, " +   
						  "pp.company_name, " +    
						  "pp.address_id, " +    
						  "u.last_login, " +  
						  "u.username, " +  
						  "u.domain_id " +  
						"from " +  
						     "pn_person p, " + 
						     "pn_person_profile pp, " + 
						     "pn_user u " + 
						"where  " + 
							"p.person_id = pp.person_id (+) and " + 
							"p.person_id = ? and " +        
							"u.user_id(+) = p.person_id");

        DBBean db = new DBBean();

        try {
            int index = 0;
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, selectionValue);
            db.executePrepared();

            if (db.result.next()) {
                populatePersonProperties(db.result);

                // now load the address if it is available
                if (!Validator.isBlankOrNull(db.result.getString ("address_id"))) {

                    // get the person's address from persistence.
                    this.addressID = db.result.getString("address_id");
                    this.address = new Address(this.addressID);
                    this.address.load();
                }

                this.isLoaded = true;
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Person.class).error("Person.load: Could not load person from the DB." + sqle);
            throw new PersistenceException("Error loading person: " + sqle, sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Populates this Person object with properties from the specified resultset.
     * @param result the result set containing person properites; note that it does not
     * expect to find address information here, only person and user information
     * @throws SQLException if there is a problem populating
     */
    void populatePersonProperties(ResultSet result) throws SQLException {
        this.personID = result.getString("person_id");
        this.username = result.getString("username");
        this.displayName = result.getString("display_name");
        this.firstName = result.getString("first_name");
        this.middleName = result.getString("middle_name");
        this.lastName = result.getString("last_name");
        this.email = result.getString("email");
        this.imageId = result.getInt("image_id");
        setTimeZoneCode(result.getString("timezone_code"));
        this.personStatus = PersonStatus.getStatusForID(result.getString("user_status"));
        //this.hasLicense = net.project.util.Conversion.toBoolean(result.getString("has_license"));
        Date lastLoginTimestamp = result.getTimestamp("last_login");
        if (lastLoginTimestamp != null) {
            this.lastLogin = new Date(lastLoginTimestamp.getTime());
        }
        this.domaninID = result.getInt("domain_id");
        
        this.salary = new PersonSalary(personID);   
        
        
    }

    public static String getDisplayName (String email) {
        String qstrGetName = "select display_name from pn_person where lower(email) = lower('" + email + "')";
        DBBean db = new DBBean();
        String name = null;

        try {
            db.executeQuery (qstrGetName);
            name = ( db.result.next() ) ? db.result.getString ("display_name") : email;
        } catch (SQLException sqle) {
            name = email;
        } finally {
            db.release();
        }

        return name;
    }

    /**
     * Returns the display name of the person with the specified id.
     * <b>Note:</b> This method performs a database query.
     * @param personID the ID of the person to get the display name for
     * @return the display name or the specified personID if no person is
     * found with that ID or there is an error finding their display name
     */
    public static String getDisplayNameForID (String personID) {

        String qstrGetName = "select display_name from pn_person where person_id = ? ";
        DBBean db = new DBBean();
        String name = null;

        try {
            db.prepareStatement(qstrGetName);
            db.pstmt.setString(1, personID);
            db.executePrepared();
            name = (db.result.next()) ? db.result.getString("display_name") : personID;
        } catch (SQLException sqle) {
            name = personID;
        } finally {
            db.release();
        }

        return name;
    }

    public static String getEmailForID (String personID) {
        String qstrGetName = "select email from pn_person where person_id = '" + personID + "'";
        DBBean db = new DBBean();
        String email = null;

        try {
            db.executeQuery (qstrGetName);
            email = ( db.result.next() ) ? db.result.getString ("email") : personID;
        } catch (SQLException sqle) {
            email = personID;
        } finally {
            db.release();
        }

        return email;
    }

    /**
     * Changes the status of a user
     *
     * @param userStatus the status to change the user to
     */
    public void changeStatus (String userStatus) throws PersistenceException {
        DBBean db = new DBBean();

        try {
            changeStatus (userStatus, db);
        } finally {
            db.release();
        }
    }


    /**
     * Changes the status of a person's record. Specifically, all that happens
     * when invoking this method is that the person's "user status" value is
     * changed to that which is passed in.
     *
     * @param userStatus the status to change the user to
     * @param db a database connection already in process.
     * @throws PersistenceException If the database operation fails
     * @since Gecko 3
     */
    public void changeStatus (String userStatus, DBBean db) throws PersistenceException {
        String qstrUpdatePersonStatus = "update pn_person set user_status = '" + userStatus + "' where person_id = " + this.getID();

        // if user is being deleted - update email address with (Deleted) 
        if (PersonStatus.getStatusForID(userStatus).equals(PersonStatus.DELETED)) {
        	qstrUpdatePersonStatus = " update pn_person set user_status = '" + userStatus + "', " + 
        							 " email = email || '("+ PersonStatus.DELETED.getName() + ")'"+
        							 " where person_id = " + this.getID();        	
        }
        
        // FIRST, make sure nobody changes the app admins status
        if (this.getID().equals(net.project.security.SecurityProvider.APPLICATION_ADMINISTRATOR_ID)) {
            throw new PersistenceException (PropertyProvider.get("prm.directory.resource.person.changestatus.appadminuser.message"));
        }

        // If we aren't trying to delete the user, verify their email address is still valid
        // and update their status
        if (!PersonStatus.getStatusForID(userStatus).equals(PersonStatus.DELETED)) {
            // next, verify that if the new user status is not "deleted", the email address is still valid
            // this is important in the case where a deleted user has the same email as an active user
            Person searchPerson = net.project.base.DefaultDirectory.lookupByEmail(this.getEmail());

            if (searchPerson != null && !this.equals (searchPerson)) {
                throw new PersistenceException (PropertyProvider.get("prm.directory.resource.person.changestatus.email.nonunique.message", new Object[]{this.getEmail()}));
            }
        }

        // FINALLY, OK to proceed with the user status update
        try {
            db.executeQuery (qstrUpdatePersonStatus);
        } catch (SQLException sqle) {
            throw new PersistenceException ("DefaultDirectory.changePersonStatus(): threw an SQLException: ", sqle);
        }
    }

    /**
     * Returns the value for the <code>value</code> attribute of the HTML
     * option.
     *
     * @return a <code>String</code> value which will become the value="?" part
     * of the option tag.
     */
    public String getHtmlOptionValue() {
        return personID;
    }

    /**
     * Returns the value for the content part of the HTML option.
     *
     * @return a <code>String</code> value that will be displayed for this
     * html option.
     */
    public String getHtmlOptionDisplay() {
        return displayName;
    }

    public void remove() throws PersistenceException {
        throw new net.project.persistence.RemoveException ("Person does not support the remove operation");
    }

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
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
	 * @return the invite
	 */
	public String getInvite() {
		return invite;
	}

	/**
	 * @param invite the invite to set
	 */
	public void setInvite(String invite) {
		this.invite = invite;
	}

	/**
	 * @return the roles
	 */
	public String[] getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	/**
	 * @return the isOnline
	 */
	public boolean isOnline() {
		return isOnline;
	}

	/**
	 * @param isOnline the isOnline to set
	 */
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	/**
	 * @return the domaninID
	 */
	public int getDomaninID() {
		return domaninID;
	}

	/**
	 * @param domaninID the domaninID to set
	 */
	public void setDomaninID(int domaninID) {
		this.domaninID = domaninID;
	}

	public PersonSalary getSalary() {
		return salary;
	}

	public void setSalary(PersonSalary salary) {
		this.salary = salary;
	}
	
	
	
	
}


