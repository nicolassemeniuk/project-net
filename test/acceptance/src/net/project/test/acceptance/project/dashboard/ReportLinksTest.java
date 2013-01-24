package net.project.test.acceptance.project.dashboard;

import net.project.hibernate.model.project_space.ProjectSchedule;
import net.project.hibernate.service.ServiceFactory;

public class ReportLinksTest extends ProjectDashboardBase {

	public void testReportLinks() {
		ProjectSchedule projectSchedule = ServiceFactory.getInstance()
				.getPnProjectSpaceService().getProjectSchedule(
						this.getProjectSpace().getProjectId());

		int numberComingDueTasks = projectSchedule.getNumberOfTaskComingDue();
		int numberCompletedTasks = projectSchedule.getNumberOfCompletedTasks();
		int numberLateTasks = projectSchedule.getNumberOfLateTasks();
		int numberUnassignedTasks = projectSchedule
				.getNumberOfUnassignedTasks();

		this.goToProject();

		_framework.assertTextPresent(numberLateTasks + " late tasks");
		_framework.assertTextPresent(numberComingDueTasks + " tasks coming due this week");
		_framework.assertTextPresent(numberUnassignedTasks + " unassigned tasks");
		_framework.assertTextPresent(numberCompletedTasks + " tasks completed");
	}

}
