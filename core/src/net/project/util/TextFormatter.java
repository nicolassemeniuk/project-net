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
|
+----------------------------------------------------------------------*/
package net.project.util;

import java.text.BreakIterator;
import java.util.Locale;

import net.project.base.property.PropertyProvider;

/**
 * Utilities to reformat text.
 * @author Matthew Flower
 * @author Tim Morrow
 * @since Gecko
 */
public class TextFormatter {

    /**
     * Reformats text to wrap with at most <code>wrapColumn</code> display
     * characters on a line.
     * Lines are broken with <code>\n</code> characters.  Note that a line
     * may contain additional space or control characters such that each
     * line contains more than <code>wrapColumn</code> actual characters.
     * Uses <code>java.text.BreakIterator.getLineInstance()</code> to determine
     * where it is accetable to break lines.
     * <p>
     * Sadly, there is a bug that causes breaks to occur on things like
     * <code>http://www.sun.com/</code>, at both the <code>:</code> and <code>.</code>.
     * See
     * <a href="http://developer.java.sun.com/developer/bugParade/bugs/4242585.html">
     * 4242585 - BreakIterator.getLineBreak() breaks on an embedded period</a> for more details.
     * Some attempt has been made to work around this issue, however this makes
     * the method tailored to western locales.
     * </p>
     * @param text the text to be re-formatted
     * @param wrapColumn the maximum width of display text to be displayed on
     * a single line.
     * @param locale the locale on which the rules for breaking text are based
     * @return the reformatted text.
     * @throws NullPointerException if text or locale are null
     * @see java.text.BreakIterator
     */
    public static String adjustRightColumn(String text, int wrapColumn, Locale locale) {

        if (text == null) {
            throw new NullPointerException("text is required");
        }

        if (locale == null) {
            throw new NullPointerException("locale is required");
        }

        // 11/07/2002 - Tim
        // Purpose of reimplementation:
        // * Current algorithm doesn't wrap correctly when there are multiple spaces
        // between words and break boundary falls on the spaces
        // * It always seems to break the last word on a line by itself
        // * We need to use line break rules for the Locale
        // * Doesn't wrap if the last word on a line ends at the last column

        // The resulting wrapped text
        StringBuffer result = new StringBuffer();

        // Iterate over text looking for next linefeed
        // We wrap the parapgraph preceding the linefeed
        int pos = 0;
        int start = 0;
        while (pos < text.length()) {

            if (text.charAt(pos) == '\n') {

                // We found a line feed, wrap that parapgraph
                result.append(wrapParagraph(text.substring(start, pos), wrapColumn, locale));

                // Append all the consecutive line break characters
                // This avoids losing line break characters when more than
                // just '\n' is used for line breaks
                while (pos < text.length() &&
                            (text.charAt(pos) == '\n' ||
                             text.charAt(pos) == '\r' ||
                             text.charAt(pos) == '\f') ) {
                    result.append(text.charAt(pos));
                    ++pos;
                }

                // Our next starting position is this non-line break character
                start = pos;

            } else {
                // Character is not line feed, continue with next
                ++pos;
            }

        }

        // Process remaining characters
        if (start < text.length()) {
            result.append(wrapParagraph(text.substring(start, text.length()), wrapColumn, locale));
        }

        return result.toString();
    }

