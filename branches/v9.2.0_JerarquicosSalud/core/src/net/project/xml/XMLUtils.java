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

 package net.project.xml;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.project.util.Conversion;
import net.project.util.Version;

import org.jdom.Element;
import org.jdom.output.Format;

/**
 * Provides utility methods for XML, including formatting and parsing methods.
 *
 * @author Tim Morrow
 * @since version 7.2
 */
public class XMLUtils {

    // DO NOT CHANGE THIS VALUE. EVER! Doing so will break xml parsing
    // It should be: yyyy-MM-dd'T'HH:mm:ss
    // Later we could add the timezone numeric offset from GMT; currently there
    // is no convenient Java format to display that
    /**
     * The default xml date format, <code>YYYY-MM-DDThh:mm:ss</code>. This is
     * based on the ISO8601:1988 format. Fractional seconds may be represented
     * with a dot or comma. For example, <code>2002-02-04T15:49:03</code>. See
     * the <a href="http://www.w3.org/TR/NOTE-datetime">W3C NOTE-datetime</a>
     *
     * @deprecated as of release 7.4 use instead {@link #DEFAULT_ISO_XML_DATE_FORMAT}.
     */
    public static final String DEFAULT_XML_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * The default xml date format, <code>YYYY-MM-DDThh:mm:ssTZD</code>. This is
     * based on the ISO8601:1988 format. Fractional seconds may be represented
     * with a dot or comma. For example, <code>2002-02-04T25:49:03.5-08:00</code>. 
     * See the <a href="http://www.w3.org/TR/NOTE-datetime">W3C NOTE-datetime</a>
     * Further note that letter Z formats the date as <b>-0800</b> and not as <b>-08:00</b> 
     * (as per W3C NOTE-datetime). Until Java comes up with exact format match we have 
     * no choice but to use Letter Z for timezone.  
     */
    public static final String DEFAULT_ISO_XML_DATE_FORMAT = DEFAULT_XML_DATE_FORMAT;
    //sjmittal: having timzone attached does not makes sense as we are not using the
    //timezone offset in any way in the UI, and having a dummy offset which is again not 
    //strictly under w3 format is causing some ajax toolkits to not able to parse the date
    //public static final String DEFAULT_ISO_XML_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    /**
     * The iso xml date formatter is cached to prevent a lot of unnecessary
     * creations.
     */
    private static final SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_ISO_XML_DATE_FORMAT, Locale.US);

    
    /**
     * Returns the date formatted as a string for an xml file.
     * This is the standard date format we use for all our xml dates.
     *
     * @param date the date to format
     * @return the formatted date or empty string if the specified date was
     *         null
     * @see #DEFAULT_ISO_XML_DATE_FORMAT
     */
    public static String formatISODateTime(Date date) {
        if (date == null) {
            return "";
        }

        //sjmittal: why time zone is hardcoded? currently fetching the date time in server timezone
        //formatter.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        return formatter.format(date);
    }

    /**
     * Returns the date parsed from the string from an xml file. A null string
     * or one that cannot be parsed will result in a null date.  Uses a strict
     * parser internally so invalid dates (such as February 29th, 2002) will
     * return a null date.
     *
     * @param dateString the date to parse; its format should be as defined by
     * the Java pattern specified by {@link #DEFAULT_ISO_XML_DATE_FORMAT}
     * @return the parsed date or null if there is a problem parsing the date
     * @see #DEFAULT_ISO_XML_DATE_FORMAT
     */
    public static Date parseISODateTime(String dateString) {
        Date date = null;

        // Only try and parse the date if it is not null
        // All null parameter to parse() will throw a NullPointerException
        // rather than a ParseException
        if (dateString != null) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_ISO_XML_DATE_FORMAT);
                // in fact, setLenient(false) is more correct, however it throws an exception on
                // jdk1.5.0_04 (works ok on other jdk, probably due to bug in version 1.5.0_04)
                // and 1.5.0_04 is the default jdk of weblogic 9.2
                formatter.setLenient(true);
                date = formatter.parse(dateString);
            } catch (ParseException e) {
                // return a null date
            }

        }
        return date;
    }


    /**
     * Returns the date formatted as a string for an xml file. This is the
     * standard date format we use for all our xml dates.
     *
     * @param date the date to format
     * @return the formatted date
     * @see #DEFAULT_XML_DATE_FORMAT
     * @deprecated as of release 7.4 use instead {@link #formatISODateTime}.
     */
    public static String formatDateForXML(Date date) {
        return formatISODateTime(date);
    }

    /**
     * Returns the date parsed from the string from an xml file.
     *
     * @param dateString the date to parse
     * @return the parsed date or null if there is a problem parsing the date
     * @see #DEFAULT_XML_DATE_FORMAT
     * @deprecated as of release 7.4 use instead {@link #parseISODateTime}.
     */
    public static Date parseDateFromXML(String dateString) {
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_XML_DATE_FORMAT);
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            // return a null date
        }
        return date;
    }

    /**
     * Outputs the Document as a string, without omitting the XML declaration.
     * The declaration is the line <code>&lt;?xml version="1.0"?&gt;</code>.
     *
     * @param document the JDOM Document to output
     * @return the string representation of the JDOM Document, or null if there
     *         is a problem outputting the document
     */
    public static String outputString(org.jdom.Document document) {
        return XMLUtils.outputString(document, false, false);
    }

    /**
     * Outputs the Document as a string, with no additional whitespace or
     * newlines.
     *
     * @param document the JDOM Document to output
     * @param omitDeclaration whether to omit the XML declaration; true means
     * the declaration will be omitted; false means it will be included
     * @param isPrettyFormat whether to include indents and newlines in output
     * @return the string representation of the JDOM Document, or null if there
     *         is a problem outputting the document
     */
    public static String outputString(org.jdom.Document document, boolean omitDeclaration, boolean isPrettyFormat) {
        String result = null;

        try {
            java.io.StringWriter out = new java.io.StringWriter();

            org.jdom.output.XMLOutputter outputter = new org.jdom.output.XMLOutputter();
            Format format = Format.getRawFormat();
            format.setOmitDeclaration(omitDeclaration);
            outputter.setFormat(format);
            if (isPrettyFormat) {
            	format = Format.getPrettyFormat();
            	format.setLineSeparator(System.getProperty("line.separator"));
            	outputter.setFormat(format);
            }
            outputter.output(document, out);
            result = out.toString();

        } catch (java.io.IOException e) {
            // Highly unlikely with a StringWriter
            // Suck it up, return null

        }

        return result;
    }

    /**
     * Returns a <code>Version</code> element. This should be added to any XML
     * produced by an object; it includes the application version number and the
     * object's own schema version number. For example, calling with
     * <code>getVersionElement(1,2)</code> would return:
     * <pre><code>
     * <Version>
     *     <ApplicationVersion>2.2.20.0</ApplicationVersion>
     *     <SchemaVersion>
     *         <Major>1</Major>
     *         <Minor>2</Minor>
     *     </SchemaVersion>
     * </version>
     * </code></pre>
     *
     * @param majorVersion the major schema version to embed in the element
     * @param minorVersion the minor schema version to embed in the element
     * @return the element
     */
    public static Element getVersionElement(int majorVersion, int minorVersion) {
        Version version = Version.getInstance();
        Element versionElement = new Element("Version");
        versionElement.addContent(new Element("ApplicationVersion").addContent(version.getAppVersion()));
        versionElement.addContent(new Element("SchemaVersion")
            .addContent(new Element("Major").addContent(String.valueOf(majorVersion)))
            .addContent(new Element("Minor").addContent(String.valueOf(minorVersion))));

        return versionElement;
    }

    /**
     * Escapes certain characters that are not permitted in ordinary text in an
     * XML element value. <p> <b>Note: </b> Does NOT escape single quotes.
     * Caution must be used when, escaping text for <i>attribute</i> values.
     * The attribute must be delimited with double quotes.  This is because
     * single quotes cannot be escaped if the string is to be used for HTML
     * output; the XML entity for a single quote (<code>&amp;apos;</code>) is
     * not understood by HTML and would be displayed as shown on the HTML page.
     * This shortcoming is permitted since the most common need for escaping
     * strings are for <i>element</i> values. </p> <p> The following characters
     * are translated into XML entities: <li><code>&lt;</code> becomes
     * <code>&amp;lt;</code> <li><code>&gt;</code> becomes <code>&amp;gt;</code>
     * <li><code>&amp;</code> becomes <code>&amp;amp;</code> <li><code>"</code>
     * becomes <code>&amp;quot;</code> </p> <p> Any character less than
     * <code>\u0020</code> EXCEPT <code>\u0009, \u000a, \u000d</code> will be
     * replaced with <code>\u200b</code> which is Unicode character <code>SPACE,
     * ZERO WIDTH</code>. It is displayed as an "empty box" character, thus
     * giving the same visual appearance as what happens when an unprintable
     * character is displayed. Most characters less than <code>\u0020</code> are
     * not valid in XML documents and will always cause an error.  There are a
     * few more ranges of Unicode characters that may cause an error; however
     * these are not currently excluded. <br> See <a href="http://www.w3.org/TR/REC-xml#charsets">XML
     * 1.0 W3C Recommendation / 2.2. Characters</a> for details on valid XML
     * characters </p>
     *
     * @param source the source string to process; when <code>null</code>, the
     * empty string is returned
     * @return the string with certain characters escaped; or the empty string
     *         if a <code>null</code> source was specified.
     */
    public static String escape(String source) {
        StringBuffer result = new StringBuffer();

        // If source is not null then we process it
        // Otherwise, an empty result string will be returned
        if (source != null) {

            char[] sourceChars = source.toCharArray();
            String entity;
            int i, last;

            // Iterate over all characters, checking each one
            // and transforming it if necessary
            for (i = 0, last = 0; i < sourceChars.length; i++) {

                // Grab next char
                char c = sourceChars[i];
                entity = null;

                if (c == '<') {
                    entity = "&lt;";

                } else if (c == '>') {
                    entity = "&gt;";

                } else if (c == '&') {
                    entity = "&amp;";

                } else if (c == '"') {
                    entity = "&quot;";

                } else {

                    // Check to see if the character is invalid
                    if (isInvalidUnicodeCharacterXML(c)) {
                        // We should always replace it with ONE character
                        // to ensure the length of the string will be the
                        // same (at least visually)
                        // Also, in those cases that the string is displayed
                        // without first transforming it via XSLT, it will
                        // likely be displayed as an "empty box"
                        // It is confusing to see the "empty box" in some places
                        // and not others

                        // Unicode 200b is defined as "SPACE, ZERO WIDTH"
                        // It is displayed as an "empty box"
                        entity = String.valueOf('\u200b');
                    }
                }

                if (entity != null) {
                    // Add all chars starting at last position up to current
                    // position to result
                    // Then add entity
                    // Effectively skips current character
                    result.append(sourceChars, last, i - last);
                    result.append(entity);
                    last = i + 1;
                }

            }

            // If we have any leftover characters, add them
            if (last < sourceChars.length) {
                result.append(sourceChars, last, i - last);
            }

        }

        return result.toString();
    }

    /**
     * Indicates whether the specified character is a valid Unicode character
     * for XML. <p> Any character less than <code>\u0020</code> EXCEPT
     * <code>\u0009, \u000a, \u000d</code> is invalud. </p>
     *
     * @param c the character to check
     * @return true if the character is a valid Unicode character for XML; false
     *         if the character will cause an error when parsing XML
     */
    public static boolean isInvalidUnicodeCharacterXML(char c) {

        boolean isInvalid = false;

        // Character less than Hex 20
        // (exlcuding 09, 0A and 0D)
        // are not valid
        // Note: We have to use \t, \n and \r to prevent the compiler
        // interpreting the characters as tab, linefeed, carriage return
        if (c < '\u0020' && !(c == '\t' || c == '\n' || c == '\r')) {
            isInvalid = true;
        }

        return isInvalid;
    }

    /**
     * Formats the object to XML string format. This method handles certain
     * types of objects specially (such as Boolean) to conform to our XML output
     * formats.  Any other types are returned using their <code>toString</code>
     * method. <p> <li>Boolean - converted to a string integer (1 or 0) by
     * {@link Conversion#booleanToInt} <li>Date - formatted with ISO format by
     * {@link #formatISODateTime} </p>
     *
     * @param value the value to return as a string
     * @return the string format of that value
     * @see net.project.util.Conversion#booleanToInt
     */
    public static String format(Object value) {
        String stringValue;

        if (value == null) {
            stringValue = "";

        } else if (value instanceof Boolean) {
            stringValue = String.valueOf(Conversion.booleanToInt(((Boolean) value).booleanValue()));

        } else if (value instanceof Date) {
            stringValue = formatISODateTime((Date) value);

        } else {
            stringValue = value.toString();

        }

        return removeInvalidUnicodeCharacters(stringValue);
    }

    public static String format(long number) {
        return String.valueOf(number);
    }

    /**
     * Returns the boolean value in XML string format.
     *
     * @param value the boolean value to convert
     * @return string form of the specified boolean value
     * @see #format(Object)
     */
    public static String format(boolean value) {
        return format(new Boolean(value));
    }

    /**
     * Returns the specified XML value as a boolean value.
     *
     * @param value the XML value to convert
     * @return the boolean value for that value
     * @see Conversion#toBool
     */
    public static boolean parseBoolean(String value) {
        return Conversion.toBool(value);
    }

    /**
     * Returns the specified XML value as a Number.
     *
     * @param value the XML value to convert
     * @return the Number for that value; this is actually a BigDecimal.
     * @throws NumberFormatException if the value cannot be parsed as a
     * BigDecimal
     * @see BigDecimal#BigDecimal(String)
     */
    public static Number parseNumber(String value) {
        return new BigDecimal(value);
    }

    /**
     * Removes invalid Unicode characters from the source string. Does not
     * escape any characters to XML entities. XML defines certain Unicode
     * characters as illegal.  They must be removed to avoid transformation
     * errors.  The accepted character range can be found in the <a
     * href="http://www.w3.org/TR/REC-xml#charsets">XML 1.0 Specification,
     * Section 2.2 Characters</a>. This is used especially for JDOM output;  we
     * need to remove those characters that will cause an error, but cannot
     * escape characters here since the JDOM outputter will do that for us.
     *
     * @param source the string to parse
     * @return the string with invalid unicode characters removed
     */
    private static String removeInvalidUnicodeCharacters(String source) {
        StringBuffer result = new StringBuffer();

        // If source is not null then we process it
        // Otherwise, an empty result string will be returned
        if (source != null) {

            char[] sourceChars = source.toCharArray();
            int i, last;

            // Iterate over all characters, checking each one
            for (i = 0, last = 0; i < sourceChars.length; i++) {

                // Grab next char
                char c = sourceChars[i];

                if (XMLUtils.isInvalidUnicodeCharacterXML(c)) {
                    // Add all chars starting at last position up to current
                    // position to result; do not add current invalid
                    // character
                    // then sets last position to be beyond this character
                    result.append(sourceChars, last, i - last);
                    last = i + 1;
                }

            }

            // If we have any leftover characters, add them
            if (last < sourceChars.length) {
                result.append(sourceChars, last, i - last);
            }

        }

        return result.toString();
    }

}
