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

 package net.project.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;
import net.project.security.User;

/**
 * Static methods to convert between various data types.
 *
 * @author Bern McCarty
 * @since 01/2000
 */
public class Conversion {
    /** Standard format for a date string exported from this class. */
    protected static final String DATE_FORMAT_STRING = "M/d/y";

    /**
     * Converts a String that represents a boolean in the database (ie. "1",
     * "0", "Y", "N", "true", "false")
     *
     * @return true if the string matches "Y" or "1" or "true", false otherwise.
     */
    public static boolean toBool(String s) {
        return toBoolean(s);
    }

    /**
     * Converts an int that represents a boolean in the database (ie. 1 or 0) to
     * a boolean
     *
     * @return true if the int is >= 1, false otherwise.
     */
    public static boolean toBool(int value) {
        return toBoolean(value);
    }


    /**
     * Converts a String that represents a boolean in the database (ie. "1",
     * "0", "Y", "N", "true", "false") to a boolean native.
     *
     * @return true if the string matches "Y" or "1" or "true", false otherwise.
     */
    public static boolean toBoolean(String s) {
        if (s != null && (s.equals("1") || s.equals("Y") || s.equals("true") || s.equals("on")))
            return true;
        else
            return false;
    }


    /**
     * Converts an int that represents a boolean in the database (ie. 1 or 0) to
     * a boolean.
     *
     * @return true if the int is >= 1, false otherwise.
     */
    public static boolean toBoolean(int value) {
        return (value >= 1);
    }

    /**
     * Convert a boolean value to "true" or "false".  Note that this method is
     * not internationalized and not intended to be used to produce
     * "human-readable" values.  Instead, it is intended to create boolean
     * values to be used in XML.
     *
     * @param flag the <code>boolean</code> variable that you wish to convert.
     * @return a <code>String</code> containing either "true" or "false".
     */
    public static String booleanToString(boolean flag) {
        if (flag) {
            return ("true");
        } else {
            return ("false");
        }
    }

    /**
     * Converts a boolean value to either "0" or "1".
     *
     * @param flag a <code>boolean</code> value that we are going to convert to
     * an integer.
     * @return a <code>Integer</code> value which contains either the int 1 for
     * true or "0" for false.
     */
    public static Integer booleanToInteger(boolean flag) {
        if (flag) {
            return (new Integer(1));
        } else {
            return (new Integer(0));
        }
    }

    /**
     * Converts a boolean value to either "0" or "1".
     *
     * @param flag a <code>boolean</code> value that we are going to convert to
     * an int.
     * @return a <code>int</code> value which contains either the int 1 for
     * true or "0" for false.
     */
    public static int booleanToInt(boolean flag) {
        return booleanToInteger(flag).intValue();
    }

    /**
     * Convert an <code>int</code> value to an <code>Integer</code> value.
     *
     * @deprecated as of Version 7.4.  Please use the standard API method
     * <code>new Integer(int)</code> instead.
     * @param i an <code>int</code> value to convert to an <code>Integer</code>.
     * @return an <code>Integer</code> value which is initialized with the
     * <code>i</code> int parameter that was passed to this method.
     */
    public static Integer toInteger(int i) {
        Integer tmp = new Integer(i);

        return tmp;
    }

    /**
     * Convert a string to a long value, returning -1 if there is a conversion
     * error.
     *
     * @deprecated as of Version 7.4.  Please use
     * <code>NumberFormat.getInstance().parseNumber(s)</code> instead, as it
     * better supports internationalization and user import.
     * @param s a <code>String</code> to be converted to an int.
     * @return a <code>long</code> value containing -1 if the <code>String</code>
     * is invalid, otherwise the converted string.
     */
    public static long toLong(String s) {
        long l = -1;

        try {
            l = Long.parseLong(s);
        } catch (Exception e) {
            l = -1;
        }

        return l;
    }


    /**
     * Convert a long to a float.
     *
     * @deprecated as of Version 7.4.  Please use the standard API call
     * <code>new Long(longValue).floatValue();</code> instead.
     * @return the float value of a the long
     */
    public static float toFloat(long longValue) {
        Long myLong = new Long(longValue);
        return myLong.floatValue();
    }


