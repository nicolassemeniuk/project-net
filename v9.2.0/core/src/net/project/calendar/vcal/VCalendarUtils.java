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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.calendar.vcal;

import java.util.StringTokenizer;

/**
 * Provides useful methods for manipulating vCalendar properties and parameters.
 */
public class VCalendarUtils {

    /**
     * Quoted-Printable CRLF to insert when escaping text.
     */
    private static final String QUOTED_PRINTABLE_CRLF = "=0D=0A";
    
    /**
     * Soft line break that may be used to split lines in Quoted-Printable encoding.
     */
    private static final String QUOTED_PRINTABLE_SOFTLINEBREAK = "=\n";

    /**
     * Returns a string in Quoted-Printable format.
     * All line breaks are replaced by <code>=0D=0A=&#92;n</code>.
     * A line break is defined as any of:
     * <ul>
     * <li><code>&#92;r&#92;n</code> - PC line break</li>
     * <li><code>&#92;r</code> - Mac line break</li>
     * <li><code>&#92;n</code> - Unix line break</li>
     * </ul>
     * @param s the string to format
     * @return the formatted string or the empty string if parameter was null
     */
    public static String makeQuotedPrintable(String s) {
        StringBuffer result = new StringBuffer();

        if (s != null) {

            // Normalize text to Unix linefeeds
            // PC --> Unix
            s = s.replaceAll("\\r\\n", "\n");
            // Mac --> Unix
            s = s.replaceAll("\\r", "\n");

            // We include delimiters in results so that empty tokens
            // are returned
            // An empty token is returned simply as a delimeter
            StringTokenizer tokens = new StringTokenizer(s, "\n", true);
            while (tokens.hasMoreTokens()) {
                String nextToken = tokens.nextToken();

                if (nextToken.endsWith("\n")) {
                    // Add next token to result, excluding linefeed
                    result.append(nextToken.substring(0, nextToken.length()-1));
                    // Add the vCalendar linefeed
                    result.append(QUOTED_PRINTABLE_CRLF);
                    result.append(QUOTED_PRINTABLE_SOFTLINEBREAK);

                } else {
                    // Last token:  no ending \n
                    result.append(nextToken);

                }

            }

        }

        return result.toString();
    }

    /**
     * Indicates whether string must be encoded Quoted-Printable.
     * A string must be encoded Quoted-Printable if it includes at least
     * one line break.  See description of {@link #makeQuotedPrintable} for
     * details on line breaks.
     * @param s the string to determine whether it must be quoted printable;
     * a null value returns <code>false</code>
     * @return true if the string must be encoded Quoted-Printable; false
     * if not
     */
    public static boolean isQuotedPrintableRequired(String s) {
        boolean isQuotedPrintableRequired = false;

        if (s != null) {

            // Normalize text to Unix linefeeds
            // PC --> Unix
            s = s.replaceAll("\\r\\n", "\n");
            // Mac --> Unix
            s = s.replaceAll("\\r", "\n");

            // If we have a linefeed, then it must be encoded QUoted-printable
            if (s.indexOf('\n') >= 0) {
                isQuotedPrintableRequired = true;
            }
        }

        return isQuotedPrintableRequired;
    }

    /**
     * Returns a parameter value with certain characters escaped.
     * Escapes <code>;</code> characters by prefixing a backslash.
     * For example, <code>;</code> becomes <code>\;</code>.
     * @param s the parameter value to escape
     * @return the parameter value with escaped characters; returns the
     * empty string if parameter was <code>null</code>
     */
    public static String escapeParameterValue(String s) {

        if (s == null) {
            return "";
        }

        StringBuffer result = new StringBuffer();
        int currentPos = 0;
        int charPos = 0;

        // Look for characters requiring escaping starting at current pos
        while ((charPos = s.indexOf(';', currentPos)) >= 0) {
            // Add string before character
            result.append(s.substring(currentPos, charPos));

            // Add escape backslash
            result.append('\\');

            // Add the character
            result.append(s.substring(charPos, charPos + 1));

            currentPos = charPos + 1;
        }

        // Add remainder of string if any
        if (currentPos < s.length()) {
            result.append(s.substring(currentPos));
        }

        return result.toString();
    }

}