    /**
     * Wraps a single paragraph, breaking lines based on a Locale's rules.
     * Note that each wrapped line may not contain exactly <code>wrapColumn</code>
     * number of characters; some lines may contain additional trailing spaces.
     * Since trailing spaces are not displayed at the end of a line,
     * it is not counted in the line length.
     * This means that a line will contain at most <code>wrapColumn</code>
     * <i>displayed</i> characters.
     * Lines are broken by <code>\n</code> characters.
     * This algorithm is based on
     * <a href="http://oss.software.ibm.com/icu4j/">IBM's International Components
     * for Unicode for Java</a> <a href="http://oss.software.ibm.com/icu/userguide/boundaryAnalysis.html">Enhanced word-break detection</a>
     * @param text the text to wrap; it is assumed this is a parapgraph without
     * line breaks; if it does include linebreaks, wrapping may be unpredictable
     * @param wrapColumn the maximum number of display characters on
     * one line
     * @return the wrapped text; there is no trailing linefeed added to the
     * paragraph itself
     */
    private static String wrapParagraph(String text, int wrapColumn, Locale locale) {

        // The wrapped output
        StringBuffer result = new StringBuffer();

        // The remaining text to wrap
        String remainingText = text;

        // Get a Line BreakIterator for the specified locale
        // This is used to determine suitable positions for breaking lines
        // based on the Locale's rules.  For example, given the text:
        //     this is some text ?
        // it would break the line _after_ the ?, not before.
        // This is somewhat different to word breaks
        BreakIterator lineBreakIt = BreakIterator.getLineInstance(locale);

        // Loop over ever-shrinking remining text
        // Chopping up remaining text and appending to result
        // each chunk terminated with '\n'
        while (remainingText.length() > 0) {
            lineBreakIt.setText(remainingText);

            // Next place to break is closest of wrapColumn or end of string
            int nextEndPos = java.lang.Math.min(wrapColumn, remainingText.length());

            // We can skip forward over space or control characters
            // Since that can remain at the end of the line without affecting
            // the display
            if (nextEndPos < remainingText.length()) {
                // Skip ahead to next non white space character
                while (nextEndPos < remainingText.length() &&
                            (Character.isSpaceChar(remainingText.charAt(nextEndPos)) ||
                             Character.isISOControl(remainingText.charAt(nextEndPos))) ) {

                    ++nextEndPos;
                }
            }

            // Now find the last legal line break decision at or before
            // the current position
            int breakPos = 0;

            if (nextEndPos == remainingText.length()) {
                // We are at the end of the string
                // We break at this position
                breakPos = nextEndPos;

            } else if (nextEndPos == remainingText.length() - 1) {
                // This else if block is special handling for the last character
                // in the text.
                // See bug 4177479 at
                // http://developer.java.sun.com/developer/bugParade/bugs/4177479.html
                // For more information
                // This block may be removed when the bug is fixed

                // We're on the last character, which might be a valid
                // break position.  However, due to implementation of preceding()
                // method, we can't pass it a position greater than the last
                // position in the text
                // Our workaround is to find the previous position to the last
                // position since the last position is going to be the end
                // of the string
                lineBreakIt.last();
                breakPos = lineBreakIt.previous();

            } else {
                // We're before the last character position
                // We find the break on or before this position...  We
                // add one since preceding() always returns a position
                // less than the passed value
                breakPos = lineBreakIt.preceding(nextEndPos+1);

            }

            // 11/07/2002 - Tim
            // Workaround to handle bug 4242585
            // Find it here:
            // http://developer.java.sun.com/developer/bugParade/bugs/4242585.html
            // This code should be removed when bug is fixed
            // It is making assumptions that breakIterator should already
            // have dealt with
            if (breakPos > 1 && breakPos < remainingText.length() &&
                    (remainingText.charAt(breakPos-1) == '.' ||
                     remainingText.charAt(breakPos-1) == ':') &&
                    (!Character.isSpaceChar(remainingText.charAt(breakPos)) &&
                    !Character.isSpaceChar(remainingText.charAt(breakPos-2))) ) {

                // Preceding character is "." or ":" and preceding and following
                // characters are non-spaces
                // So find previous break position manually
                for (int currentPos = breakPos; currentPos > 0; currentPos--) {
                    if (Character.isSpaceChar(remainingText.charAt(currentPos-1))) {
                        breakPos = currentPos;
                        break;
                    }
                }

            }
            // End Workaround

            //
            // Now sub-string the next line
            //
            int nextStartPos = 0;
            if (breakPos == 0) {
                // if the break occurs at the starting position,
                // then the line has no legal line-break positions
                result.append(remainingText.substring(0, nextEndPos));
                nextStartPos = nextEndPos;

            } else {
                // Otherwise, we got a good line break position
                result.append(remainingText.substring(0, breakPos));
                nextStartPos = breakPos;
            }

            // Remove the text just added to the result from
            // the remaining text
            // And add a linefeed to result if that wasn't the last
            // piece of remaining text
            if (nextStartPos < remainingText.length()) {
                // More to process
                // Break the line
                result.append("\n");
                // Shorten the remaining text
                remainingText = remainingText.substring(nextStartPos);

            } else {
                // At end; this will cause the loop to terminate
                remainingText = "";
            }

        }

        return result.toString();
    }

