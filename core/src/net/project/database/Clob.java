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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.project.persistence.NoDataFoundException;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Provides a convenient mechanism for manipulating Clob data. <p> Read the <a
 * href="doc-files/Clob-usage.html">Usage Examples</a> to see how to read and
 * update clob data using this class. </p>
 *
 * @author Tim Morrow
 * @since Version 2.0
 */
public class Clob {
    /** Logging category. */
    private static final Logger logger = Logger.getLogger(Clob.class);
    /**
     * The default primary key column name on the lob table, currently
     * <code>object_id</code>.
     */
    private static final String DEFAULT_ID_COLUMN_NAME = "object_id";
    /**
     * The default lob column name on the lob table, currently
     * <code>clob_field</code>.
     */
    private static final String DEFAULT_LOB_COLUMN_NAME = "clob_field";

    /**
     * The table which holds the actual lob data.  This table must have a
     * primary key of "object_id" and a column called "clob_field"
     */
    private String tableName = null;

    /** The id (primary key) column name. */
    private String idColumnName = null;

    /** The lob (data) column name. */
    private String lobColumnName = null;

    /** The DBBean in which to perform transactions. */
    private DBBean db = null;

    /**
     * Indicates whether to release the DBBean when this object is destroyed.
     * This is set if a new DBBean is created.  If a DBBean is passed in, it is
     * assumed this is part of a greater transaction and hence no release is
     * performed.
     */
    private boolean isReleaseDBBean = false;
    /** The object id of the row containing the clob data. */
    private String objectID = null;
    /** String buffer representation of the clob data. */
    private StringBuffer dataBuff = null;
    /** Indicates whether this object represents a database row or new data. */
    private boolean isLoaded = false;

    /**
     * Creates a Clob object with default ID Column Name and Data Column Name. A
     * private DBBean is used.
     *
     * @see #DEFAULT_ID_COLUMN_NAME
     * @see #DEFAULT_LOB_COLUMN_NAME
     */
    public Clob() {
        initialize();
        newDBBean();
    }

    /**
     * Creates a new Clob using the specified DBBean in which to perform
     * transactions.
     *
     * @param db the DBBean to use. This allows Clob operations to be performed
     * in the same transaction as other operations.  Any existing statement in
     * the DBBean is preserved - the DBBean is never released.
     */
    public Clob(DBBean db) {
        initialize();
        useDBBean(db);
    }

    /**
     * Creates a new Clob using the specified DBBean in which to perform
     * transactions.
     *
     * @param db the DBBean to use. This allows Clob operations to be performed
     * in the same transaction as other operations.  Any existing statement in
     * the DBBean is preserved - the DBBean is never released.
     * @param tableName the name of the Clob table
     * @param idColumnName the primary key (id) column name
     * @param dataColumnName the lob column name
     */
    public Clob(DBBean db, String tableName, String idColumnName, String dataColumnName) {
        this(db);
        setTableName(tableName);
        setIDColumnName(idColumnName);
        setDataColumnName(dataColumnName);
    }

    /**
     * Initializes this Clob with default values.
     */
    private void initialize() {
        setIDColumnName(Clob.DEFAULT_ID_COLUMN_NAME);
        setDataColumnName(Clob.DEFAULT_LOB_COLUMN_NAME);
    }

    /**
     * Creates a new DBBean in this Clob that is used for database transactions.
     * The DBBean will be released when the Clob is committed or rolled-back.
     */
    private void newDBBean() {
        setDBBean(new DBBean());
        this.isReleaseDBBean = true;
    }

    /**
     * Uses the specified DBBean for transactions. Will not release the DBBean
     * when committed or rolled-back.
     *
     * @param db the DBBean to use
     */
    private void useDBBean(DBBean db) {
        setDBBean(db);
        this.isReleaseDBBean = false;
    }

    /**
     * Sets the DBBean to use. <b>Note:<b>This class preserves any statements in
     * the DBBean.  It ensures that the connection has autoCommit set to false.
     *
     * @param db the DBBean to use
     */
    private void setDBBean(DBBean db) {
        this.db = db;
        ensureConnection();
    }

    /**
     * Ensures a connection is open and is set to autoCommit(false). Opens a new
     * connection only if there is no existing connection.
     */
    private void ensureConnection() {
        try {
            this.db.openConnection();
            this.db.getConnection().setAutoCommit(false);

        } catch (SQLException sqle) {
            //Error setting autocommit

        }
    }

    /**
     * Specifies the table which stores the lob data. This table must have a
     * primary key (id) column and a LOB (data) column with the default name or
     * the custom name specified when creating this clob.
     *
     * @param tableName the name of the table
     * @see #getTableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    /**
     * Returns the current table name.
     *
     * @return the table name
     * @see #setTableName
     */
    private String getTableName() {
        return this.tableName;
    }

