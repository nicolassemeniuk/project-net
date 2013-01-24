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
package net.project.admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.project.base.finder.Finder;
import net.project.persistence.PersistenceException;

public class DocumentRepositoryFinder extends Finder {
    private String sql = 
        "select " +
        "  repository_id, " +
        "  repository_path, " +
        "  is_active " +
        "from " +
        "  pn_doc_repository_base ";

    /**
     * Get the SQL statement which without any additional where clauses, group
     * by, or order by statements. <p> The SQL statement will include a
     * <code>SELECT</code> part, a <code>FROM</code> part and the
     * <code>WHERE</code> keyword.  It will include any conditional expressions
     * required to perform joins. All additional conditions will be appended
     * with an <code>AND</code> operator. </p>
     * 
     * @return a <code>String</code> value containing the default sql statement
     *         without any additional adornments.
     */
    protected String getBaseSQLStatement() {
        return sql;
    }

    /**
     * Find all document repository roots in the database.
     *
     * @return a <code>List</code> object containing zero or more document
     * repository bases.
     * @throws PersistenceException if there is any error loading these bases
     * from the database.
     */
    public List findAll() throws PersistenceException {
        return loadFromDB();
    }

    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link net.project.schedule.Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     * 
     * @param result a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.  The ResultSet is assumed to be
     * on a current row.
     * @return a <code>Object</code> subclass specific to your finder that has
     *         been populated with data.
     * @throws java.sql.SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet result) throws SQLException {
        DocumentRepositoryRoot docRoot = new DocumentRepositoryRoot();

        docRoot.setID(result.getString("repository_id"));
        docRoot.setPath(result.getString("repository_path"));
        docRoot.setActive(result.getBoolean("is_active"));

        return docRoot;
    }
}
