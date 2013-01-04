package net.project.test.acceptance.project.dashboard;

import java.util.List;

import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.ServiceFactory;
import net.project.test.acceptance.engine.PnetTestEngine;

public class ProjectDashboardBase extends PnetTestEngine {

	private String businessName;
	private String projectName;
	private PnProjectSpace projectSpace;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		// Create the project
		businessName = _framework.createNewBusiness("Dashboard");
		projectName = _framework.createNewProject("Dashboard", businessName);

		IPnProjectSpaceService service = ServiceFactory.getInstance()
				.getPnProjectSpaceService();
		List<PnProjectSpace> projects = service.getAllProjects();
		for (PnProjectSpace ps : projects) {
			if (ps.getProjectName().equals(projectName)) {
				projectSpace = ps;
			}
		}

		this.goToProject();
	}

	@Override
	public void tearDown() throws Exception {

		// Delete de project
		_framework.deleteProject(projectName, businessName);
		_framework.deleteBusiness(businessName);
		super.tearDown();
	}

	public void goToProject() {
		_framework.goToPersonal();
		_framework.clickLinkWithExactText(projectName);
		_framework.clickLinkWithExactText("Dashboard (Beta)");
		_framework.assertTextPresent(projectName);
	}

	public String getProjectName() {
		return this.projectName;
	}

	public PnProjectSpace getProjectSpace() {
		return this.projectSpace;
	}

}
