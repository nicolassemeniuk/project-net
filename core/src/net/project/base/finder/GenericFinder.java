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
package net.project.base.finder;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is designed for when you need to create a quick finder that is not
 * of enough consequence to be worth writing a full class.  This finder still
 * has the benefit, however, of being able to accept finder filter lists and
 * sorters.
 *
 * This class is probably only useful for places where you need to use a finder
 * filter or finder sorter on arbitrary SQL.  The rest of the time, using the
 * DBBean directly is probably more useful.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class GenericFinder extends Finder {
    /**
     * The base query that is going to be sent to the finder architecture.  This
     * query is passed into the constructor.
     */
    private String baseQuery;

    /**
     * Public constructor to create a new instance of a GenericFinder.
     *
     * @param baseQuery a <code>String</code> containing a SQL statement that is
     * going to be run by this finder.  This SQL statement needs to be in the
     * standard finder form in that it needs to have a where clause already but
     * no group by clause.  If you need to add group by statements, add them
     * afterwards using the {@link #addGroupByClause} method.
     */
    public GenericFinder(String baseQuery) {
        this.baseQuery = baseQuery;
    }

    /**
     * Add a new "Group By" clause to the sql statement.  This should be the only
     * method that you should usually add group by statements to a sql statement.
     * Adding them directly to the statement is dangerous, as we often want to
     * add where clauses.
     *
     * @param groupByClause a <code>String</code> object containing the name of
     * the field you wish to group by.  It is not necessary to include commas or
     * the phrase "group by".  This will automatically be added internally at the
     * right time.
     */
    public void addGroupByClause(String groupByClause) {
        super.addGroupByClause(groupByClause);
    }

    /**
     * Remove any group by clauses that have been stored internally.  None will
     * be applied to the SQL statement unless new ones are added.
     */
    public void clearGroupByClauses() {
        super.clearGroupByClauses();
    }

    /**
     * Add a new "Having" clause to the SQL Statement.  This should be the only
     * method that you should use to add "having" statements to a SQL statement.
     * Adding them directly to the statement is dangerous.  If you do, adding
     * filters, groupings, and sorters later won't work because this object
     * won't know where to append them to.
     *
     * @param havingClause a <code>String</code> object containing a having
     * statement.  You do not need to include an "and" or "having" statement, as
     * these will be added automatically when creating the SQL statement.
     */
    public void addHavingClause(String havingClause) {
        super.addHavingClause(havingClause);
    }

    /**
     * Remove any having clauses that have been stored internally.  None will
     * be applied to the SQL statement unless new ones are added.
     */
    public void clearHavingClauses() {
        super.clearHavingClauses();
    }

    //Unused
    protected String getBaseSQLStatement() { return baseQuery; }
    protected Object createObjectForResultSetRow(ResultSet databaseResults) throws SQLException { return null; }
}