    /**
     * Convert a String to an integer.
     *
     * @deprecated as of Version 7.4.
     * <code>NumberFormat.getInstance().parseNumber(s).intValue()</code> for
     * display to usersas it is better prepared to handle internationalized
     * numbers.  For numbers needed for XML, please use Integer.parseInt(s)
     * instead.
     * @param s a <code>String</code> to convert to an int.
     * @return the integer represented by the String s, returns -1 if not able
     * to convert.
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
     * Convert an <code>int</code> value to a <code>String</code> value.
     *
     * @deprecated as of Version 7.4.  Please use <code>new Integer(i).toString()</code>
     * instead.
     * @param i an <code>int</code> value that will be converted to a
     * <code>String</code>.
     * @return a <code>String</code> value which contains the newly converted
     * int.
     */
    public static String intToString(int i) {
        Integer tmp = new Integer(i);

        return tmp.toString();
    }

    /**
     * Parse a date value into a string.
     *
     * @deprecated as of Version 7.4.  Use {@link net.project.util.DateFormat}
     * to display dates to users and use a yet-unwritten method to store dates
     * in XML.
     * @param date a <code>Date</code> value to be converted to a String.
     * @return a <code>String</code> value containing the date converted to a
     * <code>String</code>.
     */
    public static String dateToString(java.util.Date date) {

        String dateString = null;
        User user = SessionManager.getUser();

        if (user != null)
            dateString = dateToString(date, user);
        else
            dateString = date.toString();

        return dateString;
    }

    public static String dateToString(java.util.Date date, User user) {

        DateFormat dateFormatter = user.getDateFormatter();
        String tmpString = null;

        tmpString = dateFormatter.formatDate(date);

        return tmpString;
    }

    public static String dateToString(java.util.Date date, String formatString, User user) {
        DateFormat dateFormatter = user.getDateFormatter();
        String tmpString = null;

        tmpString = dateFormatter.formatDate(date, formatString);

        return tmpString;
    }


    public static String dateToString(java.util.Date date, String formatString) {
        User user = SessionManager.getUser();
        DateFormat dateFormatter = user.getDateFormatter();
        return dateFormatter.formatDate(date, formatString);
    }

    public static java.util.Date toDate(java.sql.Date sqlDate) {
        return (sqlDate != null) ? new java.util.Date(sqlDate.getTime()) : null;
    }

    /**
     * Parses the given date string.
     *
     * @param dateString the date string to be parsed.
     * @return the parsed Date or null if the date string was null
     * or the date could not be parsed was invalid
     * @deprecated As of Gecko Update 3;
     * This method may be removed  after two releases. <br>
     * Use DateFormat.parseDateString(String dateString) instead.
     */
    public static java.util.Date stringToDate(String dateString) {
        return stringToDate(dateString, SessionManager.getUser());
    }

    /**
     * Parses the given date string according to the users date format.
     *
     * @param dateString the date string to be parsed.
     * @param user the user whose date format to apply for parsing.
     * @return the parsed Date
     * @deprecated As of Gecko Update 3;
     * This method may be removed  after two releases. <br>
     * Use SessionManager.getUser().getDateFormatter().parseDateString(String dateString) instead.
     */
    public static java.util.Date stringToDate(String dateString, User user) {

        Date date = null;

        if (dateString != null) {

            try {
                DateFormat dateFormatter = new DateFormat(user);
                date = dateFormatter.parseDateString(dateString);

            } catch (InvalidDateException e) {
                // Returns a null date
                date = null;
            }

        }

        return date;

    }

    /**
     * Parses the given date string according to the passed in pattern.
     * @param dateString the date string to be parsed.
     * @param user the user whose date formatter to use.
     * @return the parsed Date
     * @deprecated As of Gecko Update 3;
     * This method may be removed  after two releases. <br>
     * Use SessionManager.getUser().getDateFormatter().parseDateString(String dateString, String pattern) instead.
     */
    public static java.util.Date stringToDate(String dateString, String formatString, User user) {
        DateFormat dateFormatter = user.getDateFormatter();
        java.util.Date tmpDate = null;

        try {
            tmpDate = dateFormatter.parseDateString(dateString, formatString);
        } catch (InvalidDateException e) {
            // date remains null
        }

        return tmpDate;
    }


