package net.project.hibernate.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.model.PnMaterialAssignment;
import net.project.hibernate.model.PnPersonSalary;
import net.project.hibernate.model.PnTask;
import net.project.hibernate.service.IPnAssignmentService;
import net.project.hibernate.service.IPnMaterialAssignmentService;
import net.project.hibernate.service.IPnMaterialService;
import net.project.hibernate.service.IPnSpaceHasMaterialService;
import net.project.hibernate.service.IProjectFinancialService;
import net.project.hibernate.service.ServiceFactory;
import net.project.material.PnMaterialAssignmentList;
import net.project.material.PnMaterialList;
import net.project.util.TimeQuantity;

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
		Float totalMaterialCost = new Float(0.00);
		Float totalResourcesCost = new Float(0.00);

		// Obtain the completed tasks for the space.
		List<PnTask> tasks = ServiceFactory.getInstance().getPnTaskService().getCompletedTasksByProjectId(Integer.valueOf(spaceID));

		try {
			for (PnTask task : tasks) {

				// Get persons assignments for the task
				List<PnAssignment> assignmentsOfTask = assignmentService.getAssigmentsByObjectId(task.getTaskId());
				for (PnAssignment assignmentOfTask : assignmentsOfTask) {
					TimeQuantity workQuantity = TimeQuantity.parse(String.valueOf(assignmentOfTask.getWork()), String.valueOf(assignmentOfTask.getWorkUnits()));
					BigDecimal workAmount = workQuantity.converToHours();

					Integer id = assignmentOfTask.getComp_id().getPersonId();
					PnPersonSalary personSalary = ServiceFactory.getInstance().getPnPersonSalaryService().getPersonSalaryByPersonId(id);
					totalResourcesCost += workAmount.floatValue() * personSalary.getCostByHour();
				}

				// Obtain the materials from the task.
				PnMaterialAssignmentList materialAssignmentsOfTask = materialAssignmentService
						.getMaterialsAssignment(spaceID, String.valueOf(task.getTaskId()));
				for (PnMaterialAssignment materialAssignmentOfTask : materialAssignmentsOfTask) {
					PnMaterial materialFromTask = materialService.getMaterial(materialAssignmentOfTask.getComp_id().getMaterialId());
					totalMaterialCost += materialFromTask.getMaterialCost();
				}

			}

			return totalMaterialCost + totalResourcesCost;
		} catch (ParseException e) {
			// TODO handle error.
		}
		return null;
	}

	@Override
	public Float calculateEstimatedTotalCost(String spaceID) {
		try {
			// Total material cost
			PnMaterialList materialsFromSpace = materialService.getMaterials(this.spaceHasMaterialService.getMaterialsFromSpace(spaceID));
			Float totalMaterialCost = new Float(0.00);

			for (PnMaterial materialFromSpace : materialsFromSpace) {
				totalMaterialCost += materialFromSpace.getMaterialCost();
			}

			// Total resources cost.
			List<PnAssignment> assignmentsFromProject = assignmentService.getAssigmentsListForProject(Integer.valueOf(spaceID));
			Float totalResourcesCost = new Float(0.00);

			for (PnAssignment assignmentFromProject : assignmentsFromProject) {
				TimeQuantity workQuantity = TimeQuantity.parse(String.valueOf(assignmentFromProject.getWork()),
						String.valueOf(assignmentFromProject.getWorkUnits()));
				BigDecimal workAmount = workQuantity.converToHours();

				Integer id = assignmentFromProject.getComp_id().getPersonId();
				PnPersonSalary personSalary = ServiceFactory.getInstance().getPnPersonSalaryService().getPersonSalaryByPersonId(id);
				totalResourcesCost += workAmount.floatValue() * personSalary.getCostByHour();
			}

			return totalMaterialCost + totalResourcesCost;
		} catch (ParseException e) {
			// TODO handle error.
		}
		return null;
	}

}