    /**
     * Specifies the column name for the id field in the clob table. If this is
     * not specified, the default column name is used.
     *
     * @param columnName the name of the column
     * @see #getIDColumnName
     */
    public void setIDColumnName(String columnName) {
        this.idColumnName = columnName;
    }

    /**
     * Returns the id field column name or the default if none was specified.
     *
     * @return the column name
     * @see #DEFAULT_ID_COLUMN_NAME
     * @see #setIDColumnName
     */
    private String getIDColumnName() {
        return this.idColumnName;
    }

    /**
     * Specifies the data column name for the lob field in the clob table. If
     * this is not specified, the default column name is used.
     *
     * @param columnName the name of the column
     * @see #getDataColumnName
     */
    public void setDataColumnName(String columnName) {
        this.lobColumnName = columnName;
    }

    /**
     * Returns the data column name or the default if none was specified.
     *
     * @return the data column name
     * @see #DEFAULT_LOB_COLUMN_NAME
     * @see #setDataColumnName
     */
    private String getDataColumnName() {
        return this.lobColumnName;

    }

    /**
     * Set the ID of this Clob.  This may be used to load a row from the
     * appropriate table or to specify a foreign key value for a new Clob's id.
     *
     * @param objectID the object id
     */
    public void setID(String objectID) {
        this.objectID = objectID;
    }


    /**
     * Returns the current ID.
     *
     * @return the object id
     */
    public String getID() {
        return this.objectID;
    }


    /**
     * Sets the data in the clob to the specified string. No actual database
     * modifications are made until <code>{@link #store}</code> is called.
     *
     * @param data the string data
     */
    public void setData(String data) {
        if (data == null) {
            this.dataBuff = null;
        } else {
            this.dataBuff = new StringBuffer(data);
        }
    }


    /**
     * Returns the data for the current clob.  This data may have been modified
     * by setData()
     *
     * @return the data
     */
    public String getData() {
        return this.dataBuff.toString();
    }


    /**
     * Loads this Clob for read only. The Clob is not locked after this method
     * has finished; any attempt to update the clob will fail. Also releases the
     * DBBean if an existing DBBean was not passed in.
     *
     * @throws NoDataFoundException if no row was found in the Clob table for
     * the current id
     * @throws PersistenceException if there is a problem loading the Clob
     * object
     */
    public void loadReadOnly() throws PersistenceException {
        // Load the clob read-only and set the data buffer to the content
        this.dataBuff = new StringBuffer(ClobHelper.read(loadClob(true)));
        setLoaded(true);

        // If we're using our own DBBean, release it
        // The clob has not been specifically loaded for update,
        // so user of Clob will perform any further operations
        if (isReleaseDBBean) {
            this.db.release();
        }

    }


    /**
     * Load the clob data based on the current object id. Assumes the table, id
     * column and data column names are set. Assumes the id is set. After the
     * clob data is loaded, its data may be read using <code>{@link
     * #getData()}</code>. <b>Note:</b> After loading, the row will have been
     * LOCKED in the database and will remain locked until the connection is
     * committed/rolled-back.
     *
     * @throws PersistenceException if there is a problem loading the Clob
     * object
     * @see #setID
     * @see #getData
     * @see #commit
     */
    public void loadReadWrite() throws PersistenceException {
        // Load the clob for read-write and set the data buffer to the
        // content
        this.dataBuff = new StringBuffer(ClobHelper.read(loadClob(false)));
        setLoaded(true);
    }


