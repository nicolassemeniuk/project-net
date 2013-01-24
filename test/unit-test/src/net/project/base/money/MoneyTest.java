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
package net.project.base.money;

import java.util.Currency;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MoneyTest extends TestCase {
    public MoneyTest(String s) {
        super(s);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(MoneyTest.class);
        return suite;
    }

    public void testAdd() {
        try {
            Money money = new Money("0", Currency.getInstance("USD"));
            money = money.add(new Money("100", Currency.getInstance("USD")));
            assertEquals(money, new Money("100", Currency.getInstance("USD")));

            money = new Money("100", Currency.getInstance("USD"));
            money = money.add(new Money("1000000", Currency.getInstance("USD")));
            assertEquals(money, new Money("1000100", Currency.getInstance("USD")));

            money = new Money("0", Currency.getInstance("USD"));
            money = money.add(new Money("0", Currency.getInstance("USD")));
            assertEquals(money, new Money("0", Currency.getInstance("USD")));

            money = new Money("-100", Currency.getInstance("USD"));
            money = money.add(new Money("-100", Currency.getInstance("USD")));
            assertEquals(money, new Money("-200", Currency.getInstance("USD")));

            money = new Money("0", Currency.getInstance("USD"));
            money = money.add(new Money("-100", Currency.getInstance("USD")));
            assertEquals(money, new Money("-100", Currency.getInstance("USD")));
        } catch (InvalidCurrencyException e) {
            fail("testAdd reports invalid currency, when currency is valid.");
        }
    }

    public void testSubtract() {
        try {
            Money money = new Money("100", Currency.getInstance("USD"));
            money = money.subtract(new Money("50", Currency.getInstance("USD")));
            assertEquals(money, new Money("50", Currency.getInstance("USD")));

            money = new Money("0", Currency.getInstance("USD"));
            money = money.subtract(new Money("0", Currency.getInstance("USD")));
            assertEquals(money, new Money("0", Currency.getInstance("USD")));

            money = new Money("50", Currency.getInstance("USD"));
            money = money.subtract(new Money("100", Currency.getInstance("USD")));
            assertEquals(money, new Money("-50", Currency.getInstance("USD")));

            money = new Money("-50", Currency.getInstance("USD"));
            money = money.subtract(new Money("-100", Currency.getInstance("USD")));
            assertEquals(money, new Money("50", Currency.getInstance("USD")));
        } catch (InvalidCurrencyException e) {
            fail("testSubtract reports invalid currency, when currency is valid.");
        }
    }
}