    /**
     * Parses the text body looking for hyperlinks.
     * It finds http://, https:// or www. sequences to and changes clickable links
     * Some checks are performed to only create links from valid-looking URLs.
     * Currently newlines are not adjusted; it is assumed word-wrapping will take
     * care of that.
     * <p>
     * No other HTML escaping is performed.  To fully wrap and prepare text
     * for display on an HTML page, the following is suggested:
     * <code><pre>
     *     makeHyperlinkable(HtmlGen.formatHtml(adjustRightColumn(text)));
     * </pre></code>
     * </p>
     * 
     * @param text the text in which to find hyperlinks
     * @return the text with hyperlinks inserted
     */
    public static String makeHyperlinkable (String text) {
        
        // The text to adjust; manipulated as a char[] array for performance.
        // It offeres significant improvements over a StringBuffer
        char[] source = text.toCharArray();

        // The result text; It is going to be at least as big as the source.
        StringBuffer target = new StringBuffer(text.length());
        
        
        // Object representing the information about a found anchor
        // It only needs to be constructed once; its information will be completely
        // overwritten each time.
        FoundAnchor foundAnchor = new FoundAnchor();

        // Iterate over characters in source string
        // Look for the start of an href in the string at each character position
        // Caution: There will be one iteration of this loop for each character
        // in the source string.  For example, an 80Kb string will have 80,000
        // iterations.  As a result, care must be taken to avoid unnecessary
        // manipulation of objects / strings in the loop.
        int i = 0;
        while (i < source.length) {
    
            // Look for an href in the source array, starting at the current position
            if (isHrefFound(source, i, foundAnchor)) {
                
                // Found one.  foundAnchor contains information needed to construct
                // the <a> tag
                target.append("<a href=\"" + foundAnchor.href + "\" target=\"external_viewer\">");
                target.append(foundAnchor.value);
                target.append("</a>");
    
                // Skip past the end of the parsed href
                i = foundAnchor.nextCharacterPosition;
    
            } else {
                // No match, no special character, simply add char to target
                target.append(source[i]);
                i++;
            }
    
        }
            
        return makeMailable(target.toString());
    }

    public static String truncateString(String sourceString, int maxLength) {
        String stringToReturn = sourceString;
        if (sourceString == null) {
            return null;
        }

        if (sourceString.length() > maxLength) {
            stringToReturn = PropertyProvider.get("prm.global.textformatter.truncatedstringformat", sourceString.substring(0, maxLength));
        }

        return stringToReturn;
    }

    /**
     * Indicates whether an href is found starting at the specified position in the
     * the character array.
     * Note: A char[] is used for efficiency; this method will be called 1,000s
     * of times to process a large string.
     * @param source the characters to check for href in;  this is not modified by
     * this method
     * @param start the position to start looking for href in the source array
     * @param foundAnchor information about the found anchor - only valid if
     * the method returned true
     * @return true if an href was found; false otherwise
     */
    private static boolean isHrefFound(char[] source, int start, FoundAnchor foundAnchor) {
        
        // 10/19/01 - Tim - This method breaks some standard programming principals:
        // 1. It is way too big; however, it is quite inconvenient to define
        //    methods that must return multiple values, where one is an integer:
        //    inner classes have to be used in that case (Integers have a final value)
        
        // 2. It has side effects.  That is, it returns a boolean AND modifies
        //    one of its input parameters (foundAnchor).  This is done
        //    to avoid having two methods; one that checks for the anchor
        //    and one that processes it, since they would end up duplicating
        //    a large portion of code.
        
        // The potentially matched href
        StringBuffer potential = null;
        
        // Indicates whether an href was found at the start of the source string
        boolean isHrefFound = false;

        // Indicates the next character position to process.
        int nextCharacterPosition = start;

        // The strings we will be looking for
        String httpProtocol = "http://";
        String httpsProtocol = "https://";
        String noProtocolPrefix = "www.";
        
        // Indicates which protocol to be prefixed back onto the found URL.
        String protocolToPrefix = null;
        
        // Indicates whether there was a match with one of the strings
        boolean isMatch = false;
        
        // Indicates that the address we are looking for must have more
        // than one "." - required when we find "www."
        boolean isMultipleDotRequired = false;


        // Check for presence of protocols: "http://" and "https://"
        // If either is found, we skip those to get the the host part
        // If not found, we check for "www." and start from there
        
        if ( (source.length - start) > httpProtocol.length() &&
            new String(source, start, httpProtocol.length()).equalsIgnoreCase(httpProtocol)) {

            // Starts with "http://"
            // Skip that prefix
            nextCharacterPosition += httpProtocol.length();
            protocolToPrefix = httpProtocol;
            
            // We don't need more than one "." - "http://xyz.com" is valid
            isMultipleDotRequired = false;
            isMatch = true;

        } else if ( (source.length - start) > httpsProtocol.length() &&
                    new String(source, start, httpsProtocol.length()).equalsIgnoreCase(httpsProtocol)) {

            // Starts with "https://"
            // Skip that prefix
            nextCharacterPosition += httpsProtocol.length();
            protocolToPrefix = httpsProtocol;
            
            // We don't need more than one "." - "https://xyz.com" is valid
            isMultipleDotRequired = false;
            isMatch = true;


        } else if ( (source.length - start) > noProtocolPrefix.length() &&
                    new String(source, start, noProtocolPrefix.length()).equalsIgnoreCase(noProtocolPrefix)) {

            // Starts with "www."
            // We'll be adding "http://" later
            protocolToPrefix = httpProtocol;

            // We need more than one "." - "www.xyz" is not valid in our scenario.
            // Otherwise we could match the end of a sentence.  For example:
            // "This is a sentence.This is another sentence"
            isMultipleDotRequired = true;
            isMatch = true;

        } else {
            
            // No protocol, no "www."
            isMatch = false;
        
        }


        if (isMatch) {
            boolean isDone = false;
            char ch;

            // At this point "nextCharacterPosition" is set to the starting position of the
            // actual URL, excluding the http:// portion
            // We grab all the characters up to the first whitespace character
            // OR illegal href character

            potential = new StringBuffer();
            while (nextCharacterPosition < source.length & !isDone) {
                ch = source[nextCharacterPosition];

                if (ch == ' ' ||
                        ch == '\n' ||
                        ch == '\r' ||
                        ch == '\t' ||
                        ch == ',' ||
                        ch == ')' ||
                        ch == '<') {
                    // We found an invalid character
                    // We're done
                    isDone = true;

                } else {
                    // Legal, so add to potential href
                    potential.append(ch);
                    nextCharacterPosition++;

                }

            }

            // At this point "nextCharacterPosition" is set beyond the end of
            // the found URL

            // Now perform final checks to make sure found URL is valid
            if (potential.toString().indexOf(".") < 0 ||
                (isMultipleDotRequired && 
                 potential.toString().indexOf(".") == potential.toString().lastIndexOf(".")) ) {
                
                // If there is no "." OR
                // we need more than one "." but we didn't find more than one
                // (determined by the first "." position equal to the last "." position)
                // Then we don't have a valid href
                isHrefFound = false;

            } else {

                // Either we didn't need more than one ".", or we found more than one
                isHrefFound = true;
            }



        } else {
            // No match
            isHrefFound = false;
        
        }


        // Final processing
        // We construct the href, specify the value portion of the anchor
        // and specify the next character position to parse from in the source array
        // Note that the display value is exactly what was specified.  For example:
        //    Found "http://xyz.com", href="http://xyz.com", value="http://xyz.com"
        //    Found "www.project.net", href="http://www.project.net", value="www.project.net"

        if (isHrefFound) {
            // Set the href to the one we found
            foundAnchor.href = protocolToPrefix + potential.toString();
            
            // The value is _exactly_ what was parsed from the source string
            foundAnchor.value = new String(source, start, (nextCharacterPosition - start));

            // The number of characters we processed from the source string
            foundAnchor.nextCharacterPosition = nextCharacterPosition;
        }

        return isHrefFound;
    }

