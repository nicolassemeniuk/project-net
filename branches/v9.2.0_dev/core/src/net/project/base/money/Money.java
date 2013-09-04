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
package net.project.base.money;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import net.project.base.quantity.Percentage;
import net.project.security.User;
import net.project.util.NumberFormat;

import org.jdom.Element;

/**
 * An immutable monetary value.
 * Money has a value and currency.
 */
public class Money {

    //
    // Static members
    //

    /**
     * Represents an empty money value.
     * Calling getValue() or getCurrency() will result in a RuntimeException.
     * Comparison should only be based on object identity.
     */
    public static final Money EMPTY = new EmptyMoney();

    /**
     * Creates a Money object from the specified XML Element.
     * @param element the element that represents the money
     * @return the Money object, or null if the element is not a Money element
     */
    public static Money create(Element element) {
        Money money = null;

        if (element.getName().equals("Money")) {
            money = new Money();
            money.populate(element);
        }

        return money;
    }

    /**
     * Parses a value for the specified user.
     * @param value the value to parse
     * @param currencyCode the currency of the value
     * @param user the current user, used to determine the locale for parsing
     * @return the Money object for the specified value
     * @throws InvalidValueException if the value cannot be parsed
     * @throws InvalidCurrencyException if the currencyCode is not supported
     * @throws NullPointerException if the value or currencyCode is null.
     */
    public static Money parse(String value, String currencyCode, User user)
            throws InvalidValueException, InvalidCurrencyException {

        if (value == null || currencyCode == null) {
            throw new NullPointerException("value and currencyCode are required");
        }

        Number valueNumber = null;
        Currency currency = null;

        // Now check the currency code
        // This throws an exception if the currencyCode is not supported
        currency = getCurrencyInstance(currencyCode);

        try {
            // Get a number formatter for the specified user
            // and parse the value, not allowing symbols
            NumberFormat formatter = new NumberFormat(user);
            valueNumber = formatter.parseCurrency(value, currency, false);

        } catch (ParseException e) {
            throw new InvalidValueException("Error parsing Money value " + value + ": " + e, e);

        }


        // Construct the Money object from the parsed value and currency
        return new Money(valueNumber.toString(), currency);
    }

    /**
     * Convenience method that parses a money value entered using the
     * money taglib.
     * Assumes there are two request parameters available; the first is
     * <code><i>inputFieldName</i>_value</code> and contains the money value;
     * the second is <code><i>inputFieldName</i>_currencyCode</code> and contains
     * the currency code.
     * @param inputFieldName the name of the input field
     * @param user the current user
     * @param request the request from which to get the money value and currency
     * @return the Money object or {@link #EMPTY} if there is no value or currency code
     * found in the request for the specified inputFieldName
     * @throws InvalidValueException if the value cannot be parsed
     * @throws InvalidCurrencyException if the currencyCode is not supported
     */
    public static Money parseFromRequest(String inputFieldName, User user, HttpServletRequest request)
            throws InvalidValueException, InvalidCurrencyException {

        Money parsedMoney = null;

        String valueParameterName = inputFieldName + "_value";
        String currencyCodeParameterName = inputFieldName + "_currencyCode";

        String value = request.getParameter(valueParameterName);
        String currencyCode = request.getParameter(currencyCodeParameterName);

        if ((value == null || value.trim().length() == 0) ||
                currencyCode == null || currencyCode.trim().length() == 0) {

            parsedMoney = EMPTY;

        } else {
            parsedMoney = parse(value, currencyCode, user);

        }

        return parsedMoney;
    }
    
    public static Money parseFromRequestDefaultCurrency(String inputFieldName, User user, HttpServletRequest request)
            throws InvalidValueException, InvalidCurrencyException {
        Money parsedMoney = null;

        String valueParameterName = inputFieldName + "_value";
        String currencyCodeParameterName = "defaultCurrencyCode";

        String value = request.getParameter(valueParameterName);
        String currencyCode = request.getParameter(currencyCodeParameterName);

        if ((value == null || value.trim().length() == 0) ||
                currencyCode == null || currencyCode.trim().length() == 0) {

            parsedMoney = EMPTY;

        } else {
            parsedMoney = parse(value, currencyCode, user);

        }

        return parsedMoney;
    }    
    

    /**
     * Returns the Currency for the specified currency code,
     * throwing an exception if it is invalid.
     * This is a simple helper method that throws an exception if the code
     * is not supported.
     * @param currencyCode the currency code to get the Currency for
     * @return the Currency for the specified currency code
     * @throws InvalidCurrencyException if the currency code is not
     * supported
     * @see java.util.Currency
     */
    private static Currency getCurrencyInstance(String currencyCode) throws InvalidCurrencyException {
        Currency currency = Currency.getInstance(currencyCode);
        if (currency == null) {
            throw new InvalidCurrencyException("Invalid currency code " + currencyCode);
        }
        return currency;
    }

