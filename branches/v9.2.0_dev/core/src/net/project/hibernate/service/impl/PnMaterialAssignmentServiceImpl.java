package net.project.hibernate.service.impl;

import java.util.Date;

import net.project.hibernate.dao.IPnAssignmentMaterialDAO;
import net.project.hibernate.model.PnMaterialAssignment;
import net.project.hibernate.model.PnMaterialAssignmentPK;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.service.IPnMaterialAssignmentService;
import net.project.hibernate.service.ServiceFactory;
import net.project.material.MaterialAssignment;
import net.project.material.MaterialAssignmentList;
import net.project.material.PnMaterialAssignmentList;
import net.project.util.time.TimeRangeAggregator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="pnMaterialAssignmentService")
public class PnMaterialAssignmentServiceImpl implements IPnMaterialAssignmentService {
	
	@Autowired
	private IPnAssignmentMaterialDAO pnMaterialAssignmentDAO;

	@Override
	public PnMaterialAssignment getMaterialAssignment(String spaceId, String materialId, String objectId) {
		return pnMaterialAssignmentDAO.getPnAssignmentMaterial(Integer.valueOf(spaceId), Integer.valueOf(materialId), Integer.valueOf(objectId));
	}

	@Override
	public PnMaterialAssignmentList getMaterialsAssignment(String spaceId, String objectId) {
		return pnMaterialAssignmentDAO.getAssignments(Integer.valueOf(spaceId), Integer.valueOf(objectId));
	}

	@Override
	public PnMaterialAssignmentList getAssignmentsForMaterial(String spaceId, String materialId) {
		return pnMaterialAssignmentDAO.getAssignmentsForMaterial(Integer.valueOf(spaceId), Integer.valueOf(materialId));
	}

	@Override
	public boolean isOverassigned(Date startDate, Date endDate, String spaceId, String materialId, String objectId) {
		PnMaterialAssignmentList assignments = pnMaterialAssignmentDAO.getAssignmentsForMaterial(Integer.valueOf(spaceId), Integer.valueOf(materialId), Integer.valueOf(objectId));
		TimeRangeAggregator aggregator = new TimeRangeAggregator();
		
		//we add all assignments to the TimeRangeAggregator.
		for(PnMaterialAssignment assignment : assignments){
			if(assignment.getRecordStatus().equals("A")){
				MaterialAssignment newMaterialAssignment = new MaterialAssignment();
				newMaterialAssignment.setEndDate(assignment.getEndDate());
				newMaterialAssignment.setStartDate(assignment.getStartDate());
				aggregator.insert(newMaterialAssignment);
			}
		}
		
		return aggregator.existConcurrent(startDate, endDate);
	}

	@Override
	public boolean isOverassigned(Date startDate, Date endDate, String spaceId, String materialId) {
		PnMaterialAssignmentList assignments = pnMaterialAssignmentDAO.getAssignmentsForMaterial(Integer.valueOf(spaceId), Integer.valueOf(materialId));
		TimeRangeAggregator aggregator = new TimeRangeAggregator();
		
		//we add all assignments to the TimeRangeAggregator.
		for(PnMaterialAssignment assignment : assignments){
			MaterialAssignment materialAssignment = new MaterialAssignment(assignment);
			if(materialAssignment.getRecordStatus().equals("A"))
				aggregator.insert(materialAssignment);
		}
		
		return aggregator.existConcurrent(startDate, endDate);
	}

	@Override
	public void saveMaterialAssignments(MaterialAssignmentList materialAssignments)
	{
		for(MaterialAssignment assignment : materialAssignments){
			PnMaterialAssignment newAssignment = new PnMaterialAssignment();
			PnPerson assignor = ServiceFactory.getInstance().getPnPersonService().getPerson(Integer.valueOf(assignment.getAssignorId()));
			PnMaterialAssignmentPK newAssignmentPK = new PnMaterialAssignmentPK();
			newAssignmentPK.setSpaceId(Integer.valueOf(assignment.getSpaceId()));
			newAssignmentPK.setMaterialId(Integer.valueOf(assignment.getMaterialId()));
			newAssignmentPK.setObjectId(Integer.valueOf(assignment.getObjectId()));
			
			newAssignment.setComp_id(newAssignmentPK);
			newAssignment.setPnAssignor(assignor);
			newAssignment.setRecordStatus(assignment.getRecordStatus());
			newAssignment.setDateCreated(assignment.getDateCreated());
			newAssignment.setStartDate(assignment.getStartDate());
			newAssignment.setEndDate(assignment.getEndDate());
			newAssignment.setModifiedBy(Integer.valueOf(assignment.getModifiedBy()));
			newAssignment.setModifiedDate(assignment.getModifiedDate());
			newAssignment.setPercentAllocated(assignment.getPercentAssigned());
			pnMaterialAssignmentDAO.createOrUpdate(newAssignment);
		}
	

	}
}
