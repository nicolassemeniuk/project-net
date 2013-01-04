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

 package net.project.database;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.project.base.compatibility.Compatibility;
import net.project.base.compatibility.IConnectionProvider;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Provides wrapper for JDBC functions to facilitate database operations.
 * <p/>
 * Preivous Clob manipulation wrappers have been deprecated.  For full details
 * on how to read/write Clobs, see the instructions at <code>{@link Clob}</code>.
 */
public class DBBean implements Serializable {

    //
    // Static members
    //

    /** Logging category. */
    private static transient Logger logger = Logger.getLogger(DBBean.class);

    //
    // Connection Checking Code
    //
    // Connection Checking is a development-only tool that monitors
    // open connections and informs the development team when a connection
    // is not closed within a certain period.
    // The code is implement via an interface; the implementing classes
    // are packaged in a separate JAR file; it is not packaged with the
    // rest of the application.
    // The connection checking code is executed if that implementing
    // code is found

    /** Indicates whether the ConnectionChecker is enabled. */
    private static final boolean isConnectionCheckerEnabled;

    /**
     * The ConnectionChecker is a development tool which monitors connections
     * and reports when connections are not closed.
     */
    private static IConnectionChecker connectionChecker = null;

    // Initialize the isCOnnectionCheckerEnabled and connectionChecker static variables
    static {
        boolean isConnectionCheckerAvailable;

        try {
            // Try to instantiate the connection checker provider
            // This will work if there is a ConnectionCheckerProvider in the classpath
            Class checkerProviderClass = Class.forName("net.project.databasetools.ConnectionCheckerProvider");

            // One is available, so instantiate it
            IConnectionCheckerProvider checkerProvider = (IConnectionCheckerProvider) checkerProviderClass.newInstance();

            // Now grab the singleton IConnectionChecker
            connectionChecker = checkerProvider.getConnectionChecker();

            // Connection Checking is available
            isConnectionCheckerAvailable = true;
            System.out.println("DEBUG [DBBean] Connection Checking ON");

        } catch (ClassNotFoundException e) {
            // No net.project.database.ConnectionChecker in the classpath
            // Connection Checking is turned off
            isConnectionCheckerAvailable = false;

        } catch (InstantiationException e) {
            // Unable to instantiate the ConnectionChecker
            // Connection Checking is turned off
            isConnectionCheckerAvailable = false;

        } catch (IllegalAccessException e) {
            // Can't access constructor
            // Connection Checking is turned off
            isConnectionCheckerAvailable = false;

        }

        // Indicate availability
        isConnectionCheckerEnabled = isConnectionCheckerAvailable;

    }

    //
    // End of Connection Checking code
    //


    /** The provider of connections. */
    private IConnectionProvider connectionProvider = Compatibility.getConnectionProvider();

    /** The current query. */
    private String queryString = null;

    /** The current Connection. */
    public transient Connection connection = null;

    /** The current Statement. */
    public transient Statement stmt = null;

    /** The current CallableStatement. */
    public transient CallableStatement cstmt = null;

    /** The current PreparedStatement. */
    public transient PreparedStatement pstmt = null;

    /** The current ResultSet. */
    public transient ResultSet result = null;

    /**
     * Flags whether an error occurred, necessitating a rollback in
     * the release() method.
     */
    private transient boolean hasErrorOccurred = false;

    /**
     * Flag to indicate DBBean is being finalized.
     * Used for connection checking; to alert to non-released
     * connections even though they are released during
     * finalization.
     */
    private boolean isFinalizing = false;

    /**
     * Creates a new, empty DBBean.
     */
    public DBBean() {
        // Do nothing
    }

    /**
     * Closes all database connections, resultsets and statements.
     */
    public void finalize() {
        this.isFinalizing = true;
        this.release();
    }

    /**
     * Sets the SQL query for use with {@link #executeQuery()}.
     * @param query the SQL query
     */
    public void setQuery(String query) {
        this.queryString = query;
    }

    /**
     * Ensures a connection is open.
     * If a connection is not currently open, a new connection is created;
     * otherwise, the existing connection remains in use.
     * 
     * @throws SQLException if there is a problem opening the connection
     */
    public void openConnection() throws SQLException {
        if (this.connection == null) {
            this.connection = createConnection();

            // If the ConnectionChecker is enabled
            // then register the newly created connection
            if (DBBean.isConnectionCheckerEnabled) {
                DBBean.connectionChecker.open(this.connection);
            }

            // Clear the flag, since we've just opened a new connection
            setErrorOccurred(false);
        }
    }


