package net.project.test.acceptance;

import junit.framework.AssertionFailedError;
import net.project.test.acceptance.engine.PnetTestEngine;

// Vladimir Malykhin

public class PersonalHomeTest extends PnetTestEngine {
	
	@Override
	protected void tearDown() throws Exception {
		_framework.getDataCleaner().removeActualObjects();
		_framework.logout();
		super.tearDown();
	}
	
	public void testPersonalWorkSpaceMainPage() throws Exception {
		// E4E_Pnet_UIpersonal_01
		
		_framework.testPersonalHomePageSmoke();
	}

	public void testPersonalWorkSpaceHelpButton() throws Exception {
		// E4E_Pnet_UIpersonal_05

		assertWindowCountEquals(1);
		try {
			assertWindowPresentWithTitle("Project.net Help");
			throw new RuntimeException("Must throw exception");
		} catch (AssertionFailedError e) {
		}
		assertWindowPresentWithTitle("Project.net");

		_framework.clickActionHelp();

		assertWindowCountEquals(2);
		assertWindowPresentWithTitle("Project.net Help");

		gotoWindowByTitle("Project.net Help");
		_framework.testHelpPage();
		
		closeWindow();
	}

	public void testPersonalActivityChannel() throws Exception {
		// E4E_Pnet_UIpersonal_09

		assertTextPresent("Personal Planned Activity Metrics");
		assertTextPresent("Metric");
		assertTextPresent("Assignments in progress:");
		assertTextPresent("Assignments you've completed:");
		assertTextPresent("Meetings scheduled:");
		assertTextPresent("Activity Totals:");
		assertTextPresent("This Week");
		assertTextPresent("This Month");

		// TODO:
	}

	public void testHideShowPersonalActivityChannel() throws Exception {
		// E4E_Pnet_UIpersonal_10
		// E4E_Pnet_UIpersonal_11

		clickLink(_framework.PS_CHANNEL_ID_ASSIGNMENT_METRICS);
		assertTextPresent("Personal Planned Activity Metrics");
		assertTextNotPresent("Assignments in progress:");
		assertTextNotPresent("Assignments you've completed:");
		assertTextNotPresent("Meetings scheduled:");
		assertTextNotPresent("Activity Totals:");
		assertTextNotPresent("This Week");
		assertTextNotPresent("This Month");

		clickLink(_framework.PS_CHANNEL_ID_ASSIGNMENT_METRICS);
		assertTextPresent("Personal Planned Activity Metrics");
		assertTextPresent("Metric");
		assertTextPresent("Assignments in progress:");
		assertTextPresent("Assignments you've completed:");
		assertTextPresent("Meetings scheduled:");
		assertTextPresent("Activity Totals:");
		assertTextPresent("This Week");
		assertTextPresent("This Month");
	}

	public void testClosePersonalActivityChannel() throws Exception {
		// E4E_Pnet_UIpersonal_12

		clickLink(_framework.PS_CHANNEL_CLOSE_ID_PREFIX
				+ _framework.PS_CHANNEL_ID_ASSIGNMENT_METRICS);
		assertTextNotPresent("Personal Planned Activity Metrics");
		assertTextNotPresent("Metric");
		assertTextNotPresent("Assignments in progress:");
		assertTextNotPresent("Assignments you've completed:");
		assertTextNotPresent("Meetings scheduled:");
		assertTextNotPresent("Activity Totals:");
		assertTextNotPresent("This Week");
		assertTextNotPresent("This Month");

		clickLinkWithExactText("Personalize this Page");
		assertFormPresent("main");
		setWorkingForm("main");
		assertCheckboxNotSelected(_framework.PS_CHANNEL_MANAGER_ID_PREFIX
				+ _framework.PS_CHANNEL_MANAGER_ID_ASSIGNMENT_METRICS);
		checkCheckbox(_framework.PS_CHANNEL_MANAGER_ID_PREFIX
				+ _framework.PS_CHANNEL_MANAGER_ID_ASSIGNMENT_METRICS);
		_framework.clickSubmitActionbarButton();

		assertTextPresent("Personal Planned Activity Metrics");
		assertTextPresent("Metric");
		assertTextPresent("Assignments in progress:");
		assertTextPresent("Assignments you've completed:");
		assertTextPresent("Meetings scheduled:");
		assertTextPresent("Activity Totals:");
		assertTextPresent("This Week");
		assertTextPresent("This Month");
	}

