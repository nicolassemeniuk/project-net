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
package net.project.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junitx.framework.ArrayAssert;

/**
 * This class tests the functions in the {@link net.project.util.ParseString}
 * class.
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
public class ParseStringTest extends TestCase {

    public ParseStringTest(String testName) {
        super(testName);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(ParseStringTest.class);
        
        return suite;
    }

    public void testIsEmpty() {

        assertTrue(ParseString.isEmpty(null));
        assertTrue(ParseString.isEmpty(""));
        assertTrue(ParseString.isEmpty(" "));
        assertTrue(ParseString.isEmpty("     "));
        assertTrue(ParseString.isEmpty(" \t\t\n\r\t  "));

        assertFalse(ParseString.isEmpty("x"));
        assertFalse(ParseString.isEmpty("  x  "));
        assertFalse(ParseString.isEmpty("\tx\t"));

    }

    public void testGetFileExt() {

        // Empty string and no period
        assertEquals("", FileUtils.getFileExt(null));
        assertEquals("", FileUtils.getFileExt(""));
        assertEquals("", FileUtils.getFileExt(" "));
        assertEquals("", FileUtils.getFileExt("x"));

        // Period with no extension
        assertEquals("", FileUtils.getFileExt("."));
        assertEquals("", FileUtils.getFileExt("a."));
        assertEquals("", FileUtils.getFileExt("a.b.c.d."));

        // Period with extension
        assertEquals("x", FileUtils.getFileExt(".x"));
        assertEquals("xxx", FileUtils.getFileExt(".xxx"));
        assertEquals("x", FileUtils.getFileExt("a.x"));
        assertEquals("x", FileUtils.getFileExt("a.b.c.d.e.x"));

    }

    public void testEscapeDoubleQuotes() {

        // Empty string and no quotes
        assertEquals("", ParseString.escapeDoubleQuotes(null));
        assertEquals("", ParseString.escapeDoubleQuotes(""));
        assertEquals("abc", ParseString.escapeDoubleQuotes("abc"));

        // Test escaping
        assertEquals("\"\"", ParseString.escapeDoubleQuotes("\""));
        assertEquals("a\"\"b\"\"c", ParseString.escapeDoubleQuotes("a\"b\"c"));
        assertEquals("\"\"abc\"\"", ParseString.escapeDoubleQuotes("\"abc\""));

        // Ensure single quotes are not touched
        assertEquals("''", ParseString.escapeDoubleQuotes("''"));

        // Test removal of \r
        assertEquals("", ParseString.escapeDoubleQuotes("\r"));
        assertEquals("\n", ParseString.escapeDoubleQuotes("\r\n"));
        assertEquals("abc", ParseString.escapeDoubleQuotes("\rabc"));
        assertEquals("abc", ParseString.escapeDoubleQuotes("abc\r"));
        assertEquals("abc", ParseString.escapeDoubleQuotes("\rab\rc\r"));
        assertEquals("\"\"abc\"\"", ParseString.escapeDoubleQuotes("\r\"abc\""));
        assertEquals("\"\"\"\"", ParseString.escapeDoubleQuotes("\"\r\""));
    }

    public void testMakeArrayFromCommaDelimitedString() {

        try {
            assertNull(ParseString.makeArrayFromCommaDelimitedString(null));
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        ArrayAssert.assertEquals(new String[]{}, ParseString.makeArrayFromCommaDelimitedString(""));
        ArrayAssert.assertEquals(new String[]{}, ParseString.makeArrayFromCommaDelimitedString(" "));
        ArrayAssert.assertEquals(new String[]{}, ParseString.makeArrayFromCommaDelimitedString("  "));
        ArrayAssert.assertEquals(new String[]{}, ParseString.makeArrayFromCommaDelimitedString("   "));
        ArrayAssert.assertEquals(new String[]{}, ParseString.makeArrayFromCommaDelimitedString(","));
        ArrayAssert.assertEquals(new String[]{}, ParseString.makeArrayFromCommaDelimitedString(",,,,,"));
        ArrayAssert.assertEquals(new String[]{}, ParseString.makeArrayFromCommaDelimitedString("  ,  ,,  ,,  "));

        ArrayAssert.assertEquals(new String[]{"x"}, ParseString.makeArrayFromCommaDelimitedString("x"));
        ArrayAssert.assertEquals(new String[]{"x"}, ParseString.makeArrayFromCommaDelimitedString(" x"));
        ArrayAssert.assertEquals(new String[]{"x"}, ParseString.makeArrayFromCommaDelimitedString("  x"));
        ArrayAssert.assertEquals(new String[]{"x"}, ParseString.makeArrayFromCommaDelimitedString("x "));
        ArrayAssert.assertEquals(new String[]{"x"}, ParseString.makeArrayFromCommaDelimitedString("x  "));
        ArrayAssert.assertEquals(new String[]{"x"}, ParseString.makeArrayFromCommaDelimitedString(" x "));
        ArrayAssert.assertEquals(new String[]{"x"}, ParseString.makeArrayFromCommaDelimitedString(", x ,"));

        ArrayAssert.assertEquals(new String[]{"x","x"}, ParseString.makeArrayFromCommaDelimitedString("x,x"));
        ArrayAssert.assertEquals(new String[]{"x","x"}, ParseString.makeArrayFromCommaDelimitedString(" x,x"));
        ArrayAssert.assertEquals(new String[]{"x","x"}, ParseString.makeArrayFromCommaDelimitedString("x ,x"));
        ArrayAssert.assertEquals(new String[]{"x","x"}, ParseString.makeArrayFromCommaDelimitedString("x, x"));
        ArrayAssert.assertEquals(new String[]{"x","x"}, ParseString.makeArrayFromCommaDelimitedString("x,x "));
        ArrayAssert.assertEquals(new String[]{"x","x"}, ParseString.makeArrayFromCommaDelimitedString(" x, x"));
        ArrayAssert.assertEquals(new String[]{"x","x"}, ParseString.makeArrayFromCommaDelimitedString("x ,x "));
        ArrayAssert.assertEquals(new String[]{"x","x"}, ParseString.makeArrayFromCommaDelimitedString(" x , x "));
        ArrayAssert.assertEquals(new String[]{"x","x"}, ParseString.makeArrayFromCommaDelimitedString(" x , x ,"));
        ArrayAssert.assertEquals(new String[]{"x","x"}, ParseString.makeArrayFromCommaDelimitedString(", ,  x , ,  x , ,"));

    }
}
