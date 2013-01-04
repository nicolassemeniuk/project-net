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

import java.io.Serializable;
import java.sql.SQLException;

import net.project.base.finder.NumberComparator;
import net.project.base.finder.NumberFilter;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.xml.XMLUtils;

/**
 * The address and related information of a person, place or thing.
 *
 * @author Bern McCarty
 * @since 01/2000
 */
public class Address implements IJDBCPersistence, IXMLPersistence, Serializable {
    protected String id = null;
    protected String address1 = null;
    protected String address2 = null;
    protected String address3 = null;
    protected String address4 = null;
    protected String address5 = null;
    protected String address6 = null;
    protected String address7 = null;
    protected String city = null;
    protected String cityDistrict = null;
    protected String region = null;
    protected String state = null;
    protected String zipcode = null;
    protected String country = null;
    protected String officePhone = null;
    protected String homePhone = null;
    protected String faxPhone = null;
    protected String pagerPhone = null;
    protected String mobilePhone = null;
    protected String pagerEmail = null;
    protected String websiteURL = null;
    /** Indicates if the address has been loaded from the database. */
    protected boolean m_isLoaded = false;

    /** Current user */
    protected User user = null;


    /** Bean constructor */
    public Address() {
    }

    /** Constructor setting database id of the Address */
    public Address(String addressId) {
        this.id = addressId;
    }

    /**
     * Sets the current user.  This should be called after instantiating a
     * new Address object.
     * @param user the current user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /** @deprecated use setID() */
    public void setId(String addressId) {
        this.id = addressId;
    }

    /** @deprecated use getID() */
    public String getId() {
        return id;
    }


    public void setID(String addressId) {
        this.id = addressId;
    }

