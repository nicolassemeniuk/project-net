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
|   $Revision: 18995 $
|       $Date: 2009-03-05 08:36:26 -0200 (jue, 05 mar 2009) $
|     $Author: avinash $
|
+----------------------------------------------------------------------*/
package net.project.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

import net.project.security.SessionManager;
import net.project.security.User;

import org.apache.log4j.Logger;



/**
 * NumberFormat encapsulates number parsing and formatting methods based on
 * the current user's locale.
 */
public class NumberFormat implements java.io.Serializable {

    /** The pattern representation of a currency sign, irrespective of Locale */
    private static final char PATTERN_CURRENCY_SYMBOL = '\u00A4';

    /** The current user. */
    private User user = null;

    /**
     * Return a NumberFormat for the current user
     * @return number format initialized for current user's locale.
     */
    public static NumberFormat getInstance() {
        User user = SessionManager.getUser();
        return new NumberFormat(user);
    }

    /**
     * Create new NumberFormat for the specified user's locale.
     * @param user the user for whom to create a number format;
     * if null or empty, system default locale is used
     */
    public NumberFormat(User user) {
        this.user = user;
    }

    /**
     * Return a number formatter for the current user.
     * @return the formatter; uses the user's locale or the default locale
     * if no user was specified
     */
    private DecimalFormat getNumberFormat() {

        java.text.NumberFormat nf = null;

        // Note : We _MUST_ cast the formatters to DecimalFormat : we require
        // access to methods specified in that class.  This is a documented
        // mechanism for getting the number formatters.  Java documentation states
        // that DecimalFormat will be returned for all but the most esoteric Locales.

        if (this.user == null || this.user.getLocale() == null) {
            nf = java.text.NumberFormat.getNumberInstance();

        } else {
            nf = java.text.NumberFormat.getNumberInstance(this.user.getLocale());

        }

        if (!(nf instanceof DecimalFormat)) {
        	Logger.getLogger(NumberFormat.class).error("NumberFormat() found an unsupported number formatter for user's locale: " + nf.getClass().getName());
            throw new ClassCastException("Unsupported number formatter for user's locale.");

        }

        return (DecimalFormat)nf;
    }

    /**
     * Return a number formatter for the current user.
     * @return the formatter; uses the user's locale or the default locale
     * if no user was specified
     */
    private DecimalFormat getPercentFormat() {

        java.text.NumberFormat nf = null;

        // Note : We _MUST_ cast the formatters to DecimalFormat : we require
        // access to methods specified in that class.  This is a documented
        // mechanism for getting the number formatters.  Java documentation states
        // that DecimalFormat will be returned for all but the most esoteric Locales.

        if (this.user == null || this.user.getLocale() == null) {
            nf = java.text.NumberFormat.getPercentInstance();

        } else {
            nf = java.text.NumberFormat.getPercentInstance(this.user.getLocale());

        }

        if (!(nf instanceof DecimalFormat)) {
        	Logger.getLogger(NumberFormat.class).error("NumberFormat() found an unsupported number formatter for user's locale: " + nf.getClass().getName());
            throw new ClassCastException("Unsupported number formatter for user's locale.");

        }

        return (DecimalFormat)nf;
    }

    /**
     * Return a currency formatter for the current user.
     * @return the formatter; uses the user's locale or the default locale
     * if no user was specified
     */
    private DecimalFormat getCurrencyFormat() {

        java.text.NumberFormat nf = null;

        // Note : We _MUST_ cast the formatters to DecimalFormat : we require
        // access to methods specified in that class.  This is a documented
        // mechanism for getting the number formatters.  Java documentation states
        // that DecimalFormat will be returned for all but the most esoteric Locales.

        if (user == null || user.getLocale() == null) {
            // User has no locale; we must use the system default
            nf = java.text.NumberFormat.getCurrencyInstance();

        } else {
            // We prefer to use the user's locale
            nf = java.text.NumberFormat.getCurrencyInstance(user.getLocale());

        }

        if (!(nf instanceof DecimalFormat)) {
        	Logger.getLogger(NumberFormat.class).error("NumberFormat() found an unsupported currency formatter for user's locale." + nf.getClass().getName());
            throw new ClassCastException("Unsupported currency formatter for user's locale.");
        }

        return (DecimalFormat)nf;
    }

