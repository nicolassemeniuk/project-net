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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.HTMLOptionList;
import net.project.gui.html.IHTMLOption;

/**
 * Provides an HTML Option List of display timezones after filitering out
 * obsolete and duplicate time zones from the list provided by Java.
 * <p>
 * TimeZones are initially based on all TimeZones provided by Java.
 * Java's TimeZone list is based on Olson's TimeZone names.  These are available
 * here: <a href="ftp://elsie.nci.nih.gov/pub/tzdata2002d.tar.gz">ftp://elsie.nci.nih.gov/pub/tzdata2002d.tar.gz</a>.
 * However, in order to provide a less confusing list of time zones, we
 * filter out certain time zones based on their IDs.  The choice of which
 * ones to remove is based on the comments found in the files in the above mentioned
 * resource.  Please read <a href="doc-files/TimeZoneList-Exclusions.html">TimeZoneList Exclusions</a>
 * for details.
 * </p>
 * <p>
 * The timezone list is currently ordered by ID; ID is currently the initial
 * portion of the display list
 * </p>
 * @author Vishwajeet Lohakarey
 * @author Tim Morrow
 * @since Version 7.4
 */
public class TimeZoneList {

    //
    // Static Members
    //

    /**
     * The token to use for presenting the empty timezone display, currently
     * <code>prm.global.timezonelist.defaultoption.message</code>.
     */
    public static final String EMPTY_TIMEZONE_DISPLAY_TOKEN = "prm.global.timezonelist.defaultoption.message";

    /**
     * The property which provides the default timezone (if any), currently
     * <code>prm.global.brand.defaulttimezone</code>.
     * When this property has a value, that timezone is selected by default in the timezone
     * list if no other timezone has already been selected.
     */
    public static final String DEFAULT_TIMEZONE_PROPERTY = "prm.global.brand.defaulttimezone";

    /**
     * Gets the Html option list of timezones.
     * @param defaultCode the default code that is to be selected in the list.
     * if this is null, then we check use the value in the property <code>prm.global.brand.defaulttimezone</code>;
     * if that is empty, an empty option is added.  If it is absent from
     * the list of timezones, it is added to the list and selected.
     * @return the Html options list of available time zones
     * @see HTMLOptionList#makeHtmlOptionList
     */
    public static String getHtmlOptionList(String defaultCode) {

        // If we were passed a selected id, then we'll select that in the
        // list;
        // Otherwise, we look for a default timezone property
        // If none is found, use the EmptyTimeZone as the selected timezone
        DisplayTimeZone selectedTimeZone;
        if (defaultCode == null) {

            String defaultTimeZone = PropertyProvider.get(DEFAULT_TIMEZONE_PROPERTY);
            if (defaultTimeZone == null || defaultTimeZone.trim().length() == 0) {
                // No default timezone
                selectedTimeZone = new EmptyTimeZone();
            } else {
                // We found a default timezone
                selectedTimeZone = new RealDisplayTimeZone(defaultTimeZone);
            }

        } else {
            // We were passed a default timezone, so use it
            selectedTimeZone = new RealDisplayTimeZone(defaultCode);
        }

        // Returns an Option List based on the display timezone list
        // The selectedTimeZone is selected in the list if present
        // If absent, it is added to the top of the list and selected
        // This takes care of adding in the empty option in the case where
        // no timezone has been selected yet
        return HTMLOptionList.makeHtmlOptionList(getDisplayTimeZoneList(), selectedTimeZone, true);
    }

    /**
     * Returns a List of <code>DisplayTimeZone</code>s, filtered and
     * ordered correctly.
     * @return a List where each element is a <code>DisplayTimeZone</code>;
     * the list has been filtered based on our criteria and is orderd
     */
    private static List getDisplayTimeZoneList() {
        List displayList = new ArrayList();

        // Iterate over the available IDs, constructing display time zones
        for (Iterator it = getAvailableIDs().iterator(); it.hasNext(); ) {
            String timeZoneID = (String) it.next();
            displayList.add(new RealDisplayTimeZone(timeZoneID));
        }

        // Sort the display items
        Collections.sort(displayList, new DisplayComparator());

        return displayList;
    }

