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

import java.math.BigDecimal;
import java.util.Date;

import net.project.xml.XMLUtils;

/**
 * Format and parse dates and times based on the Locale of the User.
 * <p>
 * These methods are used as extension functions in XSL files.
 * <b>DO NOT USE IN JAVA CODE</b> - these methods are reserved for
 * use only in XSL files.
 * </p>
 * @author Deepak Pandey
 * @author Tim Morrow
 * @since Version 7.4
 */
public final class XSLFormat implements java.io.Serializable {

    /**
     * XSL extension function to format the given ISO8601 date to a string
     * containing the date and time based on the current user's settings.
     * This converts an XML date string to a user-formatted date string.
     * <p>
     * For example: <code>"2002-12-20T18:04:25-0800"</code> might become
     * <code>"12/20/02 6:04 PM"</code> if I'm in the PST timezone and use the US locale.
     * </p>
     * @param dateString the ISO8601 date to format, in the form specified
     * by {@link XMLUtils#DEFAULT_ISO_XML_DATE_FORMAT}
     * @return the date time as a string formatted based on user's settings;
     * if the date is unparseable, the passed-in date string is returned, so
     * a value is guaranteed.  Null parameters are returned as the empty string.
     */
    public static String formatISODateTime(String dateString) {

        String formattedDate;

        if (dateString == null || dateString.trim().equals("")) {
            formattedDate = "";

        } else {
            try {
                Date date = XMLUtils.parseISODateTime(dateString);
                formattedDate = DateFormat.getInstance().formatDateTime(date);
            } catch (NullPointerException e) {
                // This is thrown if the DateString is unparseable
                // Return the original String in that case
                formattedDate = dateString;
            }

        }

        return formattedDate;
    }

    /**
     * XSL extension function to format the given ISO8601 date to a string
     * containing the date only based on the current user's settings.
     * This converts an XML date string to a user-formatted date string.
     * <p>
     * For example: <code>"2002-12-20T18:04:25-0800"</code> might become
     * <code>"12/20/02"</code> if I'm in the PST timezone and use the US locale.
     * </p>
     * @param dateString the ISO8601 date to format, in the form specified
     * by {@link XMLUtils#DEFAULT_ISO_XML_DATE_FORMAT}
     * @return the date as a string formatted based on user's settings;
     * if the date is unparseable, the passed-in date string is returned, so
     * a value is guaranteed.  Null parameters are returned as the empty string.
     */
    public static String formatISODate(String dateString) {

        String formattedDate;

        if (dateString == null || dateString.trim().equals("")) {
            formattedDate = "";

        } else {
            try {
                Date date = XMLUtils.parseISODateTime(dateString);
                formattedDate = DateFormat.getInstance().formatDate(date);

            } catch (NullPointerException e) {
                // This is thrown if the DateString is unparseable
                // return the original String in that case
                formattedDate = dateString;
            }

        }

        return formattedDate;
    }

    /**
     * XSL extension function to format the given ISO8601 date to a string
     * containing the time only based on the current user's settings.
     * This converts an XML date string to a user-formatted time string.
     * <p>
     * For example: <code>"2002-12-20T18:04:25-0800"</code> might become
     * <code>"6:04 PM"</code> if I'm in the PST timezone and use the US locale.
     * </p>
     * @param dateString the ISO8601 date to format, in the form specified
     * by {@link XMLUtils#DEFAULT_ISO_XML_DATE_FORMAT}
     * @return the time as a string formatted based on user's settings;
     * if the date is unparseable, the passed-in date string is returned, so
     * a value is guaranteed.  Null parameters are returned as the empty string.
     */
    public static String formatISOTime(String dateString) {

        String formattedDate;

        if (dateString == null || dateString.trim().equals("")) {
            formattedDate = "";

        } else {
            try {
                Date date = XMLUtils.parseISODateTime(dateString);
                formattedDate = DateFormat.getInstance().formatTime(date);
            } catch (NullPointerException e) {
                // This is thrown if the DateString is unparseable
                // Return the original String in that case
                formattedDate = dateString;
            }

        }

        return formattedDate;
    }

