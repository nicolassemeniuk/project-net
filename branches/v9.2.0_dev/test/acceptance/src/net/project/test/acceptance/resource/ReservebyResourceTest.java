package net.project.test.acceptance.resource;

public class ReservebyResourceTest extends ResourceManagementBase {

	public void testReservebyResourceForAllBusiness() throws Exception {
		
		_framework.gotoResourceManagementPage();
		_framework.clickLinkWithText("Reserve by Resource");
		_framework.assertTextPresent("Business");
		_framework.selectOption("business", "All");
		_framework.selectOption("monthsId", "Jan");
		_framework.selectOption("yearsId", "2008");
		_framework.selectOption("person", "Application Administrator");
		_framework.assertButtonPresent("viewBtn");
		_framework.clickButton("viewBtn");
		// verify the data displayed in report
		_framework.assertTextPresent("Reserve By Resource");
		_framework.assertTextPresent("Project");
		_framework.assertTextPresent("Until End Of Project");
		_framework.assertTextPresent("Jan08");
		_framework.assertTextPresent("Feb08");
		_framework.assertTextPresent("Mar08");
		_framework.assertTextPresent("Apr08");
		_framework.assertTextPresent("May08");
		_framework.assertTextPresent("Jun08");
		_framework.assertTextPresent("Jul08");
		_framework.assertTextPresent("Aug08");
		_framework.assertTextPresent(getProjectNameOne());
		//_framework.assertTextPresent("8");
		//_framework.assertTextPresent("0");
		_framework.assertTextPresent("0.00%");
		
		//assertTableEquals("", new String[][] {
		// { "Project", "Until End Of Project", "Mar08", "Apr08", "May08", "Jun08", "Jul08", "Aug08", "Sep08", "Oct08" }, 
		// { projectNameOne, "", "8", "8", "0", "0", "0", "0", "", "" },
		// { "", "", "0%", "0%", "0%", "0%", "0%", "0%", "0%", "0%" } });
	}

	public void testReservebyResourceForOneBusiness() throws Exception {
		_framework.gotoResourceManagementPage();
		_framework.clickLinkWithText("Reserve by Resource");
		_framework.assertTextPresent("Business");
		_framework.selectOption("business", getBusinessNameOne());
		_framework.selectOption("monthsId", "Jan");
		_framework.selectOption("yearsId", "2008");
		_framework.selectOption("person", "Application Administrator");
		_framework.assertButtonPresent("viewBtn");
		_framework.clickButton("viewBtn");
		// verify the data displayed in report
		_framework.assertTextPresent("Reserve By Resource");
		_framework.assertTextPresent("Project");
		_framework.assertTextPresent("Until End Of Project");
		_framework.assertTextPresent("Jan08");
		_framework.assertTextPresent("Feb08");
		_framework.assertTextPresent("Mar08");
		_framework.assertTextPresent("Apr08");
		_framework.assertTextPresent("May08");
		_framework.assertTextPresent("Jun08");
		_framework.assertTextPresent("Jul08");
		_framework.assertTextPresent("Aug08");
		_framework.assertTextPresent(getProjectNameOne());
		//_framework.assertTextPresent("8");
		//_framework.assertTextPresent("0");
		_framework.assertTextPresent("0.00%");

		//assertTableEquals("", new String[][] {
		// { "Project", "Until End Of Project", "Mar08", "Apr08", "May08", "Jun08", "Jul08", "Aug08", "Sep08", "Oct08" }, 
		// { projectNameOne, "", "8", "8", "0", "0", "0", "0", "", "" },
		// { "", "", "0%", "0%", "0%", "0%", "0%", "0%", "0%", "0%" } });
	}
}
