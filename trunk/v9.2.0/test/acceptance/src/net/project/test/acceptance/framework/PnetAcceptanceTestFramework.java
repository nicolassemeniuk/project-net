package net.project.test.acceptance.framework;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.AssertionFailedError;
import net.project.test.acceptance.engine.PnetTestEngine;
import net.sourceforge.jwebunit.junit.WebTester;

// Vladimir Malykhin

public class PnetAcceptanceTestFramework extends PnetTestEngine {

	private static final String MENU_PERSONAL = "Personal";
	private static final String MENU_BUSINESS = "Business";
    private static final String MENU_PROJECTS = "Projects";
    private static final String MENU_RESOURCES = "Resources";

	public static final String[] SEARCH_TYPES = new String[] { "All", "Calendar", "Forms", "Deliverables", "Documents",
			"Tasks", "Discussions" };
    public static final String PS_CHANNEL_ID_SYSTEM_STATUS = "PersonalSpace_systemstatus";
    public static final String PS_CHANNEL_ID_ASSIGNMENT_METRICS = "PersonalSpace_assignmentMetrics";
    public static final String PS_CHANNEL_ID_CURRENT_ASSIGNMENTS = "PersonalSpace_currentassignments";
    public static final String PS_CHANNEL_ID_MY_BUSINESS_LIST = "PersonalSpace_MyBusinessList";
    public static final String PS_CHANNEL_ID_PROJECTS = "PersonalSpace_projects";
    public static final String PS_CHANNEL_ID_ASSIGNMENTS = "PersonalSpace_assignments";
    public static final String PS_CHANNEL_ID_MEETINGS = "PersonalSpace_meetings";
    public static final String PS_CHANNEL_ID_MY_CHECKED_OUT_DOCS = "PersonalSpace_MyCheckedOutDocs";
    public static final String PS_CHANNEL_ID_MY_ENVELOPE_LIST = "PersonalSpace_MyEnvelopeList";
    public static final String PS_CHANNEL_CLOSE_ID_PREFIX = "close";

    public static final int PS_CHANNEL_MANAGER_ID_SYSTEM_STATUS = 0;
    public static final int PS_CHANNEL_MANAGER_ID_ASSIGNMENT_METRICS = 1;
    public static final int PS_CHANNEL_MANAGER_ID_CURRENT_ASSIGNMENTS = 2;
    public static final int PS_CHANNEL_MANAGER_ID_MY_BUSINESS_LIST = 3;
    public static final int PS_CHANNEL_MANAGER_ID_PROJECTS = 4;
    public static final int PS_CHANNEL_MANAGER_ID_MEETINGS = 5;
    public static final int PS_CHANNEL_MANAGER_ID_MY_CHECKED_OUT_DOCS = 6;
    public static final int PS_CHANNEL_MANAGER_ID_MY_ENVELOPE_LIST = 7;
    public static final String PS_CHANNEL_MANAGER_ID_PREFIX = "channelSeq_";

    public static final String TOOLBAR_BUTTON_CREATE = "/images/icons/toolbar-gen-create";
    public static final String TOOLBAR_BUTTON_RESET = "/images/icons/toolbar-gen-reset";
    public static final String ACTIONBAR_BUTTON_CANCEL = "/images/icons/actionbar-cancel";
    public static final String ACTIONBAR_BUTTON_SUBMIT = "/images/icons/actionbar-submit";
    public static final String ACTIONBAR_BUTTON_FINISH = "/images/icons/actionbar-finish";
	public static final String ACTIONBAR_BUTTON_NEXT = "/images/icons/actionbar-next";

    public static final String CURRENT_ASSIGNMENTS_ID_PREFIX = "currentAssignments_";
    public static final String NEW_ITEMS_LINK_ASSIGNMENTS_ID_PREFIX = "newItems_";
    public static final String NEW_ITEMS_CHECKBOX_ASSIGNMENTS_ID_PREFIX = "newItemsCheckbox_";
    
    public static final String WORKFLOWS_STEP_ROLE_SEL_ID_PREFIX = "sel_";
    public static final String WORKFLOWS_STEP_ROLE_DESEL_ID_PREFIX = "desel_";

