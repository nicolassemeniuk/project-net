package net.project.hibernate.service.impl;

import java.util.Date;

import net.project.hibernate.dao.IPnAssignmentMaterialDAO;
import net.project.hibernate.model.PnAssignmentMaterial;
import net.project.hibernate.service.IPnAssignmentMaterialService;
import net.project.material.MaterialAssignment;
import net.project.material.PnAssignmentMaterialList;
import net.project.util.time.TimeRangeAggregator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="pnAssignmentMaterialService")
public class PnAssignmentMaterialServiceImpl implements IPnAssignmentMaterialService {
	
	@Autowired
	private IPnAssignmentMaterialDAO pnMaterialDAO;

	@Override
	public PnAssignmentMaterial getAssignmentMaterial(String spaceId, String materialId, String objectId) {
		return pnMaterialDAO.getPnAssignmentMaterial(Integer.valueOf(spaceId), Integer.valueOf(materialId), Integer.valueOf(objectId));
	}

	@Override
	public PnAssignmentMaterialList getAssignmentMaterials(String spaceId, String objectId) {
		return pnMaterialDAO.getAssignments(Integer.valueOf(spaceId), Integer.valueOf(objectId));
	}

	@Override
	public PnAssignmentMaterialList getAssignmentsForMaterial(String spaceId, String materialId) {
		return pnMaterialDAO.getAssignmentsForMaterial(Integer.valueOf(spaceId), Integer.valueOf(materialId));
	}

	@Override
	public boolean isOverassigned(Date startDate, Date endDate, String spaceId, String materialId, String objectId) {
		PnAssignmentMaterialList assignments = pnMaterialDAO.getAssignmentsForMaterial(Integer.valueOf(spaceId), Integer.valueOf(materialId), Integer.valueOf(objectId));
		TimeRangeAggregator aggregator = new TimeRangeAggregator();
		
		//we add all assignments to the TimeRangeAggregator.
		for(PnAssignmentMaterial assignment : assignments){
			MaterialAssignment materialAssignment = new MaterialAssignment(assignment);
			aggregator.insert(materialAssignment);
		}
		
		return aggregator.existConcurrent(startDate, endDate);
	}

	@Override
	public boolean isOverassigned(Date startDate, Date endDate, String spaceId, String materialId) {
		PnAssignmentMaterialList assignments = pnMaterialDAO.getAssignmentsForMaterial(Integer.valueOf(spaceId), Integer.valueOf(materialId));
		TimeRangeAggregator aggregator = new TimeRangeAggregator();
		
		//we add all assignments to the TimeRangeAggregator.
		for(PnAssignmentMaterial assignment : assignments){
			MaterialAssignment materialAssignment = new MaterialAssignment(assignment);
			aggregator.insert(materialAssignment);
		}
		
		return aggregator.existConcurrent(startDate, endDate);
	}
	
	
	
	

}
