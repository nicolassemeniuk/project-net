package net.project.test.acceptance.project.dashboard;

public class GeneralDataTest extends ProjectDashboardBase {
	
	public void testData() throws Exception {
		_framework.assertTextPresent("Project:");
		_framework.assertTextPresent("Description:");
		_framework.assertTextPresent("Manager:");
		_framework.assertTextPresent("Status:");
		_framework.assertTextPresent("Completion:");
	}

}