	public void testAssignmentsInProgressChannel() throws Exception {
		// E4E_Pnet_UIpersonal_13

		assertTextPresent("Assignments - In Progress, Late, or Starting within 2 Days");
		assertTextPresent("Assignment");
		assertTextPresent("Workspace");
		assertTextPresent("Start Date");
		assertTextPresent("Due Date");
		assertTextPresent("My Work");
		assertTextPresent("My Work Complete");
		assertTextPresent("My % Complete");
		assertTextPresent("Last Updated");

		final String newTaskName = _framework.createNewTask();
		_framework.goToPersonal();
		assertLinkPresent(_framework.CURRENT_ASSIGNMENTS_ID_PREFIX
				+ newTaskName);

		//
	}

	public void testAssignmentsInProgressHelpButton() throws Exception {
		// E4E_Pnet_UIpersonal_15

		assertWindowCountEquals(1);
		try {
			assertWindowPresentWithTitle("Project.net Help");
			throw new RuntimeException("Must throw exception");
		} catch (AssertionFailedError e) {
		}
		assertWindowPresentWithTitle("Project.net");

		_framework.clickChannelbarHelpButton();

		assertWindowCountEquals(2);
		assertWindowPresentWithTitle("Project.net Help");

		gotoWindowByTitle("Project.net Help");
		_framework.testHelpPage();
		// TODO: actually it must be current_assignments_channel.jsp
		
		closeWindow();
	}

	public void testHideShowAssignmentsInProgressChannel() throws Exception {
		// E4E_Pnet_UIpersonal_16
		// E4E_Pnet_UIpersonal_17

		final String newTaskName = _framework.createNewTask();
		_framework.goToPersonal();
		assertLinkPresent(_framework.CURRENT_ASSIGNMENTS_ID_PREFIX
				+ newTaskName);

		clickLink(_framework.PS_CHANNEL_ID_CURRENT_ASSIGNMENTS);
		assertTextPresent("Assignments - In Progress, Late, or Starting within 2 Days");
		assertTextNotPresent("Workspace");
		assertTextNotPresent("Start Date");
		assertTextNotPresent("Due Date");
		assertTextNotPresent("My Work");
		assertTextNotPresent("My Work Complete");
		assertTextNotPresent("My % Complete");
		assertTextNotPresent("Last Updated");
		assertLinkNotPresent(_framework.CURRENT_ASSIGNMENTS_ID_PREFIX
				+ newTaskName);

		clickLink(_framework.PS_CHANNEL_ID_CURRENT_ASSIGNMENTS);
		assertTextPresent("Assignments - In Progress, Late, or Starting within 2 Days");
		assertTextPresent("Workspace");
		assertTextPresent("Start Date");
		assertTextPresent("Due Date");
		assertTextPresent("My Work");
		assertTextPresent("My Work Complete");
		assertTextPresent("My % Complete");
		assertTextPresent("Last Updated");
		assertLinkPresent(_framework.CURRENT_ASSIGNMENTS_ID_PREFIX
				+ newTaskName);
	}

	public void testClickAssignmentsInProgressTaskLink() throws Exception {
		// E4E_Pnet_UIpersonal_18
		// E4E_Pnet_UIpersonal_20

		final String newTaskName = _framework.createNewTask();

		_framework.goToPersonal();

		clickLink(_framework.CURRENT_ASSIGNMENTS_ID_PREFIX + newTaskName);
		assertLinkNotPresentWithExactText(newTaskName);
		assertTextPresent(newTaskName);

		_framework.testViewAssignmentsPage();

		_framework.clickCancelActionbarButton();
		_framework.testPersonalHomePageSmoke();
	}