    /**
     * Commits on the current connection (if any).
     * Any SQLExceptions thrown are also logged.
     * @throws SQLException if an error occurs during the commit
     */
    public void commit() throws SQLException {
        try {
            if (this.connection != null) {
                this.connection.commit();
            }
        } catch (SQLException sqle) {
            setErrorOccurred(true);
            logException(sqle);
            throw sqle;
        }
    }


    /**
     * Rolls-back on the current connection (if any).
     * Any SQLExceptions thrown are also logged.
     * @throws SQLException if an error occurrs during the rollback
     */
    public void rollback() throws SQLException {
        try {
            if (this.connection != null) {
                this.connection.rollback();
            }
        } catch (SQLException sqle) {
            setErrorOccurred(true);
            logException(sqle);
            throw sqle;
        }
    }

    /**
     * Closes the current connection (if any).
     * Any SQLExceptions thrown are also logged.
     * <p>
     * If any previous SQL statement caused an error to occur then the
     * transaction is explicitly rolled-back.  Otherwise the connection is simply
     * closed (leaving it up to the connection pool to decide what to do).
     * @throws SQLException if there is a problem closing the connection
     */
    private void closeConnection() throws SQLException {

        if (this.connection != null) {

            try {

                // If an error has occurred, peform a rollback
                try {
                    if (hasErrorOccurred()) {
                        setErrorOccurred(false);
                        rollback();
                    }
                } catch (SQLException rollbackException) {
                    // Continue to close the connection
                }

                /*
                    Now, set all the "default" connection properties.  This is necessary
                    because connections are shared.  While they appear always to be rolled-back
                    upon return to the pool, properties such as "autoCommit" may NOT be reset.
                */
                this.connection.setAutoCommit(true);
                this.connection.close();

                // If the ConnectionChecker is enabled then indicate
                // that the connection is being closed.
                // We must do this before dropping the reference
                // to the connection so that the connection can be
                // removed from the checker's internal table
                //
                // We don't register the connection as closed if it is
                // closed by way of finalization:  this allows us to
                // report on code locations that don't explicitly call
                // release() even though the conneciton is closed by finalization
                if (DBBean.isConnectionCheckerEnabled && !isFinalizing) {
                    DBBean.connectionChecker.close(this.connection);
                }

                // Now drop the reference to the connection
                this.connection = null;

            } catch (SQLException sqle) {
                // Log the SQL Exception.  We may want  to log this to a separate DB log
                // at some point, instead of just DEBUG.
                logException(sqle);
                throw sqle;
            }
        }
    }

    /**
     * Returns the current connection or null if a connection has not been
     * opened yet.
     * 
     * @return the connection
     * @see #openConnection for opening a connection
     */
    public Connection getConnection() {
        return this.connection;
    }

    /**
     * Creates a new {@link java.sql.Statement}.
     * A connection is opened if necessary.  Any existing resultset or statements
     * are closed.
     * Any SQLExceptions thrown are also logged.
     * 
     * @throws SQLException if there is a problem opening a connection, closing
     *                      existing statements or resultset or creating a new statement
     */
    public void createStatement() throws SQLException {
        try {
            // Ensure a connection is open
            openConnection();

            // Release any current statements, since the user is starting a new one
            releaseStatements();

            // Create the new statement
            this.stmt = connection.createStatement();

        } catch (SQLException sqle) {
            setErrorOccurred(true);
            logException(sqle);
            throw sqle;
        }
    }

    /**
     * Sets autocommit for current connection.
     * A connection is opened if there is no current connection.
     * 
     * @param flag true to set autoCommit on for the current connection;
     *             false to turn autocommit off
     * @throws SQLException if there is a problem setting auto-commit.
     * @see java.sql.Connection#setAutoCommit
     */
    public void setAutoCommit(boolean flag) throws SQLException {
        if (this.connection == null) {
            openConnection();
        }
        connection.setAutoCommit(flag);
    }

