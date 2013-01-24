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
package net.project.util;

import java.text.ParseException;

import net.project.creditcard.CreditCardType;
import net.project.security.SessionManager;
import net.project.security.User;

import org.apache.commons.validator.GenericValidator;

/**
 * Class to determine if a String is of a certain type.  Currently, many of
 * these validations are performed by the Commons-Validator classes of the
 * Jakarta Project.  More information about these validations can be found at
 * the <a href="http://jakarta.apache.org/commons/validator/">Jakarta Commons Validator Project Page</a>.
 *
 * This class hides that implementation to ensure that we can switch to another
 * implementation of validation without affecting our API's.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class Validator {
    /**
     * Checks if the field isn't null and length of the field is greater than
     * zero not including whitespace.
     *
     * @param value The value validation is being performed on.
     * @return a <code>boolean</code> value indicating whether the test value is
     * either blank or null.
     */
    public static boolean isBlankOrNull(String value) {
        return GenericValidator.isBlankOrNull(value);
    }

    /**
     * Checks if the value matches the regular expression.  This method uses
     * <i>Java</i> regular expression syntax rather than Perl 5 syntax.
     * Equivalent to calling <code>value.matches(regexp);</code>
     *
     * @param value The value validation is being performed on.
     * @param regexp The regular expression.
     * @return a <code>boolean</code> value indicating whether the
     * <code>value</code> parameter matches the <code>regexp</code> parameter.
     */
    public static boolean matchRegexp(String value, String regexp) {
        return value.matches(regexp);
    }


    /**
     * Checks if the value can safely be converted to a byte primitive.
     *
     * @param value The value validation is being performed on.
     * @return a <code>boolean</code> value indicating whether this value can
     * safely be converted into a byte primitive.
    */
    public static boolean isByte(String value) {
        return GenericValidator.isByte(value);
    }

    /**
     * Checks if the value can safely be converted to a short primitive.
     *
     * @param value The value validation is being performed on.
     * @return a <code>boolean</code> value indicating whether the
     * <code>value</code> can safety be converted to a short value.
    */
    public static boolean isShort(String value) {
        return GenericValidator.isShort(value);
    }

    /**
     * Checks if the value can safely be converted to a int primitive.
     *
     * @param 	value 		The value validation is being performed on.
     * @return a <code>boolean</code> value indicating whether the
     * <code>value</code> can safety be converted to a int value.
     */
    public static boolean isInt(String value) {
        return GenericValidator.isInt(value);
    }

    /**
     * Checks if the value can safely be converted to a long primitive.
     *
     * @param 	value 		The value validation is being performed on.
     * @return a <code>boolean</code> value indicating whether the
     * <code>value</code> can safety be converted to a long value.
     */
    public static boolean isLong(String value) {
        return GenericValidator.isLong(value);
    }

    /**
     * Checks if the value can safely be converted to a float primitive.
     *
     * @param 	value 		The value validation is being performed on.
     * @return a <code>boolean</code> value indicating whether the
     * <code>value</code> can safety be converted to a float value.
     */
    public static boolean isFloat(String value) {
        return GenericValidator.isFloat(value);
    }

    /**
     * Checks if the value can safely be converted to a double primitive.
     *
     * @param 	value 		The value validation is being performed on.
     * @return a <code>boolean</code> value indicating whether the
     * <code>value</code> can safety be converted to a double value.
     */
    public static boolean isDouble(String value) {
        return GenericValidator.isDouble(value);
    }

    /**
     * Checks to see if a date is valid based on the date formatter of a user.
     *
     * @param value the <code>String</code> value that we are going to test.
     * @param user a user context for which we want to determine if this is a
     * valid date.  Each locale can differ slightly, so the definition of what
     * a valid date might be can differ too.
     * @return a <code>boolean</code> value indicating whether the date in the
     * <code>value</code> parameter is a valid date for this user.
     */
    public static boolean isDate(String value, User user) {
        DateFormat df = user.getDateFormatter();
        boolean isValid = true;

        try {
            df.parseDateString(value);
        } catch (InvalidDateException e) {
            isValid = false;
        }

        return isValid;
    }

    /**
     * Checks to see if the date in the <code>value</code> parameter is a valid
     * date string.
     *
     * @param value a <code>String</code> value which is going to be tested as
     * to whether it is valid for conversion to a date.
     * @return a <code>boolean</code> value indicating whether the value
     * parameter is a valid date.
     */
    public static boolean isDate(String value) {
        return isDate(value, SessionManager.getUser());
    }


    /**
     * Checks if a value is within a range (min &amp; max specified
     * in the vars attribute).
     *
     * @param value The value validation is being performed on.
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
    */
    public static boolean isInRange(int value, int min, int max) {
        return GenericValidator.isInRange(value, min, max);
    }

    /**
     * Checks if a value is within a range (min &amp; max specified
     * in the vars attribute).  This test is "inclusive", meaning that it is a
     * &lt;= and &gt;= test, not a &lt; and &gt; test.
     *
     * @param value The value validation is being performed on.
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
     * @return a <code>boolean</code> value indicating whether the value in the
     * <code>value</code> parameter is between the <code>min</code> and
     * <code>max</code> parameters.
    */
    public static boolean isInRange(float value, float min, float max) {
        return GenericValidator.isInRange(value, min, max);
    }

    /**
     * Checks if a value is within a range (min &amp; max specified
     * in the vars attribute).
     *
     * @param value The value validation is being performed on.
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
    */
    public static boolean isInRange(short value, short min, short max) {
        return GenericValidator.isInRange(value, min, max);
    }

    /**
     * Checks if a value is within a range (min &amp; max specified
     * in the vars attribute).
     *
     * @param value The value validation is being performed on.
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
    */
    public static boolean isInRange(double value, double min, double max) {
        return GenericValidator.isInRange(value, min, max);
    }

    /**
     * Checks if the field is a valid credit card number.  There are three basic
     * checks going on here.  First, is the standard Luhn-10 (aka mod-10)
     * algorithm which checks the credit card checksum.  Second, is a credit
     * card prefix and length check.  (For example, cards that start with 3 need
     * to be 15 characters in length.  Third, we are checking to ensure that
     * none of the standard credit card test numbers
     * (such as 4111-1111-1111-1111) are being used.  All of these checks help
     * to ensure that we do as few round trips to the credit card processor as
     * is possible.  (They are time consuming, and generally cost money.)
     *
     * @param value The value validation is being performed on.
    */
    public static boolean isCreditCard(String value) {
        boolean isTestNumber = false;

        if (value == null) {
            return false;
        }

        //Strip out non-numeric characters from the card string
        StringBuffer numberToValidate = new StringBuffer();
        for (int i = 0; i < value.length(); i++) {
            if (Character.isDigit(value.charAt(i))) {
                numberToValidate.append(value.charAt(i));
            }
        }

        //Determine if the number is one of the test mode numbers we use with
        //the verisign test server
        isTestNumber = (value.equals("4111111111111111") ||
            value.equals("4012888888881881") ||
            value.equals("4222222222222") ||
            value.equals("5555555555554444") ||
            value.equals("5105105105105100") ||
            value.equals("378282246310005") ||
            value.equals("371449635398431") ||
            value.equals("378734493671000")
            );

        if (isTestNumber && !SessionManager.isCreditCardTestMode()) {
            return false;
        }

        return validateCreditCardLengthAndPrefix(numberToValidate.toString()) &&
            validateCreditCardWithLuhn(numberToValidate.toString());
    }

    /**
     * Indicates if the credit card type passed in matches the credit card
     * number passed in.
     *
     * @param type a <code>CreditCardType</code> a credit card type.  (e.g. Visa
     * AMEX, or MC)
     * @param number a <code>String</code> containing a credit card number
     * without any punctuation.
     * @return a <code>boolean</code> indicating whether the credit card type
     * matches the credit card number.
     */
    public boolean creditCardTypeMatchesNumber(CreditCardType type, String number) {
        return type.numberMatchesType(number);
    }

    private static boolean validateCreditCardLengthAndPrefix(String cardNumber) {
        boolean isValid = false;
        int length = cardNumber.length();
        byte secondChar;

        if (length >= 13) {
            switch (cardNumber.charAt(0)) {
                case '3':  //American express
                    secondChar = Byte.parseByte(cardNumber.substring(1,2));
                    if (length == 15 && (secondChar == 4 || secondChar == 7)) {
                        isValid = true;
                    }
                    break;
                case '4':  //Visa
                    if (cardNumber.length() == 13 || cardNumber.length() == 16) {
                        isValid = true;
                    }
                    break;
                case '5':  //Mastercard
                    secondChar = Byte.parseByte(cardNumber.substring(1,2));
                    if (length == 16 && isInRange(secondChar, 1, 5)) {
                        isValid = true;
                    }
                    break;
                case '6':  //Discover
                    if (cardNumber.startsWith("6011") && length == 16) {
                        isValid = true;
                    }
                    break;
            }
        }

        return isValid;
    }

    private static boolean validateCreditCardWithLuhn(String cardNumber) {
        // number must be validated as 0..9 numeric first!!
        int no_digit = cardNumber.length();
        int oddoeven = no_digit & 1;
        long sum = 0;
        for (int count = 0; count < no_digit; count++) {
           int digit = 0;
           try {
              digit = Integer.parseInt(String.valueOf(cardNumber.charAt(count)));
           } catch (NumberFormatException e) {
              return false;
           }
           if (((count & 1) ^ oddoeven) == 0) { // not
               digit *= 2;
               if (digit > 9) {
                  digit -= 9;
               }
           }
           sum += digit;
        }
        if (sum == 0) {
           return false;
        }

        if (sum % 10 == 0) {
           return true;
        }

        return false;
    }

    /**
     * Checks if a field has a valid e-mail address.
     *
     * Based on a script by Sandeep V. Tamhankar (stamhankar@hotmail.com),
     * http://javascript.internet.com
     *
     * @param 	value 		The value validation is being performed on.
     * @return a <code>String</code> value indicating whether this string passed
     * into this method has the correct format to be a valid email address.
     */
    public static boolean isEmail(String value) {
        return GenericValidator.isEmail(value);
    }

    /**
     * Checks if the value's length is less than or equal to the max.
     *
     * @param value The value validation is being performed on.
     * @param max The maximum length.
     * @return a <code>boolean</code> value indicating whether the value
     * parameter is shorter in length than the <code>max</code> parameter.
    */
    public static boolean maxLength(String value, int max) {
        return GenericValidator.maxLength(value, max);
    }

    /**
     * <p>Checks if the value's length is greater than or equal to the min.</p>
     *
     * @param 	value 		The value validation is being performed on.
     * @param 	min		The minimum length.
    */
    public static boolean minLength(String value, int min) {
        return GenericValidator.minLength(value, min);
    }

    /**
     * Ensure that the String only consists of numbers.  No commas or periods
     * are allowed.
     *
     * @param value a <code>String</code> value to check as being a valid
     * numeric value.
     * @return a <code>boolean</code> value indicating whether or not this is a
     * valid numeric string.
     */
    public static boolean isNumeric(String value) {
        return isNumeric(value, false);
    }

    public static boolean isNumeric(String value, boolean acceptNull) {
        NumberFormat nf = NumberFormat.getInstance();
        boolean isValid = true;

        if ((!acceptNull) && (value == null)) {
            isValid = false;
        } else {
            try {
                nf.parseNumber(value);
            } catch (ParseException e) {
                isValid = false;
            }
        }

        return isValid;
    }
    
    /**
     * Ensure that the String only consists of negative numbers.  No commas or periods
     * are allowed. This would return false of value is null or blank
     *
     * @param value a <code>String</code> value to check as being a valid
     * negative value.
     * @return a <code>boolean</code> value indicating whether or not this is a
     * valid numeric string.
     */
    public static boolean isNegative(String value) {
        NumberFormat nf = NumberFormat.getInstance();
        boolean isValid = true;

        if ((value == null) || "".equals(value.trim())) {
            isValid = false;
        } else {
            try {
                Number number = nf.parseNumber(value);
                if(number.doubleValue() < 0)
                    isValid = true;
                else
                    isValid = false;
            } catch (ParseException e) {
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Determines whether the value supplied in the value parameter is a valid
     * database identifier in the Project.Net database.  Currently, all of the
     * PK fields are Number(20) fields in Oracle.  This means that the fields
     * can be a number of up to 20 digits in length.
     *
     * A null, empty, or blank database identifier is not considered valid
     *
     * @param value a <code>String</code> value which we are going to test to
     * determine if it is a valid database identifier.
     * @return a <code>boolean</code> value indicating whether the value
     * parameter is a valid database identifier.
     */
    public static boolean isValidDatabaseIdentifier(String value) {
        boolean isValid = true;

        if ((value == null) || (value.trim().length() == 0)){
            isValid = false;
        } else {
            //Make sure the value is under 20 characters in length
            if (!maxLength(value, 20)) {
                isValid = false;
            }

            //Make sure the value is at least 1 character in length
            if (!minLength(value, 1)) {
                isValid = false;
            }

            //Make sure the value is numeric
            if (!isNumeric(value)) {
                isValid = false;
            } else {
                //Make sure the value is > 0
                try {
                    Number asNumber = NumberFormat.getInstance().parseNumber(value);
                    if (asNumber.longValue() < 0) {
                        isValid = false;
                    }
                } catch (ParseException e) {
                    isValid = false;
                }
            }
        }

        return isValid;
    }
}