    private DateFormat formatter = new SimpleDateFormat("MM/dd/yy");

    private DataCleaner _dataCleaner;

    public PnetAcceptanceTestFramework(WebTester inializedEngineTester) {
        tester = inializedEngineTester;
        _dataCleaner = DataCleaner.getInstance(this);
    }

    /**
     * Helper function for logging in as a specified user via username/password
     * credentials
     * 
     * @param username
     * @param password
     */
    public void login(String username, String password) {
        beginAt("/Login.jsp");

        assertTitleEquals("Project.net Login");
        assertTextPresent("Login Name:");
        assertTextPresent("Password:");

        assertFormPresent("Login");
        setWorkingForm("Login");

        assertFormElementPresent("J_USERNAME");
        assertFormElementPresent("J_PASSWORD");

        setTextField("J_USERNAME", "not_existent_user");
        setTextField("J_PASSWORD", "some_password");

        submit();

        assertTextPresent("Invalid Login Name or Password");

        setTextField("J_USERNAME", username);
        setTextField("J_PASSWORD", password);

        submit();

        assertTitleEquals("Project.net");

    }

    public void goToPersonal() {
        gotoPage("/personal/Main.jsp?module=160");
        assertLinkPresentWithExactText(MENU_PERSONAL);
        clickLinkWithExactText(MENU_PERSONAL);
        // assertTitleEquals();
    }

    public void goToBusiness() {
        // gotoPage("/business/BusinessPortfolio.jsp?module=160&portfolio=true");
    	clickLinkWithExactText(MENU_BUSINESS);
        assertTextPresent("Business Portfolio");
        // assertTitleEquals();
    }

    public void goToProjects() {
        goToPersonal();
        assertLinkPresentWithExactText(MENU_PROJECTS);
        assertTextNotPresent("Project List");
        clickLinkWithExactText(MENU_PROJECTS);
        assertTextPresent("Project List");
        // assertTitleEquals();
    }

    public void gotoResourceManagementPage() throws Exception {
    	clickLinkWithExactText(MENU_RESOURCES);
        assertTextPresent("Resource Management");
    }

    public String createNewBusiness(String namePrefix) {
        goToBusiness();

        final String newBusinessName = createUniqueValue(namePrefix);

        assertLinkNotPresentWithExactText(newBusinessName);
        
        clickActionCreateNewBusiness();

        assertTextPresent("Create New Business");
        assertTextPresent("Business Identity");

        assertFormPresent("createBusiness1");
        setWorkingForm("createBusiness1");
        assertTextPresent("Business Name:");
        assertFormElementPresent("name");
        setTextField("name", newBusinessName);
        clickLinkWithImage(ACTIONBAR_BUTTON_NEXT);
        
        assertTextPresent("Business Address");
        clickLinkWithImage(ACTIONBAR_BUTTON_FINISH);
        assertTextPresent("Business Portfolio");
        assertLinkPresentWithExactText(newBusinessName);

        _dataCleaner.registerCreatedObject(new PnetBusinessObject(newBusinessName));
        
        return newBusinessName;
    }

	/*public WebTester getWebTester() {
		return _pnetTestEngine;
	}*/

    private void clickActionCreateNewBusiness() {
    	clickLinkWithExactText("Create Business");
	}

	public void testBusinessPortfolioMainPage() {
        //
    }

    public String createNewProject(String namePrefix, String... parentBusinessName) {
        return createNewProject(namePrefix, null, null, parentBusinessName);
    }

