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
|   $Revision: 19016 $
|       $Date: 2009-03-11 12:31:34 -0200 (mié, 11 mar 2009) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.util;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Provides convenience methods for formatting text for display on HTML pages.
 */
public class HTMLUtils {

    /**
     * Escapes special HTML characters in the string.
     * This includes &amp;, &lt;, &gt;, &quot;
     * <p>
     * <b>Note:</b> Only use for escaping text for HTML display.
     * For XML, use {@link net.project.xml.XMLUtils#escape}.
     * @param str the string to replace characters in
     * @return the string with special characters replaced; empty string is
     * returned if input string is null
     */
    static public String escape(String str) {
        if(str == null) {
            return "";
        }

        char[] source = str.toCharArray();
        StringBuffer result = new StringBuffer(source.length);
        char current;

        // Loop over each character in source array
        // and append to result buffer, replacing special characters
        for(int i = 0; i < source.length; i++) {
            current = source[i];
            switch(current) {
            case '&':
                result.append("&amp;");
                break;
            case '<':
                result.append("&lt;");
                break;
            case '>':
                result.append("&gt;");
                break;
            case '"':
                result.append("&quot;");
                break;
            default:
                result.append(current);
            }
        }

        return result.toString();
    }
    
    /**
     * @param str string to parse for special characters.
     * @return Parsed string
     */
    static public String escapeForValidFileName(String str) {
        if(str == null) {
            return "default";
        }
        Pattern escaper = Pattern.compile("[\\<>\":*?| ]");
        return escaper.matcher(str).replaceAll("_");
    
   }

    /**
     * Escapes single quote characters in the string.
     * So these can be used inside a javascript. 
     * <p>
     * <b>Note:</b> Only use for escaping text to be used by javascript.
     * 
     * @param str the string to replace characters in
     * @return the string with special characters replaced; empty string is
     * returned if input string is null
     * 
     * @see HTMLUtils#escape(String)
     */
    public static String jsEscape(String raw)
    {
        if (raw == null)
        {
            return "";
        }

        StringBuffer buffer = new StringBuffer();

        int afterApos = 0;
        int aposIndex = raw.indexOf('\'');

        while (aposIndex != -1)
        {
            buffer.append(raw.substring(afterApos, aposIndex));
            buffer.append("\\\'");
            afterApos = aposIndex + 1;
            aposIndex = raw.indexOf('\'', afterApos);
        }

        buffer.append(raw.substring(afterApos));

        return buffer.toString();
    }

    /**
     * Formats the specified string replacing linefeeds with HTML &lt;BR&gt;
     * tagd.
     * @param str the string to format
     * @return the formatted string
     */
    public static String formatHtmlBreaks(String str) {
        if (str == null) {
            return "";
        }

        StringBuffer result = new StringBuffer(str.length());

        // Normalize text to Unix linefeeds
        // PC --> Unix
        str = str.replaceAll("\\r\\n", "\n");
        // Mac --> Unix
        str = str.replaceAll("\\r", "\n");

        // We include delimiters in results so that empty tokens
        // are returned
        // An empty token is returned simply as a delimeter
        StringTokenizer tokens = new StringTokenizer(str, "\n", true);
        while (tokens.hasMoreTokens()) {
            String nextToken = tokens.nextToken();

            if (nextToken.endsWith("\n")) {
                // Add next token to result, excluding linefeed
                result.append(nextToken.substring(0, nextToken.length()-1));
                // Add HTML tag
                result.append("<br>");

            } else {
                // Last token:  no ending \n
                result.append(nextToken);

            }

        }

        return result.toString();
    }


    /**
     * Escaped special characters and replaces linefeeds with HTML &lt;BR&gt;
     * tags.
     * @param str the string to format
     * @return the formatted string
     */
    public static String formatHtml(String str) {
        return HTMLUtils.formatHtmlBreaks(escape(str));
    }

}