    /**
     * Gets all the available time zone IDs per the Project.net criteria.
     * @return the time zone IDs list where each element is a <code>String</code>
     */
    private static List getAvailableIDs() {
        String[] timeZoneIDs = java.util.TimeZone.getAvailableIDs();
        List timeZoneList = new ArrayList();

        for (int i = 0; i < timeZoneIDs.length; i++) {
            if (isValidTimeZone(timeZoneIDs[i])) {
                timeZoneList.add(timeZoneIDs[i]);
            }
        }

        return timeZoneList;

    }

    /**
     * Validates a given java time zone ID.
     * @param timeZoneID the timeZoneID to be validated
     * @return true if it is a valid time zone by our criteria; false
     * otherwise
     */
    private static boolean isValidTimeZone(String timeZoneID) {
        boolean isValid = true;

        if (timeZoneID.length() == 3) {
            // Eliminates the deprecated three letter ID timezones
            // See Java timezone class documentation for information on why
            // these are deprecated
            isValid = false;

        } else {
            // Check to see if it is one of the excluded ones
            if (EXCLUDED_TIME_ZONES.contains(timeZoneID)) {
                isValid = false;
            }
        }

        return isValid;
    }

    //
    // Nested top-level classes
    //

    /**
     * Provides a base for DisplayTimeZones.
     */
    private static abstract class DisplayTimeZone implements IHTMLOption {

        /**
         * Formats the time zone ID for display.
         * <p>
         * Replaces underscore characters with spaces.
         * </p>
         * @param timeZoneID the ID to format
         * @return the formatted ID
         */
        static String fixUpID(String timeZoneID) {
            return (timeZoneID == null ? "" : timeZoneID.replace('_', ' '));
        }

        /**
         * Returns the ID of this time zone.
         * @return the id
         */
        protected abstract String getID();

        /**
         * Returns the display of this time zone.
         * @return the display
         */
        protected abstract String getDisplayString();

        /**
         * Returns the offset from GMT in hours, which may be fractional.
         * @return the offset in hours
         */
        protected abstract double getHourOffset();

        //
        // Implementing IHTMLOption
        //

        /**
         * Returns the value for this option which is the time zone id.
         * @return the value
         */
        public String getHtmlOptionValue() {
            return getID();
        }

        /**
         * Returns the display for this option which is the time zone
         * display string.
         * @return the display
         */
        public String getHtmlOptionDisplay() {
            return getDisplayString();
        }


    }
    /**
     * The EmptyTimeZone is used for displaying a default option when
     * no other timezone has been selected.
     * Its ID is the empty string and display string is defined by a token.
     */
    private static class EmptyTimeZone extends DisplayTimeZone {

        /**
         * Returns the empty string.
         * @return the empty string
         */
        protected String getID() {
            return "";
        }

        /**
         * Returns the message for the empty timezone.
         * @return the result of looking up the token defined by {@link TimeZoneList#EMPTY_TIMEZONE_DISPLAY_TOKEN}
         */
        protected String getDisplayString() {
            return PropertyProvider.get(EMPTY_TIMEZONE_DISPLAY_TOKEN);
        }

        protected double getHourOffset() {
            return 0;
        }

    }

    /**
     * Project.net RealDisplayTimeZone.
     * It wraps the the Java time zone.
     * It facilitates getting the UTC offset in hours and a displayname which is
     * modified to include the short names of this timezone with and without daylight savings.
     */
    private static class RealDisplayTimeZone extends DisplayTimeZone {

        /** The java TimeZone. */
        private TimeZone timeZone = null;

        /**
         * Constructs a display timezone for a given ID.
         * @param timeZoneID the ID for which to construct a timezone
         */
        private RealDisplayTimeZone(String timeZoneID) {
            super();
            this.timeZone = java.util.TimeZone.getTimeZone(timeZoneID);
        }

        /**
         * Gets the id of this timezone.
         *	@return id of this timezone
         */
        protected String getID() {
            return this.timeZone.getID();
        }