    /**
     * XSL extension function to format the given string representation number
     * as a string based on the current user's settings.
     * This converts an XML number string into a user-formatted number string.
     * <p>
     * For example: <code>1000</code> might become <code>1,000</code> if I use
     * the US locale.
     * </p>
     * @param numberValue the number which must be parseable either by
     * {@link Long#parseLong} or {@link Double#parseDouble}; The specified
     * number is assumed to be a double if it contains a period ("<code>.</code>")
     * character anywhere in the string
     * @return the number as a string formatted based on the user's settings;
     * if the number is unparseable then the passed-in number string is returned.
     * Null parameters are returned as the empty string.  Therefore, a return
     * value is guaranteed.
     */
    public static String formatNumber(String numberValue) {
        String formattedNumber;

        if (numberValue == null || numberValue.trim().equals("")) {
            formattedNumber = "";

        } else {

            try {
                if (numberValue.indexOf(".") < 0) {
                    formattedNumber = NumberFormat.getInstance().formatNumber(Long.parseLong(numberValue));
                } else {
                    formattedNumber = NumberFormat.getInstance().formatNumber(Double.parseDouble(numberValue));
                }
            } catch (NumberFormatException nfe) {
                formattedNumber = numberValue;
            }

        }

        return formattedNumber;
    }

    public static String formatNumber(double numberValue) {
        String formattedNumber = null;

        try {
            formattedNumber = NumberFormat.getInstance().formatNumber(numberValue);
        } catch (NumberFormatException nfe) {
            formattedNumber = String.valueOf(numberValue);
        }

        return formattedNumber;
    }

    /**
     * XSL extension function to format the given string representation percent
     * as a string based on the current user's settings.
     * This converts an XML percent string into a user-formatted number string.
     * <p>
     * For example: <code>1000</code> might become <code>1,000</code> if I use
     * the US locale.
     * </p>
     * @param numberValue the number which must be parseable by {@link BigDecimal#BigDecimal(String)}
     * @return the number as a string formatted based on the user's settings;
     * if the number is unparseable then the passed-in number string is returned.
     * Null parameters are returned as the empty string.  Therefore, a return
     * value is guaranteed.
     */
    public static String formatPercent(String numberValue) {
        String formattedNumber;

        if (numberValue == null || numberValue.trim().equals("")) {
            formattedNumber = "";

        } else {

            try {
                BigDecimal bdecimal = new BigDecimal(numberValue);

                // Move it by two decimal places
                bdecimal = bdecimal.movePointLeft(2);
                formattedNumber = NumberFormat.getInstance().formatPercent(bdecimal.doubleValue(), java.lang.Math.min(3, bdecimal.scale()));

            } catch (NumberFormatException nfe) {
                formattedNumber = numberValue;
            }

        }


        return formattedNumber;
    }

    /**
     * XSL extension function to format the given string representation percent
     * as a string based on the current user's settings.
     * This converts an XML percent string into a user-formatted number string.
     * <p>
     * For example: <code>1000</code> might become <code>1,000</code> if I use
     * the US locale.
     * </p>
     * @param numberValue the number which must be parseable by {@link BigDecimal#BigDecimal(String)}
     * @return the number as a string formatted based on the user's settings;
     * if the number is unparseable then the passed-in number string is returned.
     * Null parameters are returned as the empty string.  Therefore, a return
     * value is guaranteed.
     */
    public static String formatPercent(String numberValue, int minScale, int maxScale) {
        String formattedNumber = numberValue;

        if (numberValue == null || numberValue.trim().equals("")) {
            formattedNumber = "";

        } else {
        	/*
			 * The following line is used to resolve the BFD-2872. In case the
			 * decimal separator is ok, that line doesn't do anything.
			 */
        	String targetValue = numberValue.replace(',', NumberFormat
					.getInstance().getDecimalSeparator());
            try {
                BigDecimal bdecimal = new BigDecimal(numberValue);

                // Move it by two decimal places
                bdecimal = bdecimal.movePointLeft(2);
                formattedNumber = NumberFormat.getInstance().formatPercent(
						bdecimal.doubleValue(), minScale, maxScale);

            } catch (NumberFormatException nfe) {
            	formattedNumber = numberValue;
            }
        }
        return formattedNumber;
    }