    /**
     * Parse specified text as a currency value, based on user's selected pattern.
     * @param text the currency value string
     * @return the currency value as a number.
     * @throws ParseException if there is a problem parsing the text
     * @see #parseCurrency(String, ParsePosition)
     */
    public Number parseCurrency(String text) throws ParseException {
        return parseCurrency(text, new ParsePosition(0));
    }

    /**
     * Parse text assuming it is a currency value. Based on the user's selected pattern.
     * <b>Note:</b> This method will handle case where a curency symbol is specified
     * in the pattern (either prefixed or suffixed) but is not specified in the text.
     * The currency symbol must be correct for the current locale.
     * @param text the currency value string
     * @param parsePosition the position in text to begin parsing
     * @return the currency value as a number.
     * @throws ParseException if there is a problem parsing the text
     * @see java.text.NumberFormat#parse
     */
    public Number parseCurrency(String text, ParsePosition parsePosition) throws ParseException {
        Number number = null;

        DecimalFormat currencyFormatter = getCurrencyFormat();

        if ((currencyFormatter.toLocalizedPattern().indexOf(PATTERN_CURRENCY_SYMBOL) > -1) &&
            (text.indexOf(currencyFormatter.getDecimalFormatSymbols().getCurrencySymbol()) > -1)) {
            number = currencyFormatter.parse(text, parsePosition);
        } else {
            //There isn't a currency symbol, just treat the currency as a number
            number = getNumberFormat().parse(text, parsePosition);
        }

        // If a number wasn't produced, throw an exception
        if (number == null) {
            throw new ParseException("Invalid currency number '" + text + "'", parsePosition.getErrorIndex());
        }
        return number;
    }

    /**
     * Parses specifies text as a currency value, assuming it is for the
     * specified currency.
     * @param text the currency value string
     * @param currency the currency
     * @param isSymbolAllowed true if the currency symbol is allowed in the text;
     * if this is false and a symbol is entered in the text, and error
     * will occur
     * @return the currency value as a number
     * @throws ParseException if there is a problem parsing the text
     */
    public Number parseCurrency(String text, java.util.Currency currency, boolean isSymbolAllowed)
        throws ParseException {

        DecimalFormat format = null;
        Number number = null;

        if (!isSymbolAllowed) {
            // Pattern does not include currency symbol
            // This really means that we should use a number format
            format = getNumberFormat();

        } else {
            // Include symbol; uses a currency format
            format = getCurrencyFormat();
            // Specifies the currency; affects symbols only
            format.setCurrency(currency);

        }

        // Parse the number
        number = format.parse(text);
        if (number == null) {
            throw new ParseException("Invalid currency number '" + text + "'", 0);
        }

        return number;
    }

    /**
     * Format a currency value for the user's default locale.
     * @param currencyValue the currency value to format
     * @return the currency value as a formatted string
     */
    public String formatCurrency(double currencyValue) {
        return getCurrencyFormat().format(currencyValue);
    }

    /**
     * Format a currency value for the user's default locale using the specified
     * currency.
     * This method ensures that the minimum number of decimal places displayed
     * is at least the default fractional value for the currency.
     * <b>Note:</b> This method does not include the currency symbol in the
     * formatted value.
     * @param currencyValue the number value to format
     * @param currency the currency for which to format the value.
     * @param isSymbolIncluded true if the currency symbol should be included
     * in the formatted result; false otherwise
     * @return the number value as a formatted string
     */
    public String formatCurrency(double currencyValue, java.util.Currency currency, boolean isSymbolIncluded) {
        int fractionDigits = -1;

        // Determine the default number of fraction digits for the currency
        // Apparently some "pseudo-currencies" return -1; those aren't likely
        // to be available in our application, but we'll check for it to
        // avoid any formatting problems
        fractionDigits = currency.getDefaultFractionDigits();
        if (fractionDigits == -1) {
            fractionDigits = 0;
        }

        return formatCurrency(currencyValue, currency, isSymbolIncluded, fractionDigits);
    }