    /**
     * Sets whether an error has occurred.
     * This information is used when closing a connection to determine
     * whether a rollback should be performed.  This is necessary because
     * connections aren't truly closed; they are actually released back into
     * the pool.  We do this so that if a developer forgets to perform
     * an explicit rollback, when AutoCommit is false, we ensure the rollback
     * is ALWAYS performed - otherwise, the next class to get the connection
     * might perform a commit, causing some uncommitted data to be committed.<br>
     * We only perform a rollback if an error occurred to avoid necessary
     * round-trips to the database.
     */
    private void setErrorOccurred(boolean hasErrorOccurred) {
        this.hasErrorOccurred = hasErrorOccurred;
    }

    /**
     * Indicates whether an error occurred during since a connection was opened.
     * 
     * @return true if an error occurred; false otherwise
     */
    private boolean hasErrorOccurred() {
        return this.hasErrorOccurred;
    }

    /**
     * Creates a new CallableStatement.  To be used only for stored procedures and
     * function calls.
     * A connection is opened if necessary.  Any existing resultset or statements
     * are closed.
     * Any SQLExceptions thrown are also logged.
     * 
     * @param call the callable SQL statement
     * @throws SQLException if there is a problem opening a connection, closing
     *                      existing statements or resultset or creating a new statement
     */
    public void prepareCall(String call) throws SQLException {
        try {
            // Ensure a connection is open
            openConnection();

            // Release any current statements, since the user is starting a
            // new one
            releaseStatements();

            // Create the new CallableStatement
            this.cstmt = connection.prepareCall(call);

            // Log the query string
            if (logger.isDebugEnabled()) {
                logger.debug("\n*******\nPrepared Call: " + connection.nativeSQL(call) + "\n*******");
            }

        } catch (SQLException sqle) {
            setErrorOccurred(true);
            logException(sqle);
            throw sqle;
        }
    }

    /**
     * Creates a new PreparedStatement.
     * A connection is opened if necessary.  Any existing resultset or statements
     * are closed.
     * Any SQLExceptions thrown are also logged.
     * 
     * @param call the SQL statement
     * @throws SQLException if there is a problem opening a connection, closing
     *                      existing statements or resultset or creating a new statement
     */
    public void prepareStatement(String call) throws SQLException {
        try {
            // Ensure a connection is open
            openConnection();

            // Release any current statements, since the user is starting a new one
            releaseStatements();

            // Create the new PreparedStatement
            this.pstmt = connection.prepareStatement(call);

            // Increase the number of rows fetched to improve performance.
            // TODO -- Roger.  This should be controlled at the application server connection pool level. Why are we overridding it here?
            pstmt.setFetchSize(250);

            // Log the query string
            if (logger.isDebugEnabled()) {
                logger.debug("\n*******\nPrepared Statement: " + connection.nativeSQL(call) + "\n*******");
            }

        } catch (SQLException sqle) {
            setErrorOccurred(true);
            // Log the SQL Exception.
            logException(sqle);
            throw sqle;
        }
    }

    /**
     * Closes the current Statement (if any).
     * Any SQLExceptions thrown are also logged.
     * 
     * @throws SQLException if there is a problem closing the statement
     */
    public void closeStatement() throws SQLException {
        try {
            if (stmt != null) {
                this.stmt.close();
                this.stmt = null;
            }
        } catch (SQLException sqle) {
            setErrorOccurred(true);
            logException(sqle);
            throw sqle;
        }
    }

    /**
     * Closes the current CallableStatement (if any).
     * Any SQLExceptions thrown are also logged.
     * 
     * @throws SQLException if there is a problem closing the statement
     */
    public void closeCStatement() throws SQLException {
        try {
            if (cstmt != null) {
                try {
                    this.cstmt.clearParameters();
                } catch (Exception e) {
                    //This doesn't work in all cases.  Just ignore it.
                }
                this.cstmt.close();
                this.cstmt = null;
            }
        } catch (SQLException sqle) {
            setErrorOccurred(true);
            logException(sqle);
            throw sqle;
        }
    }

    /**
     * Closes the current PreparedStatement (if any).
     * Any SQLExceptions thrown are also logged.
     * 
     * @throws SQLException if there is a problem closing the statement
     */
    public void closePStatement() throws SQLException {
        try {
            if (pstmt != null) {
                this.pstmt.close();
                this.pstmt = null;
            }
        } catch (SQLException sqle) {
            setErrorOccurred(true);
            logException(sqle);
            throw sqle;
        }
    }

