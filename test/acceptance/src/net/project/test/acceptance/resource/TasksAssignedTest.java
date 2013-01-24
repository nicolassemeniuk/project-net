package net.project.test.acceptance.resource;

public class TasksAssignedTest extends ResourceManagementBase {

	public void testTasksAssignedForAllBusiness() throws Exception {
		_framework.gotoResourceManagementPage();
		_framework.clickLinkWithText("Tasks Assigned");
		_framework.assertTextPresent("Business");
		_framework.selectOption("resourceList", "All");
		_framework.assertButtonPresent("viewBtn");
		_framework.clickButton("viewBtn");
		// verify the data displayed in report

		// _framework.assertTextPresent("Application Administrator");
		_framework.assertTextPresent("Tasks Assigned Details");
		_framework.assertTextPresent("Task Assignment");
		_framework.assertTextPresent("Project");
		_framework.assertTextPresent("Planned Start");
		_framework.assertTextPresent("Planned Finish");
		_framework.assertTextPresent("Actual Start");
		_framework.assertTextPresent("Actual Finish");
		_framework.assertTextPresent("Total Work");
		_framework.assertTextPresent("Work Complete");
		_framework.assertTextPresent("Work Remaining");
		_framework.assertTextPresent("Work % Complete");
		_framework.assertTextPresent("Total");
		//_framework.assertTextPresent("0");
		
		// assertTableEquals("", new String[][] {
		// { "Tasks Assignment", "Project", "Planned Start", "Planned Finish","Actual Start", "Actual Finish", "Total Work", "Work Complete", "Work Remaining","Work % Complete" }, 
		// {  },
		// { "Total:", "", "", "","", "", "0", "0", "0",""} });
	}

	public void testTasksAssignedForOneBusiness() throws Exception {
		_framework.gotoResourceManagementPage();
		_framework.clickLinkWithText("Tasks Assigned");
		_framework.assertTextPresent("Business");
		_framework.selectOption("resourceList", getBusinessNameOne());
		_framework.assertButtonPresent("viewBtn");
		_framework.clickButton("viewBtn");
		// verify the data displayed in report

		// _framework.assertTextPresent("Application Administrator");
		_framework.assertTextPresent("Tasks Assigned Details");
		_framework.assertTextPresent("Task Assignment");
		_framework.assertTextPresent("Project");
		_framework.assertTextPresent("Planned Start");
		_framework.assertTextPresent("Planned Finish");
		_framework.assertTextPresent("Actual Start");
		_framework.assertTextPresent("Actual Finish");
		_framework.assertTextPresent("Total Work");
		_framework.assertTextPresent("Work Complete");
		_framework.assertTextPresent("Work Remaining");
		_framework.assertTextPresent("Work % Complete");
		_framework.assertTextPresent("Total");
		//_framework.assertTextPresent("0");
		
		// assertTableEquals("", new String[][] {
		// { "Tasks Assignment", "Project", "Planned Start", "Planned Finish","Actual Start", "Actual Finish", "Total Work", "Work Complete", "Work Remaining","Work % Complete" }, 
		// {  },
		// { "Total:", "", "", "","", "", "0", "0", "0",""} });
	}

}
