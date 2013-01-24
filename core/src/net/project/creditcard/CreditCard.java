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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.creditcard;

import java.util.Date;
import java.util.GregorianCalendar;

import net.project.base.property.PropertyProvider;
import net.project.util.Validator;

/**
 * Class encapsulating a credit card and the operations that you might perform
 * on one.
 *
 * Note that the Project.Net application does *NOT* store credit card numbers in
 * the database!!!  We only collect the information to do credit card
 * processing.  We have opted to do this to help prevent credit card theft.  By
 * not storing credit cards, there are fewer opportunities for them to be
 * stolen.
 *
 * To charge an amount to a credit card or to give credit back to a card, use
 * the {@link net.project.creditcard.verisign.VerisignCreditCardProcessor} interface
 * instead.
 *
 * @author Matthew Flower
 * @since Version 8.0
 */
public class CreditCard {
    /**
     * If we are displaying a credit card in a protected format (for example,
     * XXXX-XXXX-XXXX-1111),  this is the character that we will use to mask
     * numbers that we aren't displaying.
     */
//    public static String CREDIT_CARD_MASK_CHAR = PropertyProvider.get("prm.global.creditcard.maskchar");

    /** The raw credit card number that the user has entered at the command line. */
    private String creditCardNumber;
    /**
     * The date on which the card expires.  Generally, we are only going to use
     * the month and year from this date.
     */
    private Date expirationDate;
    /**
     * This is the street address that the user claims is linked to this credit
     * card account.  Verifying this is dependent on the implementation of
     * {@link net.project.creditcard.ICreditCardProcessor}.  The
     * Project.Net implementation does check the address.
     */
    private String streetAddress;
    /*
    * This is the city that the user claims is linked to this credit card
    * account.
    */
    private String city;
    /**
    * This is the state that the user claims is linked to this credit card
    * account.
    */
    private String state;
    /**
     * This is the zip code that the user claims is linked to this credit card
     * account.  Verifying this is dependent on the implementation of
     * {@link net.project.creditcard.ICreditCardProcessor}.  The
     * Project.net implementation does check the address.
     */
    private String zip;
    /**
     * The name that appears on the card.  This is used for reporting purposes
     * primarily.
     */
    private String name;
    /**
     * This is the country code that the user selected when they were filling in
     * credit card information.  Value values are those that can be returned by
     * the {@link net.project.resource.ProfileCodes#getCountryCodeOptionList()}
     * method.
     */
    private String countryCode;
    /**
     * The type of credit card this is.  This isn't necessary for charging the
     * card, it is used primarily for display purposes.
     */
    private CreditCardType type;

    /**
     * Get the raw credit card number that the user entered.  This value may
     * contain punctuation such as digits or dashes.
     *
     * @see #getCreditCardNumberWithoutPunctuation
     * @return a <code>String</code> value containing the raw credit card number
     * that the user entered.
     */
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    /**
     * Get the credit card number in a format that hides most of the credit card
     * number, but exposes enough to make the card recognizable.  Most of the
     * time, this involves hiding a card number such as 4111-1111-1111-1111 to
     * be shown as XXXX-XXXX-XXXX-1111.
     *
     * @return a <code>String</code> value which contains portions of the credit
     * card number but retains the format of the credit card.
     */
    public String getProtectedCreditCardNumber() {
        return CreditCard.format(getCreditCardNumberWithoutPunctuation(), type, true);
    }

    /**
     * Set the credit card number for this credit card.  This will be a string
     * of numbers which may or may not be separated by punctuation such as
     * dashes or spaces.
     *
     * @param creditCardNumber a <code>String</code> value described above which
     * contains a credit card number.
     */
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    /**
     * Get the credit card number that the user entered without any punctuation
     * such as dashes or spaces.
     *
     * For example, for the fictional credit card number 4111-1111-1111-1111,
     * this method would return 4111111111111111.
     *
     * @return a <code>String</code> containing the digits of the credit card
     * number without any added punctuation.
     */
    public String getCreditCardNumberWithoutPunctuation() {
        StringBuffer ccNoPunc = new StringBuffer();

        for (int i = 0; i < creditCardNumber.length(); i++) {
            char currentChar = creditCardNumber.charAt(i);
            if (Character.isDigit(currentChar)) {
                ccNoPunc.append(currentChar);
            }
        }

        return ccNoPunc.toString();
    }

    /**
     * Determine if our validator thinks that this is a valid credit card
     * number.  This method does not verify the credit card with the credit
     * card processor, it only does a local check.
     *
     * @see net.project.util.Validator#isCreditCard
     * @return a <code>boolean</code> value indicating if this is a valid credit
     * card number.
     */
    public boolean isValidNumber() {
        return Validator.isCreditCard(getCreditCardNumberWithoutPunctuation());
    }

    /**
     * Returns whether the expiration date provided is later than the current
     * date.
     *
     * @return a <code>boolean</code> value indicating whether the expiration
     * date of the credit card is in the future.
     */
    public boolean isValidExpirationDate() {
        return expirationDate.after(new GregorianCalendar().getTime());
    }