    /**
     * Parses a string and escapes certain characters that are illegal in
     * XML text values.
     *
     * @param string the string to parse
     * @return the string with values escaped
     * @deprecated As of Gecko Update 3.
     * This method does not handle illegal unicode characters.  Additionally,
     * its implementation currently relies on {@link net.project.xml.XMLUtils#escape}
     * which may change to be incompatible with XML escaping.
     * Use {@link net.project.xml.XMLUtils#escape} instead.
     */
    public static String toXMLString(String string) {
        return (HTMLUtils.escape(string));
    }

    /**
     * Parses a boolean and formats it suitable for XML text values.
     * @param value the boolean to parse
     * @return the string representation of the boolean
     * @deprecated As of Gecko Update 3.
     * This method simply serves as a convenience and is the same as calling
     * <code>toXMLString(booleanToString(value))</code>.  Additionally,
     * its implementation currently relies on {@link net.project.xml.XMLUtils#escape}
     * which may change to be incompatible with XML escaping.
     * No replacement;  Use <code>net.project.xml.XMLUtils.escape(Conversion.booleanToString(value))</code>
     * instead.
     */
    public static String toXMLString(boolean value) {
        return (HTMLUtils.escape(booleanToString(value)));
    }

    /**
     * This method converts a collection of strings into a comma-separated list.
     * Unfortunately, this method has to handle multiple cases because of the
     * some of the eccentricities of the english language.  In english there are
     * four different cases this this method handles:<br>
     * <br>
     * 1. <b>Zero items in the list of Strings</b>  This will produce an empty string.<br>
     * 2. <b>One item in the list of String</b>  This will produce a string which only
     * contains the single item from the list of string without any separators.<br>
     * 3. <b>Two items in the list of Strings</b>  This will use the
     * <code>prm.global.conversion.list.twoitemseparator</code> property (in
     * english, this is "{0} and {1}") to construct a two item string.  If the
     * two items in the <code>listOfStrings</code> parameter were "apple" and
     * "orange", the output of the method (in English locales) would be
     * "Apple and Orange".<br>
     * 4. <b>Three or more items in a the list of Strings</b>  This case will
     * use two properties to construct the list.  One token will be used to separate
     * any two items in the list except the last pair.  Another token will be used
     * to separate the last items.  In English, by default, the following tokens
     * are used:<br>
     *     <code>prm.global.conversion.list.listseparator</code> = "{0}, {1}"
     *     <code>prm.global.conversion.list.lastlistseparator</code> = "{0}, and {1}"
     * <br>
     * So if the <code>listOfStrings</code> parameter contained four Strings:
     * "Apple", "Banana", "Carrot", "Dirt" the result of this method would be
     * "Apple, Banana, Carrot, and Dirt".<br>
     *
     * @param listOfStrings a <code>List</code> of parameters which will be
     * concatenated into a comma-separated string.
     * @return a <code>String</code> which contains all of the Strings from
     * the <code>listOfStrings</code> parameter.
     */
    public static String toCommaSeparatedString(List listOfStrings) {
        //If there is a null list, return an empty string
        if (listOfStrings == null) {
            return "";
        }

        int listSize = listOfStrings.size();
        String commaSeparatedList = "";

        for (ListIterator it = listOfStrings.listIterator(); it.hasNext(); ) {
            //We must test for the first prior to calling it.next()
            boolean isFirst = !it.hasPrevious();
            String currentItem = (String)it.next();

            if (isFirst) {
                //This is the first item
                commaSeparatedList = currentItem;
            } else if (listSize == 2) {
                commaSeparatedList = PropertyProvider.get("prm.global.conversion.list.twoitemseparator",
                    new String[] {commaSeparatedList, currentItem}) ;
            } else if ((listSize > 2) && (it.hasNext())) {
                commaSeparatedList = PropertyProvider.get("prm.global.conversion.list.listseparator",
                    new String[] {commaSeparatedList, currentItem});
            } else {
                //This is the last item in the list
                commaSeparatedList = PropertyProvider.get("prm.global.conversion.list.lastlistseparator",
                    commaSeparatedList, currentItem);
            }
        }

        return commaSeparatedList;
    }

    /**
     * Wrap an object in a list object.
     *
     * @param object a <code>Object</code> that will be the only object in a list.
     * @return a <code>List</code> containing only the object provided as a
     * parameter.
     */
    public static List asList(Object object) {
        List list = new ArrayList();
        list.add(object);
        return list;
    }
}