    public String getID() {
        return id;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress4(String address) {
        this.address4 = address;
    }

    public String getAddress4() {
        return address4;
    }

    public void setAddress5(String address) {
        this.address5 = address;
    }

    public String getAddress5() {
        return address5;
    }

    public void setAddress6(String address6) {
        this.address6 = address6;
    }

    public String getAddress6() {
        return address6;
    }

    public void setAddress7(String address7) {
        this.address7 = address7;
    }

    public String getAddress7() {
        return address7;
    }


    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setPrefecture(String prefecture) {
        setState(prefecture);
    }

    public String getPrefecture() {
        return getState();
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setCityDistrict(String district) {
        this.cityDistrict = district;
    }

    public String getCityDistrict() {
        return this.cityDistrict;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return this.region;
    }


    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setFaxPhone(String faxPhone) {
        this.faxPhone = faxPhone;
    }

    public String getFaxPhone() {
        return faxPhone;
    }

    public void setPagerPhone(String pagerPhone) {
        this.pagerPhone = pagerPhone;
    }

    public String getPagerPhone() {
        return pagerPhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setPagerEmail(String pagerEmail) {
        this.pagerEmail = pagerEmail;
    }

    public String getPagerEmail() {
        return pagerEmail;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }
   
    /**  Clear the properties of this user object */
    public void clear() {
        id = null;
        address1 = null;
        address2 = null;
        address3 = null;
        address4 = null;
        address5 = null;
        address6 = null;
        address7 = null;
        city = null;
        cityDistrict = null;
        region = null;
        state = null;
        zipcode = null;
        country = null;
        officePhone = null;
        homePhone = null;
        faxPhone = null;
        pagerPhone = null;
        mobilePhone = null;
        pagerEmail = null;
        websiteURL = null;
    }





    /***************************************************************************************
     *  Implementing IXMLPersistence
     ***************************************************************************************/


    /**
     Converts the object to XML representation without the XML version tag.
     This method returns the object as XML text.
     @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<Address>\n");
        xml.append("<id>" + XMLUtils.escape(id) + "</id>\n");
        xml.append("<address1>" + XMLUtils.escape(address1) + "</address1>\n");
        xml.append("<address2>" + XMLUtils.escape(address2) + "</address2>\n");
        xml.append("<address3>" + XMLUtils.escape(address3) + "</address3>\n");
        xml.append("<address4>" + XMLUtils.escape(address4) + "</address4>\n");
        xml.append("<address5>" + XMLUtils.escape(address5) + "</address5>\n");
        xml.append("<address6>" + XMLUtils.escape(address6) + "</address6>\n");
        xml.append("<address7>" + XMLUtils.escape(address7) + "</address7>\n");
        xml.append("<city>" + XMLUtils.escape(city) + "</city>\n");
        xml.append("<cityDistrict>" + XMLUtils.escape(cityDistrict) + "</cityDistrict>\n");
        xml.append("<region>" + XMLUtils.escape(region) + "</region>\n");
        xml.append("<state>" + XMLUtils.escape(state) + "</state>\n");
        xml.append("<zipcode>" + XMLUtils.escape(zipcode) + "</zipcode>\n");
        xml.append("<country>" + XMLUtils.escape(country) + "</country>\n");
        xml.append("<officePhone>" + XMLUtils.escape(officePhone) + "</officePhone>\n");
        xml.append("<homePhone>" + XMLUtils.escape(homePhone) + "</homePhone>\n");
        xml.append("<pagerPhone>" + XMLUtils.escape(pagerPhone) + "</pagerPhone>\n");
        xml.append("<mobilePhone>" + XMLUtils.escape(mobilePhone) + "</mobilePhone>\n");
        xml.append("<pagerEmail>" + XMLUtils.escape(pagerEmail) + "</pagerEmail>\n");
        xml.append("<websiteURL>" + XMLUtils.escape(websiteURL) + "</websiteURL>\n");
        xml.append("</Address>\n");

        return xml.toString();
    }


    /**
     Converts the object to XML representation.
     This method returns the object as XML text.
     @return XML representation
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }


    /***************************************************************************************
     *  Implementing IJDBCPersistence
     ***************************************************************************************/

    public boolean isLoaded() {
        return m_isLoaded;
    }


    /**
     Insert of update an Address in the database.
     If the address_id is null, a new pn_address will be created in the database
     and the address_id value of this object will be set wtih the new id.
     If the address_is is non-null, the address record in the database will be updated.
     */
    public void store()
        throws PersistenceException {

        DBBean db = new DBBean();

        try {
            store(db);
        } finally {
            db.release();
        }
    }


    /**
     * Insert or update an address in the database.  This version of store() requires
     * that you pass a pre-existing DBBean to the method.  This method will not
     * commit or rollback the transaction, making it ideal for situations in which you
     * want to save an address as part of a transaction.
     *
     * As with the parameter-less store(), if the address_id, a new pn_address will be
     * created in the database.  If the address_id is not null, the existing address
     * record will be updated.
     */
    public void store(DBBean db) throws PersistenceException {
        try {

            // if address_id is null, we must create a new address in the database.
            if (this.id == null) {
                // call Stored Procedure to create a new empty Address for the new person.
                // The Person will fill in the fields later when they register.
                db.prepareCall("{call PROFILE.CREATE_ADDRESS(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

                int index = 0;
                int addressIDIndex;

                db.cstmt.setString(++index, this.user.getID());
                db.cstmt.setString(++index, this.address1);
                db.cstmt.setString(++index, this.address2);
                db.cstmt.setString(++index, this.address3);
                db.cstmt.setString(++index, this.city);
                db.cstmt.setString(++index, this.state);
                db.cstmt.setString(++index, this.zipcode);
                db.cstmt.setString(++index, this.country);
                db.cstmt.setString(++index, this.officePhone);
                db.cstmt.setString(++index, this.homePhone);
                db.cstmt.setString(++index, this.mobilePhone);
                db.cstmt.setString(++index, this.faxPhone);
                db.cstmt.setString(++index, this.pagerPhone);
                db.cstmt.setString(++index, this.pagerEmail);
                db.cstmt.setString(++index, this.websiteURL);
                db.cstmt.setString(++index, this.address4);
                db.cstmt.setString(++index, this.address5);
                db.cstmt.setString(++index, this.address6);
                db.cstmt.setString(++index, this.address7);
                db.cstmt.registerOutParameter((addressIDIndex = ++index), java.sql.Types.VARCHAR);
                db.executeCallable();
                this.id = db.cstmt.getString(addressIDIndex);
            }

            // if address_id is non-null, update
            else {
                StringBuffer sb = new StringBuffer(100);

                sb.append("update pn_address set address_id=" + this.id);
                if (address1 != null) sb.append(", address1=" + DBFormat.varchar2(this.address1));
                if (address2 != null) sb.append(", address2=" + DBFormat.varchar2(this.address2));
                if (address3 != null) sb.append(", address3=" + DBFormat.varchar2(this.address3));
                if (address4 != null) sb.append(", address4=" + DBFormat.varchar2(this.address4));
                if (address5 != null) sb.append(", address5=" + DBFormat.varchar2(this.address5));
                if (address6 != null) sb.append(", address6=" + DBFormat.varchar2(this.address6));
                if (address7 != null) sb.append(", address7=" + DBFormat.varchar2(this.address7));
                if (city != null) sb.append(", city=" + DBFormat.varchar2(this.city));
                if (state != null) sb.append(", state_provence=" + DBFormat.varchar2(this.state));
                if (zipcode != null) sb.append(", zipcode=" + DBFormat.varchar2(this.zipcode));
                if (country != null) sb.append(", country_code=" + DBFormat.varchar2(this.country));
                if (officePhone != null) sb.append(", office_phone=" + DBFormat.varchar2(this.officePhone));
                if (homePhone != null) sb.append(", home_phone=" + DBFormat.varchar2(this.homePhone));
                if (faxPhone != null) sb.append(", fax_phone=" + DBFormat.varchar2(this.faxPhone));
                if (pagerPhone != null) sb.append(", pager_phone=" + DBFormat.varchar2(this.pagerPhone));
                if (mobilePhone != null) sb.append(", mobile_phone=" + DBFormat.varchar2(this.mobilePhone));
                if (pagerEmail != null) sb.append(", pager_email=" + DBFormat.varchar2(this.pagerEmail).trim());
                if (websiteURL != null) sb.append(", website_url=" + DBFormat.varchar2(this.websiteURL));
                sb.append(" where address_id=" + id);
                db.setQuery(sb.toString());
                db.executeQuery();
                m_isLoaded = true;
            }

        } catch (SQLException sqle) {
            throw new PersistenceException("Failed to store address " + sqle, sqle);
        }

    }


    /**
     * Load the address from the database.
     *
     * @throws PersistenceException if there is an error loading the address
     * from the database.
     */
    public void load()
        throws PersistenceException {
        if (id == null) {
            this.m_isLoaded = false;
            throw new NullPointerException("address ID was null");
        }

        AddressFinder finder = new AddressFinder();

        NumberFilter addressIDFilter = new NumberFilter("10", AddressFinder.ADDRESS_ID_COLUMN, false);
        addressIDFilter.setComparator(NumberComparator.EQUALS);
        addressIDFilter.setSelected(true);
        addressIDFilter.setNumber(new Integer(id));

        finder.addFinderFilter(addressIDFilter);

        if (!finder.find(this)) {
            m_isLoaded = false;
            throw new PersistenceException("Address could not be loaded from database.");
        }
    }


    /**
     * Soft delete this address in the database.
     */
    public void remove()
        throws PersistenceException {
        if (id == null)
            throw new NullPointerException("address ID was null");

        DBBean db = new DBBean();
        try {
            db.setQuery("update pn_address set record_status='D' where address_id=" + this.id);
            db.executeQuery();
            this.m_isLoaded = false;
        } catch (SQLException sqle) {
            throw new PersistenceException("Failed to remove address " + sqle, sqle);
        } finally {
            db.release();
        }
    }

}
