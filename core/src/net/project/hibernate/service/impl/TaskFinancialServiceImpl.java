package net.project.hibernate.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.model.PnMaterialAssignment;
import net.project.hibernate.model.PnPersonSalary;
import net.project.hibernate.model.PnTask;
import net.project.hibernate.service.IPnAssignmentService;
import net.project.hibernate.service.ITaskFinancialService;
import net.project.hibernate.service.ServiceFactory;
import net.project.material.PnMaterialAssignmentList;
import net.project.util.TimeQuantity;

@Service(value = "taskFinancialService")
public class TaskFinancialServiceImpl implements ITaskFinancialService {
	
	@Autowired
	private IPnAssignmentService assignmentService;

	@Override
	public Float calculateActualCostToDateForTask(String spaceId, String objectId) {
		PnTask task = ServiceFactory.getInstance().getPnTaskService().getTaskById(Integer.valueOf(objectId));
		Float totalMaterialCost = calculateMaterialActualCostToDateForTask(spaceId, task);
		Float totalResourcesCost = calculateResourcesActualCostToDateForTask(spaceId, task);
		return totalMaterialCost + totalResourcesCost;
	}

	@Override
	public Float calculateEstimatedTotalCostForTask(String spaceId, String objectId) {
		PnTask task = ServiceFactory.getInstance().getPnTaskService().getTaskById(Integer.valueOf(objectId));
		Float totalMaterialCost = calculateMaterialEstimatedTotalCostForTask(spaceId, task);
		Float totalResourcesCost = calculateResourceEstimatedTotalCostForTask(spaceId, task);
		return totalMaterialCost + totalResourcesCost;
	}
	
	@Override
	public float calculateMaterialActualCostToDateForTask(String spaceId, String objectId) {
		PnTask task = ServiceFactory.getInstance().getPnTaskService().getTaskById(Integer.valueOf(objectId));		
		return calculateMaterialActualCostToDateForTask(spaceId, task);
	}
	
	@Override
	public float calculateResourcesActualCostToDateForTask(String spaceId, String objectId) {
		PnTask task = ServiceFactory.getInstance().getPnTaskService().getTaskById(Integer.valueOf(objectId));		
		return calculateResourcesActualCostToDateForTask(spaceId, task);
	}
	
	@Override
	public float calculateMaterialEstimatedTotalCostForTask(String spaceId, String objectId) {
		PnTask task = ServiceFactory.getInstance().getPnTaskService().getTaskById(Integer.valueOf(objectId));	
		return calculateMaterialEstimatedTotalCostForTask(spaceId, task);
	}
	
	@Override
	public float calculateResourceEstimatedTotalCostForTask(String spaceId, String objectId) {
		PnTask task = ServiceFactory.getInstance().getPnTaskService().getTaskById(Integer.valueOf(objectId));	
		return calculateResourceEstimatedTotalCostForTask(spaceId, task);
	}	

	private float calculateMaterialActualCostToDateForTask(String spaceId, PnTask task) {
		float totalMaterialCost = 0;

		// Obtain the materials assigned to this task (only the ones from the
		// task's owner project).
		PnMaterialAssignmentList materialAssignments = ServiceFactory.getInstance().getPnMaterialAssignmentService()
				.getMaterialsAssignment(spaceId, String.valueOf(task.getTaskId()));

		// Obtain the materials id's from the project.
		List<Integer> materialsIds = ServiceFactory.getInstance().getPnSpaceHasMaterialService().getMaterialsFromSpace(spaceId);

		// For each material calculate the cost for this task
		for (PnMaterialAssignment materialAssignment : materialAssignments) {

			// Is from the project? (Only the active assignments)
			if (materialAssignment.getRecordStatus().equals("A") && materialsIds.contains(materialAssignment.getComp_id().getMaterialId())) {

				// First obtain all the assignments for the material
				BigDecimal totalMaterialAssignedWork = new BigDecimal("0.0");
				PnMaterialAssignmentList materialAssigmentsInAllTasks = ServiceFactory.getInstance().getPnMaterialAssignmentService()
						.getAssignmentsForMaterial(String.valueOf(materialAssignment.getComp_id().getMaterialId()));

				// Calculate the total hours in ALL task assignments for the
				// material
				for (PnMaterialAssignment assignmentInAnyTask : materialAssigmentsInAllTasks) {
					// Only the active ones
					if (assignmentInAnyTask.getRecordStatus().equals("A")) {
						PnTask anyTask = ServiceFactory.getInstance().getPnTaskService().getTaskById(assignmentInAnyTask.getComp_id().getObjectId());
						totalMaterialAssignedWork = totalMaterialAssignedWork.add(anyTask.getWork());
					}
				}

				// Calculate the percentage of all materials assignments
				// corresponding to this task
				Float currentTaskPercentage = task.getWork().multiply(new BigDecimal("100")).divide(totalMaterialAssignedWork, 2, RoundingMode.HALF_UP).floatValue();

				// Get the material to obtain the cost
				PnMaterial material = ServiceFactory.getInstance().getPnMaterialService().getMaterial(materialAssignment.getComp_id().getMaterialId());

				// Add the part corresponding to this task
				// This represents the cost by the percentage of that cost that
				// goes for the current task,
				// taking in count that this task is only partially completed.
				totalMaterialCost += material.getMaterialCost() * (currentTaskPercentage / 100) * (task.getPercentComplete().floatValue() / 100);
			}
		}
		return totalMaterialCost;
	}
	