    /**
     * XSL extension function to format the given string representation percent
     * as a string based on the current user's settings.
     * This converts an XML percent string into a user-formatted number string.
     * <p>
     * For example: <code>0.25</code> might become <code>25%</code> if I use
     * the US locale.
     * </p>
     * @param percentDecimal the number which must be parseable by {@link BigDecimal#BigDecimal(String)};
     * where <code>1.00</code> = <code>100%</code>
     * @return the number as a string formatted based on the user's settings;
     * if the number is unparseable then the passed-in number string is returned.
     * Null parameters are returned as the empty string.  Therefore, a return
     * value is guaranteed.
     */
    public static String formatPercentDecimal(String percentDecimal) {
        String formattedNumber;

        if (percentDecimal == null || percentDecimal.trim().equals("")) {
            formattedNumber = "";
        } else {
            BigDecimal bdecimal = new BigDecimal(percentDecimal);
            try {
                formattedNumber = NumberFormat.getInstance().formatPercent(bdecimal.doubleValue(), 0, 2);
            } catch (NumberFormatException nfe) {
                formattedNumber = percentDecimal;
            }
        }

        return formattedNumber;
    }

    /**
     * XSL extension function to format the given text.
     * No wrapping is performed; line breaks are preserved.
     * @param text the text to format
     * @return the formatted text; HTML tags are inserted to preserve the
     * formatting.  Returns the empty string if text is null.
     */
    public static String formatText(String text) {
        String formattedText;

        if (text == null) {
            formattedText = "";

        } else {
            formattedText = HTMLUtils.formatHtml(text);

        }

        return formattedText;
    }


    /**
     * XSL extension function to format the given text and truncating to the
     * specified number of characters and making hyperlinks clickable.
     * <p>Example: <pre><code>
     *     This is a link\nto somewhere: http://www.project.net/\nPlease click on it
     * </code><pre>
     * becomes <pre><code>
     *     This is a link&lt;br&gt;to somewhere: &lt;a href="http://www.project.net/" target="external_viewer"&gt;http://www.project.net/&lt;/a&gt;&lt;br&gt;Please click on it
     * </code></pre>
     * </p>
     * <p>
     * Note: Any HTML characters are NOT escaped (ampersand, less than, greater than, double-quote).
     * </p>
     * @param text the text to format
     * @param numberOfCharacters the maximum number of characters to return;
     * If <code>0</code> is passed then no truncation occurs
     * @return the formatted or empty string if the specified text was null;
     * hyperlinks are made clickable
     * @deprecated As of 7.6.3; Use {@link #formatTextHyperlink(String, int, int)} instead.
     * That method provides more funcionality than this method.
     */
    public static String formatTextHyperlink(String text, int numberOfCharacters) {

        String formattedText;

        if (text == null) {
            formattedText = "";

        } else {

            // Truncate text and format HTML Breaks
            if (numberOfCharacters > 0 && numberOfCharacters < text.length()) {
                formattedText = HTMLUtils.formatHtmlBreaks(text.substring(0, numberOfCharacters));
            } else {
                formattedText = HTMLUtils.formatHtmlBreaks(text);
            }

            // Now hyperlink it
            // We perform it after formatting for HTML so that hyperlink elements
            // are not escaped and displayed
            // We perform it after truncating so that hyperlinks are not truncated
            formattedText = TextFormatter.makeHyperlinkable(formattedText);

        }

        return formattedText;
    }

    /**
     * XSL extension function to format the given text and truncating to the
     * specified number of characters and making hyperlinks clickable.
     * <p>Example: <pre><code>
     *     This is a link\nto somewhere: http://www.project.net/\nPlease click on it
     * </code><pre>
     * becomes <pre><code>
     *     This is a link&lt;br&gt;to somewhere: &lt;a href="http://www.project.net/" target="external_viewer"&gt;http://www.project.net/&lt;/a&gt;&lt;br&gt;Please click on it
     * </code></pre>
     * </p>
     * <p>
     * Note: Any HTML characters are NOT escaped (ampersand, less than, greater than, double-quote).
     * </p>
     * @param text the text to format
     * @param numberOfCharacters the maximum number of characters to return;
     * If <code>0</code> is passed then no truncation occurs based on the length
     * @param maxLines maximum number of lines to allow; this helps to truncate short
     * text that has many linefeeds (which would ordinarily not be truncated due to its
     * short length).  For example, if maxLines is 5, then all text after
     * (and including) the 5th linefeed is truncated.
     * If <code>0</code> then no truncation occurs based on the number of lines
     * @return the formatted or empty string if the specified text was null;
     * hyperlinks are made clickable
     */
    public static String formatTextHyperlink(String text, int numberOfCharacters, int maxLines) {

        String formattedText;

        if (text == null) {
            formattedText = "";

        } else {

            String textToFormat = text;

            // Truncate text if too long
            if (numberOfCharacters > 0 && numberOfCharacters < textToFormat.length()) {
                textToFormat = textToFormat.substring(0, numberOfCharacters);
            }

            // Truncate number of lines if too many
            if (maxLines > 0) {

                // Find the index of the Nth linefeed
                // where N = max number of lines; this gives us the index
                // of the linefeed to truncate at
                LinefeedHelper helper = findLinefeed(textToFormat, maxLines);

                if (helper.isFound()) {
                    // index is set to the index of the extra linefeed
                    // It gets dropped
                    textToFormat = textToFormat.substring(0, helper.lastIndex());
                }


            }

            // Now format the HTML links
            formattedText = HTMLUtils.formatHtmlBreaks(textToFormat);

            // Now hyperlink it
            // We perform it after formatting for HTML so that hyperlink elements
            // are not escaped and displayed
            // We perform it after truncating so that hyperlinks are not truncated
            formattedText = TextFormatter.makeHyperlinkable(formattedText);

        }

        return formattedText;
    }

