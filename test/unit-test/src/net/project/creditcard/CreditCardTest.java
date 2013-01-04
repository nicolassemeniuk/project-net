/* 
 * Copyright 2000-2006 Project.net Inc.
 *
 * Licensed under the Project.net Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://dev.project.net/licenses/PPL1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 16593 $
|       $Date: 2007-12-01 13:23:29 +0530 (Sat, 01 Dec 2007) $
|     $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.creditcard;

import java.util.GregorianCalendar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Unit test for CreditCard object.
 *
 * @author Matthew Flower
 * @since Version 7.5
 */
public class CreditCardTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     *
     * @param s a <code>String</code> containing the name of this test.
     */
    public CreditCardTest(String s) {
        super(s);
    }

    /**
     * Direct route to unit test this object from the command line.
     *
     * @param args a <code>String[]</code> value which contains the command line
     * options.  (These will be unused.)
     */
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    /**
     * Run a collection of tests orchestrated by this object.
     *
     * @return a <code>TestSuite</code> of tests that we are going to run.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(CreditCardTest.class);
        return suite;
    }

    /**
     * Test the {@link net.project.creditcard.CreditCard#getCreditCardNumberWithoutPunctuation}
     * method.
     */
    public void testCreditCardNumberWithoutPunctuation() {
        CreditCard cc = new CreditCard();
        cc.setCreditCardNumber("4111-1111-1111-1111");
        assertEquals("4111111111111111", cc.getCreditCardNumberWithoutPunctuation());

        cc.setCreditCardNumber("4111.1111.1111.1111");
        assertEquals("4111111111111111", cc.getCreditCardNumberWithoutPunctuation());

        cc.setCreditCardNumber("4111 1111 1111 1111");
        assertEquals("4111111111111111", cc.getCreditCardNumberWithoutPunctuation());
    }

    /**
     * Test the {@link net.project.creditcard.CreditCard#isValidNumber}
     * method.
     */
    public void testIsValidNumber() {
        CreditCard cc = new CreditCard();

        //These are the test numbers that are provided to us by Verisign
        cc.setCreditCardNumber("4111-1111-1111-1111");
        assertTrue(cc.isValidNumber());
        //Visa #2
        cc.setCreditCardNumber("4012888888881881");
        assertTrue(cc.isValidNumber());
        //Visa #3
        cc.setCreditCardNumber("4222222222222");
        assertTrue(cc.isValidNumber());
        //Mastercard #1
        cc.setCreditCardNumber("5555555555554444");
        assertTrue(cc.isValidNumber());
        //Mastercard #2
        cc.setCreditCardNumber("5105105105105100");
        assertTrue(cc.isValidNumber());
        //Amex #1
        cc.setCreditCardNumber("378282246310005");
        assertTrue(cc.isValidNumber());
        //Amex #2
        cc.setCreditCardNumber("371449635398431");
        assertTrue(cc.isValidNumber());
        //Amex Corporate
        cc.setCreditCardNumber("378734493671000");
        assertTrue(cc.isValidNumber());
        //Discover #1
        cc.setCreditCardNumber("6011111111111117");
        assertTrue(cc.isValidNumber());
        //Discover #2
        cc.setCreditCardNumber("6011000990139424");
        assertTrue(cc.isValidNumber());

        /*
         * We are going to skip these card types for now because they aren't
         * supported by our validation algorithm.  Phil says that we don't need
         * them anyhow.
         */
        //JCB #1
        //cc.setCreditCardNumber("3530111333300000");
        //assertTrue(cc.isValidNumber());
        //JCB #2
        //cc.setCreditCardNumber("3566002020360505");
        //assertTrue(cc.isValidNumber());
        //Diners Club #1
        //cc.setCreditCardNumber("38520000023237");
        //assertTrue(cc.isValidNumber());
        //Diners Club #2
        //cc.setCreditCardNumber("30569309025904");
        //assertTrue(cc.isValidNumber());
    }

    /**
     * Test the {@link net.project.creditcard.CreditCard#isValidExpirationDate}
     * method.
     */
    public void testIsValidExpirationDate() {
        CreditCard cc = new CreditCard();
        GregorianCalendar cal = new GregorianCalendar();

        cal.set(1999, 1, 1);
        cc.setExpirationDate(cal.getTime());
        assertEquals(false, cc.isValidExpirationDate());

        cal.set(3000, 1, 1);
        cc.setExpirationDate(cal.getTime());
        assertEquals(true, cc.isValidExpirationDate());

        cal = new GregorianCalendar();
        cal.add(GregorianCalendar.MONTH, 1);
        cc.setExpirationDate(cal.getTime());
        assertEquals(true, cc.isValidExpirationDate());
    }

    /**
     * Test the {@link net.project.creditcard.CreditCard#getExpirationDateMMYY}
     * method.
     */
    public void testGetExpirationDateMMYY() {
        CreditCard cc = new CreditCard();
        GregorianCalendar cal = new GregorianCalendar();

        cal.set(2003, 0, 1);
        cc.setExpirationDate(cal.getTime());
        assertEquals("0103", cc.getExpirationDateMMYY());

        cal.set(1999, 11, 31);
        cc.setExpirationDate(cal.getTime());
        assertEquals("1299", cc.getExpirationDateMMYY());

        cal.set(2010, 6, 14);
        cc.setExpirationDate(cal.getTime());
        assertEquals("0710", cc.getExpirationDateMMYY());

        cal.set(3000, 1, 1);
        cc.setExpirationDate(cal.getTime());
        assertEquals("0200", cc.getExpirationDateMMYY());
    }

    /**
     * Test the {@link net.project.creditcard.CreditCard#format}
     * method.
     */
    public void testFormat() {
        assertEquals("4111-1111-1111-1111", CreditCard.format("4111111111111111", CreditCardType.VISA, false));
        assertEquals("XXXX-XXXX-XXXX-1111", CreditCard.format("4111111111111111", CreditCardType.VISA, true));
        assertEquals("XXXX-XXXX-XXXX-1111", CreditCard.format("XXXXXXXXXXXX1111", CreditCardType.VISA, false));
        assertEquals("XXXX-XXXX-XXXX-1111", CreditCard.format("XXXXXXXXXXXX1111", CreditCardType.VISA, true));
        assertEquals("42222-2222-2222", CreditCard.format("4222222222222", CreditCardType.VISA, false));
        assertEquals("XXXXX-XXXX-2222", CreditCard.format("4222222222222", CreditCardType.VISA, true));
        assertEquals("XXXXX-XXXX-2222", CreditCard.format("XXXXXXXXX2222", CreditCardType.VISA, true));
        assertEquals("XXXXX-XXXX-2222", CreditCard.format("XXXXXXXXX2222", CreditCardType.VISA, true));
        assertEquals("XXXX1234", CreditCard.format("12341234", CreditCardType.VISA, true));
        assertEquals("12341234", CreditCard.format("12341234", CreditCardType.VISA, false));
        assertEquals("XXXX1234", CreditCard.format("12341234", CreditCardType.VISA, true));

        assertEquals("5555-5555-5555-4444", CreditCard.format("5555555555554444", CreditCardType.MASTERCARD, false));
        assertEquals("XXXX-XXXX-XXXX-4444", CreditCard.format("5555555555554444", CreditCardType.MASTERCARD, true));
        assertEquals("XXXX-XXXX-XXXX-4444", CreditCard.format("XXXXXXXXXXXX4444", CreditCardType.MASTERCARD, false));
        assertEquals("XXXX-XXXX-XXXX-4444", CreditCard.format("XXXXXXXXXXXX4444", CreditCardType.MASTERCARD, true));
    }
}
