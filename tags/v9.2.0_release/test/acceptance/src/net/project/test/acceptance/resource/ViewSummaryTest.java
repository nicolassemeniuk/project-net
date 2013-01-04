package net.project.test.acceptance.resource;

public class ViewSummaryTest extends ResourceManagementBase {

	public void testViewSummaryForAllBusiness() throws Exception {
		_framework.gotoResourceManagementPage();
		_framework.assertTextPresent("Business");
		_framework.selectOption("businessList", "All");
		_framework.assertButtonPresent("viewBtn");
		_framework.clickButton("viewBtn");
		// verify the data displayed in report

		// _framework.assertTextPresent("Application Administrator");
		_framework.assertTextPresent("Summary Details");
		_framework.assertTextPresent("Project Name");
		_framework.assertTextPresent("May08");
		_framework.assertTextPresent("Jun08");
		_framework.assertTextPresent("Jul08");
		_framework.assertTextPresent("Aug08");
		_framework.assertTextPresent("Free Time");
		//_framework.assertTextPresent(getProjectNameOne());
		//_framework.assertTextPresent("8");
		_framework.assertTextPresent("0");
		//_framework.assertTextPresent("944");

		// _framework.assertTableEquals("", new String[][] {
		// {"Project Name", "Mar08", "Apr08", "May08", "Jun08","Jul08", "Aug08", "Free Time"},
		// {},
		// {projectNameOne, "8", "8", "0", "0", "0", "0", ""},
		// {"", "8", "8", "0", "0", "0", "0", "944"}});
	}

	public void testViewSummaryForOneBusiness() throws Exception {
		_framework.gotoResourceManagementPage();
		_framework.assertTextPresent("Business");
		_framework.selectOption("businessList", getBusinessNameOne());
		_framework.assertButtonPresent("viewBtn");
		_framework.clickButton("viewBtn");

		// verify the data displayed in report

		// _framework.assertTextPresent("Application Administrator");
		_framework.assertTextPresent("Summary Details");
		_framework.assertTextPresent("Project Name");
		_framework.assertTextPresent("May08");
		_framework.assertTextPresent("Jun08");
		_framework.assertTextPresent("Jul08");
		_framework.assertTextPresent("Aug08");
		_framework.assertTextPresent("Free Time");
		//_framework.assertTextPresent(getProjectNameOne());
		//_framework.assertTextPresent("8");
		_framework.assertTextPresent("0");
		//_framework.assertTextPresent("944");

		// _framework.assertTableEquals("", new String[][] {
		// {"Project Name", "Mar08", "Apr08", "May08", "Jun08","Jul08", "Aug08", "Free Time"},
		// {},
		// {projectNameOne, "8", "8", "0", "0", "0", "0", ""},
		// {"", "8", "8", "0", "0", "0", "0", "944"}});
	}

}