    public String createNewProject(String namePrefix, Date startDate, Date endDate, String... parentBusinessName) {

        String actualParentBusinessName;
        if (parentBusinessName.length > 1) {
            throw new IllegalArgumentException("You may specify only one newBusinessName argument");
        }
        if (parentBusinessName.length == 1) {
            actualParentBusinessName = parentBusinessName[0];
        } else {
            actualParentBusinessName = createNewBusiness("testBusiness");
        }

        goToProjects();

        final String newProjectName = createUniqueValue(namePrefix);

        assertLinkNotPresentWithExactText(newProjectName);

        clickActionNew();

        assertTextPresent("Create Project Workspace");
        assertTextPresent("Step 2");

        assertFormPresent("CREATEPROJ");
        setWorkingForm("CREATEPROJ");

        assertTextPresent("Project Name:");
        assertFormElementPresent("name");
        setTextField("name", newProjectName);

        assertSelectOptionPresent("parentBusinessID", actualParentBusinessName);
        selectOption("parentBusinessID", actualParentBusinessName);

        assertTextPresent("Start Date:");
        assertFormElementPresent("startDateString");
        if (startDate != null)
            setTextField("startDateString", formatter.format(startDate));

        assertTextPresent("End Date:");
        assertFormElementPresent("endDateString");
        if (endDate != null)
            setTextField("endDateString", formatter.format(endDate));

        clickLinkWithImage(ACTIONBAR_BUTTON_NEXT);

        assertTextPresent("Step 3");
        assertTextPresent(newProjectName);
        clickLinkWithImage(ACTIONBAR_BUTTON_FINISH);
        assertTextPresent("Project List");
        assertLinkPresentWithExactText(newProjectName);
        assertTextPresent(actualParentBusinessName);

        _dataCleaner.registerCreatedObject(new PnetProjectObject(actualParentBusinessName, newProjectName));

        return newProjectName;
    }

    public void clickActionNew() {
    	clickLinkWithExactText("New");
	}

	public String createNewTask(String namePrefix) {
        final String newProjectName = createNewProject();
        clickLinkWithExactText(newProjectName);

        return createNewTask(namePrefix, newProjectName);
    }

    public String createNewTask(String namePrefix, String existentProjectName) {
        return createNewTask(namePrefix, existentProjectName, null, null);
    }

    public String createNewTask(String namePrefix, String existentProjectName, Date startDate, Date endDate) {
        goToPersonal();
        clickLinkWithExactText(existentProjectName);

        final String newTaskName = createUniqueValue(namePrefix);
        assertTextNotPresent(newTaskName);

        clickWorkplanMenuItem();
        testTaskShedulingListPage();

        assertTextNotPresent(newTaskName);

        clickActionCreateNewTask();

        assertTextPresent("Create Task");
        assertFormPresent("taskEdit");
        setWorkingForm("taskEdit");
        assertTextPresent("Name:");
        assertFormElementPresent("name");
        setTextField("name", newTaskName);

        assertTextPresent("Start Date:");
        assertFormElementPresent("startTime_NotSubmitted");

        if (startDate != null)
            setTextField("startTime_NotSubmitted", formatter.format(startDate));

        assertTextPresent("Finish Date:");
        assertFormElementPresent("endTime_NotSubmitted");
        if (endDate != null)
            setTextField("endTime_NotSubmitted", formatter.format(endDate));

        clickSubmitActionbarButton();

        checkCheckbox("resource");
        clickSubmitActionbarButton();

        assertTextPresent(newTaskName);
        
        _dataCleaner.registerCreatedObject(
        		new PnetTaskObject(existentProjectName, newTaskName));
        
        return newTaskName;
    }

    private void clickActionCreateNewTask() {
    	clickLinkWithExactText("Create New Task");
	}

	public void assignResource(String taskName, String projectName) {
        assignResource(taskName, projectName, null, null);
    }

    public void assignResource(String taskName, String projectName, String businessName) {
        assignResource(taskName, projectName, null, null);
    }

    public void assignResource(String taskName, String projectName, String businessName, String percentAssigned) {
        if (businessName != null) {
            goToBusiness();
            assertTextPresent(businessName);
            clickLinkWithText(businessName);
        } else
            goToProjects();

        assertTextPresent(projectName);
        clickLinkWithText(projectName);
        clickLinkWithExactText("Workplan");
        assertTextPresent("Scheduling List");

        // Assigning a Resource by modifing the task
        assertTextPresent(taskName);
        clickLinkWithText(taskName);
        assertTextPresent("View Task");
        assertTextPresent("Resources");
        clickLinkWithText("Resources");
        assertTextPresent("View Task");
        assertTextPresent("Assign People to a Task");
        checkCheckbox("resource");
        // if (percentAssigned != null)
        // setTextField("percent_1", percentAssigned);
        clickLinkWithImage(ACTIONBAR_BUTTON_SUBMIT);
        assertTextPresent("Scheduling List");
    }