        /**
         * Gets the display string for this display timezone.
         * This includes the timezone ID, raw offset from GMT and standard & daylight
         * savings short timezone abbreviations.
         * For example: <pre>
         * <code>(GMT) Europe/London GMT/BST</code></pre>
         * If a time zone does not support daylight savings, only one abbreviation
         * is shown.  For example: <pre>
         * <code>(GMT-07:00) America/Phoenix MST</code></pre>
         * @return the display string for this timezone
         */
        protected String getDisplayString() {

            StringBuffer displayString = new StringBuffer();

            // Format the hour offset to a string
            // This looks like 11:30 or -08:00
            String offsetString = formatHourOffset(getHourOffset());

            // Figure out the display of one or two abbreviations
            StringBuffer abbreviationDisplay = new StringBuffer();
            if (this.timeZone.useDaylightTime()) {
                // Since it uses daylight time, then we need two abbreviations
                abbreviationDisplay.append(this.timeZone.getDisplayName(false, java.util.TimeZone.SHORT))
                        .append("/").append(this.timeZone.getDisplayName(true, java.util.TimeZone.SHORT));
            } else {
                abbreviationDisplay.append(this.timeZone.getDisplayName(false, java.util.TimeZone.SHORT));
            }


            // Format the final display string
            displayString.append("(GMT").append(offsetString).append(") ")
                         .append(fixUpID(getID())).append(" ")
                         .append(abbreviationDisplay);

            return displayString.toString();
        }

        /**
         * Returns the offset from UTC in hours for this timezone.
         * This may be a positive or negative value.  It is based on the raw
         * offset so is not adjusted for any daylight savings.
         * @return offset of this timezone from UTC in hours
         */
        protected double getHourOffset() {
            int offset = this.timeZone.getRawOffset();
            double hourOffset = (double) offset / 3600000; //(60 * 60 * 1000);
            return java.lang.Math.round(hourOffset*100)/100.0; // fix jRockit issue: 28800000.00/3600000.00 returns 7.9999999996 and not 8.0!
        }
       
        /**
         * Formats the hour offset in the form "+hh:mm".
         * Uses a number formatter to format the number parts, but hardcodes
         * the ":" separator to be consistent with Microsoft's style of
         * presenting timezones.  Always includes a "+" or "-" sign.
         * <b>Note:</b> The exception to this is an offset of zero.  It is
         * returned as the empty string.
         * <p>
         * For example, 11.5 is returned as "11:30"; -8 is returned as "-08:00"
         * </p>
         * @param hourOffset the hour offset to format
         * @return the formatted offset
         */
        private static String formatHourOffset(double hourOffset) {

            StringBuffer formattedHourOffset = new StringBuffer();

            if (hourOffset != 0) {

                // Cast the double to integer; this will truncate the decimal
                // part and return the hour offset only
                int hourPart = (int) hourOffset;

                // Creates the minute part by multiplying fractional part by 60
                // E.g. 11.5 would result in 30; -11.5 would also result in 30
                int minutePart = (int) java.lang.Math.abs((hourOffset - hourPart) * 60);

                // Grab a number formatter for the current user
                // We're going to format the hour and minute separately
                NumberFormat format = NumberFormat.getInstance();

                // Format the hours and minutes
                // We always include a "+" or "-" sign next to the hour part
                // We always omit any possible sign from the minute part
                formattedHourOffset.append(format.formatNumber(hourPart, "+00;-00"))
                        .append(":")
                        .append(format.formatNumber(minutePart, "00;00"));

            }

            return formattedHourOffset.toString();
        }
    }

    /**
     * Provides a comparison between two <code>DisplayTimeZone</code>
     * by offset then by id.
     * <p>
     * A time zone with a lesser offset is determined to be less than another.
     * For two time zones with the same offset, the comparison is based on ID.
     * </p>
     */
    private static class DisplayComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            DisplayTimeZone tz1 = (DisplayTimeZone) o1;
            DisplayTimeZone tz2 = (DisplayTimeZone) o2;