    private static String makeMailable(String str){

        char[] strArray = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        StringBuffer tempBuffer = new StringBuffer();

        if(str != null && str.indexOf('@') < 0 ) {
            return str;
        }

        for (int i = 0 ; i < strArray.length ; i++ ) {

            //System.out.println(strArray[i]);

            if(strArray[i] ==' ' || strArray[i] == ',' || strArray[i] == '(' || strArray[i] ==')' ) {

                if(tempBuffer.toString().indexOf('@') > 0 && tempBuffer.toString().indexOf('.') >0  ){
                    appendMailTo(tempBuffer);
                }

                sb.append(tempBuffer.toString());
                tempBuffer = new StringBuffer();
            }
            tempBuffer.append(strArray[i]);
        }

        if(tempBuffer.toString().indexOf('@') > 0 && tempBuffer.toString().indexOf('.') > 0   ){
            appendMailTo(tempBuffer);
        }

        sb.append(tempBuffer.toString());
        return sb.toString();

    }

    private static void appendMailTo(StringBuffer tempBuffer){
        String email = tempBuffer.toString();

        if(!(tempBuffer.toString().toUpperCase().indexOf("mailto:".toUpperCase())>0)) {
            if(tempBuffer.charAt(0) == ' ' || tempBuffer.charAt(0) == ',' || tempBuffer.charAt(0) == '(' || tempBuffer.charAt(0) ==')' ){ 
                tempBuffer.insert(1 ,"<a href='mailto:");
            } else {
                tempBuffer.insert(0 ,"<a href='mailto:");
            }   
        }
        else {
            if(tempBuffer.charAt(0) == ' ' || tempBuffer.charAt(0) == ',' || tempBuffer.charAt(0) == '(' || tempBuffer.charAt(0) ==')' ){ 
                tempBuffer.insert(1 ,"<a href='");
            } else {
                tempBuffer.insert(0 ,"<a href='");
            }
        }
        tempBuffer.append("'>");
        tempBuffer.append(email);
        tempBuffer.append("</a>");
    }

    private static class FoundAnchor {
        String href = null;
        String value = null;
        // The character position after any found href from which to continue
        // looking in the string
        int nextCharacterPosition = 0;
    }

}
