package net.project.schedule.importer;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.admin.ApplicationSpace;
import net.project.application.Application;
import net.project.calendar.workingtime.WorkingTimeCalendarDefinition;
import net.project.database.DBBean;
import net.project.database.ObjectManager;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;
import net.project.resource.Person;
import net.project.resource.PersonStatus;
import net.project.resource.Roster;
import net.project.resource.ScheduleEntryAssignment;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskDependency;
import net.project.schedule.TestWorkingTimeCalendarProvider;
import net.project.schedule.calc.TaskCalculationType;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.test.util.TestProperties;
import net.project.util.NumberUtils;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.utils.WellKnownObjects;

public class XMLImporterTest extends TestCase {
	/**
	 * This unfortunate flag is necessary because we don't really have a linux
	 * driver available everywhere to run tests with. Once we can start using
	 * mdbtools-java.jar to read our mdb files, we can re-enable this so it will
	 * always be tested.
	 */
	private final boolean ENABLE_TESTS = false;

	/**
	 * Constructs a test case with the given name.
	 * 
	 * @param s
	 *            a <code>String</code> containing the name of this test.
	 */
	public XMLImporterTest(String s) {
		super(s);
	}

	/**
	 * Direct route to unit test this object from the command line.
	 * 
	 * @param args
	 *            a <code>String[]</code> value which contains the command
	 *            line options. (These will be unused.)
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	/**
	 * Construct a <code>TestSuite</code> containing the test that this unit
	 * test believes it is comprised of. In most cases, it is only the current
	 * class, though it can include others.
	 * 
	 * @return a <code>TestSuite</code> object which is really a TestSuite of unit
	 *         tests.
	 */
	public static TestSuite suite() {
		TestSuite suite = new TestSuite(XMLImporterTest.class);
		return suite;
	}

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 */
	protected void setUp() throws Exception {
		Application.login();

		// Ensure the plan is created
		WellKnownObjects.ensurePlan(WellKnownObjects.TEST_SPACE_ID, WellKnownObjects.TEST_PLAN_ID);

		SessionManager.getUser().setCurrentSpace(ApplicationSpace.DEFAULT_APPLICATION_SPACE);
	}