    public String createNewTask() {
        return createNewTask("testTask");
    }

    public String createNewProject() {
        return createNewProject("testProject");
    }

    public String createNewBusiness() {
        return createNewBusiness("testBusiness");
    }

	public void deleteBusiness(String businessName) {
		goToBusiness();
		assertLinkPresentWithExactText(businessName);
		clickRadioOptionWithId(businessName);
	
		assertWindowCountEquals(1);
		try {
			assertWindowPresentWithTitle("Project Delete");
			throw new RuntimeException("Must throw exception");
		} catch (AssertionFailedError e) {
		}
		assertWindowPresentWithTitle("Project.net");

		clickActionDeleteBusiness();

		assertWindowCountEquals(2);
		assertWindowPresentWithTitle("Delete Business Workspace");
		gotoWindowByTitle("Delete Business Workspace");
		testDeleteBusinessPage();
		clickRadioOptionWithId("disablebusiness");
		clickFinishActionbarButton();
		
		assertLinkNotPresentWithExactText(businessName);
		
		/*System.out.println("businessName :- " + businessName);
		goToBusiness();
		assertTextPresent(businessName);
		//clickRadioOption("selected", "");
		clickRadioOptionWithName("selected");
		clickLinkWithImage(TOOLBAR_BUTTON_REMOVE);
		gotoWindowByTitle("Delete Business Workspace");
		assertTextPresent("Business Workspace Delete Wizard");
		clickRadioOption("selected", "business");
		//clickRadioOptionWithId("disablebusiness");
		clickLinkWithImage(ACTIONBAR_BUTTON_FINISH);
		//gotoWindowByTitle("Project.net");
		//gotoRootWindow();
		//assertTextPresent("Business Portfolio");
*/	}

    private void clickActionDeleteBusiness() {
    	clickLinkWithExactText("Delete Business");
	}

	private void testDeleteBusinessPage() {
	}

	public void deleteProject(String projectName, String businessName) {
        // if(businessName.equals(null) || businessName.equals("")) {
        goToProjects();
        assertLinkPresentWithExactText(projectName);
        clickRadioOptionWithId(projectName);

        assertWindowCountEquals(1);
        try {
            assertWindowPresentWithTitle("Project Delete");
            throw new RuntimeException("Must throw exception");
        } catch (AssertionFailedError e) {
        }
        assertWindowPresentWithTitle("Project.net");

        clickActionRemove();

        assertWindowCountEquals(2);
        assertWindowPresentWithTitle("Project Delete");
        gotoWindowByTitle("Project Delete");
        testDeleteProjectPage();
        clickRadioOptionWithId("disableproject");
        clickFinishActionbarButton();

        assertLinkNotPresentWithExactText(projectName);
		/*}
		else
			goToBusiness();*/
    }
	
	public void deleteWorkflow(String name, String projectName) {
		goToProjects();
		clickLinkWithExactText(projectName);
		clickActionWorkflow();
		clickLinkWithExactText("Workflow Designer");
		assertLinkPresentWithExactText(name);
		clickRadioOptionWithId("workflow" + name);
		clickActionRemove();
		setExpectedJavaScriptConfirm("Remove this Workflow?", true);
		assertTextPresent("There was a problem performing the remove");
		assertTextPresent("A workflow may not be removed while it is published");
		
		clickCancelActionbarButton();
		clickRadioOptionWithId("workflow" + name);
		clickLinkWithExactText(name);
		assertTextPresent("Change to unpublished");
		clickSubmitActionbarButton();
		clickLinkWithExactText("Workflow Designer");
		clickRadioOptionWithId("workflow" + name);
		clickActionRemove();
		setExpectedJavaScriptConfirm("Remove this Workflow?", true);
		
		assertLinkNotPresentWithExactText(name);
	}

    public void testDeleteProjectPage() {
        //
	}

