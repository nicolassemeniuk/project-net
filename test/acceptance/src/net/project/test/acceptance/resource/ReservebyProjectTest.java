package net.project.test.acceptance.resource;

public class ReservebyProjectTest extends ResourceManagementBase {

	public void testReservebyProjectForAllBusiness() throws Exception {
		_framework.gotoResourceManagementPage();
		_framework.clickLinkWithText("Reserve by Project");
		_framework.assertTextPresent("Business");
		_framework.selectOption("business", "All");
		_framework.selectOption("monthsId", "Jan");
		_framework.selectOption("yearsId", "2008");
		_framework.selectOption("project", getProjectNameOne());
		_framework.assertButtonPresent("viewBtn");
		_framework.clickButton("viewBtn");
		// verify the data displayed in report
		// _framework.assertTextPresent("Application Administrator");
		//_framework.assertTextPresent("Reserve By Project");
		_framework.assertTextPresent("Resource");
		_framework.assertTextPresent("Until End Of Project");
		_framework.assertTextPresent("Jan08");
		_framework.assertTextPresent("Feb08");
		_framework.assertTextPresent("Mar08");
		_framework.assertTextPresent("Apr08");
		_framework.assertTextPresent("May08");
		_framework.assertTextPresent("Jun08");
		_framework.assertTextPresent("Jul08");
		_framework.assertTextPresent("Aug08");
		//_framework.assertTextPresent("8");
		//_framework.assertTextPresent("0");
		_framework.assertTextPresent("Application Administrator");
		_framework.assertTextPresent("0.00%");
		_framework.assertTextPresent("Total:");

		// assertTableEquals("", new String[][] {
		// { "Resource", "Until End Of Project", "Mar08", "Apr08", "May08","Jun08", "Jul08", "Aug08", "Sep08", "Oct08" }, 
		// { "", "" ,"8", "8", "0", "0", "0", "0", "", "" },
		// { "Total:", "", "0%", "0%", "0%", "0%", "0%", "0%", "0%", "0%" } });
	}

	public void testReservebyProjectForOneBusiness() throws Exception {
		_framework.gotoResourceManagementPage();
		_framework.clickLinkWithText("Reserve by Project");
		_framework.assertTextPresent("Business");
		_framework.selectOption("business", getBusinessNameOne());
		_framework.selectOption("monthsId", "Jan");
		_framework.selectOption("yearsId", "2008");
		_framework.selectOption("project", getProjectNameOne());
		_framework.assertButtonPresent("viewBtn");
		_framework.clickButton("viewBtn");
		// verify the data displayed in report
		// _framework.assertTextPresent("Application Administrator");
		//_framework.assertTextPresent("Reserve By Project");
		_framework.assertTextPresent("Resource");
		_framework.assertTextPresent("Until End Of Project");
		_framework.assertTextPresent("Jan08");
		_framework.assertTextPresent("Feb08");
		_framework.assertTextPresent("Mar08");
		_framework.assertTextPresent("Apr08");
		_framework.assertTextPresent("May08");
		_framework.assertTextPresent("Jun08");
		_framework.assertTextPresent("Jul08");
		_framework.assertTextPresent("Aug08");
		//_framework.assertTextPresent("8");
		//_framework.assertTextPresent("0");
		_framework.assertTextPresent("Application Administrator");
		_framework.assertTextPresent("0.00%");
		_framework.assertTextPresent("Total:");

		// assertTableEquals("", new String[][] {
		// { "Resource", "Until End Of Project", "Mar08", "Apr08", "May08","Jun08", "Jul08", "Aug08", "Sep08", "Oct08" }, 
		// { "", "" ,"8", "8", "0", "0", "0", "0", "", "" },
		// { "Total:", "", "0%", "0%", "0%", "0%", "0%", "0%", "0%", "0%" } });
	}

}
