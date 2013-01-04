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
package net.project.calendar.vcal;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class tests the functions in the {@link net.project.calendar.vcal.VCalendarUtils}
 * class.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class VCalendarUtilsTest extends TestCase {

    public VCalendarUtilsTest(String testName) {
        super(testName);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(VCalendarUtilsTest.class);
        
        return suite;
    }

    /**
     * Test of isQuotedPrintable method, of class net.project.calendar.vcal.VCalendarUtils.
     */
    public void testIsQuotedPrintable() {
        // Test \n, \r\n, \r in middle are quoted printable
        assertEquals(VCalendarUtils.isQuotedPrintableRequired("abc\ndef"), true);
        assertEquals(VCalendarUtils.isQuotedPrintableRequired("abc\r\ndef"), true);
        assertEquals(VCalendarUtils.isQuotedPrintableRequired("abc\rdef"), true);

        // Test \n, \r\n, \r at end are quoted printable
        assertEquals(VCalendarUtils.isQuotedPrintableRequired("abcdef\n"), true);
        assertEquals(VCalendarUtils.isQuotedPrintableRequired("abcndef\r\n"), true);
        assertEquals(VCalendarUtils.isQuotedPrintableRequired("abcdef\r"), true);

        // Test \n, \r\n, \r at start are quoted printable
        assertEquals(VCalendarUtils.isQuotedPrintableRequired("abcdef\n"), true);
        assertEquals(VCalendarUtils.isQuotedPrintableRequired("abcndef\r\n"), true);
        assertEquals(VCalendarUtils.isQuotedPrintableRequired("abcdef\r"), true);

        // Test none is not quoted printable
        assertEquals(VCalendarUtils.isQuotedPrintableRequired("abcdef"), false);

        // No other text
        assertEquals(VCalendarUtils.isQuotedPrintableRequired("\n"), true);
        assertEquals(VCalendarUtils.isQuotedPrintableRequired("\r\n"), true);
        assertEquals(VCalendarUtils.isQuotedPrintableRequired("\r"), true);

        // Empty String
        assertEquals(VCalendarUtils.isQuotedPrintableRequired(""), false);

        // Null String
        assertEquals(VCalendarUtils.isQuotedPrintableRequired(null), false);

    }

    /**
     * Test of makeQuotedPrintable method, of class net.project.calendar.vcal.VCalendarUtils.
     */
    public void testMakeQuotedPrintable() {
        // In middle
        assertEquals(VCalendarUtils.makeQuotedPrintable("abc\ndef"), "abc=0D=0A=\ndef");
        assertEquals(VCalendarUtils.makeQuotedPrintable("abc\r\ndef"), "abc=0D=0A=\ndef");
        assertEquals(VCalendarUtils.makeQuotedPrintable("abc\rdef"), "abc=0D=0A=\ndef");

        // At end
        assertEquals(VCalendarUtils.makeQuotedPrintable("abcdef\n"), "abcdef=0D=0A=\n");
        assertEquals(VCalendarUtils.makeQuotedPrintable("abcdef\r\n"), "abcdef=0D=0A=\n");
        assertEquals(VCalendarUtils.makeQuotedPrintable("abcdef\r"), "abcdef=0D=0A=\n");

        // At start
        assertEquals(VCalendarUtils.makeQuotedPrintable("\nabcdef"), "=0D=0A=\nabcdef");
        assertEquals(VCalendarUtils.makeQuotedPrintable("\r\nabcdef"), "=0D=0A=\nabcdef");
        assertEquals(VCalendarUtils.makeQuotedPrintable("\rabcdef"), "=0D=0A=\nabcdef");

        // None doesn't change
        assertEquals(VCalendarUtils.makeQuotedPrintable("abcdef"), "abcdef");

        // Multiples (2 consecutive breaks)
        assertEquals(VCalendarUtils.makeQuotedPrintable("abc\n\ndef"), "abc=0D=0A=\n=0D=0A=\ndef");
        assertEquals(VCalendarUtils.makeQuotedPrintable("abc\r\n\r\ndef"), "abc=0D=0A=\n=0D=0A=\ndef");
        assertEquals(VCalendarUtils.makeQuotedPrintable("abc\r\rdef"), "abc=0D=0A=\n=0D=0A=\ndef");

        // No other text
        assertEquals(VCalendarUtils.makeQuotedPrintable("\n"), "=0D=0A=\n");
        assertEquals(VCalendarUtils.makeQuotedPrintable("\r\n"), "=0D=0A=\n");
        assertEquals(VCalendarUtils.makeQuotedPrintable("\r"), "=0D=0A=\n");

        // Empty String
        assertEquals(VCalendarUtils.makeQuotedPrintable(""), "");

        // Null String
        assertEquals(VCalendarUtils.makeQuotedPrintable(null), "");
    }

    /**
     * Test of escapeParameterValue method, of class net.project.calendar.vcal.VCalendarUtils.
     */
    public void testEscapeParameterValue() {

        // Null string
        assertEquals("", VCalendarUtils.escapeParameterValue(null));

        // Empty string
        assertEquals("", VCalendarUtils.escapeParameterValue(""));

        // Nothing to escape
        assertEquals("abc", VCalendarUtils.escapeParameterValue("abc"));

        // Various permutations
        assertEquals("\\;", VCalendarUtils.escapeParameterValue(";"));
        assertEquals("ab\\;cdef", VCalendarUtils.escapeParameterValue("ab;cdef"));
        assertEquals("\\;abc", VCalendarUtils.escapeParameterValue(";abc"));
        assertEquals("abc\\;", VCalendarUtils.escapeParameterValue("abc;"));
        assertEquals("\\;\\;\\;", VCalendarUtils.escapeParameterValue(";;;"));
        assertEquals("\\;abc\\;def\\;ghi\\;", VCalendarUtils.escapeParameterValue(";abc;def;ghi;"));

    }
}
