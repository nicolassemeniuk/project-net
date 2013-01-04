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
|    $Revision: 19546 $
|    $Date: 2009-07-18 14:52:27 -0300 (sáb, 18 jul 2009) $
|    $Author: ritesh $
|
+----------------------------------------------------------------------*/
package net.project.database;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

import org.apache.log4j.Logger;

/**
 * Provides a place to hold methods useful when using a database.
 *
 * @see net.project.database.DBBean
 * @author Phil Dixon
 * @since 09/00
 */
public class DatabaseUtils {
    /**
     * Get the "Object Type" string which is associated with a given object id.
     *
     * @param objectID a <code>String</code> containing the unique id of the
     * object we are attempting to find.
     * @return a <code>String</code> containing an object type.  This type will
     * correspond to the static variables in the {@link net.project.base.ObjectType}
     * class.  If no type is found for a given ID, null will be returned.
     */
    public static String getTypeForObjectID(String objectID) {
        String qstrGetTypeForObjectID = "select object_type from pn_object where object_id = " + objectID;
        String objectType = null;

        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrGetTypeForObjectID);

            if (db.result.next())
                objectType = db.result.getString("object_type");
        } catch (SQLException sqle2) {
            Logger.getLogger(DatabaseUtils.class).error("Unable to find type for object id." + sqle2, sqle2);
        } finally {
            db.release();
        }

        return objectType;

    }


    public static String getNextSequenceValue() {
        String nextVal = null;
        String qstrSequenceNextVal = "select pn_object_sequence.nextval from dual";

        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrSequenceNextVal);

            if (db.result.next()) {
                nextVal = db.result.getString("nextval");
            }
        } catch (SQLException sqle2) {
            Logger.getLogger(DatabaseUtils.class).debug("database.DatabaseUtils.getSequenceNextValue(): unable to return a value from the Oracle sequence: " + sqle2);
        } finally {
            db.release();
        }

        return nextVal;
    }

    /**
     * Change a <code>List</code> of <code>String</code> objects into a comma
     * separated list suitable for inclusion in an "in" clause.
     *
     * @deprecated as of Version 7.7.0.  Use {@link #collectionToCSV(java.util.Collection)}
     * instead.
     *
     * @param listOfStrings a <code>List</code> object containing zero or more
     * strings.
     * @return a <code>String</code> containing a csv of Strings.
     */
    public static String listToCSV(List listOfStrings) {
        if (listOfStrings == null) {
            return "";
        }

        StringBuffer stringToReturn = new StringBuffer();
        for (Iterator it = listOfStrings.iterator(); it.hasNext();) {
            String listItem = (String)it.next();

            if (stringToReturn.length() == 0) {
                stringToReturn.append(listItem);
            } else {
                stringToReturn.append(",").append(listItem);
            }
        }

        return stringToReturn.toString();
    }

    /**
     * Change a <code>Collection</code> of <code>String</code> objects into a comma
     * separated list suitable for inclusion in an "in" clause.
     *
     * @param stringCollection a <code>Collection</code> object containing zero
     * or more strings.
     * @return a <code>String</code> containing a csv of Strings.
     */
    public static String collectionToCSV(Collection stringCollection) {
        return collectionToCSV(stringCollection, false);
    }


    /**
     * Change a <code>Collection</code> of <code>String</code> objects into a comma
     * separated list suitable for inclusion in an "in" clause.
     *
     * @param stringCollection a <code>Collection</code> object containing zero
     * or more strings.
     * @return a <code>String</code> containing a csv of Strings.
     */
    public static String collectionToCSV(Collection stringCollection, boolean quoteValues) {
        if (stringCollection == null) {
            return "";
        }

        StringBuffer stringToReturn = new StringBuffer();
        for (Iterator it = stringCollection.iterator(); it.hasNext();) {
            String listItem = (String)it.next();

            if (stringToReturn.length() == 0) {
                stringToReturn.append((quoteValues?"'":"")+listItem+(quoteValues?"'":""));
            } else {
                stringToReturn.append(","+(quoteValues?"'":"")+listItem+(quoteValues?"'":""));
            }
        }

        return stringToReturn.toString();
    }

    /**
     * Sets a timestamp in a DBBean.  The values will be set to null
     * @param stmt a <code>PreparedStatement</code> or <code>CallableStatement</code>
     * which we are setting the timestamp for.  (CallableStatement is a subclass of
     * PreparedStatement.)
     *
     * @param index an <code>int</code> which indicates which parameter location
     * we are setting.
     * @param date a <code>Date</code> which we are going to set in the timestamp.
     * @throws SQLException if there is an error setting the timestamp in the
     * statement.
     */
    public static void setTimestamp(PreparedStatement stmt, int index, Date date) throws SQLException {
        if (date == null) {
            stmt.setNull(index, Types.TIMESTAMP);
        } else {
            stmt.setTimestamp(index, new Timestamp(date.getTime()));
        }
    }

    public static Date getTimestamp(ResultSet rs, int index) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(index);
        if (timestamp != null) {
            return new Date(timestamp.getTime());
        } else {
            return null;
        }
    }

    public static void setInteger(PreparedStatement stmt, int index, String intString) throws SQLException {
        if (intString == null || intString.trim().length() == 0) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, Integer.parseInt(intString));
        }
    }

    public static void setString(PreparedStatement stmt, int index, String stringValue) throws SQLException {
        stmt.setString(index, stringValue);
    }

    /**
     * Returns a Date constructed from the specified timestamp's millisecond
     * value, handling null values.
     * @param timestamp the timestamp from which to create the date
     * @return the date or null if the timestamp was null
     */
    public static Date makeDate(Timestamp timestamp) {
        return (timestamp == null ? null : new Date(timestamp.getTime()));
    }

    /**
     * Returns a time quantity constructed from the amount and unit ID columns.
     * <p/>
     * The time quantity is constructed from a BigDecimal constructed from the amount
     * column value interpreted as a string.
     * @param result the resultset currently on a row containing the time quantity to return
     * @param timeQuantityColID the index number of the column containing the time quantity amount
     * @param timeQuantityUnitsColID the index number of the column containing the time quantity unit ID;
     * the unit ID should match one of the IDs of <code>TimeQuantityUnit</code>
     * @return the time quantity for the specified amount and unit, or a time quantity of <code>0 hours</code>
     * if the amount column is null
     * @throws SQLException if there is a problem reading a value from the columns
     */
    public static TimeQuantity getTimeQuantity(ResultSet result, int timeQuantityColID, int timeQuantityUnitsColID) throws SQLException {
        return new TimeQuantity(BigDecimal.valueOf(result.getDouble(timeQuantityColID)), TimeQuantityUnit.getForID(result.getInt(timeQuantityUnitsColID)));
    }

    /**
     * Binds a time quantity value as parameters to a prepared statement.
     * @param stmt the prepared statement to which to bind parameters
     * @param tqToSet the time quantity value to bind; if <code>null</code> is passed, both
     * parameters will be bound with a null value
     * @param amountColID the index number of the parameter to which the time quantity amount value
     * should be bound; the time quantity amount is converted using {@link BigDecimal#toString()}
     * @param unitsColID the index number of the parameter to which the time quantity unit ID
     * shoudld be bound; the time quantity unit ID is gotten by {@link TimeQuantityUnit#getUniqueID()}
     * @throws SQLException if there is a problem binding any parameter
     */
    public static void setTimeQuantity(PreparedStatement stmt, TimeQuantity tqToSet, int amountColID, int unitsColID) throws SQLException {
        if (tqToSet == null) {
            stmt.setNull(amountColID, Types.NUMERIC);
            stmt.setNull(unitsColID, Types.NUMERIC);
        } else {
            stmt.setString(amountColID, tqToSet.getAmount().toString());
            stmt.setInt(unitsColID, tqToSet.getUnits().getUniqueID());
        }
    }

    /**
     * Sets the given BigDecimal at the given index in the given statement.
     * If the BigDecimal is null, then null is set.
     * @param stmt the statement on which to set the bigdecimal
     * @param index the parameter index position to set the value
     * @param value the value; when null, <code>NUMERIC</code> null is set; otherwise <code>setBigDecimal</code>
     * is used
     * @throws SQLException if there is a problem setting the value
     */
    public static void setBigDecimal(PreparedStatement stmt, int index, BigDecimal value) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.NUMERIC);
        } else {
            stmt.setBigDecimal(index, value);
        }
    }


}
