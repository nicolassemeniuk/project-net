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
package net.project.resource.report.newuserreport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.Finder;
import net.project.persistence.PersistenceException;
import net.project.resource.Invitee;
import net.project.schedule.Task;

/**
 * Class to find <code>Invitee</code> objects that provide the information for
 * the New User Report.  This finder is only intended for use in creating the
 * report, and should not be used outside of a reporting context.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class NewUserReportDataFinder extends Finder {
    /** Space ID Column. */
    public static ColumnDefinition SPACE_ID_COLUMN = new ColumnDefinition("iu.space_id", "prm.resource.report.newuserreport.columndef.spaceid.name");
    /** Date Invited Column. */
    public static ColumnDefinition DATE_INVITED_COLUMN = new ColumnDefinition("iu.date_invited", "prm.resource.report.newuserreport.columndef.dateinvited.name");
    /** Date Responded Column. */
    public static ColumnDefinition DATE_RESPONDED_COLUMN = new ColumnDefinition("iu.date_responded", "prm.resource.report.newuserreport.columndef.dateresponded.name");

    /**
     * SQL Statement used as the basis of the query that will populate the
     * invitee objects.
     */
    private String BASE_SQL_STATEMENT =
        "select "+
        "  invitor.display_name as invitor, "+
        "  pv.display_name, "+
        "  iu.invitee_email, "+
        "  iu.date_invited, "+
        "  iu.date_responded, "+
        "  iu.invitee_responsibilities, "+
        "  iu.invited_status, "+
        "  iu.invitation_acted_upon "+
        "from "+
        "  pn_person_view pv, "+
        "  pn_invited_users iu, "+
        "  pn_person_view invitor "+
        "where "+
        "  pv.person_id = iu.person_id "+
        "  and iu.invitor_id = invitor.person_id ";

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
     * Find all Invitee records that match the filters applied to this finder.
     *
     * @return a <code>List</code> containing zero or more
     * {@link net.project.resource.Invitee} records.
     * @throws PersistenceException if any database errors are thrown while
     * querying.
     */
    public List findAllMatches() throws PersistenceException {
        return loadFromDB();
    }

    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     *
     * @param rs a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.  The ResultSet is assumed
     * to be on a current row.
     * @return a <code>Object</code> subclass specific to your finder that has
     * been populated with data.
     * @throws SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet rs) throws SQLException {
        Invitee invitee = new Invitee();

        invitee.setInvitorDisplayName(rs.getString("invitor"));
        invitee.setDisplayName(rs.getString("display_name"));
        invitee.setEmail(rs.getString("invitee_email"));
        invitee.setInvitedDate(rs.getTimestamp("date_invited"));
        invitee.setResponseDate(rs.getTimestamp("date_responded"));
        invitee.setResponsibilities(rs.getString("invitee_responsibilities"));
        invitee.setResponded(rs.getBoolean("invitation_acted_upon"));

        return invitee;
    }
}
