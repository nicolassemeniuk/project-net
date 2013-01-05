package net.project.schedule.importer.mvc;

import java.util.TimeZone;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;
import net.project.base.Module;
import net.project.mockobjects.MockHttpServletRequest;
import net.project.mockobjects.MockHttpServletResponse;
import net.project.mockobjects.MockHttpSession;
import net.project.mockobjects.MockRequestDispatcher;
import net.project.schedule.Schedule;
import net.project.schedule.importer.XMLImporter;
import net.project.security.Action;
import net.project.security.SessionManager;

/**
 * Tests <code>ImportControllerTest</code>.
 * 
 */
public class ImportControllerTest extends TestCase {

	public ImportControllerTest(String testName) {
		super(testName);
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(ImportControllerTest.class);

		return suite;
	}

	protected void setUp() throws Exception {
		super.setUp();
		Application.registerServlet("/servlet/ImportController");
		Application.login();
	}

	public void testControlRequestUnknown() {

		MockHttpServletRequest request;
		MockHttpServletResponse response = new MockHttpServletResponse();

		request = Application.requestPage("/servlet/ImportController/Unknown?action=" + Action.VIEW + "&module="
				+ Module.SCHEDULE);
		try {
			new ImportController().controlRequest(request, response);
			fail("Unexpected success with pathInfo of 'Unknown'");
		} catch (Exception e) {
			// Expected
		}

	}

	/*public void testControlRequestChooseFile() throws Exception {

		MockHttpServletRequest request;
		MockHttpServletResponse response = new MockHttpServletResponse();

		request = Application.requestPage("/servlet/ImportController/ChooseFile?action=" + Action.MODIFY + "&module="
				+ Module.SCHEDULE);
        SessionManager.getUser().setAuthenticated(true);
		new ImportController().controlRequest(request, response);
		assertEquals("/schedule/importer/ChooseFile.jsp", getCreatedRequestDispatcher(request).getRequestedPath());
	}*/

	/*public void testControlRequestChooseItemsToImport() throws Exception {

		MockHttpServletRequest request;
		MockHttpServletResponse response = new MockHttpServletResponse();

		// Missing session attribute ScheduleImporter

		request = Application.requestPage("/servlet/ImportController/ChooseItemsToImport?action=" + Action.MODIFY
				+ "&module=" + Module.SCHEDULE);
        SessionManager.getUser().setAuthenticated(true);
		removeSessionAttribute(request, "scheduleImporter");
		removeSessionAttribute(request, "schedule");
		setSessionAttribute(request, "schedule", new Schedule());
		try {
			new ImportController().controlRequest(request, response);
			fail("Unexpected success with missing session attribute 'scheduleImporter'");
		} catch (Exception e) {
			// Expected
		}

		// Missing session attribute schedule
		request = Application.requestPage("/servlet/ImportController/ChooseItemsToImport?action=" + Action.MODIFY
				+ "&module=" + Module.SCHEDULE);
		removeSessionAttribute(request, "scheduleImporter");
		removeSessionAttribute(request, "schedule");
		setSessionAttribute(request, "scheduleImporter", new XMLImporter());
		try {
			new ImportController().controlRequest(request, response);
			fail("Unexpected success with missing session attribute 'schedule'");
		} catch (Exception e) {
			// Expected
		}

		// Both attributes present
		Schedule schedule = new Schedule();
        schedule.setID("1");
        schedule.setTimeZone(TimeZone.getDefault());
		XMLImporter importer = new XMLImporter();
		request = Application.requestPage("/servlet/ImportController/ChooseItemsToImport?action=" + Action.MODIFY
				+ "&module=" + Module.SCHEDULE);
		removeSessionAttribute(request, "scheduleImporter");
		removeSessionAttribute(request, "schedule");
		setSessionAttribute(request, "schedule", schedule);
		setSessionAttribute(request, "scheduleImporter", importer);
		new ImportController().controlRequest(request, response);
		assertEquals("/schedule/importer/ChooseItemsToImport.jsp", getCreatedRequestDispatcher(request).getRequestedPath());

	}*/

	/*public void testControlRequestMapResources() throws Exception {

		MockHttpServletRequest request;
		MockHttpServletResponse response = new MockHttpServletResponse();

		// Missing session attribute ScheduleImporter

		request = Application.requestPage("/servlet/ImportController/MapResources?action=" + Action.MODIFY + "&module="
				+ Module.SCHEDULE);
        SessionManager.getUser().setAuthenticated(true);
		removeSessionAttribute(request, "scheduleImporter");
		removeSessionAttribute(request, "schedule");
		setSessionAttribute(request, "schedule", new Schedule());
		try {
			new ImportController().controlRequest(request, response);
			fail("Unexpected success with missing session attribute 'scheduleImporter'");
		} catch (Exception e) {
			// Expected
		}

		// Missing session attribute schedule
		request = Application.requestPage("/servlet/ImportController/ChooseItemsToImport?action=" + Action.MODIFY
				+ "&module=" + Module.SCHEDULE);
		removeSessionAttribute(request, "scheduleImporter");
		removeSessionAttribute(request, "schedule");
		setSessionAttribute(request, "scheduleImporter", new XMLImporter());
		try {
			new ImportController().controlRequest(request, response);
			fail("Unexpected success with missing session attribute 'schedule'");
		} catch (Exception e) {
			// Expected
		}

		// Both attributes present
		Schedule schedule = new Schedule();
        schedule.setID("1");
        schedule.setTimeZone(TimeZone.getDefault());
		XMLImporter importer = new XMLImporter();
		request = Application.requestPage("/servlet/ImportController/ChooseItemsToImport?action=" + Action.MODIFY
				+ "&module=" + Module.SCHEDULE);
		removeSessionAttribute(request, "scheduleImporter");
		removeSessionAttribute(request, "schedule");
		setSessionAttribute(request, "schedule", schedule);
		setSessionAttribute(request, "scheduleImporter", importer);
		new ImportController().controlRequest(request, response);
		assertEquals("/schedule/importer/ChooseItemsToImport.jsp", getCreatedRequestDispatcher(request).getRequestedPath());

	}*/

	private MockRequestDispatcher getCreatedRequestDispatcher(MockHttpServletRequest request) {
		return (MockRequestDispatcher) request.getCreatedDispatchers().get(0);
	}

	private void setSessionAttribute(MockHttpServletRequest request, String name, Object value) {
		MockHttpSession session = (MockHttpSession) request.getSession();
		session.setAttribute(name, value);
	}

	private void removeSessionAttribute(MockHttpServletRequest request, String name) {
		MockHttpSession session = (MockHttpSession) request.getSession();
		session.removeAttribute(name);
	}

}