	public void deleteTask(String taskName, String projectName){
		goToProjects();
		clickLinkWithExactText(projectName);
		clickWorkplanMenuItem();
		checkCheckboxWithId("task" + taskName);
		
		assertWindowCountEquals(2);
		try {
			assertWindowPresentWithTitle("Delete Tasks");
			throw new RuntimeException("Must throw exception");
		} catch (AssertionFailedError e) {
		}
		assertWindowPresentWithTitle("Project.net");

		clickActionDeleteTask();

		assertWindowCountEquals(3);
		assertWindowPresentWithTitle("Delete Tasks");
		gotoWindowByTitle("Delete Tasks");
		testDeleteTasksPage();
		clickRadioOptionWithId("deleteTaskYes");
		clickSubmitActionbarButton();

		assertLinkNotPresentWithExactText(taskName);
    }
	
    private void testDeleteTasksPage() {
	}

	public void clickActionDeleteTask() {
    	clickLinkWithExactText("Delete Task");
	}

	public void testProjectListMainPage() {
        //
    }

    public void testProjectPage() {
        //
    }

    public void testBusinessPage() {
    }

	public void testCreateEditTaskPage() {
		//
	}
	
	public void clickActionSearch() {
		clickLinkWithExactText("Search");
	}
	
    public void clickActionCreate() {
    	clickLinkWithExactText("Create");
    }

    public void clickActionModify() {
    	clickLinkWithExactText("Modify");
    }

    public void clickSubmitActionbarButton() {
        clickLinkWithImage(ACTIONBAR_BUTTON_SUBMIT);
    }

    public void clickNextActionbarButton() {
        clickLinkWithImage(ACTIONBAR_BUTTON_NEXT);
    }

    public void clickActionHelp() {
    	clickLinkWithExactText("Help");
    }

    public void clickActionCheckOut() {
    	clickLinkWithExactText("Check Out");
    }

    public void clickActionRemove() {
    	clickLinkWithExactText("Remove");
    }

    public void clickActionWorkflow() {
    	clickLinkWithExactText("Workflow");
    }

    public void testTaskShedulingListPage() {
        assertTextPresent("Scheduling List");
		//
    }

    public void testHelpPage() {
        //
    }

    public void testDocumentCheckOutPage() {
        //
    }

    public void clickChannelbarHelpButton() {
        clickLinkWithImage("/images/icons/channelbar-help.gif");
    }

    public void testViewAssignmentsPage() {
        //
    }

    public void clickCancelActionbarButton() {
        clickLinkWithImage(ACTIONBAR_BUTTON_CANCEL);
    }

    public void testPersonalHomePageSmoke() {

        assertElementPresent("content");
        assertElementPresent("topframe");
        assertElementPresent("leftheading-person");
        assertElementPresent("actionbox-item");
        assertElementPresent("logo");
        
        assertLinkPresentWithExactText("Dashboard");
        assertLinkPresentWithExactText("Calendar");
        assertLinkPresentWithExactText("Assignments");
        assertLinkPresentWithExactText("Assignments (Beta)");
        assertLinkPresentWithExactText("Blog");
        assertLinkPresentWithExactText("Documents");
        assertLinkPresentWithExactText("Forms");
        assertLinkPresentWithExactText("Setup");
        assertLinkPresentWithExactText("Application Admin");

        assertTextPresent("My Businesses");
        assertTextPresent("My Projects");
        
        assertLinkPresentWithExactText("Personal");
        assertLinkPresentWithExactText("Business");
        assertLinkPresentWithExactText("Projects");
        assertLinkPresentWithExactText("Resources");

        assertLinkPresentWithExactText("Help");
        assertLinkPresentWithExactText("Log Out");
        assertTextPresent("Application Administrator");
        
        assertLinkPresentWithExactText("Personalize this Page");
    }

    public String createNewMeeting() {
        return createNewMeeting("testMeeting");
    }
    
    public String createNewEvent() {
        return createNewEvent("testEvent");
    }

    public void testPersonalizeThisPagePage() {

        assertTextPresent("Channel Manager");
        assertTextPresent("Active");
        assertTextPresent("Name");
        assertTextPresent("Site Status");
        assertTextPresent("Personal Planned Activity Metrics");
        assertTextPresent("Assignments - In Progress, Late, or Starting within 2 Days");
        assertTextPresent("My Businesses");
        assertTextPresent("My Projects");
        assertTextPresent("Upcoming Meetings");
        assertTextPresent("Documents Checked Out By Me");
        assertTextPresent("Workflow Inbox");

        for (int i = 0; i <= 7; i++) {
            assertCheckboxPresent(PS_CHANNEL_MANAGER_ID_PREFIX + i, "enabled");
        }
    }

