package net.project.hibernate.service.impl;

import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.model.PnTask;
import net.project.hibernate.service.IPnAssignmentService;
import net.project.hibernate.service.IPnMaterialAssignmentService;
import net.project.hibernate.service.IPnMaterialService;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.IPnSpaceHasMaterialService;
import net.project.hibernate.service.IProjectFinancialService;
import net.project.hibernate.service.ITaskFinancialService;
import net.project.hibernate.service.ServiceFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "projectFinancialService")
public class ProjectFinancialServiceImpl implements IProjectFinancialService {

	@Autowired
	private IPnMaterialService materialService;

	@Autowired
	private IPnSpaceHasMaterialService spaceHasMaterialService;

	@Autowired
	private IPnMaterialAssignmentService materialAssignmentService;

	@Autowired
	private IPnAssignmentService assignmentService;

	@Autowired
	private IPnProjectSpaceService projectSpaceService;

	@Autowired
	private ITaskFinancialService taskFinancialService;

	public IPnMaterialService getMaterialService() {
		return materialService;
	}

	public void setMaterialService(IPnMaterialService materialService) {
		this.materialService = materialService;
	}

	public IPnSpaceHasMaterialService getSpaceHasMaterialService() {
		return spaceHasMaterialService;
	}

	public void setSpaceHasMaterialService(IPnSpaceHasMaterialService spaceHasMaterialService) {
		this.spaceHasMaterialService = spaceHasMaterialService;
	}

	@Override
	public Float calculateActualCostToDate(String spaceID) {
		Float totalActualCost = new Float(0.00);

		List<PnTask> tasks = ServiceFactory.getInstance().getPnTaskService().getCompletedTasksByProjectId(Integer.valueOf(spaceID));

		for (PnTask task : tasks) {

			totalActualCost += taskFinancialService.calculateActualCostToDateForTask(spaceID, String.valueOf(task.getTaskId()));
		}
		return totalActualCost;
	}
	
	@Override
	public Float calculateMaterialActualTotalCostToDate(String spaceID) {
		Float materialTotalActualCost = new Float(0.00);

		List<PnTask> tasks = ServiceFactory.getInstance().getPnTaskService().getCompletedTasksByProjectId(Integer.valueOf(spaceID));

		for (PnTask task : tasks) {

			materialTotalActualCost += taskFinancialService.calculateMaterialActualCostToDateForTask(spaceID, String.valueOf(task.getTaskId()));
		}
		return materialTotalActualCost;
	}

	@Override
	public Float calculateResourcesActualTotalCostToDate(String spaceID) {
		Float resourcesTotalActualCost = new Float(0.00);

		List<PnTask> tasks = ServiceFactory.getInstance().getPnTaskService().getCompletedTasksByProjectId(Integer.valueOf(spaceID));

		for (PnTask task : tasks) {

			resourcesTotalActualCost += taskFinancialService.calculateResourcesActualCostToDateForTask(spaceID, String.valueOf(task.getTaskId()));
		}
		return resourcesTotalActualCost;
	}	

	@Override
	public Float calculateEstimatedTotalCost(String spaceID) {
		Float totalEstimatedCost = new Float(0.00);

		// Obtain the tasks for the space.
		List<PnTask> tasks = ServiceFactory.getInstance().getPnTaskService().getTasksByProjectId(Integer.valueOf(spaceID));

		// Calculate the cost for each task.
		for (PnTask task : tasks) {
			totalEstimatedCost += taskFinancialService.calculateEstimatedTotalCostForTask(spaceID, String.valueOf(task.getTaskId()));
		}

		// If it's active, also add the cost of the materials not assigned
		if (PropertyProvider.getBoolean("prm.global.addmaterialsnotassignedontasks.isenabled")) {
			List<PnMaterial> materialsNotInAssignments = ServiceFactory.getInstance().getMaterialService().getMaterialsFromProjectWithoutAssignments(spaceID);

			for (PnMaterial materialNotAssigned : materialsNotInAssignments) {
				totalEstimatedCost += materialNotAssigned.getMaterialCost();
			}
		}
		return totalEstimatedCost;
	}
	
	@Override
	public Float calculateMaterialCurrentEstimatedTotalCost(String spaceID) {
		Float totalEstimatedCost = new Float(0.00);

		// Obtain the tasks for the space.
		List<PnTask> tasks = ServiceFactory.getInstance().getPnTaskService().getTasksByProjectId(Integer.valueOf(spaceID));

		// Calculate the cost for each task.
		for (PnTask task : tasks) {
			totalEstimatedCost += taskFinancialService.calculateMaterialEstimatedTotalCostForTask(spaceID, String.valueOf(task.getTaskId()));
		}

		// If it's active, also add the cost of the materials not assigned
		if (PropertyProvider.getBoolean("prm.global.addmaterialsnotassignedontasks.isenabled")) {
			List<PnMaterial> materialsNotInAssignments = ServiceFactory.getInstance().getMaterialService().getMaterialsFromProjectWithoutAssignments(spaceID);

			for (PnMaterial materialNotAssigned : materialsNotInAssignments) {
				totalEstimatedCost += materialNotAssigned.getMaterialCost();
			}
		}
		return totalEstimatedCost;
	}

	@Override
	public Float calculatResourcesCurrentEstimatedTotalCost(String spaceID) {
		Float totalEstimatedCost = new Float(0.00);

		// Obtain the tasks for the space.
		List<PnTask> tasks = ServiceFactory.getInstance().getPnTaskService().getTasksByProjectId(Integer.valueOf(spaceID));

		// Calculate the cost for each task.
		for (PnTask task : tasks) {
			totalEstimatedCost += taskFinancialService.calculateResourceEstimatedTotalCostForTask(spaceID, String.valueOf(task.getTaskId()));
		}

		return totalEstimatedCost;
	}

	@Override
	public Float getBudgetedCost(String spaceID) {
		return projectSpaceService.getBudgetedTotalCost(spaceID);
	}

	@Override
	public Float getDiscretionalCost(String spaceID) {
		return projectSpaceService.getDiscretionalCost(spaceID);
	}

}
