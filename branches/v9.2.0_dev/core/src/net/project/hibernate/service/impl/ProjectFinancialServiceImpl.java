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
import net.project.hibernate.service.IPnProjectSpaceService;
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
	
	@Autowired
	private IPnProjectSpaceService projectSpaceService;

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

		try {
			
			// Obtain the completed tasks for the space.
			List<PnTask> tasks = ServiceFactory.getInstance().getPnTaskService().getCompletedTasksByProjectId(Integer.valueOf(spaceID));
			
			for (PnTask task : tasks) {

				// Get persons assignments for the task
				List<PnAssignment> assignmentsOfTask = assignmentService.getAssigmentsByObjectId(task.getTaskId());
				for (PnAssignment assignmentOfTask : assignmentsOfTask) {
					TimeQuantity workQuantity = TimeQuantity.parse(String.valueOf(assignmentOfTask.getWork()), String.valueOf(assignmentOfTask.getWorkUnits()));
					BigDecimal workAmount = workQuantity.converToHours();

					Integer id = assignmentOfTask.getComp_id().getPersonId();
					PnPersonSalary personSalary = ServiceFactory.getInstance().getPnPersonSalaryService().getPersonSalaryForDate(id, task.getActualFinish());
					totalResourcesCost += workAmount.floatValue() * personSalary.getCostByHour();
				}
			}
			
			//Obtain the materials assigned to completed tasks on this project.			
			PnMaterialList materials = ServiceFactory.getInstance().getMaterialService().getMaterialsFromCompletedTasksOfSpace(spaceID);
			
			for(PnMaterial material : materials){
				totalMaterialCost += material.getMaterialCost();
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
				PnPersonSalary personSalary = ServiceFactory.getInstance().getPnPersonSalaryService().getPersonSalaryForDate(id, assignmentFromProject.getEndDate());
				totalResourcesCost += workAmount.floatValue() * personSalary.getCostByHour();
			}

			return totalMaterialCost + totalResourcesCost;
		} catch (ParseException e) {
			// TODO handle error.
		}
		return null;
	}

	@Override
	public Float getBudgetedCost(String spaceID) {
		return projectSpaceService.getBudgetedTotalCost(spaceID);
	}

	@Override
	public Float calculateActualCostToDateForTask(String spaceId, String objectId) {
		try {
			Float totalMaterialCost = new Float(0.00);
			Float totalResourcesCost = new Float(0.00);
			PnTask task = ServiceFactory.getInstance().getPnTaskService().getTaskById(Integer.valueOf(objectId));
			
			//**********************************************
			//Task resources costs
			
			// Get persons assignments for the task
			List<PnAssignment> assignmentsOfTask = assignmentService.getAssigmentsByObjectId(Integer.valueOf(objectId));
			for (PnAssignment assignmentOfTask : assignmentsOfTask) {
				TimeQuantity workQuantity = TimeQuantity.parse(String.valueOf(assignmentOfTask.getWork()), String.valueOf(assignmentOfTask.getWorkUnits()));
				BigDecimal workAmount = workQuantity.converToHours();
	
				Integer id = assignmentOfTask.getComp_id().getPersonId();
				PnPersonSalary personSalary = ServiceFactory.getInstance().getPnPersonSalaryService().getPersonSalaryForDate(id, task.getActualFinish());
				totalResourcesCost += workAmount.floatValue() * personSalary.getCostByHour();
			}
			
			//**********************************************
			//Task material costs
			
			//Obtain the materials assigned to this task (only the ones from the task's owner project).			
			PnMaterialAssignmentList materialAssignments = ServiceFactory.getInstance().getPnMaterialAssignmentService().getMaterialsAssignment(spaceId, objectId);
			
			//For each material calculate the cost for this task
			for(PnMaterialAssignment materialAssignment : materialAssignments){
				//Only the active ones
				if(materialAssignment.getRecordStatus().equals("A")){
					//First obtain all the assignments for the material
					BigDecimal totalMaterialAssignedWork = new BigDecimal("0.0");
					PnMaterialAssignmentList materialAssigmentsInAllTasks = ServiceFactory.getInstance().getPnMaterialAssignmentService().getAssignmentsForMaterial(String.valueOf(materialAssignment.getComp_id().getMaterialId()));
					
					//Calculate the total hours in ALL task assignments for the material
					for(PnMaterialAssignment assignmentInAnyTask : materialAssigmentsInAllTasks){
						//Only the active ones
						if(assignmentInAnyTask.getRecordStatus().equals("A")){
							PnTask anyTask = ServiceFactory.getInstance().getPnTaskService().getTaskById(assignmentInAnyTask.getComp_id().getObjectId());
							totalMaterialAssignedWork = totalMaterialAssignedWork.add(anyTask.getWork());
						}
					}
					
					//Calculate the percentage of all materials assignments corresponding to this task
					Float currentTaskPercentage = task.getWork().multiply(new BigDecimal("100")).divide(totalMaterialAssignedWork).floatValue();
					
					//Get the material to obtain the cost
					PnMaterial material = ServiceFactory.getInstance().getPnMaterialService().getMaterial(materialAssignment.getComp_id().getMaterialId());
					
					//Add the part corresponding to this task
					//This represents the cost by the percentage of that cost that goes for the current task,
					//taking in count that this task is only partially completed.
					totalMaterialCost += material.getMaterialCost() * currentTaskPercentage * task.getPercentComplete().floatValue()/100;
				}				
			}
	
			return totalMaterialCost + totalResourcesCost;		

		} catch (ParseException e) {
			// TODO handle error.
		}	
		return new Float(0.00);
	}

	@Override
	public Float calculateEstimatedTotalCostForTask(String spaceId, String objectId) {
		try {
			Float totalMaterialCost = new Float(0.00);
			Float totalResourcesCost = new Float(0.00);
			PnTask task = ServiceFactory.getInstance().getPnTaskService().getTaskById(Integer.valueOf(objectId));
			
			//**********************************************
			//Task resources costs
			
			// Total resources cost.
			List<PnAssignment> assignmentsFromTask = ServiceFactory.getInstance().getPnAssignmentService().getAssigmentsByObjectId(Integer.valueOf(objectId));

			for (PnAssignment assignmentFromTask : assignmentsFromTask) {
				TimeQuantity workQuantity = TimeQuantity.parse(String.valueOf(assignmentFromTask.getWork()),
						String.valueOf(assignmentFromTask.getWorkUnits()));
				BigDecimal workAmount = workQuantity.converToHours();

				Integer id = assignmentFromTask.getComp_id().getPersonId();
				PnPersonSalary personSalary = ServiceFactory.getInstance().getPnPersonSalaryService().getPersonSalaryForDate(id, assignmentFromTask.getEndDate());
				totalResourcesCost += workAmount.floatValue() * personSalary.getCostByHour();
			}
			
			//**********************************************
			//Task material costs
			
			//Obtain the materials assigned to this task (only the ones from the task's owner project).			
			PnMaterialAssignmentList materialAssignments = ServiceFactory.getInstance().getPnMaterialAssignmentService().getMaterialsAssignment(spaceId, objectId);
			
			//For each material calculate the cost for this task
			for(PnMaterialAssignment materialAssignment : materialAssignments){
				//Only the active ones
				if(materialAssignment.getRecordStatus().equals("A")){
					//First obtain all the assignments for the material
					BigDecimal totalMaterialAssignedWork = new BigDecimal("0.0");
					PnMaterialAssignmentList materialAssigmentsInAllTasks = ServiceFactory.getInstance().getPnMaterialAssignmentService().getAssignmentsForMaterial(String.valueOf(materialAssignment.getComp_id().getMaterialId()));
					
					//Calculate the total hours in ALL task assignments for the material
					for(PnMaterialAssignment assignmentInAnyTask : materialAssigmentsInAllTasks){
						//Only the active ones
						if(assignmentInAnyTask.getRecordStatus().equals("A")){
							PnTask anyTask = ServiceFactory.getInstance().getPnTaskService().getTaskById(assignmentInAnyTask.getComp_id().getObjectId());
							totalMaterialAssignedWork = totalMaterialAssignedWork.add(anyTask.getWork());
						}
					}
					
					//Calculate the percentage of all materials assignments corresponding to this task
					Float currentTaskPercentage = task.getWork().multiply(new BigDecimal("100")).divide(totalMaterialAssignedWork).floatValue()/100;
					
					//Get the material to obtain the cost
					PnMaterial material = ServiceFactory.getInstance().getPnMaterialService().getMaterial(materialAssignment.getComp_id().getMaterialId());
					
					//Add the part corresponding to this task
					//This represents the cost by the percentage of that cost that goes for the current task.
					totalMaterialCost += material.getMaterialCost() * currentTaskPercentage;	
				}
			}

			return totalMaterialCost + totalResourcesCost;
		} catch (ParseException e) {
			// TODO handle error.
		}
		return new Float(0.00);
	}

}