    public String createNewDocument(String existentProjectName) {
        return createNewDocument("testDocument", existentProjectName);
    }

    public String createNewDocumentAndCkeckOut() {
        final String newProjectName = createNewProject();
        final String newDocumentName = createNewDocument(newProjectName);

        checkOutDocument(newDocumentName, newProjectName);
        return newDocumentName;
    }

    public void checkOutDocument(String documentName, String existentProjectName) {
        goToPersonal();
        assertLinkNotPresentWithExactText(documentName);

        clickLinkWithExactText(existentProjectName);
        clickDocumentsMenuItem();
        assertLinkPresentWithExactText(documentName);

        clickRadioOptionForDocumentById(documentName);

        assertWindowCountEquals(1);
        try {
            assertWindowPresentWithTitle("Document Check Out");
            throw new RuntimeException("Must throw exception");
        } catch (AssertionFailedError e) {
        }
        assertWindowPresentWithTitle("Project.net");

        clickActionCheckOut();

        assertWindowCountEquals(2);
        assertWindowPresentWithTitle("Document Check Out");

        gotoWindowByTitle("Document Check Out");
        testDocumentCheckOutPage();

        clickSubmitActionbarButton();
    }

    public void clickRadioOptionForDocumentById(String documentName) {
        clickRadioOptionWithId(documentName);
    }

    public String createNewDocument(String namePrefix, String existentProjectName) {
        goToPersonal();
        clickLinkWithExactText(existentProjectName);

        final String uniqueDocumentName = createUniqueValue(namePrefix);

        clickDocumentsMenuItem();
        testDocumentsPage();
        assertLinkNotPresentWithExactText(uniqueDocumentName);

        clickActionImportDocument();
        testUploadDocumentPage();

        setWorkingForm("documentUpload");

        setTextField("file", createUniqueValue("nonExistentPath"));
        setTextField("name", uniqueDocumentName);
        clickSubmitActionbarButton();
        assertTextPresent("Invalid file path");

        //setTextField("file", "web/images/pnet_logo2.gif");
        setTextField("file", "C:\\pnet_pnet_db_build.log");
        setTextField("name", uniqueDocumentName);
        clickSubmitActionbarButton();
        assertTextNotPresent("Invalid file path");
        assertLinkPresentWithExactText(uniqueDocumentName);

        return uniqueDocumentName;
    }

    private void clickActionImportDocument() {
    	clickLinkWithExactText("Import Document");
	}

	public void clickDocumentsMenuItem() {
    	clickLinkWithExactText("Documents");
    	//clickMenuItem("Documents");
    }

    public void clickWorkflowMenuItem() {
    	clickLinkWithExactText("Workflow");
    }
    
    public void clickWorkplanMenuItem() {
    	clickLinkWithExactText("Workplan");
    	//clickMenuItem("Workplan");
    }
    
    private void clickMenuItem(String menuItemName) {
    	clickElementByXPath("//span[@class=\"" + menuItemName + "\"]");
    }

    public void testUploadDocumentPage() {
        //
    }

    public void testDocumentsPage() {
        //
    }

    public void testDocumentPage() {
        //
    }