    /**
     * Format a currency value for the user's default locale using the specified
     * currency.
     * This method ensures that the minimum number of decimal places displayed
     * is at least the default fractional value for the currency.
     * <b>Note:</b> This method does not include the currency symbol in the
     * formatted value.
     * @param currencyValue the number value to format
     * @param currency the currency for which to format the value.
     * @param isSymbolIncluded true if the currency symbol should be included
     * in the formatted result; false otherwise
     * @param fractionDigits a <code>int</code> value indicating the number of
     * decimal places after the decimal that should be shown.
     * @return the number value as a formatted string
     */
    public String formatCurrency(double currencyValue, java.util.Currency currency, boolean isSymbolIncluded, int fractionDigits) {
        DecimalFormat format = null;

        if (!isSymbolIncluded) {
            // We need to eliminate the currency symbol for the pattern
            // This really means that we should use a number format
            format = getNumberFormat();

        } else {
            // Include symbol; uses a currency format
            format = getCurrencyFormat();
            // Specifies the currency; affects symbols only
            format.setCurrency(currency);

        }

        // Now set the minimum fraction digits
        format.setMinimumFractionDigits(fractionDigits);

        return format.format(currencyValue);
    }


    /**
     * Format a currency using the specified pattern.
     * This should only be used when a precise format is required.
     * @param currencyValue the currency value to format
     * @param pattern the pattern to apply
     * @return the currency value as a formatted string
     */
    public String formatCurrency(double currencyValue, String pattern) {
        DecimalFormat currencyFormatter = getCurrencyFormat();
        currencyFormatter.applyPattern(pattern);
        return currencyFormatter.format(currencyValue);
    }

    /**
     * Parse specified text as a number starting at the beginning of text.
     * @param text the number as text
     * @return the number
     * @throws ParseException if there is a problem parsing.
     * @see java.text.NumberFormat#parse
     */
    public Number parsePercent(String text) throws ParseException {
        return parsePercent(text, new ParsePosition(0));
    }

    /**
     * Parse specified text as a number staring at a specific position.
     * @param text the number as text
     * @param parsePosition the position in text to begin parsing
     * @return the number
     * @throws ParseException if there is a problem parsing.
     * @see java.text.NumberFormat#parse
     */
    public Number parsePercent(String text, ParsePosition parsePosition) throws ParseException {
        DecimalFormat numberFormatter = getPercentFormat();
        Number number = numberFormatter.parse(text, parsePosition);
        if (number == null) {
            throw new ParseException("Invalid Percent'" + text + "'", parsePosition.getErrorIndex());
        }
        return number;
    }

    /**
     * Parse specified text as an integer staring.
     * @param text the integer as text
     * @return the number
     * @throws ParseException if there is a problem parsing.
     */
    public Number parseInteger(String text) throws ParseException {
        java.text.NumberFormat nf = null;
        Number number = null;
        if (this.user == null || this.user.getLocale() == null) {
        	nf = java.text.NumberFormat.getIntegerInstance(Locale.getDefault());
       } else {
    	   nf = java.text.NumberFormat.getIntegerInstance(this.user.getLocale());
       }
        nf.setParseIntegerOnly(true);
        ParsePosition pos = new ParsePosition(0);
        number=nf.parse(text, pos);
        Integer objInteger=null;
        if (pos.getErrorIndex() == -1 && pos.getIndex() == text.length()) {
            if (number.doubleValue() >= Integer.MIN_VALUE &&
            		number.doubleValue() <= Integer.MAX_VALUE) {
            	objInteger = new Integer(number.intValue());
            }
        }
        if(objInteger==null)
        	throw new ParseException("prm.global.integerrequired.validation.message",pos.getErrorIndex());
        return objInteger;
    }

    /**
     * Parse specified text as a number starting at the beginning of text.
     * @param text the number as text
     * @return the number
     * @throws ParseException if there is a problem parsing.
     * @see java.text.NumberFormat#parse
     */
    public Number parseNumber(String text) throws ParseException {
        return parseNumber(text, new ParsePosition(0));
    }

    /**
     * Parse specified text as a number staring at a specific position.
     * @param text the number as text
     * @param parsePosition the position in text to begin parsing
     * @return the number
     * @throws ParseException if there is a problem parsing.
     * @see java.text.NumberFormat#parse
     */
    public Number parseNumber(String text, ParsePosition parsePosition) throws ParseException {
        DecimalFormat numberFormatter = getNumberFormat();
        Number number = numberFormatter.parse(text.replace('.',numberFormatter.getDecimalFormatSymbols().getDecimalSeparator()), parsePosition);
        if (number == null) {
            throw new ParseException("Invalid number '" + text + "'", parsePosition.getErrorIndex());
        }
        //sjmittal: disable strict parsing. This was not there at start and no reason is indicated here
        //as why strict parsing was introduced.
        // strict parsing, i.e. we should not parse "12abc" as 12, the default parsing returns 12 and no errors
//        if (parsePosition.getIndex() < text.length()) {
//            throw new ParseException("Invalid number '" + text + "'", parsePosition.getIndex());
//        }
        return number;
    }