    /**
     * Which type of credit card (e.g. visa, mastercard, or american express)
     * that the user has identified this card as.
     *
     * @return a <code>CreditCardType</code> object which identifies which type
     * of credit card this is.
     */
    public CreditCardType getType() {
        return type;
    }

    /**
     * Set which type of credit card this is.  (e.g. Visa, Mastercard, Amex)
     *
     * @param type a <code>CreditCardType</code> object which indicates which
     * type of credit card this is.
     */
    public void setType(CreditCardType type) {
        this.type = type;
    }

    /**
     * Get the expiration date of the credit card.  This will be the last day,
     * hour, minute, second, and millisecond of the expiration month/year
     * combination that the user entered.
     *
     * @return a <code>Date</code> representing the last time at which this
     * credit card can be used.
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Return the expiration date of the credit card as a four digit date in the
     * format MMYY, that is month digit, month digit, year digit, year digit.
     *
     * @return a <code>String</code> value containing the expiration date in
     * MMYY format.
     */
    public String getExpirationDateMMYY() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(expirationDate);
        StringBuffer expDate = new StringBuffer();

        //Month is zero based, so increment it.
        int month = cal.get(GregorianCalendar.MONTH);
        month++;

        //Make sure month has a leading zero
        if (month < 10) {
            expDate.append("0").append(month);
        } else {
            expDate.append(month);
        }

        //Make sure year is two digits
        int year = cal.get(GregorianCalendar.YEAR);
        String yearStr = String.valueOf(year);

        if (yearStr.length() == 0) {
            //This shouldn't really happen, but let's handle it anyhow
            expDate.append("00");
        } else if (yearStr.length() == 1) {
            //This shouldn't really happen, but let's handle it anyhow
            expDate.append("0").append(yearStr);
        } else {
            expDate.append(yearStr.substring(yearStr.length()-2, yearStr.length()));
        }

        return expDate.toString();
    }

    /**
     * Set the expiration date of this credit card.
     * @param expirationDate
     */
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Get the street address which is the billing address of this credit card.
     *
     * @return a <code>String</code> value containing the billing street address
     * of the credit card.
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * Set the street address which is the billing address of this credit card.
     *
     * @param streetAddress a <code>String</code> value containing the billing
     * street address of the credit card.
     */
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     * Set the city which belongs to the billing address of this credit card.
     *
     * @return a <code>String</code> which is the billing city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Set the city of the billing address of this credit card.
     *
     * @param city a <code>String</code> containing the billing city of this
     * credit card.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * This is the state of the billing address of the credit card.
     *
     * @return a <code>String</code> containing the two-character abbreviation
     * of the state.
     */
    public String getState() {
        return state;
    }

    /**
     * Set the billing state.
     *
     * @param state a <code>String</code> containing the two-character
     * abbreviation of the state.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Get the zip code of the billing address of the credit card.
     *
     * @return a <code>String</code> value identifying the zip code of the
     * billing address of the credit card.
     */
    public String getZip() {
        return zip;
    }

    /**
     * Set the zip code of the billing address of the credit card.
     *
     * @param zip a <code>String</code> containing the zip code of the credit
     * card.
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     * Get the country code for the billing address of the credit card.
     *
     * @return a <code>String</code> containing the country code of the credit
     * card billing address.
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Set the country code for the billing address of the credit card.
     *
     * @param countryCode a <code>String</code> containing the country code of
     * the credit card.  These are two-letter codes for each country which
     * can be found in the PN_COUNTRY_CODE table in the database.
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Get the full name of the person that the credit card was issued to.
     *
     * @return a <code>String</code> containing the full name of the person that
     * this card was issued to.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the person to which this credit card was issued.
     *
     * @param name a <code>String</code> value containing the full name of the
     * person to which this credit card was issued.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Format the card to be shown to a user.  Generally, this involves adding
     * dashes in the appropriate places so the credit card number doesn't appear
     * as a jumble of numbers.
     *
     * @param creditCardNumber a <code>String</code> object containing the
     * credit card number we are going to format.
     * @param type a <code>CreditCardType</code> object which represents the
     * type of credit card we are formatting.
     * @param protect whether or not some numbers should be masked to protect
     * the identity of the credit card.
     * @return a <code>String</code> which contains the credit card formatted
     * for the user.
     */
    public static String format(String creditCardNumber, CreditCardType type, boolean protect) {
        //Clear out any existing dashes or spaces
        String ccNumberToFormat = creditCardNumber.replaceAll("[ ]|-", "");

        if (protect) {
            StringBuffer protectedNumber = new StringBuffer();
            for (int i = 0; i < creditCardNumber.length()-4; i++) {
                protectedNumber.append("X");
            }
            protectedNumber.append(creditCardNumber.substring(creditCardNumber.length()-4));
            ccNumberToFormat = protectedNumber.toString();
        }

        return type.formatNumberString(ccNumberToFormat);
    }
}
