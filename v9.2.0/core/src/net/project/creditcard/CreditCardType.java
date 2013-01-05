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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.HTMLOptionList;
import net.project.gui.html.IHTMLOption;
import net.project.util.Validator;

/**
 * A typed enumeration of CreditCardType classes.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public abstract class CreditCardType implements IHTMLOption {
    /** This list contains all of the possible types for this typed enumeration. */
    private static List types = new ArrayList();
    /** Static type for VISA credit cards. */
    public static final CreditCardType VISA =
        new CreditCardType("10", "prm.global.creditcard.types.visa",
        new Object[] {new CreditCardFormat("####-####-####-####", 16),
        new CreditCardFormat("#####-####-####", 13)}) {
            public boolean numberMatchesType(String ccNumber) {
                if (ccNumber.length() != 13 && ccNumber.length() != 16) {
                    return false;
                }
                if (!ccNumber.startsWith("4")) {
                    return false;
                }
                return true;
            }
        };
    /** Static type for master card credit cards. */
    public static final CreditCardType MASTERCARD =
        new CreditCardType("20", "prm.global.creditcard.types.mastercard",
        new Object [] {new CreditCardFormat("####-####-####-####",16)}) {
            public boolean numberMatchesType(String ccNumber) {
                if (ccNumber.length() < 16) {
                    return false;
                }
                byte firstChar = Byte.parseByte(ccNumber.substring(0,1));
                byte secondChar = Byte.parseByte(ccNumber.substring(1,2));
                if (firstChar == 5 && Validator.isInRange(secondChar, 1, 5))
                    return true;
                else
                    return false;
            }
        };
    /** Static type for American Express credit cards. */
    public static final CreditCardType AMEX =
        new CreditCardType("30", "prm.global.creditcard.types.amex",
        new Object[] {new CreditCardFormat("####-######-#####", 15)}) {
            public boolean numberMatchesType(String ccNumber) {
                return (ccNumber.startsWith("33") || ccNumber.startsWith("37"))
                    && ccNumber.length() == 15;
            }
        };
    /**
     * The default credit card type to show.
     */
    public static final CreditCardType DEFAULT = VISA;

    /**
     * Get the CreditCardType that corresponds to a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of a
     * <code>CreditCardType</code> we want to find.
     * @return a <code>CreditCardType</code> corresponding to the supplied ID, or
     * the DEFAULT <code>CreditCardType</code> if one cannot be found.
     */
    public static CreditCardType getForID(String id) {
        CreditCardType toReturn = DEFAULT;

        for (Iterator it = types.iterator(); it.hasNext();) {
            CreditCardType type = (CreditCardType)it.next();
            if (type.getID().equals(id)) {
                toReturn = type;
                break;
            }
        }

        return toReturn;
    }

    /**
     * Get a string containing the credit card types encoded as an HTML option
     * list.  This allows types to be selected from a list.
     *
     * @param defaultType a <code>CreditCardType</code> value which is the card
     * that should be selected by default.  If null, no card type will be
     * selected by default.
     * @return a <code>String</code> value containing the HTML to render a
     * select list of credit card types.
     */
    public static String getHTMLOptionList(CreditCardType defaultType) {
        if (defaultType == null) {
            return HTMLOptionList.makeHtmlOptionList(types);
        } else {
            return HTMLOptionList.makeHtmlOptionList(types, defaultType);
        }
    }

    //**************************************************************************
    // Implementation code
    //**************************************************************************
    /** A Unique identifier for this CreditCardType. */
    private String id;
    /** A token used to find a human-readable name for this CreditCardType. */
    private String displayToken;
    /** Human readable formats in which to display credit cards. */
    private Map displayFormats = new HashMap();

    /**
     * Private constructor which creates a new CreditCardType instance.
     *
     * @param id a <code>String</code> value which is a unique identifier for 
     * this typed enumeration.
     * @param displayToken a <code>String</code> value which contains a token to
     * look up the display name for this type.
     * @param displayFormats a <code>Object[]</code> collection which identifies
     * the string formats that are associated with this card type.  In general,
     * no two display formats should be available for a single card type with a
     * given length.  This object array should only contain CreditCardFormat
     * objects.
     */
    private CreditCardType(String id, String displayToken, Object[] displayFormats) {
        this.id = id;
        this.displayToken = displayToken;
        types.add(this);

        for (int i = 0; i < displayFormats.length; i++) {
            this.displayFormats.put(
                new CreditCardLookupKey(this, ((CreditCardFormat)displayFormats[i]).cardLength),
                ((CreditCardFormat)displayFormats[i]).cardFormat);
        }
    }

    /**
     * Get the unique identifier for this type enumeration.
     *
     * @return a <code>String</code> value containing the unique id for this 
     * type.
     */
    public String getID() {
        return id;
    }

    /**
     * Return a human-readable display name for this CreditCardType.
     *
     * @return a <code>String</code> containing a human-readable version of this
     * CreditCardType.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }

    /**
     * Returns the value for the <code>value</code> attribute of the HTML
     * option.
     *
     * @return a <code>String</code> value which will become the value="?" part
     * of the option tag.
     */
    public String getHtmlOptionValue() {
        return getID();
    }

    /**
     * Returns the value for the content part of the HTML option.
     *
     * @return a <code>String</code> value that will be displayed for this
     * html option.
     */
    public String getHtmlOptionDisplay() {
        return toString();
    }

    public abstract boolean numberMatchesType(String ccNumber);

    /**
     * Forward a credit card number which only consists of numbers in a prettier
     * format which contains dashes.  (For example, 4111111111111111 becomes
     * 4111-1111-1111-1111.)
     *
     * @param creditCardNumber a <code>String</code> object which contains a
     * credit card number.
     * @return a <code>String</code> value containing the creditCardNumber
     * parameter reformatted
     */
    public String formatNumberString(String creditCardNumber) {
        StringBuffer formattedCardNumber = new StringBuffer();

        //Find the correct credit card format
        String creditCardFormat = (String)displayFormats.get(
            new CreditCardLookupKey(this, creditCardNumber.length()));

        //If we didn't find a format, use a default
        if (creditCardFormat == null) {
            char[] defaultFormat = new char[creditCardNumber.length()];
            Arrays.fill(defaultFormat, '#');
            creditCardFormat = new String(defaultFormat);
        }

        int creditCardNumberIndex = 0;
        for (int i = 0; i < creditCardFormat.length(); i++) {
            if (creditCardFormat.charAt(i) == '#') {
                formattedCardNumber.append(creditCardNumber.charAt(creditCardNumberIndex++));
            } else if (creditCardFormat.charAt(i) == 'X') {
                formattedCardNumber.append('X');
                creditCardNumberIndex+=1;
            } else {
                formattedCardNumber.append(creditCardFormat.charAt(i));
            }
        }

        return formattedCardNumber.toString();
    }

}

