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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.resource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.Finder;
import net.project.persistence.PersistenceException;
import net.project.schedule.Task;

/**
 * Object which consolidates all places that licenses are loaded into one
 * location.
 *
 * @author Matthew Flower
 * @since Version 7.5
 */
public class AddressFinder extends Finder {
    /** Points to the pn_address.address_id column. */
    public static final ColumnDefinition ADDRESS_ID_COLUMN = new ColumnDefinition("a.address_id", "Address ID");

    /**
     * Basis of the query we are going to run.  Where clauses are tacked onto
     * this query.
     */
    private final String BASE_SQL_STATEMENT =
        "select " +
        "    address_id, address1, address2, address3, city, state_provence, " +
        "    zipcode, country_code, office_phone, home_phone, fax_phone, " +
        "    pager_phone, mobile_phone, pager_email, website_url,address4, " +
        "    address5, address6, address7 " +
        "from " +
        "    pn_address a " +
        "where " +
        "    1=1 ";

    /**
     * Get the SQL statement which without any additional where clauses, group by, or
     * order by statements.
     * <p>
     * The SQL statement will include a <code>SELECT</code> part, a <code>FROM</code>
     * part and the <code>WHERE</code> keyword.  It will include any conditional
     * expressions required to perform joins. All additional conditions will
     * be appended with an <code>AND</code> operator.
     * </p>
     *
     * @return a <code>String</code> value containing the default sql statement
     * without any additional adornments.
     */
    protected String getBaseSQLStatement() {
        return BASE_SQL_STATEMENT;
    }

    /**
     * Find all address records from the database that correspond to the finder
     * filters added to this finder (if any.)
     *
     * @return a <code>java.util.List</code> object containing zero or more
     * Address objects.
     * @throws PersistenceException if there is an error loading address
     * information from the database.
     */
    public List find() throws PersistenceException {
        return loadFromDB();
    }

    /**
     * Load a specific address from the database.
     *
     * @param addressToLoad a <code>Address</code> object that we will populate
     * from the database.
     * @return a <code>boolean</code> value indicating whether the address
     * could be found.
     * @throws PersistenceException if there is an internal error loading the
     * address from the database.
     */
    public boolean find(Address addressToLoad) throws PersistenceException {
        return loadFromDB(addressToLoad);
    }

    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     *
     * @param databaseResults a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.  The ResultSet is assumed
     * to be on a current row.
     * @return a <code>Object</code> subclass specific to your finder that has
     * been populated with data.
     * @throws SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet databaseResults) throws SQLException {
        return createObjectForResultSetRow(databaseResults, new Address());
    }

    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link Task}
     * object.  This particular version of the createObjectForResultSetRow method
     * is available so the user can fill up an preexisting object with data more
     * easily.
     *
     * @param result a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.  The ResultSet is assumed
     * to be on a current row.
     * @param object a preexisting object that needs to be filled with data.
     * @return a <code>Object</code> subclass specific to your finder that has
     * been populated with data.
     * @throws SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet result, Object object) throws SQLException {
        Address address = (Address)object;

        address.id = result.getString("address_id");
        address.address1 = result.getString("address1");
        address.address2 = result.getString("address2");
        address.address3 = result.getString("address3");
        address.city = result.getString("city");
        address.state = result.getString("state_provence");
        address.zipcode = result.getString("zipcode");
        address.country = result.getString("country_code");
        address.officePhone = result.getString("office_phone");
        address.homePhone = result.getString("home_phone");
        address.faxPhone = result.getString("fax_phone");
        address.pagerPhone = result.getString("pager_phone");
        address.mobilePhone = result.getString("mobile_phone");
        address.pagerEmail = result.getString("pager_email");
        address.websiteURL = result.getString("website_url");
        address.address4 = result.getString("address4");
        address.address5 = result.getString("address5");
        address.address6 = result.getString("address6");
        address.address7 = result.getString("address7");
        address.m_isLoaded = true;

        return address;
    }

}