	public void testCloseAssignmentsInProgressChannel() throws Exception {
		// E4E_Pnet_UIpersonal_19

		final String newTaskName = _framework.createNewTask();

		_framework.goToPersonal();
		assertLinkPresent(_framework.CURRENT_ASSIGNMENTS_ID_PREFIX
				+ newTaskName);
		assertTextPresent("Assignments - In Progress, Late, or Starting within 2 Days");
		assertTextPresent("Workspace");
		assertTextPresent("Start Date");
		assertTextPresent("Due Date");
		assertTextPresent("My Work");
		assertTextPresent("My Work Complete");
		assertTextPresent("My % Complete");
		assertTextPresent("Last Updated");

		clickLink(_framework.PS_CHANNEL_CLOSE_ID_PREFIX
				+ _framework.PS_CHANNEL_ID_CURRENT_ASSIGNMENTS);
		assertLinkNotPresent(_framework.CURRENT_ASSIGNMENTS_ID_PREFIX
				+ newTaskName);
		assertTextNotPresent("Assignments - In Progress, Late, or Starting within 2 Days");
		assertTextNotPresent("Workspace");
		assertTextNotPresent("Start Date");
		assertTextNotPresent("Due Date");
		assertTextNotPresent("My Work");
		assertTextNotPresent("My Work Complete");
		assertTextNotPresent("My % Complete");
		assertTextNotPresent("Last Updated");

		clickLinkWithExactText("Personalize this Page");
		assertFormPresent("main");
		setWorkingForm("main");
		assertCheckboxNotSelected(_framework.PS_CHANNEL_MANAGER_ID_PREFIX
				+ _framework.PS_CHANNEL_MANAGER_ID_CURRENT_ASSIGNMENTS);
		checkCheckbox(_framework.PS_CHANNEL_MANAGER_ID_PREFIX
				+ _framework.PS_CHANNEL_MANAGER_ID_CURRENT_ASSIGNMENTS);
		_framework.clickSubmitActionbarButton();

		assertLinkPresent(_framework.CURRENT_ASSIGNMENTS_ID_PREFIX
				+ newTaskName);
		assertTextPresent("Assignments - In Progress, Late, or Starting within 2 Days");
		assertTextPresent("Workspace");
		assertTextPresent("Start Date");
		assertTextPresent("Due Date");
		assertTextPresent("My Work");
		assertTextPresent("My Work Complete");
		assertTextPresent("My % Complete");
		assertTextPresent("Last Updated");
	}

	public void testMyBusinessChannel() throws Exception {
		// E4E_Pnet_UIpersonal_21

		assertTextPresent("My Businesses");
		assertTextPresent("Business");
		assertTextPresent("Business Type");
		assertTextPresent("People");

		final String newBusinessName = _framework.createNewBusiness();			
		
		_framework.goToPersonal();
		assertLinkPresentWithExactText(newBusinessName);
	}
	
	public void testHideShowMyBusinessChannel() throws Exception {
		// E4E_Pnet_UIpersonal_22
		// E4E_Pnet_UIpersonal_23

		final String newBusinessName = _framework.createNewBusiness();
		_framework.goToPersonal();
		assertLinkPresentWithExactText(newBusinessName);

		clickLink(_framework.PS_CHANNEL_ID_MY_BUSINESS_LIST);
		assertTextPresent("My Businesses");
		assertTextNotPresent("Business Type");
		assertTextNotPresent("People");
		assertLinkNotPresentWithExactText(newBusinessName);

		clickLink(_framework.PS_CHANNEL_ID_MY_BUSINESS_LIST);
		assertTextPresent("My Businesses");
		assertTextPresent("Business Type");
		assertTextPresent("People");
		assertLinkPresentWithExactText(newBusinessName);
	}

	public void testClickBusinessLink() throws Exception {
		// E4E_Pnet_UIpersonal_24

		final String newBusinessName = _framework.createNewBusiness();

		_framework.goToPersonal();

		clickLinkWithExactText(newBusinessName);
		assertLinkNotPresentWithExactText(newBusinessName);
		assertTextPresent(newBusinessName);

		_framework.testBusinessPage();
	}

	public void testCloseMyBusinessChannel() throws Exception {
		// E4E_Pnet_UIpersonal_25

		final String newBusinessName = _framework.createNewBusiness();

		_framework.goToPersonal();
		assertLinkPresentWithExactText(newBusinessName);
		assertTextPresent("My Businesses");
		assertTextPresent("Business Type");
		assertTextPresent("People");

		clickLink(_framework.PS_CHANNEL_CLOSE_ID_PREFIX
				+ _framework.PS_CHANNEL_ID_MY_BUSINESS_LIST);
		assertLinkNotPresentWithExactText(newBusinessName);
		assertTextNotPresent("My Businesses");
		assertTextNotPresent("Business Type");
		assertTextNotPresent("People");

		clickLinkWithExactText("Personalize this Page");
		assertFormPresent("main");
		setWorkingForm("main");
		assertCheckboxNotSelected(_framework.PS_CHANNEL_MANAGER_ID_PREFIX
				+ _framework.PS_CHANNEL_MANAGER_ID_MY_BUSINESS_LIST);
		checkCheckbox(_framework.PS_CHANNEL_MANAGER_ID_PREFIX
				+ _framework.PS_CHANNEL_MANAGER_ID_MY_BUSINESS_LIST);
		_framework.clickSubmitActionbarButton();

		assertLinkPresentWithExactText(newBusinessName);
		assertTextPresent("My Businesses");
		assertTextPresent("Business Type");
		assertTextPresent("People");
	}

