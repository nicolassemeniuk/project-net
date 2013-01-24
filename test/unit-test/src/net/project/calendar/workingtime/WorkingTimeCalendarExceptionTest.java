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
|     $RCSfile$
|    $Revision: 15404 $
|        $Date: 2006-08-28 20:20:09 +0530 (Mon, 28 Aug 2006) $
|      $Author: deepak $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.calendar.workingtime;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests <code>WorkingTimeCalendarExceptionTest</code>.
 * @author
 * @since
 */
public class WorkingTimeCalendarExceptionTest extends TestCase {

    public WorkingTimeCalendarExceptionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(WorkingTimeCalendarExceptionTest.class);

        return suite;
    }

    /**
     * Tests the constructors.
     */
    public void testInit() {

        // WorkingTimeCalendarException(String)
        assertNull(new WorkingTimeCalendarException(null).getMessage());
        assertEquals("Test", new WorkingTimeCalendarException("Test").getMessage());

        // WorkingTimeCalendarException(String, Throwable)
        Exception e = new Exception();
        assertNull(new WorkingTimeCalendarException(null, e).getMessage());
        assertNull(new WorkingTimeCalendarException(null, null).getCause());
        assertEquals("Test", new WorkingTimeCalendarException("Test", e).getMessage());
        assertEquals(e, new WorkingTimeCalendarException("Test", e).getCause());

    }

}
