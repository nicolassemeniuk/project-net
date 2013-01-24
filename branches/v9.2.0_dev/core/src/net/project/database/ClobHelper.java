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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.database;

import java.io.BufferedWriter;
import java.io.Reader;
import java.sql.SQLException;

import net.project.base.compatibility.Compatibility;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Provides helper methods for reading and writing to Clobs.
 * <p>
 * Read the <a href="doc-files/ClobHelper-usage.html">Usage Examples</a> to see how
 * to read and update clob data using this class.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.5
 */
public class ClobHelper {

    /**
     * The default size for the buffer for reading Clobs, currently <code>4096</code>.
     * This value was selected so that inline clob data (potentially the most
     * common) can be read in one go.  Oracle stores 4000 bytes or less directly
     * in the row.
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024*4;

    /**
     * Helper method to read data from a Clob locater.
     * <p>
     * Example Usage:<pre><code>
     *     if (db.result.next()) {
     *         String data = <b>ClobHelper.read(</b>db.result.getClob("clob_column_name")<b>)</b>;
     *     }
     * </code></pre>
     * </p>
     * @param clob the Clob locater from which to read; if <code>null</code>
     * is passed the method returns <code>null</code>
     * @return the data read or null if the clob was null
     */
    public static String read(java.sql.Clob clob) throws PersistenceException {

        String result;

        if (clob == null) {
            // If we don't have a clob, then return null data
            result = null;

        } else {

            Reader reader = null;
            StringBuffer clobBuffer = new StringBuffer();

            try {
                // Grab the Clob data with a reader
                reader = clob.getCharacterStream();

                // Read the data and append it to the clobBuffer
                // Uses a 1024 character buffer
                char[] buffer = new char[DEFAULT_BUFFER_SIZE];
                int readCount;
                while ( (readCount = reader.read(buffer)) != -1) {
                    clobBuffer.append(buffer, 0, readCount);
                }

            } catch (java.io.IOException ioe) {
            	Logger.getLogger(ClobHelper.class).error("Clob.readData() threw an IO exception: " + ioe);
                throw new PersistenceException("Clob fetch data operation failed: " + ioe, ioe);

            } catch (SQLException sqle) {
            	Logger.getLogger(ClobHelper.class).error("Clob.readData() threw an SQL exception: " + sqle);
                throw new PersistenceException("Clob fetch data operation failed: " + sqle, sqle);

            } finally {
                             // Make sure we close the reader if it was initialized successfully
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (java.io.IOException ioe) {
                        throw new PersistenceException("Clob fetch data operation failed: " + ioe, ioe);
                    }

                }
            }

            result = clobBuffer.toString();
        }

        return result;
    }

    /**
     * Helper method to write data to a CLOB column in an existing data row.
     * The Clob column for in that row may be null. If the Clob column already
     * contains data, it will be truncated before writing.
     * @param db the DBBean in which to perform the update; autoCommit must
     * be set to false and the current transaction must be able to see the
     * row to write to.  If autocommit is true, this method will fail.
     * @param tableName the name of the table containing the Clob column
     * @param primaryKeyColumn the column that is the primary key
     * @param clobColumn the column of datatype Clob
     * @param data the data to write to the Clob column
     * @throws PersistenceException if ther is a problem writing; for example,
     * the row cannot be found
     */
    public static void write(DBBean db, String tableName, String primaryKeyColumn, String clobColumn,
            String primaryKeyValue, String data) throws PersistenceException {

        // Initialize the clob with the table and column names
        Clob clob = new Clob(db, tableName, primaryKeyColumn, clobColumn);

        // Set the ID which is required for locating the row to update
        clob.setID(primaryKeyValue);

        // Populate the data to write
        clob.setData(data);

        // Stream the data to the clob via the locater, emptying out the
        // existing data
        // This will initialize the CLOB to empty_clob if it is currently NULL
        clob.modify();

    }

    /**
     * Helper method to write data to a Clob locater.
     * The data is written beginning at the start of the clob; no attempt is
     * made to truncate the clob; it is assumed that the clob has already been
     * truncated if the new data is to completely replace the old data.
     * Oracle does not currently support the {@link java.sql.Clob#truncate(long)}
     * method and we cannot cast to <code>oracle.sql.CLOB</code> due to the use
     * of connection pools.
     * <p>
     * Example Usage:<pre><code>
     *     db.prepareCall({call stored_proc.insert_row(?, ?)});
     *     db.cstmt.setString(1, "some value");
     *     db.cstmt.registerOutParameter(2, java.sql.Types.CLOB);
     *     db.executeCallable();
     *
     *     java.sql.Clob clob = db.cstmt.getClob(2);
     *     ClobHelper.write(clob, data);
     *
     *     db.commit();
     * </code></pre>
     * </p>
     * @param clob the clob locater to write to
     * @param data the data to write; if null, then the empty string is written
     * to the clob
     * @throws PersistenceException if there is a problem writing
     * @throws NullPointerException if clob i snull
     */
    public static void write(java.sql.Clob clob, CharSequence data) throws PersistenceException {

        if (clob == null) {
            throw new NullPointerException("clob is required");
        }

        // Set the data to write to empty string if the specified data is null
        CharSequence dataToWrite;
        if (data == null) {
            dataToWrite = "";
        } else {
            dataToWrite = data;
        }

        BufferedWriter writer = null;

        try {

            // Use compatibility class to get appropriate Clob provider
            // Each vendor allows access to underlying driver's Clob extensions
            // in a different manner
            writer = new BufferedWriter(Compatibility.getClobProvider().setCharacterStream(clob));
            writer.write(dataToWrite.toString());
            writer.flush();

        } catch (java.io.IOException ioe) {
        	Logger.getLogger(ClobHelper.class).error("Clob.modify() threw an IO exception: " + ioe);
            throw new PersistenceException("Clob store data operation failed: " + ioe, ioe);

        } catch (SQLException sqle) {
        	Logger.getLogger(ClobHelper.class).error("Clob.modify() threw an SQL exception: " + sqle);
            throw new PersistenceException ("Clob store operation failed." + sqle, sqle);

        } finally {

            if (writer != null) {
                try {
                    writer.close();
                } catch (java.io.IOException ioe) {
                    throw new PersistenceException("Clob store data operation failed: " + ioe, ioe);

                }
            }
        }
    }

}