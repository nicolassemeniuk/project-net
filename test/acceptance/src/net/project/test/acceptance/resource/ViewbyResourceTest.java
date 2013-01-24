package net.project.test.acceptance.resource;

public class ViewbyResourceTest extends ResourceManagementBase {

	public void testViewbyResourceForAllBusiness() throws Exception {
		_framework.gotoResourceManagementPage();
		_framework.clickLinkWithText("View by resource");
		_framework.assertTextPresent("Business");
		_framework.selectOption("business", "All");
		_framework.assertButtonPresent("viewBtn");
		_framework.clickButton("viewBtn");
		// verify the data displayed in report

		// _framework.assertTextPresent("Application Administrator");
		_framework.assertTextPresent("Resource Reservation Summary");
		_framework.assertTextPresent("Project");
		_framework.assertTextPresent("Number Of Projects");
		_framework.assertTextPresent("May08");
		_framework.assertTextPresent("Jun08");
		_framework.assertTextPresent("Jul08");
		_framework.assertTextPresent("Aug08");
		_framework.assertTextPresent("Sep08");
		//_framework.assertTextPresent(getProjectNameOne()); 
		//_framework.assertTextPresent("1");
		_framework.assertTextPresent("0%");

		// assertTableEquals("", new String[][] {
		// { "Project", "Number of Projects", "Mar08", "Apr08", "May08","Jun08", "Jul08", "Aug08", "Sep08","Oct08" }, 
		// { projectNameOne, "", "", "", "", "", "", "", "", "" },
		// { "", "1", "0%", "0%", "0%", "0%", "0%", "0%", "0%", "0%" } });
	}

	public void testViewbyResourceForOneBusiness() throws Exception {
		_framework.gotoResourceManagementPage();
		_framework.clickLinkWithText("View by resource");
		_framework.assertTextPresent("Business");
		_framework.selectOption("business", getBusinessNameOne());
		_framework.assertButtonPresent("viewBtn");
		_framework.clickButton("viewBtn");
		// verify the data displayed in report

		// _framework.assertTextPresent("Application Administrator");
		_framework.assertTextPresent("Resource Reservation Summary");
		_framework.assertTextPresent("Project");
		_framework.assertTextPresent("Number Of Projects");
		_framework.assertTextPresent("May08");
		_framework.assertTextPresent("Jun08");
		_framework.assertTextPresent("Jul08");
		_framework.assertTextPresent("Aug08");
		_framework.assertTextPresent("Sep08");
		//_framework.assertTextPresent(getProjectNameOne()); 
		//_framework.assertTextPresent("1");
		_framework.assertTextPresent("0%");

		// assertTableEquals("", new String[][] {
		// { "Project", "Number of Projects", "Mar08", "Apr08", "May08","Jun08", "Jul08", "Aug08", "Sep08","Oct08" }, 
		// { projectNameOne, "", "", "", "", "", "", "", "", "" },
		// { "", "1", "0%", "0%", "0%", "0%", "0%", "0%", "0%", "0%" } });
	}

}
