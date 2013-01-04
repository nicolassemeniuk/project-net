package net.project.test.acceptance.project.dashboard;

public class TeammatesTest extends ProjectDashboardBase {
	
	public void testTeammates() {
		this.goToProject();
		
		_framework.assertTextPresent("Teammates");
		_framework.assertTextPresent("A. Administrator");
	}

}