    //
    // Instance members
    //

    /**
     * The value of this money.
     */
    private BigDecimal value = null;

    /**
     * The Currency of the monetary value.
     */
    private Currency currency = null;

    //
    // Constructors
    //

    /**
     * Creates an Money object with value 0 and currency USD.
     */
    public Money() {
        this(new BigDecimal("0"), Currency.getInstance("USD"));
    }

    /**
     * Creates a new Money with the specified value.
     * The value is assumed to be a string value suitable for passing to <code>BigDecimal</code>.
     * The currency defaults to USD.
     * @param value the value for this Money
     * @see java.math.BigDecimal
     * @deprecated As of Gecko Update 4; use {@link Money(String, Currency)} instead.
     * A Currency must always be specified.  This method will be removed in a future release.
     */
    public Money(String value) {
        this(new BigDecimal(value), Currency.getInstance("USD"));
    }

    /**
     * Creates a new Money with the specified value and currency.
     * The value is assumed to be suitable for passing to <code>BigDecimal</code>.
     * @param value the money value
     * @param currency the Currency
     */
    public Money(String value, Currency currency) {    	
        this(new BigDecimal(value), currency);
    }

    /**
     * Creates a new Money with the specified value.
     * The currency defaults to USD.
     * @param value the money value
     * @deprecated As of Gecko Update 4; use {@link Money(BigDecimal, Currency)} instead.
     * A Currency must always be specified.  This method will be removed in a future release.
     */
    public Money(BigDecimal value) {
        this(value, Currency.getInstance("USD"));
    }

    /**
     * Creates a new Money with the specified value and currency.
     * @param value the money value
     * @param currency the Currency
     */
    public Money(BigDecimal value, Currency currency) {
        setValue(value);
        setCurrency(currency);
    }

    /**
     * Creates a new Money with the specified value of the same currency
     * as the specified Money.
     * @param value the money value
     * @param money the money from which to get the currency
     */
    public Money(BigDecimal value, Money money) {
        this(value, money.getCurrency());
    }

    //
    // End Constructors
    //

    /**
     * Indicates whether another Money is equal to this one.
     * Two Money objects are equal if their currency codes are equal
     * and their amounts are equal
     * @param other the other Money to compare
     * @return true if the Money object is equal to this; false otherwise
     */
    public boolean equals(Object other) {
        boolean isEqual = false;

        if (this == other) {
            isEqual = true;
        } else if (other instanceof Money && this.equals((Money) other)) {
            isEqual = true;
        }

        return isEqual;
    }

    /**
     * Indicates whether the specified Money is equal to this one.
     * Two Money objects are equal if their currency's codes are the same
     * and their values are the same (ignoring scale).
     * @param other
     * @return true if the Money object is equal to this; false otherwise
     * @see java.math.BigDecimal#compareTo
     */
    private boolean equals(Money other) {
        boolean isEqual = false;

        if (this.currency.getCurrencyCode().equals(other.currency.getCurrencyCode()) &&
            this.value.compareTo(other.value) == 0) {

            // Currency codes are equal and values are equal (ignoring scale)
            isEqual = true;
        }

        return isEqual;
    }

    /**
     * Returns the hashCode for this Money.
     * @return the hashCode
     */
    public int hashCode() {
        // Hashcode must simply be the same for equal objects
        // It does not have to be different for different objects
        // XOR the Long hashCode with the country code hashcode
        // To produce a reasonable distribution
        return (new Long(this.value.longValue()).hashCode()) ^ this.currency.getCurrencyCode().hashCode();
    }

    /**
     * Sets this Money's value.
     * @param value the value of this money
     */
    private void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Returns this Money's value.
     * @return the value of this money
     */
    public BigDecimal getValue() {
        // We can return the actual value since BigDecimal is immutable
        // Thus this Money continues to be immutable
        return this.value;
    }

    /**
     * Sets this Money's currency.
     * @param currency the currency
     */
    private void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * Returns the currency for this Money.
     * @return the currency
     */
    public Currency getCurrency() {
        // There is only one Currency instance for each currency
        // Thus this Money continues to be immutable
        return this.currency;
    }

    /**
     * Returns a new Money instance calculated from this Money multiplied
     * by the specified percentage.
     * The Currency remains the same.
     * @param percentage the percentage by which to multiply this Money
     * @return the new Money
     */
    public Money multiply(Percentage percentage) {
        // Return new Money based on same currency as this Money
        return new Money(getValue().multiply(percentage.getDecimalValue()), this);
    }

