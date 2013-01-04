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
|   $Revision: 15404 $
|       $Date: 2006-08-28 20:20:09 +0530 (Mon, 28 Aug 2006) $
|     $Author: deepak $
|
+-----------------------------------------------------------------------------*/
package net.project.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class tests the functions in the {@link XSLFormat} class.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class XSLFormatTest extends TestCase {

    public XSLFormatTest(String testName) {
        super(testName);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(XSLFormatTest.class);
        
        return suite;
    }

    /**
     * Tests method {@link XSLFormat#formatText(String)}.
     */
    public void testFormatText() {

        String source;
        String expected;

        assertEquals("", XSLFormat.formatText(null));
        assertEquals("", XSLFormat.formatText(""));
        assertEquals("This is a test", XSLFormat.formatText("This is a test"));

        // No line breaks or &amp;, &lt;, &gt;, &quot;

        // Line breaks
        source = "Test\nline break";
        expected = "Test<br>line break";
        assertEquals(expected, XSLFormat.formatText(source));

        // ampersands &
        source = "Test & ampersand";
        expected = "Test &amp; ampersand";
        assertEquals(expected, XSLFormat.formatText(source));

        // less than <
        source = "Test < less than";
        expected = "Test &lt; less than";
        assertEquals(expected, XSLFormat.formatText(source));

        // greater than >
        source = "Test > greater than";
        expected = "Test &gt; greater than";
        assertEquals(expected, XSLFormat.formatText(source));

        // double-quote "
        source = "Test \" double quote";
        expected = "Test &quot; double quote";
        assertEquals(expected, XSLFormat.formatText(source));

        // Mix
        source = "\n&<>\"";
        expected = "<br>&amp;&lt;&gt;&quot;";
        assertEquals(expected, XSLFormat.formatText(source));

    }

    /**
     * Tests {@link XSLFormat#formatTextHyperlink(String, int)}.
     */
    public void testFormatTextHyperlinkTruncateByLength() {

        String source;
        String expected;

        // Null, empty string and no special handling
        assertEquals("", XSLFormat.formatTextHyperlink(null, 0));
        assertEquals("", XSLFormat.formatTextHyperlink("", 0));
        assertEquals("This is a test", XSLFormat.formatTextHyperlink("This is a test", 0));

        // Test length parameter
        source = "This is a test";

        // No change with invalid length parameter
        expected = source;
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, -1));
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0));
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, source.length()));

        // Valid length parameter
        expected = "T";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 1));

        expected = "Th";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 2));

        expected = "This is a tes";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 13));

        // Test Escape
        source = "Test\nline break";
        expected = "Test<br>line break";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0));

        // Test hyperlink
        source = "http://www.project.net/";
        expected = "<a href=\"http://www.project.net/\" target=\"external_viewer\">http://www.project.net/</a>";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0));

        // Truncation plus hyperlink
        source = "This is a test http://www.project.net/ this is truncated";
        expected = "This is a test <a href=\"http://www.project.net/\" target=\"external_viewer\">http://www.project.net/</a>";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 38));

    }

    /**
     * Tests {@link XSLFormat#formatTextHyperlink(String, int, int)}.
     */
    public void testFormatTextHyperlink() {

        String source;
        String expected;

        // Null, empty string and no special handling
        source = null;
        expected = "";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0, 0));

        source = "";
        expected = source;
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0, 0));

        source = "This is a test";
        expected = source;
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0, 0));

        source = "This\nis\na test";
        expected = "This<br>is<br>a test";
        assertEquals(expected, XSLFormat.formatTextHyperlink(expected, 0, 0));

        //
        // No truncation but escaping / handling
        //

        // Test Escape
        source = "Test\nline break";
        expected = "Test<br>line break";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0, 0));

        // Test hyperlink
        source = "http://www.project.net/";
        expected = "<a href=\"http://www.project.net/\" target=\"external_viewer\">http://www.project.net/</a>";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0, 0));


        //
        // Test truncation by length
        //

        // Test length parameter
        source = "This is a test";

        expected = source;
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, -1, 0));
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0, 0));
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, source.length(), 0));

        expected = "T";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 1, 0));

        expected = "Th";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 2, 0));

        expected = "This is a tes";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 13, 0));

        // Truncation plus hyperlink
        source = "This is a test http://www.project.net/ this is truncated";
        expected = "This is a test <a href=\"http://www.project.net/\" target=\"external_viewer\">http://www.project.net/</a>";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 38, 0));


        //
        // Test truncation by linefeed
        //

        source = "This\nis\na\ntest\n";
        expected = "This<br>is<br>a<br>test<br>";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0, -1));
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0, 0));
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0, 5));

        expected = "This";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0, 1));
        expected = "This<br>is";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0, 2));
        expected = "This<br>is<br>a";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0, 3));
        expected = "This<br>is<br>a<br>test";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0, 4));
        expected = "This<br>is<br>a<br>test<br>";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0, 5));

        // Truncation plus hyperlink
        source = "This is a test\nhttp://www.project.net/\nthis is truncated";
        expected = "This is a test<br><a href=\"http://www.project.net/\" target=\"external_viewer\">http://www.project.net/</a>";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 0, 2));

        //
        // Combination length and linefeed
        //

        source = "This\nis\na\ntest\n";

        expected = "This<br>is<br>a";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 14, 3));

        expected = "This<br>is<br>a";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 10, 3));

        // BFD-1985
        // An exception occurs when the last linefeed occurs after the truncation point
        // In this example, the last linfeed (linefeed number 1) occurrs at position 9,
        // which is after the truncation point (position 7)
        source = "This is a\ntest";
        expected = "This is";
        assertEquals(expected, XSLFormat.formatTextHyperlink(source, 7, 1));

    }

    /**
     * Tests {@link XSLFormat#formatTextHyperlink(String, int, int)}.
     */
    public void testIsTruncationRequired() {

        assertFalse(XSLFormat.isTruncationRequired(null, 0, 0));
        assertFalse(XSLFormat.isTruncationRequired("", 0, 0));
        assertFalse(XSLFormat.isTruncationRequired("This is a test", 0, 0));
        assertFalse(XSLFormat.isTruncationRequired("This\nis a test", 0, 0));

        // Check length
        assertFalse(XSLFormat.isTruncationRequired("This is a test", 14, 0));
        assertTrue(XSLFormat.isTruncationRequired("This is a test", 13, 0));
        assertTrue(XSLFormat.isTruncationRequired("This is a test", 1, 0));

        // Check linefeed count
        assertFalse(XSLFormat.isTruncationRequired("This\nis a test", 0, 2));
        assertTrue(XSLFormat.isTruncationRequired("This\nis a test", 0, 1));
        assertFalse(XSLFormat.isTruncationRequired("This\nis\na\ntest", 0, 4));
        assertTrue(XSLFormat.isTruncationRequired("This\nis\na\ntest", 0, 3));

    }
}
