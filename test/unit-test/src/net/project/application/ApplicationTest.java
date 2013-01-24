/*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 11474 $
|       $Date: 2003-07-24 04:28:19 +0530 (Thu, 24 Jul 2003) $
|     $Author: matt $
|
+-----------------------------------------------------------------------------*/
package net.project.application;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import javax.servlet.http.HttpServletRequest;
import net.project.security.Action;
import net.project.base.Module;

public class ApplicationTest extends TestCase {
    /**
     * Constructs a test case with the given name.
     * 
     * @param s a <code>String</code> containing the name of this test.
     */
    public ApplicationTest(String s) {
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
     * tests. 
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(ApplicationTest.class);
        return suite;
    }

    protected void setUp() throws Exception {
        Application.login();
    }

    public void testRequestPage1() {
        Application.registerServlet("/servlet/ScheduleController");

        HttpServletRequest request = Application.requestPage(
            "/servlet/ScheduleController/TaskView?action="+Action.VIEW+"&module="+
            Module.SCHEDULE);
        assertEquals("/servlet/ScheduleController/TaskView", request.getRequestURI());
        assertEquals("/TaskView", request.getPathInfo());
        assertEquals(String.valueOf(Action.VIEW), request.getParameter("action"));
        assertEquals(String.valueOf(Module.SCHEDULE), request.getParameter("module"));
        assertNotNull(request.getSession());
    }

    public void testRequestPage2() {
        HttpServletRequest request = Application.requestPage(
            "/schedule/TaskView.jsp?action="+Action.VIEW+"&module="+Module.SCHEDULE+
            "&id=123"
        );
        assertEquals("/schedule/TaskView.jsp", request.getRequestURI());
        assertEquals("", request.getPathInfo());
        assertEquals(String.valueOf(Action.VIEW), request.getParameter("action"));
        assertEquals(String.valueOf(Module.SCHEDULE), request.getParameter("module"));
        assertEquals("123", request.getParameter("id"));
        assertNotNull(request.getSession());
    }

}
