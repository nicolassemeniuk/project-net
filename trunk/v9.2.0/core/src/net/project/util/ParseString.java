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
package net.project.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import net.project.base.property.PropertyProvider;

/**
 * A set of static String manipulation utility methods.
 */
public class ParseString {

    /**
     * Indicates whether the specified string is empty.
     * Empty is defined as <code>null</code> or empty string, after trimming
     * leading and trailing spaces.
     * @param s the string to check
     * @return true if the string is empty; false otherwise
     * @deprecated As of 7.7.1; use {@link Validator#isBlankOrNull(String)} instead.
     */
    public static boolean isEmpty(String s) {
        return Validator.isBlankOrNull(s);
    }

    /**
     * Coverts a String representation of a number of Bytes
     * to a String representation of KBytes.
     * @param s the string containing the number of bytes;  This is expected
     * to be a number only. When null, the empty string is returned.
     * If the number is less than one KB, a value of <code>1 KB</code> is returned
     * @return the string representation of the same number in Kilobytes;
     * it contains a number with <code>" KB"</code> appended as a suffix.
     * A hardcoded algorithm is used to insert comma characters as digit
     * grouping symbols every 3 characters.
     */
    public static String makeKfromBytes(String s) {

        if (Validator.isBlankOrNull(s)) {
            return "";
        }

        Double value;

        try {
            value = new Double(s);
            double filesize = value.doubleValue();
            filesize /= 1024;
            if (filesize < 1) {
                filesize = 1;
            }
            value = new Double(filesize);

        } catch (NumberFormatException e) {
            return "makeKfromBytes: " + s;

        }

        String outstr = NumberFormat.getInstance().formatNumber(value.doubleValue(), "###,###");
        return outstr + PropertyProvider.get("prm.global.datavalue.abbreviation.kilobyte.name");
    }


    /**
     * Converts a String of comma separated items into a ArrayList of String
     * items.
     * @param string a String containing a comma separated list of items.
     * @return an ArrayList of the items where every item in the source string
     * separated by a comma becomes an array element.
     */
    public static ArrayList makeArrayListFromCommaDelimitedString(String string) {
        ArrayList list;

        if (string == null) {
            list = null;
        } else {
            list = new ArrayList(makeListFromCommaDelimitedString(string));
        }

        return list;
    }


    /**
     * Coverts a String of comma separated items into a String array of the items.
     * <p/>
     * Leading and trailing spaces of each item are trimmed.
     * Any empty item is ignored, thus if the entire string is empty, then the resulting string will have no elements.
     * If the given string is non-empty then the resulting string will have one or more elements.
     * <p/>
     * For example:
     * <ul>
     * <li>null - returns throws exception
     * <li><code>""</code>, <code>" "</code>, <code>"    "</code> etc. - returns zero length array
     * <li><code>"x"</code>, <code>" x "</code> etc. - returns single element array where item [0] is <code>"X"</code>
     * <li><code>"x,y"</code>, <code>" x , y "</code> etc. - returns two item array containing <code>"x"</code> and <code>"y"</code>
     * <li><code>"x,"</code>, <code>",,x"</code> are equivalent to <code>"x"</code>
     * </ul>
     * @param s a String containing a comma separated list of items.
     * @return a String[] array equivelant of the items where every item in the source string separated by a comma becomes an array element.
     * @throws NullPointerException if s is null
     */
    public static String[] makeArrayFromCommaDelimitedString(String s) {
        List elements = makeListFromCommaDelimitedString(s);
        return (String[]) elements.toArray(new String[elements.size()]);
    }

    /**
     * Creates a list containing the trimmed comma-separated elements of the given
     * string.
     * <p/>
     * Empty elements are ignored, thus if s contains only whitespace or elements consisting
     * only of whitespace, the resulting list will be empty.
     * @param s the string from which to construct the list
     * @return the possibly empty list of elements
     * @throws NullPointerException if the given string is null
     */
    private static List makeListFromCommaDelimitedString(String s) {

        if (s == null) {
            throw new NullPointerException("Missing required parameter s");
        }

        List elements = new LinkedList();

        StringTokenizer tok = new StringTokenizer(s, ",");
        while (tok.hasMoreElements()) {
            String element = ((String) tok.nextElement()).trim();
            if (element.length() > 0) {
                elements.add(element);
            }
        }

        return elements;
    }

    /**
     * Returns the file extension of the filename in the specified string.
     * The file extension is defined as the text following the last period (".")
     * to the end of the string (excluding the period itself).
     * If the string has no extension or is <code>null</code>, the empty string
     * is returned.  If the period is the last character then the empty string
     * is returned
     * @return the file extension (excluding ".") or the empty string
     * @deprecated Use {@link FileUtils#getFileExt} instead; it is better
     * located in that class
     */
    public static String getFileExt(String s) {
        return FileUtils.getFileExt(s);
    }


    /**
     * Returns a String where each occurance of a double quote character is replaced by two double quote characters.
     * The source String: Roger needs to "sleep" more. will be returned as: Roger needs to ""sleep"" more.
     * This conversion is useful for escaping double quotes for CSV files and other formats where quotes must be escaped.
     * <b>Note:</b> Carriage Return characters (\r) are removed from the string.
     * @param s the string to escape
     * @return the string with each double quote replaced by two double quotes
     */
    public static String escapeDoubleQuotes(String s) {
        if (s == null) {
            return "";
        }

        StringBuffer result = new StringBuffer();

        char[] chars = s.toCharArray();
        for (int pos = 0; pos < chars.length; pos++) {
            char c = chars[pos];

            if (c == '\r') {
                // Skip it
            } else {

                if (c == '"') {
                    result.append("\"");
                }

                result.append(c);
            }
        }

        return result.toString();
    }

}
