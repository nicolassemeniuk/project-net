package net.project.test.acceptance.resource;

public class ProjectSummaryTest extends ResourceManagementBase {

	public void testProjectSummaryForAllBusiness() throws Exception {
		_framework.gotoResourceManagementPage();
		_framework.assertTextPresent("Project Summary");
		_framework.clickLinkWithText("Project Summary");
		_framework.assertTextPresent("Business");
		_framework.selectOption("business", "All");
		_framework.assertButtonPresent("viewBtn");
		_framework.clickButton("viewBtn");
		// verify the data displayed in report

		// _framework.assertTextPresent("Application Administrator");
		_framework.assertTextPresent("Project Summary");
		_framework.assertTextPresent("Project Name");
		_framework.assertTextPresent("May08");
		_framework.assertTextPresent("Jun08");
		_framework.assertTextPresent("Jul08");
		_framework.assertTextPresent("Aug08");
		_framework.assertTextPresent("Sep08");
		_framework.assertTextPresent("Oct08");
		_framework.assertTextPresent("Nov08");
		_framework.assertTextPresent("Dec08");
		_framework.assertTextPresent("Jan09");
		_framework.assertTextPresent("Feb09");
		//_framework.assertTextPresent(getProjectNameOne());
		//_framework.assertTextPresent("8");
		_framework.assertTextPresent("0");

		// assertTableEquals("", new String[][] {
		// { "Project Name", "Mar08", "Apr08", "May08","Jun08", "Jul08", "Aug08", "Sep08", "Oct08","Nov08","Dec08","Jan09","Feb09" }, 
		// { projectNameOne, "8","8", "","", "", "", "", "","","","","" },
		// { "", "8", "8", "0","0", "0", "0", "0", "0","0","0","0","0" } });

	}

	public void testProjectSummaryForOneBusiness() throws Exception {
		_framework.gotoResourceManagementPage();
		_framework.assertTextPresent("Project Summary");
		_framework.clickLinkWithText("Project Summary");
		_framework.assertTextPresent("Business");
		_framework.selectOption("business", getBusinessNameOne());
		_framework.assertButtonPresent("viewBtn");
		_framework.clickButton("viewBtn");
		// verify the data displayed in report

		// _framework.assertTextPresent("Application Administrator");
		_framework.assertTextPresent("Project Summary");
		_framework.assertTextPresent("Project Name");
		_framework.assertTextPresent("May08");
		_framework.assertTextPresent("Jun08");
		_framework.assertTextPresent("Jul08");
		_framework.assertTextPresent("Aug08");
		_framework.assertTextPresent("Sep08");
		_framework.assertTextPresent("Oct08");
		_framework.assertTextPresent("Nov08");
		_framework.assertTextPresent("Dec08");
		_framework.assertTextPresent("Jan09");
		_framework.assertTextPresent("Feb09");
		//_framework.assertTextPresent(getProjectNameOne());
		//_framework.assertTextPresent("8");
		_framework.assertTextPresent("0");

		// assertTableEquals("", new String[][] {
		// { "Project Name", "Mar08", "Apr08", "May08","Jun08", "Jul08", "Aug08", "Sep08", "Oct08","Nov08","Dec08","Jan09","Feb09" }, 
		// { projectNameOne, "8","8", "","", "", "", "", "","","","","" },
		// { "", "8", "8", "0","0", "0", "0", "0", "0","0","0","0","0" } });

	}

}