    /**
     * Load the clob data based on the current object id. Assumes the table, id
     * column and data column names are set. Assumes the id is set. After the
     * clob data is loaded, its data may be read using <code>{@link
     * #getData()}</code>. <b>Note:</b> If isReadOnly is specified <b>false</b>,
     * after loading, the row will have been LOCKED in the database and will
     * remain locked until the connection is committed/rolled-back.
     *
     * @param isReadOnly true if the data should be loaded without being locked;
     * false means it will be locked
     * @return the Clob locater
     * @throws NoDataFoundException if no row was found in the Clob table for
     * the current id
     * @throws PersistenceException if there is a problem loading the Clob
     * object
     * @throws IllegalStateException if not table name has been specified
     * @see #setID
     * @see #getData
     * @see #commit
     */
    private java.sql.Clob loadClob(boolean isReadOnly) throws NoDataFoundException, PersistenceException {

        if (getTableName() == null) {
            throw new IllegalStateException("Table name is required");
        }

        /* Statement of the form:
            SELECT <data_column> FROM <table_name> WHERE <id_column> = <id> FOR UPDATE NOWAIT
        */
        StringBuffer loadQuery = new StringBuffer();
        loadQuery.append("SELECT ").append(getDataColumnName()).append(" ");
        loadQuery.append("FROM ").append(getTableName()).append(" ");
        loadQuery.append("WHERE ").append(getIDColumnName()).append(" = ? ");
        if (!isReadOnly) {
            loadQuery.append("FOR UPDATE NOWAIT ");
        }

        ResultSet rs = null;
        PreparedStatement pstmt = null;

        // The Clob locater for the clob data
        java.sql.Clob clob = null;

        try {

            // Ensure we have an open connection
            ensureConnection();

            // Log the query
            if (logger.isDebugEnabled()) {
                logger.debug("\n*******\nPrepared Statement: " + this.db.connection.nativeSQL(loadQuery.toString()) + "\n*******\n");
            }

            // Select the clob object from the table
            // Uses its own PreparedStatement to avoid trashing any existing
            // ones in the DBBean.
            int index = 0;
            pstmt = this.db.connection.prepareStatement(loadQuery.toString());
            pstmt.setString(++index, getID());
            rs = pstmt.executeQuery();
            
            // Check that we got one row
            if (rs.next()) {
                // Save the Clob as an oracle CLOB
                // Reads the returned column matching the data column name specified
                clob = rs.getClob(getDataColumnName());

            } else {
                // No row found for the id
                // We should indicate this as an excpeption;  we don't automatically
                // go into "create" mode.
                throw new NoDataFoundException("No data found for " + getTableName() + "." + getIDColumnName() + " = " + getID());
            }

        } catch (SQLException sqle) {
            logger.warn("SQL Exception thrown in Clob", sqle);
            throw new PersistenceException("Clob.loadClob() threw a SQLException: " + sqle, sqle);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle) {
                // Simply log the error
                logger.warn("Clob.loadClob() threw an SQL exception", sqle);
            }

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException sqle) {
                // simply log the error
                logger.warn("Clob.loadClob() threw an SQL exception", sqle);
            }

        }

        return clob;
    }

    /**
     * Store the clob data, inserting or updating. <p> If this clob was loaded
     * from the database ({@link #isLoaded}), the existing row will be updated.
     * It must be loaded in read-write mode for the update to be successful.
     * </p> <p> If this clob has not been loaded from the database, a new record
     * will be inserted.  If an ID has been specified by {@link #setID} that ID
     * will be used as the primary key.  If no ID has been specified, a new ID
     * will automatically be created and will be available by calling {@link
     * #getID} after this method completes. </p>
     *
     * @throws PersistenceException if there is a problem storing
     */
    public void store() throws PersistenceException {
        if (isLoaded()) {
            modify();
        } else {
            create();
        }
    }


    /**
     * Modifies the lob data for a clob.
     *
     * @throws PersistenceException if there is a problem writing the clob data
     * @throws IllegalStateException if no ID has been set
     */
    void modify() throws PersistenceException {

        if (getID() == null) {
            throw new IllegalStateException("Unable to remove Clob: missing ID");
        }

        // Steps to modify a Clob, completing replacing its data:
        // 1. Update the Clob to the empty_clob()
        // 2. Reload the Clob locater
        // 3. Stream the data to the Clob
        //
        // These steps are necessary with JDBC 2.0 and earlier; no mechanism
        // is provided to easily truncate a Clob
        // However, with JDBC 3.0, new methods are introduced to truncate a Clob
        // Unfortunately JDBC 3.0 is only supported by JDK 1.4 and Oracle only supplies a
        // JDK 1.4-compatible driver for Oracle 9i Release 2 client
        // Since we continue to support 8.1.7 and 9.0 clients, we must support
        // only JDBC 2.0 APIs

        // Statement of the form:
        // UPDATE <table_name> SET <data_column> = empty_clob() where <id_column> = <id>
        StringBuffer updateQuery = new StringBuffer();
        updateQuery.append("UPDATE ").append(getTableName()).append(" ");
        updateQuery.append("SET ").append(getDataColumnName()).append(" = empty_clob() ");
        updateQuery.append("WHERE ").append(getIDColumnName()).append(" = ? ");

        PreparedStatement pstmt = null;

        try {
            // Ensure we have an open connection
            ensureConnection();

            // Execute the update statement to update with the empty clob holder
            int index = 0;
            pstmt = this.db.getConnection().prepareStatement(updateQuery.toString());
            pstmt.setString(++index, getID());
            pstmt.executeUpdate();

        } catch (SQLException sqle) {
            logger.warn("Clob.create() threw an SQL exception", sqle);
            throw new PersistenceException("Clob store operation failed: " + sqle, sqle);

        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }

            } catch (SQLException sqle) {
                logger.warn("Clob.create() threw an SQL exception", sqle);
                throw new PersistenceException("Clob store operation failed: " + sqle, sqle);
            }
        }

        // Now load this empty clob and write the data to it
        ClobHelper.write(loadClob(false), this.dataBuff);

    }

    /**
     * Inserts the current data into a new Clob row. After calling, an ID will
     * have been generated if none was specified. The data will be available in
     * this Clob; it may be updated then stored again.
     *
     * @throws PersistenceException if there is a problem inserting
     */
    private void create() throws PersistenceException {

        // Steps to create a Clob
        // 1. Insert the Clob, initializing it to the empty_clob()
        // 2. Reload the Clob locater
        // 3. Stream the data to the Clob

        // Statement of the form:
        // INSERT INTO <table_name> (<id_column>, <data_column>) VALUES (<id>, empty_clob())
        StringBuffer insertQuery = new StringBuffer();
        insertQuery.append("INSERT INTO ").append(getTableName()).append(" (");
        insertQuery.append(getIDColumnName()).append(", ").append(getDataColumnName());
        insertQuery.append(") VALUES (?, empty_clob()) ");

        // If we don't have an object id, generate one
        if (getID() == null) {
            setID(generateID());
        }

        PreparedStatement pstmt = null;

        try {
            // Ensure we have an open connection
            ensureConnection();
            
            // Execute the insert statement to create the empty clob locater
            int index = 0;
            pstmt = this.db.getConnection().prepareStatement(insertQuery.toString());
            pstmt.setString(++index, getID());
            pstmt.executeUpdate();

        } catch (SQLException sqle) {
            logger.warn("Clob.create() threw an SQL exception", sqle);
            throw new PersistenceException("Clob store operation failed: " + sqle, sqle);

        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }

            } catch (SQLException sqle) {
                logger.warn("Clob.create() threw an SQL exception", sqle);
                throw new PersistenceException("Clob store operation failed: " + sqle, sqle);
            }
        }

        // Now load this empty clob and write the data to it
        ClobHelper.write(loadClob(false), this.dataBuff);

    }

    /**
     * Removes the clob row for the current id. After calling, the id is still
     * set.
     *
     * @throws PersistenceException if there is a problem removing
     * @throws IllegalStateException if the current id is null
     * @see #getID
     */
    public void remove() throws PersistenceException {

        if (getID() == null) {
            throw new IllegalStateException("Unable to remove Clob: missing ID");
        }

        // Statement of the form:
        // DELETE FROM <table_name> WHERE <id_column> = ?
        StringBuffer deleteQuery = new StringBuffer();
        deleteQuery.append("DELETE FROM ").append(getTableName()).append(" ");
        deleteQuery.append("WHERE ").append(getIDColumnName()).append(" = ? ");

        PreparedStatement pstmt = null;

        try {
            // Ensure we have an open connection
            ensureConnection();

            // Execute the insert statement to create the empty clob holder
            int index = 0;
            pstmt = this.db.getConnection().prepareStatement(deleteQuery.toString());
            pstmt.setString(++index, getID());
            pstmt.execute();

        } catch (SQLException sqle) {
            logger.warn("Clob.remove() threw an SQL exception", sqle);
            throw new PersistenceException("Clob remove operation failed: " + sqle, sqle);

        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }

            } catch (SQLException sqle) {
                logger.warn("Clob.remove() threw an SQL exception", sqle);
                throw new PersistenceException("Clob remove operation failed: " + sqle, sqle);
            }
        }

    }


    /**
     * Generate a new object id. This is used when creating a new Clob object.
     * It is useful to generate the ID prior to storing the clob such that it
     * may be used as a foreign key in another table. This does not affect the
     * current transaction.
     *
     * @return the new object id
     */
    public String generateID() {
        setID(ObjectManager.getNewObjectID());
        return getID();
    }


    /**
     * Commits the modifications to this Clob. Also releases the DBBean if an
     * existing DBBean was not passed in.
     *
     * @throws PersistenceException if there is a problem committing
     */
    public void commit() throws PersistenceException {
        try {
            try {
                if (this.db.getConnection() != null) {
                    this.db.getConnection().commit();
                }

            } catch (SQLException sqle) {
                logger.warn("Clob.commit() threw an SQL exception", sqle);
                throw new PersistenceException("Clob commit operation failed: " + sqle, sqle);
            }
        } finally {
            if (isReleaseDBBean) {
                this.db.release();
            }
        }
    }


    /**
     * Clear this Clob's values in order to re-use the Clob. This Clob may then
     * be used to load, store or remove other data in the same clob table in the
     * same database transaction. Fields that are cleared include: id, data,
     * loaded flag. Fields that are not cleared include: table name, id column
     * name, data column name, DBBean
     */
    public void clear() {
        setID(null);
        setData(null);
        setLoaded(false);
    }

    /**
     * Sets whether this Clob has been loaded or not.
     *
     * @param isLoaded true if this clob has been loaded
     */
    private void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    /**
     * Indicates whether this Clob has been loaded or is new.
     *
     * @return true if this clob has been loaded; false otherwise
     */
    private boolean isLoaded() {
        return this.isLoaded;
    }

}