	public void testMyProjectsChannel() throws Exception {
		// E4E_Pnet_UIpersonal_26

		assertTextPresent("My Projects");
		assertTextPresent("Project");
		assertTextPresent("Responsibilities");
		assertTextPresent("Status");

		final String newProjectName = _framework.createNewProject();
		_framework.goToPersonal();
		assertLinkPresentWithExactText(newProjectName);
	}

	public void testHideShowMyProjectsChannel() throws Exception {
		// E4E_Pnet_UIpersonal_27
		// E4E_Pnet_UIpersonal_28

		final String newProjectName = _framework.createNewProject();
		_framework.goToPersonal();
		assertLinkPresentWithExactText(newProjectName);

		clickLink(_framework.PS_CHANNEL_ID_PROJECTS);
		assertTextPresent("My Projects");
		assertTextNotPresent("Responsibilities");
		assertLinkNotPresentWithExactText(newProjectName);

		clickLink(_framework.PS_CHANNEL_ID_PROJECTS);
		assertTextPresent("My Projects");
		assertTextPresent("Responsibilities");
		assertLinkPresentWithExactText(newProjectName);
	}

	public void testClickProjectLink() throws Exception {
		// E4E_Pnet_UIpersonal_29

		final String newProjectName = _framework.createNewProject();

		_framework.goToPersonal();

		clickLinkWithExactText(newProjectName);
		assertLinkNotPresentWithExactText(newProjectName);
		assertTextPresent(newProjectName);

		_framework.testProjectPage();
	}

	public void testClickBusinessLinkOfProject() throws Exception {
		// E4E_Pnet_UIpersonal_30

		final String newParentBusinessName = _framework.createNewBusiness();
		final String newProjectName = _framework.createNewProject(
				"testProject", newParentBusinessName);

		_framework.goToPersonal();

		clickLink(newProjectName + '_' + newParentBusinessName);
		assertLinkNotPresentWithExactText(newParentBusinessName);
		assertTextPresent(newParentBusinessName);
		assertLinkPresentWithExactText(newProjectName);

		_framework.testBusinessPage();
	}

	public void testCloseMyProjectChannel() throws Exception {
		// E4E_Pnet_UIpersonal_31

		final String newProjectName = _framework.createNewProject();

		_framework.goToPersonal();
		assertLinkPresentWithExactText(newProjectName);
		assertTextPresent("My Projects");
		assertTextPresent("Responsibilities");

		clickLink(_framework.PS_CHANNEL_CLOSE_ID_PREFIX
				+ _framework.PS_CHANNEL_ID_PROJECTS);
		assertLinkNotPresentWithExactText(newProjectName);
		assertTextNotPresent("My Projects");
		assertTextNotPresent("Responsibilities");

		clickLinkWithExactText("Personalize this Page");
		assertFormPresent("main");
		setWorkingForm("main");
		assertCheckboxNotSelected(_framework.PS_CHANNEL_MANAGER_ID_PREFIX
				+ _framework.PS_CHANNEL_MANAGER_ID_PROJECTS);
		checkCheckbox(_framework.PS_CHANNEL_MANAGER_ID_PREFIX
				+ _framework.PS_CHANNEL_MANAGER_ID_PROJECTS);
		_framework.clickSubmitActionbarButton();

		assertLinkPresentWithExactText(newProjectName);
		assertTextPresent("My Projects");
		assertTextPresent("Responsibilities");
	}

	public void NOtestUpcomingMeetingsChannel() throws Exception {
		// E4E_Pnet_UIpersonal_40

		assertTextPresent("Upcoming Meetings");
		assertTextPresent("Meeting");
		assertTextPresent("Date");
		assertTextPresent("Start");
		assertTextPresent("End");
		assertTextPresent("Status");

		final String newMeetingName = _framework.createNewMeeting();
		_framework.goToPersonal();
		assertLinkPresentWithExactText(newMeetingName);
	}