    /**
     * Closes the current ResultSet (if any).
     * Any SQLExceptions thrown are also logged.
     * 
     * @throws SQLException if there is a problem closing the resultset
     */
    public void closeResultSet() throws SQLException {
        try {
            if (result != null) {
                this.result.close();
                this.result = null;
            }
        } catch (SQLException sqle) {
            setErrorOccurred(true);
            logException(sqle);
            throw sqle;
        }
    }

    /**
     * Releases the current result set and all statements.
     * The Connection remains open.
     */
    private void releaseStatements() {
        try {
            closeResultSet();
            closeStatement();
            closeCStatement();
            closePStatement();
        } catch (SQLException sqle) {
            logException(sqle);
        }
    }

    /**
     * Closes all database objects.
     * Closes any resultset, statement, prepared statement, callable statement
     * and connection.
     */
    public void release() {

        try {
            releaseStatements();
            closeConnection();
        } catch (SQLException sqle) {
            logException(sqle);
        }

    }

    /**
     * Executes the current query.
     * 
     * @throws SQLException if there is a problem executing
     * @see #setQuery
     */
    public void executeQuery() throws SQLException {
        executeQuery(this.queryString);
        // SQLException already logged in called method
    }

    /**
     * Executes the specified query.
     * 
     * @param qstr the query to execute.
     * @throws SQLException if there is a problem executing
     */
    public void executeQuery(String qstr) throws SQLException {

        // Create a statement if necessary
        if (stmt == null) {
            createStatement();
        }

        // Log the query string
        if (logger.isDebugEnabled()) {
            logger.debug("\n*******\nQuery Performed: " + qstr + "\n*******");
        }

        try {
            long start = System.currentTimeMillis();
            this.result = this.stmt.executeQuery(qstr);
            logExecutionTime(start);
        } catch (SQLException sqle) {
            setErrorOccurred(true);
            // Log the SQL Exception.
            logException(sqle);
            throw sqle;
        }
    }

    /**
     * Executes a batched statement.  Statement created using {@link #createStatement}.
     * Logs any SQLExceptions, indicating which statement failed in the case of
     * a BatchUpdateException.
     * 
     * @throws SQLException if there is a problem executing the batch statement
     * @see java.sql.Statement#executeBatch
     * @throws IllegalStateException if the current statement is null
     */
    public void executeBatch() throws SQLException {
        if (this.stmt == null) {
            throw new IllegalStateException("Cannot executeBatch on a null Statement");
        }

        try {
            long start = System.currentTimeMillis();
            this.stmt.executeBatch();
            logExecutionTime(start);

        } catch (SQLException sqle) {
            setErrorOccurred(true);
            logException(sqle);
            throw sqle;
        }
    }


    /**
     * Executes a batched PreparedStatement created through {@link #prepareStatement}.
     * 
     * @throws SQLException if there is a problem executing the batch statement
     * @see java.sql.PreparedStatement#executeBatch
     * @throws IllegalStateException if the current prepared statement is null
     */
    public void executePreparedBatch() throws SQLException {
        if (this.pstmt == null) {
            throw new IllegalStateException("Cannot executeBatch on a null Prepared Statement");
        }

        try {
            if (this.pstmt != null) {
                long start = System.currentTimeMillis();
                this.pstmt.executeBatch();
                logExecutionTime(start);
            } else {
                throw new SQLException("executePreparedBatch() expected to find a valid PreparedStatement " +
                        "internally, but did not.  Check to make sure that you have called " +
                        "prepareStatement() prior to calling executePrepared().");
            }
        } catch (SQLException sqle) {
            setErrorOccurred(true);
            logException(sqle);
            throw sqle;
        }
    }