    /**
     * Indicates whether the specified text will be truncated based on the maximum number of
     * characters and the maximum number of lines.
     * @param text the text to check
     * @param numberOfCharacters the maximum number of characters to allow
     * @param maxLines the maximum number of lines to allow
     * @return true if the text would be truncated to satisfy the parameters; false if the
     * text would not be truncated
     */
    public static boolean isTruncationRequired(String text, int numberOfCharacters, int maxLines) {

        boolean isTruncationRequired = false;

        if (text != null) {

            boolean isTooLong = false;
            boolean isTooManyLines = false;

            // Check the length of the text
            isTooLong = (numberOfCharacters > 0 && numberOfCharacters < text.length());

            if (!isTooLong) {
                // Check the number of lines

                if (maxLines > 0) {

                    // Find the index of the Nth linefeed
                    // where N = max number of lines; this gives us the index
                    // of the linefeed to truncate at
                    LinefeedHelper helper = findLinefeed(text, maxLines);

                    if (helper.isFound()) {
                        isTooManyLines = true;
                    }

                }

            }

            isTruncationRequired = isTooLong || isTooManyLines;
        }

        return isTruncationRequired;

    }

    /**
     * Finds the index Nth linefeed in text where N is specified by the parameter <code>linefeedCount</code>.
     * @param text the text to look in
     * @param linefeedCount the number of the linefeed who's index to find
     * @return a helper that indicates whether the linefeed was found, and its index
     */
    private static LinefeedHelper findLinefeed(String text, int linefeedCount) {

        LinefeedHelper helper = new LinefeedHelper(linefeedCount);

        // Count line feeds (stopping once we've counted enough)
        int pos = 0;
        while (pos < text.length() && !helper.isFound()) {

            int index;
            if ((index = text.substring(pos).indexOf('\n')) >= 0) {
                // Found a line feed
                helper.flagLinefeed((pos + index));
                // Skip the linefeed to avoid finding it again
                pos = helper.lastIndex() + 1;

            } else {
                // No more line feeds
                break;
            }

        }

        return helper;
    }

    /**
     * Helper class for maintaining count and last index of linefeed.
     */
    private static class LinefeedHelper {

        /** The number of the linefeed we want to find. */
        private final int countToFind;

        private int count = 0;
        private int index = 0;

        LinefeedHelper(int countToFind) {
            this.countToFind = countToFind;
        }

        /**
         * Flags a linefeed at the specified index.
         * This should be the index of the linefeed based from
         * the start of a string.
         * @param index the index
         */
        void flagLinefeed(int index) {
            this.count++;
            this.index = index;
        }

        /**
         * Indicates whether the counted number of linefeeds
         * is equal to the number we wanted to find.
         * @return true if the count equals the count to find; false otherwise
         */
        boolean isFound() {
            return (this.count == this.countToFind);
        }

        int lastIndex() {
            return this.index;
        }

    }
    
    /**
     * Returns the date formatted as a string for an xml file.
     * This is the custom date format we use for xml dates.
     *
     * @param dateString the date to format
     * @param pattern the date pattern 
     * @return formatted date or empty string if the specified date is null
     * 
     */
    public static String customFormatDate(String dateString, String pattern){
        String formattedDate = "";
        if (StringUtils.isNotEmpty(dateString)) {
        	formattedDate = StringUtils.isNotEmpty(pattern) 
			        				? DateFormat.getInstance().formatDate(XMLUtils.parseISODateTime(dateString), pattern)
			        					: formatISODateTime(dateString);
        }
        return formattedDate;
    }
}
