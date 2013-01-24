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
 /*----------------------------------------------------------------------+
|                                                                       
|    $RCSfile$
|   $Revision: 17088 $
|       $Date: 2008-03-21 21:29:11 +0530 (Fri, 21 Mar 2008) $
|     $Author: sjmittal $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.xml;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests XMLUtils.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class XMLUtilsTest extends TestCase {

    public XMLUtilsTest(String testName) {
        super(testName);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(XMLUtilsTest.class);

        return suite;
    }

    /**
     */
    public void testFormatISODateTime() {

        // Setup date formatter with predictable format
        //sjmittal: no timezone as this is not as per strict w3 format. See my comments in XMLUtils
        SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
//        df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        df.applyPattern("MM/dd/yyyy hh:mm:ss");

        try {
            // Null date
            assertEquals("", XMLUtils.formatISODateTime(null));

            // Regular Dates
            assertEquals("2002-12-21T17:32:25", XMLUtils.formatISODateTime(df.parse("12/21/2002 17:32:25")));
            assertEquals("2000-02-29T17:32:25", XMLUtils.formatISODateTime(df.parse("02/29/2000 17:32:25")));
            assertEquals("2002-01-01T00:00:00", XMLUtils.formatISODateTime(df.parse("01/01/2002 00:00:00")));

        } catch (ParseException e) {
            fail("XMLUtils.formatISODateTime caught an unexpected ParseException: " + e);

        }

    }

    public void testParseISODateTime() {

        // Setup date formatter with predictable format
        //sjmittal: no timezone as this is not as per strict w3 format. See my comments in XMLUtils        
        SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
//        df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        df.applyPattern("MM/dd/yyyy hh:mm:ss");

        // Null returns null
        assertNull(XMLUtils.parseISODateTime(null));

        // Invalid returns null
        assertNull(XMLUtils.parseISODateTime(""));
        assertNull(XMLUtils.parseISODateTime("xyzabc"));
        
        // it may play with formatter.setLenient(false);
        //assertNull(XMLUtils.parseISODateTime("2002-02-29T00:00:00-0800"));
        //assertNull(XMLUtils.parseISODateTime("2002-01-01T26:00:00-0800"));

        try {
            // Regular Date
            assertEquals(df.parse("12/21/2002 17:32:25"), XMLUtils.parseISODateTime("2002-12-21T17:32:25"));
            assertEquals(df.parse("02/29/2000 17:32:25"), XMLUtils.parseISODateTime("2000-02-29T17:32:25"));
            assertEquals(df.parse("01/01/2002 00:00:00"), XMLUtils.parseISODateTime("2002-01-01T00:00:00"));

        } catch (ParseException e) {
            fail("XMLUtils.formatISODateTime caught an unexpected ParseException: " + e);

        }

    }

    public void testEscape() {

        assertEquals("", XMLUtils.escape(null));
        assertEquals("", XMLUtils.escape(""));
        assertEquals("abc", XMLUtils.escape("abc"));

        assertEquals("a&gt;b", XMLUtils.escape("a>b"));
        assertEquals("a&lt;b", XMLUtils.escape("a<b"));
        assertEquals("a&amp;b", XMLUtils.escape("a&b"));
        assertEquals("a&quot;b", XMLUtils.escape("a\"b"));
        assertEquals("x&gt;&gt;x&lt;&lt;x&amp;&amp;x&quot;&quot;x", XMLUtils.escape("x>>x<<x&&x\"\"x"));

        // Now check escaping of unicode characters

        // The String we're going to test is built from all characters under u0020
        // Build array of all characters under u0020
        char[] original1 = new char[0x20];
        for (int i = 0; i < 0x20; i++) {
            original1[i] = (char) i;
        }

        // The expected results is a string that has every character changed
        // to u200b except for u0009 (\t), u000a (\n), u000d (\r)
        char[] expected1 = new char[0x20];
        for (int i = 0; i < 0x20; i++) {
            if ((i == 0x09 || i == 0x0a || i == 0x0d)) {
                // If the character won't be escaped, add it
                expected1[i] = (char) i;
            } else {
                // Character will be escaped so expect it to be u200b
                expected1[i] = '\u200b';

            }
        }

        assertEquals(String.valueOf(expected1), XMLUtils.escape(String.valueOf(original1)));

    }

}