	public void testTRIPMpd() throws SQLException, PersistenceException, ImportException {
		if (!ENABLE_TESTS)
			return;

		XMLImporter importer = new XMLImporter();
		String fileName = TestProperties.getInstance().getProperty("testFilePath") + File.separatorChar + "trip.mpd";
		assertTrue("Test MPD File " + fileName + " does not exist.", new File(fileName).isFile());
		importer.setFileName(fileName);

		importer.setImportAssignments(true);
		importer.setImportResourceWorkingTimeCalendars(true);
		importer.setImportTasks(true);
		importer.setImportWorkingTimeCalendars(true);
		importer.setMSProjectID(2);

		// Test Schedule
		Schedule schedule = new Schedule();
		schedule.setName("Test Plan");
		schedule.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
		schedule.setID(WellKnownObjects.TEST_PLAN_ID);
		// We use a custom working time calendar provider that always
		// returns the default working time calendar
		schedule.setWorkingTimeCalendarProvider(TestWorkingTimeCalendarProvider.make(WorkingTimeCalendarDefinition
				.makeDefaultWorkingTimeCalendarDefinition()));
		importer.setSchedule(schedule);

		importer.load();
//		importer.loadProjects();

		// Needed to load resources
		importer.createResourceResolver();

		importer.prepareImport();
		importer.importSchedule();

		Map storedTasks = importer.getTaskMap();
		ScheduleEntry mspSeq1 = (ScheduleEntry) storedTasks.get(new Integer("127"));

		// Test that the first task has the information in it that we expect
		assertEquals("Training Rollout Initiative and Plan (TRIP)", mspSeq1.getName());
		assertEquals(new TimeQuantity(806, TimeQuantityUnit.HOUR), mspSeq1.getWorkTQ());
		assertEquals(new TimeQuantity(157.5, TimeQuantityUnit.DAY), mspSeq1.getDurationTQ());
		assertEquals(new BigDecimal("0.12744"), mspSeq1.getWorkPercentCompleteDecimal());
		// assertEquals(1, mspSeq1.getSequenceNumber());

		ScheduleEntry mspSeq2 = (ScheduleEntry) storedTasks.get(new Integer("4"));
		assertEquals("TRIP - Stage 1 - Project Planning", mspSeq2.getName());
		assertEquals(new TimeQuantity(364, TimeQuantityUnit.HOUR), mspSeq2.getWorkTQ());
		assertEquals(new TimeQuantity(19, TimeQuantityUnit.DAY), mspSeq2.getDurationTQ());
		// assertEquals(2, mspSeq2.getSequenceNumber());

		ScheduleEntry mspSeq3 = (ScheduleEntry) storedTasks.get(new Integer("58"));
		assertEquals("Define project objectives and describe mission statement", mspSeq3.getName());
		assertEquals(new TimeQuantity(16, TimeQuantityUnit.HOUR), mspSeq3.getWorkTQ());
		assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), mspSeq3.getDurationTQ());
		// assertEquals(3, mspSeq3.getSequenceNumber());

		ScheduleEntry mspSeq4 = (ScheduleEntry) storedTasks.get(new Integer("71"));
		assertEquals("Ensure total participation by training team support staff", mspSeq4.getName());
		assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), mspSeq4.getWorkTQ());
		assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), mspSeq4.getDurationTQ());
		// assertEquals(4, mspSeq4.getSequenceNumber());

		ScheduleEntry mspSeq5 = (ScheduleEntry) storedTasks.get(new Integer("5"));
		assertEquals("Identify Departments or End-User Community to be Trained", mspSeq5.getName());
		assertEquals(new TimeQuantity(124, TimeQuantityUnit.HOUR), mspSeq5.getWorkTQ());
		assertEquals(new TimeQuantity(7, TimeQuantityUnit.DAY), mspSeq5.getDurationTQ());
		// assertEquals(5, mspSeq5.getSequenceNumber());

		ScheduleEntry mspSeq6 = (ScheduleEntry) storedTasks.get(new Integer("126"));
		assertEquals("Create list of target departments", mspSeq6.getName());
		assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), mspSeq6.getWorkTQ());
		assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), mspSeq6.getDurationTQ());
		// assertEquals(6, mspSeq6.getSequenceNumber());
		// Check dependencies
		assertTrue(mspSeq6.getPredecessors().size() > 0);
		TaskDependency tdSeq6 = mspSeq6.getPredecessors().get(0);
		assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), tdSeq6.getLag());
		assertEquals(mspSeq3.getID(), tdSeq6.getDependencyID());

		ScheduleEntry mspSeq7 = (ScheduleEntry) storedTasks.get(new Integer("67"));
		assertEquals("Perform departmental training needs analysis", mspSeq7.getName());
		assertEquals(new TimeQuantity(56, TimeQuantityUnit.HOUR), mspSeq7.getWorkTQ());
		assertEquals(new TimeQuantity(7, TimeQuantityUnit.DAY), mspSeq7.getDurationTQ());
		// assertEquals(7, mspSeq7.getSequenceNumber());
		// Check dependencies
		assertTrue(mspSeq7.getPredecessors().size() > 0);
		TaskDependency tdSeq7 = mspSeq7.getPredecessors().get(0);
		assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), tdSeq7.getLag());
		assertEquals(mspSeq4.getID(), tdSeq7.getDependencyID());

		ScheduleEntry mspSeq8 = (ScheduleEntry) storedTasks.get(new Integer("73"));
		assertEquals("Compile results and present findings to training coordinator", mspSeq8.getName());
		assertEquals(new TimeQuantity(16, TimeQuantityUnit.HOUR), mspSeq8.getWorkTQ());
		assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), mspSeq8.getDurationTQ());
		// assertEquals(8, mspSeq8.getSequenceNumber());
		// Check dependencies
		assertTrue(mspSeq8.getPredecessors().size() > 0);
		TaskDependency tdSeq8 = mspSeq8.getPredecessors().get(0);
		assertEquals(new TimeQuantity(-4, TimeQuantityUnit.DAY), tdSeq8.getLag());
		assertEquals(mspSeq7.getID(), tdSeq8.getDependencyID());

		ScheduleEntry mspSeq9 = (ScheduleEntry) storedTasks.get(new Integer("68"));
		assertEquals("Prioritize training delivery according to critical need", mspSeq9.getName());
		assertEquals(new TimeQuantity(16, TimeQuantityUnit.HOUR), mspSeq9.getWorkTQ());
		assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), mspSeq9.getDurationTQ());
		// assertEquals(9, mspSeq9.getSequenceNumber());
		// Check dependencies
		assertTrue(mspSeq9.getPredecessors().size() > 0);
		TaskDependency tdSeq9 = mspSeq9.getPredecessors().get(0);
		assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), tdSeq9.getLag());
		assertEquals(mspSeq8.getID(), tdSeq9.getDependencyID());

		ScheduleEntry mspSeq10 = (ScheduleEntry) storedTasks.get(new Integer("76"));
		assertEquals("Inform department heads of the training initiative", mspSeq10.getName());
		assertEquals(new TimeQuantity(16, TimeQuantityUnit.HOUR), mspSeq10.getWorkTQ());
		assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), mspSeq10.getDurationTQ());
		// assertEquals(10, mspSeq10.getSequenceNumber());
		// Check dependencies
		assertTrue(mspSeq10.getPredecessors().size() > 0);
		TaskDependency tdSeq10 = mspSeq10.getPredecessors().get(0);
		assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), tdSeq10.getLag());
		assertEquals(mspSeq6.getID(), tdSeq10.getDependencyID());

		ScheduleEntry mspSeq11 = (ScheduleEntry) storedTasks.get(new Integer("72"));
		assertEquals("Determine if training can be done in-house or outsourced", mspSeq11.getName());
		assertEquals(new TimeQuantity(12, TimeQuantityUnit.HOUR), mspSeq11.getWorkTQ());
		assertEquals(new TimeQuantity(1.5, TimeQuantityUnit.DAY), mspSeq11.getDurationTQ());
		// assertEquals(11, mspSeq11.getSequenceNumber());
		// Check dependencies
		assertTrue(mspSeq11.getPredecessors().size() > 0);
		TaskDependency tdSeq11 = mspSeq11.getPredecessors().get(0);
		assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), tdSeq11.getLag());
		assertEquals(mspSeq6.getID(), tdSeq11.getDependencyID());

		ScheduleEntry mspSeq12 = (ScheduleEntry) storedTasks.get(new Integer("20"));
		assertEquals("Seek Input from Training Vendor", mspSeq12.getName());
		assertEquals(new TimeQuantity(104, TimeQuantityUnit.HOUR), mspSeq12.getWorkTQ());
		assertEquals(new TimeQuantity(11, TimeQuantityUnit.DAY), mspSeq12.getDurationTQ());
		// assertEquals(12, mspSeq12.getSequenceNumber());

		ScheduleEntry mspSeq13 = (ScheduleEntry) storedTasks.get(new Integer("61"));
		assertEquals("Define vendor deliverables - can they meet our needs?", mspSeq13.getName());
		assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), mspSeq13.getWorkTQ());
		assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), mspSeq13.getDurationTQ());
		// assertEquals(13, mspSeq13.getSequenceNumber());
		// Check dependencies
		assertTrue(mspSeq13.getPredecessors().size() > 0);
		TaskDependency tdSeq13 = mspSeq13.getPredecessors().get(0);
		assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), tdSeq13.getLag());
		assertEquals(mspSeq11.getID(), tdSeq13.getDependencyID());

		ScheduleEntry mspSeq14 = (ScheduleEntry) storedTasks.get(new Integer("60"));
		assertEquals("Obtain vendor commitment to training rollout schedule", mspSeq14.getName());
		assertEquals(new TimeQuantity(24, TimeQuantityUnit.HOUR), mspSeq14.getWorkTQ());
		assertEquals(new TimeQuantity(3, TimeQuantityUnit.DAY), mspSeq14.getDurationTQ());
		// assertEquals(14, mspSeq14.getSequenceNumber());
		// Check dependencies
		assertTrue(mspSeq14.getPredecessors().size() > 0);
		TaskDependency tdSeq14 = mspSeq14.getPredecessors().get(0);
		assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), tdSeq14.getLag());
		assertEquals(mspSeq13.getID(), tdSeq14.getDependencyID());

		ScheduleEntry mspSeq15 = (ScheduleEntry) storedTasks.get(new Integer("70"));
		assertEquals("Review and customize training material", mspSeq15.getName());
		assertEquals(new TimeQuantity(56, TimeQuantityUnit.HOUR), mspSeq15.getWorkTQ());
		assertEquals(new TimeQuantity(7, TimeQuantityUnit.DAY), mspSeq15.getDurationTQ());
		// assertEquals(15, mspSeq15.getSequenceNumber());
		// Check dependencies
		assertTrue(mspSeq15.getPredecessors().size() == 2);
		// Check first task dependency
		TaskDependency tdSeq15 = mspSeq15.getPredecessors().get(0);
		assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), tdSeq15.getLag());
		assertEquals(mspSeq13.getID(), tdSeq15.getDependencyID());
		// Check second task dependency
		tdSeq15 = mspSeq15.getPredecessors().get(1);
		assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), tdSeq15.getLag());
		assertEquals(mspSeq14.getID(), tdSeq15.getDependencyID());

		ScheduleEntry mspSeq16 = (ScheduleEntry) storedTasks.get(new Integer("59"));
		assertEquals("Obtain approval for purchase orders to cover vendor invoices", mspSeq16.getName());
		assertEquals(new TimeQuantity(16, TimeQuantityUnit.HOUR), mspSeq16.getWorkTQ());
		assertEquals(new TimeQuantity(2, TimeQuantityUnit.DAY), mspSeq16.getDurationTQ());
		// assertEquals(16, mspSeq16.getSequenceNumber());
		// Check dependencies
		assertTrue(mspSeq16.getPredecessors().size() > 0);
		TaskDependency tdSeq16 = mspSeq16.getPredecessors().get(0);
		assertEquals(new TimeQuantity(0, TimeQuantityUnit.DAY), tdSeq16.getLag());
		assertEquals(mspSeq14.getID(), tdSeq16.getDependencyID());

		ScheduleEntry mspSeq17 = (ScheduleEntry) storedTasks.get(new Integer("8"));
		assertEquals("Perform End-User Skill Assessment", mspSeq17.getName());
		assertEquals(new TimeQuantity(112, TimeQuantityUnit.HOUR), mspSeq17.getWorkTQ());
		assertEquals(new TimeQuantity(11, TimeQuantityUnit.DAY), mspSeq17.getDurationTQ());
		// assertEquals(17, mspSeq17.getSequenceNumber());

		ScheduleEntry mspSeq24 = (ScheduleEntry) storedTasks.get(new Integer("17"));
		assertEquals("TRIP - Stage 2 - Project Commencement", mspSeq24.getName());
		assertEquals(new TimeQuantity(112, TimeQuantityUnit.HOUR), mspSeq24.getWorkTQ());
		assertEquals(new TimeQuantity(14, TimeQuantityUnit.DAY), mspSeq24.getDurationTQ());

		/*
		 * We aren't able to make this test case work yet. It relies on us being
		 * able to have duration without having work. We can't do this yet.
		 */
		// ScheduleEntry mspSeq25 = (ScheduleEntry)storedTasks.get(new
		// Integer("81"));
		// assertEquals("Prepare Rollout Schedule and Create Awareness",
		// mspSeq25.getName());
		// assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR),
		// mspSeq25.getWorkTQ());
		// assertEquals(new TimeQuantity(12, TimeQuantityUnit.DAY),
		// mspSeq25.getDurationTQ());
		ScheduleEntry mspSeq79 = (ScheduleEntry) storedTasks.get(new Integer("128"));
		assertEquals("Milestone 1", mspSeq79.getName());
		assertEquals(new TimeQuantity(0, TimeQuantityUnit.HOUR), mspSeq79.getWorkTQ());
		assertEquals(new TimeQuantity(1, TimeQuantityUnit.DAY), mspSeq79.getDurationTQ());
		assertEquals(new BigDecimal("1.00000"), mspSeq79.getPercentCompleteDecimal());

		// Test that all non-summary tasks are Fixed Unit, Effort Driven
		// for (Iterator iterator = storedTasks.values().iterator();
		// iterator.hasNext();) {
		// ScheduleEntry nextScheduleEntry = (ScheduleEntry) iterator.next();
		// if (nextScheduleEntry.getTaskType().equals(TaskType.TASK)) {
		// assertEquals("Task id, name: " + nextScheduleEntry.getID() + ", " +
		// nextScheduleEntry.getName(),
		// TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN,
		// nextScheduleEntry.getTaskCalculationType());
		// }
		// }

	}

	/**
	 * Tests MPD import for a variety of Default Task Type / New Task Effort
	 * driven schedule option values.
	 */
	public void testScheduleDefaultTaskType() throws SQLException, PersistenceException, ImportException {
		if (!ENABLE_TESTS)
			return;

		File file = getFile("TaskTypesTest.mpd");

		Schedule schedule = new Schedule();
		schedule.setID(WellKnownObjects.TEST_PLAN_ID);
		schedule.load();
		schedule.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

		// Project 1 - Fixed Unit, Effort Driven
		XMLImporter importer = createImporterForScheduleDefaultTaskTypeTest(file, 1, schedule);
		importScheduleForScheduleDefaultTaskTypeTest(importer);
		assertEquals(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN, schedule.getDefaultTaskCalculationType());

		// Project 2 - Fixed Duration, Effort Driven
		importer = createImporterForScheduleDefaultTaskTypeTest(file, 2, schedule);
		importScheduleForScheduleDefaultTaskTypeTest(importer);
		assertEquals(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN, schedule.getDefaultTaskCalculationType());

		// Project 3 - Fixed Work
		importer = createImporterForScheduleDefaultTaskTypeTest(file, 3, schedule);
		importScheduleForScheduleDefaultTaskTypeTest(importer);
		assertEquals(TaskCalculationType.FIXED_WORK, schedule.getDefaultTaskCalculationType());

		// Project 4 - Fixed Unit, Non-Effort Driven
		importer = createImporterForScheduleDefaultTaskTypeTest(file, 4, schedule);
		importScheduleForScheduleDefaultTaskTypeTest(importer);
		assertEquals(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN, schedule.getDefaultTaskCalculationType());

		// Project 5 - Fixed Duration, Non-Effort Driven
		importer = createImporterForScheduleDefaultTaskTypeTest(file, 5, schedule);
		importScheduleForScheduleDefaultTaskTypeTest(importer);
		assertEquals(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN, schedule.getDefaultTaskCalculationType());

	}

	/**
	 * Creates an importer that doesn't import anything other than the schedule
	 * itself.
	 * 
	 * @param mpdFile
	 *            the MPD file to import
	 * @param msProjectID
	 *            the id of the project to import
	 * @param schedule
	 *            the schedule to import into
	 * @return the importer for importing into that schedule
	 */
	private static XMLImporter createImporterForScheduleDefaultTaskTypeTest(File mpdFile, int msProjectID,
			Schedule schedule) {
		XMLImporter importer = new XMLImporter();
		importer.setImportAssignments(false);
		importer.setImportResourceWorkingTimeCalendars(false);
		importer.setImportTasks(false);
		importer.setImportWorkingTimeCalendars(false);
		importer.setFileName(mpdFile.getAbsolutePath());
		importer.setMSProjectID(msProjectID);
		importer.setSchedule(schedule);
		return importer;
	}

	/**
	 * Imports the schedule.
	 * 
	 * @param importer
	 *            the importer to use to import
	 * @throws SQLException
	 * @throws PersistenceException
	 * @throws ImportException
	 */
	private static void importScheduleForScheduleDefaultTaskTypeTest(XMLImporter importer) throws SQLException,
			PersistenceException, ImportException {
		importer.load();
//		importer.loadProjects();
		importer.prepareImport();
		importer.importSchedule();
	}

	/**
	 * Tests import of various task types (Fixed Unit, Fixed Duration, Fixed
	 * Work; Effort Driven, Non-effort Driven).
	 * 
	 * @throws SQLException
	 * @throws PersistenceException
	 * @throws ImportException
	 */
	public void testTaskTypes() throws SQLException, PersistenceException, ImportException {
		if (!ENABLE_TESTS)
			return;

		File file = getFile("TaskTypesTest.mpd");

		Schedule schedule = new Schedule();
		schedule.setID(WellKnownObjects.TEST_PLAN_ID);
		schedule.load();
		schedule.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

		XMLImporter importer = new XMLImporter();
		importer.setImportAssignments(false);
		importer.setImportResourceWorkingTimeCalendars(false);
		importer.setImportTasks(true);
		importer.setImportWorkingTimeCalendars(false);
		importer.setFileName(file.getAbsolutePath());
		importer.setMSProjectID(6);
		importer.setSchedule(schedule);
		importer.load();
//		importer.loadProjects();
		importer.prepareImport();
		importer.importSchedule();

		Map storedTasks = importer.getTaskMap();

		ScheduleEntry scheduleEntry;
		scheduleEntry = (ScheduleEntry) storedTasks.get(new Integer(1));
		assertEquals(TaskCalculationType.FIXED_UNIT_EFFORT_DRIVEN, scheduleEntry.getTaskCalculationType());

		scheduleEntry = (ScheduleEntry) storedTasks.get(new Integer(2));
		assertEquals(TaskCalculationType.FIXED_UNIT_NON_EFFORT_DRIVEN, scheduleEntry.getTaskCalculationType());

		scheduleEntry = (ScheduleEntry) storedTasks.get(new Integer(3));
		assertEquals(TaskCalculationType.FIXED_DURATION_EFFORT_DRIVEN, scheduleEntry.getTaskCalculationType());

		scheduleEntry = (ScheduleEntry) storedTasks.get(new Integer(4));
		assertEquals(TaskCalculationType.FIXED_DURATION_NON_EFFORT_DRIVEN, scheduleEntry.getTaskCalculationType());

		scheduleEntry = (ScheduleEntry) storedTasks.get(new Integer(5));
		assertEquals(TaskCalculationType.FIXED_WORK, scheduleEntry.getTaskCalculationType());
	}

	/**
	 * Tests import of task actual start / end dates.
	 * 
	 * @throws SQLException
	 * @throws PersistenceException
	 * @throws ImportException
	 */
	public void testActualDates() throws SQLException, PersistenceException, ImportException {
		if (!ENABLE_TESTS)
			return;

		File file = getFile("Test Import Actuals.mpd");

		Schedule schedule = new Schedule();
		schedule.setID(WellKnownObjects.TEST_PLAN_ID);
		schedule.load();
		schedule.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

		XMLImporter importer = new XMLImporter();
		importer.setImportAssignments(false);
		importer.setImportResourceWorkingTimeCalendars(false);
		importer.setImportTasks(true);
		importer.setImportWorkingTimeCalendars(false);
		importer.setFileName(file.getAbsolutePath());

		importer.setMSProjectID(1);
		importer.setSchedule(schedule);
		importer.load();
//		importer.loadProjects();
		importer.prepareImport();
		importer.importSchedule();

		Map storedTasks = importer.getTaskMap();

		ScheduleEntry scheduleEntry;

		// Task 1: 0% Complete, No actuals
		scheduleEntry = (ScheduleEntry) storedTasks.get(new Integer(1));
		assertEquals(makeDateTime("05/03/04 8:00 AM"), scheduleEntry.getStartTime());
		assertEquals(makeDateTime("05/04/04 5:00 PM"), scheduleEntry.getEndTime());
		assertNull(scheduleEntry.getActualStartTime());
		assertNull(scheduleEntry.getActualEndTime());

		// Task 2: 50% Complete, Actual Start, No Actual End
		scheduleEntry = (ScheduleEntry) storedTasks.get(new Integer(2));
		assertEquals(makeDateTime("05/03/04 8:00 AM"), scheduleEntry.getStartTime());
		assertEquals(makeDateTime("05/04/04 5:00 PM"), scheduleEntry.getEndTime());
		assertEquals(makeDateTime("05/03/04 8:00 AM"), scheduleEntry.getActualStartTime());
		assertNull(scheduleEntry.getActualEndTime());

		// Task 3: 100% Complete, Actual Start, Actual End
		scheduleEntry = (ScheduleEntry) storedTasks.get(new Integer(3));
		assertEquals(makeDateTime("05/03/04 8:00 AM"), scheduleEntry.getStartTime());
		assertEquals(makeDateTime("05/04/04 5:00 PM"), scheduleEntry.getEndTime());
		assertEquals(makeDateTime("05/03/04 8:00 AM"), scheduleEntry.getActualStartTime());
		assertEquals(makeDateTime("05/04/04 5:00 PM"), scheduleEntry.getActualEndTime());

	}

	/**
	 * Tests import of assignment work.
	 * 
	 * @throws SQLException
	 * @throws PersistenceException
	 * @throws ImportException
	 */
	public void testAssignmentWork() throws SQLException, PersistenceException, ImportException {
		if (!ENABLE_TESTS)
			return;

		File file = getFile("Test Assignment Work.mpd");

		Roster roster;
		Person person1, person2;
		Space space;
		Schedule schedule;
		ScheduleEntry scheduleEntry;
		ScheduleEntryAssignment assignment;
		XMLImporter importer;
		ResourceResolver resourceResolver;

		// Set up the space and persons
		space = new ProjectSpace(WellKnownObjects.TEST_SPACE_ID);
		roster = new Roster();
		roster.add((person1 = createPerson(1)));
		roster.add((person2 = createPerson(2)));
		ensureAddedToSpace(space, person1);
		ensureAddedToSpace(space, person2);

		schedule = new Schedule();
		schedule.setID(WellKnownObjects.TEST_PLAN_ID);
		schedule.load();
		schedule.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

		importer = new XMLImporter();
		importer.setImportAssignments(true);
		importer.setImportResourceWorkingTimeCalendars(false);
		importer.setImportTasks(true);
		importer.setImportWorkingTimeCalendars(false);
		importer.setFileName(file.getAbsolutePath());

		importer.setMSProjectID(1);
		importer.setSchedule(schedule);
		importer.setCurrentSpace(space);
		importer.setRoster(roster);
		importer.load();
//		importer.loadProjects();

		// Resolve resources:
		// MSP resource ID "1" = person 1
		// MSP resource ID "2" = person 1" (intentional same mapping)
		// MSP resource ID "3" = person 2
		resourceResolver = importer.getResourceResolver();
		resourceResolver.addResourceMapping(new Integer(1), Integer.valueOf(person1.getID()));
		resourceResolver.addResourceMapping(new Integer(2), Integer.valueOf(person1.getID()));
		resourceResolver.addResourceMapping(new Integer(3), Integer.valueOf(person2.getID()));

		importer.prepareImport();
		importer.importSchedule();
		assertFalse(importer.getErrorReporter().getErrorDescriptions().toString(), importer.getErrorReporter()
				.errorsFound());

		Map storedTasks = importer.getTaskMap();

		// Task 1: 1day, 8 hour task
		// MSP resource "1": 100%, 8 hours
		// Expected: Person "1" 100% 8 hours
		scheduleEntry = (ScheduleEntry) storedTasks.get(new Integer(1));
		scheduleEntry.load();
		assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), scheduleEntry.getWorkTQ());
		assignment = (ScheduleEntryAssignment) scheduleEntry.getAssignmentList().getForResourceID(person1.getID());
		assertEquals(new TimeQuantity(8, TimeQuantityUnit.HOUR), assignment.getWork());
		assertEquals(new BigDecimal("1.00"), assignment.getPercentAssignedDecimal());

		// Task 2: 1 day, 16 hour task
		// MSP resource "1" and "2" mapped to person ID "1"
		// Expected: Person "1" 200% 16 hours
		scheduleEntry = (ScheduleEntry) storedTasks.get(new Integer(2));
		scheduleEntry.load();
		assertEquals(new TimeQuantity(16, TimeQuantityUnit.HOUR), scheduleEntry.getWorkTQ());
		assignment = (ScheduleEntryAssignment) scheduleEntry.getAssignmentList().getForResourceID(person1.getID());
		assertEquals(new TimeQuantity(16, TimeQuantityUnit.HOUR), assignment.getWork());
		assertEquals(new BigDecimal("2.00"), assignment.getPercentAssignedDecimal());

		// Task 3: 1 day, 32 hour task, 4 assignments
		// MSP resource "1" and "2" mapped to person ID "1"
		// MSP resource "3" mapped to person ID "2"
		// MSP resource "4" not mapped
		// Expected: Person "1" 267% 21.33 hours
		// Person "2" 133% 10.67 hours
		scheduleEntry = (ScheduleEntry) storedTasks.get(new Integer(3));
		scheduleEntry.load();
		assertEquals(new TimeQuantity(32, TimeQuantityUnit.HOUR), scheduleEntry.getWorkTQ());
		assignment = (ScheduleEntryAssignment) scheduleEntry.getAssignmentList().getForResourceID(person1.getID());
		assertEquals(new TimeQuantity(new BigDecimal("21.3333333333"), TimeQuantityUnit.HOUR), assignment.getWork());
		assertEquals(new BigDecimal("2.67"), assignment.getPercentAssignedDecimal());
		assignment = (ScheduleEntryAssignment) scheduleEntry.getAssignmentList().getForResourceID(person2.getID());
		assertEquals(new TimeQuantity(new BigDecimal("10.6666666666"), TimeQuantityUnit.HOUR), assignment.getWork());
		assertEquals(new BigDecimal("1.33"), assignment.getPercentAssignedDecimal());

	}

	public void testWorkPercentComplete() throws SQLException, ImportException, PersistenceException {
		if (!ENABLE_TESTS)
			return;

		XMLImporter importer = new XMLImporter();
		importer.setImportAssignments(true);
		importer.setImportResourceWorkingTimeCalendars(false);
		importer.setImportTasks(true);
		importer.setImportWorkingTimeCalendars(true);

		File file = getFile("Test Work Percent Complete.mpd");
		importer.setFileName(file.getAbsolutePath());

		importer.setMSProjectID(1);
		Schedule schedule = new Schedule();
		schedule.setID(WellKnownObjects.TEST_PLAN_ID);
		schedule.load();
		schedule.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
		importer.setSchedule(schedule);

		Space space = new ProjectSpace(WellKnownObjects.TEST_SPACE_ID);
		importer.setCurrentSpace(space);

		Roster roster = new Roster();
		Person person1 = createPerson(1);
		roster.add(person1);
		ensureAddedToSpace(space, person1);
		importer.setRoster(roster);

		importer.load();
//		importer.loadProjects();

		ResourceResolver resourceResolver = importer.getResourceResolver();
		resourceResolver.addResourceMapping(new Integer(1), Integer.valueOf(person1.getID()));

		importer.prepareImport();
		importer.importSchedule();
		Map storedTasks = importer.getTaskMap();

		ScheduleEntry task1 = (ScheduleEntry) storedTasks.get(new Integer(1));
		task1.load();
		assertEquals("Task 1", task1.getName());
		assertEquals(0, task1.getAssignmentList().size());
		assertEquals(new BigDecimal(1), NumberUtils.dynamicScale(task1.getWorkPercentCompleteDecimal()));

		ScheduleEntry task2 = (ScheduleEntry) storedTasks.get(new Integer(2));
		task2.load();
		assertEquals("Task 2", task2.getName());
		assertEquals(new BigDecimal(1), NumberUtils.dynamicScale(task2.getWorkPercentCompleteDecimal()));
		assertEquals(1, task2.getAssignmentList().size());
		ScheduleEntryAssignment assignment = (ScheduleEntryAssignment) task2.getAssignmentList().getAssignments()
				.get(0);
		assertEquals(new BigDecimal(1), NumberUtils.dynamicScale(assignment.getPercentComplete()));

		ScheduleEntry task3 = (ScheduleEntry) storedTasks.get(new Integer(3));
		task3.load();
		assertEquals("Task 3", task3.getName());
		assertEquals(new BigDecimal(1), NumberUtils.dynamicScale(task3.getWorkPercentCompleteDecimal()));
		assertEquals(0, task3.getAssignmentList().size());

		ScheduleEntry task4 = (ScheduleEntry) storedTasks.get(new Integer(4));
		task4.load();
		assertEquals("Task 4", task4.getName());
		assertEquals(new BigDecimal(1), NumberUtils.dynamicScale(task4.getWorkPercentCompleteDecimal()));
		assertEquals(1, task4.getAssignmentList().size());
		assignment = (ScheduleEntryAssignment) task4.getAssignmentList().getAssignments().get(0);
		assertEquals(new BigDecimal(1), NumberUtils.dynamicScale(assignment.getPercentComplete()));
	}

	/**
	 * Fetches a file from the default test file location for the specified
	 * name.
	 * 
	 * @param fileName
	 *            the name of the file to get from the file path given by test
	 *            property <code>testFilePath</code>
	 * @return the File
	 * @throws junit.framework.AssertionFailedError
	 *             if the file cannot be found or is not readable
	 */
	private static File getFile(String fileName) {
		String pathToFile = TestProperties.getInstance().getProperty("testFilePath") + File.separatorChar + fileName;
		File file = new File(pathToFile);
		assertTrue("Test MPD File " + pathToFile + " does not exist.", file.isFile());
		assertTrue("Test MPD File " + pathToFile + " is not readable.", file.canRead());
		return file;
	}

	/**
	 * Parses a date in the short format for US locale assuming PST/PDT
	 * timezone.
	 * 
	 * @param dateTimeString
	 *            the date and time to parse
	 * @return the parsed date or null if there was a problem parsing
	 */
	private static Date makeDateTime(String dateTimeString) {
		Date date;

		try {
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
			df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
			date = df.parse(dateTimeString);
		} catch (ParseException e) {
			date = null;
		}

		return date;
	}

	/**
	 * Helper method to create a Person, stored in the database. <p/> The person
	 * has a unique email address, and display name.
	 * 
	 * @param seq
	 *            a number to help differentiate this person from another being
	 *            created at the same time; while all required unique attributes
	 *            will be unique anyway, this helps the caller identify users
	 * @return a person, with ID set
	 */
	private static Person createPerson(int seq) throws PersistenceException {
		String uniqueID = ObjectManager.getNewObjectID();

		// First create the person
		Person person = new Person();
		person.setEmail(seq + "." + uniqueID + ".unittest@project.net");
		person.setFirstName(seq + " Unit");
		person.setLastName(seq + " Test");
		person.setDisplayName(seq + "." + uniqueID + " Unit Test");
		person.setStatus(PersonStatus.UNREGISTERED);
		person.setTimeZone(TimeZone.getTimeZone("America/Los_angeles"));
		person.createStub();
		return person;
	}

	/**
	 * Ensures that the the specified person is added to the space with
	 * specified ID and adds them if not already. <p/> Persons must belong to a
	 * space to permit assignments to be created for that person.
	 * 
	 * @param space
	 *            the space to which to add
	 * @param person
	 *            the person to ensure added
	 */
	private static void ensureAddedToSpace(Space space, Person person) throws PersistenceException {

		DBBean db = new DBBean();
		try {

			db.prepareStatement("select 1 from pn_space_has_person where space_id = ? and person_id = ? ");
			db.pstmt.setString(1, space.getID());
			db.pstmt.setString(2, person.getID());
			db.executePrepared();
			if (db.result.next()) {
				// Already added to space
				return;
			}
			db.release();

			db
					.prepareStatement("insert into pn_space_has_person "
							+ "(space_id, person_id, relationship_person_to_space, relationship_space_to_person, responsibilities, record_status)"
							+ "values " + "(?, ?, 'member', 'has', null, 'A')");
			db.pstmt.setString(1, space.getID());
			db.pstmt.setString(2, person.getID());
			db.executePreparedUpdate();

		} catch (SQLException e) {
			throw new PersistenceException("Error associating person to space", e);

		} finally {
			db.release();

		}

	}
}
