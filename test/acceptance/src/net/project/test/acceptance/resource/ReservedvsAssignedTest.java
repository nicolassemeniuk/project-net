package net.project.test.acceptance.resource;

public class ReservedvsAssignedTest extends ResourceManagementBase {
	
	public void testReservedvsAssignedForAllBusiness() throws Exception {
		_framework.gotoResourceManagementPage();
		_framework.assertTextPresent("Reserved vs. Assigned");
		_framework.clickLinkWithText("Reserved vs. Assigned");
		_framework.assertTextPresent("Business");
		_framework.selectOption("business", "All");
		_framework.assertButtonPresent("viewBtn");
		_framework.clickButton("viewBtn");
		// verify the data displayed in report
		
		// _framework.assertTextPresent("Application Administrator");
		_framework.assertTextPresent("Reserved vs. Assigned");
		_framework.assertTextPresent("Project");
		_framework.assertTextPresent("May08");
		_framework.assertTextPresent("Jun08");
		_framework.assertTextPresent("Jul08");
		_framework.assertTextPresent("Aug08");
		_framework.assertTextPresent("Sep08");
		_framework.assertTextPresent("Oct08");
		_framework.assertTextPresent("Nov08");
		_framework.assertTextPresent("Dec08");
		_framework.assertTextPresent("Jan09");
		//_framework.assertTextPresent("Feb09");
		//_framework.assertTextPresent(getProjectNameOne()); 
		_framework.assertTextPresent("--");
		//_framework.assertTextPresent("0%");
		
		// assertTableEquals("", new String[][] {
		// { "Project", "Mar08", "Apr08", "May08","Jun08", "Jul08", "Aug08", "Sep08","Oct08","Nov08","Dec08","Jan09","Feb09" }, 
		// { projectNameOne, "--", "--", "--","--", "--", "--", "--", "--","--","--","--","-" },
		// { "", "0%", "0%", "0%","0%", "0%", "0%", "0%", "0%","0%","0%","0%","0%" } });
	}

	public void testReservedvsAssignedForOneBusiness() throws Exception {
		_framework.gotoResourceManagementPage();
		_framework.assertTextPresent("Reserved vs. Assigned");
		_framework.clickLinkWithText("Reserved vs. Assigned");
		_framework.assertTextPresent("Business");
		_framework.selectOption("business", getBusinessNameOne());
		_framework.assertButtonPresent("viewBtn");
		_framework.clickButton("viewBtn");
		// verify the data displayed in report
		// _framework.assertTextPresent("Application Administrator");
		_framework.assertTextPresent("Reserved vs. Assigned");
		_framework.assertTextPresent("Project");
		_framework.assertTextPresent("May08");
		_framework.assertTextPresent("Jun08");
		_framework.assertTextPresent("Jul08");
		_framework.assertTextPresent("Aug08");
		_framework.assertTextPresent("Sep08");
		_framework.assertTextPresent("Oct08");
		_framework.assertTextPresent("Nov08");
		_framework.assertTextPresent("Dec08");
		_framework.assertTextPresent("Jan09");
		//_framework.assertTextPresent("Feb09");
		//_framework.assertTextPresent(getProjectNameOne()); 
		_framework.assertTextPresent("--");
		//_framework.assertTextPresent("0%");
		
		// assertTableEquals("", new String[][] {
		// { "Project", "Mar08", "Apr08", "May08","Jun08", "Jul08", "Aug08", "Sep08","Oct08","Nov08","Dec08","Jan09","Feb09" }, 
		// { projectNameOne, "--", "--", "--","--", "--", "--", "--", "--","--","--","--","-" },
		// { "", "0%", "0%", "0%","0%", "0%", "0%", "0%", "0%","0%","0%","0%","0%" } });
	}

}