    /**
     * Executes a batched CallableStatement created through {@link #prepareCall}.
     * 
     * @throws SQLException if there is a problem executing the batch statement
     * @see java.sql.CallableStatement#executeBatch
     * @throws IllegalStateException if the current callable statement is null
     */
    public void executeCallableBatch() throws SQLException {
        if (this.cstmt == null) {
            throw new IllegalStateException("Cannot executeBatch on a null Callable Statement");
        }

        try {
            if (this.cstmt != null) {
                long start = System.currentTimeMillis();
                this.cstmt.executeBatch();
                logExecutionTime(start);
            } else {
                throw new SQLException("executeCallableBatch() expected to find a valid CallableStatement internally, " +
                        "but did not.  Check to make sure that you have called prepareCall() prior to" +
                        "calling executeCallableBatch().");
            }
        } catch (SQLException sqle) {
            setErrorOccurred(true);
            logException(sqle);
            throw sqle;
        }

    }

    /**
     * Executes the PreparedStatement saving the ResultSet in {@link #result}.
     * 
     * @throws SQLException if there is a problem executing the statement.
     * @see java.sql.PreparedStatement#execute
     */
    public void executePrepared() throws SQLException {

        if (pstmt != null) {
            try {
                long start = System.currentTimeMillis();
                this.pstmt.execute();
                logExecutionTime(start);
            } catch (SQLException sqle) {
                setErrorOccurred(true);
                // Log the SQL Exception.
                logException(sqle);
                throw sqle;
            }

        } else {
            throw new SQLException("net.project.database.DBBean.executePrepared() expected to find a valid PreparedStatement internally, " +
                    "but did not.  Check to make sure that you have called prepareStatement()" +
                    "prior to calling executePrepared().");
        }

        // RGB -- added support for selects getting result set.
        this.result = this.pstmt.getResultSet();
    }

    /**
     * Executes the CallableStatement saving the ResultSet in {@link #result}.
     * 
     * @throws SQLException if there is a problem executing the statement.
     * @see java.sql.CallableStatement#execute
     */
    public void executeCallable() throws SQLException {
        try {
            if (cstmt != null) {
                long start = System.currentTimeMillis();
                this.cstmt.execute();
                logExecutionTime(start);
            } else {
                throw new SQLException("executeCallable() expected to find a valid CallableStatement internally, " +
                        "but did not.  Check to make sure that you have called prepareCall() prior" +
                        "to calling executeCallable().");
            }

        } catch (SQLException sqle) {
            setErrorOccurred(true);
            logException(sqle);
            throw sqle;
        }
    }

    /**
     * Executes the prepared statement as an update and returns the number
     * of rows updated.
     * 
     * @return number of rows updated; or 0 for SQL statements that return nothing;
     *         or 0 if there is no prepared statement to execute.
     * @throws SQLException if there is a problem executing the statement
     * @see java.sql.PreparedStatement#executeUpdate
     */
    public int executePreparedUpdate() throws SQLException {
        int result;

        try {
            if (this.pstmt != null) {
                long start = System.currentTimeMillis();
                result = this.pstmt.executeUpdate();
                logExecutionTime(start);

            } else {
                result = 0;

            }

        } catch (SQLException sqle) {
            setErrorOccurred(true);
            logException(sqle);
            throw sqle;
        }

        return result;
    }

    /**
     * Logs a SQLException.  This gives special consideration to BatchUpdateExceptions;
     * it will log the failed statement numbers.
     * 
     * @param sqle the SQLException to log
     */
    private static void logException(SQLException sqle) {

        if (sqle instanceof java.sql.BatchUpdateException) {
            StringBuffer updateErrors = new StringBuffer();
            int[] counts = ((java.sql.BatchUpdateException) sqle).getUpdateCounts();

            updateErrors.append(counts.length + " statements executed:\n");

            // Determine information about which statements succeeded or not
            // Note that the counts may NOT contain the same number of elements
            // as executed statements;  in this case, the JDBC driver stopped
            // executing after the first failed statement
            // If the counts do contain the same number of elements as
            // statements, then it processed all statements
            for (int i = 0; i < counts.length; i++) {
                if (counts[i] == -3) {
                    updateErrors.append("Statement " + i + " failed.\n");
                } else {
                    updateErrors.append("Statement " + i + " succeeded.\n");
                }
            }

            updateErrors.append("If this number is less than the number of batched statements, ");
            updateErrors.append("then the driver aborted after the first failure.\n");

            // Now report the results
            logger.warn("BatchUpdateException thrown in DBBean with following report:\n" +
                    updateErrors.toString(), sqle);

        } else {
            // Log the sql exception
            logger.warn("SQL Exception thrown in DBBean", sqle);

        }

    }


