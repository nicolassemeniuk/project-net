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
 /*
 * TimeQuantityUnitTest.java
 * JUnit based test
 *
 * Created on November 26, 2002, 2:30 PM
 */

package net.project.util;

import java.math.BigDecimal;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Class to unit test the TimeQuantityUnit class.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class TimeQuantityUnitTest extends TestCase {
    public TimeQuantityUnitTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(TimeQuantityUnitTest.class);
        
        return suite;
    }
    
    /** Test of getForID method, of class net.project.util.TimeQuantityUnit. */
    public void testGetForID() {

        // null
        try {
            TimeQuantityUnit.getForID(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        // Invalid number
        try {
            TimeQuantityUnit.getForID("x");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Invalid unit
        try {
            TimeQuantityUnit.getForID("1234567");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        //Test each possible value to make sure the proper thing is returned
        assertEquals(TimeQuantityUnit.getForID(TimeQuantityUnit.MILLISECOND.getUniqueID()), TimeQuantityUnit.MILLISECOND);
        assertEquals(TimeQuantityUnit.getForID(TimeQuantityUnit.SECOND.getUniqueID()), TimeQuantityUnit.SECOND);
        assertEquals(TimeQuantityUnit.getForID(TimeQuantityUnit.MINUTE.getUniqueID()), TimeQuantityUnit.MINUTE);
        assertEquals(TimeQuantityUnit.getForID(TimeQuantityUnit.HOUR.getUniqueID()), TimeQuantityUnit.HOUR);
        assertEquals(TimeQuantityUnit.getForID(TimeQuantityUnit.DAY.getUniqueID()), TimeQuantityUnit.DAY);
        assertEquals(TimeQuantityUnit.getForID(TimeQuantityUnit.WEEK.getUniqueID()), TimeQuantityUnit.WEEK);
        assertEquals(TimeQuantityUnit.getForID(TimeQuantityUnit.STANDARD_WORK_DAY.getUniqueID()), TimeQuantityUnit.STANDARD_WORK_DAY);
        assertEquals(TimeQuantityUnit.getForID(TimeQuantityUnit.STANDARD_WORK_WEEK.getUniqueID()), TimeQuantityUnit.STANDARD_WORK_WEEK);
    }
    
    /** Test of getHTMLOptionList method, of class net.project.util.TimeQuantityUnit. */
    public void testGetHTMLOptionList() {
        //Can't test this until we have a stub for SessionManager
        //assertNotNull(TimeQuantityUnit.getHTMLOptionList(TimeQuantityUnit.DAY));
    }
    
    /** Test of getBase method, of class net.project.util.TimeQuantityUnit. */
    public void testGetBase() {
        assertEquals(TimeQuantityUnit.MILLISECOND.getBase(), new BigDecimal("0.001"));
        assertEquals(TimeQuantityUnit.SECOND.getBase(), new BigDecimal(1));
        assertEquals(TimeQuantityUnit.MINUTE.getBase(), new BigDecimal(60));
        assertEquals(TimeQuantityUnit.HOUR.getBase(), new BigDecimal(3600));
        assertEquals(TimeQuantityUnit.DAY.getBase(), new BigDecimal(86400));
        assertEquals(TimeQuantityUnit.WEEK.getBase(), new BigDecimal(604800));
        assertEquals(TimeQuantityUnit.STANDARD_WORK_DAY.getBase(), new BigDecimal(28800));
        assertEquals(TimeQuantityUnit.STANDARD_WORK_WEEK.getBase(), new BigDecimal(144000));
    }
    
    /** Test of equals method, of class net.project.util.TimeQuantityUnit. */
    public void testEquals() {
        assertTrue(TimeQuantityUnit.WEEK.equals(TimeQuantityUnit.WEEK));
        assertTrue(!TimeQuantityUnit.WEEK.equals(TimeQuantityUnit.DAY));
    }

    public void testGetSmallerUnit() {
        assertEquals(TimeQuantityUnit.WEEK, TimeQuantityUnit.MONTH.getSmallerUnit());
        assertEquals(TimeQuantityUnit.DAY, TimeQuantityUnit.WEEK.getSmallerUnit());
        assertEquals(TimeQuantityUnit.HOUR, TimeQuantityUnit.DAY.getSmallerUnit());
        assertEquals(TimeQuantityUnit.MINUTE, TimeQuantityUnit.HOUR.getSmallerUnit());
        assertEquals(TimeQuantityUnit.SECOND, TimeQuantityUnit.MINUTE.getSmallerUnit());
        assertEquals(TimeQuantityUnit.MILLISECOND, TimeQuantityUnit.SECOND.getSmallerUnit());
        assertNull(TimeQuantityUnit.MILLISECOND.getSmallerUnit());
    }

    public void testGetLargerUnit() {
        assertEquals(TimeQuantityUnit.SECOND, TimeQuantityUnit.MILLISECOND.getLargerUnit());
        assertEquals(TimeQuantityUnit.MINUTE, TimeQuantityUnit.SECOND.getLargerUnit());
        assertEquals(TimeQuantityUnit.HOUR,  TimeQuantityUnit.MINUTE.getLargerUnit());
        assertEquals(TimeQuantityUnit.DAY, TimeQuantityUnit.HOUR.getLargerUnit());
        assertEquals(TimeQuantityUnit.WEEK, TimeQuantityUnit.DAY.getLargerUnit());
        assertEquals(TimeQuantityUnit.MONTH, TimeQuantityUnit.WEEK.getLargerUnit());
        assertNull(TimeQuantityUnit.MONTH.getLargerUnit());
    }
}