	public void testDocumentsCheckedOutByMeChannel() throws Exception {
		// E4E_Pnet_UIpersonal_45

		assertTextPresent("Documents Checked Out By Me");
		assertTextPresent("Name");
		assertTextPresent("Space");
		assertTextPresent("Format");
		assertTextPresent("Status");
		assertTextPresent("C/O Date");
		assertTextPresent("Est. Return");

		final String newDocumentName = _framework.createNewDocumentAndCkeckOut();
		_framework.goToPersonal();
		assertLinkPresentWithExactText(newDocumentName);
	}

	public void testHideShowDocumentsCheckedOutByMeChannel() throws Exception {
		// E4E_Pnet_UIpersonal_46
		// E4E_Pnet_UIpersonal_47

		final String newDocumentName = _framework.createNewDocumentAndCkeckOut();
		_framework.goToPersonal();
		assertLinkPresentWithExactText(newDocumentName);

		clickLink(_framework.PS_CHANNEL_ID_MY_CHECKED_OUT_DOCS);
		assertTextPresent("Documents Checked Out By Me");
		assertTextNotPresent("Name");
		assertTextNotPresent("Format");
		assertTextNotPresent("C/O Date");
		assertTextNotPresent("Est. Return");
		assertLinkNotPresentWithExactText(newDocumentName);

		clickLink(_framework.PS_CHANNEL_ID_MY_CHECKED_OUT_DOCS);
		assertTextPresent("Documents Checked Out By Me");
		assertTextPresent("Name");
		assertTextPresent("Format");
		assertTextPresent("C/O Date");
		assertTextPresent("Est. Return");
		assertLinkPresentWithExactText(newDocumentName);
	}

	public void testClickDocumentCheckedOutByMeLink() throws Exception {
		// E4E_Pnet_UIpersonal_48

		final String newDocumentName = _framework.createNewDocumentAndCkeckOut();

		_framework.goToPersonal();

		clickLinkWithExactText(newDocumentName);
		assertLinkPresentWithExactText(newDocumentName);

		_framework.testDocumentPage();
	}

	public void testCloseDocumentsCheckedOutByMeChannel() throws Exception {
		// E4E_Pnet_UIpersonal_49

		final String newDocumentName = _framework.createNewDocumentAndCkeckOut();

		_framework.goToPersonal();
		assertLinkPresentWithExactText(newDocumentName);
		assertTextPresent("Documents Checked Out By Me");
		assertTextPresent("Name");
		assertTextPresent("Format");
		assertTextPresent("C/O Date");
		assertTextPresent("Est. Return");

		clickLink(_framework.PS_CHANNEL_CLOSE_ID_PREFIX
				+ _framework.PS_CHANNEL_ID_MY_CHECKED_OUT_DOCS);
		assertLinkNotPresentWithExactText(newDocumentName);
		assertTextNotPresent("Documents Checked Out By Me");
		assertTextNotPresent("Name");
		assertTextNotPresent("Format");
		assertTextNotPresent("C/O Date");
		assertTextNotPresent("Est. Return");

		clickLinkWithExactText("Personalize this Page");
		assertFormPresent("main");
		setWorkingForm("main");
		assertCheckboxNotSelected(_framework.PS_CHANNEL_MANAGER_ID_PREFIX
				+ _framework.PS_CHANNEL_MANAGER_ID_MY_CHECKED_OUT_DOCS);
		checkCheckbox(_framework.PS_CHANNEL_MANAGER_ID_PREFIX
				+ _framework.PS_CHANNEL_MANAGER_ID_MY_CHECKED_OUT_DOCS);
		_framework.clickSubmitActionbarButton();

		assertLinkPresentWithExactText(newDocumentName);
		assertTextPresent("Documents Checked Out By Me");
		assertTextPresent("Name");
		assertTextPresent("Format");
		assertTextPresent("C/O Date");
		assertTextPresent("Est. Return");
	}