/**
 * Class primarily designed to reside in a hashmap.  It has fields identifying
 * how to format credit cards of different lengths.
 *
 * @author Matthew Flower
 * @since Version 7.5
 */
class CreditCardFormat {
    /**
     * Contains the mask of how to display a credit card number.  (For example
     * ####-####-####-####).
     */
    String cardFormat;
    /** The length of credit card which is required for this format. */
    int cardLength;

    /**
     * Standard constructor which creates a new instance of CreditCardFormat.
     *
     * @param cardFormat a <code>String</code> which contains the format in
     * which the credit card should be displayed.  A pound sign represents a
     * credit card digit.
     * @param cardLength a <code>int</code> which contains the length of
     * credit cards which match this format.
     */
    public CreditCardFormat(String cardFormat, int cardLength) {
        this.cardFormat = cardFormat;
        this.cardLength = cardLength;
    }
}

/**
 * Class which provides the ability to look up a credit card format according to
 * the card type and length.
 *
 * @author Matthew Flower
 * @since Version 7.5
 */
class CreditCardLookupKey {
    /** Card type. */
    private CreditCardType type;
    /** Number of digits in the credit card. */
    private int length;

    /**
     * Standard constructor.
     *
     * @param type which type of cc this key points to.
     * @param length length of cc which this key's formatter can format.
     */
    public CreditCardLookupKey(CreditCardType type, int length) {
        this.type = type;
        this.length = length;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CreditCardLookupKey))
            return false;

        final CreditCardLookupKey creditCardLookupKey = (CreditCardLookupKey)o;

        if (length != creditCardLookupKey.length)
            return false;
        if (type != null ? !type.equals(creditCardLookupKey.type) : creditCardLookupKey.type != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (type != null ? type.hashCode() : 0);
        result = 29 * result + length;
        return result;
    }
}