    /**
     * Return a new Money instance calculated from this Money multiplied by
     * the multiplicant.  The currency will not be modified.  This method does
     * not multiply the current instance -- it only returns a new one.
     *
     * @param multiplicant a <code>BigDecimal</code> by which we are going to
     * multiply this Money.
     * @return a <code>Money</code> object which is the result of multiplying
     * this object by the multiplicant.
     */
    public Money multiply(BigDecimal multiplicant) {
        return new Money(getValue().multiply(multiplicant), currency);
    }

    /**
     * Formats the money value for the specified user.
     * The formatted value does not contain the currency symbol.
     * @param user the user for whom to format the money value
     * @return the formatted value
     */
    public String formatValue(User user) {
        String formattedValue = null;

        // Get a number formatter for the user
        // And format the number based on the current currency
        // Choose to not include the symbol in the value
        NumberFormat formatter = new NumberFormat(user);
        formattedValue = formatter.formatCurrency(getValue().doubleValue(), getCurrency(), false);

        return formattedValue;
    }

    /**
     * Format the current money object for display.
     *
     * @param user a <code>User</code> whose currency format and locale we will
     * use to format the money object.
     * @return a <code>String</code> value containing the money object properly
     * formatted.
     */
    public String format(User user) {
        NumberFormat formatter = new NumberFormat(user);
        return formatter.formatCurrency(getValue().doubleValue(), getCurrency(), true);
    }

    /**
     * Format the current money object for display.
     *
     * @param user a <code>User</code> whose currency format and locale we will
     * use to format the money object.
     * @param showFractionalAmounts a <code>boolean</code> value indicating
     * whether fractional amounts of currency should be shown.
     * @return a <code>String</code> value containing the money object properly
     * formatted.
     */
    public String format(User user, boolean showFractionalAmounts) {
        NumberFormat formatter = new NumberFormat(user);

        String formattedCurrency;
        if (showFractionalAmounts) {
            formattedCurrency = formatter.formatCurrency(getValue().doubleValue(), getCurrency(), true);
        } else {
            formattedCurrency = formatter.formatCurrency(getValue().doubleValue(), getCurrency(), true, 0);
        }

        return formattedCurrency;
    }

    /**
     * Returns this Money as an XML Element.
     * @return the element for this money
     */
    public Element getXMLElement() {
        Element rootElement = new Element("Money");
        rootElement.addContent(new Element("Value").addContent(getValue().toString()));
        rootElement.addContent(new Element("Currency").addContent(getCurrency().getCurrencyCode()));
        return rootElement;

    }

    /**
     * Populates this money object from the specified element.
     * @param element the money element
     */
    private void populate(Element element) {
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("Value")) {
                // Value contains a string number suitable for passing
                // to BigDecimal
                setValue(new BigDecimal(childElement.getTextTrim()));
            
            } else if (childElement.getName().equals("Currency")) {
                // Currency contains a string which is a currency code
                setCurrency(Currency.getInstance(childElement.getTextTrim()));

            }
        }
    }

    /**
     * Provides an XML structure of this Money.
     * This structure may be used for presentation purposes.
     * @return the XML structure
     */
    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("Money");
            doc.addElement("Value", new Double(getValue().doubleValue()));
            doc.addElement("Currency", getCurrency().getCurrencyCode());
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

    /**
     * Add one money object to this object to produce an object which is the sum
     * of the two monies.  This object is not modified by the process.
     *
     * @param money a money to get added.
     * @return a <code>Money</code> object
     * @throws InvalidCurrencyException if the money objects don't have the
     * same currencies.
     */
    public Money add(Money money) throws InvalidCurrencyException {
        if (!money.getCurrency().equals(this.getCurrency())) {
            throw new InvalidCurrencyException("Cannot add two money objects " +
                "with different currencies.");
        }

        return new Money(getValue().add(money.getValue()), getCurrency());
    }

    /**
     * Subtract the moneyToSubtract monetary value from this object to produce a
     * new money value.
     *
     * @param moneyToSubtract
     * @return a <code>Money</code> object
     * @throws InvalidCurrencyException if the money objects don't have the
     * same currencies.
     */
    public Money subtract(Money moneyToSubtract) throws InvalidCurrencyException {
        if (!moneyToSubtract.getCurrency().equals(this.getCurrency())) {
            throw new InvalidCurrencyException("Cannot subtract two money objects " +
                "with different currencies.");
        }

        return new Money(getValue().subtract(moneyToSubtract.getValue()), getCurrency());
    }

    /**
     * Return a new money object containing the absolute value of the current
     * money object.
     *
     * @return a <code>Money</code> value which is similar to the current object
     * except that it will always be positive.
     */
    public Money abs() {
        return new Money(getValue().abs(), getCurrency());
    }

    private static class EmptyMoney extends Money {

        public BigDecimal getValue() {
            throw new UnsupportedOperationException("Unsupported Operation");
        }

        public Currency getCurrency() {
            throw new UnsupportedOperationException("Unsupported Operation");
        }


    }

}
