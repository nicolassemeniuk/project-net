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

/**
 * Unit test for the StringUtils utility class.
 *
 * @author Matthew Flower
 * @since Version 7.6.4
 */
public class StringUtilsTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public StringUtilsTest(String s) {
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
     * Construct a <code>TestSuite</code> containing the test that this unit
     * test believes it is comprised of.  In most cases, it is only the current
     * class, though it can include others.
     * 
     * @return a <code>Test</code> object which is really a TestSuite of unit
     *         tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(StringUtilsTest.class);
        return suite;
    }

    public void testStringOfChar() {
        String stringOfSpaces = "     ";
        String blankString = StringUtils.stringOfChar(5, ' ');
        assertEquals(5, blankString.length());
        assertEquals(stringOfSpaces, blankString);

        String stringOfAs = "A";
        assertEquals(stringOfAs, StringUtils.stringOfChar(1, 'A'));

        String emptyString = "";
        assertEquals(emptyString, StringUtils.stringOfChar(0, 'B'));
    }

    public void testRPad() {
        String initialString = "AAA";

        assertEquals("AAA   ", StringUtils.rpad(initialString, 6));
        assertEquals("AAA", StringUtils.rpad(initialString, 3));
        assertEquals("AAA", StringUtils.rpad(initialString, 2));
    }
}
