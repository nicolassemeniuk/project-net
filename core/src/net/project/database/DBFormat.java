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

import java.text.SimpleDateFormat;

/**
 * Static database field formatting methods.
 */
public class DBFormat {
    public static final String SINGLE_QUOTE = "'";
    public static final int BOOLEAN = 1;
    public static final int DATE = 2;
    public static final int VARCHAR2 = 3;
    public static final int DATE_TIME = 6;
    public static final int NUMBER = 7;

    /**
     * @return a string for safe storage in SQL as a VARCHAR2
     */
    public static String varchar2(String s) {
        if ((s == null) || s.equals("")) {
            return "NULL";
        } else {
            return "'" + escape(s) + "'";
        }
    }

    /**
     * Escapes the String to the database friendly value
     *
     * @param s
     * @return  the String to the database friendly value
     */
    public static String escape(String s) {
        StringBuffer sb = new StringBuffer(s.length() + 10);
        int iIndex = -1;
        int prevIndex = 0;

        while ((iIndex = s.indexOf("'", prevIndex)) > -1) {
            sb.append(s.substring(prevIndex, iIndex + 1));
            sb.append("'");
            prevIndex = iIndex + 1;
        }
        sb.append(s.substring(prevIndex, s.length()));
        return sb.toString();
    }

    /**
     @return a string for safe storage in SQL as a NUMBER
     */
    public static String number(String s) {
        if ((s == null) || s.equals(""))
            return "NULL";
        else
            return s;
    }

    /**
     @return a string for safe storage of a boolean as a NUMBER(1)
     */
    public static String bool(String s) {
        if (s == null || s.equals(""))
            return "NULL";

        if (s.equals("Y") || s.equals("1"))
            return "1";
        else
            return "0";
    }

    /**
     * Returns a string for safe storage of a boolean as a NUMBER(1)
     *
     * @param b the boolean value
     * @return a string for safe storage of a boolean as a NUMBER(1)
     */
    public static String bool(boolean b) {
        if (b)
            return "1";
        else
            return "0";
    }

    /**
     @return a string for safe storage in SQL as a DATE
     */
    public static String date(String s) {
        if ((s == null) || s.equals(""))
            return "NULL";
        else
            return ("TO_DATE('" + s + "', 'FMMM/DD/YYYY')");
    }

    /**
     * Returns a string which represents a date value in a SQL statement
     * @return
     */
    public static String dateTime(java.util.Date date) {
        if (date == null) {
            return "NULL";
        } else {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return "TO_DATE('" + df.format(date) + "', 'YYYY-MM-DD HH24:MI:SS')";
        }
    }

    /**
     * Converts a String that represents a boolean in the database (ie.  "1", "0", "Y", "N")
     *
     * @param s
     * @return true if the string matches "Y" or "1", false otherwise.
     */
    public static boolean toBool(String s) {

        if (s != null && (s.equals("1") || s.equals("Y")))
            return true;
        else
            return false;
    }

    /**
     * Convert a String to an integer.
     *
     * @param s
     * @return the integer represented by the String s, returns -1 if not able to convert.
     */
    public static int toInt(String s) {
        int i;

        try {
            i = Integer.parseInt(s);
        } catch (Exception e) {
            i = -1;
        }

        return i;
    }

    /**
     * Return a string for safe storage in SQL as a crc column.  This will be
     * a TO_DATE() clause including seconds portion.<br>
     * <b>Note</b> - The date object will be modified.  Its milliseconds will
     * be truncated so that it may be used to compare with the stored crc
     * value.  This is because Oracle does not store dates to a millisecond precision.<br>
     * <b>Note 2</b> - A java.sql.Timestamp object may be passed.  Its nanosecond
     * portion will be set to 0.<br>
     * <b>Note 3</b> - The date produced should only be used for crc comparison.  It should
     * never be used as an actual date.<br>
     * Example of return value:<br>
     *   <code>"TO_DATE('2000-11-28 10:16:23', 'YYYY-MM-DD HH24:MI:SS')"</code><br>
     * @param date the Date object to be converted and modified.
     * @return TO_DATE() clause including seconds.
     */
    public static String crc(java.util.Date date) {
        String sql = null;
        long currentTime = 0;
        java.text.SimpleDateFormat formatter = null;

        if (date == null) {
            sql = "NULL";

        } else {

            // If date is really a sql timestamp, reset its nanoseconds before
            // proceeding.
            if (date instanceof java.sql.Timestamp) {
                ((java.sql.Timestamp)date).setNanos(0);
            }

            // First truncate the milliseconds
            currentTime = date.getTime();
            date.setTime(currentTime - (currentTime % 1000));

            // Now format the string date the to_date clause with the date in a predictable format
            formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sql = "TO_DATE('" + formatter.format(date) + "', 'YYYY-MM-DD HH24:MI:SS')";
        }

        return sql;
    }

    /**
     * Formats the value to database friendly string based on the object type and object value
     *
     * @param type   the type of the object
     * @param object The value of the object
     * @return database friendly string
     */
    public static String format(int type, Object object) {

        if (type == BOOLEAN) {
            if (object instanceof java.lang.Boolean) {
                return bool(((Boolean)object).booleanValue());
            } else if (object instanceof String) {
                return bool((String)object);
            } else {
                return "";
            }
        }

        if (type == DATE || type == DATE_TIME) {
            if (object instanceof java.util.Date) {
                return dateTime((java.util.Date)object);
            } else if (object instanceof String) {
                return date((String)object);
            } else {
                return "";
            }
        }

        if (type == VARCHAR2) {
            if (object instanceof String) {
                return varchar2((String)object);
            } else {
                return "";
            }
        }

        if (type == NUMBER) {
            if (object instanceof String) {
                return number((String)object);
            } else {
                return "";
            }
        }
        return "";
    }

}

