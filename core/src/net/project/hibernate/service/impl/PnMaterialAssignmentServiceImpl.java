package net.project.hibernate.service.impl;

import java.util.Date;

import net.project.hibernate.dao.IPnAssignmentMaterialDAO;
import net.project.hibernate.model.PnMaterialAssignment;
import net.project.hibernate.service.IPnMaterialAssignmentService;
import net.project.material.MaterialAssignment;
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
			MaterialAssignment materialAssignment = new MaterialAssignment(assignment);
			aggregator.insert(materialAssignment);
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
			aggregator.insert(materialAssignment);
		}
		
		return aggregator.existConcurrent(startDate, endDate);
	}

	@Override
	public void saveMaterialAssignments(PnMaterialAssignmentList materialAssignments)
	{
		for(PnMaterialAssignment assignment : materialAssignments)		
			pnMaterialAssignmentDAO.createOrUpdate(assignment);
	}
}