            // Compare by offset
            if (tz1.getHourOffset() < tz2.getHourOffset()) {
                return -1;
            } else if (tz1.getHourOffset() > tz2.getHourOffset()) {
                return 1;
            } else {
                // When we have matching offsets, then compare by ID
                return (tz1.getID().compareTo(tz2.getID()));
            }

        }

    }

    /**
     * Defines the list of time zones which will be exlcuded from our
     * presentation.
     * See the class description {@link TimeZoneList} for details on why
     * things are in this list.
     */
    private static final java.util.Collection EXCLUDED_TIME_ZONES = new HashSet(Arrays.asList(
            new String[] {
                // SystemV
                "SystemV/AST4ADT",
                "SystemV/EST5EDT",
                "SystemV/CST6CDT",
                "SystemV/MST7MDT",
                "SystemV/PST8PDT",
                "SystemV/YST9YDT",
                "SystemV/AST4",
                "SystemV/EST5",
                "SystemV/CST6",
                "SystemV/MST7",
                "SystemV/PST8",
                "SystemV/YST9",
                "SystemV/HST10",

                // Renamed
                "America/Atka",
                "America/Ensenada",
                "America/Fort_Wayne",
                "America/Knox_IN",
                "America/Porto_Acre",
                "America/Rosario",
                "America/Virgin",
                "Asia/Ashkhabad",
                "Asia/Chungking",
                "Asia/Dacca",
                "Asia/Macao",
                "Asia/Ujung_Pandang",
                "Asia/Tel_Aviv",
                "Asia/Thimbu",
                "Asia/Ulan_Bator",
                "Australia/ACT",
                "Australia/Canberra",
                "Australia/LHI",
                "Australia/NSW",
                "Australia/North",
                "Australia/Queensland",
                "Australia/South",
                "Australia/Tasmania",
                "Australia/Victoria",
                "Australia/West",
                "Australia/Yancowinna",
                "Brazil/Acre",
                "Brazil/DeNoronha",
                "Brazil/East",
                "Brazil/West",
                "Canada/Atlantic",
                "Canada/Central",
                "Canada/East-Saskatchewan",
                "Canada/Eastern",
                "Canada/Mountain",
                "Canada/Newfoundland",
                "Canada/Pacific",
                "Canada/Saskatchewan",
                "Canada/Yukon",
                "Chile/Continental",
                "Chile/EasterIsland",
                "Cuba",
                "Egypt",
                "Eire",
                "Europe/Tiraspol",
                "GB",
                "GB-Eire",
                "GMT+0",
                "GMT-0",
                "GMT0",
                "Greenwich",
                "Hongkong",
                "Iceland",
                "Iran",
                "Israel",
                "Jamaica",
                "Japan",
                "Kwajalein",
                "Libya",
                "Mexico/BajaNorte",
                "Mexico/BajaSur",
                "Mexico/General",
                "Navajo",
                "NZ",
                "NZ-CHAT",
                "Pacific/Samoa",
                "Poland",
                "Portugal",
                "PRC",
                "ROC",
                "ROK",
                "Singapore",
                "Turkey",
                "UCT",

                // 01/09/2003 - Tim
                // Removing from the exclusion list these US timezones
                // These are available as alternate names, however we're
                // including them to make it easier for US users to find
                // their timezones
                /*
                "US/Alaska",
                "US/Aleutian",
                "US/Arizona",
                "US/Central",
                "US/East-Indiana",
                "US/Eastern",
                "US/Hawaii",
                "US/Indiana-Starke",
                "US/Michigan",
                "US/Mountain",
                "US/Pacific",
                "US/Samoa",
                */

                "UTC",
                "Universal",
                "W-SU",
                "Zulu",

                // Pacific-New
                "US/Pacific-New",

                // Etc Renamed
                "GMT",
                "Etc/Universal",
                "Etc/Zulu",
                "Etc/Greenwich",
                "Etc/GMT-0",
                "Etc/GMT+0",
                "Etc/GMT0",

                // Etc Obsolete
                "Etc/GMT-14",
                "Etc/GMT-13",
                "Etc/GMT-12",
                "Etc/GMT-11",
                "Etc/GMT-10",
                "Etc/GMT-9",
                "Etc/GMT-8",
                "Etc/GMT-7",
                "Etc/GMT-6",
                "Etc/GMT-5",
                "Etc/GMT-4",
                "Etc/GMT-3",
                "Etc/GMT-2",
                "Etc/GMT-1",
                "Etc/GMT+1",
                "Etc/GMT+2",
                "Etc/GMT+3",
                "Etc/GMT+4",
                "Etc/GMT+5",
                "Etc/GMT+6",
                "Etc/GMT+7",
                "Etc/GMT+8",
                "Etc/GMT+9",
                "Etc/GMT+10",
                "Etc/GMT+11",
                "Etc/GMT+12",

                // North America Renamed
                "EST5EDT",
                "CST6CDT",
                "MST7MDT",
                "PST8PDT",
                "EST",
                "MST",
                "HST",

                // Asia obsolete
                "Asia/Riyadh87",
                "Asia/Riyadh88",
                "Asia/Riyadh89",
                "Mideast/Riyadh87",
                "Mideast/Riyadh88",
                "Mideast/Riyadh89"

            }
    ));

}