    /**
     * Format a number for the user's default locale.
     * @param numberValue the number value to format
     * @return the number value as a formatted string
     */
    public String formatPercent(double numberValue) {
        return getPercentFormat().format(numberValue);
    }

    /**
     * Format a number for the user's default locale.
     * @param numberValue the number value to format
     * @param minimumFractionDigits the minimum number of digits that appear
     * after the decimal.
     * @param maximumFractionDigits the maximum number of digits that appear
     * after the decimal.
     * @return the number value as a formatted string
     */
    public String formatPercent(double numberValue, int minimumFractionDigits, int maximumFractionDigits) {
        DecimalFormat percentFormat = getPercentFormat();
        percentFormat.setMinimumFractionDigits(minimumFractionDigits);
        percentFormat.setMaximumFractionDigits(maximumFractionDigits);
        
        return percentFormat.format(numberValue);
    }

    /**
     * Format a number for the user's default locale.
     * @param numberValue the number value to format
     * @param maximumFractionDigits the maximum number of digits that appear
     * after the decimal.
     * @return the number value as a formatted string
     */
    public String formatPercent(double numberValue, int maximumFractionDigits) {
        DecimalFormat percentFormat = getPercentFormat();
        percentFormat.setMaximumFractionDigits(maximumFractionDigits);

        return percentFormat.format(numberValue);
    }

    /**
     * Format a number using the specified pattern.
     * This should only be used when a precise format is required.
     * @param numberValue the number value to format
     * @param pattern the pattern to apply
     * @return the number value as a formatted string
     */
    public String formatPercent(double numberValue, String pattern) {
        DecimalFormat numberFormatter = getNumberFormat();
        numberFormatter.applyPattern(pattern);
        return numberFormatter.format(numberValue);
    }

    /**
     * Format a number for the user's default locale.
     * @param numberValue the number value to format
     * @return the number value as a formatted string
     */
    public String formatNumber(double numberValue) {
        return getNumberFormat().format(numberValue);
    }

    /**
     * Format a number for the user's default locale.
     * @param numberValue the number value to format
     * @param minimumDigits a <code>int</code> value containing the minimum
     * number of decimal digits that we want the number to contain.
     * @param maximumDigits an <code>int</code> value containing the minimum
     * number of decimal digits that we want the resulting number to contain.
     * @return the number value as a formatted string
     */
    public String formatNumber(double numberValue, int minimumDigits, int maximumDigits) {
        //The interface doesn't specify whether the number formatter is cached,
        //and we don't want to interfere with a global one.
        DecimalFormat numberFormatter = (DecimalFormat)getNumberFormat().clone();
        numberFormatter.setMinimumFractionDigits(minimumDigits);
        numberFormatter.setMaximumFractionDigits(maximumDigits);

        return numberFormatter.format(numberValue);
    }

    /**
     * Format a number using the specified pattern.
     * This should only be used when a precise format is required.
     * @param numberValue the number value to format
     * @param pattern the pattern to apply
     * @return the number value as a formatted string
     */
    public String formatNumber(double numberValue, String pattern) {
        DecimalFormat numberFormatter = getNumberFormat();
        numberFormatter.applyPattern(pattern);
        return numberFormatter.format(numberValue);
    }

    /**
     * Format a number for the user's default locale.
     * @param numberValue the number value to format
     * @return the number value as a formatted string
     */
    public String formatNumber(long numberValue) {
        return getNumberFormat().format(numberValue);
    }

    /**
     * Format a number using the specified pattern.
     * This should only be used when a precise format is required.
     * @param numberValue the number value to format
     * @param pattern the pattern to apply
     * @return the number value as a formatted string
     */
    public String formatNumber(long numberValue, String pattern) {
        DecimalFormat numberFormatter = getNumberFormat();
        numberFormatter.applyPattern(pattern);
        return numberFormatter.format(numberValue);
    }

    /**
     * Create an example currency value for the current locale.
     * Includes the currency symbol.
     * @return the formatted currency example
     * @see #getCurrencyExample(java.util.Currency, boolean)
     */
    public String getCurrencyExample() {
        DecimalFormat format = getCurrencyFormat();
        return format.format(getExampleNumber(format).doubleValue());
    }