    /**
     * Logs the execution time of a statement by calculating the number of elapsed milliseconds
     * between now and the specified start time.
     * Only logs if <code>isDebugEnabled</code>.
     * 
     * @param start the start time from which to measure elapsed time
     */
    private static final void logExecutionTime(long start) {
        if (logger.isDebugEnabled()) {
            logger.debug("PRECEDING stmt exec time: " + (System.currentTimeMillis() - start) + " ms");
        }
    }


    /**
     * Retunrs a new connection object.
     * Note this does NOT affect the connection attribute of DBBean
     * @return the Connection object
     * @throws SQLException if there is a problem creating the connection
     */
    private Connection createConnection() throws SQLException {

        Connection conn;

        try {
            conn = this.connectionProvider.getConnection();

        } catch (java.sql.SQLException jsqle) {
            logException(jsqle);
            throw jsqle;

        }

        return conn;
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Deprecated Clob stuff
    ////////////////////////////////////////////////////////////////////////////

    /** Name of the table that all Clob methods act on. */
    private String clobTableName = null;

    /**
     * Sets the clob table name used for all clob database io.
     * 
     * @param tableName the name of the table (e.g. <code>pn_notification_clob</code>)
     * @deprecated No longer required due to new Clob mechanism.  This method
     *             will be removed in a later release. Use {@link Clob} instead.
     */
    public void setClobTableName(String tableName) {
        this.clobTableName = tableName;
    }

    /**
     * Creates a lob object based on the specified data.  The resulting Clob
     * object has an ID and may be stored immediately.  The Clob table name
     * must be set prior to calling this.
     * The Clob methods act within the MAIN DBBean transaction (i.e. same transaction)
     * 
     * @param data the data for the lob
     * @return the newly created Clob
     * @throws PersistenceException if there is a problem getting the clob data
     *                              or if the table name has not been set.
     * @see #setClobTableName
     * @see Clob
     * @deprecated No longer required due to new Clob mechanism.  This method
     *             will be removed in a later release.
     */
    public Clob createClob(String data) throws PersistenceException {

        if (clobTableName == null || clobTableName.equals("")) {
            throw new PersistenceException("Missing Clob Table Name in createClob()");
        }

        Clob clob = new Clob(this);
        clob.setTableName(clobTableName);
        clob.generateID();
        clob.setData(data);
        return clob;
    }


    /**
     * Returns a lob object based on the objectID.<br>
     * <b>The clob may NOT be updated.</b><br>
     * <code>setClobTableName()</code> must be called prior to this method
     * 
     * @param objectID the object id of the Clob to load
     * @return the Clob object
     * @throws PersistenceException if there is a problem getting the clob data
     *                              or if the table name has not been set.
     * @deprecated No longer required due to new Clob mechanism.  This method
     *             will be removed in a later release.
     */
    public Clob getClob(String objectID) throws PersistenceException {
        if (clobTableName == null || clobTableName.equals("")) {
            throw new PersistenceException("Missing Clob Table Name in getClob()");
        }

        Clob clob;
        clob = new Clob();
        clob.setTableName(clobTableName);
        clob.setID(objectID);
        clob.loadReadOnly();
        return clob;
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        logger = Logger.getLogger(DBBean.class);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();            
    }
    
    /**
     * Creates a new PreparedCall.
     * This function is used mainly to avoid cyclic query in prior versions of 
     * Oracle before 10g. This function does contain the release statement because
     * we need to use recurse function calling which will reset all the result set connections
     * Any SQLExceptions thrown are also logged.
     * 
     * @param call the SQL statement
     * @throws SQLException if there is a problem opening a connection, closing
     *                      existing statements or resultset or creating a new statement
     */
    public void prepareCallWithNoRelease(String call) throws SQLException {
        try {
            // Ensure a connection is open
            openConnection();

            // Create the new CallableStatement
            this.cstmt = connection.prepareCall(call);

            // Log the query string
            if (logger.isDebugEnabled()) {
                logger.debug("\n*******\nPrepared Call: " + connection.nativeSQL(call) + "\n*******");
            }

        } catch (SQLException sqle) {
            setErrorOccurred(true);
            logException(sqle);
            throw sqle;
        }
    }
}