	public void testPersonalWorkSpaceSearchButton() throws Exception {
		// E4E_Pnet_UIpersonal_04
	
		_framework.clickActionSearch();
	
		assertTextPresent("Search");
	
		assertTextPresent("Search Type:");
		assertFormPresent("TYPE_PICK");
		setWorkingForm("TYPE_PICK");
		assertSelectOptionsPresent("otype", _framework.SEARCH_TYPES);
	
		assertTextPresent("Quick Search");
		assertTextPresent("Keyword:");
		assertFormPresent("ObjSearch");
		setWorkingForm("ObjSearch");
		assertFormElementPresent("KEYWORD");
	
		assertLinkPresentWithExactText("Cancel");
		assertLinkPresentWithImage(_framework.ACTIONBAR_BUTTON_CANCEL);
		assertLinkPresentWithExactText("Search");
		assertLinkPresentWithImage("/images/icons/actionbar-search_off.gif");
	
		setTextField("KEYWORD", "someSearchQuery");
		clickLinkWithImage("/images/icons/actionbar-search_off.gif");
		assertTextPresent("Search Results");
		assertLinkPresentWithExactText("New Search");
		assertLinkPresentWithImage("/images/icons/actionbar-search_off.gif");
		assertLinkPresentWithExactText("Cancel");
		assertLinkPresentWithImage(_framework.ACTIONBAR_BUTTON_CANCEL);
		_framework.clickCancelActionbarButton();
		assertTitleEquals("Project.net");
		assertLinkNotPresentWithImage("/images/icons/actionbar-search_off.gif");
	
		// TODO: it must be extended to careful Search queries test
	}

	public void testWorkflowInboxChannel() throws Exception {
		// E4E_Pnet_UIpersonal_50

		assertTextPresent("Workflow Inbox");
		assertTextPresent("Title");
		assertTextPresent("Description");
		assertTextPresent("Current Step");
		assertTextPresent("Current Status");
		assertTextPresent("Last Changed");
		
		final String newWorkflowName = _framework.addDocumentToWokflowInbox();
		
		_framework.goToPersonal();
		assertLinkPresentWithExactText(newWorkflowName);
	}

	public void testHideShowWorkflowInboxChannel() throws Exception {
		// E4E_Pnet_UIpersonal_51
		// E4E_Pnet_UIpersonal_52

		final String newWorkflowName = _framework.addDocumentToWokflowInbox();
		_framework.goToPersonal();
		assertLinkPresentWithExactText(newWorkflowName);
		
		clickLink(_framework.PS_CHANNEL_ID_MY_ENVELOPE_LIST);
		assertTextPresent("Workflow Inbox");
		assertTextNotPresent("Description");
		assertTextNotPresent("Current Step");
		assertTextNotPresent("Current Status");
		assertTextNotPresent("Last Changed");
		assertLinkNotPresentWithExactText(newWorkflowName);
		
		clickLink(_framework.PS_CHANNEL_ID_MY_ENVELOPE_LIST);
		assertTextPresent("Workflow Inbox");
		assertTextPresent("Description");
		assertTextPresent("Current Step");
		assertTextPresent("Current Status");
		assertTextPresent("Last Changed");
		assertLinkPresentWithExactText(newWorkflowName);
		
	}
	
	public void testClickPersonalizeThisPageLink() throws Exception {
		// E4E_Pnet_UIpersonal_55

		clickLinkWithExactText("Personalize this Page");
		_framework.testPersonalizeThisPagePage();

		//assertFormPresent("main"); setWorkingForm("main");
		//assertCheckboxNotSelected(_framework.PS_CHANNEL_MANAGER_ID_PREFIX +
			 //_framework.PS_CHANNEL_MANAGER_ID_ASSIGNMENTS);
		//checkCheckbox(_framework.PS_CHANNEL_MANAGER_ID_PREFIX +
			 //_framework.PS_CHANNEL_MANAGER_ID_ASSIGNMENTS);
		//_framework.clickSubmitActionbarButton();

	}
	
	public void testCreateProjectTasksStress() throws Exception {
		final String newProjectName = 
			_framework.createNewProject("testProjectWithTasksStress");
		for (int i = 0; i < 3; i++) {
			_framework.createNewTask("testTask", newProjectName);			
		}
	}
}

/*package net.project.test.acceptance;

import junit.framework.AssertionFailedError;
import net.project.test.acceptance.engine.PnetTestEngine;
import net.project.test.acceptance.framework.PnetTestFramework;

// Vladimir Malykhin

public class PersonalHomeTest extends PnetTestEngine {

	PnetTestFramework _framework;
	
	public PersonalHomeTest() {
		super();
		_framework = new PnetTestFramework(super.getTester());
	}

	@Override
	public void setUp() throws Exception {
		tester = _framework.getTester();
		_framework.login();
	}

	@Override
	protected void tearDown() throws Exception {
		_framework.logout();
		super.tearDown();
	}
	
*/
