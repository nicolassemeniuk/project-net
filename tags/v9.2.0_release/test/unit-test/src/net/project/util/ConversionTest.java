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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;

public class ConversionTest extends TestCase {
    public ConversionTest(String s) {
        super(s);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        Application.login();
        TestSuite suite = new TestSuite(ConversionTest.class);
        return suite;
    }

    public void testToCommaSeparatedList() {
        List stringList = new ArrayList();

        //Case 1: zero strings in the list
        assertEquals("", Conversion.toCommaSeparatedString(stringList));

        //Case 2: one string in the list
        stringList.add("apple");
        assertEquals("apple", Conversion.toCommaSeparatedString(stringList));
/*
        //Case 3: two strings in the list
        stringList.add("banana");
        assertEquals("apple and banana", Conversion.toCommaSeparatedString(stringList));

        //Case 4: three strings in the list
        stringList.add("carrot");
        assertEquals("apple, banana, and carrot", Conversion.toCommaSeparatedString(stringList));

        //Case 5: four strings in the list
        stringList.add("dirt");
        assertEquals("apple, banana, carrot, and dirt", Conversion.toCommaSeparatedString(stringList));
*/
        //Case 6: null list
        assertEquals("", Conversion.toCommaSeparatedString(null));
    }
}
