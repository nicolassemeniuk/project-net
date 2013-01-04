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
| Singleton PropertyProvider
+----------------------------------------------------------------------*/
package net.project.base.property;


/**
 * Provides facilities for parsing token values to replace other tokens
 * that are references inside the value.
 */
class TokenParser {

    private static final char OPEN_DELIMITER = '{';
    private static final char CLOSE_DELIMITER = '}';
    private static final char TOKEN_DELIMITER = '@';

    /**
     * Parsed a value looking for other tokens to replace their values.
     * Embedded tokens are of the form <code>{<!---->@tokenname}</code>
     * Continues until all token values have been parsed.
     * Recurses until no more tokens are found.
     * If any found token values cannot be looked-up, they are left as-is.
     * @param tokenValue value to parse
     * @return the parsed value
     */
    static String getParsedValue(String tokenValue) {

        StringBuffer parsedValue = new StringBuffer();
        StringBuffer token = (tokenValue != null && tokenValue.indexOf(OPEN_DELIMITER) >= 0) ? new StringBuffer(tokenValue) : null;

        if (token != null) {
            // Token contains at least an open delimeter;  it might contain a token

            int startAppendPosition = 0;
            int startPosition = -1;
            int endPosition = -1;

            for (int currentPosition = 0; currentPosition < token.length(); currentPosition++) {

                if (token.charAt(currentPosition) == OPEN_DELIMITER) {
                    // If we find an open delimeter, grab it
                    // every time we find a new open, reset the starting position
                    // and reset the endPosition
                    // This handles cases where text is like {@text{@token}
                    // The first {@ is effectively ignored
                    // startPosition is the position of the "{"
                    startPosition = currentPosition;
                    endPosition = -1;
                }

                if (token.charAt(currentPosition) == CLOSE_DELIMITER && startPosition >= 0) {
                    // if we find a close delim and the start has been set, then set the end position
                    endPosition = currentPosition + 1;
                }

                // IF we have both a start and end, AND they are not equal, try to replace the token
                // Otherwise, we continue to loop

                if (startPosition >= 0 && endPosition >= 0 && endPosition > startPosition) {

                    // Grab the text inside { }
                    String replaceString = token.substring((startPosition + 1), (endPosition - 1));

                    if (replaceString != null && replaceString.charAt(0) == TOKEN_DELIMITER) {
                        // Begins with @

                        // If token not found, result is equal to replaceString
                        // Therefore it is ok to go ahead and replace it
                        // result is null only when an error occurs looking it up
                        // Note that getting the token value here will
                        // recurse and parse the value until all tokens within
                        // it are replaced
                        String result = PropertyProvider.get(replaceString);

                        if (result != null) {

                            // Copy everything up to token starting position
                            if (startAppendPosition < startPosition) {
                                parsedValue.append(token.substring(startAppendPosition, startPosition));
                            }

                            // Copy looked-up token value
                            parsedValue.append(result);
                            startAppendPosition = endPosition;
                        }

                    } else {
                        // Didn't find TOKEN_DELIMETER at next position
                        // The thing we have is not a token
                        parsedValue.append(token.substring(startAppendPosition, endPosition));
                        startAppendPosition = endPosition;
                    }

                    // Reset positions and continue
                    startPosition = -1;
                    endPosition = -1;

                }

            }

            // Now add in any remaining characters
            if (startAppendPosition < token.length()) {
                parsedValue.append(token.substring(startAppendPosition, token.length()));
            }

        } else {
            // original value is null or there was no token in it
            // parsed value is identical to original
            if (tokenValue != null) {
                parsedValue.append(tokenValue);
            }
        }

        return parsedValue.toString();
    }

}