    public String createNewWorkflow(String existentProjectName) {
        goToPersonal();
        clickLinkWithExactText(existentProjectName);
        clickWorkflowMenuItem();

        // Definition
        testWorkflowsListPage();

        final String newWorkflowName = createUniqueValue("testWorflow");
        assertLinkNotPresentWithExactText(newWorkflowName);

        clickActionCreate();
        testWorkflowCreateDefinitionPage();
        setWorkingForm("workflowCreate");
        setTextField("name", newWorkflowName);
        selectOption("ownerID", "Application Administrator");
        selectOption("strictnessID", "Relaxed");
        // selectOption("objectTypeSelect", "Document : Any");
        clickSubmitActionbarButton();

        // Steps
        testWorkflowStepsListPage();

        final String uniqueWorkflowStepName = createUniqueValue("testWorflowStep");
        assertLinkNotPresentWithExactText(uniqueWorkflowStepName);

        clickActionCreate();
        testWorkflowCreateStepPage();
        setTextField("name", uniqueWorkflowStepName);
        setTextField("sequence", "1");
        clickRadioOptionWithId("stepTypeInitialStep");
        clickSubmitActionbarButton();

        // Roles
        clickLinkWithExactText("Add roles");
        testCreateWorflowStepRolesPage();
        assertElementPresent(WORKFLOWS_STEP_ROLE_SEL_ID_PREFIX + "Application Administrator");
        assertElementNotPresent(WORKFLOWS_STEP_ROLE_DESEL_ID_PREFIX + "Application Administrator");
        checkCheckboxWithId(WORKFLOWS_STEP_ROLE_SEL_ID_PREFIX + "Application Administrator");
        clickAddSelectedWokflowsStepRoleButton();
        assertElementNotPresent(WORKFLOWS_STEP_ROLE_SEL_ID_PREFIX + "Application Administrator");
        assertElementPresent(WORKFLOWS_STEP_ROLE_DESEL_ID_PREFIX + "Application Administrator");
        checkCheckboxWithId(WORKFLOWS_STEP_ROLE_DESEL_ID_PREFIX + "Application Administrator");
        clickSubmitActionbarButton();
        testWorkflowStepsListPage();
        assertLinkPresentWithExactText(uniqueWorkflowStepName);

        // Transitions
        clickLinkWithExactText("Transitions");
        testWorkflowTransitionsListPage();

        final String uniqueWorkflowTransitionName = createUniqueValue("testWorflowTransitionName");
        assertLinkNotPresentWithExactText(uniqueWorkflowTransitionName);

        clickActionCreate();
        testWorkflowCreateTransitionPage();
        setTextField("transitionVerb", uniqueWorkflowTransitionName);
        selectOption("beginStepID", uniqueWorkflowStepName);
        selectOption("endStepID", uniqueWorkflowStepName);
        clickSubmitActionbarButton();
        clickCreateChannelbarButton();
        checkCheckboxWithId("Application Administrator");
        clickSubmitActionbarButton();
        testWorkflowTransitionsListPage();
        assertLinkPresentWithExactText(uniqueWorkflowTransitionName);

        clickLinkWithExactText("Publish");
        assertTextPresent("This workflow is not currently published");
        assertTextNotPresent("This workflow is currently published");
        clickSubmitActionbarButton();
        assertTextNotPresent("This workflow is not currently published");
        assertTextPresent("This workflow is currently published");

        clickWorkflowMenuItem();
        assertLinkPresentWithExactText(newWorkflowName);

        _dataCleaner.registerCreatedObject(
        		new PnetWorkflowObject(existentProjectName, newWorkflowName));
        
        return newWorkflowName;
    }

    public String addDocumentToWokflowInbox() {
        final String newProjectName = createNewProject();
        final String newWorkflowName = createNewWorkflow(newProjectName);

        goToPersonal();
        assertLinkNotPresentWithExactText(newWorkflowName);

        final String newDocumentName = createNewDocument(newProjectName);
        clickRadioOptionForDocumentById(newDocumentName);

        assertWindowCountEquals(1);
        try {
            assertWindowPresentWithTitle("Start Workflow Wizard");
            throw new RuntimeException("Must throw exception");
        } catch (AssertionFailedError e) {
        }
        assertWindowPresentWithTitle("Project.net");

        clickActionAddToWorkflow();
        assertWindowCountEquals(2);
        assertWindowPresentWithTitle("Start Workflow Wizard");

        gotoWindowByTitle("Start Workflow Wizard");
        testStartWorkflowWizardPage1();
        selectOption("workflowID", newWorkflowName);
        setTextField("name", newWorkflowName);
        clickNextActionbarButton();

        testStartWorkflowWizardPage2();
        selectOption("statusID", "In Progress");
        clickFinishActionbarButton();
        assertWindowCountEquals(1);
        assertWindowPresentWithTitle("Project.net");

        return newWorkflowName;
    }