	private float calculateMaterialEstimatedTotalCostForTask(String spaceId, PnTask task) {
		float totalMaterialCost = 0;

		// Obtain the materials assigned to this task (only the ones from the
		// task's owner project).
		PnMaterialAssignmentList materialAssignments = ServiceFactory.getInstance().getPnMaterialAssignmentService()
				.getMaterialsAssignment(spaceId, String.valueOf(task.getTaskId()));

		// Obtain the materials id's from the project.
		List<Integer> materialsIds = ServiceFactory.getInstance().getPnSpaceHasMaterialService().getMaterialsFromSpace(spaceId);

		// For each material calculate the cost for this task
		for (PnMaterialAssignment materialAssignment : materialAssignments) {

			// Is from the project?
			if (materialsIds.contains(materialAssignment.getComp_id().getMaterialId())) {

				// Get the material
				PnMaterial material = ServiceFactory.getInstance().getPnMaterialService().getMaterial(materialAssignment.getComp_id().getMaterialId());

				// Only the active ones
				if (materialAssignment.getRecordStatus().equals("A")) {
					// First obtain all the assignments for the material
					BigDecimal totalMaterialAssignedWork = new BigDecimal("0.0");
					PnMaterialAssignmentList materialAssigmentsInAllTasks = ServiceFactory.getInstance().getPnMaterialAssignmentService()
							.getAssignmentsForMaterial(String.valueOf(materialAssignment.getComp_id().getMaterialId()));

					// Calculate the total hours in ALL task assignments for the
					// material
					for (PnMaterialAssignment assignmentInAnyTask : materialAssigmentsInAllTasks) {
						// Only the active ones
						if (assignmentInAnyTask.getRecordStatus().equals("A")) {
							PnTask anyTask = ServiceFactory.getInstance().getPnTaskService().getTaskById(assignmentInAnyTask.getComp_id().getObjectId());
							totalMaterialAssignedWork = totalMaterialAssignedWork.add(anyTask.getWork());
						}
					}

					// Calculate the percentage of all materials assignments
					// corresponding to this task
					Float currentTaskPercentage = task.getWork().multiply(new BigDecimal("100")).divide(totalMaterialAssignedWork, 2, RoundingMode.HALF_UP).floatValue() / 100;

					// Add the part corresponding to this task
					// This represents the cost by the percentage of that cost
					// that goes for the current task.
					totalMaterialCost += material.getMaterialCost() * currentTaskPercentage;
				}
			}
		}
		return totalMaterialCost;
	}
	


	private float calculateResourcesActualCostToDateForTask(String spaceId, PnTask task) {
		float totalResourcesCost = 0;
		try {
			// Get persons assignments for the task
			List<PnAssignment> assignmentsOfTask = assignmentService.getAssigmentsByObjectId(Integer.valueOf(task.getTaskId()));
			for (PnAssignment assignmentOfTask : assignmentsOfTask) {
				// Only the active ones
				if (assignmentOfTask.getRecordStatus().equals("A")) {
					TimeQuantity workQuantity = TimeQuantity.parse(String.valueOf(assignmentOfTask.getWorkComplete()),
							String.valueOf(assignmentOfTask.getWorkCompleteUnits()));
					totalResourcesCost += calculateResourcesCost(workQuantity, task, assignmentOfTask);
				}
			}
			return totalResourcesCost;
		} catch (ParseException e) {
			// TODO handle error.
			return totalResourcesCost;
		}
	}
	




	private float calculateResourceEstimatedTotalCostForTask(String spaceId, PnTask task) {
		float totalResourcesCost = 0;
		try {
			// Total resources cost.
			List<PnAssignment> assignmentsFromTask = ServiceFactory.getInstance().getPnAssignmentService()
					.getAssigmentsByObjectId(Integer.valueOf(task.getTaskId()));
			for (PnAssignment assignmentOfTask : assignmentsFromTask) {
				// Only the active ones
				if (assignmentOfTask.getRecordStatus().equals("A")) {
					TimeQuantity workQuantity = TimeQuantity.parse(String.valueOf(assignmentOfTask.getWork()),
							String.valueOf(assignmentOfTask.getWorkUnits()));
					totalResourcesCost += calculateResourcesCost(workQuantity, task, assignmentOfTask);
				}
			}
			return totalResourcesCost;
		} catch (ParseException e) {
			// TODO handle error.
			return totalResourcesCost;
		}
	}
	
	public float calculateResourcesCost(TimeQuantity workQuantity, PnTask task, PnAssignment assignment){
		BigDecimal workAmount = workQuantity.converToHours();

		Integer id = assignment.getComp_id().getPersonId();
		PnPersonSalary personSalary = new PnPersonSalary();

		// This value can be null in case the work didn't start yet
		if (task.getActualFinish() != null) {
			personSalary = ServiceFactory.getInstance().getPnPersonSalaryService().getPersonSalaryForDate(id, assignment.getActualFinish());
		} else {
			personSalary = ServiceFactory.getInstance().getPnPersonSalaryService().getPersonSalaryForDate(id, assignment.getEndDate());
		}
		
		return workAmount.floatValue() * personSalary.getCostByHour();
	}	

}
