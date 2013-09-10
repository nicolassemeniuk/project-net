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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.base.finder.Finder;
import net.project.base.finder.FinderListener;
import net.project.base.finder.FinderListenerAdapter;
import net.project.base.finder.WhereClauseFilter;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.schedule.Task;
import net.project.space.Space;
import net.project.util.Conversion;
import net.project.util.ParseString;

public class PersonFinder extends Finder {
	
    private final String BASE_SQL_STATEMENT =
        "select " +
        "  p.person_id, p.display_name display_name, p.first_name, p.last_name, " +
        "  p.middle_name, p.email, p.timezone_code, p.user_status, " +
        "  p.company_name, p.address_id, p.has_license, u.last_login, " +
        "  u.username "+
        "from " +
        "  pn_person_view p, " +
        "  pn_user_view u  " +
        "where" +
        "  u.user_id(+) = p.person_id ";
    private Map addressCache = new HashMap();

    private FinderListener listener = new FinderListenerAdapter() {
        /**
         * This method is called just before running <code>executePrepared</code> on
         * the sql statement constructed by calling <code>getSQLStatement</code>.
         * This allows a user to set parameters that are required to execute a
         * prepared statement.
         *
         * @param db a {@link DBBean} that is just about to
         * call {@link DBBean#executePrepared}.
         * @throws SQLException if an error occurs while modifying the
         * <code>DBBean</code>.
         */
        public void preExecute(DBBean db) throws SQLException {
            AddressFinder addressFinder = new AddressFinder();

            WhereClauseFilter whereClauseFilter = new WhereClauseFilter(
                "address_id in ( " +
                "  select address_id " +
                "  from " +
                "    pn_person_view p, " +
                "    pn_user_view u " +
                "  where" +
                "    u.user_id(+) = p.person_id "+
                getWhereClause()+
                ")"
            );
            whereClauseFilter.setSelected(true);
            addressFinder.addFinderFilter(whereClauseFilter);

            try {
                List addresses = addressFinder.find();

                for (Iterator it = addresses.iterator(); it.hasNext();) {
                    Address a = (Address)it.next();
                    addressCache.put(a.getID(), a);
                }
            } catch (PersistenceException e) {
                throw new SQLException("Unable to load addresses for person finder.");
            }
        }
    };

    public PersonFinder() {
        addFinderListener(listener);
    }

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

    public List find() throws PersistenceException {
        return loadFromDB();
    }

    public List findForSpace(Space space) throws PersistenceException {
        addWhereClause("person_id in (select person_id from pn_space_has_person where space_id = " + space.getID() + ")");
        return loadFromDB();
    }


    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     *
     * @param result a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.  The ResultSet is assumed
     * to be on a current row.
     * @return a <code>Object</code> subclass specific to your finder that has
     * been populated with data.
     * @throws SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet result) throws SQLException {
        Person person = new Person();

        person.setID(result.getString("person_id"));
        person.setUserName(result.getString("username"));
        person.setDisplayName(result.getString("display_name"));
        person.setFirstName(result.getString("first_name"));
        person.setMiddleName(result.getString("middle_name"));
        person.setLastName(result.getString("last_name"));
        person.setEmail(result.getString("email"));
        person.setTimeZoneCode(result.getString("timezone_code"));
        person.setStatus(PersonStatus.getStatusForID(result.getString("user_status")));
        person.setLicensed(Conversion.toBoolean(result.getString("has_license")));
        person.setLastLogin(result.getTimestamp("last_login"));

        // now load the address if it is available
        if (!ParseString.isEmpty(result.getString ("address_id"))) {
            person.setAddressID(result.getString("address_id"));

            Address address = (Address)addressCache.get(result.getString("address_id"));
            if (address != null) {
                person.setAddress(address);
            }
        }
        person.setSalary(new PersonSalary(result.getString("person_id")));

        person.isLoaded = true;

        return person;
    }
}
