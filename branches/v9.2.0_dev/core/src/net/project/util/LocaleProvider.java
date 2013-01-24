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

import java.util.Locale;

import net.project.base.property.PropertyProvider;


/**
 * Project.net Locale provider.
 */
public class LocaleProvider {

    /**
     * Gets the Html option list of java locales.
     * @param defaultLocaleCode the code of the locale that would be shown selected by default in the list
     * @return the Html options list of available locales
     */
    public static String getHtmlOptionList(String defaultLocaleCode) {

        StringBuffer options = new StringBuffer();
        boolean foundSelectedCode = false;

        Locale[] locales = Locale.getAvailableLocales();
        java.util.Arrays.sort(locales, new LocaleComparator());
        for (int i = 0; i < locales.length; i++) {
            if (defaultLocaleCode != null && defaultLocaleCode.equals(locales[i].toString())) {

                options.append("<option SELECTED value=\"" + locales[i].toString() +
                        "\">" + locales[i].getDisplayName(locales[i]) + "</option>");
                foundSelectedCode = true;

            } else {
                options.append("<option value=\"" + locales[i].toString() +
                        "\">" + locales[i].getDisplayName(locales[i]) + "</option>");
            }
        }

        //The default option requesting selection of a locale
        if (!foundSelectedCode) {
            String selectedOption = "<option value=\"\" SELECTED>" + PropertyProvider.get("prm.global.localelist.defaultoption.message") + "</option>\n";
            options = options.insert(0, selectedOption.toCharArray());
        }

        return options.toString();
    }


    /**
     * Gets the Java Locale object for the given localeID string.
     * @param localeID the localeID for which to get the locale
     *                 It is the programmatic name of the entire locale, with the language,
     *                 country and variant separated by underbars e.g. de_DE_EURO
     * @return the Java Locale object for the localeID string or the US Locale, if
     *         it can not understand the localeID.
     * @exception InvalidLocaleException
     *                   throws when there is problem getting a locale for given localeID
     * @see Locale
     */
    public static Locale getLocale(String localeID)
            throws InvalidLocaleException {
        //The localeID string is being parsed with assumption that it will always be of the form 
        // _DE, de_DE or de_DE_EURO
        String language;
        String country = null;
        String variant = null;

        int firstUnderBar = localeID.indexOf("_");
        int secondUnderBar = localeID.indexOf("_", firstUnderBar + 1);

        if (firstUnderBar != -1) {
            language = localeID.substring(0, firstUnderBar);

            if (secondUnderBar == -1) {
                country = localeID.substring(firstUnderBar + 1);
            } else {
                country = localeID.substring((firstUnderBar + 1), secondUnderBar);
                variant = localeID.substring(secondUnderBar);
            }

        } else {
            language = localeID;
        }

        if (language != null) {
            if (country != null) {
                if (variant != null) {

                    return new Locale(language, country, variant);
                }

                return new Locale(language, country);
            }

            return new Locale(language);
        } else {

            throw new InvalidLocaleException("LocaleProvider.getLocale():The locale could not be constructed for given localeID.");
        }

    }

    /**
     * Provides a Comparator that orders locales based on their display name.
     */
    private static class LocaleComparator implements java.util.Comparator {

        /**
         * The locale to use when getting the display name of the locale.
         * May be null.
         */
        final Locale defaultLocale;

        /**
         * Creates a locale comparator using the locale itself to determine the 
         * locale to use when formatting the display.
         */
        LocaleComparator() {
            this.defaultLocale = null;
        }

        /**
         * Constructs a locale order comparator for a given locale.
         * @param locale the locale to use to compare the display names of the locales
         */
        LocaleComparator(Locale locale) {
            defaultLocale = locale;
        }

        /**
         * Compares two <code>Locale</code>s by their display name.
         * <p>
         * If a default locale was specified in the constructor, that is used to
         * compare locale display names (that is, all locales names are compared based on
         * a single locale).  Otherwise locale display names are compared based on
         * the formatted name in its own locale.
         * </p>
         * @param o1 first locale
         * @param o2 second locale
         * @return negative number if first locale is lower than second;
         * zero if both locales are equal; positive number if first locale
         * is higher than second locale
         */
        public int compare(Object o1, Object o2) {
            Locale locale1 = (Locale) o1;
            Locale locale2 = (Locale) o2;

            String localeDisplay1;
            String localeDisplay2;

            if (defaultLocale == null) {
                localeDisplay1 = locale1.getDisplayName(locale1);
                localeDisplay2 = locale2.getDisplayName(locale2);
            } else {
                localeDisplay1 = locale1.getDisplayName(defaultLocale);
                localeDisplay2 = locale2.getDisplayName(defaultLocale);
            }

            // Returns -ve if locale1's name is less than locale2's name
            return localeDisplay1.compareToIgnoreCase(localeDisplay2);
        }

    }

}