    private void clickActionAddToWorkflow() {
    	clickLinkWithExactText("Add to Workflow");
	}

	public String createNewAssignableForm(String existentProjectName, boolean assignable) {
        final AssignableFormsTester assignableFormTester = new AssignableFormsTester(this);
        return assignableFormTester.createNewAssignableForm(existentProjectName, assignable);
    }

	public String createNewMeeting(String namePrefix) {
		final String newMeetingName = createUniqueValue(namePrefix);
		
		goToPersonal();
		clickCalendarMenuItem();
		assertTextPresent("iCalendar Integration");
		
		clickLinkWithText("Compose New");
		setTextField("name", newMeetingName);
		clickLinkWithText("Next");
		assertTextPresent("Meeting Attendees");
		clickLinkWithText("Next");
		assertTextPresent("Meeting Agenda");
		clickFinishActionbarButton();
		
		assertTextPresent("iCalendar Integration");
		assertTextPresent(newMeetingName);
	
		_dataCleaner.registerCreatedObject(new PnetMeetingObject(newMeetingName));
		
		return newMeetingName;
	}
	
	public String createNewEvent(String namePrefix) {
		final String newEventName = createUniqueValue(namePrefix);
		
		goToPersonal();
		clickCalendarMenuItem();
		assertTextPresent("iCalendar Integration");

		selectOption("compose", "Event");
		clickLinkWithText("Compose New");
		setTextField("name", newEventName);
		setTextField("FacilityDescription", "Event Faclity Description");
		clickSubmitActionbarButton();

		assertTextPresent("iCalendar Integration");
		assertTextPresent(newEventName);
		
		_dataCleaner.registerCreatedObject(new PnetEventObject(newEventName));
		
		return newEventName;
	}
	
	public void clickCalendarMenuItem() {
		clickLinkWithExactText("Calendar");
	}

	public void deleteMeeting(String meetingName) {
		goToPersonal();
		clickCalendarMenuItem();
		clickLinkWithExactText(meetingName);
		clickActionRemove();
		assertLinkNotPresentWithExactText(meetingName);
	}
	
	public void deleteEvent(String eventName) {
		goToPersonal();
		clickCalendarMenuItem();
		clickLinkWithExactText(eventName);
		clickActionRemove();
		assertLinkNotPresentWithExactText(eventName);
	}
	
    public void clickCreateChannelbarButton() {
        clickLinkWithImage("/images/icons/channelbar-create");
    }

    public void testWorkflowCreateTransitionPage() {
        //
    }

    public void testWorkflowTransitionsListPage() {
        //
    }

    public void clickAddSelectedWokflowsStepRoleButton() {
        clickLinkWithImage("/images/icons/actionbar-nextpost");
    }

    public void clickFinishActionbarButton() {
        clickLinkWithImage(ACTIONBAR_BUTTON_FINISH);
    }
    
    public void clickActionCaptureWork() {
    	clickLinkWithExactText("Capture work");
    }

    public void testCreateWorflowStepRolesPage() {
        //
    }

    public void testWorkflowCreateStepPage() {
        //
    }

    public void testWorkflowStepsListPage() {
        //
    }

    public void testWorkflowCreateDefinitionPage() {
        //
    }

    public void testWorkflowsListPage() {
        //
    }

    public void testStartWorkflowWizardPage1() {
        //
    }

    public void testStartWorkflowWizardPage2() {
        //
    }

    public void logout() {
        //clickLinkWithExactText("Log Out");
    	gotoPage("/Logout.jsp?t=1");
    }

    public DataCleaner getDataCleaner() {
        return _dataCleaner;
    }
}

/*package net.project.test.acceptance.framework;

 import junit.framework.AssertionFailedError;
 import net.project.test.acceptance.engine.PnetTestEngine;
 import net.sourceforge.jwebunit.junit.WebTester;

 // Vladimir Malykhin

 public class PnetTestFramework {
 
 private WebTester _tester;
 
 public PnetTestFramework(WebTester inializedPnetTestEngine) {
 _tester = inializedPnetTestEngine;
 }
 
 */
