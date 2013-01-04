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
package net.project.creditcard.verisign;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for VeriSign credit card processing.
 *
 * @author Matthew Flower
 * @since Version 7.5
 */
public class VerisignCreditCardProcessorTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     *
     * @param s a <code>String</code> containing the name of this test.
     */
    public VerisignCreditCardProcessorTest(String s) {
        super(s);
    }

    /**
     * Direct route to unit test this object from the command line.
     *
     * @param args a <code>String[]</code> value which contains the command line
     * options.  (These will be unused.)
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Method designed to run multiple tests in the same package.
     *
     * @return a <code>TestSuite</code> object.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(VerisignCreditCardProcessorTest.class);
        return suite;
    }

    /**
     * Test the charge method of the VeriSignCreditCardProcessor.
     */
    public void testCharge() {
        //This test only works if the pnetCreditCard.jar is present.
        /*
        //Set up the credit card
        GregorianCalendar cal = new GregorianCalendar();
        CreditCard cc = new CreditCard();
        cc.setCreditCardNumber("4111-1111-1111-1111");
        cal.set(2005,8,1);
        cc.setExpirationDate(cal.getTime());

        //Charge the credit card 150.00
        VerisignCreditCardProcessor processor = new VerisignCreditCardProcessor();
        processor.setMerchantAccount(new PNETMerchantAccount());

        try {

            ICreditCardProcessingResults results = processor.makePurchase(cc,
                new Money("150.00", Currency.getInstance("USD")), "1231234", "");

            if (results.getResultType() != CreditCardResultType.SUCCESS) {
                fail("Card was not approved: "+results.getMessage());
            } else {
                System.out.println("Card approved: "+results.getMessage());
            }
        } catch (PnetException e) {
            fail("VerisignCreditCardProcessor.charge failed: "+e.getMessage());
        }
        */
    }

}