    /**
     * Create an example currency value for the specified currency.
     * This is constructed as follows:
     * <ul>
     * <li>The number of integer digits is one greater than the grouping size, but
     * no more than the max. allowed and no less than the min allowed.
     * <li>The number of fraction digits is 2 but no more than the max and no less
     * than the min allowed.
     * </ul>
     * For example, for country Japan, default currency formatter: <code>9,999</code>
     * @param currency the currency for which to get the example.
     * @param isSymbolIncluded true if the currency symbol should be included
     * in the formatted value; false otherwise
     * @return the formatted currency example
     */
    public String getCurrencyExample(java.util.Currency currency, boolean isSymbolIncluded) {

        int fractionDigits = -1;

        // Determine the default number of fraction digits for the currency
        // Apparently some "pseudo-currencies" return -1; those aren't likely
        // to be available in our application, but we'll check for it to
        // avoid any formatting problems
        fractionDigits = currency.getDefaultFractionDigits();
        if (fractionDigits == -1) {
            fractionDigits = 0;
        }

        DecimalFormat format = null;
        if (isSymbolIncluded) {
            // Symbol included so we must use the a CurrencyFormat
            format = getCurrencyFormat();
            format.setCurrency(currency);

        } else {
            // No symbol to be included, so we must use a NumberFormat
            format = getNumberFormat();

        }

        format.setMinimumFractionDigits(fractionDigits);
        return format.format(getExampleNumber(format).doubleValue());
    }

    /**
     * Returns an example number for producing the currency example.
     * The <code>format</code> is used for determining the number of digits
     * to include in the example number.
     * <ul>
     * <li>The number of integer digits is one greater than the grouping size, but
     * no more than the max. allowed and no less than the min allowed.
     * <li>The number of fraction digits is 2 but no more than the max and no less
     * than the min allowed.
     * </ul>
     * @param format the format for getting the number of allowable digits
     * @return the example number; currency only a <code>Double</code> is
     * returned
     */
    private Number getExampleNumber(DecimalFormat format) {
        int numIntegerDigits = 0;
        int numFractionDigits = 0;
        double currencyValue = 0;

        // Integer digits are lesser of grouping size + 1 and maximum allowable
        // integer digits
        // Integer digits and greater of that number or minimum integer digits
        numIntegerDigits = format.getGroupingSize() + 1;
        numIntegerDigits = java.lang.Math.min(numIntegerDigits, format.getMaximumIntegerDigits());
        numIntegerDigits = java.lang.Math.max(numIntegerDigits, format.getMinimumIntegerDigits());

        // Fraction digits are lesser of 2 or maximum fraction digits
        // and greater of that number of minimum fraction digits
        numFractionDigits = 2;
        numFractionDigits = java.lang.Math.min(numFractionDigits, format.getMaximumFractionDigits());
        numFractionDigits = java.lang.Math.max(numFractionDigits, format.getMinimumFractionDigits());

        for (int i = (-1 * numFractionDigits); i < numIntegerDigits; i++) {
            currencyValue += 9 * java.lang.Math.pow(10, i);
        }

        return new Double(currencyValue);
    }

    /**
     * Returns a localized pattern based on the specified pattern.
     * This is used to present to a user the expected format of a number
     * based on a pattern constructed manually.
     * @param pattern the non-localized pattern to return as a localized
     * pattern
     * @return the localized pattern
     */
    public String toLocalizedPattern(String pattern) {
        DecimalFormat df = getNumberFormat();
        df.applyPattern(pattern);
        return df.toLocalizedPattern();
    }

    /**
     * Get the character for the current local that separates the whole part of
     * a number from the fractional part of the number.  For example, for the US,
     * this is ".", whereas for Austria this is ",".
     *
     * @return a <code>char</code> value indicating the decimal separator for
     * the current locale.
     */
    public char getDecimalSeparator() {
        return getNumberFormat().getDecimalFormatSymbols().getDecimalSeparator();
    }
    
    /**
     * Format a number without using user locale.
     * @param numberValue
     * @param minimumDigits
     * @param maximumDigits
     * @return Number String
     */
    public static String formatSimpleNumber(double numberValue, int minimumDigits, int maximumDigits) {
        java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance();
        
        DecimalFormat numberFormatter = (DecimalFormat)nf;
        numberFormatter.setMinimumFractionDigits(minimumDigits);
        numberFormatter.setMaximumFractionDigits(maximumDigits);
        
        return numberFormatter.format(numberValue);
    }

}
