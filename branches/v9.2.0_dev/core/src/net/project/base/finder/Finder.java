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

 package net.project.base.finder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.EventListenerList;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

/**
 * Base class for any finder object in Project.Net.  This type of object should
 * be the primary pattern we use for querying the database.
 * <p>
 * Up until the creation of this class, the primary method that we used to query
 * the database was to embed database code directly in the load() and store()
 * methods of an object.  This method wasn't horrible, but it did have a major
 * downside - if you have objects to load a list of objects or a single object,
 * they used different code to do the same task.  To make matters worse, sometimes
 * the methods that would populate these data objects would get out of sync at
 * times.  This could mean, for example, that you populated a {@link net.project.schedule.Task}
 * object both from the {@link net.project.schedule.Task} object as well as in
 * the {@link net.project.schedule.Schedule} class.  If a user didn't know that
 * there were multiple load methods, they wouldn't update the other method and
 * not all private member variables were populated correctly.
 * </p>
 * <p>
 * This finder base class should eliminate those problems by providing a standard
 * place that all selection code should be isolated.  Updates and deletes usually
 * aren't as much of a problem, so they are being left in the classes for now,
 * in places such as Task.store() and Task.delete().
 * </p>
 * <p>
 * Note that you should still be implementing load() methods.  They should just
 * be delegating their functionality to a finder.
 * </p>
 * <p>
 * <u>How to use</u><br>
 * <b>Example 1</b> - Simple statement <br>
 * This example shows the simplest way to implement a Finder by concatentating
 * where clause parameters.
 * <ol>
 * <li>Create a sub-class of <code>Finder</code>
 * <li>Implement <code>getBaseSQLStatement</code>
 * <li>Implement <code>createObjectForResultSetRow</code>
 * <li>Add a <i>find</i> method with a body similar to the following: <code><pre>
 *     addWhereClause("some_column = " + someValue);
 *     return loadFromDB();
 * </pre></code>
 * </ol>
 * <b>Example 2</b> - Prepared statement with bound parameters <br>
 * It is preferable to use a PreparedStatement with bound parameters for
 * performance.  This example shows how to do this in a way that allows
 * different find methods to bind different parameters.
 * <ol>
 * <li>Create a sub-class of <code>Finder</code>
 * <li>Implement <code>getBaseSQLStatement</code>
 * <li>Implement <code>createObjectForResultSetRow</code>
 * <li>Add a <i>find</i> method with a body similar to the following: <code><pre>
 *     addWhereClause("some_column = ? ");
 *     FinderListener listener = new FinderListener() {
 *         public void preConstruct(Finder f) { }
 *         public void preExecute(DBBean db) throws SQLException {
 *             db.pstmt.setString(1, someValue);
 *         }
 *     };
 *     addFinderListener(listener);
 *     return loadFromDB(binder);
 * </pre></code>
 * </ol>
 * </p>
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public abstract class Finder {
    /**
     * This a flag returned by createObjectForResultSetRow if the row did not
     * result in a new unique object being created.  When this flag is returned,
     * nothing is added to the result set.
     */
    protected static Object NON_UNIQUE_ROW = "non_unique";

    /**
     * A list of <code>String</code> values that are going to be concatenated
     * with "AND" and added to the SQL Statement.
     */
    protected ArrayList whereClauses = new ArrayList();
    /**
     * A list of <code>String</code> objects which will be collected and added
     * to create a "group by" statement.
     */
    protected ArrayList groupByClauses = new ArrayList();
    /**
     * A list of <code>String</code> objects which will be collected and added
     * to create a "having" statement.
     */
    protected ArrayList havingClauses = new ArrayList();
    /**
     * A list of <code>String</code> objects which will be collected and added
     * together to create a "order by" statement.
     */
    protected ArrayList orderByClauses = new ArrayList();
    /**
     * A list of zero or more {@link net.project.base.finder.FinderFilter}
     * objects which will be translated into where clauses.
     */
    protected ArrayList finderFilters = new ArrayList();
    /**
     * A list of zero or more {@link net.project.base.finder.FinderSorter}
     * objects which will be translated into order by clauses.
     */
    protected ArrayList finderSorters = new ArrayList();
    /**
     * A list of {@link net.project.base.finder.FinderListener} objects which
     * will be notified prior to creating the where clause and again prior to
     * executing it.
     */
    protected EventListenerList finderListeners = new EventListenerList();
    /**
     * This value indicates the maximum number of rows that we are willing to
     * fetch from the database.  Zero is an Oracle default meaning "no limit".
     * The default is zero.
     */
    private int maximumRowsToFetch = 0;

    /**
     * Add a new listener to the list of finder listeners.  A finder listener
     * will receive callbacks at various times to inform interested code about
     * events that are occurring.
     *
     * @param l a <code>FinderListener</code> that will receive callbacks when
     * Finder events occur.
     */
    public void addFinderListener(FinderListener l) {
        finderListeners.add(FinderListener.class, l);
    }

    /**
     * Remove a listener that was listening to the finder listeners.
     *
     * @param l a <code>FinderListener</code> that was previously listening for
     * events that occur on this finder.
     */
    public void removeFinderListener(FinderListener l) {
        finderListeners.remove(FinderListener.class, l);
    }

    /**
     * Get all listeners that are listening for changes to this finder.
     *
     * @return an array of <code>FinderListener</code> objects that are listening
     * to changes made on this finder.
     */
    public FinderListener[] getFinderListeners() {
        return (FinderListener[])finderListeners.getListeners(FinderListener.class);
    }

    /**
     * Add a new where clause to the list of where clauses that are going to be
     * concatenated to the SQL statement.  This method is generally only used
     * by subclasses (hence the protected access scope.)  If client code wants
     * to apply a custom where clause, they should write a new "finder" method.
     *
     * @param whereClause a <code>String</code> value containing a boolean
     * statement which will become a where clause.  This statement should not
     * have an "and" statement in the beginning of it -- this will be added
     * automatically.
     */
    protected void addWhereClause(String whereClause) {
        whereClauses.add(whereClause);
    }

    /**
     * Clear out any where clauses that have been previously added to this finder
     * object.
     */
    protected void clearWhereClauses() {
        whereClauses.clear();
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
    protected void addGroupByClause(String groupByClause) {
        groupByClauses.add(groupByClause);
    }

    /**
     * Remove any group by clauses that have been stored internally.  None will
     * be applied to the SQL statement unless new ones are added.
     */
    protected void clearGroupByClauses() {
        groupByClauses.clear();
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
    protected void addHavingClause(String havingClause) {
        havingClauses.add(havingClause);
    }

    /**
     * Remove any having clauses that have been stored internally.  None will
     * be applied to the SQL statement unless new ones are added.
     */
    protected void clearHavingClauses() {
        havingClauses.clear();
    }

    /**
     * Remove any order by clauses that have been stored internally.  None will
     * be applied to the SQL statement unless new ones are added.
     */
    protected void clearOrderByClauses() {
        orderByClauses.clear();
    }

    /**
     * Add a single column that should be included in an "order by" statement.
     *
     * @param orderByColumn a <code>String</code> containing the name of a
     * column that you wish to order by.  This might also include "ASC or DESC"
     * if necessary.
     */
    protected void addOrderByClause(String orderByColumn) {
        orderByClauses.add(orderByColumn);
    }

    /**
     * Add custom where clauses and order by clauses to the SQL statement from
     * the specified finder ingredients.
     * @param finderIngredients the filters and sorters to add to the SQL
     * statement.
     */
    public void addFinderIngredients(FinderIngredients finderIngredients) {
        addFinderFilterList(finderIngredients.getFinderFilterList());
        addFinderSorterList(finderIngredients.getFinderSorterList());
    }

    /**
     * Add a custom where clause statement to the SQL statement.  This where
     * clause is embedded in a {@link net.project.base.finder.FinderFilter}
     * object.  A <code>FinderFilter</code> object is primarily concerned with
     * presenting the options to the user, but it also produce a where clause.
     *
     * Note that the filter will only be applied to the where clause if its
     * <code>selected</code> flag is set to true.
     *
     * @param finderFilter a <code>FinderFilter</code> object that will provide
     * a where cluase to the sql statement.
     */
    public void addFinderFilter(FinderFilter finderFilter) {
        finderFilters.addAll(finderFilter.getSelectedFilters());
    }

    /**
     * Add a list of custom where clause filters to the SQL statement.  These
     * where clauses is embedded in {@link net.project.base.finder.FinderFilter}
     * objects.  A <code>FinderFilter</code> object is primarily concerned with
     * presenting the options to the user, but it also produce a where clause.
     *
     * Note that the filter will only be applied to the where clause if its
     * <code>selected</code> flag is set to true.
     *
     * @param finderFilterList a <code>FinderFilterList</code> object which
     * contains zero or more <code>FinderFilter</code> objects.
     */
    public void addFinderFilterList(FinderFilterList finderFilterList) {
        // Since a FinderFilterList is a FinderFilter, we don't need to
        // access its selected filters
        // That will be done for us when the where clause is produced from
        // the FinderFilterList
        this.finderFilters.add(finderFilterList);
    }

    /**
     * Clear out any filters that are being stored internally.
     */
    public void clearFinderFilter() {
        finderFilters.clear();
    }

    /**
     * Add an order by statement to the sql statement which is embedded inside of
     * a {@link net.project.base.finder.FinderSorter} object.
     *
     * @param finderSorter a <code>FinderSorter</code> which will produce an
     * order by statement for the sql statement.
     */
    public void addFinderSorter(FinderSorter finderSorter) {
        finderSorters.add(finderSorter);
    }

    /**
     * Add a list of order by statements to the sql statement.
     *
     * @param sorterList a {@link net.project.base.finder.FinderSorterList} object
     * containing zero or more {@link net.project.base.finder.FinderSorter}
     * objects.
     */
    public void addFinderSorterList(FinderSorterList sorterList) {
        finderSorters.addAll(sorterList.getSelectedSorters());
    }
    /**
     * Clear out any <code>FilterSorter</code> objects that are being stored
     * internally.
     */
    public void clearFinderSorter() {
        finderSorters.clear();
    }

    /**
     * Clear out any where clauses, group by clauses, or objects that are modifying
     * the base results of the sql statement.
     */
    public void clear() {
        clearWhereClauses();
        clearGroupByClauses();
        clearHavingClauses();
        clearOrderByClauses();
        clearFinderFilter();
        clearFinderSorter();
        maximumRowsToFetch = 0;
    }

    /**
     * Get the maximum number of rows that we are willing to fetch from the
     * database.
     *
     * @return a <code>int</code> indicating the maximum number of rows we will
     * fetch from the database.  The value 0 indicates that we will fetch any
     * number of rows.
     */
    public int getMaximumRowsToFetch() {
        return maximumRowsToFetch;
    }

    /**
     * Set the maximum number of rows we are willing to fetch from the database.
     * The value "0" is the default.  This value means that any number of rows
     * will be fetched.
     *
     * @param maximumRowsToFetch a <code>int</code> indicating the maximum number
     * of rows to fetch from the database.  The calling class should pass 0 to
     * indicate that any number rows will be fetched.
     */
    public void setMaximumRowsToFetch(int maximumRowsToFetch) {
        this.maximumRowsToFetch = maximumRowsToFetch;
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
    protected abstract String getBaseSQLStatement();

    /**
     * Get the SQL statement that will be run to return results from the database.
     * Custom filters, sorters, where clauses, and order by statements will be
     * included in this version of the SQL Statement.
     *
     * @return a <code>String</code> value containing the final SQL statement that
     * will be run to return results.
     */
    protected final String getSQLStatement() {
        StringBuffer sqlStatement = new StringBuffer();

        //Let subclasses perform any final tinkering before building the SQL
        //statement
        preConstructSQLStatement();

        sqlStatement.append(getBaseSQLStatement());

        //Add the "Where" portion of the SQL statement.
        sqlStatement.append(getWhereClause());

        //Append all order by clauses to the SQL statement
        boolean orderByStatementAdded = false;
        Iterator sorterOperationIT = finderSorters.iterator();

        if (sorterOperationIT.hasNext()) {
            sqlStatement.append(" order by ");
            orderByStatementAdded = true;
        }

        while (sorterOperationIT.hasNext()) {
            FinderSorter sorter = (FinderSorter)sorterOperationIT.next();
            sqlStatement.append(sorter.getOrderByClause());
            if (sorterOperationIT.hasNext()) {
                sqlStatement.append(",");
            }
        }

        if (orderByClauses.size() > 0 && !orderByStatementAdded) {
            sqlStatement.append(" order by ");
            orderByStatementAdded = true;
        }

        //Add all the custom order by statements
        for (Iterator it = orderByClauses.iterator(); it.hasNext();) {
            sqlStatement.append((String)it.next());
            if (it.hasNext()) {
                sqlStatement.append(",");
            }
        }

        //If there are any group by clauses to be added, add them
        Iterator groupByIT = groupByClauses.iterator();
        if (groupByIT.hasNext()) {
            sqlStatement.append(" group by ");
        }
        while (groupByIT.hasNext()) {
            sqlStatement.append((String)groupByIT.next());
            if (groupByIT.hasNext()) {
                sqlStatement.append(",");
            }
        }

        //If there are any having clauses to be added, add them
        Iterator havingIT = havingClauses.iterator();
        if (havingIT.hasNext()) {
            sqlStatement.append(" having ");
        }
        while (havingIT.hasNext()) {
            sqlStatement.append((String)havingIT.next());
            if (havingIT.hasNext()) {
                sqlStatement.append(" and ");
            }
        }

        //Return our newly constructed SQL statement
        return sqlStatement.toString();
    }

    protected final String getWhereClause() {
        StringBuffer whereStatement = new StringBuffer();

        //Iterate through all of the available where clauses and add them to the
        //SQL statement
        Iterator whereClauseIT = whereClauses.iterator();
        while (whereClauseIT.hasNext()) {
            String whereClause = (String)whereClauseIT.next();

            if ((whereClause != null) && (whereClause.trim().length() > 0)) {
                whereStatement.append(" and " + whereClause);
            }
        }

        //Iterator through all the finder operations to find more where clauses
        Iterator finderOperationIT = finderFilters.iterator();
        while (finderOperationIT.hasNext()) {
            FinderFilter oper = (FinderFilter)finderOperationIT.next();
            String whereClause = oper.getWhereClause();

            if ((whereClause != null) && (whereClause.trim().length() > 0)) {
                whereStatement.append(" and " + whereClause);
            }
        }

        if (maximumRowsToFetch != 0) {
            whereStatement.append(" and (rownum <= "+maximumRowsToFetch+") ");
        }

        return whereStatement.toString();
    }

    /**
     * This method calls the preConstruct() method of every listener that is
     * currently listening to this finder for events to occur.
     */
    private void preConstructSQLStatement() {
        FinderListener[] listeners = (FinderListener[])
            finderListeners.getListeners(FinderListener.class);

        for (int i = 0; i < listeners.length; i++) {
            listeners[i].preConstruct(this);
        }
    }

    /**
     * This method calls every listener's
     * {@link net.project.base.finder.FinderListener#preExecute} method to
     * notify them that the executePrepared() call is just about to occur.
     *
     * @param db a <code>DBBean</code> object that the user can modify.
     * @throws SQLException if any of the listeners throw a SQLException.
     */
    private void preExecute(DBBean db) throws SQLException {
        FinderListener[] listeners = (FinderListener[])
            finderListeners.getListeners(FinderListener.class);

        for (int i = 0; i < listeners.length; i++) {
            listeners[i].preExecute(db);
        }
    }

    private void postExecute(DBBean db, List list) throws SQLException {
        FinderListener[] listeners = (FinderListener[])
            finderListeners.getListeners(FinderListener.class);

        for (int i = 0; i < listeners.length; i++) {
            listeners[i].postExecute(db, list);
        }
    }

    private void postExecute(DBBean db, Object object) throws SQLException {
        FinderListener[] listeners = (FinderListener[])
            finderListeners.getListeners(FinderListener.class);

        for (int i = 0; i < listeners.length; i++) {
            listeners[i].postExecute(db, object);
        }
    }

    /**
     * Load all data objects from the database that correspond to the
     * {@link #getSQLStatement} query into domain objects.  Return these values
     * in a <code>List</code> object.
     *
     * @return a <code>List</code> object containing one or more data objects
     * constructed in {@link #createObjectForResultSetRow}
     * @throws PersistenceException if a database error occurs while querying
     * for data or while populating objects.
     */
    protected List loadFromDB() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            return loadFromDB(db);
        } catch (SQLException sqle) {
            throw new PersistenceException("Finder load operation failed: " + sqle.getLocalizedMessage(), sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Load all data objects from the database that correspond to the
     * {@link #getSQLStatement} query into domain objects.  Return these values
     * in a <code>List</code> object.
     *
     * @return a <code>List</code> object containing one or more data objects
     * constructed in {@link #createObjectForResultSetRow}
     * @throws SQLException if a database error occurs while querying
     * for data or while populating objects.
     */
    protected List loadFromDB(DBBean db) throws SQLException {
        ArrayList dataToReturn = new ArrayList();

        db.prepareStatement(getSQLStatement());

        if (maximumRowsToFetch != 0) {
            db.pstmt.setMaxRows(maximumRowsToFetch);
        }

        //Call any last minute things a subclass may want to do
        preExecute(db);
        db.executePrepared();

        while (db.result.next()) {
            Object toAdd = createObjectForResultSetRow(db.result);
            //Make sure that what was return was meant to be added.
            if (!NON_UNIQUE_ROW.equals(toAdd)) {
                dataToReturn.add(toAdd);
            }
        }

        postExecute(db, dataToReturn);

        return dataToReturn;
    }

    /**
     * Populate a single object of the domain type of the current finder.
     *
     * In order to use this method, you need to implement the
     * {@link #createObjectForResultSetRow(java.sql.ResultSet, java.lang.Object))}
     * method.
     *
     * @return an <code>boolean</code> indicating whether any data was found.
     * {@link #createObjectForResultSetRow(java.sql.ResultSet, java.lang.Object))}
     * @throws PersistenceException if a database error occurs while querying
     * for data or while populating objects.
     */
    protected boolean loadFromDB(Object object) throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.prepareStatement(getSQLStatement());

            if (maximumRowsToFetch != 0) {
                db.pstmt.setMaxRows(maximumRowsToFetch);
            }

            preExecute(db);
            db.executePrepared();

            if (db.result.next()) {
                createObjectForResultSetRow(db.result, object);
                postExecute(db, object);
                return true;
            } else {
                postExecute(db, object);
                return false;
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("Finder load operation failed: " + sqle.getLocalizedMessage(), sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link net.project.schedule.Task}
     * object.  Any class that extends the finder base class needs to implement
     * this method the finder can use its build-in loadFromDB method to load
     * objects.
     *
     * @param databaseResults a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.  The ResultSet is assumed
     * to be on a current row.
     * @return a <code>Object</code> subclass specific to your finder that has
     * been populated with data.
     * @throws java.sql.SQLException if an error occurs populating the object.
     */
    protected abstract Object createObjectForResultSetRow(ResultSet databaseResults) throws SQLException;


    /**
     * Populate a domain object with data specific to the query result.  For
     * example, a task finder would populate a {@link net.project.schedule.Task}
     * object.  This particular version of the createObjectForResultSetRow method
     * is available so the user can fill up an preexisting object with data more
     * easily.
     *
     * @param databaseResults a <code>ResultSet</code> that provides the data
     * necessary to populate the domain object.  The ResultSet is assumed
     * to be on a current row.
     * @param object a preexisting object that needs to be filled with data.
     * @return a <code>Object</code> subclass specific to your finder that has
     * been populated with data.
     * @throws java.sql.SQLException if an error occurs populating the object.
     */
    protected Object createObjectForResultSetRow(ResultSet databaseResults, Object object) throws SQLException {
        return object;
    }

    /**
     * Return a DBBean object which has been populated with the result of running
     * a finder.
     *
     * @param db a <code>DBBean</code> object which is going to be populated and
     * returned as the result of this method.
     * @return a <code>DBBean</code> object which is the same instance which was
     * passed into this method.  This DBBean will have already queried using the
     * SQL statement passed into the constructor of this method.
     * @throws java.sql.SQLException if any error occurs which running this finder.
     */
    public DBBean find(DBBean db) throws SQLException {
        db.prepareStatement(getSQLStatement());
        //Call any last minute things a subclass may want to do
        preExecute(db);
        db.executePrepared();

        return db;
    }
    
    public String getUsername(int id) throws SQLException, PersistenceException {
    	String perName = null;
    	DBBean db = new DBBean();
        try {
/*        	db.prepareCall("{ ? = call SCHEDULE.GET_USERNAME (?) }");
        	db.cstmt.registerOutParameter(1, java.sql.Types.VARCHAR);
        	db.cstmt.setInt(2, id);
        	db.executeCallable();
        	perName=db.cstmt.getString(1);*/
        	
        	String sql = "SELECT username from pn_user where user_id = ? and record_status = 'A'";        	
        	db.prepareStatement(sql);
        	db.pstmt.setInt(1, id);
        	db.executePrepared();
        	if (db.result.next()) {
        		perName = db.result.getString("username");
        	}        		        	
        } catch (SQLException sqle) {
            throw new PersistenceException("Get user name failed: " + sqle.getLocalizedMessage(), sqle);
        } finally {
            db.release();
        }
    	return perName;
    }
    
}
