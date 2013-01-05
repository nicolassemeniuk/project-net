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
|   $Revision: 17125 $
|       $Date: 2008-03-28 16:01:53 +0530 (Fri, 28 Mar 2008) $
|     $Author: sjmittal $
|
+-----------------------------------------------------------------------------*/
package net.project.base.mvc;

import java.io.ByteArrayInputStream;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.base.PnetRuntimeException;

public class HandlerMappingTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     *
     * @param s a <code>String</code> containing the name of this test.
     */
    public HandlerMappingTest(String s) {
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
        TestSuite suite = new TestSuite(HandlerMappingTest.class);
        return suite;
    }

    public void testHandlerMappingInvalidXML() {
        String xml =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<HandlerMapping>" +
            "<Class>" +
            "</Class>" +
            "</HandlerMapping>";
        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());

        HandlerMapping mapping = new HandlerMapping();
        mapping.setHandlerMap(null);
        try {
            mapping.ensureMap(bais);
            fail("Invalid XML structure not captured");
        } catch (PnetRuntimeException e) {}
        mapping.setHandlerMap(null);
    }

    public void testEmptyHandlerMapping() {
        String xml =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<HandlerMapping>" +
            "</HandlerMapping>";

        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
        HandlerMapping mapping = new HandlerMapping();
        mapping.setHandlerMap(null);
        mapping.ensureMap(bais);
        Map map = mapping.getHandlerMap();

        assertNotNull(map);
        assertTrue(map.size() == 0);
        mapping.setHandlerMap(null);
    }

    public void testHandlerMapping1Entry() {
        String xml =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<HandlerMapping>" +
            "<Handler url=\"/CreateShare\">"+
            "<Class>net.project.schedule.ScheduleEntry</Class>" +
            "</Handler>" +
            "</HandlerMapping>";

        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
        HandlerMapping mapping = new HandlerMapping();
        mapping.setHandlerMap(null);
        mapping.ensureMap(bais);
        Map map = mapping.getHandlerMap();

        assertNotNull(map);
        assertTrue(map.size() == 1);
        assertNotNull(map.get("/CreateShare"));
        assertTrue(map.get("/CreateShare") instanceof HandlerMapEntry);

        HandlerMapEntry entry = (HandlerMapEntry)map.get("/CreateShare");
        assertEquals("net.project.schedule.ScheduleEntry", entry.className);
        assertEquals("/CreateShare", entry.url);
        mapping.setHandlerMap(null);
    }

    public void testHandlerMapping2Entry() {
        String xml =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<HandlerMapping>" +
            "<Handler url=\"/CreateShare\">"+
            "<Class>net.project.schedule.ScheduleEntry</Class>" +
            "</Handler>" +
            "<Handler url=\"/DeleteShare\">"+
            "<Class>net.project.schedule.Schedule</Class>" +
            "</Handler>" +
            "</HandlerMapping>";

        ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
        HandlerMapping mapping = new HandlerMapping();
        mapping.setHandlerMap(null);
        mapping.ensureMap(bais);
        Map map = mapping.getHandlerMap();

        assertNotNull(map);
        assertTrue(map.size() == 2);

        assertNotNull(map.get("/CreateShare"));
        assertTrue(map.get("/CreateShare") instanceof HandlerMapEntry);
        HandlerMapEntry entry = (HandlerMapEntry)map.get("/CreateShare");
        assertEquals("net.project.schedule.ScheduleEntry", entry.className);
        assertEquals("/CreateShare", entry.url);

        assertNotNull(map.get("/DeleteShare"));
        assertTrue(map.get("/DeleteShare") instanceof HandlerMapEntry);
        entry = (HandlerMapEntry)map.get("/DeleteShare");
        assertEquals("net.project.schedule.Schedule", entry.className);
        assertEquals("/DeleteShare", entry.url);
        mapping.setHandlerMap(null);
    }
}
